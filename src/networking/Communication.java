package networking;

import java.net.Socket;

import library.models.network.MessageType;
import library.models.network.NetworkMessage;
import library.networking.CommonCommunication;
import library.util.FileUtils;
import managers.MessagingManager;
import managers.UserManager;

public class Communication extends CommonCommunication {

	private Long userId = UserManager.NO_USER;

	public Communication(Socket communicationSocket) {
		super(communicationSocket);
	}

	@Override
	public void handleMessage(NetworkMessage networkMessage) {
		super.handleMessage(networkMessage);

		NetworkMessage statusMessage;
		long userId;

		switch (networkMessage.getType()) {
		case HEARTBEAT:
			heartbeatThread.resetTimeoutBuffer();
			break;
		case CREATE_USER:
			userId = UserManager.getInstance().createUser(networkMessage.getActor(), networkMessage.getPasswordHash(),
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
			userId = UserManager.getInstance().login(networkMessage.getActor(), networkMessage.getPasswordHash());

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
					+ UserManager.getInstance().getLoggedInUser(this.userId) + "\\" + networkMessage.getFilePath();
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
