package io.jayms.serenno.menu;

import org.bukkit.inventory.ItemStack;

public interface Button {
	
	void setClickHandler(ClickHandler set);
	
	ClickHandler getClickHandler();
	
	void setItemStack(ItemStack it);
	
	ItemStack getItemStack();
	
	void setPickUpAble(boolean set);
	
	boolean isPickUpAble();
	
	void setMenu(Menu menu);
	
	Menu getMenu();
	
}
