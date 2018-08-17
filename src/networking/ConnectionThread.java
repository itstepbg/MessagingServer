package networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.logging.Logger;

import library.models.network.MessageType;
import library.models.network.NetworkMessage;
import library.util.ConstantsFTP;
import library.util.Crypto;
import library.util.MessagingLogger;
import managers.MessagingManager;
import storage.FTPConstants;

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

				// send server hello
				NetworkMessage helloMessage = new NetworkMessage();
				helloMessage.setType(MessageType.SERVER_HELLO);
				helloMessage.setText(FTPConstants.SERVER_HELLO_MESSAGE);
				newCommunication.sendMessage(helloMessage);

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
