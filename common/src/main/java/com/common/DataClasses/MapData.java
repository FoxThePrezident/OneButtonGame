package com.common.DataClasses;

import java.util.ArrayList;

/**
 * Map data for level
 */
public class MapData {
	public ArrayList<Position> walls = new ArrayList<>();
	public ArrayList<Interactive> interactive = new ArrayList<>();
	public ArrayList<Position> ground = new ArrayList<>();
}
