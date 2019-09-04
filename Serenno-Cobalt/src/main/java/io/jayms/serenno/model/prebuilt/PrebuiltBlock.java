package io.jayms.serenno.model.prebuilt;

import io.jayms.serenno.util.Coords;

public class PrebuiltBlock {

	private String blueprintType;
	
	private Coords coords;
	private PrebuiltBlockType blockType;
	
	public PrebuiltBlock(String blueprintType, Coords coords, PrebuiltBlockType blockType) {
		this.blueprintType = blueprintType;
		this.coords = coords;
		this.blockType = blockType;
	}
	
	public PrebuiltBlockType getBlockType() {
		return blockType;
	}
	
	public String getBlueprintType() {
		return blueprintType;
	}
	
	public Coords getCoords() {
		return coords;
	}
	
}
