package com.FoxThePrezident.entities;

import com.FoxThePrezident.map.Icons;

public class Zombie extends Entity {
	public Zombie(int[] position) {
		super(position);
		icon = Icons.Enemies.zombie;
	}
}
