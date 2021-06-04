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
	private boolean mirrorRowTiling;
	private boolean mirrorColumnTiling;
	private PuzzleDirection direction;
	
	private HashMap<Integer, Point> visitedMap;
	
	public Puzzler(int w, int h, boolean mirrorRowTiling, boolean mirrorColumnTiling, PuzzleDirection direction) {
		this.w = w;
		this.h = h;
		this.direction = direction;
		this.mirrorRowTiling = mirrorRowTiling;
		this.mirrorColumnTiling = mirrorColumnTiling;
		visitedMap = new HashMap<>();
	}
	
	public void puzzle() {
		System.out.println("Puzzling a new structure: " + getW() + "x" + getH());
		System.out.println("Mirror rows: "+isMirrorRowTiling()+". Mirror cols: "+mirrorColumnTiling);
		
		PuzzleDirection currentDirection = direction;
		PuzzleDirection startDirection = direction;
		
		int x = 0;
		int y = getH() - 1;
		
		for (int i = 0; i <= getSize(); i++) {
			Point p = transformPosition(x,y);
			visitedMap.put(i, p);
			System.out.println("Map: " + i + " -> " + p.x + "x" + p.y + " - " + currentDirection);
			
			switch (currentDirection) {
				case RIGHT:
					if (x + 1 < getW() && !hasPosition(x + 1, y)) {
						x += 1;
					} else {
						if (startDirection == PuzzleDirection.DOWN) {
							y += 1;
							currentDirection = PuzzleDirection.UP;
						} else {
							y -= 1;
							currentDirection = PuzzleDirection.DOWN;
						}
					}
					break;
				case DOWN:
					if (y - 1 >= 0 && !hasPosition(x, y - 1)) {
						y -= 1;
					} else {
						if (startDirection == PuzzleDirection.DOWN) {
							x += 1;
							currentDirection = PuzzleDirection.RIGHT;
						} else {
							x -= 1;
							currentDirection = PuzzleDirection.LEFT;
						}
					}
					break;
				case LEFT:
					if (x - 1 >= 0 && !hasPosition(x - 1, y)) {
						x -= 1;
					} else {
						if (startDirection == PuzzleDirection.DOWN) {
							y -= 1;
							currentDirection = PuzzleDirection.DOWN;
						} else {
							y += 1;
							currentDirection = PuzzleDirection.UP;
						}
					}
					break;
				case UP:
					if (y + 1 < getH() && !hasPosition(x, y + 1)) {
						y += 1;
					} else {
						if (startDirection == PuzzleDirection.DOWN) {
							x -= 1;
							currentDirection = PuzzleDirection.LEFT;
						} else {
							x += 1;
							currentDirection = PuzzleDirection.RIGHT;
						}
					}
					break;
			}
		}
	}
	
	public int getW() {
		return w;
	}
	
	public int getH() {
		return h;
	}
	
	public int getSize() {
		return getW() * getH();
	}
	
	private Point transformPosition(int x, int y) {
		if (isMirrorColumnTiling()) {
			x = getW() - x;
		}
		if (isMirrorRowTiling()) {
			y = getH() - y;
		}
		return new Point(x, y);
	}
	
	public boolean hasPosition(int x, int y) {
		return getPosition(x, y) != POSITION_NOT_USED;
	}
	
	public boolean isMirrorRowTiling() {
		return mirrorRowTiling;
	}
	
	public boolean isMirrorColumnTiling() {
		return mirrorColumnTiling;
	}
	
	public int getPosition(int x, int y) {
		return getPosition(transformPosition(x, y));
	}
	
	public int getPosition(Point v) {
		for (Integer i : visitedMap.keySet()) {
			Point visited = getPosition(i);
			if (visited.equals(v)) return i;
		}
		return POSITION_NOT_USED;
	}
	
	public Point getPosition(int i) {
		return visitedMap.get(i);
	}
	
	public boolean equals(Puzzler p) {
		return getW() == p.getW() && getH() == p.getH() && getDirection() == p.getDirection() && isMirrorColumnTiling() == p.isMirrorColumnTiling() && isMirrorRowTiling() && isMirrorRowTiling();
	}
	
	public PuzzleDirection getDirection() {
		return direction;
	}
	
	@Override
	public int hashCode() {
		int hash = "Nils".hashCode();
		
		hash = 31 * hash + getW();
		hash = 31 * hash + getH();
		hash = 31 * hash + visitedMap.hashCode();
		
		return hash;
	}
}
