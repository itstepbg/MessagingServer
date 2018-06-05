package models.data;

import java.net.InetAddress;

public class User {

	//TODO Place enum before all field declarations.
	
	private String name;

	private String passwordHash;

	private String email;

	//TODO Change the enum name as per the Java standard and add a field for the status itself.
	private enum status {
		OFFLINE, ONLINE, AWAY, INVISIBLE, DND
	}

	private InetAddress ipAddress;
	
	//TODO Create initialization constructor.
	
	//TODO Create field accessor methods.
}
