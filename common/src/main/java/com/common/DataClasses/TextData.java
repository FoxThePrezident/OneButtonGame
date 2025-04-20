package com.common.DataClasses;

public class TextData {
	/**
	 * Text field position, default is [0, 0]
	 */
	public Position position = new Position();
	/**
	 * Text field text, default is ""
	 */
	public String text = "";
	/**
	 * Text field text size, default is 16
	 */
	public int size = 16;
	/**
	 * If text field should be centered on screen, default is false
	 */
	public boolean centered = false;
	/**
	 * Background color of text field, default is false
	 */
	public Colour backgroundColor = new Colour(0, 0, 0);
	/**
	 * Text color, default is White
	 */
	public Colour foregroundColor = new Colour(255, 255, 255);

	/**
	 * Border color, default is black
	 */
	public Colour borderColor = new Colour(0, 0, 0);
	public int borderWidth = 2;
}
