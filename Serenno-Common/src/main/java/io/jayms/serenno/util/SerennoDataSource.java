package io.jayms.serenno.util;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Collection;

public interface SerennoDataSource<T, K> {

	MongoCollection<Document> getCollection();

	T fromDocument(Document doc);

	Document toDocument(T val);

	Bson getFilter(K key);

	public void create(T value);
	
	public void update(T value);
	
	public T get(K key);
	
	public boolean exists(K key);
	
	public Collection<T> getAll();
	
	public void delete(T value);
	
	default void deleteAll() {
	}
	
}
