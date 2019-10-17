package io.jayms.serenno.model.prebuilt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.manager.BastionManager;
import io.jayms.serenno.manager.CitadelManager;
import io.jayms.serenno.manager.ReinforcementManager;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.model.group.Group;
import io.jayms.serenno.util.Coords;
import io.jayms.serenno.util.gson.ItemStackDeserializer;
import io.jayms.serenno.util.gson.ItemStackSerializer;
import io.jayms.serenno.util.gson.MultimapAdapter;

public class PrebuiltStructure {

	public static PrebuiltStructure load(File loadFrom) {
		try (Reader reader = new FileReader(loadFrom)) {
			Gson gson = new GsonBuilder()
					.registerTypeAdapter(ItemStack.class, new ItemStackDeserializer())
					.registerTypeAdapter(Multimap.class, new MultimapAdapter())
					.create();
			PrebuiltStructure ps = gson.fromJson(reader, PrebuiltStructure.class);
			return ps;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void save(PrebuiltStructure ps, File toSave) {
		try (Writer writer = new FileWriter(toSave)) {
			Gson gson = new GsonBuilder()
					.registerTypeAdapter(ItemStack.class, new ItemStackSerializer())
					.registerTypeAdapter(Multimap.class, new MultimapAdapter())
					.setPrettyPrinting()
					.create();
			gson.toJson(ps, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String name;
	private Map<String, ReinforcementBlueprint> reinforcementBlueprints;
	private Map<String, BastionBlueprint> bastionBlueprints;
	private Multimap<String, PrebuiltBlock> prebuiltBlocks;
	
	private PrebuiltStructure(Builder builder) {
		this.name = builder.getName();
		this.reinforcementBlueprints = builder.getReinforcementBlueprints();
		this.bastionBlueprints = builder.getBastionBlueprints();
		this.prebuiltBlocks = builder.getPrebuiltBlocks();
	}
	
	public String getName() {
		return name;
	}
	
	public Map<String, ReinforcementBlueprint> getReinforcementBlueprints() {
		return reinforcementBlueprints;
	}
	
	public Map<String, BastionBlueprint> getBastionBlueprints() {
		return bastionBlueprints;
	}
	
	public Multimap<String, PrebuiltBlock> getPrebuiltBlocks() {
		return prebuiltBlocks;
	}
	
	public void load(Player loader, Group group, World world) {
		CitadelManager cm = SerennoCobalt.get().getCitadelManager();
		ReinforcementManager rm = cm.getReinforcementManager();
		BastionManager bm = cm.getBastionManager();
		
		Collection<PrebuiltBlock> reinforcements = prebuiltBlocks.get(PrebuiltBlockType.REINFORCEMENT.toString());
		for (PrebuiltBlock rein : reinforcements) {
			String blueprintType = rein.getBlueprintType();
			ReinforcementBlueprint blueprint = reinforcementBlueprints.get(blueprintType);
			Coords coords = rein.getCoords();
			Block block = world.getBlockAt(coords.getX(), coords.getY(), coords.getZ());
			rm.reinforceBlock(loader, block, null, blueprint, group);
		}
		
		Collection<PrebuiltBlock> bastions = prebuiltBlocks.get(PrebuiltBlockType.BASTION.toString());
		for (PrebuiltBlock bastion : bastions) {
			String blueprintType = bastion.getBlueprintType();
			BastionBlueprint blueprint = bastionBlueprints.get(blueprintType);
			Coords coords = bastion.getCoords();
			Block block = world.getBlockAt(coords.getX(), coords.getY(), coords.getZ());
			Reinforcement rein = rm.getReinforcement(block);
			if (rein != null) {
				bm.placeBastion(rein, blueprint);
			}
		}
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private String name;
		private Map<String, ReinforcementBlueprint> reinforcementBlueprints;
		private Map<String, BastionBlueprint> bastionBlueprints;
		private Multimap<String, PrebuiltBlock> prebuiltBlocks;
		
		private Builder() {
		}
		
		public Builder name(String name) {
			this.name = name;
			return this;
		}
		
		public String getName() {
			return name;
		}
		
		public Builder reinforcementBlueprints(Map<String, ReinforcementBlueprint> reinforcementBlueprints) {
			this.reinforcementBlueprints = reinforcementBlueprints;
			return this;
		}
		
		public Builder reinforcementBlueprint(String type, ReinforcementBlueprint reinforcement) {
			reinforcementBlueprints.put(type, reinforcement);
			return this;
		}
		
		public Map<String, ReinforcementBlueprint> getReinforcementBlueprints() {
			return reinforcementBlueprints;
		}
		
		public Builder bastionBlueprints(Map<String, BastionBlueprint> bastionBlueprints) {
			this.bastionBlueprints = bastionBlueprints;
			return this;
		}
		
		public Builder bastionBlueprint(String type, BastionBlueprint bastion) {
			bastionBlueprints.put(type, bastion);
			return this;
		}
		
		public Map<String, BastionBlueprint> getBastionBlueprints() {
			return bastionBlueprints;
		}
		
		public Builder prebuiltBlocks(Multimap<String, PrebuiltBlock> prebuiltBlocks) {
			this.prebuiltBlocks = prebuiltBlocks;
			return this;
		}
		
		public Builder prebuiltBlock(PrebuiltBlockType type, PrebuiltBlock block) {
			if (prebuiltBlocks == null) {
				prebuiltBlocks = HashMultimap.create();
			}
			prebuiltBlocks.put(type.toString(), block);
			return this;
		}
		
		public Builder prebuiltBlocks(PrebuiltBlockType type, Set<PrebuiltBlock> blocks) {
			if (prebuiltBlocks == null) {
				prebuiltBlocks = HashMultimap.create();
			}
			prebuiltBlocks.putAll(type.toString(), blocks);
			return this;
		}
		
		public Multimap<String, PrebuiltBlock> getPrebuiltBlocks() {
			return prebuiltBlocks;
		}
		
		public PrebuiltStructure build() {
			if (reinforcementBlueprints == null) {
				reinforcementBlueprints = new HashMap<>();
			}
			if (bastionBlueprints == null) {
				bastionBlueprints = new HashMap<>();
			}
			if (prebuiltBlocks == null) {
				prebuiltBlocks = HashMultimap.create();
			}
			return new PrebuiltStructure(this);
		}
 	}
	
}
