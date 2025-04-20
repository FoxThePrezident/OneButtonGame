package com.common.entities.templates;

import com.common.Data;
import com.common.DataClasses.ImageWrapper;
import com.common.DataClasses.Position;
import com.common.DataClasses.TextData;
import com.common.graphics.Icons;
import com.common.listeners.RefreshListener;

import static com.common.Data.graphics;
import static com.common.Debug.Flags.Entities.Templates.SIGN;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.Levels.INFORMATION;
import static com.common.Debug.debug;
import static com.common.graphics.Graphics.ENTITIES_LAYER;

/**
 * Sign for displaying text by stepping on it.
 */
public class Sign implements RefreshListener {
	protected final Position position;
	protected final ImageWrapper icon = Icons.Interactive.sign;

	private final String text;

	public Sign(Position position, String text) {
		debug(SIGN, CORE, "--- [Sign.constructor]");

		this.position = new Position(position);
		this.text = text;
	}

	@Override
	public Position getPosition() {
		debug(SIGN, INFORMATION, "--- [Sign.getPosition]");

		return new Position(position);
	}

	@Override
	public void onRefresh() {
		debug(SIGN, CORE, "--- [Sign.onRefresh]");

		if (Data.Player.position.equals(position)) {
			TextData textField = new TextData();
			textField.position = new Position(Data.Player.radius * (Data.IMAGE_SCALE - 1) * Data.IMAGE_SIZE, Data.Player.radius * Data.IMAGE_SCALE * Data.IMAGE_SIZE);
			textField.text = text;
			textField.size = 20;
			textField.centered = true;
			graphics.drawText(textField);
		}

		graphics.drawTile(position, icon, ENTITIES_LAYER);
	}

	@Override
	public void getEntityDamage(int damage) {
	}
}
