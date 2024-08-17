package com.FoxThePrezident.entities;

import com.FoxThePrezident.Data;
import com.FoxThePrezident.listeners.RefreshListener;
import com.FoxThePrezident.map.Graphics;
import com.FoxThePrezident.map.Icons;

import javax.swing.*;

/**
 * Sign for displaying text by stepping on it.
 */
public class Sign implements RefreshListener {
	protected final int[] position;
	protected ImageIcon icon = Icons.Interactive.sign;
	private final Graphics graphics = new Graphics();

	private final String text;

	public Sign(int[] Position, String Text) {
		position = Position;
		text = Text;
	}

	@Override
	public void onRefresh() {
		if (Data.debug) System.out.println("--- [Sign.onRefresh]");

		if (position[0] == Data.Player.position[0] && position[1] == Data.Player.position[1]) {
			graphics.drawText(new int[]{Data.Player.radius * (Data.imageScale - 1) * 16, Data.Player.radius * Data.imageScale * 16}, text, 20, true);
		}

		graphics.drawTile(position, icon, graphics.DECOR_LAYER);
	}
}
