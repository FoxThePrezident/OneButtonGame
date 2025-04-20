package com.common.entities.enemies;

import com.common.DataClasses.Position;
import com.common.entities.templates.Enemy;
import com.common.graphics.Icons;

import static com.common.Debug.Flags.Entities.Enemies.SKELETON;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.debug;

public class Skeleton extends Enemy {
	public Skeleton(Position position) {
		super(position);

		debug(SKELETON, CORE, "--- [Zombie.constructor]");

		detectionRange = 5;
		keepDistance = 3;
		movementDelay = 3;

		couldFireProjectile = true;
		projectileDelay = 5;

		icon = Icons.Enemies.skeleton;
		health = 5;
	}
}
