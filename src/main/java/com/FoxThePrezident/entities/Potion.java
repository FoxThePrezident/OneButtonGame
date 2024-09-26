package com.FoxThePrezident.entities;

import com.FoxThePrezident.Data;
import com.FoxThePrezident.Debug;
import com.FoxThePrezident.listeners.Listeners;
import com.FoxThePrezident.listeners.RefreshListener;
import com.FoxThePrezident.map.Graphics;

import javax.swing.*;

/**
 * Interactive potion.
 */
public class Potion implements RefreshListener {
	protected final int[] position;
	protected ImageIcon icon;
	private final Graphics graphics = new Graphics();
	private final Listeners listeners = new Listeners();

	protected int heal = 0;

	public Potion(int[] Position) {
		if (Debug.entities.Potion) System.out.println("--- [Potion.constructor]");
		position = Position;
	}

	@Override
	public int[] getPosition() {
		if (Debug.entities.Potion) System.out.println("--- [Potion.getPosition]");
		return position;
	}

	@Override
	public void onRefresh() {
		if (Debug.entities.Potion) System.out.println(">>> [Potion.onRefresh]");

		if (Data.running) {
			if (position[0] == Data.Player.position[0] && position[1] == Data.Player.position[1]) {
				Player.getHeal(heal);
				heal = 0;
			}
		}

		// Removing potion if it was used
		if (heal <= 0) {
			listeners.removeRefreshListener(this);
			if (Debug.entities.Potion) System.out.println("<<< [Potion.onRefresh] Premature exit due already being used");
			return;
		}

		graphics.drawTile(position, icon, graphics.DECOR_LAYER);
		if (Debug.entities.Potion) System.out.println("<<< [Potion.onRefresh]");
	}
}
