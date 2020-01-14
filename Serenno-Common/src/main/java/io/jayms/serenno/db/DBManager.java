package io.jayms.serenno.db;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.mongodb.client.MongoCollection;

import io.jayms.serenno.SerennoCommon;
import io.jayms.serenno.db.event.DBConnectEvent;

public class DBManager {
	
	private Map<DBKey, MongoCollection<Document>> collections = new ConcurrentHashMap<>();
	private String db;

	public MongoCollection<Document> getCollection(String name) {
	    return getCollection(new DBKey(db, name), (c) -> {});
    }

    public MongoCollection<Document> getCollection(DBKey key) {
	    return getCollection(key, (c) -> {});
    }

	public MongoCollection<Document> getCollection(DBKey key, Consumer<MongoCollection<Document>> init) {
		if (!MongoAPI.isConnected()) {
			return null;
		}
		
		MongoCollection<Document> collection = collections.get(key);
		if (collection == null) {
			collection = MongoAPI.getCollection(key.getDb(), key.getCollection());
			collections.put(key, collection);
		}
		return collection;
	}
	
	public DBManager() { 
	}
	
	/**
     * Connects to the MongoDB instance, every plugin that needs to do so waits for this method to be ran
     */
    public void establishConnection() {
        if(MongoAPI.isConnected())
            return;

        boolean creds = SerennoCommon.get().getConfigManager().isCreds();
        String host = SerennoCommon.get().getConfigManager().getHost();
        int port = SerennoCommon.get().getConfigManager().getPort();
        String user = SerennoCommon.get().getConfigManager().getHost();
        String pass = SerennoCommon.get().getConfigManager().getPass();
        db = SerennoCommon.get().getConfigManager().getDb();
        
        new BukkitRunnable()
        {
            public void run()
            {
                if(creds) {
                    MongoAPI.connect(
                    		host,
                            port,
                            user,
                            pass,
                            db
                    );
                }

                else {
                    MongoAPI.connect(
                    		host,
                    		port,
                            null, null, null
                    );
                }
                
                DBConnectEvent connEvent = new DBConnectEvent(MongoAPI.isConnected());
                Bukkit.getPluginManager().callEvent(connEvent);
            }
        }.runTaskAsynchronously(SerennoCommon.get());
    }
    
}
