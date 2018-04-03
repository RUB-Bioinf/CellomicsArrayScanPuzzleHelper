package de.rub.bph;

import de.rub.bph.ui.PuzzleHelperGUI;

import javax.swing.*;

/**
 * Created by nilfoe on 12/03/2018.
 */
public class CellomicsPuzzleHelper {

	public static final String NAME = "ThermoFischer SCIENTIFIC ArrayScan Cellomics Image Puzzle-Helper";
	public static final String VERSION = "1.0";
	public static final String AUTHOR = "Nils Förster";

	public static PuzzleHelperGUI helperGUI;

	public static void main(String[] args) {
		//JFrame frame = new JFrame("Nuls test");
		//frame.setContentPane(new PuzzleHelperGUI().basePanel);
		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setVisible(true);
		//frame.pack();
		//frame.setLocationRelativeTo(null);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
					e.printStackTrace();
				}

				helperGUI = new PuzzleHelperGUI();
			}
		});
	}

	;

}
