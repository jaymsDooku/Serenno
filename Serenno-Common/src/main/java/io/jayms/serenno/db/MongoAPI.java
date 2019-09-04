package io.jayms.serenno.db;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;

public class MongoAPI {

private static MongoClient mongoClient = null;
	
	public static void connect(String host, int port, String user, String pass, String db) {
		if (user != null && pass != null && db != null) {
			MongoCredential credential = MongoCredential.createCredential(user, db, pass.toCharArray());
			MongoClientOptions options = MongoClientOptions.builder().build();
			
			mongoClient = new MongoClient(new ServerAddress(host, port), credential, options);
		} else {
			mongoClient = new MongoClient(new ServerAddress(host, port));
		}
	}
	
	public static void close() {
		if (!isConnected()) {
			return;
		}
		
		mongoClient.close();
	}
	
	public static MongoCollection<Document> getCollection(String db, String collectionName) {
		return mongoClient.getDatabase(db).getCollection(collectionName);
	}
	
	public static boolean isConnected() {
		return mongoClient != null;
	}
}
