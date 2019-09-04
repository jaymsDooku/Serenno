package io.jayms.serenno.util.gson;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class MultimapJsonDeserializer<K, V> implements JsonDeserializer<Multimap<K, V>> {

	@SuppressWarnings("unchecked")
	@Override
	public Multimap<K, V> deserialize(JsonElement el, Type type, JsonDeserializationContext ctx)
			throws JsonParseException {
		Map map = ctx.deserialize(el.getAsJsonObject(), Map.class);
		Map<K, Collection<V>> mapCollection = (Map<K, Collection<V>>) map;
		Multimap<K, V> result = HashMultimap.create();
		for (Entry<K, Collection<V>> en : mapCollection.entrySet()) {
			result.putAll(en.getKey(), en.getValue());
		}
		return result;
	}
}
	
