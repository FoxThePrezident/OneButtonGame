package com.OneOfManySimons.DataClasses;

import java.awt.*;
import java.util.ArrayList;

/**
 * Map data for level
 */
public class MapData {
	public ArrayList<Point> walls = new ArrayList<>();
	public ArrayList<Interactive> interactive = new ArrayList<>();
	public ArrayList<Point> ground = new ArrayList<>();
}
