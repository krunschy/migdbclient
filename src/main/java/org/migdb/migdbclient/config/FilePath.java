/**
 * 
 */
package org.migdb.migdbclient.config;

import java.io.File;

/**
 * @author Kani
 *
 */
public enum FilePath {

	DOCUMENT(System.getProperty("user.home") + File.separator + "Documents"+ File.separator + "MigDB" + File.separator),
	XMLPATH("Xmldump.xml"),
	DBSTRUCTUREFILE("Database structure.json"),
	DELETEDITEMFILE("Item deleted.json"),
	COLLECTIONFILE("Collection.json"),
	CHANGESTRUCTURE("ChangeStructure.json");

	private String path;

	FilePath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
