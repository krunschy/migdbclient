package org.migdb.migdbclient.models.modificationevaluator;

public class ForeignKeyReference {
	
	String referencingTab;
	String referencingCol;
	String referencedTab;
	String referencedCol;
	String relationshipType;
	
	public ForeignKeyReference(String referencingTab, String referencingCol, String referencedTab, String referencedCol,
			String relationshipType) {
		super();
		this.referencingTab = referencingTab;
		this.referencingCol = referencingCol;
		this.referencedTab = referencedTab;
		this.referencedCol = referencedCol;
		this.relationshipType = relationshipType;
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

	public String getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(String relationshipType) {
		this.relationshipType = relationshipType;
	}

}
