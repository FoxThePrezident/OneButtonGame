package com.one_of_many_simons.one_button_game.utils

import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Data.Libraries.listeners
import com.one_of_many_simons.one_button_game.Data.loadInteractive
import com.one_of_many_simons.one_button_game.Debug
import com.one_of_many_simons.one_button_game.Launcher
import com.one_of_many_simons.one_button_game.dataClasses.Position

/**
 * Managing map related stuff.<br></br>
 * Like constructing map, deconstructing and map shifting.
 */
class MapUtils {
    /**
     * Constructing map from position of individual things.<br></br>
     * From:
     * <pre>`{
     * "walls":[
     * [0, 0],
     * [0, 1],
     * ...
     * ]}
    `</pre> *
     * To:
     * <pre>`[
     * ["W", "W", "W"],
     * ["W", " ", "W"],
     * ["W", "W", "W"]
     * ]`</pre>
     *
     * @return ArrayList of constructed map
     */
    fun constructMap(): ArrayList<ArrayList<String>> {
        if (Debug.Utils.MAP_UTILS) println(">>> [MapUtils.constructMap]")

        val map = ArrayList<ArrayList<String>>()

        // Putting walls to map
        val walls = Data.Map.walls
        if (Debug.Utils.MAP_UTILS) println("--- [MapUtils.constructMap] Putting walls to map")
        for (wall in walls) {
            val row = getRow(map, wall.y)

            // Ensure the row has enough size
            while (row.size <= wall.x) {
                row.add("") // Add empty space until the row is large enough
            }

            row[wall.x] = "W" // Safely set the value at the desired index
        }

        // Putting ground to map
        if (Debug.Utils.MAP_UTILS) println("--- [MapUtils.constructMap] Putting ground to map")
        val grounds = Data.Map.ground
        for (ground in grounds) {
            val row = getRow(map, ground.y)

            // Ensure the row has enough size
            while (row.size <= ground.x) {
                row.add("") // Add empty space until the row is large enough
            }

            row[ground.x] = " " // Safely set the value at the desired index
        }

        if (Debug.Utils.MAP_UTILS) println("<<< [MapUtils.constructMap]")

        return map
    }

    /**
     * Destructuring map to better formation to be stored in a file.<br></br>
     * From:
     * <pre>`[
     * ["W", "W", "W"],
     * ["W", " ", "W"],
     * ["W", "W", "W"]
     * ]`</pre>
     * To:
     * <pre>`{
     * "walls":[
     * [0, 0],
     * [0, 1],
     * ...
     * ]}
    `</pre> *
     */
    fun deconstructMap() {
        if (Debug.Utils.MAP_UTILS) println(">>> [MapUtils.deconstructMap]")

        // Create a JSONObject to store the map data
        val walls = ArrayList<Position>()
        val ground = ArrayList<Position>()
        for (y in Data.map!!.indices) {
            val row: java.util.ArrayList<String> = Data.map!![y]
            for (x in Data.map!![y].indices) {
                val element = row[x]
                // Process the element
                val tile = Position(x, y)
                // Checking, which element is on a map
                when (element) {
                    "W" -> walls.add(tile)
                    " " -> ground.add(tile)
                }
            }
        }

        // Storing data
        Data.Map.walls = walls
        Data.Map.ground = ground

        if (Debug.Utils.MAP_UTILS) println("<<< [MapUtils.deconstructMap]")
    }

    /**
     * Creating new rows for a map if needed.
     *
     * @param map      from which we want a row
     * @param rowIndex which we want
     * @return JSONArray of the row we want
     */
    private fun getRow(map: ArrayList<ArrayList<String>>, rowIndex: Int): ArrayList<String> {
        if (Debug.Utils.MAP_UTILS) println("--- [MapUtils.getRow]")

        while (map.size <= rowIndex) {
            map.add(ArrayList()) // Add a new row if it doesn't exist
        }
        return map[rowIndex]
    }

    /**
     * Shifting map to be able to place things on "negative" indexes of a map.
     *
     * @param toShift formated like `[y, x]`, where y, x >= 0.
     */
    fun shiftMap(toShift: Position) {
        if (Debug.Utils.MAP_UTILS) println(">>> [MapUtils.shiftMap]")

        val newMap = ArrayList<ArrayList<String>>()
        var maxRowNum = 0

        // Row shifting
        for (y in Data.map!!.size - 1 downTo toShift.y) {
            var row: java.util.ArrayList<String>
            if (y < 0) {
                row = java.util.ArrayList()
                for (x in 0..maxRowNum) {
                    row.add("")
                }
            } else {
                // Column shifting
                row = java.util.ArrayList<String>(Data.map!![y]) // Create a copy
                if (toShift.x < 0) {
                    for (x in row.size - 1 downTo toShift.x) {
                        while (x - toShift.x >= row.size) row.add("")
                        if (x < 0) {
                            row[x - toShift.x] = ""
                        } else {
                            row[x - toShift.x] = row[x]
                        }
                    }
                    if (row.size - 1 > maxRowNum) maxRowNum = row.size - 1
                }
            }
            newMap.add(0, row) // Add to new map
        }

        // Replace old map with shifted map
        Data.map = newMap

        // Update player and editor positions
        Data.Player.position.x -= toShift.x
        Data.Player.position.y -= toShift.y
        Data.LevelEditor.holdPosition.x -= toShift.x
        Data.LevelEditor.holdPosition.y -= toShift.y

        // Shift interactive objects
        if (Debug.Utils.MAP_UTILS) println("--- [MapUtils.shiftMap] Shifting interactive things")
        Data.Map.interactive.forEach { interactive ->
            interactive.position.x -= toShift.x
            interactive.position.y -= toShift.y
        }

        // Refresh entities
        listeners.clearListeners()
        listeners.addRefreshListener(Launcher.player!!)
        listeners.addRefreshListener(Launcher.levelEditor!!)
        loadInteractive()

        if (Debug.Utils.MAP_UTILS) println("<<< [MapUtils.shiftMap]")
    }

}
