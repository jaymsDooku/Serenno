package io.jayms.serenno.manager;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.boydti.fawe.object.FawePlayer;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.model.prebuilt.PrebuiltBlock;
import io.jayms.serenno.model.prebuilt.PrebuiltBlockType;
import io.jayms.serenno.model.prebuilt.PrebuiltStructure;
import io.jayms.serenno.util.Coords;
import net.md_5.bungee.api.ChatColor;

public class PrebuiltManager {

	private ReinforcementManager rm;
	private BastionManager bm;
	private Map<String, PrebuiltStructure> pbStructures = Maps.newConcurrentMap();
	private File prebuiltStructureFolder;
	
	public PrebuiltManager(CitadelManager cm) {
		this.rm = cm.getReinforcementManager();
		this.bm = cm.getBastionManager();
		this.prebuiltStructureFolder = prebuiltStructureFolder();
	}
	
	private File prebuiltStructureFolder() {
		File psf = new File(SerennoCobalt.get().getDataFolder(), "prebuiltStructures");
		if (!psf.exists()) {
			psf.mkdirs();
		}
		return psf;
	}
	
	public File getPrebuiltStructureFolder() {
		return prebuiltStructureFolder;
	}
	
	public void save(Player player, String pbName) {
		FawePlayer<Player> fawePlayer = FawePlayer.wrap(player);
		com.sk89q.worldedit.regions.Region selection = fawePlayer.getSelection();
		Vector p1 = new Vector(selection.getMinimumPoint().getX(), selection.getMinimumPoint().getY(), selection.getMinimumPoint().getZ());
		Vector p2 = new Vector(selection.getMaximumPoint().getX(), selection.getMaximumPoint().getY(), selection.getMaximumPoint().getZ());
		
		World w = Bukkit.getWorld(selection.getWorld().getName());
		
		if (w == null) {
			player.sendMessage(ChatColor.RED + "Clipboard selection error.");
			return ;
		}
		
		Location l1 = new Location(w, p1.getX(), p1.getY(), p1.getZ());
		Location l2 = new Location(w, p2.getX(), p2.getY(), p2.getZ());
		
		Set<Reinforcement> reinforcements = rm.getReinforcementsInArea(l1, l2);
		Set<Bastion> bastions = bm.getBastionsInArea(l1, l2);
		
		System.out.println("reinforcements: " + reinforcements);
		System.out.println("bastions: " + bastions);
		
		Set<ReinforcementBlueprint> reinBlueprints = reinforcements.stream()
				.map(r -> r.getBlueprint())
				.collect(Collectors.toSet());
		Set<BastionBlueprint> bastionBlueprints = bastions.stream()
				.map(b -> b.getBlueprint())
				.collect(Collectors.toSet());
		
		System.out.println("reinforcement blueprints: " + reinBlueprints);
		System.out.println("bastion blueprints: " + bastionBlueprints);
		
		Map<String, ReinforcementBlueprint> reinBlueprintMap = reinBlueprints
				.stream()
				.collect(Collectors.toMap(ReinforcementBlueprint::getName, Functions.identity()));
		
		Map<String, BastionBlueprint> bastionBlueprintMap = bastionBlueprints
				.stream()
				.collect(Collectors.toMap(BastionBlueprint::getName, Functions.identity()));
		
		PrebuiltStructure.Builder psBuilder = PrebuiltStructure.builder()
				.name(pbName)
				.reinforcementBlueprints(reinBlueprintMap)
				.bastionBlueprints(bastionBlueprintMap);
		
		Set<PrebuiltBlock> pbReins = reinforcements.stream()
				.map(r -> new PrebuiltBlock(r.getBlueprint().getName(), Coords.fromLocation(r.getLocation()), PrebuiltBlockType.REINFORCEMENT))
				.collect(Collectors.toSet());
		Set<PrebuiltBlock> pbBastions = bastions.stream()
				.map(b -> new PrebuiltBlock(b.getBlueprint().getName(), Coords.fromLocation(b.getReinforcement(SerennoCobalt.get().getCitadelManager().getReinforcementManager().getReinforcementWorld(w)).getLocation()), PrebuiltBlockType.BASTION))
				.collect(Collectors.toSet());
		
		psBuilder.prebuiltBlocks(PrebuiltBlockType.REINFORCEMENT, pbReins);
		psBuilder.prebuiltBlocks(PrebuiltBlockType.BASTION, pbBastions);
		
		PrebuiltStructure ps = psBuilder.build();
		File toSave = new File(prebuiltStructureFolder, ps.getName() + ".json");
		if (!toSave.exists()) {
			try {
				toSave.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				SerennoCobalt.get().getLogger().warning("Failed to create file for prebuilt structure: " + ps.getName());
				return;
			}
		}
		PrebuiltStructure.save(ps, toSave);
		pbStructures.put(ps.getName(), ps);
		SerennoCobalt.get().getLogger().info("Saving prebuilt structure: " + ps.getName());
	}
	
	public PrebuiltStructure load(Player player, String pbName) {
		PrebuiltStructure result = pbStructures.get(pbName);
		if (result == null) {
			File file = new File(prebuiltStructureFolder, pbName + ".json");
			if (!file.exists()) {
				return null;
			}
			result = PrebuiltStructure.load(file);
			pbStructures.put(pbName, result);
		}
		return result;
	}
	
}
