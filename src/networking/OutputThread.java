package networking;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import models.network.NetworkMessage;

public class OutputThread extends CommunicationThread {
	
	private BlockingQueue<NetworkMessage> messages = new ArrayBlockingQueue<NetworkMessage>(64);

	public OutputThread(Socket socket) {
		super(socket);
	}

	public void addMessage(NetworkMessage message) {
		try {
			messages.put(message);
		} catch (InterruptedException e) {

		}
	}

	@Override
	public void run() {
		DataOutputStream outToClient = null;

		try {
			outToClient = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (outToClient != null) {
			while (!socket.isClosed()) {
				NetworkMessage networkMessage = null;
				try {
					networkMessage = messages.poll(2, TimeUnit.SECONDS);
				} catch (InterruptedException e) {

				}
				
				if (networkMessage != null) {
					try {
						String messageXml = serializeMessage(networkMessage);
						outToClient.writeBytes(messageXml);
					} catch (IOException e) {
						// TODO ?
						e.printStackTrace();
					}
				}
			}
		}
	}

	private String serializeMessage(NetworkMessage networkMessage) {
		
		String serializedMessage = null;
		
		try {
			JAXBContext ctx = JAXBContext.newInstance(NetworkMessage.class);

	        Marshaller m = ctx.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);

	        StringWriter sw = new StringWriter();
	        m.marshal(networkMessage, sw);
	        sw.close();
	        
	        serializedMessage = sw.toString();
	        
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return serializedMessage;
	}
}
