package com.FoxThePrezident.entities;

import com.FoxThePrezident.Data;
import com.FoxThePrezident.map.Graphics;
import com.FoxThePrezident.map.Icons;
import com.FoxThePrezident.listeners.RefreshListener;

public class Hp_potion implements RefreshListener {
	private int hp = 10;
	private final int[] position;
	private final Graphics graphics = new Graphics();

	public Hp_potion(int[] Position) {
		position = Position;
	}

	@Override
	public void onRefresh() {
		if (position[0] == Data.Player.position[0] && position[1] == Data.Player.position[1]) {
			Player.getHeal(hp);
			hp = 0;
		}

		if (hp > 0) {
			graphics.drawTile(position, Icons.Interactive.hp_potion, graphics.DECOR_LAYER);
		}
	}
}
