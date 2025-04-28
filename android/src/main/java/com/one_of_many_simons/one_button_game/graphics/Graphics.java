package com.one_of_many_simons.one_button_game.graphics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.common.Data;
import com.common.DataClasses.Colour;
import com.common.DataClasses.ImageWrapper;
import com.common.DataClasses.Position;
import com.common.DataClasses.TextData;
import com.common.Debug;
import com.one_of_many_simons.one_button_game.listeners.PlayerInputListener;

import java.util.ArrayList;

import static com.common.Debug.Flags.Graphics.GRAPHICS;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.debug;

public class Graphics extends com.common.graphics.Graphics {
	private final Context context;
	private final FrameLayout rootLayout;
	private ArrayList<GridLayout> layers;
	private FrameLayout textLayer;

	public Graphics(Context context, FrameLayout rootLayout) {
		debug(GRAPHICS, CORE, ">>> [Graphics.Constructor]");

		this.context = context;
		this.rootLayout = rootLayout;

		debug(GRAPHICS, CORE, "<<< [Graphics.Constructor]");
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void initMap() {
		debug(GRAPHICS, CORE, ">>> [Graphics.initMap]");

		rootLayout.setLayoutParams(new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.MATCH_PARENT
		));
		rootLayout.setOnClickListener(new PlayerInputListener());
		rootLayout.setOnLongClickListener(new PlayerInputListener());

		layers = new ArrayList<>();
		for (int i = 0; i < layersCount; i++) {
			GridLayout layer = new GridLayout(context);

			layer.setColumnCount(Data.Player.radius * 2 + 1);
			layer.setRowCount(Data.Player.radius * 2 + 1);
			layer.setBackgroundColor(Color.TRANSPARENT);

			// Looping over each cell in a grid and generating new ImageView
			for (int y = 0; y < Data.Player.radius * 2 + 1; y++) {
				for (int x = 0; x < Data.Player.radius * 2 + 1; x++) {
					ImageView imageView = new ImageView(context);
					imageView.setTag(x + "_" + y);

					GridLayout.LayoutParams params = new GridLayout.LayoutParams();
					params.width = Data.IMAGE_SIZE * Data.IMAGE_SCALE;
					params.height = Data.IMAGE_SIZE * Data.IMAGE_SCALE;
					imageView.setLayoutParams(params);

					layer.addView(imageView);
				}
			}

			layers.add(layer);
			rootLayout.addView(layer);
		}
		textLayer = new FrameLayout(context);
		textLayer.setBackgroundColor(Color.TRANSPARENT);
		rootLayout.addView(textLayer);

		resizeScreen();

		debug(GRAPHICS, CORE, "<<< [Graphics.initMap]");
	}

	@Override
	public void clearScreen() {
		debug(GRAPHICS, Debug.Levels.INFORMATION, ">>> [Graphics.clearScreen]");

		new Handler(Looper.getMainLooper()).post(() -> {
			textLayer.removeAllViews();
			for (int i = 0; i < layersCount; i++) {
				GridLayout grid = layers.get(i);
				grid.removeAllViews(); // You may want to clear existing views first

				// Looping over each cell in a grid and generating new ImageView
				for (int y = 0; y < Data.Player.radius * 2 + 1; y++) {
					for (int x = 0; x < Data.Player.radius * 2 + 1; x++) {
						ImageView imageView = new ImageView(context);
						imageView.setTag(x + "_" + y);

						GridLayout.LayoutParams params = new GridLayout.LayoutParams();
						params.width = Data.IMAGE_SIZE * Data.IMAGE_SCALE;
						params.height = Data.IMAGE_SIZE * Data.IMAGE_SCALE;
						imageView.setLayoutParams(params);

						grid.addView(imageView);
					}
				}
			}
		});

		debug(GRAPHICS, Debug.Levels.INFORMATION, "<<< [Graphics.clearScreen]");
	}

	@Override
	public void resizeScreen() {
		debug(GRAPHICS, Debug.Levels.INFORMATION, ">>> [Graphics.resizeScreen]");

		// Getting metrics in pixels
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);

		int width = dm.widthPixels;
		int height = dm.heightPixels;

		int gameSize = Data.IMAGE_SIZE * Data.IMAGE_SCALE * (Data.Player.radius * 2 + 1);

		int marginX = (width - gameSize) / 2;
		int marginY = (height - gameSize) / 2;

		rootLayout.setPadding(marginX, marginY, 0, 0);

		debug(GRAPHICS, Debug.Levels.INFORMATION, "<<< [Graphics.resizeScreen]");
	}

	@Override
	public void clearLayer(int layer) {
		debug(GRAPHICS, Debug.Levels.INFORMATION, ">>> [Graphics.clearLayer]");

		new Handler(Looper.getMainLooper()).post(() -> {
			if (layer == TEXT_LAYER) {
				textLayer.removeAllViews();
			} else {
				GridLayout grid = layers.get(layer);
				grid.removeAllViews();

				for (int y = 0; y < Data.Player.radius * 2 + 1; y++) {
					for (int x = 0; x < Data.Player.radius * 2 + 1; x++) {
						ImageView imageView = new ImageView(context);
						imageView.setTag(x + "_" + y);

						GridLayout.LayoutParams params = new GridLayout.LayoutParams();
						params.width = Data.IMAGE_SIZE * Data.IMAGE_SCALE;
						params.height = Data.IMAGE_SIZE * Data.IMAGE_SCALE;
						imageView.setLayoutParams(params);

						grid.addView(imageView);
					}
				}
			}
		});

		debug(GRAPHICS, Debug.Levels.INFORMATION, "<<< [Graphics.clearLayer]");
	}


	@Override
	public void revalidate() {
	}

	@Override
	public void drawTile(Position position, ImageWrapper tile, int layer) {
		debug(GRAPHICS, CORE, ">>> [Graphics.drawTile]");

		new Handler(Looper.getMainLooper()).post(() -> {
			if (layer >= 0 && layer < layers.size()) {
				// Player position
				int playerX = Data.Player.position.x;
				int playerY = Data.Player.position.y;

				// Starting position
				int startX = playerX - Data.Player.radius;
				int startY = playerY - Data.Player.radius;

				// Adjusting coordinate based on player position
				int x = position.x - startX;
				int y = position.y - startY;

				if ((x < 0 || y < 0) || (x > Data.Player.radius * 2 || y > Data.Player.radius * 2)) {
					debug(GRAPHICS, CORE, "<<< [Graphics.drawTile] Outside of bounds");
					return;
				}

				ImageView imageView = layers.get(layer).findViewWithTag(x + "_" + y);
				int scaledSize = Data.IMAGE_SIZE * Data.IMAGE_SCALE;
				Bitmap scaled = Bitmap.createScaledBitmap((Bitmap) tile.getIcon(), scaledSize, scaledSize, false);

				imageView.setImageBitmap(scaled);
			}
		});

		debug(GRAPHICS, CORE, "<<< [Graphics.drawTile]");
	}

	@Override
	public void drawText(TextData textField) {
		Debug.debug(GRAPHICS, CORE, ">>> [Graphics.drawText]");

		new Handler(Looper.getMainLooper()).post(() -> {
			TextView textView = new TextView(context);

			// Set core properties
			textView.setText(textField.text);
			textView.setTextSize((float) (textField.size * 0.75));
			textView.setTextColor(convertColor(textField.foregroundColor));
			textView.setBackgroundColor(convertColor(textField.backgroundColor));

			// Border using background drawable
			GradientDrawable drawable = new GradientDrawable();
			drawable.setColor(convertColor(textField.backgroundColor));
			drawable.setStroke(textField.borderWidth, convertColor(textField.borderColor));
			textView.setBackground(drawable);

			// Layout params for positioning
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
					ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT
			);

			// Centered or positioned by grid
			if (textField.centered) {
				params.gravity = Gravity.CENTER;
			} else {
				params.leftMargin = textField.position.x;
				params.topMargin = textField.position.y;
			}

			textView.setLayoutParams(params);
			textLayer.addView(textView);
		});

		Debug.debug(GRAPHICS, CORE, "<<< [Graphics.drawText]");
	}


	public static int convertColor(Colour color) {
		return android.graphics.Color.rgb(color.r, color.g, color.b);
	}

	@Override
	public void showTextInput() {
	}
}
