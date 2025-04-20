package com.common.entities.algorithms;

import com.common.DataClasses.ImageWrapper;
import com.common.DataClasses.Position;
import com.common.map.Collisions;

import java.util.Arrays;
import java.util.List;

import static com.common.Data.graphics;
import static com.common.Debug.Flags.Entities.Algorithms.PATH_FINDING;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.Levels.INFORMATION;
import static com.common.Debug.debug;
import static java.lang.Math.sqrt;

public class PathFinding {
	public static final List<Position> DIRECTIONS = Arrays.asList(new Position(0, -1), new Position(1, 0), new Position(0, 1), new Position(-1, 0));

	/**
	 * Algorithm for movement
	 *
	 * @param startPoint  caller position
	 * @param endPosition target position
	 * @return direction index that will get closer to player. If it is -1, means that entity is too close to it`s destination
	 */
	public static int toPoint(Position startPoint, Position endPosition) {
		return toPoint(startPoint, endPosition, 0);
	}

	/**
	 * Algorithm for movement
	 *
	 * @param startPoint   caller position
	 * @param endPosition  target position
	 * @param keepDistance for not allowing original entity to get too close to target position
	 * @return direction index that will get closer to player. If it is -1, means that entity is too close to it`s destination
	 */
	public static int toPoint(Position startPoint, Position endPosition, int keepDistance) {
		debug(PATH_FINDING, CORE, ">>> [PathFinding.toPoint]");

		double distanceToEnd = Double.MAX_VALUE;
		int directionIndex = 0;
		int direction = -1;

		while (directionIndex <= 3) {
			// Checking, if that is a valid place
			Position nextPosition = getNextPosition(startPoint, directionIndex);
			ImageWrapper nextTile = graphics.getTile(nextPosition);
			int couldMove = Collisions.checkForCollision(nextTile);
			if (couldMove == Collisions.immovable) {
				directionIndex++;
				continue;
			}

			// Getting distance and checking if it is closer to a player
			double distance = getDistance(nextPosition, endPosition);
			if ((distance < distanceToEnd) && (distance > keepDistance)) {
				distanceToEnd = distance;
				direction = directionIndex;
			}
			directionIndex++;
		}

		debug(PATH_FINDING, CORE, "<<< [PathFinding.toPoint]");
		return direction;
	}

	/**
	 * Getting distance to a player from the next position.
	 *
	 * @return double of that distance
	 */
	public static double getDistance(Position startPoint, Position endPosition) {
		debug(PATH_FINDING, INFORMATION, "--- [PathFinding.getDistance]");

		int deltaY = startPoint.y - endPosition.y;
		int deltaX = startPoint.x - endPosition.x;

		// Use Pythagoras' theorem to calculate the distance
		return sqrt(deltaY * deltaY + deltaX * deltaX);
	}

	/**
	 * Get position if position moved based on direction index
	 */
	public static Position getNextPosition(Position position, int direction) {
		debug(PATH_FINDING, INFORMATION, "--- [PathFinding.getNextPosition]");

		int y = position.y + DIRECTIONS.get(direction).y;
		int x = position.x + DIRECTIONS.get(direction).x;
		return new Position(x, y);
	}
}