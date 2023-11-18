package control;

import static gui.MainFrame.GRAPH_SPLIT_PANE_DEFAULT_RATIO;
import static gui.MainFrame.SPLIT_PANE_DEFAULT_RATIO;

import java.awt.Component;
import java.io.File;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.miv.pherd.IdAlreadyInUseException;

import core.PetrinetAnalyser;
import exceptions.DuplicateIdException;
import exceptions.PetrinetException;
import gui.MainFrame;
import gui.PetrinetMenuController;
import gui.PetrinetPanel;
import gui.PetrinetToolbar;
import gui.PetrinetToolbarController;
import gui.ResizableSplitPane;
import gui.ToolbarMode;
import listeners.ToolbarToggleListener;
import reachabilityGraphLayout.LayoutType;

// TODO: Auto-generated Javadoc
/**
 * The Class MainController.
 */
public class MainController implements PetrinetMenuController, PetrinetToolbarController, ToolbarToggleListener {

	// TODO warn if unsaved changes

	private File lastDirectory;
	private MainFrame parent;

	private PetrinetPanel currentPetrinetPanel;

	private boolean tabAdded;

	private LayoutType layoutType = LayoutType.TREE;

	/**
	 * Instantiates a new main controller.
	 *
	 * @param parent the parent
	 */
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
				setToolbarMode(currentPetrinetPanel.getToolbarMode());

				getFrame().getToolbar().setToolbarTo(currentPetrinetPanel, layoutType);
			}

		});

	}

	// GETTER METHODS

	/**
	 * Gets the frame.
	 *
	 * @return the frame
	 */
	public MainFrame getFrame() {
		return parent;
	}

	/**
	 * Gets the current panel.
	 *
	 * @return the current panel
	 */
	public PetrinetPanel getCurrentPanel() {
		return currentPetrinetPanel;
	}

	/**
	 * Gets the layout type.
	 *
	 * @return the layout type
	 */
	public LayoutType getLayoutType() {
		return layoutType;
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

		PetrinetViewerController controller = currentPetrinetPanel.getPetrinetController();
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

		getFrame().getToolbar().setToolbarTo(currentPetrinetPanel, layoutType);

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

		// for some reason the divider jumps all the way to the left when using look and
		// feel Metal
		if (UIManager.getLookAndFeel().getName().equals("Metal"))
			currentPetrinetPanel.getGraphSplitPane().resetDivider();
	}

	private void setToolbarMode(ToolbarMode toolbarMode) {
		if (currentPetrinetPanel == null)
			return;
		currentPetrinetPanel.setToolbarMode(toolbarMode);
		getFrame().getToolbar().setToolbarMode(toolbarMode);
	}

	@Override
	public void onNew() {
		setNewPanel(null, false);
		onSetSplitPanesDefault();
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

		PetrinetViewerController controller = currentPetrinetPanel.getPetrinetController();

		if (controller.getCurrentFile() == null)
			return;

		setNewPanel(controller.getCurrentFile(), false);

	}

	@Override
	public void onSave() {
		if (currentPetrinetPanel == null)
			return;
		PetrinetViewerController controller = currentPetrinetPanel.getPetrinetController();

		if (controller.getCurrentFile() == null)
			onSaveAs();
		controller.writeToFile();
		setStatusLabel();
	}

	@Override
	public void onSaveAs() {
		if (currentPetrinetPanel == null)
			return;
		PetrinetViewerController controller = currentPetrinetPanel.getPetrinetController();

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
			try {
				results[counter] = (new PetrinetAnalyser(f)).getResults();
				counter++;
			} catch (PetrinetException e) {
				JOptionPane.showMessageDialog(null, "Could not parse file " + f.getName() + " -> " + e.getMessage(), "",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
		parent.print(printResults(results));

	}

	private static String printResults(String[][] strings) {
		String[] header = { "File", " Bounded? ", " Nodes/Edges -- Path length; m, m'" };

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

		if (currentPetrinetPanel == null)
			return;
		
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
		PetrinetViewerController controller = currentPetrinetPanel.getPetrinetController();

		File currentFile = controller.getCurrentFile();

		if (currentFile == null || !currentFile.exists())
			return null;

		File directory = currentFile.getParentFile();

		if (directory == null || !directory.isDirectory())
			return null;

		File[] files = directory.listFiles();

		if (files == null)
			return null;

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

	// PETRINET RELATED METHODS

	@Override
	public void onResetPetrinet() {
		if (currentPetrinetPanel == null)
			return;
		PetrinetViewerController controller = currentPetrinetPanel.getPetrinetController();
		controller.resetPetrinet();
	}

	// EDITOR RELATED METHODS

	@Override
	public void onIncrement() {
		if (currentPetrinetPanel == null)
			return;

		boolean changed = currentPetrinetPanel.getEditor().incrementMarkedPlace();

		if (changed)
			setStatusLabel();

	}

	@Override
	public void onDecrement() {
		if (currentPetrinetPanel == null)
			return;

		boolean changed = currentPetrinetPanel.getEditor().decrementMarkedPlace();

		if (!changed)
			return;

		setStatusLabel();
	}

	@Override
	public void onAddPlace() {
		PetrinetViewerController controller = currentPetrinetPanel.getPetrinetController();

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
			currentPetrinetPanel.getEditor().addPlace(id);
		} catch (DuplicateIdException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		setStatusLabel();
	}

	@Override
	public void onAddTransition() {

		PetrinetViewerController controller = currentPetrinetPanel.getPetrinetController();

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
			currentPetrinetPanel.getEditor().addTransition(id);
		} catch (DuplicateIdException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);

			return;
		}

		setStatusLabel();
	}

	@Override
	public void onRemoveComponent() {
		currentPetrinetPanel.getEditor().removeComponent();
		setStatusLabel();
	}

	@Override
	public void onAddEdge() {

		PetrinetEditorController editor = currentPetrinetPanel.getEditor();

		PetrinetToolbar toolbar = parent.getToolbar();

		if (editor.addsEdge()) {
			editor.abortAddEdge();
			toolbar.toggleAddEdgeButton();
			return;
		}

		String id = null;

		id = JOptionPane.showInputDialog(null, "Enter id for edge:");

		if (id == null)
			return;
		if (id.trim().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Invalid id: the id cannot be empty.", "",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		try {
			editor.addEdge(id);
			toolbar.toggleAddEdgeButton();

		} catch (DuplicateIdException e) {
			JOptionPane.showMessageDialog(null, "Invalid id: the id already exists.", "",
					JOptionPane.INFORMATION_MESSAGE);
		}

	}

	@Override
	public void onEdgeAdded() {
		getFrame().getToolbar().toggleAddEdgeButton();
		setStatusLabel();

	}

	@Override
	public void onRemoveEdge() {
		currentPetrinetPanel.getEditor().toggleRemoveEdge();
		getFrame().getToolbar().toggleRemoveEdgeButton();
	}

	@Override
	public void onEdgeRemoved() {
		getFrame().getToolbar().toggleRemoveEdgeButton();
		setStatusLabel();

	}

	@Override
	public void onAddLabel() {

		boolean changed = currentPetrinetPanel.getEditor().setLabel();

		if (changed)
			setStatusLabel();
	}

	@Override
	public void onZoomInPetrinet() {
		if (currentPetrinetPanel == null)
			return;

		currentPetrinetPanel.zoomInPetrinet();
	}

	@Override
	public void onZoomOutPetrinet() {
		if (currentPetrinetPanel == null)
			return;

		currentPetrinetPanel.zoomOutPetrinet();
	}

	// REACHABILITY GRAPH RELATED METHODS

	@Override
	public void onAnalyse() {
		if (currentPetrinetPanel == null)
			return;

		PetrinetAnalyser analyser = currentPetrinetPanel.analyse();
		String[][] result = { analyser.getResults() };
		parent.print(printResults(result));

		JOptionPane.showMessageDialog(null, "The petrinet is " + (analyser.isBounded() ? "bounded" : "unbounded") + ".",
				"", JOptionPane.INFORMATION_MESSAGE);

	}

	@Override
	public void onReset() {
		if (currentPetrinetPanel == null)
			return;
		currentPetrinetPanel.resetReachabilityGraph();
	}

	@Override
	public void onClearTextArea() {
		parent.clearTextArea();
	}

	@Override
	public void onUndo() {
		if (currentPetrinetPanel == null)
			return;
		currentPetrinetPanel.undo();
	}

	@Override
	public void onRedo() {
		if (currentPetrinetPanel == null)
			return;
		currentPetrinetPanel.redo();
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
	public void onToggleTreeLayout() {
		if (layoutType == LayoutType.TREE)
			return;
		layoutType = LayoutType.TREE;

		parent.getToolbar().toggleTreeLayoutButton();

		if (parent.getTabbedPane().getTabCount() != 0) {
			for (Component comp : parent.getTabbedPane().getComponents())
				((PetrinetPanel) comp).setLayoutType(layoutType);
		}

	}

	@Override
	public void onToggleCircleLayout() {
//		if (layoutType == LayoutType.CIRCLE)
//			return;
		layoutType = LayoutType.CIRCLE;
		parent.getToolbar().toggleCircleLayoutButton();

		if (parent.getTabbedPane().getTabCount() != 0) {
			for (Component comp : parent.getTabbedPane().getComponents())
				((PetrinetPanel) comp).setLayoutType(layoutType);
		}
	}

	@Override
	public void onToggleAutoLayout() {
		if (layoutType == LayoutType.AUTOMATIC)
			return;
		layoutType = LayoutType.AUTOMATIC;

		parent.getToolbar().toggleAutoLayoutButton();
		if (parent.getTabbedPane().getTabCount() != 0) {
			for (Component comp : parent.getTabbedPane().getComponents())
				((PetrinetPanel) comp).setLayoutType(layoutType);
		}
	}

	@Override
	public void onUndoChanged() {

		parent.getToolbar().toggleUndoButton();
	}

	@Override
	public void onRedoChanged() {
		parent.getToolbar().toggleRedoButton();
	}

	// DESIGN/WINDOW RELATED METHODS

	@Override
	public void onSetSplitPanesDefault() {
		ResizableSplitPane mainSplitPane = parent.getSplitPane();

		mainSplitPane.setDividerRatio(SPLIT_PANE_DEFAULT_RATIO);
		mainSplitPane.resetDivider();

		if (currentPetrinetPanel == null)
			return;

		ResizableSplitPane graphSplitPane = currentPetrinetPanel.getGraphSplitPane();
		graphSplitPane.setDividerRatio(GRAPH_SPLIT_PANE_DEFAULT_RATIO);
		graphSplitPane.resetDivider();
	}

	@Override
	public void onChaneLookAndFeel() {
		parent.changeLookAndFeel();
		JTabbedPane tabbedPane = parent.getTabbedPane();
		for (Component comp : tabbedPane.getComponents())
			((PetrinetPanel) comp).setSplitPane();
	}

	@Override
	public void onReadjustDividers() {
		if (currentPetrinetPanel != null)
			currentPetrinetPanel.getGraphSplitPane().resetDivider();
		parent.getSplitPane().resetDivider();
	}
}
