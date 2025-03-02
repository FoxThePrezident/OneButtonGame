package com.one_of_many_simons.one_button_game.entities.templates

import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Data.Libraries.graphics
import com.one_of_many_simons.one_button_game.Debug
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.graphics.Graphics.Companion.ENTITIES_LAYER
import com.one_of_many_simons.one_button_game.graphics.Icons
import com.one_of_many_simons.one_button_game.graphics.Text
import com.one_of_many_simons.one_button_game.listeners.RefreshListener

/**
 * SIGN for displaying text by stepping on it.
 */
class Sign(position: Position, text: String) : RefreshListener {
    @JvmField
    val position: Position
    private val icon: ByteArray? = Icons.Interactive.sign

    private val text: String

    init {
        if (Debug.Entities.Templates.SIGN) println("--- [Sign.constructor]")
        this.position = Position(position)
        this.text = text
    }

    override fun onRefresh() {
        if (Debug.Entities.Templates.SIGN) println("--- [Sign.onRefresh]")

        if (Data.Player.position == position) {
            val textField = Text()
            textField.setPosition(
                Position(
                    Data.Player.radius * (Data.IMAGE_SCALE - 1) * Data.IMAGE_SIZE,
                    Data.Player.radius * Data.IMAGE_SCALE * Data.IMAGE_SIZE
                )
            )
            textField.setText(text)
            textField.setSize(20)
            textField.setCentered(true)
            graphics.drawText(textField)
        }

        graphics.drawTile(position, icon, ENTITIES_LAYER)
    }

    override fun getPosition(): Position {
        return position
    }
}
