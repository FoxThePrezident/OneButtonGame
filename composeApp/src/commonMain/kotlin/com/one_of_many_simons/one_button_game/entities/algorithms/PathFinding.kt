package com.one_of_many_simons.one_button_game.entities.algorithms

import androidx.compose.ui.graphics.ImageBitmap
import com.one_of_many_simons.one_button_game.Debug.Flags.Entities.Algorithms.PATH_FINDING
import com.one_of_many_simons.one_button_game.Debug.Levels.INFORMATION
import com.one_of_many_simons.one_button_game.Debug.Levels.CORE
import com.one_of_many_simons.one_button_game.Debug.debug
import com.one_of_many_simons.one_button_game.Libraries.collisions
import com.one_of_many_simons.one_button_game.Libraries.graphics
import com.one_of_many_simons.one_button_game.dataClasses.Position
import kotlin.math.sqrt

class PathFinding {
    private val directions: Array<Position> = arrayOf(Position(0, -1), Position(1, 0), Position(0, 1), Position(-1, 0))

    /**
     * Algorithm for movement
     * @param startPoint caller position
     * @param endPosition target position
     * @return direction index that will get closer to player. If it is -1, means that entity is too close to it`s destination
     */
    fun toPoint(startPoint: Position, endPosition: Position): Int {
        return toPoint(startPoint, endPosition, 0)
    }

    /**
     * Algorithm for movement
     * @param startPoint caller position
     * @param endPosition target position
     * @param keepDistance for not allowing original entity to get too close to target position
     * @return direction index that will get closer to player. If it is -1, means that entity is too close to it`s destination
     */
    fun toPoint(startPoint: Position, endPosition: Position, keepDistance: Int): Int {
        debug(PATH_FINDING, CORE, ">>> [PathFinding.toPoint]")

        var distanceToEnd = Double.MAX_VALUE
        var directionIndex = 0
        var direction = -1

        while (directionIndex <= 3) {
            // Checking, if that is a valid place
            val nextPosition = getNextPosition(startPoint, directionIndex)
            val nextTile: ImageBitmap = graphics.getTile(nextPosition)
            val couldMove: Int = collisions.checkForCollision(nextTile)
            if (couldMove == collisions.immovable) {
                directionIndex++
                continue
            }

            // Getting distance and checking if it is closer to a player
            val distance = getDistance(nextPosition, endPosition)
            if ((distance < distanceToEnd) && (distance > keepDistance)) {
                distanceToEnd = distance
                direction = directionIndex
            }
            directionIndex++
        }

        debug(PATH_FINDING, CORE, "<<< [PathFinding.toPoint]")
        return direction
    }

    /**
     * Getting distance to a player from the next position.
     *
     * @return double of that distance
     */
    fun getDistance(startPoint: Position, endPosition: Position): Double {
        debug(PATH_FINDING, INFORMATION, "--- [PathFinding.getDistance]")

        val deltaY = startPoint.y - endPosition.y
        val deltaX = startPoint.x - endPosition.x

        // Use Pythagoras' theorem to calculate the distance
        return sqrt((deltaY * deltaY + deltaX * deltaX).toDouble())
    }

    /**
     * Get position if position moved based on direction index
     */
    fun getNextPosition(position: Position, direction: Int): Position {
        debug(PATH_FINDING, INFORMATION, "--- [PathFinding.getNextPosition]")

        val y = position.y + directions[direction].y
        val x = position.x + directions[direction].x
        return Position(x, y)
    }
}