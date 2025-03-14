package com.one_of_many_simons.one_button_game.listeners

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import com.one_of_many_simons.one_button_game.Debug
import com.one_of_many_simons.one_button_game.Libraries.graphics

/**
 * Handling getting text input from user
 */
class TextInputListener {
    private lateinit var action: () -> Unit
    private lateinit var canvasFocusRequester: FocusRequester
    private var textInput = mutableStateOf("")

    companion object {
        private var textInputVisibility = mutableStateOf(false)
    }

    /**
     * Opens listener for text input
     */
    fun show(action: () -> Unit) {
        if (Debug.Listeners.TEXT_INPUT_LISTENER) println("--- [TextInputListener.show]")
        textInputVisibility.value = true
        canvasFocusRequester = graphics.getFocus()
        this.action = action
    }

    /**
     * Hides text input
     */
    fun hide() {
        if (Debug.Listeners.TEXT_INPUT_LISTENER) println("--- [TextInputListener.hide]")

        textInputVisibility.value = false
        canvasFocusRequester.requestFocus()
    }

    /**
     * Get text from Text input
     * @return written text
     */
    fun getText(): String {
        if (Debug.Listeners.TEXT_INPUT_LISTENER) println("--- [TextInputListener.getText]")

        return textInput.value.replace("\n", "")
    }

    /**
     * Getter for Text input listener
     */
    @Composable
    fun getTextInput() {
        if (Debug.Listeners.TEXT_INPUT_LISTENER) println("--- [TextInputListener.getTextInput]")

        if (textInputVisibility.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)), // Black overlay with 50% opacity
                contentAlignment = Alignment.Center // Centers the text field
            ) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = 8.dp
                ) {
                    OutlinedTextField(
                        value = textInput.value,
                        onValueChange = { textInput.value = it },
                        label = { Text("Enter Text") },
                        modifier = Modifier
                            .padding(16.dp) // Padding inside the card
                            .onKeyEvent { event ->
                                if (event.key == Key.Enter) {
                                    action()
                                    hide()
                                    true
                                } else {
                                    false
                                }
                            }
                    )
                }
            }
        }
    }

    fun getVisibility(): Boolean {
        return textInputVisibility.value
    }
}