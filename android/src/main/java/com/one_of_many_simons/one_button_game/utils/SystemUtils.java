package com.one_of_many_simons.one_button_game.utils;

import androidx.appcompat.app.AppCompatActivity;

import static com.common.Debug.Flags.Utils.SYSTEM_UTILS;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.debug;

public class SystemUtils extends com.common.utils.SystemUtils {
	private final AppCompatActivity appCompatActivity;

	public SystemUtils(AppCompatActivity appCompatActivity) {
		this.appCompatActivity = appCompatActivity;
	}

	@Override
	public void exit() {
		debug(SYSTEM_UTILS, CORE, "--- [SystemUtils.exit]");
		appCompatActivity.finishAffinity();
	}
}
