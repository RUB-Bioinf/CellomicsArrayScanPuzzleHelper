package de.rub.bph;

import de.rub.bph.ui.PuzzleHelperGUI;

import javax.swing.*;

/**
 * Created by nilfoe on 12/03/2018.
 */

public class CellomicsPuzzleHelper {
	
	public static final String AUTHOR = "Nils Förster";
	public static final String NAME = "OmniSphero Smart-Well Creator";
	public static final String VERSION = "1.8";
	public static PuzzleHelperGUI helperGUI;
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
				e.printStackTrace();
			}
			
			helperGUI = new PuzzleHelperGUI();
		});
	}
}
