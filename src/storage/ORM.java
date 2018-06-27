package storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import managers.DatabaseManager;
import models.data.User;

public class ORM {
	// TODO
	// insertUser name,pass,email
	// deleteUser
	// select * users

	public static boolean insertUser(User user) {
		boolean result = false;
		String name = user.getName();
		String passwordHash = user.getPasswordHash();
		String email = user.getEmail();

		Map<String, String> userData = new HashMap<>();
		userData.put(User.COLUMN_NAME, name);
		userData.put(User.COLUMN_PASSWORD_HASH, passwordHash);
		userData.put(User.COLUMN_EMAIL, email);

		try {
			result = DatabaseManager.getInstance().insert(User.TABLE_NAME, userData);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static boolean deleteUser(User user) {
		boolean result = false;
		try {
			// TODO
			result = DatabaseManager.getInstance().delete(User.TABLE_NAME, user.getUserId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static User selectUser(String name, String email) {
		User user = null;
		WCB filter = new WCB();
		filter.eq(User.COLUMN_NAME, name).or().eq(User.COLUMN_EMAIL, email);
		ResultSet dbResultSet = null;
		try {
			dbResultSet = DatabaseManager.getInstance().select(User.TABLE_NAME, filter);
			if (dbResultSet.next()) {
				user = resultToUser(dbResultSet);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return user;
	}

	public static List<User> selectAllUsers() {
		List<User> listOfUsers = new ArrayList<>();
		ResultSet dbResultSet = null;

		try {
			dbResultSet = DatabaseManager.getInstance().select(User.TABLE_NAME, null);

			while (dbResultSet.next()) {
				User tempUser = resultToUser(dbResultSet);
				listOfUsers.add(tempUser);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (dbResultSet != null) {
				try {
					dbResultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return listOfUsers;
	}

	private static User resultToUser(ResultSet dbResultSet) {
		User result = null;
		try {
			long userId = dbResultSet.getLong(User.COLUMN_ID);
			String name = dbResultSet.getString(User.COLUMN_NAME);
			String passwordHash = dbResultSet.getString(User.COLUMN_PASSWORD_HASH);
			String email = dbResultSet.getString(User.COLUMN_EMAIL);

			result = new User(userId, name, passwordHash, email);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
