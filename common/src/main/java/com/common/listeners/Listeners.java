package com.common.listeners;

import com.common.DataClasses.Position;

import java.util.ArrayList;

import static com.common.Debug.Flags.Listeners.LISTENERS;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.debug;

/**
 * Class containing manipulation of listeners
 */
public class Listeners {
	/**
	 * Array of listeners that are called after refreshing screen
	 */
	private static ArrayList<RefreshListener> refreshListeners = new ArrayList<>();
	/**
	 * List of listeners for removal.<br>
	 * If listener wants to delete itself (like enemy is out of HP), it will be stored in own array.
	 * So we could prevent array shifting when we are still using it.
	 */
	private static ArrayList<RefreshListener> refreshListenersRemove = new ArrayList<>();

	/**
	 * Adding listeners that will be called on screen refresh.
	 *
	 * @param toAdd class that will be notified
	 */
	public static void addRefreshListener(RefreshListener toAdd) {
		debug(LISTENERS, CORE, "--- [Listeners.addListener]");

		refreshListeners.add(toAdd);
	}

	/**
	 * Resetting listeners to nothing
	 */
	public static void clearListeners() {
		debug(LISTENERS, CORE, "--- [Listeners.clearListeners]");

		refreshListeners = new ArrayList<>();
		refreshListenersRemove = new ArrayList<>();
	}

	/**
	 * Removing listener and preventing it from screen refresh calling.
	 *
	 * @param toRemove class that will be romed from notification
	 */
	public static void removeRefreshListener(RefreshListener toRemove) {
		debug(LISTENERS, CORE, "--- [Listeners.removeListener]");

		refreshListenersRemove.add(toRemove);
	}

	/**
	 * Removing listener and preventing it from screen refresh calling.
	 *
	 * @param position formatted like {@code [y, x]} of listener, that we want to remove
	 */
	public static void removeRefreshListener(Position position) {
		debug(LISTENERS, CORE, "--- [Listeners.removeListener]");

		// Looping throughout listeners array and finding, which listener is having same position, as we want to remove.
		for (RefreshListener listener : refreshListeners) {
			Position listenerPosition = listener.getPosition();
			if (listenerPosition != null) {
				if (position.equals(listenerPosition)) {
					refreshListenersRemove.add(listener);
					// We found that listener, there is no point of continuing
					return;
				}
			}
		}
	}

	/**
	 * Getter for listener
	 *
	 * @param position formatted like {@code [y, x]} of listener, that we want to remove
	 */
	public static RefreshListener getRefreshListener(Position position) {
		// Looping throughout listeners array and finding, which listener is having same position, as we want it to return.
		for (RefreshListener listener : refreshListeners) {
			Position listenerPosition = listener.getPosition();
			if (listenerPosition != null) {
				if (position.equals(listenerPosition)) {
					return listener;
				}
			}
		}
		return null;
	}

	/**
	 * Calling listener that screen got refreshed.
	 */
	public static void callRefreshListeners() {
		debug(LISTENERS, CORE, "--- [Listeners.callListeners]");

		// Looping over each listener
		for (int i = 0; i < refreshListeners.toArray().length; i++) {
			refreshListeners.get(i).onRefresh();
		}

		// Removing listeners, that need to be removed
		for (int i = 0; i < refreshListenersRemove.toArray().length; i++) {
			refreshListeners.remove(refreshListenersRemove.get(i));
		}
		// Clearing to remove listeners
		refreshListenersRemove = new ArrayList<>();
	}
}
