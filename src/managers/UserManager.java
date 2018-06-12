package managers;

import java.util.Collections;
import java.util.List;

import models.data.User;
import storage.ORM;

public class UserManager {
	
	private static UserManager instance = new UserManager();

	private UserManager() {
	}

	public static UserManager getInstance() {
		return instance;
	}
	
	/* TODO
	 * create
	 * remove
	 * get all users
	 * get single user
	 * login and others ...
	 */
	
	public boolean createUser(String name, String passwordHash, String email) {
		User user = new User(name, passwordHash, email);
		return ORM.insertUser(user);
	}

	
	public boolean removeUser(String keyword) {		
		User user = getUser(keyword);
		return ORM.deleteUser(user);
	}
	
	public List<User> getAllUsers(){
		return Collections.unmodifiableList(ORM.selectAllUsers()); 		
	}
	
	public User getUser(String keyword) {
		return ORM.selectUser(keyword, keyword);
	}
	
}
