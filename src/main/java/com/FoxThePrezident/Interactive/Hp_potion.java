package com.FoxThePrezident.Interactive;

import com.FoxThePrezident.entities.Player;
import com.FoxThePrezident.graphics.Graphics;
import com.FoxThePrezident.graphics.Icons;
import com.FoxThePrezident.graphics.RefreshListener;

public class Hp_potion implements RefreshListener {
	private int hp = 10;
	private final int[] position;
	private final Graphics graphics = new Graphics();

	public Hp_potion(int[] Position) {
		position = Position;
	}
	@Override
	public void onRefresh() {
		if (position[0] == Player.position[0] && position[1] == Player.position[1]) {
			Player.getHeal(hp);
			hp = 0;
		}

		if (hp > 0) {
			graphics.drawTile(position[0], position[1], Icons.Interactive.hp_potion, graphics.DECOR_LAYER);
		}
	}
}
