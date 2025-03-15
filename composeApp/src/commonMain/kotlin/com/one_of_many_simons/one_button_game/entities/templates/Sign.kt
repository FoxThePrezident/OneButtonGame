package com.one_of_many_simons.one_button_game.entities.templates

import androidx.compose.ui.graphics.ImageBitmap
import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Debug.Flags.Entities.Templates.SIGN
import com.one_of_many_simons.one_button_game.Debug.Levels.CORE
import com.one_of_many_simons.one_button_game.Debug.Levels.INFORMATION
import com.one_of_many_simons.one_button_game.Debug.debug
import com.one_of_many_simons.one_button_game.Libraries.graphics
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.dataClasses.TextData
import com.one_of_many_simons.one_button_game.graphics.Graphics.Companion.ENTITIES_LAYER
import com.one_of_many_simons.one_button_game.graphics.Icons
import com.one_of_many_simons.one_button_game.listeners.RefreshListener

/**
 * SIGN for displaying text by stepping on it.
 */
class Sign(position: Position, text: String) : RefreshListener {
    @JvmField
    val position: Position
    private val icon: ImageBitmap = Icons.Interactive.sign

    private val text: String

    init {
        debug(SIGN, CORE, "--- [Sign.constructor]")

        this.position = Position(position)
        this.text = text
    }

    override fun onRefresh() {
        debug(SIGN, CORE, "--- [Sign.onRefresh]")

        if (Data.Player.position.equals(position)) {
            val textField = TextData()

            textField.position =
                Position(
                    Data.Player.radius * Data.IMAGE_SCALE * Data.IMAGE_SIZE,
                    Data.Player.radius * Data.IMAGE_SCALE * (Data.IMAGE_SIZE - 1)
                )
            textField.text = text
            textField.isCentered = true

            graphics.drawTextField(textField)
        }

        graphics.drawTile(position, icon, ENTITIES_LAYER)
    }

    override fun getPosition(): Position {
        debug(SIGN, INFORMATION, "--- [Sign.getPosition]")

        return position
    }
}
