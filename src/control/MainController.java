package control;

import static view.MainFrame.GRAPH_SPLIT_PANE_DEFAULT_RATIO;
import static view.MainFrame.SPLIT_PANE_DEFAULT_RATIO;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import ReachabilityGraphLayout.LayoutTypes;
import datamodel.DuplicateIdException;
import datamodel.PetrinetState;
import util.OnEditedListener;
import view.MainFrame;
import view.PetrinetPanel;
import view.ResizableSplitPane;

public class MainController implements MenuInterface, PetrinetToolbarInterface, OnEditedListener {

	private File lastDirectory;
	private MainFrame parent;

	private PetrinetPanel currentPetrinetPanel;

	private boolean tabAdded;
	
	private LayoutTypes layoutType = LayoutTypes.TREE;

	public MainController(MainFrame parent) {
		this.parent = parent;
				
		lastDirectory = new File(System.getProperty("user.dir") + "/../ProPra-WS23-Basis/Beispiele/");
		setStatusLabel();

		parent.getTabbedPane().addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {

				if (tabAdded) {
					tabAdded = false;// prevent component at selected index to update editorToolbar with outdated
										// instance of PetrinetPanel and therefore highlighting buttons that should not
										// be highlighted
					return;
				}

				JTabbedPane tabbedPane = parent.getTabbedPane();
				int index = tabbedPane.getSelectedIndex();

				if (index < 0)
					return;

				PetrinetPanel panel = (PetrinetPanel) tabbedPane.getComponentAt(index);
				currentPetrinetPanel = panel;

				parent.setStatusLabel(getStatusLabel());
				setToolbarMode(currentPetrinetPanel.getController().getToolbarMode());

				if (currentPetrinetPanel.getController().getToolbarMode() == ToolbarMode.EDITOR) {
					getFrame().getToolbar().setToolbarTo(currentPetrinetPanel.getController().getEditor());
				}
			}

		});

	}

	public MainFrame getFrame() {
		return parent;
	}

	private void setStatusLabel() {

		JTabbedPane tabbedPane = parent.getTabbedPane();

		String labelString = getStatusLabel();

		parent.setStatusLabel(labelString);
		int index = tabbedPane.getSelectedIndex();
		if (index >= 0)
			tabbedPane.setTitleAt(index, getTabString(labelString));

	}

	private String getStatusLabel() {

		if (currentPetrinetPanel == null) {
			return "java.version = " + System.getProperty("java.version") + "  |  user.dir = "
					+ System.getProperty("user.dir");
		}

		PetrinetController controller = currentPetrinetPanel.getController();
		File file = controller.getCurrentFile();

		if (file == null)
			return "*New File";

		else if (controller.getFileChanged())
			return "*" + file.getName();

		else
			return file.getName();

	}

	private void setNewPanel(File file, boolean newTab) {

		tabAdded = true;

		PetrinetPanel newPanel = null;
		try {
			newPanel = new PetrinetPanel(this, file, layoutType);
		} catch (PetrinetException e) {
			JOptionPane.showMessageDialog(null,
					"Could not create panel from file " + file.getName() + " -> " + e.getMessage(), "",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		currentPetrinetPanel = newPanel;

		if (file == null)
			setToolbarMode(ToolbarMode.EDITOR);
		else
			setToolbarMode(ToolbarMode.VIEWER);

		getFrame().getToolbar().setToolbarTo(currentPetrinetPanel.getController().getEditor());

		JTabbedPane tabbedPane = parent.getTabbedPane();

		if (tabbedPane.getTabCount() == 0) {
			newTab = true;
		}
		if (newTab) {
			tabbedPane.add(getTabString(getStatusLabel()), currentPetrinetPanel);
			tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

		} else {
			int index = tabbedPane.getSelectedIndex();
			tabbedPane.insertTab(getTabString(getStatusLabel()), null, currentPetrinetPanel, null, index);
			tabbedPane.setSelectedIndex(index);

			tabbedPane.remove(index + 1);

		}
		setStatusLabel();

	}

	private void setToolbarMode(ToolbarMode toolbarMode) {
		PetrinetController controller = currentPetrinetPanel.getController();
		controller.setToolbarMode(toolbarMode);
		getFrame().getToolbar().setToolbarMode(toolbarMode);
	}

	@Override
	public void onNew() {
		setNewPanel(null, false);
		

	}

	@Override
	public void onOpen() {

		JTabbedPane tabbedPane = parent.getTabbedPane();

		if (tabbedPane.getTabCount() == 0) {
			onOpenInNewTab();
			return;
		}

		File file = getFile();
		if (file == null)
			return;

		setNewPanel(file, false);
	}

	@Override
	public void onOpenInNewTab() {
		File file = getFile();
		if (file == null)
			return;

		setNewPanel(file, true);
	}

	private File getFile() {

		JFileChooser fileChooser = new JFileChooser();

		setFileChosserFilter(fileChooser);

		fileChooser.setCurrentDirectory(lastDirectory);
		int result = fileChooser.showOpenDialog(parent);

		if (result == 0) {
			File file = fileChooser.getSelectedFile();
			lastDirectory = file.getParentFile();
			return file;
		}
		return null;
	}

	private String getTabString(String fileName) {
		if (fileName == null)
			return null;

		if (fileName.length() > 13) {
			fileName = fileName.substring(0, 9);
			return fileName + "...";
		} else
			return fileName;
	}

	@Override
	public void onReload() {
		if (currentPetrinetPanel == null)
			return;

		PetrinetController controller = currentPetrinetPanel.getController();

		if (controller.getCurrentFile() == null)
			return;

		setNewPanel(controller.getCurrentFile(), false);

	}

	@Override
	public void onMergeWith() {

		if (currentPetrinetPanel == null)
			return;

		PetrinetController controller = currentPetrinetPanel.getController();

		JFileChooser fileChooser = new JFileChooser();

		setFileChosserFilter(fileChooser);

		fileChooser.setCurrentDirectory(lastDirectory);
		int result = fileChooser.showOpenDialog(parent);

		if (result == 0) {
			File file = fileChooser.getSelectedFile();
			currentPetrinetPanel.getController().mergeWith(file);
			lastDirectory = file.getParentFile();
		}
		setStatusLabel();
	}

	@Override
	public void onSave() {
		if (currentPetrinetPanel == null)
			return;
		PetrinetController controller = currentPetrinetPanel.getController();

		if (controller.getCurrentFile() == null)
			onSaveAs();
		controller.writeToFile();
		setStatusLabel();
	}

	@Override
	public void onSaveAs() {
		if (currentPetrinetPanel == null)
			return;
		PetrinetController controller = currentPetrinetPanel.getController();

		JFileChooser fileChooser = new JFileChooser();
		setFileChosserFilter(fileChooser);

		fileChooser.setCurrentDirectory(lastDirectory);
		int result = fileChooser.showOpenDialog(parent);
		if (result == 0) {
			File file = fileChooser.getSelectedFile();
			lastDirectory = file.getParentFile();

			controller.writeToFile(file);
			setStatusLabel();
		}

	}

	@Override
	public void onAnalyseMany() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(true);

		setFileChosserFilter(fileChooser);

		fileChooser.setCurrentDirectory(lastDirectory);

		int result = fileChooser.showOpenDialog(parent);

		if (!(result == JFileChooser.APPROVE_OPTION))
			return;

		File[] files = fileChooser.getSelectedFiles();

		lastDirectory = files[0].getParentFile();

		String[][] results = new String[files.length][3];

		int counter = 0;

		for (File f : files) {
			PetrinetController controller = null;
			try {
				controller = new PetrinetController(f, true);
				results[counter] = controller.analyse();
				counter++;
			} catch (PetrinetException e) {
				JOptionPane.showMessageDialog(null, "Could not parse file " + f.getName() + " -> " + e.getMessage(), "",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
		parent.print(printResults(results));

	}

	private static String printResults(String[][] strings) {
		String[] header = { "File", " Finite ", " Nodes/Edges -- Path length; m, m'" };

		int max1 = header[0].length(), max2 = header[1].length(), max3 = header[2].length();

		for (String[] s : strings) {
			max1 = Math.max(max1, s[0].length());
			max2 = Math.max(max2, s[1].length());
			max3 = Math.max(max3, s[2].length());
		}

		String format = "%-" + max1 + "s|%-" + max2 + "s|%-" + max3 + "s\n";

		StringBuilder sb = new StringBuilder();

		sb.append(formatStringForAnalysesOutput(header, format));

		header[0] = String.format("%-" + max1 + "s", " ").replace(' ', '-');
		header[1] = String.format("%-" + max2 + "s", " ").replace(' ', '-');
		header[2] = String.format("%-" + max3 + "s", " ").replace(' ', '-');

		sb.append(formatStringForAnalysesOutput(header, format));

		for (String[] s : strings)
			sb.append(formatStringForAnalysesOutput(s, format));

		sb.append("\n\n");
		return sb.toString();
	}

	private static String formatStringForAnalysesOutput(String[] strings, String format) {

		if (strings.length != 3) {
			if (strings.length > 3)
				System.out.println("String-Array is too long.");
			else
				System.out.println("String-Array is too short.");
			return null;
		}

		return String.format(format, strings[0], strings[1], strings[2]);
	}

	private void setFileChosserFilter(JFileChooser fileChooser) {
		fileChooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "PNML files (.pnml)";
			}

			@Override
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;

				return f.getName().endsWith(".pnml");
			}
		});

	}

	@Override
	public void onClose() {

		JTabbedPane tabbedPane = getFrame().getTabbedPane();

		int index = tabbedPane.getSelectedIndex();

		tabbedPane.remove(index);

		if (tabbedPane.getTabCount() == 0) {
			currentPetrinetPanel = null;
			setStatusLabel();
		}
	}

	@Override
	public void onExit() {
		System.exit(0);
	}

	@Override
	public void onOpenEditor() {
		if (currentPetrinetPanel != null)
			setToolbarMode(ToolbarMode.EDITOR);
	}

	@Override
	public void onCloseEditor() {
		setToolbarMode(ToolbarMode.VIEWER);
	}

	@Override
	public void onInfo() {

		JOptionPane.showMessageDialog(parent, "java.version = " + System.getProperty("java.version") + "\n\nuser.dir = "
				+ System.getProperty("user.dir") + "\n", "Information", JOptionPane.PLAIN_MESSAGE);
	}

	@Override
	public void onPrevious() {

		if (currentPetrinetPanel == null)
			return;

		File previousFile = getFileFromCurrentFile(FileEnum.PREVIOUS_FILE);

		if (previousFile != null) {
			setNewPanel(previousFile, false);

		}

	}

	@Override
	public void onNext() {
		if (currentPetrinetPanel == null)
			return;
		File nextFile = getFileFromCurrentFile(FileEnum.NEXT_FILE);

		if (nextFile != null) {
			setNewPanel(nextFile, false);
		}
	}

	private enum FileEnum {
		NEXT_FILE, PREVIOUS_FILE;
	}

	private File getFileFromCurrentFile(FileEnum fileEnum) {
		PetrinetController controller = currentPetrinetPanel.getController();

		File currentFile = controller.getCurrentFile();

		if (currentFile == null || !currentFile.exists())
			return null;

		File directory = currentFile.getParentFile();

		if (directory == null || !directory.isDirectory())
			return null;

		File[] files = directory.listFiles();

		TreeMap<String, File> tree = new TreeMap<String, File>(String.CASE_INSENSITIVE_ORDER);

		for (File f : files)
			if (f.getName().endsWith(".pnml"))
				tree.put(f.getName(), f);

		String soughtFileString = null;
		if (fileEnum == FileEnum.NEXT_FILE)
			soughtFileString = tree.higherKey(currentFile.getName());
		if (fileEnum == FileEnum.PREVIOUS_FILE)
			soughtFileString = tree.lowerKey(currentFile.getName());

		if (soughtFileString == null)
			return null;

		File nextFile = tree.get(soughtFileString);
		return nextFile;
	}

	@Override
	public void onRestart() {
		if (currentPetrinetPanel == null)
			return;
		PetrinetController controller = currentPetrinetPanel.getController();
		controller.resetPetrinet();
	}

	@Override
	public void onPlus() {
		if (currentPetrinetPanel == null)
			return;
		PetrinetController controller = currentPetrinetPanel.getController();

		boolean changed = controller.incrementMarkedPlace();
		if (changed)
			setStatusLabel();

	}

	@Override
	public void onMinus() {
		if (currentPetrinetPanel == null)
			return;

		PetrinetController controller = currentPetrinetPanel.getController();

		controller.decrementMarkedPlace();

		if (!controller.getFileChanged())
			return;

		setStatusLabel();
	}

	@Override
	public void onReset() {
		if (currentPetrinetPanel == null)
			return;

		currentPetrinetPanel.getController().resetReachabilityGraph();
	}

	@Override
	public void onAnalyse() {
		if (currentPetrinetPanel == null)
			return;
		PetrinetController controller = currentPetrinetPanel.getController();

		String[][] result = { controller.analyse() };
		parent.print(printResults(result));
	}

	@Override
	public void onClear() {
		parent.clearTextArea();
	}

	@Override
	public void onUndo() {
		if (currentPetrinetPanel == null)
			return;

		// TODO Auto-generated method stub

	}

	@Override
	public void onRedo() {
		if (currentPetrinetPanel == null)
			return;
		currentPetrinetPanel.getController().getReachabilityGraph().addingLoop();

		// TODO Auto-generated method stub

	}

	@Override
	public void onSetDefault() {

		ResizableSplitPane mainSplitPane = parent.getSplitPane();

		mainSplitPane.setDefaultRatio(SPLIT_PANE_DEFAULT_RATIO);
		mainSplitPane.resetDivider();

		if (currentPetrinetPanel == null)
			return;

		ResizableSplitPane graphSplitPane = currentPetrinetPanel.getGraphSplitPane();
		graphSplitPane.setDefaultRatio(GRAPH_SPLIT_PANE_DEFAULT_RATIO);
		graphSplitPane.resetDivider();
	}

	@Override
	public void onAddPlace() {
		PetrinetController controller = currentPetrinetPanel.getController();

		String id = null;

		id = JOptionPane.showInputDialog(null, "Enter id for place:");

		if (id == null)
			return;
		if (id.trim().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Invalid id: the id cannot be empty.", "",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		try {
			controller.getEditor().addPlace(id);
		} catch (DuplicateIdException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		setStatusLabel();
	}

	@Override
	public void onAddTransition() {

		PetrinetController controller = currentPetrinetPanel.getController();

		String id = null;

		id = JOptionPane.showInputDialog(null, "Enter id for transition:");

		if (id == null)
			return;
		if (id.trim().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Invalid id: the id cannot be empty.", "",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		try {
			controller.getEditor().addTransition(id);
		} catch (DuplicateIdException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "",
					JOptionPane.INFORMATION_MESSAGE);

			return;
		}

		setStatusLabel();
	}

	@Override
	public void onAddEdge() {
		PetrinetController controller = currentPetrinetPanel.getController();
		String id = null;

		id = JOptionPane.showInputDialog(null, "Enter id for edge:");

		if (id == null)
			return;
		if (id.trim().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Invalid id: the id cannot be empty.", "",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		boolean addToggled = controller.getEditor().toggleAddEdge(id);

		if (!addToggled)
			JOptionPane.showMessageDialog(null, "Invalid id: the id already exists.", "",
					JOptionPane.INFORMATION_MESSAGE);
		else
			getFrame().getToolbar().toggleAddEdgeButton();
	}

	@Override
	public void onAddLabel() {

		// TODO sometimes label is set to null!
		if (!currentPetrinetPanel.nodeMarked())
			return;

		String label = JOptionPane.showInputDialog(null, "Enter label for element:");

		if (label == null)
			return;

		currentPetrinetPanel.getController().setLabel(label);
		setStatusLabel();
	}

	@Override
	public void onRemoveComponent() {
		PetrinetController controller = currentPetrinetPanel.getController();
		controller.getEditor().removeComponent();
		setStatusLabel();
	}

	@Override
	public void onRemoveEdge() {
		currentPetrinetPanel.getController().getEditor().toggleRemoveEdge();
		getFrame().getToolbar().toggleRemoveEdgeButton();
	}

	@Override
	public void onEdgeAdded() {
		getFrame().getToolbar().toggleAddEdgeButton();
		setStatusLabel();

	}

	@Override
	public void onEdgeRemoved() {
		getFrame().getToolbar().toggleRemoveEdgeButton();
		setStatusLabel();

	}

	@Override
	public void onZoomIn() {
		if (currentPetrinetPanel == null)
			return;

		currentPetrinetPanel.zoomInPetrinet();
	}

	@Override
	public void onZoomOut() {
		if (currentPetrinetPanel == null)
			return;

		currentPetrinetPanel.zoomOutPetrinet();
	}

	@Override
	public void onZoomInReachability() {
		if (currentPetrinetPanel == null)
			return;

		currentPetrinetPanel.zoomInReachability();		
	}

	@Override
	public void onZoomOutReachability() {
		if (currentPetrinetPanel == null)
			return;

		currentPetrinetPanel.zoomOutReachability();				
	}

	@Override
	public void onToggleAutoLayout() {
		if (layoutType != LayoutTypes.AUTOMATIC) {
			layoutType = LayoutTypes.AUTOMATIC;

			if (parent.getTabbedPane().getTabCount() != 0) {
				for (Component comp: parent.getTabbedPane().getComponents())
					((PetrinetPanel) comp).setLayoutType(layoutType);
			}
		}
			
		
	}

	@Override
	public void onToggleTreeLayout() {
		if (layoutType != LayoutTypes.TREE) {
			layoutType = LayoutTypes.TREE;

			if (parent.getTabbedPane().getTabCount() != 0) {
				for (Component comp: parent.getTabbedPane().getComponents())
					((PetrinetPanel) comp).setLayoutType(layoutType);
			}
		}		
	}

	@Override
	public void onToggleCircleLayout() {
		if (layoutType != LayoutTypes.CIRCLE) {
			layoutType = LayoutTypes.CIRCLE;

			if (parent.getTabbedPane().getTabCount() != 0) {
				for (Component comp: parent.getTabbedPane().getComponents())
					((PetrinetPanel) comp).setLayoutType(layoutType);
			}
		}		
	}

}
