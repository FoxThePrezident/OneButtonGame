package com.OneOfManySimons.entities.templates;

import com.OneOfManySimons.Data;
import com.OneOfManySimons.Debug;
import com.OneOfManySimons.entities.player.Player;
import com.OneOfManySimons.listeners.RefreshListener;

import javax.swing.*;
import java.awt.*;

import static com.OneOfManySimons.Data.libraries.*;

/**
 * Movable entity.<br>
 * Track the shortest path to the player
 */
public class Enemy implements RefreshListener {
	// Movement
	protected final int detectionRange = 3;
	protected final Point[] DIRECTIONS = {new Point(0, -1), new Point(1, 0), new Point(0, 1), new Point(-1, 0)};
	protected Point position;
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
	protected int directionIndex = 0;

	public Enemy(Point position) {
		if (Debug.entities.templates.Enemy) System.out.println("--- [Enemy.constructor]");
		this.position = new Point(position);
	}

	/**
	 * Looking to the future, what place I will occupy if I go there.
	 *
	 * @return int pair of the next position
	 */
	protected Point getNextPosition() {
		if (Debug.entities.templates.Enemy) System.out.println("--- [Enemy.getNextPosition]");
		int y = position.y + DIRECTIONS[directionIndex].y;
		int x = position.x + DIRECTIONS[directionIndex].x;
		return new Point(x, y);
	}

	/**
	 * Getting distance to a player from the next position.
	 *
	 * @return double of that distance
	 */
	protected double getDistance() {
		if (Debug.entities.templates.Enemy) System.out.println("--- [Enemy.getDistance]");
		Point nextPosition = getNextPosition();
		int deltaY = Data.Player.position.y - nextPosition.y;
		int deltaX = Data.Player.position.x - nextPosition.x;

		// Use Pythagoras' theorem to calculate the distance
		return Math.sqrt(deltaY * deltaY + deltaX * deltaX);
	}

	/**
	 * Helper function for checking, if player and enemy are touching.
	 *
	 * @return yes if they share same space, no otherwise
	 */
	protected boolean checkForCollision() {
		if (Debug.entities.templates.Enemy) System.out.println("--- [Enemy.checkForCollision]");

		if (Data.Player.position.equals(position)) {
			int tempHealth = health;
			health -= Player.health;
			Player.getDamage(tempHealth);
			Data.Map.enemyCount--;
			return true;
		}
		return false;
	}

	/**
	 * FUnction managing movement of the enemy.
	 */
	protected void move() {
		if (Debug.entities.templates.Enemy) System.out.println(">>> [Enemy.move]");

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
		if (Debug.entities.templates.Enemy)
			System.out.println("--- [Entity.onRefresh] Getting shortest path to the player");
		for (directionIndex = 0; directionIndex <= 3; directionIndex++) {
			// Checking, if that is a valid place
			Point nextPosition = getNextPosition();
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

		if (Debug.entities.templates.Enemy) System.out.println("<<< [Enemy.move]");
	}

	@Override
	public Point getPosition() {
		if (Debug.entities.templates.Enemy) System.out.println("--- [Enemy.getPosition]");
		return new Point(position);
	}

	@Override
	public void onRefresh() {
		if (Debug.entities.templates.Enemy) System.out.println(">>> [Entity.onRefresh]");

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

			if (Debug.entities.templates.Enemy)
				System.out.println("<<< [Entity.onRefresh] Early exit due to low enemy HP");
			return;
		}

		if (Data.running) {
			move();
		} else {
			// Drawing entity
			graphics.drawTile(position, icon, graphics.ENTITIES_LAYER);
		}

		if (Debug.entities.templates.Enemy) System.out.println("<<< [Entity.onRefresh]");
	}
}
