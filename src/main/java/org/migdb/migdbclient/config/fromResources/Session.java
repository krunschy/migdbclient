/**
 * 
 */
package org.migdb.migdbclient.config.fromResources;

import org.migdb.migdbclient.models.dto.ReferenceDTO;

/**
 * @author Kani
 *
 */
public enum Session {
	INSTANCE;

	private String activeDB;
	private ReferenceDTO manyToManyTables;

	public String getActiveDB() {
		return activeDB;
	}

	public void setActiveDB(String activeDB) {
		this.activeDB = activeDB;
	}

	public ReferenceDTO getManyToManyTables() {
		return manyToManyTables;
	}

	public void setManyToManyTables(ReferenceDTO manyToManyTables) {
		this.manyToManyTables = manyToManyTables;
	}

}
