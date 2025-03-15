package com.one_of_many_simons.one_button_game.entities.potions

import com.one_of_many_simons.one_button_game.Debug.Flags.Entities.Potions
import com.one_of_many_simons.one_button_game.Debug.Levels.CORE
import com.one_of_many_simons.one_button_game.Debug.debug
import com.one_of_many_simons.one_button_game.dataClasses.Position
import com.one_of_many_simons.one_button_game.entities.templates.Potion
import com.one_of_many_simons.one_button_game.graphics.Icons

/**
 * Health potion
 */
class HP(position: Position) : Potion(position) {
    init {
        debug(Potions.HP, CORE, "--- [HP.constructor]")
        icon = Icons.Interactive.hp_potion
        heal = 10
    }
}
