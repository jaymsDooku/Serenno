package io.jayms.serenno.db;

import com.mongodb.client.*;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.util.Collection;
import java.util.Iterator;

public class MongoAPI {

private static MongoClient mongoClient = null;
	
	public static void connect(String host, int port, String user, String pass, String db) {
		if (user != null && pass != null && db != null) {
			MongoCredential credential = MongoCredential.createCredential(user, db, pass.toCharArray());
			MongoClientOptions options = MongoClientOptions.builder()
					.connectionsPerHost(1000)
					.build();

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

	public static boolean databaseExists(String db) {
		MongoIterable<String> dbNames = mongoClient.listDatabaseNames();
		Iterator<String> it = dbNames.iterator();
		while (it.hasNext()) {
			String dbName = it.next();
			if (dbName.equals(db)) {
				return true;
			}
		}
		return false;
	}

	public static MongoDatabase getDatabase(String db) {
		return mongoClient.getDatabase(db);
	}
	
	public static MongoCollection<Document> getCollection(String db, String collectionName) {
		return mongoClient.getDatabase(db).getCollection(collectionName);
	}

	public static void copy(MongoCollection<Document> source, MongoCollection<Document> dest) {
		FindIterable<Document> info = source.find();
		MongoCursor<Document> it = info.iterator();
		while (it.hasNext()) {
			dest.insertOne(it.next());
		}
	}
	
	public static boolean isConnected() {
		return mongoClient != null;
	}
}
