package org.migdb.migdbclient.config;

public enum TreeviewSize {

	TREEVIEWHEIGHT(200),
	TREEVIEWIDTH(155),
	XSTART(10),
	YSTART(10),
	XSPACE(40),
	YSPACE(20);
	
	private double size;
	
	TreeviewSize(double size) {
		this.size = size;
	}
	
	public double getSize() {
		return size;
	}
}
