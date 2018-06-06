package models.data;

import java.util.ArrayList;
import java.util.List;

public class ChatGroup {

	// Placed enum before all field declarations.
	// Changed the enum name as per the Java standard and added a field for the type
	// itself.
	private enum Type {
		STANDARD, GLOBAL, LOBBY
	}

	// Set the id to long.
	private long id;

	private String name;

	// TODO Should all users be referenced by their ID instead of name?
	private List<String> users = new ArrayList<String>();
	private String host;

	private Boolean isPrivate;

	// Created field accessors.
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Boolean getIsPrivate() {
		return isPrivate;
	}

	public void setIsPrivate(Boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

}
