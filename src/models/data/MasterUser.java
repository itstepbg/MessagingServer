package models.data;

public class MasterUser {

	public final static String COLUMN_NAME = "name";
	public final static String COLUMN_PASSWORD_HASH = "password";
	public final static String TABLE_NAME = "masterusers";

	private String name;
	private String passwordHash;

	public MasterUser(String name, String passwordHash) {
		this.name = name;
		this.passwordHash = passwordHash;
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
}
