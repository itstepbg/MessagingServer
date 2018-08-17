package managers;

import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import library.models.data.User;
import library.util.Crypto;
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

	public long insertSharedFileInfo(String userNameSharedTo, String userNameSharedFrom, String fileName,
			String filePathSharedFile) {
		User user = new User(userNameSharedTo, userNameSharedFrom, fileName, filePathSharedFile);
		return ORM.insertSharedFileInfo(user);
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

	public User getLoggedInUser(Long userId) {
		return loggedUsers.get(userId);
	}

	//checks if the 
	public long login(String salt, String name, String passwordHash, int randomIterationsFromClient) {

		User user = getUser(name);
		if (user == null) {
			return NO_USER;
		} else {
			// The iterations are hardcoded for now

			String saltedPassword = Crypto.saltPassword(salt, user.getPasswordHash(), randomIterationsFromClient);
			String encodedPassBase64 = Base64.getEncoder().encodeToString(saltedPassword.getBytes());
			if (encodedPassBase64.equals(passwordHash)) {
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
