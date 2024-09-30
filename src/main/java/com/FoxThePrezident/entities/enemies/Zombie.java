package com.FoxThePrezident.entities.enemies;

import com.FoxThePrezident.Debug;
import com.FoxThePrezident.entities.Enemy;
import com.FoxThePrezident.map.Icons;

/**
 * Zombie entity
 */
public class Zombie extends Enemy {
	public Zombie(int[] position) {
		super(position);
		if (Debug.entities.Zombie) System.out.println("--- [Zombie.constructor]");
		icon = Icons.Enemies.zombie;
		movementDelay = 2;
	}
}
