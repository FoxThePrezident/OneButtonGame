package com.OneOfManySimons.entities.templates;

import com.OneOfManySimons.Data;
import com.OneOfManySimons.Debug;
import com.OneOfManySimons.graphics.Icons;
import com.OneOfManySimons.graphics.Text;
import com.OneOfManySimons.listeners.RefreshListener;

import javax.swing.*;
import java.awt.*;

import static com.OneOfManySimons.Data.libaries.graphics;

/**
 * Sign for displaying text by stepping on it.
 */
public class Sign implements RefreshListener {
	protected final Point position;
	protected final ImageIcon icon = Icons.Interactive.sign;

	private final String text;

	public Sign(Point position, String text) {
		if (Debug.entities.templates.Sign) System.out.println("--- [Sign.constructor]");
		this.position = new Point(position);
		this.text = text;
	}

	@Override
	public Point getPosition() {
		if (Debug.entities.templates.Sign) System.out.println("--- [Sign.getPosition]");
		return new Point(position);
	}

	@Override
	public void onRefresh() {
		if (Debug.entities.templates.Sign) System.out.println("--- [Sign.onRefresh]");

		if (Data.Player.position.equals(position)) {
			Text textField = new Text();
			textField.setPosition(new Point(Data.Player.radius * (Data.imageScale - 1) * Data.imageSize, Data.Player.radius * Data.imageScale * Data.imageSize));
			textField.setText(text);
			textField.setSize(20);
			textField.setCentered(true);
			graphics.drawText(textField);
		}

		graphics.drawTile(position, icon, graphics.ENTITIES_LAYER);
	}
}
