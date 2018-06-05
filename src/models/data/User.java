package models.data;

import java.net.InetAddress;

public class User {

	// Placed enum before all field declarations.
	// Changed the enum name as per the Java standard and added a field for the
	// status
	// itself.
	private enum Status {
		OFFLINE, ONLINE, AWAY, INVISIBLE, DND
	}

	private Status status;

	private String name;

	private String passwordHash;

	private String email;

	private InetAddress ipAddress;

	// Created initialization constructor.
	public User(String name, String passwordHash, String email) {
		super();
		this.name = name;
		this.passwordHash = passwordHash;
		this.email = email;
	}

	// Created field accessors.
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public InetAddress getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(InetAddress ipAddress) {
		this.ipAddress = ipAddress;
	}
}
