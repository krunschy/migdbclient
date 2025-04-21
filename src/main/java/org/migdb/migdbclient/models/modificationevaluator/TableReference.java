package org.migdb.migdbclient.models.modificationevaluator;

public class TableReference {
	
	String referencedTab;
	String referencedCol;
	String referencingTab;
	String referencingCol;
	String relationshipType;
	
	public TableReference(String referencedTab, String referencedCol, String referencingTab, String referencingCol,
			String relationshipType) {
		super();
		this.referencedTab = referencedTab;
		this.referencedCol = referencedCol;
		this.referencingTab = referencingTab;
		this.referencingCol = referencingCol;
		this.relationshipType = relationshipType;
	}

	public String getReferencedTab() {
		return referencedTab;
	}

	public void setReferencedTab(String referencedTab) {
		this.referencedTab = referencedTab;
	}

	public String getReferencedCol() {
		return referencedCol;
	}

	public void setReferencedCol(String referencedCol) {
		this.referencedCol = referencedCol;
	}

	public String getReferencingTab() {
		return referencingTab;
	}

	public void setReferencingTab(String referencingTab) {
		this.referencingTab = referencingTab;
	}

	public String getReferencingCol() {
		return referencingCol;
	}

	public void setReferencingCol(String referencingCol) {
		this.referencingCol = referencingCol;
	}

	public String getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(String relationshipType) {
		this.relationshipType = relationshipType;
	}
	
		
}
