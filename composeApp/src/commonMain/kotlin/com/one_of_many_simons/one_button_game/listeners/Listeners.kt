package com.one_of_many_simons.one_button_game.listeners

import com.one_of_many_simons.one_button_game.Debug
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
        if (Debug.Listeners.LISTENERS) println("--- [Listeners.addListener]")

        refreshListeners.add(toAdd)
    }

    /**
     * Resetting listeners to nothing
     */
    fun clearListeners() {
        if (Debug.Listeners.LISTENERS) println("--- [Listeners.clearListeners]")

        refreshListeners = ArrayList()
        refreshListenersRemove = ArrayList()
    }

    /**
     * Removing listener and preventing it from screen refresh calling.
     *
     * @param toRemove class that will be romed from notification
     */
    fun removeRefreshListener(toRemove: RefreshListener) {
        if (Debug.Listeners.LISTENERS) println("--- [Listeners.removeListener]")

        refreshListenersRemove.add(toRemove)
    }

    /**
     * Removing listener and preventing it from screen refresh calling.
     *
     * @param toRemove  class that will be romed from notification
     * @param removeNow if that listener needs to be removed instantly, or after notifying all other listeners
     */
    fun removeRefreshListener(toRemove: RefreshListener, removeNow: Boolean) {
        if (Debug.Listeners.LISTENERS) println("--- [Listeners.removeListener]")

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
        if (Debug.Listeners.LISTENERS) println("--- [Listeners.removeListener]")

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
        if (Debug.Listeners.LISTENERS) println("--- [Listeners.callListeners]")

        // Notify all listeners
        for (listener in refreshListeners.toList()) {
            listener.onRefresh()
        }

        // Remove listeners marked for removal
        refreshListeners.removeAll(refreshListenersRemove)
        refreshListenersRemove.clear()

        // Debugging output
//        println("Still present listeners")
//        for (listener in refreshListeners) {
//            println(listener)
//        }
//        println("End of listeners")
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
