package io.jayms.serenno.util;

import java.util.Collection;

public interface SerennoDataSource<T, K> {

	public void create(T value);
	
	public void update(T value);
	
	public T get(K key);
	
	public boolean exists(K key);
	
	public Collection<T> getAll();
	
	public void delete(T value);
	
}
