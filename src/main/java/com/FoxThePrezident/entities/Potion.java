package com.FoxThePrezident.entities;

import com.FoxThePrezident.Data;
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

	protected int heal = 0;

	public Potion(int[] Position) {
		if (Data.debug) System.out.println("--- [Potion.constructor]");
		position = Position;
	}

	@Override
	public void onRefresh() {
		if (Data.debug) System.out.println(">>> [Potion.onRefresh]");

		if (position[0] == Data.Player.position[0] && position[1] == Data.Player.position[1]) {
			Player.getHeal(heal);
			heal = 0;
		}

		if (heal > 0) {
			graphics.drawTile(position, icon, graphics.DECOR_LAYER);
		}

		if (Data.debug) System.out.println("<<< [Potion.onRefresh]");
	}
}
