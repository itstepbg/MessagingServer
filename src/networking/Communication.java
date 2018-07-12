package networking;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;

import managers.MessagingManager;
import managers.UserManager;
import models.network.NetworkMessage;
import util.MessagingServerLogger;

public class Communication implements CommunicationInterface {

	private static Logger logger = MessagingServerLogger.getLogger();
	
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
	public void sendMessage(NetworkMessage  networkMessage) {
		outputThread.addMessage(networkMessage);
	}

	@Override
	public void handleMessage(NetworkMessage networkMessage) {
		switch (networkMessage.getType()) {
		case LOGIN:
			long userId = UserManager.getInstance().login(networkMessage.getActor(),
					networkMessage.getPasswordHash());

			// TODO Generate and send a status response to the client.
			if (userId > UserManager.NO_USER) {
				MessagingManager.getInstance().addLoggedUserInMap(userId, this);
				logger.info("User " + userId + " logged in!");
			} else {
				closeCommunication();
				logger.info("User login error.");
			}
			break;
		// The explicit LOGOUT may be redundant, due to the fact that closing the
		// client-side socket handles this.
		case LOGOUT:
			closeCommunication();
			break;
		default:
			break;
		}
	}

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
