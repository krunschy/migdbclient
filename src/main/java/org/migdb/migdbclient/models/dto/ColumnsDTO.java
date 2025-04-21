/**
 * 
 */
package org.migdb.migdbclient.models.dto;

/**
 * @author Kani
 *
 */
public class ColumnsDTO {
	
	private String TABLE_NAME;
	private String COLUMN_NAME;
	private String DATA_TYPE;
	private String COLUMN_KEY;
	private int DATA_TYPE_COUNT;
	
	public ColumnsDTO(){
		
	}

	public ColumnsDTO(String tABLE_NAME, String cOLUMN_NAME, String dATA_TYPE, String cOLUMN_KEY) {
		super();
		TABLE_NAME = tABLE_NAME;
		COLUMN_NAME = cOLUMN_NAME;
		DATA_TYPE = dATA_TYPE;
		COLUMN_KEY = cOLUMN_KEY;
	}

	public String getTABLE_NAME() {
		return TABLE_NAME;
	}

	public void setTABLE_NAME(String tABLE_NAME) {
		TABLE_NAME = tABLE_NAME;
	}

	public String getCOLUMN_NAME() {
		return COLUMN_NAME;
	}

	public void setCOLUMN_NAME(String cOLUMN_NAME) {
		COLUMN_NAME = cOLUMN_NAME;
	}

	public String getDATA_TYPE() {
		return DATA_TYPE;
	}

	public void setDATA_TYPE(String dATA_TYPE) {
		DATA_TYPE = dATA_TYPE;
	}

	public String getCOLUMN_KEY() {
		return COLUMN_KEY;
	}

	public void setCOLUMN_KEY(String cOLUMN_KEY) {
		COLUMN_KEY = cOLUMN_KEY;
	}

	public int getDATA_TYPE_COUNT() {
		return DATA_TYPE_COUNT;
	}

	public void setDATA_TYPE_COUNT(int dATA_TYPE_COUNT) {
		DATA_TYPE_COUNT = dATA_TYPE_COUNT;
	}

}
