package org.migdb.migdbclient.config;

import java.util.Arrays;
import java.util.List;

public enum DataTypes {

	NUMBERTYPES(Arrays.asList("tinyint","smallint","mediumint","int","bigint","float"
			,"double","decimal")),	
	TEXTTYPES(Arrays.asList("char","varchar","tinytext","text","blob","mediumtext"
			 ,"mediumblob","longtext","longblob","enum","set")),
	DATETYPES(Arrays.asList("date","datetime","timestamp","time","year"));
	
	List<String> types;

	DataTypes(List<String> types) {
		this.types = types;
	}

	public List<String> getTypes() {
		return types;
	}

	
}
