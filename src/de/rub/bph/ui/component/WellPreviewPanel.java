package de.rub.bph.ui.component;

import de.rub.bph.data.PuzzleDirection;
import de.rub.bph.data.Puzzler;

import javax.swing.*;
import java.awt.*;

/**
 * Created by nilfoe on 12/03/2018.
 */
public class WellPreviewPanel extends JPanel {

	private int w;
	private int h;
	private PuzzleDirection direction;
	private Puzzler puzzler;

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
		update();
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
		update();
	}

	public PuzzleDirection getDirection() {
		return direction;
	}

	public void setDirection(PuzzleDirection direction) {
		this.direction = direction;
		update();
	}

	public void update(int w, int h, PuzzleDirection direction) {
		this.w = w;
		this.h = h;
		this.direction = direction;

		update();
	}

	public void update() {
		Puzzler p = new Puzzler(getW(), getH(), getDirection());
		if (puzzler == null) puzzler = p;

		if (!puzzler.equals(p)) {
			puzzler = p;
			puzzler.puzzle();
		}
		
		invalidate();
		revalidate();
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

	}
}
