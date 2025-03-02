package com.one_of_many_simons.one_button_game.graphics

import com.one_of_many_simons.one_button_game.dataClasses.Colour
import com.one_of_many_simons.one_button_game.dataClasses.Position

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class Text() {
    var position: Position
    var text: String
    var size: Int
    var centered: Boolean
    var backgroundColor: Colour?
    var foregroundColor: Colour
    var borderColor: Colour?

    /**
     * Set text field position, default is [0, 0]
     *
     * @param position int[y, x]
     */
    fun setPosition(position: Position)

    /**
     * Set text color, default is White
     * @param color to which text will be set
     */
    fun setForegroundColor(color: Colour)

    /**
     * Set text field text, default is ""
     *
     * @param text which will be set to text field
     */
    fun setText(text: String)

    /**
     * Set  text field text size, default is 16
     *
     * @param size int of text size
     */
    fun setSize(size: Int)

    /**
     * If text field should be centered on screen, default is false
     *
     * @param centered boolean
     */
    fun setCentered(centered: Boolean)

    /**
     * Background color of text field, default is false
     *
     * @param backgroundColor of text field
     */
    fun setBackgroundColor(backgroundColor: Colour?)

    /**
     * Border of text field, default border is null
     *
     * @param color of text field
     */
    fun setBorderColor(colour: Colour)

    fun getText(width: Int): Any?
}
