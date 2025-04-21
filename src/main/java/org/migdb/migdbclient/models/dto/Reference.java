package org.migdb.migdbclient.models.dto;

public class Reference {
	
	int oneSideTbl;
	int oneSideCol;
	int manySideTbl;
	int manySideCol;
	
	public Reference(int oneSideTbl, int oneSideCol, int manySideTbl, int manySideCol) {
		super();
		this.oneSideTbl = oneSideTbl;
		this.oneSideCol = oneSideCol;
		this.manySideTbl = manySideTbl;
		this.manySideCol = manySideCol;
	}

	public int getOneSideTbl() {
		return oneSideTbl;
	}

	public void setOneSideTbl(int oneSideTbl) {
		this.oneSideTbl = oneSideTbl;
	}

	public int getOneSideCol() {
		return oneSideCol;
	}

	public void setOneSideCol(int oneSideCol) {
		this.oneSideCol = oneSideCol;
	}

	public int getManySideTbl() {
		return manySideTbl;
	}

	public void setManySideTbl(int manySideTbl) {
		this.manySideTbl = manySideTbl;
	}

	public int getManySideCol() {
		return manySideCol;
	}

	public void setManySideCol(int manySideCol) {
		this.manySideCol = manySideCol;
	}

}
