package networking;

import java.net.Socket;

import models.network.NetworkMessage;

public class Communication implements CommunicationInterface {

	private InputThread inputThread;
	private OutputThread outputThread;

	// TODO The communicationSocket should also be a field of this class.
	public Communication(Socket communicationSocket) {
		inputThread = CommunicationThreadFactory.createInputThread(communicationSocket);
		outputThread = CommunicationThreadFactory.createOutputThread(communicationSocket);

		inputThread.setCommunicationListener(this);
		outputThread.setCommunicationListener(this);

		inputThread.start();
		outputThread.start();
	}

	@Override
	public void addMessageToQueue(NetworkMessage message) {
		// TODO Add message to the output thread queue.
	}

	@Override
	public void closeCommunication() {
		// TODO Closing the socket and removing the communication object from the
		// MessagingManager data structures should be handled here instead of the InputThread.
	}
	
	// TODO Add the message serialization method.
}
