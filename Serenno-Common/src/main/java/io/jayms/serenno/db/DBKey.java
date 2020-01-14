package io.jayms.serenno.db;

import java.util.Objects;

public class DBKey {

    private String db;
    private String collection;

    public DBKey(String db, String collection) {
        this.db = db;
        this.collection = collection;
    }

    public String getDb() {
        return db;
    }

    public String getCollection() {
        return collection;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DBKey dbKey = (DBKey) o;
        return Objects.equals(db, dbKey.db) &&
                Objects.equals(collection, dbKey.collection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(db, collection);
    }
}
