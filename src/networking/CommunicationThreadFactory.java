package networking;

import java.net.Socket;

public class CommunicationThreadFactory {

	static CommunicationThread createCommunicationThread(Socket socket) {
		return new CommunicationThread(socket);
	}
}
