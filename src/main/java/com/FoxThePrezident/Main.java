package com.FoxThePrezident;

import com.FoxThePrezident.Interactive.Hp_potion;
import com.FoxThePrezident.entities.Entity;
import com.FoxThePrezident.entities.Player;
import com.FoxThePrezident.graphics.Graphics;
import com.FoxThePrezident.common.Settings;
import com.FoxThePrezident.handlers.FileHandle;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;


public class Main {
	public static void main(String[] args) {
		Main main = new Main();
		main.init();
	}

	public void init() {
		try {
			FileHandle fileHandle = new FileHandle();
			MapUtils mapUtils = new MapUtils();

			String map_data = fileHandle.loadText("json/map.json");
			JSONObject map = fileHandle.parseToJSONObject(map_data);
			Settings.map = mapUtils.constructMap(map);

			Settings.playerRadius = 3;
			Settings.playerControlDelay = 500;
			Settings.imageScale = 3;

			JSONArray playerPosition = map.getJSONArray("player");
			int playerY = playerPosition.getInt(0);
			int playerX = playerPosition.getInt(1);

			Graphics window = new Graphics();
			Player _player = new Player(new int[]{playerY, playerX});
			window.addListener(_player);
			window.initMap();

			Thread player = new Thread(_player);
			player.start();

			JSONArray enemies = map.getJSONArray("enemies");
			for (int i = 0; i < enemies.length(); i++) {
				JSONArray enemyPosition = enemies.getJSONArray(i);
				int enemyY = enemyPosition.getInt(0);
				int enemyX = enemyPosition.getInt(1);
				Entity enemy = new Entity(new int[]{enemyY, enemyX});
				window.addListener(enemy);
			}

			Hp_potion hp = new Hp_potion(new int[]{4, 4});
			window.addListener(hp);

//			window.refreshScreen();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}