package networking;

import java.net.Socket;

public class CommunicationThread extends Thread{

	protected CommunicationInterface communicationListener;
	protected Socket socket;
	
	public CommunicationThread(Socket socket) {
		this.socket = socket;
	}

	public void setCommunicationListener(CommunicationInterface communicationListener) {
		this.communicationListener = communicationListener;
	}
}
