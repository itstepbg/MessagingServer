package models.data;

import java.net.InetAddress;

public class User {

	private String name;

	private String passwordHash;

	private String email;

	private enum status {
		OFFLINE, ONLINE, AWAY, INVISIBLE, DND
	}

	private InetAddress ipAddress;
}
