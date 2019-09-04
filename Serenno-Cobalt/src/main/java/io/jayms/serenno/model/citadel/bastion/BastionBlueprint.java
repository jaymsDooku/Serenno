package io.jayms.serenno.model.citadel.bastion;

import org.bukkit.inventory.ItemStack;

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
