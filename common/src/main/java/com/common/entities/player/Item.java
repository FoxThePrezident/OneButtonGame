package com.common.entities.player;

import com.common.DataClasses.ImageWrapper;
import com.common.DataClasses.Position;
import com.common.graphics.Icons;

import static com.common.Data.graphics;
import static com.common.Debug.Flags.Entities.Player.ITEM;
import static com.common.Debug.Levels.*;
import static com.common.Debug.debug;
import static com.common.graphics.Graphics.ACTIONS_LAYER;

/**
 * Class containing information about usable items inside players inventory
 */
public class Item {
	private final ImageWrapper icon;
	final boolean isNull;
	private int heal;

	public Item(ImageWrapper icon) {
		debug(ITEM, CORE, "--- [Item.constructor]");

		this.icon = icon;
		isNull = false;
	}

	public Item(ImageWrapper icon, boolean isNull) {
		debug(ITEM, CORE, "--- [Item.constructor]");

		this.icon = icon;
		this.isNull = isNull;
	}

	/**
	 * Drawing inventory
	 *
	 * @param position that item will be drawn
	 */
	public void draw(Position position) {
		debug(ITEM, INFORMATION, "--- [Item.draw]");

		if (!isNull) {
			graphics.drawTile(position, Icons.LevelEditor.cursor, ACTIONS_LAYER);
		}
		graphics.drawTile(position, icon, ACTIONS_LAYER);
	}

	/**
	 * Applying all effects, that this item has
	 */
	public void applyEffects() {
		debug(ITEM, INFORMATION, "--- [Item.applyEffects]");

		Player.getHeal(heal);
	}

	public void setHeal(int heal) {
		if (heal < 0) debug(ITEM, EXCEPTION, "--- [Item.setHeal] Heal cannot be less than zero");
		this.heal = heal;
	}
}
