package com.common.entities.templates;

import com.common.Data;
import com.common.DataClasses.ImageWrapper;
import com.common.DataClasses.Position;
import com.common.entities.player.Item;
import com.common.entities.player.Player;
import com.common.graphics.Icons;
import com.common.listeners.Listeners;
import com.common.listeners.RefreshListener;

import static com.common.Data.graphics;
import static com.common.Debug.Flags.Entities.Templates.POTION;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.Levels.INFORMATION;
import static com.common.Debug.debug;
import static com.common.graphics.Graphics.DECOR_LAYER;

/**
 * Interactive potion.
 */
public class Potion implements RefreshListener {
	protected final Position position;
	protected ImageWrapper icon;
	protected int heal = 0;

	public Potion(Position position) {
		debug(POTION, CORE, "--- [Potion.constructor]");

		this.position = new Position(position);
	}

	@Override
	public Position getPosition() {
		debug(POTION, INFORMATION, "--- [Potion.getPosition]");

		return new Position(position);
	}

	@Override
	public void onRefresh() {
		debug(POTION, CORE, ">>> [Potion.onRefresh]");

		if (Data.running) {
			if (Data.Player.position.equals(position)) {
				Item item = new Item(Icons.Interactive.hp_potion);
				item.setHeal(heal);
				Player.addItem(item);
				Listeners.removeRefreshListener(this);
				debug(POTION, CORE, "<<< [Potion.onRefresh]");
				return;
			}
		}

		graphics.drawTile(position, icon, DECOR_LAYER);
		debug(POTION, CORE, "<<< [Potion.onRefresh]");
	}

	@Override
	public void getEntityDamage(int damage) {
	}
}
