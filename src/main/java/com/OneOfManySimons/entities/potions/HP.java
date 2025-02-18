package com.OneOfManySimons.entities.potions;

import com.OneOfManySimons.Debug;
import com.OneOfManySimons.entities.templates.Potion;
import com.OneOfManySimons.graphics.Icons;

import java.awt.*;

/**
 * Health potion
 */
public class HP extends Potion {
	public HP(Point position) {
		super(position);
		if (Debug.entities.potions.HP) System.out.println("--- [HP.constructor]");
		icon = Icons.Interactive.hp_potion;
		heal = 10;
	}
}
