package com.one_of_many_simons.one_button_game.entities.player

import androidx.compose.ui.graphics.ImageBitmap
import com.one_of_many_simons.one_button_game.Data.Libraries.graphics
import com.one_of_many_simons.one_button_game.Debug
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.entities.player.Player.Companion.getHeal
import com.one_of_many_simons.one_button_game.graphics.Graphics.Companion.ARROW_LAYER
import com.one_of_many_simons.one_button_game.graphics.Icons

/**
 * Class containing information about usable items inside players inventory
 */
class Item {
    private val icon: ImageBitmap
    val isNull: Boolean
    private var heal = 0

    constructor(icon: ImageBitmap) {
        if (Debug.Entities.Player.ITEM) println("--- [Item.constructor]")

        this.icon = icon
        isNull = false
    }

    constructor(icon: ImageBitmap, isNull: Boolean) {
        if (Debug.Entities.Player.ITEM) println("--- [Item.constructor]")

        this.icon = icon
        this.isNull = isNull
    }

    /**
     * Drawing inventory
     *
     * @param position that item will be drawn
     */
    fun draw(position: Position) {
        if (Debug.Entities.Player.ITEM) println("--- [Item.draw]")

        if (!isNull) {
            Icons.LevelEditor.cursor.let { graphics.drawTile(position, it, ARROW_LAYER) }
        }
        graphics.drawTile(position, icon, ARROW_LAYER)
    }

    /**
     * Applying all effects, that this item has
     */
    fun applyEffects() {
        if (Debug.Entities.Player.ITEM) println("--- [Item.applyEffects]")

        getHeal(heal)
    }

    fun setHeal(heal: Int) {
        if (heal < 0) throw RuntimeException("Heal cannot be negative")
        this.heal = heal
    }
}
