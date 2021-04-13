package de.rub.bph.ui.component;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

public class WellPartitionPanel extends Panel implements MouseListener, MouseMotionListener {
	
	private int w;
	private int h;
	private int fontSizeBase = 12;
	private int strokeSize = 4;
	private ArrayList<WellRectangle> rectList;
	private ArrayList<WellPartitionPanelListener> listeners;
	private boolean selectMode;
	private Point selectStartPoint, selectEndPoint;
	
	public WellPartitionPanel(int w, int h) {
		this.w = w;
		this.h = h;
		rectList = new ArrayList<>();
		listeners = new ArrayList<WellPartitionPanelListener>();
		selectMode = false;
		selectStartPoint = new Point(0, 0);
		selectEndPoint = new Point(0, 0);
		
		for (int i = 0; i < getW(); i++) {
			for (int j = 0; j < getH(); j++) {
				WellRectangle rect = new WellRectangle(i, j);
				rectList.add(rect);
			}
		}
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.BOTH;
		GridBagLayout layout = new GridBagLayout();
		layout.setConstraints(this, gbc);
		setLayout(layout);
		addMouseListener(this);
		addMouseMotionListener(this);
		update();
	}
	
	public int getW() {
		return w;
	}
	
	public int getH() {
		return h;
	}
	
	public void setH(int h) {
		this.h = h;
		update();
	}
	
	public void update() {
		invalidate();
		revalidate();
		repaint();
		
		notifyListeners();
	}
	
	private void notifyListeners() {
		for (WellPartitionPanelListener listener : listeners) {
			listener.onWellPartitionPanelChange(this);
		}
	}
	
	public void setW(int w) {
		this.w = w;
		update();
	}
	
	public void addListener(WellPartitionPanelListener listener) {
		listeners.add(listener);
	}
	
	public void clearListeners() {
		listeners.clear();
	}
	
	public void importWellListsXML(String usedWells, String controlWells) {
		ArrayList<String> usedWellList = new ArrayList<>(Arrays.asList(usedWells.split(";")));
		ArrayList<String> controlWellList = new ArrayList<>(Arrays.asList(controlWells.split(";")));
		
		for (WellRectangle r : rectList) {
			String currentWell = r.getNameShort();
			r.setState(0);
			
			if (usedWellList.contains(currentWell)) {
				r.setState(1);
			}
			if (controlWellList.contains(currentWell)) {
				r.setState(2);
			}
		}
		
		update();
		notifyListeners();
	}
	
	@Override
	public void mouseDragged(MouseEvent mouseEvent) {
		selectEndPoint = mouseEvent.getPoint();
		update();
	}
	
	@Override
	public void mouseMoved(MouseEvent mouseEvent) {
	}
	
	@Override
	public void mouseClicked(MouseEvent mouseEvent) {
		update();
		
		Point p = mouseEvent.getPoint();
		for (WellRectangle rectangle : rectList) {
			if (rectangle.contains(p)) {
				System.out.println("Click in " + rectangle.getNameLong());
				rectangle.onClick(mouseEvent);
			}
		}
	}
	
	@Override
	public void mousePressed(MouseEvent mouseEvent) {
		selectStartPoint = mouseEvent.getPoint();
		selectMode = true;
	}
	
	@Override
	public void mouseReleased(MouseEvent mouseEvent) {
		ArrayList<WellRectangle> selectedList = getSelectedWellRectangles();
		System.out.println("Selected: " + selectedList.size());
		
		int maxState = 0;
		for (WellRectangle r : selectedList) {
			maxState = Math.max(maxState, r.incrementState());
		}
		for (WellRectangle r : selectedList) {
			r.setState(maxState);
		}
		
		selectMode = false;
		update();
	}
	
	@Override
	public void mouseEntered(MouseEvent mouseEvent) {
		update();
	}
	
	@Override
	public void mouseExited(MouseEvent mouseEvent) {
		selectMode = false;
		update();
	}
	
	public ArrayList<WellRectangle> getSelectedWellRectangles() {
		ArrayList<WellRectangle> selectedList = new ArrayList<>();
		if (!selectMode) {
			return selectedList;
		}
		for (WellRectangle r : rectList) {
			if (r.isHighlighted()) selectedList.add(r);
		}
		return selectedList;
	}
	
	/**
	 * @Override public Dimension getMinimumSize() {
	 * Dimension dim = super.getMinimumSize();
	 * dim.setSize(dim.getWidth() * 2, dim.getHeight() * 2);
	 * return dim;
	 * }
	 **/
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		paintComponents(g);
	}
	
	public void drawWell(Graphics g, WellRectangle rectangle) {
		int strokeOffset = (int) (getStrokeSize() * 1.5);
		int x = (int) rectangle.getX();
		int y = (int) rectangle.getY();
		int w = (int) rectangle.getWidth();
		int h = (int) rectangle.getHeight();
		
		int centerX = x + w / 2;
		int centerY = y + h / 2;
		int circleRadius = Math.min(w, h);
		x = centerX - circleRadius / 2;
		y = centerY - circleRadius / 2;
		
		String wellText = rectangle.getNameLong();
		Graphics2D g2 = (Graphics2D) g;
		Stroke s = g2.getStroke();
		
		g.setColor(Color.WHITE);
		switch (rectangle.getState()) {
			case 1:
				g.setColor(Color.RED);
				break;
			case 2:
				g.setColor(Color.CYAN);
				break;
		}
		g.fillOval(x, y, circleRadius, circleRadius);
		
		g.setColor(Color.GRAY);
		if (rectangle.isHighlighted()) g.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(getStrokeSize()));
		g2.drawOval(x, y, circleRadius, circleRadius);
		g2.setStroke(s);
		
		g.setColor(Color.BLACK);
		FontRenderContext frc = g2.getFontRenderContext();
		Rectangle2D textBound = g.getFont().getStringBounds(wellText, frc);
		
		int textX = (int) (centerX - textBound.getWidth() / 2);
		int textY = (int) (centerY + textBound.getHeight() / 2);
		g.drawString(wellText, textX, textY);
	}
	
	public int getStrokeSize() {
		return strokeSize;
	}
	
	public void setStrokeSize(int strokeSize) {
		this.strokeSize = strokeSize;
	}
	
	@Override
	public void paintComponents(Graphics g) {
		super.paintComponents(g);
		int w = getWidth() / getW();
		int h = getHeight() / getH();
		
		g.setColor(Color.BLACK);
		Font f = g.getFont();
		g.setFont(new Font(f.getName(), f.getStyle(), fontSizeBase));
		
		for (int i = 0; i < getW(); i++) {
			for (int j = 0; j < getH(); j++) {
				int px = -1;
				int py = -1;
				int pw = -1;
				int ph = -1;
				int x = i * getWidth() / getW();
				int y = j * getHeight() / getH();
				
				if (selectMode) {
					px = Math.min(selectEndPoint.x, selectStartPoint.x);
					py = Math.min(selectEndPoint.y, selectStartPoint.y);
					pw = Math.abs(selectEndPoint.x - selectStartPoint.x);
					ph = Math.abs(selectEndPoint.y - selectStartPoint.y);
				}
				Rectangle selectionRect = new Rectangle(px, py, pw, ph);
				
				for (WellRectangle rectangle : rectList) {
					if (rectangle.getWellX() == i && rectangle.getWellY() == j) {
						rectangle.setHighlighted(rectangle.intersects(selectionRect));
						rectangle.setBounds(x, y, w, h);
						drawWell(g, rectangle);
					}
				}
				
				if (selectMode) {
					g.drawRect(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height);
					//g.drawRect(px, py, pw, ph);
				}
			}
		}
		notifyListeners();
	}
	
	public int getControlCount() {
		return getControlWells().size();
	}
	
	public ArrayList<WellRectangle> getControlWells() {
		ArrayList<WellRectangle> list = new ArrayList<>();
		for (WellRectangle rectangle : rectList) {
			if (rectangle.getState() >= 2) {
				list.add(rectangle);
			}
		}
		return list;
	}
	
	public int getUsedWellCount() {
		return getUsedWells().size();
	}
	
	public ArrayList<WellRectangle> getUsedWells() {
		ArrayList<WellRectangle> list = new ArrayList<>();
		for (WellRectangle rectangle : rectList) {
			if (rectangle.getState() >= 1) {
				list.add(rectangle);
			}
		}
		return list;
	}
	
	public static class WellRectangle extends Rectangle implements Comparable<WellRectangle> {
		
		private final String nameLong, nameShort;
		private int state;
		private int wellX;
		private int wellY;
		private boolean highlighted;
		
		public WellRectangle(int wellX, int wellY) {
			this.wellX = wellX;
			this.wellY = wellY;
			state = 0;
			highlighted = false;
			
			String wellChar = String.valueOf((char) (65 + wellY));
			nameShort = wellChar + (wellX + 1);
			if (wellX < 9) {
				wellChar = wellChar + "0";
			}
			nameLong = wellChar + (wellX + 1);
		}
		
		public void onClick(MouseEvent event) {
			incrementState();
		}
		
		public int incrementState() {
			setState((getState() + 1) % 3);
			return getState();
		}
		
		public int getState() {
			return state;
		}
		
		private void setState(int state) {
			this.state = state;
		}
		
		@Override
		public int compareTo(@NotNull WellPartitionPanel.WellRectangle rectangle) {
			return getNameLong().compareTo(rectangle.getNameLong());
		}
		
		public String getNameLong() {
			return nameLong;
		}
		
		public String getNameShort() {
			return nameShort;
		}
		
		public int getWellX() {
			return wellX;
		}
		
		public void setWellX(int wellX) {
			this.wellX = wellX;
		}
		
		public int getWellY() {
			return wellY;
		}
		
		public void setWellY(int wellY) {
			this.wellY = wellY;
		}
		
		public boolean isHighlighted() {
			return highlighted;
		}
		
		public void setHighlighted(boolean highlighted) {
			this.highlighted = highlighted;
		}
	}
	
	public static interface WellPartitionPanelListener {
		
		public void onWellPartitionPanelChange(WellPartitionPanel source);
	}
	
	
}


