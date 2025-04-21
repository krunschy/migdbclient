/**
 * 
 */
package org.migdb.migdbclient.models.dto;

import java.util.ArrayList;

/**
 * @author KANI
 *
 */
public class ManyToManyValidator {
	
	private String tableName;
	private ArrayList<String> columns;
	private ArrayList<String> tables;
	private boolean isValid = false;
	
	public ManyToManyValidator() {
		super();
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public ArrayList<String> getColumns() {
		return columns;
	}

	public void setColumns(ArrayList<String> columns) {
		this.columns = columns;
	}

	public ArrayList<String> getTables() {
		return tables;
	}

	public void setTables(ArrayList<String> tables) {
		this.tables = tables;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}

}
