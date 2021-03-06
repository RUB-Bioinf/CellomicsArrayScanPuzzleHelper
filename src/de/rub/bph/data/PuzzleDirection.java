package de.rub.bph.data;

/**
 * Created by nilfoe on 12/03/2018.
 */
public enum PuzzleDirection {

	LEFT, RIGHT, UP, DOWN;

	public static PuzzleDirection getViaString(String name) {
		for (PuzzleDirection direction : values()) {
			if (direction.toString().equals(name)) return direction;
		}
		return null;
	}

	@Override
	public String toString() {
		switch (this) {
			case LEFT:
				return "Left";
			case RIGHT:
				return "Right";
			case UP:
				return "Up";
			case DOWN:
				return "Down";
			default:
				System.err.println("Invalid PuzzleDirection value requested!");
				return "??";
		}

	}
}
