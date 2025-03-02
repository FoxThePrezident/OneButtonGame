package com.one_of_many_simons.one_button_game.listeners

import com.one_of_many_simons.one_button_game.Data
import com.one_of_many_simons.one_button_game.Data.Libraries.graphics
import com.one_of_many_simons.one_button_game.Data.Libraries.listeners
import com.one_of_many_simons.one_button_game.Debug
import com.one_of_many_simons.one_button_game.TextInput
import com.one_of_many_simons.one_button_game.dataClasses.Interactive
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.entities.templates.Sign
import java.awt.event.ActionEvent
import java.awt.event.ActionListener

/**
 * Listener for submitting text to a sign.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class TextInputListener : ActionListener {
    override fun actionPerformed(e: ActionEvent) {
        if (Debug.Listeners.TEXT_INPUT_LISTENER) println(">>> [TextInputListener.actionPerformed]")

        val text = TextInput.getText()

        // Getting coordinates
        val position = Position(Data.Player.position)

        // Creating JSON object for a sign
        val sign = Interactive()
        sign.entityType = "sign"
        sign.position = Position(position)
        sign.text = text
        Data.Map.interactive.add(sign)

        // Hiding text input
        TextInput.dispose()

        listeners.addRefreshListener(Sign(Position(position), text))

        // Updating screen
        graphics.refreshScreen()

        if (Debug.Listeners.TEXT_INPUT_LISTENER) println("<<< [TextInputListener.actionPerformed]")
    }
}
