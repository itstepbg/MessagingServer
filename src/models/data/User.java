package models.data;

import java.net.InetAddress;

public class User {

	public final static String TABLE_NAME = "users";
	public final static String COLUMN_ID = "id";
	public final static String COLUMN_NAME = "name";
	public final static String COLUMN_PASSWORD_HASH = "passwordHash";
	public final static String COLUMN_EMAIL = "email";

	// Placed enum before all field declarations.
	// Changed the enum name as per the Java standard and added a field for the
	// status
	// itself.

	private enum Status {
		OFFLINE, ONLINE, AWAY, INVISIBLE, DND
	}

	private long userId;

	private Status status;

	private String name;

	private String passwordHash;

	private String email;

	private InetAddress ipAddress;

	// Created initialization constructor.
	public User(String name, String passwordHash, String email) {
		this.name = name;
		this.passwordHash = passwordHash;
		this.email = email;
	}

	public User(long id, String name, String passwordHash, String email) {
		this(name, passwordHash, email);
		this.userId = id;
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

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

}
