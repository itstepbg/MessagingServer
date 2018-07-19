package networking;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import library.models.network.MessageType;
import library.models.network.NetworkMessage;
import library.networking.CommonCommunication;
import library.networking.CommunicationInterface;
import library.networking.CommunicationThreadFactory;
import library.networking.InputThread;
import library.networking.OutputThread;
import library.util.MessagingLogger;
import managers.MessagingManager;
import managers.UserManager;

public class Communication extends CommonCommunication implements CommunicationInterface {

	private static Logger logger = MessagingLogger.getLogger();

	private InputThread inputThread;
	private OutputThread outputThread;
	private Socket communicationSocket;

	private Long userId = UserManager.NO_USER;

	public Communication(Socket communicationSocket) {
		this.communicationSocket = communicationSocket;

		inputThread = CommunicationThreadFactory.createInputThread(communicationSocket);
		outputThread = CommunicationThreadFactory.createOutputThread(communicationSocket);

		inputThread.setCommunicationListener(this);
		outputThread.setCommunicationListener(this);

		inputThread.start();
		outputThread.start();
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
		case CREATE_USER:
			userId = UserManager.getInstance().createUser(networkMessage.getActor(), networkMessage.getPasswordHash(),
					networkMessage.getEmail());

			statusMessage = new NetworkMessage();
			statusMessage.setType(MessageType.STATUS_RESPONSE);
			statusMessage.setMessageId(networkMessage.getMessageId());

			if (userId > UserManager.NO_USER) {
				statusMessage.setStatus(NetworkMessage.STATUS_OK);
				logger.info("User " + userId + " successfully.");
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
			this.userId = UserManager.NO_USER;
			sendMessage(statusMessage);
			break;
		default:
			break;
		}
	}

	// TODO There should be a separate closeSocket() method
	// that should be called from the MessagingManager instead.
	@Override
	public void closeCommunication() {
		logger.info("Closing communication for " + communicationSocket.getInetAddress().getHostAddress());

		if (!communicationSocket.isClosed()) {
			try {
				communicationSocket.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (userId != UserManager.NO_USER) {
			MessagingManager.getInstance().removeLoggedUserFromMap(userId);
		}

		MessagingManager.getInstance().removeCommunication(this);
	}
}
