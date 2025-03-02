package com.one_of_many_simons.one_button_game.dataClasses

/**
 * Set containing player actions.
 */
class PlayerActionData {
    var name: String = ""
    var items: ArrayList<PlayerActionItem> = ArrayList()
}

/**
 * Specific player action, like movement, inventory, ...
 */
class PlayerActionItem {
    var action: String = ""
    var icon: String = ""
    var vector: Position = Position()
    var setName: String = ""
    var slot: Int = 0
    var menu: String = ""
}
