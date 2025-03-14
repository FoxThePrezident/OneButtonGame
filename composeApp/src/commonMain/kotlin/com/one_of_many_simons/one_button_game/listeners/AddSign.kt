package com.one_of_many_simons.one_button_game.listeners

import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Debug.Flags.Listeners.ADD_SIGN
import com.one_of_many_simons.one_button_game.Debug.Levels.CORE
import com.one_of_many_simons.one_button_game.Debug.debug
import com.one_of_many_simons.one_button_game.Libraries.graphics
import com.one_of_many_simons.one_button_game.Libraries.listeners
import com.one_of_many_simons.one_button_game.Libraries.textInputListeners
import com.one_of_many_simons.one_button_game.dataClasses.Interactive
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.entities.templates.Sign

/**
 * Add sign listener for Text Input Listener
 */
fun addSignMap() {
    debug(ADD_SIGN, CORE, ">>> [addSignMap]")

    val text = textInputListeners.getText()

    // Getting coordinates
    val position = Position(Data.Player.position)

    // Creating JSON object for a sign
    val sign = Interactive()
    sign.entityType = "sign"
    sign.position = Position(position)
    sign.text = text
    Data.Map.interactive.add(sign)

    // Hiding text input
    textInputListeners.hide()

    listeners.addRefreshListener(Sign(Position(position), text))

    // Updating screen
    graphics.refreshScreen()

    debug(ADD_SIGN, CORE, "<<< [addSignMap]")
}