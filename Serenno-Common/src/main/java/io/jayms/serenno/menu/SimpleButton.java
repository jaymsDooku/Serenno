package io.jayms.serenno.menu;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.kit.ItemStackBuilder;

public class SimpleButton implements Button {

	private ClickHandler clickHandler;
	private ItemStack itemStack;
	private boolean pickUpAble;
	private boolean normal;
	private Menu menu;
	
	@Override
	public ClickHandler getClickHandler() {
		return clickHandler;
	}
	
	@Override
	public void setClickHandler(ClickHandler set) {
		this.clickHandler = set;
	}
	
	@Override
	public ItemStack getItemStack() {
		return this.itemStack;
	}
	
	@Override
	public void setItemStack(ItemStack it) {
		this.itemStack = it;
	}
	
	@Override
	public boolean isPickUpAble() {
		return this.pickUpAble;
	}
	
	@Override
	public void setPickUpAble(boolean set) {
		this.pickUpAble = set;
	}
	
	@Override
	public boolean isNormal() {
		return normal;
	}
	
	@Override
	public void setNormal(boolean normal) {
		this.normal = normal;
	}
	
	@Override
	public Menu getMenu() {
		return menu;
	}
	
	@Override
	public void setMenu(Menu menu) {
		this.menu = menu;
	}
	
	@Override
	public String toString() {
		return "SimpleButton [clickHandler=" + clickHandler + ", itemStack=" + itemStack + ", pickUpAble=" + pickUpAble + ", normal=" + normal
				+ ", menu=" + menu + "]";
	}

	public SimpleButton(Builder builder) {
		this.clickHandler = builder.clickHandler;
		this.itemStack = builder.itemStack;
		this.pickUpAble = builder.pickUpAble;
		this.menu = builder.menu;
		this.normal = builder.normal;
	}
	
	public static class Builder {
		
		private ClickHandler clickHandler = null;
		private ItemStack itemStack = new ItemStackBuilder(Material.STAINED_GLASS_PANE, 1).durability((short) 7).build();
		private boolean pickUpAble = false;
		private boolean normal = false;
		private Menu menu;
		
		public Builder(Menu menu) {
			this.menu = menu;
		}
		
		public Builder setClickHandler(ClickHandler set) {
			this.clickHandler = set;
			return this;
		}
		
		public Builder setItemStack(ItemStack set) {
			this.itemStack = set;
			return this;
		}
		
		public Builder setPickUpAble(boolean set) {
			this.pickUpAble = set;
			return this;
		}
		
		public Builder setNormal(boolean set) {
			this.normal = set;
			return this;
		}
		
		public SimpleButton build() {
			return new SimpleButton(this);
		}
	}

}
