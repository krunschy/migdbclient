/**
 * 
 */
package org.migdb.migdbclient.models.dto;

/**
 * @author Kani
 *
 */
public class TableDTO {
	
	private String tableName;

	public TableDTO() {
	}

	public TableDTO(String tableName) {
		super();
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
