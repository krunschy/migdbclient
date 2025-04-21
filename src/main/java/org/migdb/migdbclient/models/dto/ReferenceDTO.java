/**
 * 
 */
package org.migdb.migdbclient.models.dto;

/**
 * @author Kani
 *
 */
public class ReferenceDTO {
	
	private String tableSchema;
	private String tableName;
	private String columnName;
	private String referencedTableSchema;
	private String referencedTableName;
	private String referencedColumnName;
	
	public ReferenceDTO() {
		super();
	}

	public ReferenceDTO(String tableSchema, String tableName, String columnName, String referencedTableSchema,
			String referencedTableName, String referencedColumnName) {
		super();
		this.tableSchema = tableSchema;
		this.tableName = tableName;
		this.columnName = columnName;
		this.referencedTableSchema = referencedTableSchema;
		this.referencedTableName = referencedTableName;
		this.referencedColumnName = referencedColumnName;
	}

	public String getTableSchema() {
		return tableSchema;
	}

	public void setTableSchema(String tableSchema) {
		this.tableSchema = tableSchema;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getReferencedTableSchema() {
		return referencedTableSchema;
	}

	public void setReferencedTableSchema(String referencedTableSchema) {
		this.referencedTableSchema = referencedTableSchema;
	}

	public String getReferencedTableName() {
		return referencedTableName;
	}

	public void setReferencedTableName(String referencedTableName) {
		this.referencedTableName = referencedTableName;
	}

	public String getReferencedColumnName() {
		return referencedColumnName;
	}

	public void setReferencedColumnName(String referencedColumnName) {
		this.referencedColumnName = referencedColumnName;
	}

}
