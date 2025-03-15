package com.one_of_many_simons.one_button_game.listeners

import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Debug.Flags.Listeners.NEW_MAP_LISTENER
import com.one_of_many_simons.one_button_game.Debug.Levels.CORE
import com.one_of_many_simons.one_button_game.Debug.debug
import com.one_of_many_simons.one_button_game.Libraries.fileHandle
import com.one_of_many_simons.one_button_game.Libraries.menuCommands
import com.one_of_many_simons.one_button_game.Libraries.textInputListeners
import java.io.IOException

/**
 * Listener for new map name input from text input listener
 */
fun newMapListener() {
    debug(NEW_MAP_LISTENER, CORE, "--- [newMapListener]")

    try {
        val mapName = textInputListeners.getText().replace("\n", "")
        val newMap: String = fileHandle.loadText("json/templates/map.json", true)!!
        fileHandle.saveText("/maps/$mapName.json", newMap)
        Data.Map.currentMap = mapName
        menuCommands.newMapLevelEdit(mapName)

        textInputListeners.hide()
    } catch (ex: IOException) {
        throw RuntimeException(ex)
    }
}