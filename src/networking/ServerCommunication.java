package networking;

import java.net.Socket;
import java.util.Base64;

import FTPLibrary.FTPConstants;
import library.models.network.MessageType;
import library.models.network.NetworkMessage;
import library.networking.Communication;
import library.util.Crypto;
import library.util.FileUtils;
import library.util.ConstantsFTP;
import managers.MessagingManager;
import managers.UserManager;

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
			serverResponse.setText(FTPConstants.SERVER_WELCOME + " " + "<<" + networkMessage.getClientFQDN() + ">>");
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

			statusMessage = new NetworkMessage();
			statusMessage.setType(MessageType.STATUS_RESPONSE);
			statusMessage.setMessageId(networkMessage.getMessageId());

			if (userId > UserManager.NO_USER) {
				statusMessage.setStatus(NetworkMessage.STATUS_OK);
				logger.info("User " + userId + " successfully.");
				FileUtils.createDirectory(UserManager.USER_FILES_DIRECTORY + networkMessage.getActor());
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
			handleIncomingFile(localFilePath);
			break;
		case UPLOAD_CHUNK:
			handleFileChunk(networkMessage.getText());
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
