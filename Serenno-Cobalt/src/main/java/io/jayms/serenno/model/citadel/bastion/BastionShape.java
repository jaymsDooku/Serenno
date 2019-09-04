package io.jayms.serenno.model.citadel.bastion;

import io.jayms.serenno.model.citadel.bastion.field.CircleFieldLogic;
import io.jayms.serenno.model.citadel.bastion.field.SquareFieldLogic;

public enum BastionShape {

	SQUARE, CIRCLE;

	public static BastionFieldLogic getFieldLogic(BastionShape shape, Bastion bastion) {
		switch (shape) {
			case CIRCLE:
				return new CircleFieldLogic(bastion);
			case SQUARE:
			default:
				return new SquareFieldLogic(bastion);
		}
	}
	
}
