package com.one_of_many_simons.one_button_game.entities.enemies

import com.one_of_many_simons.one_button_game.Debug.Flags.Entities.Enemies.ZOMBIE
import com.one_of_many_simons.one_button_game.Debug.Levels.CORE
import com.one_of_many_simons.one_button_game.Debug.debug
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.entities.templates.Enemy
import com.one_of_many_simons.one_button_game.graphics.Icons

/**
 * ZOMBIE entity
 */
class Zombie(position: Position) : Enemy(position) {
    init {
        debug(ZOMBIE, CORE, "--- [Zombie.constructor]")
        icon = Icons.Enemies.zombie
        movementDelay = 3
    }
}
