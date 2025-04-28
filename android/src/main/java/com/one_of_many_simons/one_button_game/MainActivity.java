package com.one_of_many_simons.one_button_game;

import android.os.Bundle;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.common.Data;
import com.common.Launcher;
import com.one_of_many_simons.one_button_game.graphics.Graphics;
import com.one_of_many_simons.one_button_game.utils.FileHandle;
import com.one_of_many_simons.one_button_game.utils.SystemUtils;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FrameLayout frameLayout = new FrameLayout(this);
		setContentView(frameLayout);

		Data.graphics = new Graphics(this, frameLayout);
		Data.fileHandle = new FileHandle(this);
		Data.systemUtils = new SystemUtils(this);

		Launcher main = new Launcher();

		main.init();

//		this.finishAffinity();
	}
}