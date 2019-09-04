package io.jayms.serenno.util.gson;

import java.lang.reflect.Type;

import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import io.jayms.serenno.kit.ItemStackEncoder;

public class ItemStackSerializer implements JsonSerializer<ItemStack> {

	@Override
	public JsonElement serialize(ItemStack item, Type type, JsonSerializationContext ctx) {
		ItemStack[] itemArr = new ItemStack[1];
		itemArr[0] = item;
		return new JsonPrimitive(ItemStackEncoder.itemStackArrayToBase64(itemArr));
	}
	
}
