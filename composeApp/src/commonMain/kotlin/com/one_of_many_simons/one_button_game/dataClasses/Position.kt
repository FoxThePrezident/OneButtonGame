package com.one_of_many_simons.one_button_game.dataClasses

class Position {
    var x: Int = 0
    var y: Int = 0

    constructor() {
        x = 0
        y = 0
    }

    constructor(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    constructor(position: Position) {
        this.x = position.x
        this.y = position.y
    }

    @Suppress("CovariantEquals")
    fun equals(position: Position): Boolean {
        return x == position.x && y == position.y
    }
}