package io.jayms.serenno.vault.data.mongodb;

import com.mongodb.client.MongoCollection;
import io.jayms.serenno.util.SerennoDataSource;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.checkerframework.checker.units.qual.K;

public interface MongoSerennoDataSource<T, K> extends SerennoDataSource<T, K> {

    MongoCollection<Document> getCollection();

    T fromDocument(Document doc);

    Document toDocument(T val);

    Bson getFilter(K key);

}
