package com.one_of_many_simons.one_button_game.entities.enemies

import com.one_of_many_simons.one_button_game.Debug
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.entities.templates.Enemy
import com.one_of_many_simons.one_button_game.graphics.Icons

/**
 * ZOMBIE entity
 */
class Zombie(position: Position) : Enemy(position) {
    init {
        if (Debug.Entities.Enemies.ZOMBIE) println("--- [Zombie.constructor]")
        icon = Icons.Enemies.zombie
        movementDelay = 3
    }
}
