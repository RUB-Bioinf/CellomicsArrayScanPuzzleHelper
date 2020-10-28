package de.rub.bph.ui.component;

import de.rub.bph.data.PuzzleDirection;
import de.rub.bph.data.Puzzler;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Random;

/**
 * Created by nilfoe on 12/03/2018.
 */
public class WellPreviewPanel extends JPanel {
	
	private int w;
	private int h;
	private boolean mirrorRowTiling;
	private boolean highlightSpiral;
	private boolean mirrorColumnTiling;
	private PuzzleDirection direction;
	private Puzzler puzzler;
	private int fontSizeBase = 12;
	
	public void update(int w, int h, boolean mirrorRowTiling, boolean mirrorColumnTiling, boolean highlightSpiral, PuzzleDirection direction) {
		this.w = w;
		this.h = h;
		this.highlightSpiral=highlightSpiral;
		this.mirrorRowTiling = mirrorRowTiling;
		this.mirrorColumnTiling = mirrorColumnTiling;
		this.direction = direction;
		
		update();
	}
	
	public void update() {
		//System.out.println("Updating panel.");
		Puzzler p = new Puzzler(getW(), getH(), mirrorRowTiling, mirrorColumnTiling, getDirection());
		if (puzzler == null || !puzzler.equals(p)) {
			puzzler = p;
			puzzler.puzzle();
		}
		
		invalidate();
		revalidate();
		repaint();
	}
	
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
	
	public int incrementFontSize() {
		setFontSizeBase(getFontSizeBase() + 1);
		update();
		return getFontSizeBase();
	}
	
	public int getFontSizeBase() {
		return fontSizeBase;
	}
	
	public void setFontSizeBase(int fontSizeBase) {
		this.fontSizeBase = fontSizeBase;
	}
	
	public int decrementFontSize() {
		setFontSizeBase(getFontSizeBase() - 1);
		if (getFontSizeBase() < 1) {
			setFontSizeBase(1);
		}
		update();
		return getFontSizeBase();
	}
	
	@Deprecated
	private void autoGenStringSize(Graphics g, String text, int w, int h) {
		Font f = g.getFont();
		int fontSize = 0;
		boolean b = false;
		
		while (!b) {
			f = new Font(f.getName(), f.getStyle(), fontSize++);
			b = checkStringSize(g, f, text, w, h);
		}
		
		g.setFont(f);
	}
	
	@Deprecated
	private boolean checkStringSize(Graphics g, Font f, String text, int w, int h) {
		FontMetrics metrics = g.getFontMetrics(f);
		Rectangle2D r = metrics.getStringBounds(text, g);
		return !(r.getWidth() < w) && !(r.getHeight() < h);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		double wDist = (double) getWidth() / (double) getW();
		double hDist = (double) getHeight() / (double) getH();
		
		g.setColor(Color.BLACK);
		Font f = g.getFont();
		g.setFont(new Font(f.getName(), f.getStyle(), getFontSizeBase()));
		
		float maxVal = getW()*getH();
		float currentVal = 0;
		Random r = new Random();
		for (int i = 0; i < getW(); i++) {
			for (int j = 0; j < getH(); j++) {
				currentVal++;
				
				int px = i;
				int py = j;
				if (mirrorRowTiling) {
					px = getW() - i - 1;
				}
				if (mirrorColumnTiling) {
					py = getH() - j - 1;
				}
				
				if(highlightSpiral){
					float index = puzzler.getPosition(px, py);
					float v = index / maxVal;
					g.setColor(new Color(1,v*.95f,.85f+v*.1337f));
				}else{
					g.setColor(Color.WHITE);
				}
				
				int x = (int)(i * wDist);
				int y = (int) (getHeight()-j * hDist-hDist);
				g.fillRect(x, y, (int)wDist+1, (int)hDist+1);
			}
		}
		
		g.setColor(Color.BLACK);
		for (int i = 0; i < getW(); i++) {
			double k = (i + 1) * wDist;
			g.setColor(Color.BLACK);
			g.drawLine((int) k, 0, (int) k, getHeight());
			
			for (int j = 0; j < getH(); j++) {
				k = (j + 1) * hDist;
				
				int x = i;
				int y = j;
				if (mirrorRowTiling) {
					x = getW() - i - 1;
				}
				if (mirrorColumnTiling) {
					y = getH() - j - 1;
				}
				
				int index = puzzler.getPosition(x, y);
				String value = String.valueOf(getH() * getW() - index);
				if (index == Puzzler.POSITION_NOT_USED) {
					System.err.println("Invalid position index for " + x + "," + y);
					value = "?";
				}
				
				int textW = g.getFontMetrics().stringWidth(value);
				g.drawString(value, (int) (i * wDist + wDist / 2 - textW / 2), getHeight() - (int) (j * hDist + hDist / 2));
				g.drawLine(0, (int) k, getWidth(), (int) k);
			}
			
		}
	}
	
	
}
