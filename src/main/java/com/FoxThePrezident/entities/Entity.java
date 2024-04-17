package com.FoxThePrezident.entities;

import com.FoxThePrezident.common.Collisions;
import com.FoxThePrezident.graphics.Graphics;
import com.FoxThePrezident.graphics.Icons;
import com.FoxThePrezident.graphics.RefreshListener;

import javax.swing.*;

public class Entity implements RefreshListener {
	protected int[] position;
	protected int health = 10;
	protected int directionIndex = 0;
	protected final int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

	protected Graphics graphics = new Graphics();
	protected Collisions collisions = new Collisions();

	public Entity(int[] position) {
		this.position = position;
	}

	protected int[] getNextPosition() {
		int y = position[0] + DIRECTIONS[directionIndex][0];
		int x = position[1] + DIRECTIONS[directionIndex][1];
		return new int[]{y, x};
	}

	protected double getDistance() {
		int[] nextPosition = getNextPosition();
		int deltaY = Player.position[0] - nextPosition[0];
		int deltaX = Player.position[1] - nextPosition[1];

		// Use Pythagoras' theorem to calculate the distance
		return Math.sqrt(deltaY * deltaY + deltaX * deltaX);
	}

	@Override
	public void onRefresh() {
		if ((position[0] == Player.position[0]) && (position[1] == Player.position[1])) {
			int tempHealth = health;
			health -= Player.health;
			Player.getDamage(tempHealth);
		}

		if (health <= 0) {
			health = 0;
			return;
		}

		double distanceToPlayer = Double.MAX_VALUE;
		int direction = 0;

		for (directionIndex = 0; directionIndex <= 3; directionIndex++) {
			int[] nextPosition = getNextPosition();
			ImageIcon nextTile = graphics.getTile(nextPosition[0], nextPosition[1]);
			int couldMove = collisions.checkForCollision(nextTile);
			if (couldMove == collisions.immovable) continue;

			double distance = getDistance();
			if (distance < distanceToPlayer) {
				distanceToPlayer = distance;
				direction = directionIndex;
			}
		}
		directionIndex = direction;
		position = getNextPosition();

		if ((position[0] == Player.position[0]) && (position[1] == Player.position[1])) {
			int tempHealth = health;
			health -= Player.health;
			Player.getDamage(tempHealth);
			return;
		}

		graphics.drawTile(position[0], position[1], Icons.Enemies.zombie, graphics.ENTITIES_LAYER);
	}
}
