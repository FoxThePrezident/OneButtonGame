package com.FoxThePrezident.entities;

import com.FoxThePrezident.map.Collisions;
import com.FoxThePrezident.Data;
import com.FoxThePrezident.map.Graphics;
import com.FoxThePrezident.listeners.RefreshListener;

import javax.swing.*;

/**
 * MMovable entity, which trac the shortest path to a player
 */
public class Entity implements RefreshListener {
	protected int[] position;
	protected ImageIcon icon;
	protected int health = 10;
	protected int directionIndex = 0;
	protected final int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

	protected Graphics graphics = new Graphics();
	protected Collisions collisions = new Collisions();

	public Entity(int[] position) {
		this.position = position;
	}

	/**
	 * Looking to the future, what place I will occupy if I go there
	 *
	 * @return int pair of the next position
	 */
	protected int[] getNextPosition() {
		int y = position[0] + DIRECTIONS[directionIndex][0];
		int x = position[1] + DIRECTIONS[directionIndex][1];
		return new int[]{y, x};
	}

	/**
	 * Getting distance to a player from the next position
	 *
	 * @return double of that distance
	 */
	protected double getDistance() {
		int[] nextPosition = getNextPosition();
		int deltaY = Data.Player.position[0] - nextPosition[0];
		int deltaX = Data.Player.position[1] - nextPosition[1];

		// Use Pythagoras' theorem to calculate the distance
		return Math.sqrt(deltaY * deltaY + deltaX * deltaX);
	}

	@Override
	public void onRefresh() {
		// Checking, if entity got onto player
		if ((position[0] == Data.Player.position[0]) && (position[1] == Data.Player.position[1])) {
			int tempHealth = health;
			health -= Player.health;
			Player.getDamage(tempHealth);
		}

		// Checking, if it got enough health left
		if (health <= 0) {
			health = 0;
			return;
		}

		double distanceToPlayer = Double.MAX_VALUE;
		int direction = 0;

		// Looping over each direction
		for (directionIndex = 0; directionIndex <= 3; directionIndex++) {
			// Checking, if that is a valid place
			int[] nextPosition = getNextPosition();
			ImageIcon nextTile = graphics.getTile(nextPosition);
			int couldMove = collisions.checkForCollision(nextTile);
			if (couldMove == collisions.immovable) continue;

			// Getting distance and checking if it is closer to a player
			double distance = getDistance();
			if (distance < distanceToPlayer) {
				distanceToPlayer = distance;
				direction = directionIndex;
			}
		}
		// Storing that direction
		directionIndex = direction;
		position = getNextPosition();

		// Checking, if it landed again on a player
		if ((position[0] == Data.Player.position[0]) && (position[1] == Data.Player.position[1])) {
			int tempHealth = health;
			health -= Player.health;
			Player.getDamage(tempHealth);
			return;
		}

		// Drawing entity
		graphics.drawTile(position, icon, graphics.ENTITIES_LAYER);
	}
}
