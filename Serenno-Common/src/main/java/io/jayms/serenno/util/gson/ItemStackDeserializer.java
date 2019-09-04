package io.jayms.serenno.util.gson;

import java.io.IOException;
import java.lang.reflect.Type;

import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import io.jayms.serenno.kit.ItemStackDecoder;

public class ItemStackDeserializer implements JsonDeserializer<ItemStack> {

	@Override
	public ItemStack deserialize(JsonElement el, Type type, JsonDeserializationContext ctx)
			throws JsonParseException {
		try {
			ItemStack[] itemArr = ItemStackDecoder.itemStackArrayFromBase64(el.getAsJsonPrimitive().getAsString());
			return itemArr[0];
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
