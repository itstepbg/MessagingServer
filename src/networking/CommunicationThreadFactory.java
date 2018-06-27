package networking;

import java.net.Socket;

public class CommunicationThreadFactory {

	public static CommunicationThread createCommunicationThread(Long userId, Socket socket) {
		
		return new CommunicationThread(userId, socket);
	}
}
