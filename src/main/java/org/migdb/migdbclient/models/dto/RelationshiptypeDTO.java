/**
 * 
 */
package org.migdb.migdbclient.models.dto;

/**
 * @author Kani
 *
 */
public class RelationshiptypeDTO {

	private String COLUMN_KEY;
	private String COLUMN_NAME;
	private String TABLE_NAME;
	
	public RelationshiptypeDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RelationshiptypeDTO(String cOLUMN_KEY, String cOLUMN_NAME, String tABLE_NAME) {
		super();
		COLUMN_KEY = cOLUMN_KEY;
		COLUMN_NAME = cOLUMN_NAME;
		TABLE_NAME = tABLE_NAME;
	}

	public String getCOLUMN_KEY() {
		return COLUMN_KEY;
	}

	public void setCOLUMN_KEY(String cOLUMN_KEY) {
		COLUMN_KEY = cOLUMN_KEY;
	}

	public String getCOLUMN_NAME() {
		return COLUMN_NAME;
	}

	public void setCOLUMN_NAME(String cOLUMN_NAME) {
		COLUMN_NAME = cOLUMN_NAME;
	}

	public String getTABLE_NAME() {
		return TABLE_NAME;
	}

	public void setTABLE_NAME(String tABLE_NAME) {
		TABLE_NAME = tABLE_NAME;
	}
	
}
