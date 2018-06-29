package networking;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.CamelCaseStyle;

import managers.MessagingManager;
import managers.UserManager;
import models.network.MessageType;
import models.network.NetworkMessage;
import util.Logger;

public class CommunicationThread extends Thread {

	private Socket socket;
	private Long userId = UserManager.NO_USER;

	public CommunicationThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		BufferedReader inFromClient = null;
		DataOutputStream outToClient = null;

		try {
			inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// TODO Outgoing communication should be implemented in a separate thread.
			outToClient = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (inFromClient != null && outToClient != null) {
			while (!socket.isClosed()) {
				String messageXml = null;

				try {
					messageXml = inFromClient.readLine();
					if (messageXml == null) {
						closeCommunication();
						break;
					}
					Logger.logMessage(messageXml);
				} catch (IOException e) {
					// The read has timed-out, so we do a blocking wait for input again...
					continue;
				}

				Serializer serializer = new Persister();
				NetworkMessage networkMessage = null;
				try {
					networkMessage = serializer.read(NetworkMessage.class, messageXml);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (networkMessage != null) {
					switch (networkMessage.getType()) {
					case LOGIN:
						long userId = UserManager.getInstance().login(networkMessage.getActor(),
								networkMessage.getPasswordHash());

						if (userId > UserManager.NO_USER) {
							MessagingManager.getInstance().addLoggedUserInMap(userId, this);
							Logger.logInfo("User " + userId + " logged in!");
						} else {
							closeCommunication();
							Logger.logInfo("User login error.");
						}
						break;
					// The explicit LOGOUT may be redundant, due to the fact that closing the
					// client-side socket handles this.
					case LOGOUT:
						closeCommunication();
						break;
					default:
						break;
					}
				}
			}
		}
	}

	public void closeCommunication() {
		Logger.logInfo("Closing communication for " + socket.getInetAddress().getHostAddress());

		if (!socket.isClosed()) {
			try {
				socket.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (userId != UserManager.NO_USER) {
			MessagingManager.getInstance().removeLoggedUserFromMap(userId);
		}

		MessagingManager.getInstance().removeCommunicationThread(this);
	}
}
