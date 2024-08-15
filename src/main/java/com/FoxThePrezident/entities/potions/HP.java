package com.FoxThePrezident.entities.potions;

import com.FoxThePrezident.entities.Potion;
import com.FoxThePrezident.map.Icons;

public class HP extends Potion {
	public HP(int[] position) {
		super(position);
		icon = Icons.Interactive.hp_potion;

		heal = 10;
	}
}
