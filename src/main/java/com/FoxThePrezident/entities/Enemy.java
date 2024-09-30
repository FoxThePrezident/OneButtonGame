package com.FoxThePrezident.entities;

import com.FoxThePrezident.Debug;
import com.FoxThePrezident.listeners.Listeners;
import com.FoxThePrezident.map.Collisions;
import com.FoxThePrezident.Data;
import com.FoxThePrezident.map.Graphics;
import com.FoxThePrezident.listeners.RefreshListener;

import javax.swing.*;

/**
 * Movable entity.<br>
 * Track the shortest path to the player
 */
public class Enemy implements RefreshListener {
	protected int[] position;
	protected ImageIcon icon;
	protected int health = 10;

	/**
	 * Tell how often enemy will move.
	 * For example move once and skip twice
	 * It is max threshold
	 */
	protected int movementDelay = 1;
	/**
	 * Tell current move since enemy moved
	 */
	protected int movementNumber = 0;

	// Movement
	protected final int detectionRange = 3;
	protected int directionIndex = 0;
	protected final int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

	protected final Graphics graphics = new Graphics();
	protected final Listeners listeners = new Listeners();
	protected final Collisions collisions = new Collisions();

	public Enemy(int[] position) {
		if (Debug.entities.Enemy) System.out.println("--- [Entity.constructor]");
		this.position = position;
	}

	/**
	 * Looking to the future, what place I will occupy if I go there.
	 *
	 * @return int pair of the next position
	 */
	protected int[] getNextPosition() {
		if (Debug.entities.Enemy) System.out.println("--- [Entity.getNextPosition]");
		int y = position[0] + DIRECTIONS[directionIndex][0];
		int x = position[1] + DIRECTIONS[directionIndex][1];
		return new int[]{y, x};
	}

	/**
	 * Getting distance to a player from the next position.
	 *
	 * @return double of that distance
	 */
	protected double getDistance() {
		if (Debug.entities.Enemy) System.out.println("--- [Entity.getDistance]");
		int[] nextPosition = getNextPosition();
		int deltaY = Data.Player.position[0] - nextPosition[0];
		int deltaX = Data.Player.position[1] - nextPosition[1];

		// Use Pythagoras' theorem to calculate the distance
		return Math.sqrt(deltaY * deltaY + deltaX * deltaX);
	}

	/**
	 * Helper function for checking, if player and enemy are touching.
	 *
	 * @return yes if they share same space, no otherwise
	 */
	protected boolean checkForCollision() {
		if (Debug.entities.Enemy) System.out.println("--- [Enemy.checkForCollision]");

		if ((position[0] == Data.Player.position[0]) && (position[1] == Data.Player.position[1])) {
			int tempHealth = health;
			health -= Player.health;
			Player.getDamage(tempHealth);
			return true;
		}
		return false;
	}

	/**
	 * FUnction managing movement of the enemy.
	 */
	protected void move() {
		if (Debug.entities.Enemy) System.out.println(">>> [Enemy.move]");

		// Controlling, if enemy could move
		movementNumber++;
		// Overflow check
		if (movementNumber >= movementDelay) {
			movementNumber = 0;
		}
		// Case when enemy cannot move
		if (movementNumber != 0) {
			graphics.drawTile(position, icon, graphics.ENTITIES_LAYER);
			return;
		}

		// Checking, if player is outside of detection range
		if (getDistance() > detectionRange) {
			graphics.drawTile(position, icon, graphics.ENTITIES_LAYER);
			return;
		}

		double distanceToPlayer = Double.MAX_VALUE;
		int direction = 0;

		// Looping over each direction
		if (Debug.entities.Enemy) System.out.println("--- [Entity.onRefresh] Getting shortest path to the player");
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

		if (checkForCollision()) return;

		// Drawing entity
		graphics.drawTile(position, icon, graphics.ENTITIES_LAYER);

		if (Debug.entities.Enemy) System.out.println("<<< [Enemy.move]");
	}

	@Override
	public int[] getPosition() {
		if (Debug.entities.Enemy) System.out.println("--- [Enemy.getPosition]");
		return position;
	}

	@Override
	public void onRefresh() {
		if (Debug.entities.Enemy) System.out.println(">>> [Entity.onRefresh]");

		// For case of level editor cursor is on top.
		if (!Data.LevelEditor.levelEdit) {
			if (checkForCollision()) {
				return;
			}
		}

		// Checking, if it got enough health left
		if (health <= 0) {
			health = 0;
			listeners.removeRefreshListener(this);

			if (Debug.entities.Enemy) System.out.println("<<< [Entity.onRefresh] Early exit due to low enemy HP");
			return;
		}

		if (Data.running) {
			move();
		} else {
			// Drawing entity
			graphics.drawTile(position, icon, graphics.ENTITIES_LAYER);
		}

		if (Debug.entities.Enemy) System.out.println("<<< [Entity.onRefresh]");
	}
}
