package com.one_of_many_simons.one_button_game.entities.player

import androidx.compose.ui.graphics.ImageBitmap
import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Debug.Flags.Entities.Player.ARMOR
import com.one_of_many_simons.one_button_game.Debug.Levels.CORE
import com.one_of_many_simons.one_button_game.Debug.Levels.INFORMATION
import com.one_of_many_simons.one_button_game.Debug.debug
import com.one_of_many_simons.one_button_game.Libraries.graphics
import com.one_of_many_simons.one_button_game.Libraries.listeners
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.graphics.Icons
import com.one_of_many_simons.one_button_game.listeners.RefreshListener
import com.one_of_many_simons.one_button_game.graphics.Graphics.Companion.ENTITIES_LAYER
import kotlin.math.roundToInt

class Armor(position: Position) : RefreshListener {
    /**
     * Armor health
     */
    private var health: Int = 10
    private var position: Position

    /**
     * How much armor will protect
     */
    private var blockPercentage: Float = 0.75f
        set(value) {
            debug(ARMOR, INFORMATION, "--- [Armor.setBlockPercentage]")

            if ((0f <= value) && (value <= 1f)) {
                field = value
            }
        }

    var icon: ImageBitmap = Icons.Interactive.armor

    init {
        this.position = position
    }

    /**
     * Apply damage to armor
     * @param damage that is dealt
     * @return Pair of Int and Boolean values. Int represent damage that was not blocked. Boolean represent, if armor was broken or not. True for armor was destroyed
     */
    fun getDamage(damage: Int): Pair<Int, Boolean> {
        debug(ARMOR, CORE, "--- [Armor.getDamage]")

        var destroyed = false

        health -= (damage * blockPercentage).roundToInt()
        var returnDamage = (damage*(1-blockPercentage)).roundToInt()

        // Check if armor is destroyed
        if (health <= 0) {
            destroyed = true
        }

        // Case if armor was not strong enough to hold all damage applied
        if (health < 0) {
            returnDamage -= health
        }

        return Pair(returnDamage, destroyed)
    }

    override fun onRefresh() {
        if (!Data.LevelEditor.levelEdit) {
            if (Data.Player.position.equals(position)) {
                Player.addArmor(this)
                listeners.removeRefreshListener(this)
                return
            }
        }

        graphics.drawTile(position, icon, ENTITIES_LAYER)
    }

    override fun getPosition(): Position {
        return position
    }
}