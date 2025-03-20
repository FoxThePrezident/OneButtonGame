package com.one_of_many_simons.one_button_game.entities.player

import androidx.compose.ui.graphics.ImageBitmap
import com.one_of_many_simons.one_button_game.Debug.Flags.Entities.Player.ITEM
import com.one_of_many_simons.one_button_game.Debug.Levels.CORE
import com.one_of_many_simons.one_button_game.Debug.Levels.EXCEPTION
import com.one_of_many_simons.one_button_game.Debug.Levels.INFORMATION
import com.one_of_many_simons.one_button_game.Debug.debug
import com.one_of_many_simons.one_button_game.Libraries.graphics
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.entities.player.Player.Companion.getHeal
import com.one_of_many_simons.one_button_game.graphics.Graphics.Companion.ACTIONS_LAYER
import com.one_of_many_simons.one_button_game.graphics.Icons

/**
 * Class containing information about usable items inside players inventory
 */
class Item {
    private val icon: ImageBitmap
    val isNull: Boolean
    private var heal = 0

    constructor(icon: ImageBitmap) {
        debug(ITEM, CORE, "--- [Item.constructor]")

        this.icon = icon
        isNull = false
    }

    constructor(icon: ImageBitmap, isNull: Boolean) {
        debug(ITEM, CORE, "--- [Item.constructor]")

        this.icon = icon
        this.isNull = isNull
    }

    /**
     * Drawing inventory
     *
     * @param position that item will be drawn
     */
    fun draw(position: Position) {
        debug(ITEM, INFORMATION, "--- [Item.draw]")

        if (!isNull) {
            Icons.LevelEditor.cursor.let { graphics.drawTile(position, it, ACTIONS_LAYER) }
        }
        graphics.drawTile(position, icon, ACTIONS_LAYER)
    }

    /**
     * Applying all effects, that this item has
     */
    fun applyEffects() {
        debug(ITEM, INFORMATION, "--- [Item.applyEffects]")

        getHeal(heal)
    }

    fun setHeal(heal: Int) {
        debug(ITEM, INFORMATION, "--- [Item.setHeal]")

        if (heal < 0) {
            debug(ITEM, EXCEPTION, "--- [Item.setHeal] Heal cannot be negative")
            return
        }
        this.heal = heal
    }
}
