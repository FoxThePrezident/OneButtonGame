package com.common.listeners;

import com.common.DataClasses.Position;

/**
 * Listener, used for calling after refreshing screen.
 */
public interface RefreshListener {
	void onRefresh();
	void getEntityDamage(int damage);
	Position getPosition();
}
