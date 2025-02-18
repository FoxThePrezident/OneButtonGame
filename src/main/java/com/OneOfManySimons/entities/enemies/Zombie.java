package com.OneOfManySimons.entities.enemies;

import com.OneOfManySimons.Debug;
import com.OneOfManySimons.entities.templates.Enemy;
import com.OneOfManySimons.graphics.Icons;

import java.awt.*;

/**
 * Zombie entity
 */
public class Zombie extends Enemy {
	public Zombie(Point position) {
		super(position);
		if (Debug.entities.enemies.Zombie) System.out.println("--- [Zombie.constructor]");
		icon = Icons.Enemies.zombie;
		movementDelay = 2;
	}
}
