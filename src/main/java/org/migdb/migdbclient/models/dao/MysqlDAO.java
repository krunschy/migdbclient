package org.migdb.migdbclient.models.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.migdb.migdbclient.controllers.dbconnector.MySQLDbConnManager;
import org.migdb.migdbclient.models.dto.ColumnsDTO;
import org.migdb.migdbclient.models.dto.ReferenceDTO;
import org.migdb.migdbclient.models.dto.RelationshiptypeDTO;
import org.migdb.migdbclient.models.dto.TableDTO;

public class MysqlDAO {

	MySQLDbConnManager dbConnManager = null;

	public MysqlDAO() {
		dbConnManager = new MySQLDbConnManager();
	}

	public ArrayList<String> getDatabases(String host, int port, String database, String username, String password) {
		ArrayList<String> databases = null;
		Connection dbConn = null;
		try {
			dbConn = dbConnManager.getConnection(host, port, database, username, password);
			String query = "SHOW DATABASES";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			databases = new ArrayList<String>();
			while (rs.next()) {
				databases.add(rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}

		return databases;
	}

	public ArrayList<TableDTO> getTables(String host, int port, String database, String username, String password) {
		ArrayList<TableDTO> tables = null;
		Connection dbConn = null;
		try {
			dbConn = dbConnManager.getConnection(host, port, database, username, password);
			String query = "SHOW TABLES";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			tables = new ArrayList<TableDTO>();
			while (rs.next()) {
				TableDTO dto = new TableDTO();
				dto.setTableName(rs.getString(1));
				tables.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
		return tables;
	}

	public String getPrimaryKey(String host, int port, String database, String username, String password,
			String table) {
		String primaryField = null;
		Connection dbConn = null;
		try {
			dbConn = dbConnManager.getConnection(host, port, database, username, password);
			String query = "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS where TABLE_NAME = '" + table
					+ "' AND COLUMN_KEY = 'PRI'";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				primaryField = rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
		return primaryField;
	}

	public int getColumnCount(String host, int port, String database, String username, String password, String table) {
		int count = 0;
		Connection dbConn = null;
		try {
			dbConn = dbConnManager.getConnection(host, port, database, username, password);
			String query = "SELECT COUNT(COLUMN_NAME) FROM INFORMATION_SCHEMA.COLUMNS where TABLE_NAME = '" + table
					+ "'";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
		return count;
	}
	
	public ArrayList<ColumnsDTO> getDataTypeCount(String host, int port, String database, String username, String password, String table) {
		ArrayList<ColumnsDTO> dataType = null;
		Connection dbConn = null;
		try {
			dbConn = dbConnManager.getConnection(host, port, database, username, password);
			String query = "select DATA_TYPE,COUNT(DATA_TYPE) from information_schema.columns where table_schema = '"+database+"' and table_name='"+table+"' GROUP BY DATA_TYPE";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			dataType = new ArrayList<ColumnsDTO>();
			while (rs.next()) {
				ColumnsDTO dto = new ColumnsDTO();
				dto.setDATA_TYPE(rs.getString(1));
				dto.setDATA_TYPE_COUNT(rs.getInt(2));
				dataType.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
		return dataType;
	}

	public ArrayList<RelationshiptypeDTO> getRelationshipType(String host, int port, String database, String username,
			String password, String table) {
		ArrayList<RelationshiptypeDTO> type = null;
		Connection dbConn = null;
		try {
			dbConn = dbConnManager.getConnection(host, port, database, username, password);
			String query = "select INFORMATION_SCHEMA.COLUMNS.COLUMN_KEY, INFORMATION_SCHEMA.COLUMNS.COLUMN_NAME, INFORMATION_SCHEMA.COLUMNS.TABLE_NAME from INFORMATION_SCHEMA.COLUMNS join INFORMATION_SCHEMA.KEY_COLUMN_USAGE on INFORMATION_SCHEMA.COLUMNS.COLUMN_NAME=INFORMATION_SCHEMA.KEY_COLUMN_USAGE.COLUMN_NAME where INFORMATION_SCHEMA.KEY_COLUMN_USAGE.TABLE_NAME= '"
					+ table + "' and referenced_table_name is not null";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			type = new ArrayList<RelationshiptypeDTO>();
			while (rs.next()) {
				RelationshiptypeDTO dto = new RelationshiptypeDTO();
				dto.setCOLUMN_KEY(rs.getString(1));
				dto.setCOLUMN_NAME(rs.getString(2));
				dto.setTABLE_NAME(rs.getString(3));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}

		return type;
	}

	public ArrayList<ColumnsDTO> getColumnsAccordingTable(String host, int port, String database, String username,
			String password) {
		ArrayList<ColumnsDTO> columns = null;
		Connection dbConn = null;
		try {
			dbConn = dbConnManager.getConnection(host, port, database, username, password);
			String query = "SELECT * " + "FROM INFORMATION_SCHEMA.COLUMNS " + "WHERE TABLE_SCHEMA = SCHEMA() "
					+ "ORDER BY TABLE_NAME,ORDINAL_POSITION";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			columns = new ArrayList<ColumnsDTO>();
			while (rs.next()) {
				ColumnsDTO dto = new ColumnsDTO();
				dto.setCOLUMN_KEY(rs.getString("COLUMN_KEY"));
				dto.setCOLUMN_NAME(rs.getString("COLUMN_NAME"));
				dto.setDATA_TYPE(rs.getString("DATA_TYPE"));
				dto.setTABLE_NAME(rs.getString("TABLE_NAME"));
				columns.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
		return columns;
	}

	public ArrayList<ReferenceDTO> getReferencedList(String host, int port, String database, String username,
			String password) {
		ArrayList<ReferenceDTO> references = null;
		Connection dbConn = null;
		try {
			dbConn = dbConnManager.getConnection(host, port, database, username, password);
			String queryUse = "use " + database;
			PreparedStatement ps = dbConn.prepareStatement(queryUse);
			ResultSet rs = ps.executeQuery();
			String query = "SELECT TABLE_SCHEMA, TABLE_NAME, COLUMN_NAME, REFERENCED_TABLE_SCHEMA, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME "
					+ "FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE "
					+ "WHERE TABLE_SCHEMA = SCHEMA() AND REFERENCED_TABLE_NAME IS NOT NULL";
			ps = dbConn.prepareStatement(query);
			rs = ps.executeQuery();
			references = new ArrayList<ReferenceDTO>();
			while (rs.next()) {
				ReferenceDTO dto = new ReferenceDTO();
				dto.setColumnName(rs.getString("COLUMN_NAME"));
				dto.setReferencedColumnName(rs.getString("REFERENCED_COLUMN_NAME"));
				dto.setReferencedTableName(rs.getString("REFERENCED_TABLE_NAME"));
				dto.setReferencedTableSchema(rs.getString("REFERENCED_TABLE_SCHEMA"));
				dto.setTableName(rs.getString("TABLE_NAME"));
				dto.setTableSchema(rs.getString("TABLE_SCHEMA"));
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
	 * Get most frequently available values
	 * from the database as a descending order
	 * @param host
	 * @param port
	 * @param database
	 * @param username
	 * @param password
	 * @param table
	 * @param column
	 * @return
	 */
	public ArrayList<String> getMostFrequentValues(String host, int port, String database, String username,
			String password, String table, String column) {
		ArrayList<String> frequentObject = new ArrayList<String>();
		Connection dbConn = null;
		try {
			dbConn = dbConnManager.getConnection(host, port, database, username, password);
			String query = "SELECT " + column + " FROM " + table + " GROUP BY " + column + " ORDER BY COUNT(" + column
					+ ") desc";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				frequentObject.add(rs.getString(column));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
		return frequentObject;
	}

	/**
	 * Get distinct object count
	 * @param host
	 * @param port
	 * @param database
	 * @param username
	 * @param password
	 * @param table
	 * @param criteriaCol
	 * @param countedCol
	 * @param criteriaVal
	 * @return
	 */
	public int getDistinctObjCount(String host, int port, String database, String username, String password, String table, String criteriaCol,String countedCol,String criteriaVal){
		int countObject = 0;
		Connection dbConn = null;
		try {
			dbConn = dbConnManager.getConnection(host, port, database, username, password);
			String query = "SELECT COUNT(DISTINCT "+countedCol+") FROM "+table+" WHERE "+criteriaCol+"='"+criteriaVal+"'";
			PreparedStatement ps = dbConn.prepareStatement(query);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				countObject = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbConnManager.closeConnection(dbConn);
		}
		return countObject;
	}

}
