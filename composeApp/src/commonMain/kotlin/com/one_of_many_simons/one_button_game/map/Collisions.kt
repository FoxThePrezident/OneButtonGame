package com.one_of_many_simons.one_button_game.map

import androidx.compose.ui.graphics.ImageBitmap
import com.one_of_many_simons.one_button_game.Debug
import com.one_of_many_simons.one_button_game.graphics.Icons

/**
 * Collision related.<br></br>
 * Checking, if that tile have collisions or not.
 */
class Collisions {
    // Collision types
    val empty: Int = 0
    val immovable: Int = 1

    /**
     * Checking, if tile has a collision border.
     *
     * @param tile that we want to check
     * @return integer
     */
    fun checkForCollision(tile: ImageBitmap): Int {
        if (Debug.Map.COLLISIONS) println("--- [Collisions.checkForCollision]")

        if (tile == Icons.Environment.wall) {
            return immovable
        }
        if (tile == Icons.Environment.blank) {
            return immovable
        }
        return empty
    }
}
