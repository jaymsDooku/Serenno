package io.jayms.serenno.util;

public class Armour {

	private int toughness;
	private int armour;
	
	public Armour(int toughness, int armour) {
		this.toughness = toughness;
		this.armour = armour;
	}
	
	public int getToughness() {
		return toughness;
	}
	
	public int getArmour() {
		return armour;
	}

	@Override
	public String toString() {
		return "Armour [toughness=" + toughness + ", armour=" + armour + "]";
	}
	
}
