package com.one_of_many_simons.one_button_game.entities.templates

import androidx.compose.ui.graphics.ImageBitmap
import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Debug.Flags.Entities.Templates.ENEMY
import com.one_of_many_simons.one_button_game.Debug.Levels.CORE
import com.one_of_many_simons.one_button_game.Debug.Levels.INFORMATION
import com.one_of_many_simons.one_button_game.Debug.debug
import com.one_of_many_simons.one_button_game.Libraries.graphics
import com.one_of_many_simons.one_button_game.Libraries.listeners
import com.one_of_many_simons.one_button_game.Libraries.pathFinding
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.entities.player.Player
import com.one_of_many_simons.one_button_game.graphics.Graphics.Companion.DECOR_LAYER
import com.one_of_many_simons.one_button_game.graphics.Graphics.Companion.ENTITIES_LAYER
import com.one_of_many_simons.one_button_game.graphics.Icons
import com.one_of_many_simons.one_button_game.listeners.RefreshListener

/**
 * Movable entity.<br></br>
 * Track the shortest path to the player
 */
open class Enemy(position: Position) : RefreshListener {
    // ********************
    // ***** Movement *****
    // ********************

    /**
     * Current enemy position
     */
    @JvmField
    var position: Position = Position()

    /**
     * Range from which enemy will detect player
     */
    var detectionRange: Int = 3

    /**
     * Distance that will enemy try to keep from player
     */
    var keepDistance = 0

    /**
     * Which way is enemy facing
     */
    private var directionIndex: Int = 0

    /**
     * Tell how often enemy will move.
     * For example move once and skip twice.
     * It is max threshold
     */
    @JvmField
    protected var movementDelay: Int = 2

    /**
     * Tell current move since enemy moved
     */
    private var movementNumber: Int = 0

    // ***********************
    // ***** Projectiles *****
    // ***********************

    /**
     * If this entity have ability to shoot projectiles towards player
     */
    var couldFireProjectile: Boolean = false

    /**
     * Max threshold for how often enemy could fire a projectile towards enemy
     */
    var projectileDelay = 0

    /**
     * Current move since enemy fired
     */
    private var projectileNumber: Int = 0

    // *******************
    // ***** General *****
    // *******************

    @JvmField
    protected var icon: ImageBitmap? = null

    /**
     * Enemy health
     */
    var health: Int = 10

    init {
        debug(ENEMY, CORE, "--- [Enemy.constructor]")

        this.position = Position(position)
    }

    /**
     * Helper function for checking, if player and enemy are touching.
     *
     * @return yes if they share same space, no otherwise
     */
    private fun checkForCollision(): Boolean {
        debug(ENEMY, CORE, "--- [Enemy.checkForCollision]")

        if (Data.Player.position.equals(position)) {
            val tempHealth = health
            health -= Player.health
            Player.getDamage(tempHealth)
            Data.Map.enemyCount--
            return true
        }
        return false
    }

    /**
     * FUnction managing movement of the enemy.
     */
    private fun move() {
        debug(ENEMY, CORE, ">>> [Enemy.move]")

        // Checking, if player is outside of detection range
        if (pathFinding.getDistance(position, Data.Player.position) > detectionRange) {
            graphics.drawTile(position, icon, ENTITIES_LAYER)
            return
        }

        // Projectile
        if (couldFireProjectile) {
            debug(ENEMY, INFORMATION, "--- [Enemy.move] Firing projectile")
            projectileNumber++

            // Overflow check
            if (projectileNumber >= projectileDelay) {
                projectileNumber = 0
            }

            // Drawing attention right before entity shoots
            if (projectileNumber == projectileDelay - 1) {
                val attentionPosition = Position(position.x, position.y - 1)
                graphics.drawTile(attentionPosition, Icons.General.attention, DECOR_LAYER)
            }

            // Spawn projectile
            if (projectileNumber == 0) {
                val projectile = Projectile(position, Data.Player.position)
                listeners.addRefreshListener(projectile)

                // Drawing entity
                graphics.drawTile(position, icon, ENTITIES_LAYER)

                // Preventing multiple actions from occurring
                return
            }
        }

        // Controlling, if enemy could move
        debug(ENEMY, INFORMATION, "--- [Enemy.move] Moving")
        movementNumber++

        // Overflow check
        if (movementNumber >= movementDelay) {
            movementNumber = 0
        }

        // Signals to player, that enemy is ready to attack
        if (movementNumber == movementDelay - 1) {
            val attentionPosition = Position(position.x, position.y - 1)
            graphics.drawTile(attentionPosition, Icons.General.attention, DECOR_LAYER)
        }

        // Case when enemy cannot move
        if (movementNumber != 0) {
            graphics.drawTile(position, icon, ENTITIES_LAYER)
            return
        }

        // Storing that direction
        directionIndex = pathFinding.toPoint(position, Data.Player.position, keepDistance)
        if (directionIndex == -1) {
            graphics.drawTile(position, icon, ENTITIES_LAYER)
            debug(ENEMY, CORE, "<<< [Enemy.move]")
            return
        }
        position = pathFinding.getNextPosition(position, directionIndex)

        if (checkForCollision()) {
            debug(ENEMY, CORE, "<<< [Enemy.move]")
            return
        }

        // Drawing entity
        graphics.drawTile(position, icon, ENTITIES_LAYER)

        debug(ENEMY, CORE, "<<< [Enemy.move]")
    }

    override fun onRefresh() {
        debug(ENEMY, CORE, ">>> [Enemy.onRefresh]")

        // For case of level editor cursor is on top.
        if (!Data.LevelEditor.levelEdit) {
            if (checkForCollision()) {
                return
            }
        }

        // Checking, if it got enough health left
        if (health <= 0) {
            health = 0
            listeners.removeRefreshListener(this)

            debug(ENEMY, CORE, "<<< [Enemy.onRefresh] Early exit due to low enemy HP")
            return
        }

        if (Data.running) {
            move()
        } else {
            // Drawing entity
            graphics.drawTile(position, icon, ENTITIES_LAYER)
        }

        debug(ENEMY, CORE, "<<< [Enemy.onRefresh]")
    }

    override fun getPosition(): Position {
        debug(ENEMY, INFORMATION, "--- [Enemy.getPosition]")

        return position
    }
}