package io.jayms.serenno.util;

import java.util.Set;

public interface SerennoDataSource<T, K> {

	public void create(T value);
	
	public void update(T value);
	
	public T get(K key);
	
	public Set<T> getAll();
	
	public void delete(T value);
	
}
