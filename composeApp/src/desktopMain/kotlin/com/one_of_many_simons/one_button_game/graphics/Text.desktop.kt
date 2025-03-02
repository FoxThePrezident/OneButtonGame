package com.one_of_many_simons.one_button_game.graphics

import androidx.lifecycle.ViewModelProvider
import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Debug
import com.one_of_many_simons.one_button_game.dataClasses.Colour
import com.one_of_many_simons.one_button_game.dataClasses.Position
import java.awt.Color
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.SwingConstants
import javax.swing.border.Border
import kotlin.math.pow

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
        this.position = Position(position)
    }

    /**
     * Set text color, default is White
     * @param color to which text will be set
     */
    actual fun setForegroundColor(color: Colour) {
        this.foregroundColor = color
    }

    /**
     * Set text field text, default is ""
     *
     * @param text which will be set to text field
     */
    actual fun setText(text: String) {
        this.text = text
    }

    /**
     * Set  text field text size, default is 16
     *
     * @param size int of text size
     */
    actual fun setSize(size: Int) {
        this.size = size
    }

    /**
     * If text field should be centered on screen, default is false
     *
     * @param centered boolean
     */
    actual fun setCentered(centered: Boolean) {
        this.centered = centered
    }

    /**
     * Background color of text field, default is false
     *
     * @param backgroundColor of text field
     */
    actual fun setBackgroundColor(backgroundColor: Colour?) {
        this.backgroundColor = backgroundColor
    }

    /**
     * Border of text field, default border is null
     *
     * @param color of text field
     */
    actual fun setBorderColor(colour: Colour) {
        this.borderColor = colour
    }

    actual fun getText(width: Int): Any? {
        if (Debug.Graphics.TEXT) println(">>> [Text.getText]")

        val label = JLabel()

        label.text = "<html><p style=text-align: 'center';>$text</p></html>"

        label.font = Font("Serif", Font.PLAIN, size)
        label.foreground = Color(foregroundColor.red, foregroundColor.green, foregroundColor.blue)

        // Set background color
        if (backgroundColor != null) {
            label.isOpaque = true
            label.background = Color(backgroundColor!!.red, backgroundColor!!.green, backgroundColor!!.blue)
        } else {
            label.isOpaque = false
        }

        // Set border
        if (borderColor != null) {
            val color = Color(borderColor!!.red, borderColor!!.green, borderColor!!.blue)
            label.border = BorderFactory.createLineBorder(color, 3)
        }

        // Get preferred size to accommodate text and border
        val textSize = label.preferredSize

        // Calculate label width considering the border thickness
        val labelWidth = textSize.width + 8
        var labelHeight = textSize.height
        // For case of long text
        val maxTextLength = (2.0.pow(Data.Player.radius.toDouble()) * 2 - 1).toInt()
        if (text.length > maxTextLength) {
            labelHeight *= 2
        }

        // Centering or positioning text
        if (centered) {
            label.setBounds(0, position.y, width, labelHeight)
        } else {
            label.setBounds(position.x, position.y, labelWidth, labelHeight)
        }

        label.horizontalAlignment = SwingConstants.CENTER

        if (Debug.Graphics.TEXT) println("<<< [Text.getText]")
        return label
    }
}