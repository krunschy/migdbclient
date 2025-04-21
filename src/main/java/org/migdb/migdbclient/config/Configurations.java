/**
 * 
 */
package org.migdb.migdbclient.config;

import java.io.File;

import org.migdb.migdbclient.models.dao.SqliteDAO;

/**
 * @author KANI
 *
 */
public class Configurations {
	
	SqliteDAO dao = new SqliteDAO();

	public void createAppFolder() {
		try {
			// Create application folder in a user's document
			File migDB = new File(FilePath.DOCUMENT.getPath());
			if (!migDB.exists()) {
				migDB.mkdir();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createConnectionTable() {
		try {
			dao.createTable();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void insertOperators() {
		try {
			if(dao.getQueryOperators().size() == 0){
				dao.insertOperators();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
