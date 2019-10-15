package io.jayms.serenno.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.Vector;

public final class LocationTools {
	
	public static final Set<BlockFace> PLANAR_SIDES = EnumSet.of(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST,
			BlockFace.EAST);

	public static boolean isBetween(Location l1, Location l2, Location p) {
		if (!(l1.getWorld().getUID().equals(l2.getWorld().getUID()))) {
			return false;
		}
		
		if (!(l1.getWorld().getUID().equals(p.getWorld().getUID()))) {
			return false;
		}
		
		int minX;
		int minY;
		int minZ;
		
		int maxX;
		int maxY;
		int maxZ;
		
		if (l1.getBlockX() > l2.getBlockX()) {
			minX = l2.getBlockX();
			maxX = l1.getBlockX();
		} else {
			minX = l1.getBlockX();
			maxX = l2.getBlockX();
		}
		
		if (l1.getBlockY() > l2.getBlockY()) {
			minY = l2.getBlockY();
			maxY = l1.getBlockY();
		} else {
			minY = l1.getBlockY();
			maxY = l2.getBlockY();
		}
		
		if (l1.getBlockZ() > l2.getBlockZ()) {
			minZ = l2.getBlockZ();
			maxZ = l1.getBlockZ();
		} else {
			minZ = l1.getBlockZ();
			maxZ = l2.getBlockZ();
		}
		
		int x = p.getBlockX();
		int y = p.getBlockY();
		int z = p.getBlockZ();
		
		return (minX <= x && minY <= y && minZ <= z) && (maxX >= x && maxY >= y && maxZ >= z);
	}
	
	public static Collection<LivingEntity> getNearbyLivingEntities(Location l, double r) {
		return l.getNearbyLivingEntities(r);
	}
	
	public static Location rotateAroundAxisX(Location loc, double angle) {
		Vector v = loc.toVector();
		Vector rotated = MathTools.rotateAroundAxisX(v, angle);
		return loc.set(rotated.getX(), rotated.getY(), rotated.getZ());
	}
	
	public static Location rotateAroundAxisZ(Location loc, double angle) {
		Vector v = loc.toVector();
		Vector rotated = MathTools.rotateAroundAxisZ(v, angle);
		return loc.set(rotated.getX(), rotated.getY(), rotated.getZ());
	}
	
	public static Location rotateAroundAxisY(Location loc, double angle) {
		Vector v = loc.toVector();
		Vector rotated = MathTools.rotateAroundAxisY(v, angle);
		return loc.set(rotated.getX(), rotated.getY(), rotated.getZ());
	}
	
	public static List<Location> getCircle(final Location loc, final int radius, final int height, final boolean hollow, final boolean sphere, final int plusY) {
		final List<Location> circleblocks = new ArrayList<Location>();
		final int cx = loc.getBlockX();
		final int cy = loc.getBlockY();
		final int cz = loc.getBlockZ();

		for (int x = cx - radius; x <= cx + radius; x++) {
			for (int z = cz - radius; z <= cz + radius; z++) {
				for (int y = (sphere ? cy - radius : cy); y < (sphere ? cy + radius : cy + height); y++) {
					final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);

					if (dist < radius * radius && !(hollow && dist < (radius - 1) * (radius - 1))) {
						final Location l = new Location(loc.getWorld(), x, y + plusY, z);
						circleblocks.add(l);
					}
				}
			}
		}
		return circleblocks;
	}
	
	public static World loadWorld(String name) {
		WorldCreator creator = new WorldCreator(name);
		creator.generator(new ChunkGenerator() {
		    @Override
		    public byte[] generate(World world, Random random, int x, int z) {
		        return new byte[32768]; //Empty byte array
		    }
		});
		creator.environment(Environment.NORMAL);
		creator.generateStructures(false);
		creator.type(WorldType.FLAT);
		World world = Bukkit.createWorld(creator);
		world.setAnimalSpawnLimit(0);
		world.setAmbientSpawnLimit(0);
		world.setMonsterSpawnLimit(0);
		return world;
	}
	
	public static void copyWorld(File source, File target){
	    try {
	        ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
	        if(!ignore.contains(source.getName())) {
	            if(source.isDirectory()) {
	                if(!target.exists())
	                target.mkdirs();
	                String files[] = source.list();
	                for (String file : files) {
	                    File srcFile = new File(source, file);
	                    File destFile = new File(target, file);
	                    copyWorld(srcFile, destFile);
	                }
	            } else {
	                InputStream in = new FileInputStream(source);
	                OutputStream out = new FileOutputStream(target);
	                byte[] buffer = new byte[1024];
	                int length;
	                while ((length = in.read(buffer)) > 0)
	                    out.write(buffer, 0, length);
	                in.close();
	                out.close();
	            }
	        }
	    } catch (IOException e) {
	 
	    }
	}
	
	public static boolean deleteWorld(File path) {
	      if(path.exists()) {
	          File files[] = path.listFiles();
	          for(int i=0; i<files.length; i++) {
	              if(files[i].isDirectory()) {
	                  deleteWorld(files[i]);
	              } else {
	                  files[i].delete();
	              }
	          }
	      }
	      return(path.delete());
	}
	
}
