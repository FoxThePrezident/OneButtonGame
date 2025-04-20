package com.one_of_many_simons;

import com.common.Data;
import com.common.Launcher;
import com.one_of_many_simons.graphics.Graphics;
import com.one_of_many_simons.utils.FileHandle;
import com.one_of_many_simons.utils.SystemUtils;

public class Main {
	public static void main(String[] args) {
		Data.graphics = new Graphics();
		Data.fileHandle = new FileHandle();
		Data.systemUtils = new SystemUtils();

		Launcher main = new Launcher();
		main.init();
	}
}