package managers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import library.models.data.User;
import models.data.MasterUser;
import storage.ORM;

public class UserManager {

	public static final String USER_FILES_DIRECTORY = "userDirectories\\";
	public static final long NO_USER = -1;

	private final static UserManager instance = new UserManager();

	private HashMap<Long, User> loggedUsers = new HashMap<>();

	private UserManager() {
	}

	public static UserManager getInstance() {
		return instance;
	}

	public long createUser(String name, String passwordHash, String email) {
		User user = new User(name, passwordHash, email);
		return ORM.insertUser(user);
	}

	public boolean removeUser(String keyword) {
		User user = getUser(keyword);
		return ORM.deleteUser(user);
	}

	public List<User> getAllUsers() {
		return Collections.unmodifiableList(ORM.selectAllUsers());
	}

	public User getUser(String keyword) {
		return ORM.selectUser(keyword, keyword);
	}

	public MasterUser getMasterUser(String keyword) {
		return ORM.selectMasterUser(keyword);
	}

	public User getLoggedInUser(Long userId) {
		return loggedUsers.get(userId);
	}

	public long login(String name, String passwordHash) {
		User user = getUser(name);

		if (user == null) {
			return NO_USER;
		} else {
			if (user.getPasswordHash().equals(passwordHash)) {
				loggedUsers.put(user.getUserId(), user);
				return user.getUserId();
			}
			return NO_USER;
		}
	}

	public void logout(long userId) {
		loggedUsers.remove(userId);
	}
}
