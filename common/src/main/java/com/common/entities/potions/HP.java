package com.common.entities.potions;

import com.common.DataClasses.Position;
import com.common.Debug;
import com.common.entities.templates.Potion;
import com.common.graphics.Icons;

import static com.common.Debug.Levels.CORE;
import static com.common.Debug.debug;

/**
 * Health potion
 */
public class HP extends Potion {
	public HP(Position position) {
		super(position);
		debug(Debug.Flags.Entities.Potions.HP, CORE, "--- [HP.constructor]");
		icon = Icons.Interactive.hp_potion;
		heal = 10;
	}
}
