package networking;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class OutputThread extends CommunicationThread {
	
	private Socket socket;
	private BlockingQueue<String> messages = new ArrayBlockingQueue<String>(64);

	public OutputThread(Socket socket) {
		super(socket);
	}

	public void addMessage(String message) {
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
				String message = null;
				try {
					message = messages.poll(2, TimeUnit.SECONDS);
				} catch (InterruptedException e) {

				}
				if (message != null) {
					try {
						outToClient.writeBytes(message);
					} catch (IOException e) {
						// TODO ?
						e.printStackTrace();
					}
				}
			}
		}
	}
}
