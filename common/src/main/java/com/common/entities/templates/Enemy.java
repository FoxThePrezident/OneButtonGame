package com.common.entities.templates;

import com.common.Data;
import com.common.DataClasses.ImageWrapper;
import com.common.DataClasses.Position;
import com.common.entities.algorithms.PathFinding;
import com.common.entities.player.Player;
import com.common.graphics.Icons;
import com.common.listeners.Listeners;
import com.common.listeners.RefreshListener;

import static com.common.Data.graphics;
import static com.common.Debug.Flags.Entities.Templates.ENEMY;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.Levels.INFORMATION;
import static com.common.Debug.debug;
import static com.common.graphics.Graphics.DECOR_LAYER;
import static com.common.graphics.Graphics.ENTITIES_LAYER;

/**
 * Movable entity.<br>
 * Track the shortest path to the player
 */
public class Enemy implements RefreshListener {
	// ********************
	// ***** Movement *****
	// ********************
	/**
	 * Current enemy position
	 */
	protected Position position;

	/**
	 * Range from which enemy will detect player
	 */
	protected int detectionRange = 3;

	/**
	 * Distance that will enemy try to keep from player
	 */
	protected int keepDistance = 0;

	/**
	 * Which way is enemy facing
	 */
	protected int directionIndex = 0;

	/**
	 * Tell how often enemy will move.
	 * For example move once and skip twice.
	 * It is max threshold
	 */
	protected int movementDelay = 2;

	/**
	 * Tell current move since enemy moved
	 */
	protected int movementNumber = 0;

	// ***********************
	// ***** Projectiles *****
	// ***********************

	/**
	 * If this entity have ability to shoot projectiles towards player
	 */
	protected boolean couldFireProjectile = false;

	/**
	 * Max threshold for how often enemy could fire a projectile towards enemy
	 */
	protected int projectileDelay = 0;

	/**
	 * Current move since enemy fired
	 */
	protected int projectileNumber = 0;

	// *******************
	// ***** General *****
	// *******************

	protected ImageWrapper icon = null;

	/**
	 * Enemy health
	 */
	protected int health = 10;

	public Enemy(Position position) {
		debug(ENEMY, CORE, "--- [Enemy.constructor]");

		this.position = new Position(position);
	}

	/**
	 * Helper function for checking, if player and enemy are touching.
	 *
	 * @return yes if they share same space, no otherwise
	 */
	private boolean checkForCollision() {
		debug(ENEMY, CORE, "--- [Enemy.checkForCollision]");

		if (Data.Player.position.equals(position)) {
			int tempHealth = health;
			health -= Player.getHealth();
			Player.getDamage(tempHealth);
			Data.Map.enemyCount--;
			return true;
		}
		return false;
	}

	/**
	 * FUnction managing movement of the enemy.
	 */
	private void move() {
		debug(ENEMY, CORE, ">>> [Enemy.move]");

		// Checking, if player is outside of detection range
		if (PathFinding.getDistance(position, Data.Player.position) > detectionRange) {
			graphics.drawTile(position, icon, ENTITIES_LAYER);
			return;
		}

		// Projectile
		if (couldFireProjectile) {
			debug(ENEMY, INFORMATION, "--- [Enemy.move] Firing projectile");
			projectileNumber++;

			// Overflow check
			if (projectileNumber >= projectileDelay) {
				projectileNumber = 0;
			}

			// Drawing attention right before entity shoots
			if (projectileNumber == projectileDelay - 1) {
				Position attentionPosition = new Position(position.x, position.y - 1);
				graphics.drawTile(attentionPosition, Icons.General.attention, DECOR_LAYER);
			}

			// Spawn projectile
			if (projectileNumber == 0) {
				Projectile projectile = new Projectile(position, Data.Player.position);
				Listeners.addRefreshListener(projectile);

				// Drawing entity
				graphics.drawTile(position, icon, ENTITIES_LAYER);

				// Preventing multiple actions from occurring
				return;
			}
		}

		// Controlling, if enemy could move
		debug(ENEMY, INFORMATION, "--- [Enemy.move] Moving");
		movementNumber++;

		// Overflow check
		if (movementNumber >= movementDelay) {
			movementNumber = 0;
		}

		// Signals to player, that enemy is ready to attack
		if (movementNumber == movementDelay - 1) {
			Position attentionPosition = new Position(position.x, position.y - 1);
			graphics.drawTile(attentionPosition, Icons.General.attention, DECOR_LAYER);
		}

		// Case when enemy cannot move
		if (movementNumber != 0) {
			graphics.drawTile(position, icon, ENTITIES_LAYER);
			return;
		}

		// Storing that direction
		directionIndex = PathFinding.toPoint(position, Data.Player.position, keepDistance);
		if (directionIndex == -1) {
			graphics.drawTile(position, icon, ENTITIES_LAYER);
			debug(ENEMY, CORE, "<<< [Enemy.move]");
			return;
		}
		position = PathFinding.getNextPosition(position, directionIndex);

		if (checkForCollision()) {
			debug(ENEMY, CORE, "<<< [Enemy.move]");
			return;
		}

		// Drawing entity
		graphics.drawTile(position, icon, ENTITIES_LAYER);

		debug(ENEMY, CORE, "<<< [Enemy.move]");
	}

	@Override
	public void getEntityDamage(int damage) {
		debug(ENEMY, CORE, "--- [Enemy.getDamage]");

		if (damage > 0) {
			health -= damage;
		}
	}

	@Override
	public void onRefresh() {
		debug(ENEMY, CORE, ">>> [Enemy.onRefresh]");

		// For case of level editor cursor is on top.
		if (!Data.LevelEditor.levelEdit) {
			if (checkForCollision()) {
				return;
			}
		}

		// Checking, if it got enough health left
		if (health <= 0) {
			health = 0;
			Listeners.removeRefreshListener(this);

			debug(ENEMY, CORE, "<<< [Enemy.onRefresh] Early exit due to low enemy HP");
			return;
		}

		if (Data.running) {
			move();
		} else {
			// Drawing entity
			graphics.drawTile(position, icon, ENTITIES_LAYER);
		}

		debug(ENEMY, CORE, "<<< [Enemy.onRefresh]");
	}

	@Override
	public Position getPosition() {
		debug(ENEMY, INFORMATION, "--- [Enemy.getPosition]");

		return position;
	}
}
