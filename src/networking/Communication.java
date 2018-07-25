package networking;

import java.io.IOException;
import java.net.Socket;

import library.models.network.MessageType;
import library.models.network.NetworkMessage;
import library.networking.CommonCommunication;
import library.networking.CommunicationInterface;
import library.util.FileUtils;
import managers.MessagingManager;
import managers.UserManager;

public class Communication extends CommonCommunication implements CommunicationInterface {

	private Long userId = UserManager.NO_USER;

	public Communication(Socket communicationSocket) {
		super(communicationSocket);
		startCommunicationThreads(this);
	}

	@Override
	public void sendMessage(NetworkMessage networkMessage) {
		updateMessageCounter(networkMessage);
		outputThread.addMessage(networkMessage);
	}

	@Override
	public void handleMessage(NetworkMessage networkMessage) {
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
			handleIncomingFile(this, localFilePath);
			break;
		case UPLOAD_CHUNK:
			handleFileChunk(this, networkMessage.getText());
			break;
		default:
			break;
		}
	}

	@Override
	public void closeCommunication() {
		logger.info("Closing communication for " + communicationSocket.getInetAddress().getHostAddress());

		heartbeatThread.interrupt();
		// TODO Maybe the file thread reference should be removed when it dies?
		if (fileThread != null && fileThread.isAlive()) {
			fileThread.interrupt();
		}

		if (!communicationSocket.isClosed()) {
			try {
				communicationSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void unregisterCommunication() {
		closeCommunication();

		if (userId != UserManager.NO_USER) {
			MessagingManager.getInstance().removeLoggedUserFromMap(userId);
		}

		MessagingManager.getInstance().removeCommunication(this);
	}

}
