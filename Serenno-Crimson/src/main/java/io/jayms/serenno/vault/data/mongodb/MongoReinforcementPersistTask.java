package io.jayms.serenno.vault.data.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class MongoReinforcementPersistTask implements Runnable {

    private MongoVaultMapReinforcementDataSource dataSource;
    private MongoCollection<Document> collection;
    private ConcurrentLinkedQueue<Reinforcement> toSave;
    private ReinforcementWorld.UnloadCallback callback;
    private int total;
    private int bucketSize;
    private int soFar = 0;

    public MongoReinforcementPersistTask(MongoVaultMapReinforcementDataSource dataSource, Collection<Reinforcement> reinforcements, int saveSize, ReinforcementWorld.UnloadCallback callback) {
        this.dataSource = dataSource;
        this.collection = dataSource.getCollection();
        this.total = reinforcements.size();
        this.toSave = new ConcurrentLinkedQueue<>(reinforcements);
        this.bucketSize = saveSize;
        this.callback = callback;
    }

    @Override
    public void run() {
        List<Document> toInsert = new ArrayList<>();
        List<Reinforcement> toRemove = new ArrayList<>();
        int i = 0;
        while (!toSave.isEmpty()) {
            Reinforcement reinforcement = toSave.poll();
            soFar++;
            if (reinforcement == null) {
                System.out.println("broke loop");
                break;
            }
            if (reinforcement.isDirty()) {
                toRemove.add(reinforcement);
                if (!reinforcement.isBroken()) {
                    toInsert.add(dataSource.toDocument(reinforcement));
                    reinforcement.setInMemory(false);
                    reinforcement.setDirty(false);
                }
                i++;
            }

            if (i >= bucketSize || toSave.isEmpty()) {
                if (!toRemove.isEmpty()) {
                    Bson deleteFilter = Filters.all(MongoVaultMapReinforcementDataSource.REINFORCEMENT_ID, toRemove.stream()
                            .map(r -> r.getID().toString())
                            .collect(Collectors.toList()));
                    collection.deleteMany(deleteFilter);
                    toRemove.clear();
                }

                if (!toInsert.isEmpty()) {
                    collection.insertMany(toInsert);
                    toInsert.clear();
                }
                i = 0;
                SerennoCrimson.get().getLogger().info("Saved " + soFar + "/" + total + " (" + toSave.size() + " left) reinforcements for vault map: " + dataSource.getDb().getWorld().getName());
            }
        }
        new BukkitRunnable() {

            @Override
            public void run() {
                callback.unload();
            }
        }.runTask(SerennoCobalt.get());
    }

}
