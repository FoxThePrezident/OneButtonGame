package com.common.DataClasses;

public class Position {
	public int x;
	public int y;

	public Position() {
		x = 0;
		y = 0;
	}

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Position(Position position) {
		x = position.x;
		y = position.y;
	}

	public boolean equals(Position position) {
		return (position.x == x) && (position.y == y);
	}
}
