package de.rub.bph.ui;

import de.rub.bph.CellomicsPuzzleHelper;
import de.rub.bph.data.PuzzleDirection;
import de.rub.bph.ui.component.WellPreviewPanel;
import ij.ImagePlus;
import ij.io.Opener;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


/**
 * Created by nilfoe on 12/03/2018.
 */
public class PuzzleHelperGUI extends JFrame {

	private FileNameExtensionFilter exportFileFilter, imageFileFilter;

	private JButton button1;
	private JPanel basePanel;
	private JButton exportInstructionFileButton;
	private JSpinner pWidthSP;
	private JSpinner pHeightSP;
	private JSpinner imWidthSP;
	private JSpinner imHeightSP;
	private JComboBox<PuzzleDirection> directionCB;
	private JCheckBox flipImagesAlongTheCheckBox;
	private WellPreviewPanel previewPL;
	private JLabel imagecountLB;
	private JButton button2;
	private JButton button3;
	private JButton autoReadImageSizesButton;

	public PuzzleHelperGUI() {

		exportFileFilter = new FileNameExtensionFilter("XML", "xml");
		imageFileFilter = new FileNameExtensionFilter("Image files", "tiff", "tif", "png", "bmp");

		pWidthSP.setModel(new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1));
		pHeightSP.setModel(new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1));
		imWidthSP.setModel(new SpinnerNumberModel(1000, 1, Integer.MAX_VALUE, 1));
		imHeightSP.setModel(new SpinnerNumberModel(1000, 1, Integer.MAX_VALUE, 1));

		ChangeListener l = changeEvent -> update();
		pWidthSP.addChangeListener(l);
		pHeightSP.addChangeListener(l);
		imWidthSP.addChangeListener(l);
		imHeightSP.addChangeListener(l);

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
		exportInstructionFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				try {
					exportFile();
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(PuzzleHelperGUI.this, "Failed to save data to the file:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		autoReadImageSizesButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				autoReadImageSize();
			}
		});

		update();
		setTitle(CellomicsPuzzleHelper.NAME+" [Version "+CellomicsPuzzleHelper.VERSION+"]");
		setContentPane(basePanel);
		pack();
		setMinimumSize(getSize());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	private void exportFile() throws IOException {
		File f = chooseFile();
		if (f == null) {
			return;
		}

		boolean missingExtensions = true;
		for (String extension : exportFileFilter.getExtensions()) {
			if (f.getAbsolutePath().toLowerCase().endsWith("." + extension.toLowerCase())) missingExtensions = false;
		}
		if (missingExtensions) {
			f = new File(f.getAbsolutePath() + "." + exportFileFilter.getExtensions()[0]);
		}

		if (f.exists()) {
			int answer = JOptionPane.showConfirmDialog(this, "The file " + f.getName() + " already exists.\nDo you want to overwrite it?", CellomicsPuzzleHelper.NAME, JOptionPane.YES_NO_OPTION);
			if (answer != JOptionPane.YES_OPTION) {
				exportFile();
				return;
			}
		} else {
			f.createNewFile();
		}

		if (!exportFileFilter.accept(f)) return;

		HashMap<String, String> map = new HashMap<String, String>();
		map.put("imwidth", String.valueOf(imWidthSP.getValue()));
		map.put("imheight", String.valueOf(imHeightSP.getValue()));
		map.put("pwidth", String.valueOf(pWidthSP.getValue()));
		map.put("pheight", String.valueOf(pHeightSP.getValue()));
		map.put("pdir", String.valueOf(directionCB.getSelectedItem()));
		map.put("pfliprow", String.valueOf(flipImagesAlongTheCheckBox.isSelected()));

		FileOutputStream fout = new FileOutputStream(f);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fout));
		bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		bw.newLine();
		bw.write("<WellType>");

		ArrayList<String> args = new ArrayList<>();
		args.addAll(map.keySet());
		Collections.sort(args);

		for (String s : args) {
			bw.newLine();
			bw.write("\t<" + s + ">" + map.get(s) + "</" + s + ">");
		}

		bw.newLine();
		bw.write("</WellType>");
		bw.close();
	}

	@Nullable
	private File chooseFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Save puzzle instructions");
		chooser.setFileFilter(exportFileFilter);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setApproveButtonText("Save");
		int i = chooser.showOpenDialog(this);
		if (i == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile();
		} else return null;
	}

	private void autoReadImageSize() {
		File selectFile;

		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select image folder or file");
		chooser.setFileFilter(imageFileFilter);
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setApproveButtonText("Select");
		int i = chooser.showOpenDialog(this);
		if (i == JFileChooser.APPROVE_OPTION) {
			selectFile = chooser.getSelectedFile();
		} else return;

		if (selectFile.isDirectory()) {
			File[] files = selectFile.listFiles();
			if (files == null || files.length == 0) {
				JOptionPane.showMessageDialog(this, "The selected directory is empty.");
				return;
			}

			for (File f : files) {
				if (f.isDirectory()) continue;

				if (imageFileFilter.accept(f)) {
					selectFile = f;
					break;
				}
			}
			if (selectFile.isDirectory()) {
				JOptionPane.showMessageDialog(this, "No suitable image file found in the selected directory.");
				return;
			} else {
				JOptionPane.showMessageDialog(this, "Assuming image dimensions based on file: " + selectFile.getName());
			}
		}

		//Using imageJ for better performance on large image files
		ImagePlus ip = new Opener().openImage(selectFile.getAbsolutePath());
		imWidthSP.setValue(ip.getWidth());
		imHeightSP.setValue(ip.getHeight());
		update();
	}

	private void update() {
		int w = (int) pWidthSP.getValue();
		int h = (int) pHeightSP.getValue();
		PuzzleDirection direction = (PuzzleDirection) directionCB.getSelectedItem();

		previewPL.update(w, h, direction);
		imagecountLB.setText("Image count per well: " + w * h + " [" + w + "x" + h + "]");
	}

}
