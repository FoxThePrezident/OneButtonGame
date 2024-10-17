package com.FoxThePrezident.entities;

import com.FoxThePrezident.entities.player.Player;
import com.FoxThePrezident.graphics.Graphics;
import com.FoxThePrezident.graphics.Icons;

import javax.swing.*;

/**
 * Class containing information about usable items inside players inventory
 */
public class Item {
	private final ImageIcon _icon;
	private final boolean _isNull;
	private static final Graphics graphics = new Graphics();
	private int _heal;

	public Item(ImageIcon icon) {
		_icon = icon;
		_isNull = false;
	}

	public Item(ImageIcon icon, boolean isNull) {
		_icon = icon;
		_isNull = isNull;
	}

	public boolean getIsNull() {
		return _isNull;
	}

	/**
	 * Drawing inventory
	 *
	 * @param position that item will be drawn
	 */
	public void draw(int[] position) {
		if (!_isNull) {
			graphics.drawTile(position, Icons.LevelEditor.cursor, graphics.ARROW_LAYER);
		}
		graphics.drawTile(position, _icon, graphics.ARROW_LAYER);
	}

	/**
	 * Applying all effects, that this item has
	 */
	public void applyEffects() {
		Player.getHeal(_heal);
	}

	public void setHeal(int heal) {
		if (heal < 0) throw new RuntimeException("Heal cannot be negative");
		_heal = heal;
	}
}
