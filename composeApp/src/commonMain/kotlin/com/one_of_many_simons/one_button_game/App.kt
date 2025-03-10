package com.one_of_many_simons.one_button_game

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.rememberTextMeasurer
import com.one_of_many_simons.one_button_game.dataClasses.Global

@Composable
fun App() {
    Global.textMeasurer = rememberTextMeasurer()
    val launcher = Launcher()
    launcher.init()
    Data.Libraries.graphics.render()
}
