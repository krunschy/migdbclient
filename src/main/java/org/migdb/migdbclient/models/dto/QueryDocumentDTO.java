/**
 * 
 */
package org.migdb.migdbclient.models.dto;

/**
 * @author HP
 *
 */
public class QueryDocumentDTO {
	
	private String field;
	private String operators;
	private String values;
	private String condition;
	
	public QueryDocumentDTO() {
		super();
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getOperators() {
		return operators;
	}

	public void setOperators(String operators) {
		this.operators = operators;
	}

	public String getValues() {
		return values;
	}

	public void setValues(String values) {
		this.values = values;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

}
