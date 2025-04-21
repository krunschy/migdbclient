/**
 * 
 */
package org.migdb.migdbclient.models.dto;

/**
 * @author Kani
 *
 */
public class RelationshiCardinalityDTO {
	
	private String TABLE_NAME;
	private String COLUMN_NAME;
	private String REFERENCED_TABLE_NAME;
	private String REFERENCED_COLUMN_NAME;
	private String RELATIONSHIP_TYPE;
	
	public RelationshiCardinalityDTO() {
	}

	public RelationshiCardinalityDTO(String tABLE_NAME, String cOLUMN_NAME, String rEFERENCED_TABLE_NAME,
			String rEFERENCED_COLUMN_NAME, String rELATIONSHIP_TYPE) {
		super();
		TABLE_NAME = tABLE_NAME;
		COLUMN_NAME = cOLUMN_NAME;
		REFERENCED_TABLE_NAME = rEFERENCED_TABLE_NAME;
		REFERENCED_COLUMN_NAME = rEFERENCED_COLUMN_NAME;
		RELATIONSHIP_TYPE = rELATIONSHIP_TYPE;
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

	public String getREFERENCED_TABLE_NAME() {
		return REFERENCED_TABLE_NAME;
	}

	public void setREFERENCED_TABLE_NAME(String rEFERENCED_TABLE_NAME) {
		REFERENCED_TABLE_NAME = rEFERENCED_TABLE_NAME;
	}

	public String getREFERENCED_COLUMN_NAME() {
		return REFERENCED_COLUMN_NAME;
	}

	public void setREFERENCED_COLUMN_NAME(String rEFERENCED_COLUMN_NAME) {
		REFERENCED_COLUMN_NAME = rEFERENCED_COLUMN_NAME;
	}

	public String getRELATIONSHIP_TYPE() {
		return RELATIONSHIP_TYPE;
	}

	public void setRELATIONSHIP_TYPE(String rELATIONSHIP_TYPE) {
		RELATIONSHIP_TYPE = rELATIONSHIP_TYPE;
	}
}
