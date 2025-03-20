package com.one_of_many_simons.one_button_game.entities.templates

import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Debug.debug
import com.one_of_many_simons.one_button_game.Debug.Levels.CORE
import com.one_of_many_simons.one_button_game.Debug.Flags.Entities.Templates.PROJECTILE
import com.one_of_many_simons.one_button_game.Debug.Levels.INFORMATION
import com.one_of_many_simons.one_button_game.Libraries.collisions
import com.one_of_many_simons.one_button_game.Libraries.graphics
import com.one_of_many_simons.one_button_game.Libraries.listeners
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.listeners.RefreshListener
import com.one_of_many_simons.one_button_game.Libraries.pathFinding
import com.one_of_many_simons.one_button_game.entities.player.Player
import com.one_of_many_simons.one_button_game.graphics.Graphics.Companion.DECOR_LAYER
import com.one_of_many_simons.one_button_game.graphics.Icons

class Projectile(position: Position, target: Position) : RefreshListener {
    private var position: Position
    private var directionIndex: Int
    private var icon = Icons.Interactive.projectile
    private var damage: Int = 5

    init {
        debug(PROJECTILE, CORE, "--- [Projectile.constructor]")

        this.position = position
        directionIndex = pathFinding.toPoint(position, target)

        onRefresh()
    }

    override fun onRefresh() {
        debug(PROJECTILE, CORE, ">>> [Projectile.onRefresh]")

        if (position.equals(Data.Player.position)) {
            Player.getDamage(damage)
            listeners.removeRefreshListener(this)
            return
        }

        position = pathFinding.getNextPosition(position, directionIndex)

        val couldMove = collisions.checkForCollision(graphics.getTile(position))
        if (couldMove == collisions.immovable) {
            listeners.removeRefreshListener(this)

            debug(PROJECTILE, CORE, "<<< [Projectile.onRefresh]")
            return
        }

        if (position.equals(Data.Player.position)) {
            Player.getDamage(damage)
            listeners.removeRefreshListener(this)

            debug(PROJECTILE, CORE, "<<< [Projectile.onRefresh]")
            return
        }

        graphics.drawTile(position, icon, DECOR_LAYER)

        debug(PROJECTILE, CORE, "<<< [Projectile.onRefresh]")
    }

    override fun getPosition(): Position {
        debug(PROJECTILE, INFORMATION, "--- [Projectile.getPosition]")

        return position
    }
}