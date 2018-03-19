package de.rub.bph.ui.component;

import de.rub.bph.data.PuzzleDirection;
import de.rub.bph.data.Puzzler;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Created by nilfoe on 12/03/2018.
 */
public class WellPreviewPanel extends JPanel {

	private int w;
	private int h;
	private PuzzleDirection direction;
	private Puzzler puzzler;
	private int fontSizeBase = 12;

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

	public int getFontSizeBase() {
		return fontSizeBase;
	}

	public void setFontSizeBase(int fontSizeBase) {
		this.fontSizeBase = fontSizeBase;
	}

	public int incrementFontSize() {
		setFontSizeBase(getFontSizeBase() + 1);
		update();
		return getFontSizeBase();
	}

	public int decrementFontSize() {
		setFontSizeBase(getFontSizeBase() - 1);
		if (getFontSizeBase() < 1) {
			setFontSizeBase(1);
		}
		update();
		return getFontSizeBase();
	}


	public void update() {
		//System.out.println("Updating panel.");
		Puzzler p = new Puzzler(getW(), getH(), getDirection());
		if (puzzler == null || !puzzler.equals(p)) {
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

		double wDist = (double) getWidth() / (double) getW();
		double hDist = (double) getHeight() / (double) getH();

		g.setColor(Color.BLACK);
		Font f = g.getFont();
		g.setFont(new Font(f.getName(), f.getStyle(), getFontSizeBase()));

		for (int i = 0; i < getW(); i++) {
			double k = (i + 1) * wDist;
			g.drawLine((int) k, 0, (int) k, getHeight());

			for (int j = 0; j < getH(); j++) {
				k = (j + 1) * hDist;
				g.drawLine(0, (int) k, getWidth(), (int) k);
				int index = puzzler.getPosition(i, j);
				String value = String.valueOf(getH() * getW() - index);
				if (index == Puzzler.POSITION_NOT_USED) {
					System.err.println("Invalid position index for " + i + "," + j);
					value = "?";
				}
				//autoGenStringSize(g, value, wDist, hDist);
				int textW = g.getFontMetrics().stringWidth(value);
				g.drawString(value, (int) (i * wDist + wDist / 2-textW/2), getHeight() - (int) (j * hDist + hDist / 2));
			}
		}
	}

	@Deprecated
	private boolean checkStringSize(Graphics g, Font f, String text, int w, int h) {
		FontMetrics metrics = g.getFontMetrics(f);
		Rectangle2D r = metrics.getStringBounds(text, g);
		return !(r.getWidth() < w) && !(r.getHeight() < h);
	}

	@Deprecated
	private void autoGenStringSize(Graphics g, String text, int w, int h) {
		Font f = g.getFont();
		int fontSize = 0;
		boolean b = false;

		while (!b) {
			f = new Font(f.getName(), f.getStyle(), fontSize++);
			b = checkStringSize(g, f, text, w, h);
			//g.setFont(f);
		}

		g.setFont(f);
	}
}
