package com.one_of_many_simons.one_button_game.entities.templates

import androidx.compose.ui.graphics.ImageBitmap
import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Debug.Flags.Entities.Templates.ENEMY
import com.one_of_many_simons.one_button_game.Debug.Levels.CORE
import com.one_of_many_simons.one_button_game.Debug.Levels.INFORMATION
import com.one_of_many_simons.one_button_game.Debug.debug
import com.one_of_many_simons.one_button_game.Libraries.collisions
import com.one_of_many_simons.one_button_game.Libraries.graphics
import com.one_of_many_simons.one_button_game.Libraries.listeners
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.entities.player.Player
import com.one_of_many_simons.one_button_game.graphics.Graphics.Companion.DECOR_LAYER
import com.one_of_many_simons.one_button_game.graphics.Graphics.Companion.ENTITIES_LAYER
import com.one_of_many_simons.one_button_game.graphics.Icons
import com.one_of_many_simons.one_button_game.listeners.RefreshListener
import kotlin.math.sqrt

/**
 * Movable entity.<br></br>
 * Track the shortest path to the player
 */
open class Enemy(position: Position) : RefreshListener {
    // Movement
    private val detectionRange: Int = 3
    private val directions: Array<Position> = arrayOf(Position(0, -1), Position(1, 0), Position(0, 1), Position(-1, 0))

    @JvmField
    var position: Position = Position()

    @JvmField
    protected var icon: ImageBitmap? = null
    private var health: Int = 10

    /**
     * Tell how often enemy will move.
     * For example move once and skip twice
     * It is max threshold
     */
    @JvmField
    protected var movementDelay: Int = 2

    /**
     * Tell current move since enemy moved
     */
    private var movementNumber: Int = 0
    private var directionIndex: Int = 0

    init {
        debug(ENEMY, CORE, "--- [Enemy.constructor]")

        this.position = Position(position)
    }

    private val nextPosition: Position
        /**
         * Looking to the future, what place I will occupy if I go there.
         *
         * @return int pair of the next position
         */
        get() {
            debug(ENEMY, INFORMATION, "--- [Enemy.getNextPosition]")

            val y = position.y + directions[directionIndex].y
            val x = position.x + directions[directionIndex].x
            return Position(x, y)
        }

    /**
     * Getting distance to a player from the next position.
     *
     * @return double of that distance
     */
    private fun getDistance(): Double {
        debug(ENEMY, INFORMATION, "--- [Enemy.getDistance]")

        val nextPosition = nextPosition
        val deltaY = Data.Player.position.y - nextPosition.y
        val deltaX = Data.Player.position.x - nextPosition.x

        // Use Pythagoras' theorem to calculate the distance
        return sqrt((deltaY * deltaY + deltaX * deltaX).toDouble())
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
        if (getDistance() > detectionRange) {
            graphics.drawTile(position, icon, ENTITIES_LAYER)
            return
        }

        // Controlling, if enemy could move
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

        var distanceToPlayer = Double.MAX_VALUE
        var direction = 0

        // Looping over each direction
        debug(ENEMY, INFORMATION, "--- [Enemy.onRefresh] Getting shortest path to the player")

        directionIndex = 0
        while (directionIndex <= 3) {
            // Checking, if that is a valid place
            val nextPosition = nextPosition
            val nextTile: ImageBitmap = graphics.getTile(nextPosition)
            val couldMove: Int = collisions.checkForCollision(nextTile)
            if (couldMove == collisions.immovable) {
                directionIndex++
                continue
            }

            // Getting distance and checking if it is closer to a player
            val distance = getDistance()
            if (distance < distanceToPlayer) {
                distanceToPlayer = distance
                direction = directionIndex
            }
            directionIndex++
        }

        // Storing that direction
        directionIndex = direction
        position = nextPosition

        if (checkForCollision()) return

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
