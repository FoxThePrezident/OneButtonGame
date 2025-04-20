package com.one_of_many_simons.one_button_game.listeners;

import android.view.View;
import com.common.entities.player.Player;

public class PlayerInputListener implements View.OnClickListener {

	@Override
	public void onClick(View view) {
		Player.action();
	}
}
