package com.one_of_many_simons.one_button_game.listeners

import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Data.Libraries.fileHandle
import com.one_of_many_simons.one_button_game.Debug
import com.one_of_many_simons.one_button_game.TextInput
import com.one_of_many_simons.one_button_game.menu.MenuCommands
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.io.IOException

class NewMapListener : ActionListener {
    /**
     * TEXT input listener for text input for creating new game
     *
     * @param e the event to be processed
     */
    override fun actionPerformed(e: ActionEvent) {
        if (Debug.Menu.MENU_COMMANDS) println("--- [MenuCommands.actionPerformed]")
        try {
            val mapName = TextInput.getText()
            val newMap: String = fileHandle.loadText("json/templates/map.json", true)!!
            fileHandle.saveText("/maps/$mapName.json", newMap)
            Data.Map.currentMap = "map"
            TextInput.dispose()
            MenuCommands.newMapLevelEdit(mapName)
        } catch (ex: IOException) {
            throw RuntimeException(ex)
        }
    }
}

actual fun openMapTextInput() {
    TextInput.open(NewMapListener())
}