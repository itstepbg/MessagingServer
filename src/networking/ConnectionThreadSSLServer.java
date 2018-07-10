package networking;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author user
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.PrivilegedActionException;
import java.security.Security;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import com.sun.net.ssl.internal.ssl.Provider;

public class ConnectionThreadSSLServer {

	/**
	 * @param args
	 */

	public void runServer() {

		int intSSLport = 4443; // Port where the SSL Server needs to listen for new requests from the client

		{
			// Registering the JSSE provider
			Security.addProvider(new Provider());

			// Specifying the Keystore details
			System.setProperty("javax.net.ssl.keyStore", "C:\\Users\\doom\\MessagingServer\\store1\\server.ks");
			System.setProperty("javax.net.ssl.keyStorePassword", "cisco.123");

			// Enable debugging to view the handshake and communication which happens
			// between the SSLClient and the SSLServer
			// System.setProperty("javax.net.debug", "all");
		}

		try {
			// Initialize the Server Socket
			SSLServerSocketFactory sslServerSocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
					.getDefault();
			SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketfactory.createServerSocket(intSSLport);
			SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();

			// Create Input / Output Streams for communication with the client
			while (true) {
				BufferedReader in;
				try (PrintWriter out = new PrintWriter(sslSocket.getOutputStream(), true)) {
					in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
					String inputLine, outputLine;
					while ((inputLine = in.readLine()) != null) {
						out.println(inputLine);
						System.out.println(inputLine);
					}

					// Close the streams and the socket
				}
				in.close();
				sslSocket.close();
				sslServerSocket.close();

			}
		}

		catch (Exception exp) {
			PrivilegedActionException priexp = new PrivilegedActionException(exp);
			System.out.println(" Priv exp --- " + priexp.getMessage());

			System.out.println(" Exception occurred .... " + exp);
			exp.printStackTrace();
		}

	}

}
