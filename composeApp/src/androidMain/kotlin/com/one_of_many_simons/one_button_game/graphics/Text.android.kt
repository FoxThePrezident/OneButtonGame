package com.one_of_many_simons.one_button_game.graphics

import com.one_of_many_simons.one_button_game.dataClasses.Colour
import com.one_of_many_simons.one_button_game.dataClasses.Position

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class Text {
    @JvmField
    actual var position: Position = Position()

    @JvmField
    actual var text: String = ""

    @JvmField
    actual var size: Int = 0

    @JvmField
    actual var centered: Boolean = false

    @JvmField
    actual var backgroundColor: Colour? = null

    @JvmField
    actual var foregroundColor: Colour = Colour(0, 0, 0)

    @JvmField
    actual var borderColor: Colour? = null

    /**
     * Set text field position, default is [0, 0]
     *
     * @param position int[y, x]
     */
    actual fun setPosition(position: Position) {
    }

    /**
     * Set text color, default is White
     * @param color to which text will be set
     */
    actual fun setForegroundColor(color: Colour) {
    }

    /**
     * Set text field text, default is ""
     *
     * @param text which will be set to text field
     */
    actual fun setText(text: String) {
    }

    /**
     * Set  text field text size, default is 16
     *
     * @param size int of text size
     */
    actual fun setSize(size: Int) {
    }

    /**
     * If text field should be centered on screen, default is false
     *
     * @param centered boolean
     */
    actual fun setCentered(centered: Boolean) {
    }

    /**
     * Background color of text field, default is false
     *
     * @param backgroundColor of text field
     */
    actual fun setBackgroundColor(backgroundColor: Colour?) {
    }

    /**
     * Border of text field, default border is null
     *
     * @param colour of text field
     */
    actual fun setBorderColor(colour: Colour) {
    }

    actual fun getText(width: Int): Any? {
        return null
    }
}