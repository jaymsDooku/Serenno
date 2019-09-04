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
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.util.gson.ItemStackDeserializer;
import io.jayms.serenno.util.gson.ItemStackSerializer;
import io.jayms.serenno.util.gson.MultimapJsonDeserializer;
import io.jayms.serenno.util.gson.MultimapJsonSerializer;

public class PrebuiltStructure {

	public static PrebuiltStructure load(File loadFrom) {
		try (Reader reader = new FileReader(loadFrom)) {
			Gson gson = new GsonBuilder()
					.registerTypeAdapter(ItemStack.class, new ItemStackDeserializer())
					.registerTypeAdapter(Multimap.class, new MultimapJsonDeserializer<>())
					.create();
			return gson.fromJson(reader, PrebuiltStructure.class);
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
					.registerTypeAdapter(Multimap.class, new MultimapJsonSerializer<>())
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
	private Multimap<PrebuiltBlockType, PrebuiltBlock> prebuiltBlocks;
	
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
	
	public Multimap<PrebuiltBlockType, PrebuiltBlock> getPrebuiltBlocks() {
		return prebuiltBlocks;
	}
	
	public void load(World world) {
		CitadelManager cm = SerennoCobalt.get().getCitadelManager();
		ReinforcementManager rm = cm.getReinforcementManager();
		BastionManager bm = cm.getBastionManager();
		
		Map<PrebuiltBlockType, Collection<PrebuiltBlock>> pbMap = prebuiltBlocks.asMap();
		Collection<PrebuiltBlock> reinforcements = pbMap.get(PrebuiltBlockType.REINFORCEMENT);
		for (PrebuiltBlock rein : reinforcements) {
			
		}
		
		Collection<PrebuiltBlock> bastions = pbMap.get(PrebuiltBlockType.BASTION);
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private String name;
		private Map<String, ReinforcementBlueprint> reinforcementBlueprints;
		private Map<String, BastionBlueprint> bastionBlueprints;
		private Multimap<PrebuiltBlockType, PrebuiltBlock> prebuiltBlocks;
		
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
		
		public Builder prebuiltBlocks(Multimap<PrebuiltBlockType, PrebuiltBlock> prebuiltBlocks) {
			this.prebuiltBlocks = prebuiltBlocks;
			return this;
		}
		
		public Builder prebuiltBlock(PrebuiltBlockType type, PrebuiltBlock block) {
			if (prebuiltBlocks == null) {
				prebuiltBlocks = HashMultimap.create();
			}
			prebuiltBlocks.put(type, block);
			return this;
		}
		
		public Builder prebuiltBlocks(PrebuiltBlockType type, Set<PrebuiltBlock> blocks) {
			if (prebuiltBlocks == null) {
				prebuiltBlocks = HashMultimap.create();
			}
			prebuiltBlocks.putAll(type, blocks);
			return this;
		}
		
		public Multimap<PrebuiltBlockType, PrebuiltBlock> getPrebuiltBlocks() {
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
