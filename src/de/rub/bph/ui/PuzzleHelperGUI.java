package de.rub.bph.ui;

import de.rub.bph.CellomicsPuzzleHelper;
import de.rub.bph.ui.component.WellPreviewPanel;
import ij.ImagePlus;
import ij.io.Opener;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.rub.bph.data.PuzzleDirection;


/**
 * Created by nilfoe on 12/03/2018.
 */
public class PuzzleHelperGUI extends JFrame {

	public static final String INSTRUCTION_IMWIDTH = "imwidth";
	public static final String INSTRUCTION_IMHEIGHT = "imheight";
	public static final String INSTRUCTION_PWIDTH = "pwidth";
	public static final String INSTRUCTION_PHEIGHT = "pheight";
	public static final String INSTRUCTION_PDIR = "pdir";
	public static final String INSTRUCTION_PFLIPROW = "pfliprow";
	public static final String INSTRUCTION_PFLIPRESULT = "pflipresult";
	public static final String INSTRUCTION_VERSION = "version";
	public static final String INSTRUCTION_MAGNIFICATION = "immagni";

	private FileNameExtensionFilter exportFileFilter, imageFileFilter;

	private JButton button1;
	private JPanel basePanel;
	private JButton exportInstructionFileButton;
	private JSpinner pWidthSP;
	private JSpinner pHeightSP;
	private JSpinner imWidthSP;
	private JSpinner imHeightSP;
	private JComboBox<PuzzleDirection> directionCB;
	private JCheckBox flipRowCB;
	private WellPreviewPanel previewPL;
	private JLabel imagecountLB;
	private JButton button2;
	private JButton button3;
	private JButton autoReadImageSizesButton;
	private JCheckBox flipFinalImageCB;
	private JButton importBT;
	private JSpinner magnificationSP;

	private JCheckBoxMenuItem autoUpdateCB;

	public PuzzleHelperGUI() {

		exportFileFilter = new FileNameExtensionFilter("XML", "xml");
		imageFileFilter = new FileNameExtensionFilter("Image files", "tiff", "tif", "png", "bmp");

		magnificationSP.setModel(new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1));
		pWidthSP.setModel(new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1));
		pHeightSP.setModel(new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1));
		imWidthSP.setModel(new SpinnerNumberModel(1000, 1, Integer.MAX_VALUE, 1));
		imHeightSP.setModel(new SpinnerNumberModel(1000, 1, Integer.MAX_VALUE, 1));

		ChangeListener l = changeEvent -> requestUpdate();
		pWidthSP.addChangeListener(l);
		pHeightSP.addChangeListener(l);
		imWidthSP.addChangeListener(l);
		imHeightSP.addChangeListener(l);

		directionCB.addItem(PuzzleDirection.RIGHT);
		directionCB.addItem(PuzzleDirection.DOWN);

		JMenuBar bar = new JMenuBar();
		JMenu menu = new JMenu("File");
		JMenuItem item = new JMenuItem("Exit");
		item.setAccelerator(KeyStroke.getKeyStroke('W', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menu.add(item);
		bar.add(menu);

		menu = new JMenu("View");
		autoUpdateCB = new JCheckBoxMenuItem("Auto update preview");
		autoUpdateCB.setAccelerator(KeyStroke.getKeyStroke('U', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		autoUpdateCB.setSelected(true);
		menu.add(autoUpdateCB);

		item = new JMenuItem("Reset");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				pack();
				setLocationRelativeTo(null);
				requestFocus();
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke('R', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menu.add(item);
		bar.add(menu);

		menu = new JMenu("?");
		item = new JMenuItem("About");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				JOptionPane.showMessageDialog(PuzzleHelperGUI.this, "'" + CellomicsPuzzleHelper.NAME + "' by " + CellomicsPuzzleHelper.AUTHOR + ".\nVersion: " + CellomicsPuzzleHelper.VERSION + "\n\nCreated at the 'Ruhr University Bochum' and 'Leibniz Research Institute for environmental medicine'.", "About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		menu.add(item);

		item = new JMenuItem("View on GitHub");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				try {
					browseURL("https://github.com/RUB-Bioinf/CellomicsArrayScanPuzzleHelper");
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(PuzzleHelperGUI.this, "Failed to browse URL: " + e.getMessage());
				}
			}
		});
		menu.add(item);
		bar.add(menu);

		setJMenuBar(bar);

		button1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				update();
			}
		});
		directionCB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				requestUpdate();
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
		importBT.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				try {
					actionImportFile();
				} catch (IOException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(PuzzleHelperGUI.this, "Failed to read instruction file:\n" + e.getMessage(), CellomicsPuzzleHelper.NAME, JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		update();
		setTitle(CellomicsPuzzleHelper.NAME + " [Version " + CellomicsPuzzleHelper.VERSION + "]");
		setContentPane(basePanel);
		pack();
		setMinimumSize(getSize());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	private void browseURL(String url) throws NullPointerException, URISyntaxException, IOException {
		Desktop desktop = Desktop.getDesktop();
		desktop.browse(new URI(url));
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
		map.put(INSTRUCTION_IMWIDTH, String.valueOf(imWidthSP.getValue()));
		map.put(INSTRUCTION_IMHEIGHT, String.valueOf(imHeightSP.getValue()));
		map.put(INSTRUCTION_PWIDTH, String.valueOf(pWidthSP.getValue()));
		map.put(INSTRUCTION_PHEIGHT, String.valueOf(pHeightSP.getValue()));
		map.put(INSTRUCTION_PDIR, String.valueOf(directionCB.getSelectedItem()));
		map.put(INSTRUCTION_PFLIPROW, String.valueOf(flipRowCB.isSelected()));
		map.put(INSTRUCTION_PFLIPRESULT, String.valueOf(flipFinalImageCB.isSelected()));
		map.put(INSTRUCTION_MAGNIFICATION, String.valueOf(magnificationSP.getValue()));
		map.put(INSTRUCTION_VERSION, CellomicsPuzzleHelper.VERSION);

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

	private void actionImportFile() throws IOException {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select instruction file");
		chooser.setFileFilter(exportFileFilter);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setApproveButtonText("Import");
		int i = chooser.showOpenDialog(this);

		if (i != JFileChooser.APPROVE_OPTION) return;
		File f = chooser.getSelectedFile();

		ArrayList<String> XMLArgumentList = new ArrayList<>();
		XMLArgumentList.add(INSTRUCTION_IMWIDTH);
		XMLArgumentList.add(INSTRUCTION_IMHEIGHT);
		XMLArgumentList.add(INSTRUCTION_PWIDTH);
		XMLArgumentList.add(INSTRUCTION_PHEIGHT);
		XMLArgumentList.add(INSTRUCTION_PDIR);
		XMLArgumentList.add(INSTRUCTION_PFLIPROW);
		XMLArgumentList.add(INSTRUCTION_PFLIPRESULT);
		XMLArgumentList.add(INSTRUCTION_VERSION);
		XMLArgumentList.add(INSTRUCTION_MAGNIFICATION);
		HashMap<String, String> xmlMap = new HashMap<>();

		BufferedReader br = new BufferedReader(new FileReader(f));
		String line;
		while ((line = br.readLine()) != null) {
			line = line.trim();
			ArrayList<String> temp = new ArrayList<>(XMLArgumentList);
			for (String s : temp) {
				String regex = "<" + s + ">(\\w+)</" + s + ">";
				Matcher m = Pattern.compile(regex).matcher(line);
				if (m.find()) {
					System.out.println("Regex done: " + regex + " -> " + m.group(1));
					xmlMap.put(s, m.group(1));
					XMLArgumentList.remove(s);
				}
			}
		}

		if (!xmlMap.containsKey(INSTRUCTION_VERSION)) {
			xmlMap.put(INSTRUCTION_VERSION, String.valueOf(CellomicsPuzzleHelper.VERSION));
			XMLArgumentList.remove(INSTRUCTION_VERSION);
			JOptionPane.showMessageDialog(this, "Failed to read the Version on this file. This could lead to unexpected behaviour.");
		}

		if (!XMLArgumentList.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Not enough instruction lines. This may lead to unexpected behaviour! Missing expected lines:\n" + Arrays.toString(XMLArgumentList.toArray()));
		}

		if (!xmlMap.get(INSTRUCTION_VERSION).equals(CellomicsPuzzleHelper.VERSION)) {
			JOptionPane.showMessageDialog(this, "The instruction file was created from a different version. This could lead to unexpected behaviour.");
		}

		imWidthSP.setValue(Integer.valueOf(xmlMap.get(INSTRUCTION_IMWIDTH)));
		imHeightSP.setValue(Integer.valueOf(xmlMap.get(INSTRUCTION_IMHEIGHT)));
		pWidthSP.setValue(Integer.valueOf(xmlMap.get(INSTRUCTION_PWIDTH)));
		magnificationSP.setValue(Integer.valueOf(xmlMap.get(INSTRUCTION_MAGNIFICATION)));
		pHeightSP.setValue(Integer.valueOf(xmlMap.get(INSTRUCTION_PHEIGHT)));
		flipFinalImageCB.setSelected(Boolean.valueOf(xmlMap.get(INSTRUCTION_PFLIPRESULT)));
		flipRowCB.setSelected(Boolean.valueOf(xmlMap.get(INSTRUCTION_PFLIPROW)));
		directionCB.setSelectedItem(PuzzleDirection.valueOf(xmlMap.get(INSTRUCTION_PDIR)));

		update();
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
		requestUpdate();
	}

	private void requestUpdate() {
		if (autoUpdateCB.isSelected()) update();
	}

	private void update() {
		int w = (int) pWidthSP.getValue();
		int h = (int) pHeightSP.getValue();
		PuzzleDirection direction = (PuzzleDirection) directionCB.getSelectedItem();

		previewPL.update(w, h, direction);
		imagecountLB.setText("Image count per well: " + w * h + " [" + w + "x" + h + "]");
	}

}
