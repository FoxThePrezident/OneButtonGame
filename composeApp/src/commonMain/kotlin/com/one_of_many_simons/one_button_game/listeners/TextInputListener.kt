package com.one_of_many_simons.one_button_game.listeners

import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import com.one_of_many_simons.one_button_game.Data

class TextInputListener {
    private lateinit var action: () -> Unit
    private lateinit var canvasFocusRequester: FocusRequester
    private var textInput = mutableStateOf("")

    companion object {
        private var textInputVisibility = mutableStateOf(false)
    }

    fun show(action: () -> Unit) {
        textInputVisibility.value = true
        canvasFocusRequester = Data.Libraries.graphics.getFocus()
        this.action = action
    }

    fun hide() {
        textInputVisibility.value = false
        canvasFocusRequester.requestFocus()
    }

    fun getText(): String {
        return textInput.value.replace("\n", "")
    }

    @Composable
    fun getTextInput() {
        if (textInputVisibility.value) {
            return OutlinedTextField(
                value = textInput.value,
                onValueChange = { textInput.value = it },
                label = { Text("Enter Text") },
                modifier = Modifier
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