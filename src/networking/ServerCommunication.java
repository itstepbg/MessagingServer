package networking;

import java.io.File;
import java.net.Socket;

import java.util.List;

import java.util.Base64;


import library.models.data.Directory;
import library.models.data.User;
import library.models.network.MessageType;
import library.models.network.NetworkMessage;
import library.networking.Communication;
import library.util.Crypto;
import library.util.FileUtils;
import library.util.ConstantsFTP;
import managers.MessagingManager;
import managers.UserManager;
import storage.ORM;

public class ServerCommunication extends Communication {

	// TODO This should be moved to the CommonCommunication class.
	private Long userId = UserManager.NO_USER;
	private int iterations;

	public ServerCommunication(Socket communicationSocket) {
		super(communicationSocket);
	}

	@Override
	public void handleMessage(NetworkMessage networkMessage) {
		super.handleMessage(networkMessage);

		NetworkMessage statusMessage;
		long userId;

		switch (networkMessage.getType()) {

		case CLIENT_HELLO:
			logger.info("Client Hello message recieved with FQDN " + networkMessage.getClientFQDN());
			NetworkMessage serverResponse = new NetworkMessage();
			serverResponse.setType(MessageType.WELCOME_MESSAGE);
			serverResponse.setMessageId(networkMessage.getMessageId());
			serverResponse.setClientFQDN(networkMessage.getClientFQDN());
			serverResponse.setText(FTPLibrary.FTPConstants.SERVER_WELCOME + " " + "<<" + networkMessage.getClientFQDN() + ">>");
			sendMessage(serverResponse);
			break;

		case REGISTER_PLAIN:
			logger.info("Plain registration request.");
			System.out.println("Plain registration request.");
			statusMessage = new NetworkMessage();
			statusMessage.setType(MessageType.CONTINUE_WITH_PASS);
			statusMessage.setMessageId(networkMessage.getMessageId());

			//new random salt for the current connection generated
			salt = new String (Crypto.generateRandomSalt());
			//encrypt the salt in base64
			String saltEncodedBase64 = Base64.getEncoder().encodeToString(salt.getBytes());
			iterations = Crypto.getRandomIterations();

			statusMessage.setSalt(saltEncodedBase64);
			statusMessage.setIterations(iterations);
			sendMessage(statusMessage);
			break;

		case REGISTER_PASS:
			logger.info("Registration password delivered");

			registerPassword = Crypto.saltPassword(salt, ConstantsFTP.MASTER_PASS, iterations);
			String registerPasswordFromClient = networkMessage.getText();

			statusMessage = new NetworkMessage();
			if (registerPassword.equals(registerPasswordFromClient)) {
				statusMessage.setType(MessageType.REGISTRATION_ALLOWED);
				statusMessage.setMessageId(networkMessage.getMessageId());
			}else {
				statusMessage.setType(MessageType.AUTHENTICATION_FAILED);
				statusMessage.setMessageId(networkMessage.getMessageId());
			}

			sendMessage(statusMessage);

			break;

		case CREATE_USER:

			String randomSalt = new String (Base64.getDecoder().decode(networkMessage.getSalt().getBytes()));
			byte[] initVector = Base64.getDecoder().decode(networkMessage.getInitVector());
			int iterations = networkMessage.getIterations();
			String secretKey = Crypto.saltPassword(randomSalt,registerPassword, iterations);

			String encryptedPasswordHash = new String(Base64.getDecoder().decode(networkMessage.getPassword()));

			String passwordHash = Crypto.decryptAES256(encryptedPasswordHash, initVector, secretKey);


			userId = UserManager.getInstance().createUser(networkMessage.getActor(), passwordHash,
					networkMessage.getEmail());
			String userDirectoryPath = UserManager.USER_FILES_DIRECTORY + networkMessage.getActor();

			statusMessage = new NetworkMessage();
			statusMessage.setType(MessageType.STATUS_RESPONSE);
			statusMessage.setMessageId(networkMessage.getMessageId());

			if (userId > UserManager.NO_USER) {
				statusMessage.setStatus(NetworkMessage.STATUS_OK);

				if (FileUtils.createDirectory(userDirectoryPath) == false) {
					FileUtils.createDirectory(userDirectoryPath);
				}
				logger.info("User " + userId + " created successfully.");
			} else {
				statusMessage.setStatus(NetworkMessage.STATUS_ERROR_CREATING_USER);
				logger.info("User creation failed.");
			}
			sendMessage(statusMessage);
			break;
		case LOGIN:
			int randomIterationsFromClient = networkMessage.getIterations();
			userId = UserManager.getInstance().login(salt, networkMessage.getActor(), networkMessage.getPassword(), randomIterationsFromClient);

			statusMessage = new NetworkMessage();
			statusMessage.setType(MessageType.STATUS_RESPONSE);
			statusMessage.setMessageId(networkMessage.getMessageId());

			if (userId > UserManager.NO_USER) {
				this.userId = userId;
				MessagingManager.getInstance().addLoggedUserInMap(userId, this);
				logger.info("User " + userId + " logged in!");
				statusMessage.setStatus(NetworkMessage.STATUS_OK);
			} else {
				logger.info("User login error.");
				statusMessage.setStatus(NetworkMessage.STATUS_ERROR_LOGGING_IN);
			}
			sendMessage(statusMessage);
			break;
		case LOGOUT:
			statusMessage = new NetworkMessage();
			statusMessage.setType(MessageType.STATUS_RESPONSE);
			statusMessage.setStatus(NetworkMessage.STATUS_OK);
			statusMessage.setMessageId(networkMessage.getMessageId());

			logger.info("User logged out.");
			MessagingManager.getInstance().removeLoggedUserFromMap(this.userId);
			UserManager.getInstance().logout(this.userId);
			this.userId = UserManager.NO_USER;
			sendMessage(statusMessage);
			break;
		case UPLOAD_FILE:
			String localFilePath = UserManager.USER_FILES_DIRECTORY
					+ UserManager.getInstance().getLoggedInUser(this.userId).getName() + "\\"
					+ networkMessage.getFilePath();
			handleIncomingFile(localFilePath, networkMessage.getMessageId());
			break;
		case DOWNLOAD_FILE:
			String localPath = UserManager.USER_FILES_DIRECTORY
					+ UserManager.getInstance().getLoggedInUser(this.userId).getName() + "\\"
					+ networkMessage.getFilePath();
			handleOutcomingFile(localPath, networkMessage.getMessageId());
			break;
		case UPLOAD_CHUNK:
			handleFileChunk(networkMessage.getText());
			break;
		case DELETE_FILE:
			statusMessage = new NetworkMessage();
			statusMessage.setType(MessageType.STATUS_RESPONSE);
			String filePath = UserManager.USER_FILES_DIRECTORY
					+ UserManager.getInstance().getLoggedInUser(this.userId).getName() + "\\"
					+ networkMessage.getFilePath();

			if (FileUtils.deleteFile(filePath) == true) {
				statusMessage.setStatus(NetworkMessage.STATUS_OK);
			} else {
				statusMessage.setStatus(NetworkMessage.STATUS_ERROR_DELETING_FILE);
			}

			statusMessage.setMessageId(networkMessage.getMessageId());
			sendMessage(statusMessage);
			break;
		case COPY_FILE:
			statusMessage = new NetworkMessage();
			statusMessage.setType(MessageType.STATUS_RESPONSE);

			String sourcePath = UserManager.USER_FILES_DIRECTORY
					+ UserManager.getInstance().getLoggedInUser(this.userId).getName() + "\\"
					+ networkMessage.getFilePath();
			String targetPath = UserManager.USER_FILES_DIRECTORY
					+ UserManager.getInstance().getLoggedInUser(this.userId).getName() + "\\"
					+ networkMessage.getNewFilePath();

			if (FileUtils.copyFile(sourcePath, targetPath) == true) {
				statusMessage.setStatus(NetworkMessage.STATUS_OK);
			} else {
				statusMessage.setStatus(NetworkMessage.STATUS_ERROR_COPYING_FILE);
			}

			statusMessage.setMessageId(networkMessage.getMessageId());
			sendMessage(statusMessage);
			break;
		case MOVE_FILE:
			statusMessage = new NetworkMessage();
			statusMessage.setType(MessageType.STATUS_RESPONSE);

			String srcPath = UserManager.USER_FILES_DIRECTORY
					+ UserManager.getInstance().getLoggedInUser(this.userId).getName() + "\\"
					+ networkMessage.getFilePath();
			String targPath = UserManager.USER_FILES_DIRECTORY
					+ UserManager.getInstance().getLoggedInUser(this.userId).getName() + "\\"
					+ networkMessage.getNewFilePath();

			if (FileUtils.moveFile(srcPath, targPath) == true) {
				statusMessage.setStatus(NetworkMessage.STATUS_OK);
			} else {
				statusMessage.setStatus(NetworkMessage.STATUS_ERROR_MOVING_FILE);
			}

			statusMessage.setMessageId(networkMessage.getMessageId());
			sendMessage(statusMessage);
			break;
		case RENAME_FILE:
			statusMessage = new NetworkMessage();
			statusMessage.setType(MessageType.STATUS_RESPONSE);

			String pathName = UserManager.USER_FILES_DIRECTORY
					+ UserManager.getInstance().getLoggedInUser(this.userId).getName() + "\\"
					+ networkMessage.getFilePath();
			String newPathName = UserManager.USER_FILES_DIRECTORY
					+ UserManager.getInstance().getLoggedInUser(this.userId).getName() + "\\"
					+ networkMessage.getNewFilePath();

			if (FileUtils.renameFile(pathName, newPathName) == true) {
				statusMessage.setStatus(NetworkMessage.STATUS_OK);
			} else {
				statusMessage.setStatus(NetworkMessage.STATUS_ERROR_RENAME_FILE);
			}

			statusMessage.setMessageId(networkMessage.getMessageId());
			sendMessage(statusMessage);
			break;
		case CREATE_DIRECTORY:
			statusMessage = new NetworkMessage();
			statusMessage.setType(MessageType.STATUS_RESPONSE);

			String directoryPath = UserManager.USER_FILES_DIRECTORY
					+ UserManager.getInstance().getLoggedInUser(this.userId).getName() + "\\"
					+ networkMessage.getFilePath();

			if (FileUtils.createDirectory(directoryPath) == true) {
				statusMessage.setStatus(NetworkMessage.STATUS_OK);
			} else {
				statusMessage.setStatus(NetworkMessage.STATUS_ERROR_CREATING_DIRECTORY);
			}

			statusMessage.setMessageId(networkMessage.getMessageId());
			sendMessage(statusMessage);
			break;
		case LIST_FILES:
			statusMessage = new NetworkMessage();
			statusMessage.setType(MessageType.STATUS_RESPONSE);

			String defaultDir = UserManager.USER_FILES_DIRECTORY
					+ UserManager.getInstance().getLoggedInUser(this.userId).getName();

			Directory dirToClient = new Directory(defaultDir);
			dirToClient = FileUtils.listFiles(defaultDir, dirToClient);

			if (!dirToClient.equals(null)) {
				statusMessage.setFileList(dirToClient);
				statusMessage.setStatus(NetworkMessage.STATUS_OK);
			}

			statusMessage.setMessageId(networkMessage.getMessageId());
			sendMessage(statusMessage);
			break;
		case LIST_FILES_SHARED_BY_YOU:
			statusMessage = new NetworkMessage();
			statusMessage.setType(MessageType.STATUS_RESPONSE);

			String userName = UserManager.getInstance().getLoggedInUser(this.userId).getName();

			List<User> allFilesShared = ORM.selectAllFilesYouHaveShared(userName);
			Directory listSharedFiles = new Directory();
			File file;
			String recipientName;
			for (User sharedFile : allFilesShared) {
				if (!sharedFile.equals(null)) {
					file = new File(sharedFile.getFileName());
					recipientName = sharedFile.getUserNameSharedTo();
					listSharedFiles.addUserToShareWith(file.getName(), recipientName);
				}
			}
			statusMessage.setFileList(listSharedFiles);
			statusMessage.setStatus(NetworkMessage.STATUS_OK);

			statusMessage.setMessageId(networkMessage.getMessageId());
			sendMessage(statusMessage);
			break;
		case LIST_FILES_SHARED_WITH_YOU:
			statusMessage = new NetworkMessage();
			statusMessage.setType(MessageType.STATUS_RESPONSE);

			String notifierUserName = UserManager.getInstance().getLoggedInUser(this.userId).getName();

			List<User> allFilesSharedFrom = ORM.selectAllFilesSharedWithMe(notifierUserName);
			Directory listSharedFilesFrom = new Directory();
			File sharedFileFrom;
			String senderName;
			for (User sharedFile : allFilesSharedFrom) {
				if (!sharedFile.equals(null)) {
					sharedFileFrom = new File(sharedFile.getFileName());
					senderName = sharedFile.getUserNameSharedTo();
					listSharedFilesFrom.addUserToShareWith(sharedFileFrom.getName(), senderName);
				}
			}
			statusMessage.setFileList(listSharedFilesFrom);
			statusMessage.setStatus(NetworkMessage.STATUS_OK);

			statusMessage.setMessageId(networkMessage.getMessageId());
			sendMessage(statusMessage);
			break;
		case SHARE_FILE:

			userId = UserManager.getInstance().insertSharedFileInfo(networkMessage.getUser(),
					UserManager.getInstance().getLoggedInUser(this.userId).getName(), networkMessage.getFileName(),
					networkMessage.getFilePath());

			statusMessage = new NetworkMessage();
			statusMessage.setType(MessageType.STATUS_RESPONSE);

			String userNameSharedTo = networkMessage.getUser();
			String filePathSharedFile = networkMessage.getFilePath();

			statusMessage.setStatus(NetworkMessage.STATUS_OK);

			statusMessage.setMessageId(networkMessage.getMessageId());
			sendMessage(statusMessage);
			break;
		default:
			break;
		}
	}

	@Override
	public void unregisterCommunication() {
		super.unregisterCommunication();

		if (userId != UserManager.NO_USER) {
			MessagingManager.getInstance().removeLoggedUserFromMap(userId);
		}

		MessagingManager.getInstance().removeCommunication(this);
	}

}
