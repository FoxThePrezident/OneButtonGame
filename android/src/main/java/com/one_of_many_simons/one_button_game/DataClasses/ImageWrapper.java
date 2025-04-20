package com.one_of_many_simons.one_button_game.DataClasses;

import android.graphics.Bitmap;

public class ImageWrapper extends com.common.DataClasses.ImageWrapper {
	private final Bitmap icon;

	public ImageWrapper(Bitmap icon) {
		this.icon = icon;
	}

	@Override
	public Bitmap getIcon() {
		return icon;
	}
}
