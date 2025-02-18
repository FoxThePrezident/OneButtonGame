package com.OneOfManySimons.entities.templates;

import com.OneOfManySimons.Data;
import com.OneOfManySimons.Debug;
import com.OneOfManySimons.entities.Item;
import com.OneOfManySimons.entities.player.Player;
import com.OneOfManySimons.graphics.Icons;
import com.OneOfManySimons.listeners.RefreshListener;

import javax.swing.*;
import java.awt.*;

import static com.OneOfManySimons.Data.libaries.graphics;
import static com.OneOfManySimons.Data.libaries.listeners;

/**
 * Interactive potion.
 */
public class Potion implements RefreshListener {
	protected final Point position;
	protected ImageIcon icon;
	protected int heal = 0;

	public Potion(Point position) {
		if (Debug.entities.templates.Potion) System.out.println("--- [Potion.constructor]");
		this.position = new Point(position);
	}

	@Override
	public Point getPosition() {
		if (Debug.entities.templates.Potion) System.out.println("--- [Potion.getPosition]");
		return new Point(position);
	}

	@Override
	public void onRefresh() {
		if (Debug.entities.templates.Potion) System.out.println(">>> [Potion.onRefresh]");

		if (Data.running) {
			if (Data.Player.position.equals(position)) {
				Item item = new Item(Icons.Interactive.hp_potion);
				item.setHeal(heal);
				Player.addItem(item);
				listeners.removeRefreshListener(this);
				if (Debug.entities.templates.Potion)
					System.out.println("<<< [Potion.onRefresh] Premature exit due already being used");
				return;
			}
		}

		graphics.drawTile(position, icon, graphics.DECOR_LAYER);
		if (Debug.entities.templates.Potion) System.out.println("<<< [Potion.onRefresh]");
	}
}
