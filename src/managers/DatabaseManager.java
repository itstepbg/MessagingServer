package managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import exceptions.InvalidWhereClauseException;
import storage.SqlData;
import storage.WCB;
import library.util.MessagingLogger;

public class DatabaseManager {
	private static final String JDB_DATABASE = "piraty_chat_client";
	private static final String DB_URL = "jdbc:mysql://18.184.169.162:3306/" + JDB_DATABASE;
	private static final String DB_USER = "chatServer";
	private static final String DB_PASS = "testPassword";
	private static Logger logger = MessagingLogger.getLogger();

	private Connection dbConnection = null;
	private PreparedStatement dbPreparedStatement = null;

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
			logger.info("Connected to SQL server.");
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public boolean insert(String table, Map<String, String> sqlDataMap) throws SQLException {
		boolean result = false;
		SqlData sqlData = prepareSqlData(sqlDataMap);
		String valuesWildCards = generateValuePlaceHolders(sqlDataMap.size());

		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ").append(table).append(" (").append(sqlData.getColumns()).append(") ").append("VALUES")
				.append(" (").append(valuesWildCards).append(")");

		String insertQuery = sb.toString();
		try {
			dbPreparedStatement = dbConnection.prepareStatement(insertQuery);
			if (dbConnection != null) {
				for (int i = 1; i <= sqlData.getValues().size(); i++) {
					dbPreparedStatement.setString(i, sqlData.getValues().get(i - 1));
				}

				result = dbPreparedStatement.executeUpdate() > 0;
				logger.info("Insert Completed Successfully...");

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	public ResultSet select(String table, WCB whereClause) throws SQLException {
		ResultSet dbResultSet = null;

		try {
			String selectQuery = "SELECT * FROM " + table;

			if (whereClause != null) {
				try {
					selectQuery += whereClause.build();
				} catch (InvalidWhereClauseException e) {
					System.out.println(e.getMessage());
				}
			}

			System.out.println("SQL: " + selectQuery);

			if (dbConnection != null) {
				dbPreparedStatement = dbConnection.prepareStatement(selectQuery);
				dbResultSet = dbPreparedStatement.executeQuery();
			}

		} catch (SQLException e) {
			logger.warning(e.getMessage());
		}

		return dbResultSet;
	}

	// TODO implement where clause builder.

	public boolean delete(String table, Long rowId) throws SQLException {
		boolean result = false;
		String deleteStatement = "DELETE FROM " + table + " WHERE id = " + rowId;

		try {
			dbPreparedStatement = dbConnection.prepareStatement(deleteStatement);
			result = dbPreparedStatement.executeUpdate() == 1;
		} catch (SQLException e) {
			logger.warning(e.getMessage());
		}
		return result;
	}

	private String generateValuePlaceHolders(int count) {
		StringBuilder sb = new StringBuilder();
		String wildCard = "?";
		for (int i = 0; i < count; i++) {
			sb.append(wildCard).append(", ");
		}

		String output = sb.substring(0, sb.length() - 2);

		return output;
	}

	private SqlData prepareSqlData(Map<String, String> data) {

		List<String> tmpValues = new ArrayList<>();

		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> dataEntry : data.entrySet()) {
			sb.append(dataEntry.getKey()).append(", ");
			tmpValues.add(dataEntry.getValue());
		}
		String columns = sb.substring(0, sb.length() - 2);

		return new SqlData(columns, tmpValues);
	}

	public void closeConnection() {
		try {
			if (dbPreparedStatement != null) {
				dbPreparedStatement.close();
			}
			if (dbConnection != null) {
				dbConnection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
