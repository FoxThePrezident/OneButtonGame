package com.one_of_many_simons.one_button_game.dataClasses

class Colour {
    @JvmField
    var red: Int = 0

    @JvmField
    var green: Int = 0

    @JvmField
    var blue: Int = 0

    constructor(red: Int, green: Int, blue: Int) {
        this.red = red
        this.green = green
        this.blue = blue
    }
}