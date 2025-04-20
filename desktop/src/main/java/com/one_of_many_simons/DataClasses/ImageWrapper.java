package com.one_of_many_simons.DataClasses;

import javax.swing.*;

public class ImageWrapper extends com.common.DataClasses.ImageWrapper {
	private final ImageIcon icon;

	public ImageWrapper(ImageIcon icon) {
		this.icon = icon;
	}

	@Override
	public ImageIcon getIcon() {
		return icon;
	}
}
