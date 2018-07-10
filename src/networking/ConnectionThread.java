package networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

import util.MessagingServerLogger;

public class ConnectionThread extends Thread {

	private int serverPort;
	private ServerSocket connectionSocket;
	private static Logger logger = MessagingServerLogger.getLogger();

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
				communicationSocket.setSoTimeout(2000);
				logger.info("New connection from " + communicationSocket.getInetAddress().getHostAddress());
				CommunicationThread communicationThread = CommunicationThreadFactory
						.createCommunicationThread(communicationSocket);
				communicationThread.start();
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
