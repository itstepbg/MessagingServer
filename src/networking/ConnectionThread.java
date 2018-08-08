package networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.logging.Logger;

import library.models.network.MessageType;
import library.models.network.NetworkMessage;
import library.util.Crypto;
import library.util.MessagingLogger;
import managers.MessagingManager;

public class ConnectionThread extends Thread {

	private int serverPort;
	private ServerSocket connectionSocket;
	private static Logger logger = MessagingLogger.getLogger();

	public ConnectionThread(int serverPort) {
		this.serverPort = serverPort;
	}

	@Override
	public void run() {
		try {
			connectionSocket = new ServerSocket(serverPort);
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (!Thread.interrupted()) {
			try {
				Socket communicationSocket = connectionSocket.accept();
				// communicationSocket.setSoTimeout(2000);
				logger.info("New connection from " + communicationSocket.getInetAddress().getHostAddress());
				ServerCommunication newCommunication = new ServerCommunication(communicationSocket);
				newCommunication.setSessionID(MessagingManager.generateSessionUUID());
				MessagingManager.getInstance().addConnection(newCommunication);

				String salt = Base64.getEncoder().encodeToString(Crypto.generateRandomBytes(8));
				NetworkMessage networkMessage = new NetworkMessage();
				networkMessage.setType(MessageType.SALT);
				networkMessage.setText(salt);

				newCommunication.setSalt(salt);
				newCommunication.sendMessage(networkMessage);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			connectionSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
