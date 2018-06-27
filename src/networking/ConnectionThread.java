package networking;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import managers.MessagingManager;
import managers.UserManager;
import models.network.NetworkMessage;

public class ConnectionThread extends Thread {

	ServerSocket connectionSocket;

	@Override
	public void run() {
		try {
			connectionSocket = new ServerSocket(6789);
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (!Thread.interrupted()) {
			try {
				Socket communicationSocket = connectionSocket.accept();

				BufferedReader inFromClient = new BufferedReader(
						new InputStreamReader(communicationSocket.getInputStream()));
				//TODO
				DataOutputStream outToClient = new DataOutputStream(communicationSocket.getOutputStream());

				String loginMessageXml = inFromClient.readLine();

				Serializer serializer = new Persister();
				NetworkMessage loginMessage = null;
				try {
					loginMessage = serializer.read(NetworkMessage.class, loginMessageXml);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (loginMessage != null) {
					long userId = UserManager.getInstance().login(loginMessage.getActor(),
							loginMessage.getPasswordHash());

					if (userId > UserManager.NO_USER) {
						MessagingManager.getInstance().initCommunication(userId, communicationSocket);
					} else {
						communicationSocket.close();
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
