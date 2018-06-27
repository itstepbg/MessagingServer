package networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionThread extends Thread {

	private int serverPort;
	private ServerSocket connectionSocket;
	
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
				CommunicationThread communicationThread = CommunicationThreadFactory
						.createCommunicationThread(communicationSocket);
				communicationThread.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
