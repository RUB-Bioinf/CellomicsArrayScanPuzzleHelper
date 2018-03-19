package de.rub.bph.ui;

import de.rub.bph.data.PuzzleDirection;
import de.rub.bph.ui.component.WellPreviewPanel;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by nilfoe on 12/03/2018.
 */
public class PuzzleHelperGUI extends JFrame {
	private JButton button1;
	private JPanel basePanel;
	private JButton exportInstructionFileButton;
	private JSpinner spinner1;
	private JSpinner spinner2;
	private JSpinner spinner3;
	private JSpinner spinner4;
	private JComboBox<PuzzleDirection> directionCB;
	private JCheckBox flipImagesAlongTheCheckBox;
	private WellPreviewPanel previewPL;
	private JLabel imagecountLB;
	private JButton button2;
	private JButton button3;

	public PuzzleHelperGUI() {
		spinner1.setModel(new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1));
		spinner2.setModel(new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1));
		spinner3.setModel(new SpinnerNumberModel(1000, 1, Integer.MAX_VALUE, 1));
		spinner4.setModel(new SpinnerNumberModel(1000, 1, Integer.MAX_VALUE, 1));

		ChangeListener l = changeEvent -> update();
		spinner1.addChangeListener(l);
		spinner2.addChangeListener(l);
		spinner3.addChangeListener(l);
		spinner4.addChangeListener(l);

		directionCB.addItem(PuzzleDirection.RIGHT);
		directionCB.addItem(PuzzleDirection.DOWN);

		JMenuBar bar = new JMenuBar();
		JMenu menu = new JMenu("File");

		JMenuItem item = new JMenuItem("Exit");
		menu.add(item);
		bar.add(menu);
		setJMenuBar(bar);

		button1.addActionListener(new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				System.out.println("Button click test.");
				update();
			}
		});
		directionCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				update();
			}
		});
		button3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				previewPL.incrementFontSize();
			}
		});
		button2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				previewPL.decrementFontSize();
			}
		});

		update();
		setTitle("ThermoFischer SCIENTIFIC ArrayScan Cellomics Image Puzzle-Helper");
		setContentPane(basePanel);
		pack();
		setMinimumSize(getSize());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

	}

	private void update() {
		int w = (int) spinner1.getValue();
		int h = (int) spinner2.getValue();
		int imw = (int) spinner3.getValue();
		int imh = (int) spinner4.getValue();
		PuzzleDirection direction = (PuzzleDirection) directionCB.getSelectedItem();

		previewPL.update(w, h, direction);
		imagecountLB.setText("Image count per well: " + w * h + " [" + w + "x" + h + "]");
	}

}
