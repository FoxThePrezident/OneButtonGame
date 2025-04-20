package com.common.entities.player;

import com.common.Data;
import com.common.DataClasses.ArmorReturnDamage;
import com.common.DataClasses.ImageWrapper;
import com.common.DataClasses.Position;
import com.common.graphics.Icons;
import com.common.listeners.Listeners;
import com.common.listeners.RefreshListener;

import static com.common.Data.graphics;
import static com.common.Debug.Flags.Entities.Player.ARMOR;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.debug;
import static com.common.graphics.Graphics.ENTITIES_LAYER;

public class Armor implements RefreshListener {
	/**
	 * Armor health
	 */
	private int health = 10;
	private final Position position;

	/**
	 * How much armor will protect
	 */
	@SuppressWarnings("FieldCanBeLocal")
	private final float blockPercentage = 0.75f;

	ImageWrapper icon = Icons.Interactive.armor;

	public Armor(Position position) {
		this.position = position;
	}

	/**
	 * Apply damage to armor
	 *
	 * @param damage that is dealt
	 * @return Pair of Int and Boolean values. Int represent damage that was not blocked. Boolean represent, if armor was broken or not. True for armor was destroyed
	 */
	ArmorReturnDamage getDamage(int damage) {
		debug(ARMOR, CORE, "--- [Armor.getDamage]");

		ArmorReturnDamage data = new ArmorReturnDamage();

		health -= (int) (damage * blockPercentage);
		data.returnDamage = (int) (damage * (1 - blockPercentage));

		// Check if armor is destroyed
		if (health <= 0) {
			data.destroyed = true;
		}

		// Case if armor was not strong enough to hold all damage applied
		if (health < 0) {
			data.returnDamage -= health;
		}

		return data;
	}

	@Override
	public void onRefresh() {
		if (!Data.LevelEditor.levelEdit) {
			if (Data.Player.position.equals(position)) {
				Player.addArmor(this);
				Listeners.removeRefreshListener(this);
				return;
			}
		}

		graphics.drawTile(position, icon, ENTITIES_LAYER);
	}

	@Override
	public void getEntityDamage(int damage) {
	}

	@Override
	public Position getPosition() {
		return position;
	}
}