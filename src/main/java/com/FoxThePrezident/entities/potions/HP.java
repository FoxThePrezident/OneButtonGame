package com.FoxThePrezident.entities.potions;

import com.FoxThePrezident.Debug;
import com.FoxThePrezident.entities.templates.Potion;
import com.FoxThePrezident.graphics.Icons;

/**
 * Health potion
 */
public class HP extends Potion {
	public HP(int[] position) {
		super(position);
		if (Debug.entities.potions.HP) System.out.println("--- [HP.constructor]");
		icon = Icons.Interactive.hp_potion;
		heal = 10;
	}
}
