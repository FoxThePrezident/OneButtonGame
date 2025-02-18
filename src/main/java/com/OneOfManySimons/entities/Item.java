package com.OneOfManySimons.entities;

import com.OneOfManySimons.Debug;
import com.OneOfManySimons.entities.player.Player;
import com.OneOfManySimons.graphics.Icons;

import javax.swing.*;
import java.awt.*;

import static com.OneOfManySimons.Data.libaries.graphics;

/**
 * Class containing information about usable items inside players inventory
 */
public class Item {
	private final ImageIcon icon;
	private final boolean isNull;
	private int heal;

	public Item(ImageIcon icon) {
		if (Debug.entities.Item) System.out.println("--- [Item.constructor]");

		this.icon = icon;
		isNull = false;
	}

	public Item(ImageIcon icon, boolean isNull) {
		if (Debug.entities.Item) System.out.println("--- [Item.constructor]");

		this.icon = icon;
		this.isNull = isNull;
	}

	public boolean getIsNull() {
		return isNull;
	}

	/**
	 * Drawing inventory
	 *
	 * @param position that item will be drawn
	 */
	public void draw(Point position) {
		if (Debug.entities.Item) System.out.println("--- [Item.draw]");

		if (!isNull) {
			graphics.drawTile(position, Icons.LevelEditor.cursor, graphics.ARROW_LAYER);
		}
		graphics.drawTile(position, icon, graphics.ARROW_LAYER);
	}

	/**
	 * Applying all effects, that this item has
	 */
	public void applyEffects() {
		if (Debug.entities.Item) System.out.println("--- [Item.applyEffects]");

		Player.getHeal(heal);
	}

	public void setHeal(int heal) {
		if (heal < 0) throw new RuntimeException("Heal cannot be negative");
		this.heal = heal;
	}
}
