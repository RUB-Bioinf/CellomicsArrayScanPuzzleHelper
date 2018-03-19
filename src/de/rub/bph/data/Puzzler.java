package de.rub.bph.data;

import java.awt.*;
import java.util.HashMap;

/**
 * Created by nilfoe on 12/03/2018.
 */
public class Puzzler {

	public static final int POSITION_NOT_USED = -1;

	private int w;
	private int h;
	private PuzzleDirection direction;

	private HashMap<Integer, Point> visitedMap;

	public Puzzler(int w, int h, PuzzleDirection direction) {
		this.w = w;
		this.h = h;
		this.direction = direction;
		visitedMap = new HashMap<>();
	}

	public void puzzle() {
		System.out.println("Puzzling a new structure: " + getW() + "x" + getH());

		PuzzleDirection currentDirection = direction;
		int x = 0;
		int y = getH()-1;

		for (int i = 0; i <= getSize(); i++) {
			visitedMap.put(i, new Point(x, y));
			System.out.println("Map: " + i + " -> " + x + "x" + y + " - " + currentDirection);

			switch (currentDirection) {
				case RIGHT:
					if (x + 1 < getW() && !hasPosition(x + 1, y)) {
						x += 1;
					} else {
						y -= 1;
						currentDirection = PuzzleDirection.DOWN;
					}
					break;
				case DOWN:
					if (y - 1 >= 0 && !hasPosition(x, y - 1)) {
						y -= 1;
					} else {
						x -= 1;
						currentDirection = PuzzleDirection.LEFT;
					}
					break;
				case LEFT:
					if (x - 1 >= 0 && !hasPosition(x - 1, y)) {
						x -= 1;
					} else {
						y += 1;
						currentDirection = PuzzleDirection.UP;
					}
					break;
				case UP:
					if (y + 1 < getH() && !hasPosition(x, y + 1)) {
						y += 1;
					} else {
						x += 1;
						currentDirection = PuzzleDirection.RIGHT;
					}
					break;
			}
		}
	}

	public boolean equals(Puzzler p) {
		return getW() == p.getW() && getH() == p.getH() && getDirection() == p.getDirection();
	}

	@Override
	public int hashCode() {
		int hash = "Nils".hashCode();

		hash = 31 * hash + getW();
		hash = 31 * hash + getH();
		hash = 31 * hash + visitedMap.hashCode();

		return hash;
	}

	public boolean hasPosition(int x, int y) {
		return getPosition(x, y) != POSITION_NOT_USED;
	}

	public Point getPosition(int i) {
		return visitedMap.get(i);
	}


	public int getPosition(int x, int y) {
		return getPosition(new Point(x, y));
	}

	public int getPosition(Point v) {
		for (Integer i : visitedMap.keySet()) {
			Point visited = getPosition(i);
			if (visited.equals(v)) return i;
		}
		return POSITION_NOT_USED;
	}

	public int getSize() {
		return getW() * getH();
	}

	public int getW() {
		return w;
	}

	public int getH() {
		return h;
	}

	public PuzzleDirection getDirection() {
		return direction;
	}
}
