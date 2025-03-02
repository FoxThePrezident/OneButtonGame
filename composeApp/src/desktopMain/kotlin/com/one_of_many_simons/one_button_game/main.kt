package com.one_of_many_simons.one_button_game

fun main() {
    if (Debug.LAUNCHER) println(">>> [LAUNCHER.main]")

    // Initializing main components
    val launcher = Launcher()
    launcher.init()

    if (Debug.LAUNCHER) println("<<< [LAUNCHER.main]")
}