package org.migdb.migdbclient.controllers.dbconnector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.migdb.migdbclient.config.DbConnector;

public class SqliteDbConnManager {

	/**
	 * Sqlite database connection initialize method
	 * @return
	 */
	public Connection getConnection() {
		Connection dbConn = null;
		try {
			String connectionURL = DbConnector.SQLITECONNECTIONURL.getConnector();
			Class.forName("org.sqlite.JDBC");
			dbConn = DriverManager.getConnection(connectionURL);

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return dbConn;
	}
	
	/**
	 * Sqlite database connection object destroy method
	 * @param dbConn
	 */
	public void closeConnection(Connection dbConn) {
		try {
			dbConn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
