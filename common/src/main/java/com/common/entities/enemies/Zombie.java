package com.common.entities.enemies;

import com.common.DataClasses.Position;
import com.common.entities.templates.Enemy;
import com.common.graphics.Icons;

import static com.common.Debug.Flags.Entities.Enemies.ZOMBIE;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.debug;

/**
 * Zombie entity
 */
public class Zombie extends Enemy {
	public Zombie(Position position) {
		super(position);
		debug(ZOMBIE, CORE, "--- [Zombie.constructor]");
		icon = Icons.Enemies.zombie;
		movementDelay = 2;
	}
}
