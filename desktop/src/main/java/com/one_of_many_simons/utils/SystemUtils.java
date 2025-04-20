package com.one_of_many_simons.utils;

import static com.common.Debug.Flags.Utils.SYSTEM_UTILS;
import static com.common.Debug.Levels.CORE;
import static com.common.Debug.debug;

public class SystemUtils extends com.common.utils.SystemUtils {
	@Override
	public void exit() {
		debug(SYSTEM_UTILS, CORE, "--- [SystemUtils.exit]");
		System.exit(0);
	}
}
