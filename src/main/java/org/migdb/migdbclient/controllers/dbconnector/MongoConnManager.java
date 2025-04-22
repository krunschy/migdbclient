package org.migdb.migdbclient.controllers.dbconnector;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import java.util.Collections;

public enum MongoConnManager {

	INSTANCE;

	private MongoClient client = null;

	public MongoClient connect(String host, int port) throws Exception {
		if (client == null) {
			MongoClientSettings settings = MongoClientSettings.builder()
					.applyToClusterSettings(builder ->
							builder.hosts(Collections.singletonList(new ServerAddress(host, port))))
					.build();
			client = MongoClients.create(settings);
		}

		return client;
	}


	public MongoDatabase connectToDatabase(String database) throws Exception {
		if (client == null) {
			throw new IllegalStateException("MongoClient is not connected.");
		}

		return client.getDatabase(database);
	}
}
