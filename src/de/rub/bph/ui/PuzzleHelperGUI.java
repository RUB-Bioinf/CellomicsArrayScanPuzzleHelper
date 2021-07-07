package de.rub.bph.ui;

import de.rub.bph.CellomicsPuzzleHelper;
import de.rub.bph.data.PuzzleDirection;
import de.rub.bph.ui.component.WellPartitionPanel;
import de.rub.bph.ui.component.WellPreviewPanel;
import ij.ImagePlus;
import ij.io.Opener;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


/**
 * Created by nilfoe on 12/03/2018.
 */
public class PuzzleHelperGUI extends JFrame implements WellPartitionPanel.WellPartitionPanelListener {
	
	public static final String INSTRUCTION_BASE_FILE = "WellType";
	
	public static final String INSTRUCTION_IMHEIGHT = "imheight";
	public static final String INSTRUCTION_IMWIDTH = "imwidth";
	public static final String INSTRUCTION_SEPARATOR = "separatorChar";
	public static final String INSTRUCTION_MAGNIFICATION = "immagni";
	public static final String INSTRUCTION_MIRROR_COLUMN_TILING = "pmirrorcolumntiling";
	public static final String INSTRUCTION_MIRROR_ROW_TILING = "pmirrorrowtiling";
	public static final String INSTRUCTION_PARTITION = "Partitions";
	public static final String INSTRUCTION_PARTITION_CONTROL = "ControlWells";
	public static final String INSTRUCTION_PARTITION_USED = "UsedWells";
	public static final String INSTRUCTION_PDIR = "pdir";
	public static final String INSTRUCTION_PFLIPRESULT = "pflipresult";
	public static final String INSTRUCTION_PFLIPROW = "pfliprow";
	public static final String INSTRUCTION_PHEIGHT = "pheight";
	public static final String INSTRUCTION_PWIDTH = "pwidth";
	public static final String INSTRUCTION_VERSION = "version";
	private FileNameExtensionFilter exportFileFilter, imageFileFilter;
	
	private JButton refreshBT;
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
	private JCheckBox mirrorRowTilingCB;
	private JCheckBox mirrorColumnTilingCB;
	private JCheckBox highlightPuzzleCB;
	private JTabbedPane partitionsTabbedPane;
	private JSpinner partitionsSP;
	private JLabel magnificationLB;
	private JLabel partitionsWarningLB;
	private JTextField separatorTF;
	
	private JCheckBoxMenuItem autoUpdateCB;
	
	public PuzzleHelperGUI() {
		exportFileFilter = new FileNameExtensionFilter("XML", "xml");
		imageFileFilter = new FileNameExtensionFilter("Image files", "tiff", "tif", "png", "bmp");
		
		MouseAdapter mouseListenerUpdate = new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				requestUpdate();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				requestUpdate();
			}
		};
		
		magnificationSP.setModel(new SpinnerNumberModel(1.0, -100.0, 100.0, 0.001));
		pWidthSP.setModel(new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1));
		pHeightSP.setModel(new SpinnerNumberModel(10, 1, Integer.MAX_VALUE, 1));
		imWidthSP.setModel(new SpinnerNumberModel(1000, 1, Integer.MAX_VALUE, 1));
		imHeightSP.setModel(new SpinnerNumberModel(1000, 1, Integer.MAX_VALUE, 1));
		partitionsSP.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
		
		ChangeListener l = changeEvent -> requestUpdate();
		pWidthSP.addChangeListener(l);
		pHeightSP.addChangeListener(l);
		imWidthSP.addChangeListener(l);
		imHeightSP.addChangeListener(l);
		partitionsSP.addChangeListener(changeEvent -> update());
		magnificationSP.addChangeListener(changeEvent -> update());
		magnificationSP.addMouseListener(mouseListenerUpdate);
		exportInstructionFileButton.addMouseListener(mouseListenerUpdate);
		
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
		item.addActionListener(actionEvent -> {
			pack();
			setLocationRelativeTo(null);
			requestFocus();
		});
		item.setAccelerator(KeyStroke.getKeyStroke('R', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menu.add(item);
		bar.add(menu);
		
		menu = new JMenu("?");
		item = new JMenuItem("About");
		item.addActionListener(actionEvent -> JOptionPane.showMessageDialog(PuzzleHelperGUI.this, "'" + CellomicsPuzzleHelper.NAME + "' by " + CellomicsPuzzleHelper.AUTHOR + ".\nVersion: " + CellomicsPuzzleHelper.VERSION + "\n\nCreated at the 'Ruhr University Bochum' and 'Leibniz Research Institute for environmental medicine'.", "About", JOptionPane.INFORMATION_MESSAGE));
		menu.add(item);
		
		item = new JMenuItem("View on GitHub");
		item.addActionListener(actionEvent -> {
			try {
				browseURL("https://github.com/RUB-Bioinf/CellomicsArrayScanPuzzleHelper");
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(PuzzleHelperGUI.this, "Failed to browse URL: " + e.getMessage());
			}
		});
		
		menu.add(item);
		bar.add(menu);
		setJMenuBar(bar);
		
		mirrorColumnTilingCB.addActionListener(actionEvent -> requestUpdate());
		mirrorRowTilingCB.addActionListener(actionEvent -> requestUpdate());
		refreshBT.addActionListener(actionEvent -> update());
		highlightPuzzleCB.addActionListener(actionEvent -> update());
		directionCB.addActionListener(actionEvent -> requestUpdate());
		button3.addActionListener(actionEvent -> previewPL.incrementFontSize());
		button2.addActionListener(actionEvent -> previewPL.decrementFontSize());
		exportInstructionFileButton.addActionListener(actionEvent -> {
			try {
				actionExportFile();
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(PuzzleHelperGUI.this, "Failed to save data to the file:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		autoReadImageSizesButton.addActionListener(actionEvent -> autoReadImageSize());
		importBT.addActionListener(actionEvent -> {
			try {
				actionImportFile();
			} catch (IOException | ParserConfigurationException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(PuzzleHelperGUI.this, "Failed to read instruction file:\n" + e.getMessage(), CellomicsPuzzleHelper.NAME, JOptionPane.ERROR_MESSAGE);
			}
		});
		
		exportInstructionFileButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				update();
				
				Font f = partitionsWarningLB.getFont();
				f=f.deriveFont(f.getStyle() | Font.BOLD);
				partitionsWarningLB.setForeground(Color.RED);
				partitionsWarningLB.setFont(f);
				
				if(partitionsWarningLB.isVisible()){
					Toolkit.getDefaultToolkit().beep();
				}
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				update();
				
				Font f = partitionsWarningLB.getFont();
				f=f.deriveFont(f.getStyle() & ~Font.BOLD);
				partitionsWarningLB.setForeground(Color.BLACK);
				partitionsWarningLB.setFont(f);
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
	
	private void actionExportFile() throws IOException {
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
				actionExportFile();
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
		map.put(INSTRUCTION_MIRROR_COLUMN_TILING, String.valueOf(mirrorColumnTilingCB.isSelected()));
		map.put(INSTRUCTION_MIRROR_ROW_TILING, String.valueOf(mirrorRowTilingCB.isSelected()));
		map.put(INSTRUCTION_PFLIPROW, String.valueOf(flipRowCB.isSelected()));
		map.put(INSTRUCTION_PFLIPRESULT, String.valueOf(flipFinalImageCB.isSelected()));
		map.put(INSTRUCTION_MAGNIFICATION, String.valueOf(getMagnification()));
		map.put(INSTRUCTION_SEPARATOR, String.valueOf(separatorTF.getText()));
		map.put(INSTRUCTION_VERSION, CellomicsPuzzleHelper.VERSION);
		
		FileOutputStream fout = new FileOutputStream(f);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fout));
		bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		bw.newLine();
		bw.write("<" + INSTRUCTION_BASE_FILE + ">");
		
		ArrayList<String> args = new ArrayList<>();
		args.addAll(map.keySet());
		Collections.sort(args);
		
		for (String s : args) {
			bw.newLine();
			bw.write("\t<" + s + ">" + map.get(s) + "</" + s + ">");
		}
		
		bw.newLine();
		printPartitionsAsXML(bw);
		
		bw.newLine();
		bw.write("</" + INSTRUCTION_BASE_FILE + ">");
		bw.close();
	}
	
	public void printPartitionsAsXML(BufferedWriter bw) throws IOException {
		bw.write("\t<" + INSTRUCTION_PARTITION + ">");
		bw.newLine();
		int partitionIndex = 0;
		for (int i = 0; i < partitionsTabbedPane.getTabCount(); i++) {
			Component c = partitionsTabbedPane.getComponentAt(i);
			if (c instanceof WellPartitionPanel) {
				WellPartitionPanel p = (WellPartitionPanel) c;
				partitionIndex++;
				ArrayList<WellPartitionPanel.WellRectangle> usedWellList = p.getUsedWells();
				ArrayList<WellPartitionPanel.WellRectangle> controlWellList = p.getControlWells();
				Collections.sort(usedWellList);
				Collections.sort(controlWellList);
				
				bw.write("\t\t<p" + partitionIndex + ">");
				bw.newLine();
				
				bw.write("\t\t\t<" + INSTRUCTION_PARTITION_USED + ">");
				for (int j = 0; j < usedWellList.size(); j++) {
					WellPartitionPanel.WellRectangle r = usedWellList.get(j);
					if (j != 0) {
						bw.write(";");
					}
					bw.write(r.getNameShort());
				}
				bw.write("</" + INSTRUCTION_PARTITION_USED + ">");
				bw.newLine();
				
				bw.write("\t\t\t<" + INSTRUCTION_PARTITION_CONTROL + ">");
				for (int j = 0; j < controlWellList.size(); j++) {
					WellPartitionPanel.WellRectangle r = controlWellList.get(j);
					if (j != 0) {
						bw.write(";");
					}
					bw.write(r.getNameShort());
				}
				bw.write("</" + INSTRUCTION_PARTITION_CONTROL + ">");
				bw.newLine();
				
				bw.write("\t\t</p" + partitionIndex + ">");
				bw.newLine();
			}
		}
		bw.write("\t</" + INSTRUCTION_PARTITION + ">");
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
	
	private void actionImportFile() throws IOException, ParserConfigurationException {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select instruction file");
		chooser.setFileFilter(exportFileFilter);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setApproveButtonText("Import");
		int inputResult = chooser.showOpenDialog(this);
		
		if (inputResult != JFileChooser.APPROVE_OPTION) return;
		File f = chooser.getSelectedFile();
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document;
		try {
			document = documentBuilder.parse(f);
		} catch (SAXException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "The selected file is in the wrong format.");
			return;
		}
		
		if (document.getElementsByTagName(INSTRUCTION_VERSION).getLength() == 0) {
			JOptionPane.showMessageDialog(this, "Failed to read the Version on this file. This could lead to unexpected behaviour.");
		} else {
			if (!document.getElementsByTagName(INSTRUCTION_VERSION).item(0).getTextContent().equals(CellomicsPuzzleHelper.VERSION)) {
				JOptionPane.showMessageDialog(this, "The instruction file was created from a different version. This could lead to unexpected behaviour.");
			}
		}
		
		try {
			imWidthSP.setValue(Integer.valueOf(document.getElementsByTagName(INSTRUCTION_IMWIDTH).item(0).getTextContent()));
			imHeightSP.setValue(Integer.valueOf(document.getElementsByTagName(INSTRUCTION_IMHEIGHT).item(0).getTextContent()));
			pWidthSP.setValue(Integer.valueOf(document.getElementsByTagName(INSTRUCTION_PWIDTH).item(0).getTextContent()));
			magnificationSP.setValue(Double.valueOf(document.getElementsByTagName(INSTRUCTION_MAGNIFICATION).item(0).getTextContent()));
			pHeightSP.setValue(Integer.valueOf(document.getElementsByTagName(INSTRUCTION_PHEIGHT).item(0).getTextContent()));
			separatorTF.setText(String.valueOf(document.getElementsByTagName(INSTRUCTION_SEPARATOR).item(0).getTextContent()));
			flipFinalImageCB.setSelected(Boolean.valueOf(document.getElementsByTagName(INSTRUCTION_PFLIPRESULT).item(0).getTextContent()));
			flipRowCB.setSelected(Boolean.valueOf(document.getElementsByTagName(INSTRUCTION_PFLIPROW).item(0).getTextContent()));
			mirrorColumnTilingCB.setSelected(Boolean.valueOf(document.getElementsByTagName(INSTRUCTION_MIRROR_ROW_TILING).item(0).getTextContent()));
			mirrorRowTilingCB.setSelected(Boolean.valueOf(document.getElementsByTagName(INSTRUCTION_MIRROR_COLUMN_TILING).item(0).getTextContent()));
			directionCB.setSelectedItem(PuzzleDirection.getViaString(document.getElementsByTagName(INSTRUCTION_PDIR).item(0).getTextContent()));
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "The file did not contain every smart well parameter required by this version and importing stopped. This may lead to unexpected behaviour.", "Error!", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// Reading well partitions
		ArrayList<String> usedWellList = new ArrayList<>();
		ArrayList<String> controlWellList = new ArrayList<>();
		if (document.getElementsByTagName(INSTRUCTION_PARTITION).getLength() == 0) {
			JOptionPane.showMessageDialog(this, "This file has no information about well partitions.");
		} else {
			Element el = (Element) (document.getElementsByTagName(INSTRUCTION_PARTITION).item(0));
			NodeList children = el.getChildNodes();
			
			int partitionCount = 0;
			for (int i = 0; i < children.getLength(); i++) {
				System.out.println("Children: " + i);
				Node currentNode = children.item(i);
				String name = currentNode.getNodeName();
				if (name.startsWith("p")) {
					NodeList wellLists = currentNode.getChildNodes();
					if (wellLists instanceof Element) {
						partitionCount++;
						System.out.println("Found a partition list with node name '" + currentNode.getNodeName() + "' at index " + partitionCount);
						Element e = (Element) wellLists;
						String usedWells = e.getElementsByTagName("UsedWells").item(0).getTextContent();
						String controlWells = e.getElementsByTagName("ControlWells").item(0).getTextContent();
						System.out.println("Used wells: " + usedWells);
						System.out.println("Control wells: " + controlWells);
						
						usedWellList.add(usedWells);
						controlWellList.add(controlWells);
					}
				}
			}
			
			// Applying the read well lists
			partitionsSP.setValue(partitionCount);
			updateWellPartitionTabs();
			for (int i = 0; i < partitionCount; i++) {
				Component c = partitionsTabbedPane.getComponentAt(i);
				if (c instanceof WellPartitionPanel) {
					WellPartitionPanel p = (WellPartitionPanel) c;
					p.importWellListsXML(usedWellList.get(i),controlWellList.get(i));
				}
			}
			updateWellPartitionTabs();
		}
		
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
		try {
			if (autoUpdateCB.isSelected()) update();
		}catch (Exception e){
			e.printStackTrace();
			JOptionPane.showConfirmDialog(this,"Error while trying to update the UI:\n"+e.getMessage(),"Error: "+e.getClass().getName(),JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void update() {
		int w = (int) pWidthSP.getValue();
		int h = (int) pHeightSP.getValue();
		PuzzleDirection direction = (PuzzleDirection) directionCB.getSelectedItem();
		
		boolean mirrorRowTiling = mirrorRowTilingCB.isSelected();
		boolean mirrorColumnTiling = mirrorColumnTilingCB.isSelected();
		boolean highlightPuzzle = highlightPuzzleCB.isSelected();
		
		previewPL.update(w, h, mirrorRowTiling, mirrorColumnTiling, highlightPuzzle, direction);
		imagecountLB.setText("Image count per well: " + w * h + " [" + w + "x" + h + "]");
		
		double magnification = getMagnification();
		magnificationLB.setText("Scaling factor: 1 px represents "+magnification+" \u00B5m");
		
		updateWellPartitionTabs();
		partitionsWarningLB.setVisible(!hasWellPartitionsWithAtLeastOneControl());
	}
	
	private void updateWellPartitionTabs() {
		System.out.println("Updating Well Partition Tabs");
		int currentTabCount = partitionsTabbedPane.getTabCount();
		int partitionSPValue = (int) partitionsSP.getValue();
		
		for (int i = 1; i <= partitionSPValue; i++) {
			if (i > currentTabCount) {
				System.out.println("Partition Index " + i + " exceeds current partition count. Adding a partition.");
				WellPartitionPanel p = new WellPartitionPanel(12, 8);
				partitionsTabbedPane.addTab("Unknown Partition " + i, p);
				p.addListener(this);
				
				p.update();
			}
		}
		
		while (partitionsTabbedPane.getTabCount() > partitionSPValue) {
			partitionsTabbedPane.removeTabAt(partitionsTabbedPane.getTabCount() - 1);
		}
		
		invalidate();
		repaint();
		partitionsTabbedPane.invalidate();
		partitionsTabbedPane.repaint();
	}
	
	public boolean hasWellPartitionsWithAtLeastOneControl(){
		for (int i = 0; i < partitionsTabbedPane.getTabCount(); i++) {
			Component c = partitionsTabbedPane.getComponentAt(i);
			if (c instanceof WellPartitionPanel) {
				WellPartitionPanel p = (WellPartitionPanel) c;
				int controlCount = p.getControlCount();
				if (controlCount == 0) return false;
			}
		}
		return true;
	}
	
	public double getMagnification(){
		DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
		DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
		char sep=symbols.getDecimalSeparator();
		DecimalFormat df = new DecimalFormat("#.###");
		
		double magnification;
		// So, there is a bug within Java to round the double spinner value to the nearest integer using "getValue()"
		// That's why we gotta read the spinner text. Great.
		JSpinner.NumberEditor numEditor = (JSpinner.NumberEditor) magnificationSP.getEditor();
		JFormattedTextField textField = numEditor.getTextField();
		JFormattedTextField.AbstractFormatter formatter = textField.getFormatter();
		String text = textField.getText();
		if (sep==','){
			// Turns out we are on a German Locale. That means we gotta read the text field directly.
			text = text.replace(",",".");
			magnification = Double.parseDouble(text);
		}else{
			magnification=(double) magnificationSP.getValue();
		}
		
		String formattedMagnification = df.format(magnification);
		formattedMagnification=formattedMagnification.replace(",",".");
		return Double.parseDouble(formattedMagnification);
	}
	
	@Override
	public void onWellPartitionPanelChange(WellPartitionPanel source) {
		int partitionSPValue = (int) partitionsSP.getValue();
		if (partitionSPValue <= 0) {
			return;
		}
		
		for (int i = 0; i < partitionsTabbedPane.getTabCount(); i++) {
			Component c = partitionsTabbedPane.getComponentAt(i);
			if (c instanceof WellPartitionPanel) {
				WellPartitionPanel p = (WellPartitionPanel) c;
				int wellCount = p.getUsedWellCount();
				int controlCount = p.getControlCount();
				partitionsTabbedPane.setTitleAt(i, "Partition " + (i + 1) + " [" + wellCount + ";" + controlCount + "]");
			}
		}
		
		update();
	}
}
