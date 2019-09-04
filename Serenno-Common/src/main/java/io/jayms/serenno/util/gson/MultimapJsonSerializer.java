package io.jayms.serenno.util.gson;

import java.lang.reflect.Type;

import com.google.common.collect.Multimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class MultimapJsonSerializer<K, V> implements JsonSerializer<Multimap<K, V>> {

	@Override
	public JsonElement serialize(Multimap<K, V> map, Type type, JsonSerializationContext ctx) {
		return ctx.serialize(map.asMap());
	}
	
}
