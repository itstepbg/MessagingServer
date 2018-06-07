package managers;

import java.sql.DriverManager;

import java.sql.*;

public class DatabaseManager {

	private static final String JDB_DATABASE = "piraty_chat_client";
	private static final String DB_URL = "jdbc:mysql://18.184.169.162:3306/" + JDB_DATABASE;
	private static final String DB_TABLE = "user";
	private static final String DB_USER = "chatServer";
	private static final String DB_PASS = "testPassword";

	private Connection dbConnection = null;

	private static DatabaseManager instance = new DatabaseManager();

	private DatabaseManager() {
		initDatabaseConnection();
	}

	public static DatabaseManager getInstance() {

		return instance;
	}

	private void initDatabaseConnection() {

		try {
			dbConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
			System.out.println("Connected to SQL server.");
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

}
