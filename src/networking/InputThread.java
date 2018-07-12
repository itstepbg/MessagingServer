package networking;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Logger;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import managers.MessagingManager;
import managers.UserManager;
import models.network.NetworkMessage;
import util.MessagingServerLogger;

public class InputThread extends CommunicationThread {

	public InputThread(Socket socket) {
		super(socket);
	}

	@Override
	public void run() {
		BufferedReader inFromClient = null;

		try {
			inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			communicationListener.closeCommunication();
		}

		if (inFromClient != null) {
			while (!socket.isClosed()) {
				String messageXml = null;

				try {
					messageXml = inFromClient.readLine();
					if (messageXml == null) {
						communicationListener.closeCommunication();
						break;
					}
					logger.info(messageXml);
				} catch (IOException e) {
					// The read has timed-out, so we do a blocking wait for input again...
					continue;
				}

				NetworkMessage networkMessage = deserializeMessage(messageXml);

				if (networkMessage != null) {
					communicationListener.handleMessage(networkMessage);
				}
			}
		}
	}

	private NetworkMessage deserializeMessage(String messageXml) {
		Serializer serializer = new Persister();
		NetworkMessage networkMessage = null;
		try {
			networkMessage = serializer.read(NetworkMessage.class, messageXml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return networkMessage;
	}
}
