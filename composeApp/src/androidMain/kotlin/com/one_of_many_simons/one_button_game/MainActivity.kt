package com.one_of_many_simons.one_button_game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.one_of_many_simons.one_button_game.Libraries.fileHandle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fileHandle.setContext(this)

        setContent {
            app()
        }
    }
}

@Preview
@Composable
fun appAndroidPreview() {
    app()
}