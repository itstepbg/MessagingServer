package networking;

import java.net.Socket;

public class CommunicationThreadFactory {

	static InputThread createInputThread(Socket socket) {
		return new InputThread(socket);
	}
	static OutputThread createOutputThread(Socket socket) {
		return new OutputThread(socket);
	}
}
