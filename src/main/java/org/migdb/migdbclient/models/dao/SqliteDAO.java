package org.migdb.migdbclient.models.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.migdb.migdbclient.controllers.dbconnector.SqliteDbConnManager;
import org.migdb.migdbclient.models.dto.ConnectorDTO;
import org.migdb.migdbclient.models.dto.RelationshiCardinalityDTO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SqliteDAO {

	SqliteDbConnManager dbConnManager = null;

	/**
	 * Create database connection manager object from SqliteDbConnManager class
	 */
	public SqliteDAO() {
		dbConnManager = new SqliteDbConnManager();
	}

	/**
	 * Create table called CONNECTIONS and QUERY_OPERATORS if not exists
	 */
	public void createTable() {
		Connection dbConn = null;

		String queryConnection = "CREATE TABLE IF NOT EXISTS `CONNECTIONS` (`ConnectionName` TEXT,`mysqlHostName` TEXT,`mongoHostName` TEXT,`mysqlPort` INTEGER, `mongoPort` INTEGER,`UserName` TEXT,`Password` TEXT,`SchemaName` TEXT,PRIMARY KEY(ConnectionName));";
		String queryOperators = "CREATE TABLE IF NOT EXISTS `QUERY_OPERATORS` (`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `Operation` TEXT NOT NULL, `Symbol` TEXT NOT NULL, `Keyword` TEXT );";
		String queryPath = "CREATE TABLE IF NOT EXISTS `PATH` (`id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,`path` TEXT NOT NULL, `isDefault` INTEGER, `type` TEXT);";
		String[] queries = new String[]{queryConnection,queryOperators,queryPath};
		dbConn = dbConnManager.getConnection();
		
		try {
			for(String sql : queries){
				PreparedStatement ps = dbConn.prepareStatement(sql);
				ps.executeUpdate();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
	}

	/**
	 * Insert connection informations into database
	 * 
	 * @param dto
	 * @return
	 */
	public boolean insertConnection(ConnectorDTO dto) {
		boolean result = false;
		Connection dbConn = null;

		try {
			dbConn = dbConnManager.getConnection();
			String query = "INSERT INTO CONNECTIONS(ConnectionName,mysqlHostName,mongoHostName,mysqlPort,mongoPort,UserName,Password,SchemaName) VALUES(?,?,?,?,?,?,?,?)";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ps.setString(1, dto.getConnectionName());
			ps.setString(2, dto.getMysqlHostName());
			ps.setString(3, dto.getMongoHostName());
			ps.setInt(4, dto.getMysqlPort());
			ps.setInt(5, dto.getMongoPort());
			ps.setString(6, dto.getUserName());
			ps.setString(7, dto.getPassword());
			ps.setString(8, dto.getSchemaName());
			int val = ps.executeUpdate();
			result = (val == 1) ? true : false;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}

		return result;
	}
	
	/**
	 * Insert Operators into QUERY_OPERATORS table
	 */
	public void insertOperators() {
		Connection dbConn = null;
		try {
			dbConn = dbConnManager.getConnection();
			String query = "INSERT INTO QUERY_OPERATORS(id,Operation, Symbol, Keyword) VALUES"
					+ "(1,'Equality','=',''),"
					+ "(2,'Less Than','<','$lt'),"
					+ "(3,'Less Than Equals','<=','$lte'),"
					+ "(4,'Greater Than','>','$gt'),"
					+ "(5,'Greater Than Equals','>=','$gte'),"
					+ "(6,'Not Equals','!=','$ne');";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
	}

	/**
	 * Get connection information from database
	 * 
	 * @return
	 */
	public ArrayList<ConnectorDTO> getConnectionInfo() {
		ArrayList<ConnectorDTO> connectionInfo = null;
		Connection dbConn = null;

		try {
			dbConn = dbConnManager.getConnection();
			String query = "SELECT * FROM CONNECTIONS";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			connectionInfo = new ArrayList<ConnectorDTO>();
			while (rs.next()) {
				ConnectorDTO connector = new ConnectorDTO();
				connector.setConnectionName(rs.getString("ConnectionName"));
				connector.setMysqlHostName(rs.getString("mysqlHostName"));
				connector.setMongoHostName(rs.getString("mongoHostName"));
				connector.setMysqlPort(rs.getInt("mysqlPort"));
				connector.setMongoPort(rs.getInt("mongoPort"));
				connector.setUserName(rs.getString("UserName"));
				connector.setPassword(rs.getString("Password"));
				connector.setSchemaName(rs.getString("SchemaName"));
				connectionInfo.add(connector);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
		return connectionInfo;
	}
	
	public void createRelationshipTypes(){
		Connection dbConn = null;
		try {
			dbConn = dbConnManager.getConnection();
			String query = "CREATE TABLE IF NOT EXISTS REFERENCEDLIST (`TABLE_NAME` TEXT,`COLUMN_NAME` TEXT,`REFERENCED_TABLE_NAME` TEXT,`REFERENCED_COLUMN_NAME` TEXT, `RELATIONSHIP_TYPE` TEXT);";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
	}
	
	/**
	 * Insert relationship types into REFERENCEDLIST table
	 * @param tablesName
	 * @param columnName
	 * @param referencedTableName
	 * @param referencedColumnName
	 * @param relationshipType
	 */
	public void insertRelationshipTypes(String tablesName, String columnName, String referencedTableName, String referencedColumnName, String relationshipType){
		boolean result = false;
		Connection dbConn = null;
		try {
			dbConn = dbConnManager.getConnection();
			String query = "INSERT INTO REFERENCEDLIST(TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME, RELATIONSHIP_TYPE) VALUES(?,?,?,?,?)";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ps.setString(1, tablesName);
			ps.setString(2, columnName);
			ps.setString(3, referencedTableName);
			ps.setString(4, referencedColumnName);
			ps.setString(5, relationshipType);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
	}
	
	/**
	 * Drop relationship type from REFERENCEDLIST table
	 */
	public void dropRelationshipTypes(){
		Connection dbConn = null;
		try {
			dbConn = dbConnManager.getConnection();
			String query = "DROP TABLE REFERENCEDLIST";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
	}
	
	/**
	 * View REFERENCEDLIST details
	 * @return
	 */
	public ArrayList<RelationshiCardinalityDTO> getReferencedList() {
		ArrayList<RelationshiCardinalityDTO> references = null;
		Connection dbConn = null;
		try {
			dbConn = dbConnManager.getConnection();
			String query = "SELECT * FROM REFERENCEDLIST";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			references = new ArrayList<RelationshiCardinalityDTO>();
			while (rs.next()) {
				RelationshiCardinalityDTO dto = new RelationshiCardinalityDTO();
				dto.setCOLUMN_NAME(rs.getString("COLUMN_NAME"));
				dto.setREFERENCED_COLUMN_NAME(rs.getString("REFERENCED_COLUMN_NAME"));
				dto.setREFERENCED_TABLE_NAME(rs.getString("REFERENCED_TABLE_NAME"));
				dto.setRELATIONSHIP_TYPE(rs.getString("RELATIONSHIP_TYPE"));
				dto.setTABLE_NAME(rs.getString("TABLE_NAME"));
				references.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
		return references;
	}
	
	/**
	 * Get query operators details
	 * @return
	 */
	public ObservableList<String> getQueryOperators(){
		ObservableList<String> operators = FXCollections.observableArrayList();
		Connection dbConn = null;
		try {
			dbConn = dbConnManager.getConnection();
			String query = "SELECT * FROM QUERY_OPERATORS";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				operators.add(rs.getString(3));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
		return operators;
		
	}
	
	/**
	 * Get Keyword in query operator according to symbol name
	 * @param keyword
	 * @return
	 */
	public String getQueryOperatorsKeyword(String keyword){
		String keywords = null;
		Connection dbConn = null;
		try {
			dbConn = dbConnManager.getConnection();
			String query = "SELECT keyword FROM QUERY_OPERATORS WHERE Symbol = '"+keyword+"'";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				keywords = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
		return keywords;
		
	}
	
	/**
	 * Check whether mongo database path is exists
	 * @param type
	 * @return
	 */
	public boolean isPathExist(String type){
		boolean isExist = false;
		Connection dbConn = null;
		try {
			dbConn = dbConnManager.getConnection();
			String query = "SELECT * FROM PATH WHERE type = '"+type+"'";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				isExist = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isExist;
	}
	
	/**
	 * Insert mongo database path
	 * @param dbPath
	 * @param isDefault
	 * @param dbType
	 */
	public void insertMongoDbPath(String dbPath, int isDefault, String dbType){
		Connection dbConn = null;
		try {
			dbConn = dbConnManager.getConnection();
			String query = "INSERT INTO PATH(path, isDefault, type) VALUES(?,?,?)";
			PreparedStatement ps =dbConn.prepareStatement(query);
			ps.setString(1, dbPath);
			ps.setInt(2, isDefault);
			ps.setString(3, dbType);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
	}

}
