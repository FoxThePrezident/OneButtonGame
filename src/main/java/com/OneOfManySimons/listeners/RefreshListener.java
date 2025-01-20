package com.OneOfManySimons.listeners;

/**
 * Listener, used for calling after refreshing screen.
 */
public interface RefreshListener {
	void onRefresh();

	int[] getPosition();
}
