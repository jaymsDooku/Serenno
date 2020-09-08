package io.jayms.serenno.model.citadel.bastion;

import io.jayms.serenno.util.ItemUtil;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class BastionBlueprint {
	
	private String name;
	private String displayName;
	private ItemStack itemStack;
	private BastionShape shape;
	private int radius;
	private boolean requiresMaturity;
	private PearlConfig pearlConfig;
	
	public BastionBlueprint(BastionBlueprint.Builder builder) {
		this.name = builder.getName();
		this.displayName = builder.getDisplayName();
		this.itemStack = builder.getItemStack();
		this.shape = builder.getShape();
		this.radius = builder.getRadius();
		this.requiresMaturity = builder.isRequiresMaturity();
		this.pearlConfig = builder.getPearlConfig();
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public void setShape(BastionShape shape) {
		this.shape = shape;
	}
	
	public void setRadius(int radius) {
		this.radius = radius;
	}
	
	public void setRequiresMaturity(boolean requiresMaturity) {
		this.requiresMaturity = requiresMaturity;
	}
	
	public void setPearlBlock(boolean set) {
		this.pearlConfig.block = set;
	}
	
	public void setPearlBlockMidAir(boolean set) {
		this.pearlConfig.blockMidAir = set;
	}
	
	public void setPearlConsumeOnBlock(boolean set) {
		this.pearlConfig.consumeOnBlock = set;
	}
	
	public void setPearlRequiresMaturity(boolean set) {
		this.pearlConfig.requiresMaturity = set;
	}
	
	public void setPearlDamage(double damage) {
		this.pearlConfig.damage = damage;	
	}
	
	public String getName() {
		return name;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public ItemStack getItemStack() {
		return itemStack;
	}

	public BastionShape getShape() {
		return shape;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public boolean requiresMaturity() {
		return requiresMaturity;
	}
	
	public PearlConfig getPearlConfig() {
		return pearlConfig;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.YELLOW + "Name: " + ChatColor.GOLD + name + "\n");
		sb.append(ChatColor.YELLOW + "Display Name: " + ChatColor.GOLD + displayName + "\n");
		sb.append(ChatColor.YELLOW + "Item Stack: " + ChatColor.GOLD + "[" + ChatColor.YELLOW + itemStack.getType().toString() + ChatColor.GOLD + "|" + ChatColor.YELLOW + itemStack.getAmount() + ChatColor.GOLD + "|" + ChatColor.YELLOW + ItemUtil.getName(itemStack) + ChatColor.GOLD + "]\n");
		sb.append(ChatColor.YELLOW + "Radius: " + ChatColor.GOLD + radius + "\n");
		sb.append(ChatColor.YELLOW + "Requires Maturity: " + ChatColor.GOLD + requiresMaturity + "\n");
		sb.append(ChatColor.YELLOW + "Shape: " + ChatColor.GOLD + shape + "\n");
		sb.append(ChatColor.YELLOW + "Pearl Block: " + pearlConfig.block() + "\n");
		sb.append(ChatColor.YELLOW + "Pearl Block Mid Air: " + pearlConfig.block() + "\n");
		sb.append(ChatColor.YELLOW + "Pearl Consume On Block: " + pearlConfig.blockMidAir() + "\n");
		sb.append(ChatColor.YELLOW + "Pearl Requires Maturity: " + pearlConfig.consumeOnBlock() + "\n");
		sb.append(ChatColor.YELLOW + "Pearl Damage: " + pearlConfig.getDamage() + "\n");
		return sb.toString();
	}
	
	public static class PearlConfig {
		
		private boolean block;
		private boolean blockMidAir;
		private boolean consumeOnBlock;
		private boolean requiresMaturity;
		private double damage;
		
		private PearlConfig(PearlConfig.Builder builder) {
			this.block = builder.isBlock();
			this.blockMidAir = builder.isBlockMidAir();
			this.consumeOnBlock = builder.isConsumeOnBlock();
			this.requiresMaturity = builder.isRequiresMaturity();
			this.damage = builder.getDamage();
		}
		
		public boolean block() {
			return block;
		}
		
		public boolean blockMidAir() {
			return blockMidAir;
		}
		
		public boolean consumeOnBlock() {
			return consumeOnBlock;
		}
		
		public double getDamage() {
			return damage;
		}
		
		public boolean requiresMaturity() {
			return requiresMaturity;
		}
		
		public static Builder builder() {
			return new Builder();
		}
		
		public static class Builder {
			
			private boolean block;
			private boolean blockMidAir;
			private boolean consumeOnBlock;
			private boolean requiresMaturity;
			private double damage;
			
			private Builder() {
			}
			
			public Builder block(boolean block) {
				this.block = block;
				return this;
			}
			
			public boolean isBlock() {
				return block;
			}
			
			public Builder blockMidAir(boolean blockMidAir) {
				this.blockMidAir = blockMidAir;
				return this;
			}
			
			public boolean isBlockMidAir() {
				return blockMidAir;
			}
			
			public Builder consumeOnBlock(boolean consumeOnBlock) {
				this.consumeOnBlock = consumeOnBlock;
				return this;
			}
			
			public boolean isConsumeOnBlock() {
				return consumeOnBlock;
			}
			
			public Builder requiresMaturity(boolean requiresMaturity) {
				this.requiresMaturity = requiresMaturity;
				return this;
			}
			
			public boolean isRequiresMaturity() {
				return requiresMaturity;
			}
			
			public Builder damage(double damage) {
				this.damage = damage;
				return this;
			}
			
			public double getDamage() {
				return damage;
			}
			
			public PearlConfig build() {
				return new PearlConfig(this);
			}
		}
		
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private String name;
		private String displayName;
		private ItemStack itemStack;
		private BastionShape shape;
		private int radius;
		private boolean requiresMaturity;
		private PearlConfig pearlConfig;
		
		private Builder() {
		}
		
		public Builder name(String name) {
			this.name = name;
			return this;
		}
		
		public String getName() {
			return name;
		}
		
		public Builder displayName(String displayName) {
			this.displayName = displayName;
			return this;
		}
		
		public String getDisplayName() {
			return displayName;
		}
		
		public Builder itemStack(ItemStack itemStack) {
			this.itemStack = itemStack;
			return this;
		}
		
		public ItemStack getItemStack() {
			return itemStack;
		}
		
		public Builder shape(BastionShape shape) {
			this.shape = shape;
			return this;
		}
		
		public BastionShape getShape() {
			return shape;
		}
		
		public Builder radius(int radius) {
			this.radius = radius;
			return this;
		}
		
		public int getRadius() {
			return radius;
		}
		
		public Builder requiresMaturity(boolean requiresMaturity) {
			this.requiresMaturity = requiresMaturity;
			return this;
		}
		
		public boolean isRequiresMaturity() {
			return requiresMaturity;
		}
		
		public Builder pearlConfig(PearlConfig pearlConfig) {
			this.pearlConfig = pearlConfig;
			return this;
		}
		
		public PearlConfig getPearlConfig() {
			return pearlConfig;
		}
		
		public BastionBlueprint build() {
			return new BastionBlueprint(this);
		}
	}
	
}
