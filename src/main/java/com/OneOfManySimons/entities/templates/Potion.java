package com.OneOfManySimons.entities.templates;

import com.OneOfManySimons.Data;
import com.OneOfManySimons.Debug;
import com.OneOfManySimons.entities.Item;
import com.OneOfManySimons.entities.player.Player;
import com.OneOfManySimons.graphics.Graphics;
import com.OneOfManySimons.graphics.Icons;
import com.OneOfManySimons.listeners.Listeners;
import com.OneOfManySimons.listeners.RefreshListener;

import javax.swing.*;

/**
 * Interactive potion.
 */
public class Potion implements RefreshListener {
	protected final int[] position;
	private final Graphics graphics = new Graphics();
	private final Listeners listeners = new Listeners();
	protected ImageIcon icon;
	protected int heal = 0;

	public Potion(int[] Position) {
		if (Debug.entities.templates.Potion) System.out.println("--- [Potion.constructor]");
		position = Position;
	}

	@Override
	public int[] getPosition() {
		if (Debug.entities.templates.Potion) System.out.println("--- [Potion.getPosition]");
		return position;
	}

	@Override
	public void onRefresh() {
		if (Debug.entities.templates.Potion) System.out.println(">>> [Potion.onRefresh]");

		if (Data.running) {
			if (position[0] == Data.Player.position[0] && position[1] == Data.Player.position[1]) {
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
