package com.one_of_many_simons.one_button_game.listeners

import com.one_of_many_simons.one_button_game.dataClasses.Position

/**
 * Listener, used for calling after refreshing screen.
 */
interface RefreshListener {
    fun onRefresh()

    fun getPosition(): Position?
}
