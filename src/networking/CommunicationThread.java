package networking;

import java.net.Socket;

import managers.MessagingManager;

public class CommunicationThread extends Thread{
	
	private Socket socket;
	private Long userId;
	
	public CommunicationThread(Long userId, Socket socket) {
		this.socket = socket;
		this.userId = userId;
	}
	
	@Override
	public void run() {
		while (!socket.isClosed()) {
			//TODO
		}
		
		MessagingManager.getInstance().closeCommunication(userId);
		
	}

}
