package com.one_of_many_simons.one_button_game

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "One Button Game") {
        App()
    }
}
