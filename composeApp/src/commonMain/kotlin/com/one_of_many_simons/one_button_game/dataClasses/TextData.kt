package com.one_of_many_simons.one_button_game.dataClasses

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class TextData {
    var text: String = ""
    var textColor: Color = Color.White
    var backgroundColor: Color? = Color.DarkGray
    var borderColor: Color? = Color.Black
    var borderWidth: Dp = 2.dp
    var isCentered: Boolean = false
    var position: Position = Position()
}