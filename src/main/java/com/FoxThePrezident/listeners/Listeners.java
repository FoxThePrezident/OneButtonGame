package com.FoxThePrezident.listeners;

import com.FoxThePrezident.Debug;

import java.util.ArrayList;

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
	public void addRefreshListener(RefreshListener toAdd) {
		if (Debug.listeners.Listeners) System.out.println("--- [Graphics.addListener]");
		refreshListeners.add(toAdd);
	}

	/**
	 * Resetting listeners to nothing
	 */
	public void clearListeners() {
		if (Debug.listeners.Listeners) System.out.println("--- [Graphics.clearListeners]");
		refreshListeners = new ArrayList<>();
		refreshListenersRemove = new ArrayList<>();
	}

	/**
	 * Removing listener and preventing it from screen refresh calling.
	 *
	 * @param toRemove class that will be romed from notification
	 */
	public void removeRefreshListener(RefreshListener toRemove) {
		if (Debug.listeners.Listeners) System.out.println("--- [Graphics.removeListener]");
		refreshListenersRemove.add(toRemove);
	}

	/**
	 * Removing listener and preventing it from screen refresh calling.
	 *
	 * @param toRemove class that will be romed from notification
	 * @param removeNow if that listener needs to be removed instantly, or after notifying all other listeners
	 */
	public void removeRefreshListener(RefreshListener toRemove, boolean removeNow) {
		if (Debug.listeners.Listeners) System.out.println("--- [Graphics.removeListener]");
		if (removeNow) {
			refreshListeners.remove(toRemove);
		} else {
			removeRefreshListener(toRemove);
		}
	}

	/**
	 * Removing listener and preventing it from screen refresh calling.
	 *
	 * @param position formatted like {@code [y, x]} of listener, that we want to remove
	 */
	public void removeRefreshListener(int[] position) {
		if (Debug.listeners.Listeners) System.out.println("--- [Graphics.removeListener]");

		// Looping throughout listeners array and finding, which listener is having same position, as we want to remove.
		for (RefreshListener listener : refreshListeners) {
			int[] listenerPosition = listener.getPosition();
			if (listenerPosition != null) {
				if ((listenerPosition[0] == position[0]) && (listenerPosition[1] == position[1])) {
					refreshListenersRemove.add(listener);
					// We found that listener, there is no point of continuing
					return;
				}
			}
		}
	}

	/**
	 * Calling listener that screen got refreshed.
	 */
	public void callRefreshListeners() {
		if (Debug.listeners.Listeners) System.out.println("--- [Graphics.callListeners]");
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
