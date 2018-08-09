package networking;

import java.net.Socket;

import library.models.data.Directory;
import library.models.network.MessageType;
import library.models.network.NetworkMessage;
import library.networking.Communication;
import library.util.FileUtils;
import managers.MessagingManager;
import managers.UserManager;

public class ServerCommunication extends Communication {

	// TODO This should be moved to the CommonCommunication class.
	private Long userId = UserManager.NO_USER;

	public ServerCommunication(Socket communicationSocket) {
		super(communicationSocket);
	}

	@Override
	public void handleMessage(NetworkMessage networkMessage) {
		super.handleMessage(networkMessage);

		NetworkMessage statusMessage;
		long userId;

		switch (networkMessage.getType()) {
		case CREATE_USER:
			userId = UserManager.getInstance().createUser(networkMessage.getActor(), networkMessage.getPasswordHash(),
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
			userId = UserManager.getInstance().login(salt, networkMessage.getActor(), networkMessage.getPasswordHash());

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

			if (FileUtils.moveFile(pathName, newPathName) == true) {
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
			// Directory fileList = new Directory();
			statusMessage = new NetworkMessage();
			statusMessage.setType(MessageType.STATUS_RESPONSE);

			String defaultDir = UserManager.USER_FILES_DIRECTORY
					+ UserManager.getInstance().getLoggedInUser(this.userId).getName();

			Directory dirToClient = new Directory(defaultDir);
			dirToClient = FileUtils.listFiles(defaultDir, dirToClient);
			dirToClient.printDirectories();
			dirToClient.printFiles();
			if (!dirToClient.equals(null)) {
				statusMessage.setStatus(NetworkMessage.STATUS_OK);
			}

			statusMessage.setMessageId(networkMessage.getMessageId());
			statusMessage.setFileList(dirToClient);
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
