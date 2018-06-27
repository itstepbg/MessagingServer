package managers;

import networking.ConnectionThread;

public class NetworkManager {

	private ConnectionThread connectionThread;
	
	public void startConnectionThread(int serverPort) {
		connectionThread = new ConnectionThread(serverPort);
		connectionThread.start();
	}
	
	public void stopConnectionThread() {
		connectionThread.interrupt();
	}
}
