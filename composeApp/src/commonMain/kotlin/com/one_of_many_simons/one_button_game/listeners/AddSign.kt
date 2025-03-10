package com.one_of_many_simons.one_button_game.listeners

import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Data.Libraries.graphics
import com.one_of_many_simons.one_button_game.Data.Libraries.listeners
import com.one_of_many_simons.one_button_game.Debug
import com.one_of_many_simons.one_button_game.dataClasses.Interactive
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.entities.templates.Sign

fun addSignMap() {
    if (Debug.Listeners.TEXT_INPUT_LISTENER) println(">>> [TextInputListener.actionPerformed]")

    val text = Data.Libraries.textInputListeners.getText()

    // Getting coordinates
    val position = Position(Data.Player.position)

    // Creating JSON object for a sign
    val sign = Interactive()
    sign.entityType = "sign"
    sign.position = Position(position)
    sign.text = text
    Data.Map.interactive.add(sign)

    // Hiding text input
    Data.Libraries.textInputListeners.hide()

    listeners.addRefreshListener(Sign(Position(position), text))

    // Updating screen
    graphics.refreshScreen()

    if (Debug.Listeners.TEXT_INPUT_LISTENER) println("<<< [TextInputListener.actionPerformed]")
}