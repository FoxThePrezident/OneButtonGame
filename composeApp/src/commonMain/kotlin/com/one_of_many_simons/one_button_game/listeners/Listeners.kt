package com.one_of_many_simons.one_button_game.listeners

import com.one_of_many_simons.one_button_game.Debug.Flags.Listeners.LISTENERS
import com.one_of_many_simons.one_button_game.Debug.Levels.CORE
import com.one_of_many_simons.one_button_game.Debug.debug
import com.one_of_many_simons.one_button_game.dataClasses.Position

/**
 * Class containing manipulation of listeners
 */
class Listeners {
    /**
     * Adding listeners that will be called on screen refresh.
     *
     * @param toAdd class that will be notified
     */
    fun addRefreshListener(toAdd: RefreshListener) {
        debug(LISTENERS, CORE, "--- [Listeners.addListener]")

        refreshListeners.add(toAdd)
    }

    /**
     * Resetting listeners to nothing
     */
    fun clearListeners() {
        debug(LISTENERS, CORE, "--- [Listeners.clearListeners]")

        refreshListeners = ArrayList()
        refreshListenersRemove = ArrayList()
    }

    /**
     * Removing listener and preventing it from screen refresh calling.
     *
     * @param toRemove class that will be romed from notification
     */
    fun removeRefreshListener(toRemove: RefreshListener) {
        debug(LISTENERS, CORE, "--- [Listeners.removeRefreshListener]")

        refreshListenersRemove.add(toRemove)
    }

    /**
     * Removing listener and preventing it from screen refresh calling.
     *
     * @param toRemove  class that will be romed from notification
     * @param removeNow if that listener needs to be removed instantly, or after notifying all other listeners
     */
    fun removeRefreshListener(toRemove: RefreshListener, removeNow: Boolean) {
        debug(LISTENERS, CORE, "--- [Listeners.removeRefreshListener]")

        if (removeNow) {
            refreshListeners.remove(toRemove)
        } else {
            removeRefreshListener(toRemove)
        }
    }

    /**
     * Removing listener and preventing it from screen refresh calling.
     *
     * @param position formatted like `[y, x]` of listener, that we want to remove
     */
    fun removeRefreshListener(position: Position) {
        debug(LISTENERS, CORE, "--- [Listeners.removeRefreshListener]")

        // Looping throughout listeners array and finding, which listener is having same position, as we want to remove.
        for (listener in refreshListeners) {
            val listenerPosition = listener.getPosition()
            if (listenerPosition != null) {
                if (position.equals(listenerPosition)) {
                    println("Preparing to remove: $listener")
                    refreshListenersRemove.add(listener)
                    // We found that listener, there is no point of continuing
                    return
                }
            }
        }
    }

    /**
     * Calling listener that screen got refreshed.
     */
    fun callRefreshListeners() {
        debug(LISTENERS, CORE, "--- [Listeners.callRefreshListeners]")

        // Notify all listeners
        for (listener in refreshListeners.toList()) {
            listener.onRefresh()
        }

        // Remove listeners marked for removal
        refreshListeners.removeAll(refreshListenersRemove)
        refreshListenersRemove.clear()
    }

    companion object {
        /**
         * Array of listeners that are called after refreshing screen
         */
        private var refreshListeners: MutableList<RefreshListener> = mutableListOf()

        /**
         * List of listeners for removal.<br></br>
         * If listener wants to delete itself (like enemy is out of HP), it will be stored in own array.
         * So we could prevent array shifting when we are still using it.
         */
        private var refreshListenersRemove: MutableList<RefreshListener> = mutableListOf()
    }
}
