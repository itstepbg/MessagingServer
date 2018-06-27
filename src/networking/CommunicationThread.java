package networking;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import managers.MessagingManager;
import managers.UserManager;
import models.network.NetworkMessage;

public class CommunicationThread extends Thread {

	private Socket socket;
	private Long userId;

	public CommunicationThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		BufferedReader inFromClient = null;
		DataOutputStream outToClient = null;

		try {
			inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// TODO
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
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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
							MessagingManager.getInstance().initCommunication(userId, this);
						} else {
							try {
								socket.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						break;

					default:
						break;
					}
				}
			}
		}

		MessagingManager.getInstance().closeCommunication(userId);
	}

}
