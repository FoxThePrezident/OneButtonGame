package com.one_of_many_simons.one_button_game.entities.enemies

import com.one_of_many_simons.one_button_game.Debug.Flags.Entities.Enemies.SKELETON
import com.one_of_many_simons.one_button_game.Debug.Levels.CORE
import com.one_of_many_simons.one_button_game.Debug.debug
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.entities.templates.Enemy
import com.one_of_many_simons.one_button_game.graphics.Icons

/**
 * Skeleton entity that shoots arrows towards player
 */
class Skeleton(position: Position) : Enemy(position) {
    init {
        debug(SKELETON, CORE, "--- [Zombie.constructor]")

        detectionRange = 5
        keepDistance = 3
        movementDelay = 3

        couldFireProjectile = true
        projectileDelay = 5

        icon = Icons.Enemies.skeleton
        health = 5
    }
}