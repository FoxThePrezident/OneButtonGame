package com.one_of_many_simons.one_button_game.entities.templates

import androidx.compose.ui.graphics.ImageBitmap
import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Debug.Flags.Entities.Templates.POTION
import com.one_of_many_simons.one_button_game.Debug.Levels.CORE
import com.one_of_many_simons.one_button_game.Debug.Levels.INFORMATION
import com.one_of_many_simons.one_button_game.Debug.debug
import com.one_of_many_simons.one_button_game.Libraries.graphics
import com.one_of_many_simons.one_button_game.Libraries.listeners
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.entities.player.Item
import com.one_of_many_simons.one_button_game.entities.player.Player
import com.one_of_many_simons.one_button_game.graphics.Graphics.Companion.ENTITIES_LAYER
import com.one_of_many_simons.one_button_game.graphics.Icons
import com.one_of_many_simons.one_button_game.listeners.RefreshListener

/**
 * Interactive potion.
 */
open class Potion(position: Position) : RefreshListener {
    @JvmField
    var position: Position = Position()

    @JvmField
    protected var icon: ImageBitmap? = null

    @JvmField
    protected var heal: Int = 0

    init {
        debug(POTION, CORE, "--- [Potion.constructor]")

        this.position = Position(position)
    }

    override fun onRefresh() {
        debug(POTION, CORE, ">>> [Potion.onRefresh]")

        if (Data.running) {
            if (Data.Player.position.equals(position)) {
                val item = Item(Icons.Interactive.hp_potion)
                item.setHeal(heal)
                Player.addItem(item)
                listeners.removeRefreshListener(this)

                debug(POTION, CORE, "<<< [Potion.onRefresh] Premature exit due already being used")
                return
            }
        }

        graphics.drawTile(position, icon, ENTITIES_LAYER)

        debug(POTION, CORE, "<<< [Potion.onRefresh]")
    }

    override fun getPosition(): Position {
        debug(POTION, INFORMATION, "--- [Potion.getPosition]")

        return position
    }
}
