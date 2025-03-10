package com.one_of_many_simons.one_button_game.listeners

import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Data.Libraries.fileHandle
import com.one_of_many_simons.one_button_game.Debug
import java.io.IOException

fun newMapListener() {
    if (Debug.Menu.MENU_COMMANDS) println("--- [MenuCommands.actionPerformed]")
    try {
        val mapName = Data.Libraries.textInputListeners.getText().replace("\n", "")
        val newMap: String = fileHandle.loadText("json/templates/map.json", true)!!
        fileHandle.saveText("/maps/$mapName.json", newMap)
        Data.Map.currentMap = mapName
        Data.Libraries.menuCommands.newMapLevelEdit(mapName)

        Data.Libraries.textInputListeners.hide()
    } catch (ex: IOException) {
        throw RuntimeException(ex)
    }
}