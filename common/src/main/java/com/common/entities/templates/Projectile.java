package com.common.entities.templates;

import com.common.Data;
import com.common.DataClasses.ImageWrapper;
import com.common.DataClasses.Position;
import com.common.entities.algorithms.PathFinding;
import com.common.entities.player.Player;
import com.common.graphics.Icons;
import com.common.listeners.Listeners;
import com.common.listeners.RefreshListener;
import com.common.map.Collisions;

import static com.common.Data.graphics;
import static com.common.Debug.Flags.Entities.Templates.PROJECTILE;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.Levels.INFORMATION;
import static com.common.Debug.debug;
import static com.common.graphics.Graphics.DECOR_LAYER;

public class Projectile implements RefreshListener {
	private Position position;
	private final int directionIndex;
	private final ImageWrapper icon = Icons.Interactive.projectile;
	private int damage = 5;

	public Projectile(Position position, Position target) {
		debug(PROJECTILE, CORE, "--- [Projectile.constructor]");

		this.position = position;
		directionIndex = PathFinding.toPoint(position, target);

		onRefresh();
	}

	@Override
	public void onRefresh() {
		debug(PROJECTILE, CORE, ">>> [Projectile.onRefresh]");

		// Checking, if projectile still can do some damage
		if (damage <= 0) {
			Listeners.removeRefreshListener(this);
			debug(PROJECTILE, CORE, "<<< [Projectile.onRefresh]");
			return;
		}

		// Checking for collision with player
		if (position.equals(Data.Player.position)) {
			Player.getDamage(damage);
			Listeners.removeRefreshListener(this);

			debug(PROJECTILE, CORE, "<<< [Projectile.onRefresh]");
			return;
		}

		// Case with collision with other things
		RefreshListener refreshListener = Listeners.getRefreshListener(position);
		if (refreshListener != null) {
			if (position.equals(Listeners.getRefreshListener(position))) {
				refreshListener.getEntityDamage(damage);
				Listeners.removeRefreshListener(this);

				debug(PROJECTILE, CORE, "<<< [Projectile.onRefresh]");
				return;
			}
		}

		position = PathFinding.getNextPosition(position, directionIndex);

		int couldMove = Collisions.checkForCollision(graphics.getTile(position));
		if (couldMove == Collisions.immovable) {
			Listeners.removeRefreshListener(this);

			debug(PROJECTILE, CORE, "<<< [Projectile.onRefresh]");
			return;
		}

		// Checking for collision with player
		if (position.equals(Data.Player.position)) {
			Player.getDamage(damage);
			Listeners.removeRefreshListener(this);

			debug(PROJECTILE, CORE, "<<< [Projectile.onRefresh]");
			return;
		}

		// Case with collision with other things
		refreshListener = Listeners.getRefreshListener(position);
		if (refreshListener != null) {
			if (position.equals(Listeners.getRefreshListener(position))) {
				refreshListener.getEntityDamage(damage);
				Listeners.removeRefreshListener(this);

				debug(PROJECTILE, CORE, "<<< [Projectile.onRefresh]");
				return;
			}
		}

		graphics.drawTile(position, icon, DECOR_LAYER);

		debug(PROJECTILE, CORE, "<<< [Projectile.onRefresh]");
	}

	@Override
	public void getEntityDamage(int damage) {
		debug(PROJECTILE, INFORMATION, "--- [Projectile.getEntityDamage]");
		if (damage > 0) {
			this.damage -= damage;
		}
	}

	@Override
	public Position getPosition() {
		debug(PROJECTILE, INFORMATION, "--- [Projectile.getPosition]");

		return position;
	}
}