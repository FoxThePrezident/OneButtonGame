package com.OneOfManySimons.entities.templates;

import com.OneOfManySimons.Data;
import com.OneOfManySimons.Debug;
import com.OneOfManySimons.graphics.Graphics;
import com.OneOfManySimons.graphics.Icons;
import com.OneOfManySimons.graphics.Text;
import com.OneOfManySimons.listeners.RefreshListener;

import javax.swing.*;

/**
 * Sign for displaying text by stepping on it.
 */
public class Sign implements RefreshListener {
	protected final int[] position;
	protected final ImageIcon icon = Icons.Interactive.sign;
	private final Graphics graphics = new Graphics();

	private final String text;

	public Sign(int[] Position, String Text) {
		if (Debug.entities.templates.Sign) System.out.println("--- [Sign.constructor]");
		position = Position;
		text = Text;
	}

	@Override
	public int[] getPosition() {
		if (Debug.entities.templates.Sign) System.out.println("--- [Sign.getPosition]");
		return position;
	}

	@Override
	public void onRefresh() {
		if (Debug.entities.templates.Sign) System.out.println("--- [Sign.onRefresh]");

		if (position[0] == Data.Player.position[0] && position[1] == Data.Player.position[1]) {
			Text textField = new Text();
			textField.setPosition(new int[]{Data.Player.radius * (Data.imageScale - 1) * Data.imageSize, Data.Player.radius * Data.imageScale * Data.imageSize});
			textField.setText(text);
			textField.setSize(20);
			textField.setCentered(true);
			graphics.drawText(textField);
		}

		graphics.drawTile(position, icon, graphics.ENTITIES_LAYER);
	}
}
