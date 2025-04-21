package org.migdb.migdbclient.controllers.dbconnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDbConnManager {

	/**
	 * MySQL database connection initialize method
	 * @param host
	 * @param port
	 * @param database
	 * @param username
	 * @param password
	 * @return
	 */
	public Connection getConnection(String host, int port, String database, String username, String password){
		Connection dbConn = null;
		try {
			// Add SSL-related params to disable SSL and avoid warnings
			String connectionURL = "jdbc:mysql://" + host + ":" + port + "/" + database +
					"?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
			Class.forName("com.mysql.jdbc.Driver");
			dbConn = DriverManager.getConnection(connectionURL, username, password);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace(); // This is key
			dbConn = null;
		}
		return dbConn;
	}

	/**
	 * MySQL database connection object destroy method
	 * @param dbConn
	 */
	public void closeConnection(Connection dbConn){
		try {
			dbConn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
