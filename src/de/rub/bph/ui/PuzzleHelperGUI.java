package de.rub.bph.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by nilfoe on 12/03/2018.
 */
public class PuzzleHelperGUI extends JFrame{
	private JButton button1;
	private JPanel basePanel;
	private JButton button2;

	public PuzzleHelperGUI() {
		button1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				System.out.println("I am test");
			}
		});

		JMenuBar bar = new JMenuBar();
		JMenu menu = new JMenu("File");

		JMenuItem item = new JMenuItem("Exit");
		menu.add(item);
		bar.add(menu);

		setJMenuBar(bar);

		setTitle("ThermoFischer SCIENTIFIC ArrayScan Cellomics Image Puzzle-Helper");
		setContentPane(basePanel);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
}
