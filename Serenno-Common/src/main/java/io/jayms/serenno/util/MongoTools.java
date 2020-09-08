package io.jayms.serenno.util;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public final class MongoTools {

	public static Document toDocument(Vector vec) {
		Document document = new Document();
		document.append("x", vec.getX());
		document.append("y", vec.getY());
		document.append("z", vec.getZ());
		return document;
	}
	
	public static Vector vector(Document doc) {
		return new Vector(doc.getDouble("x"), doc.getDouble("y"), doc.getDouble("z"));
	}
	
	public static Document toDocument(Location loc) {
		Document document = new Document();
		World world = loc.getWorld();
		document.append("worldName", loc.getWorld() != null ? loc.getWorld().getName() : "null");
		document.append("x", loc.getX());
		document.append("y", loc.getY());
		document.append("z", loc.getZ());
		document.append("yaw", ((double)loc.getYaw()));
		document.append("pitch", ((double)loc.getPitch()));
		return document;
	}
	
	public static Location location(Document doc) {
		double dyaw = doc.getDouble("yaw");
		double dpitch = doc.getDouble("pitch");
		float yaw = (float) dyaw;
		float pitch = (float) dpitch;
		return new Location(Bukkit.getWorld(doc.getString("worldName")), doc.getDouble("x"), doc.getDouble("y"), doc.getDouble("z"), yaw, pitch);
	}
	
	public static Document toDocument(ItemStack it) {
		Document document = new Document();
		document.append("material", it.getType().toString());
		document.append("data", it.getData().getData());
		document.append("amount", it.getAmount());
		return document;
	}
	
	public static ItemStack itemstack(Document doc) {
		String materialStr = doc.getString("material");
		Material material = Material.valueOf(materialStr);
		int amount = doc.getInteger("amount");
		int iData = doc.getInteger("data");
		byte data = (byte) iData;
		ItemStack it = new ItemStack(material, amount);
		it.getData().setData(data);
		return it;
	}
	
}
