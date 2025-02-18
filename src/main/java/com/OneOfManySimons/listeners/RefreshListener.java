package com.OneOfManySimons.listeners;

import java.awt.*;

/**
 * Listener, used for calling after refreshing screen.
 */
public interface RefreshListener {
	void onRefresh();

	Point getPosition();
}
