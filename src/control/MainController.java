package control;

import static gui.MainFrame.GRAPH_SPLIT_PANE_DEFAULT_RATIO;
import static gui.MainFrame.SPLIT_PANE_DEFAULT_RATIO;

import java.awt.Component;
import java.io.File;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

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
import listeners.ToolbarChangedListener;
import reachabilityGraphLayout.LayoutType;

/**
 * <p>
 * A controller managing all the GUI components at the level of the mainframe.
 * </p>
 * 
 * <p>
 * This includes the menu, toolbar, the text area and the panels inside the
 * TabbedPane.
 * </p>
 * 
 */
public class MainController implements PetrinetMenuController, PetrinetToolbarController, ToolbarChangedListener {

	/** The last directory visited. */
	private File workingDirectory;
	/** The mainFrame holding all components. */
	private MainFrame mainFrame;
	/** The petrinet panel that is currently loaded. */
	private PetrinetPanelInterface currentPetrinetPanel;
//	/** Keeps track of whether a tab has been added. */
//	private boolean tabAdded;//TODO remove?
	/** The layout type currently in use. */
	private LayoutType layoutType = LayoutType.TREE;
	/** The toolbar mode. */
	private ToolbarMode toolbarMode = ToolbarMode.VIEWER;
	/** Keeps track of whether boundedness should be shown. */
	private boolean showBoundedness = true;

	/**
	 * Instantiates a new main controller.
	 *
	 * @param mainFrame The main frame.
	 */
	public MainController(MainFrame mainFrame) {
		this.mainFrame = mainFrame;

		// set default dirctory
		workingDirectory = new File(System.getProperty("user.dir") + "/../ProPra-WS23-Basis/Beispiele/");
		// set status label
		setStatusLabel();

		// set listener for tabbed pane -> if tabs are switched panel status label and
		// toolbar need to be updated
		mainFrame.getTabbedPane().addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {

//				if (tabAdded) {//TODO remove?
//					tabAdded = false;// prevent component at selected index to update editorToolbar with outdated
//										// instance of PetrinetPanel and therefore highlighting buttons that should not
//										// be highlighted
//					return;
//				}

				// get the selected tab
				JTabbedPane tabbedPane = mainFrame.getTabbedPane();
				int index = tabbedPane.getSelectedIndex();

				// if last tab was closed set current panel to null and reset status label
				if (tabbedPane.getTabCount() == 0) {
					currentPetrinetPanel = null;
					setStatusLabel();
				}
				// safety measure for case there are no tabs already
				if (index < 0)
					return;

				// get panel and upadte current panel
				PetrinetPanelInterface panel = (PetrinetPanelInterface) tabbedPane.getComponentAt(index);
				currentPetrinetPanel = panel;

				// update status label and toolbar
				mainFrame.setStatusLabel(getStatusLabel());
				setToolbarMode(currentPetrinetPanel.getToolbarMode());
				getFrame().getToolbar().setToolbarTo(currentPetrinetPanel, layoutType);
			}

		});

	}

	// GETTER METHODS

	/**
	 * Get the frame.
	 *
	 * @return the frame
	 */
	public MainFrame getFrame() {
		return mainFrame;
	}

	/**
	 * Get the current panel.
	 *
	 * @return the current panel
	 */
	public PetrinetPanelInterface getCurrentPanel() {
		return currentPetrinetPanel;
	}

	/**
	 * Get the layout type.
	 *
	 * @return the layout type
	 */
	public LayoutType getLayoutType() {
		return layoutType;
	}

	// set status label and currently selected tab to current file if panel is open;
	// to user directory and system information otherwise (see getStatusLabel()
	private void setStatusLabel() {

		// get label string and set label to it
		String labelString = getStatusLabel();
		mainFrame.setStatusLabel(labelString);

		// get tabbed pane and if selected index >= 0 set title to status label
		JTabbedPane tabbedPane = mainFrame.getTabbedPane();
		int index = tabbedPane.getSelectedIndex();
		if (index >= 0)
			tabbedPane.setTitleAt(index, getTabString(labelString));

	}

	// get the status label -> if no panel is open it provides the java version and
	// current working directory; otherwise it it returns "filename" ("*filename"
	// for changed files)
	private String getStatusLabel() {

		// return system information
		if (currentPetrinetPanel == null) {
			return "java.version = " + System.getProperty("java.version") + "  |  user.dir = "
					+ System.getProperty("user.dir");
		}

		// get the current file
		PetrinetViewerController controller = currentPetrinetPanel.getPetrinetViewerController();
		File file = controller.getCurrentFile();

		// if it is null return "*New File", return filname otherwise
		if (file == null)
			return "*New File";
		else if (controller.getFileChanged())
			return "*" + file.getName();
		else
			return file.getName();

	}

	// set up a new panel
	private void setNewPanel(File file, boolean newTab) {

//		tabAdded = true;//TODO remove?

		// ask if changes should be saved, on abort return
		if (currentPetrinetPanel != null && currentPetrinetPanel.getPetrinetViewerController().getFileChanged())
			if (saveDialog())
				return;

		// create panel and catch errors from parsing the file -> return in case
		PetrinetPanel newPanel = null;
		try {
			newPanel = new PetrinetPanel(this, file, layoutType, toolbarMode);
		} catch (PetrinetException e) {
			JOptionPane.showMessageDialog(null,
					"Could not create panel from file " + file.getName() + " -> " + e.getMessage(), "",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// set current panel to new panel
		currentPetrinetPanel = newPanel;

		currentPetrinetPanel.getReachabilityGraph().setShowBoundedness(showBoundedness);

		// if no file has been provided open the editor
		if (file == null)
			setToolbarMode(ToolbarMode.EDITOR);

		// get tabbed pane and create new tab / update old tab
		getFrame().getToolbar().setToolbarTo(currentPetrinetPanel, layoutType);
		JTabbedPane tabbedPane = mainFrame.getTabbedPane();

		// if there are no tabs in the pane we have to create a new tab
		if (tabbedPane.getTabCount() == 0)
			newTab = true;
		if (newTab) {
			tabbedPane.add(getTabString(getStatusLabel()), (Component) currentPetrinetPanel);
			tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

		} else {
			// insert into current index and remove the old tab
			int index = tabbedPane.getSelectedIndex();
			tabbedPane.insertTab(getTabString(getStatusLabel()), null, (Component) currentPetrinetPanel, null, index);
			tabbedPane.setSelectedIndex(index);
			tabbedPane.remove(index + 1);

		}

		// update status label
		setStatusLabel();

		// for some reason the divider jumps all the way to the left when in the editor
		currentPetrinetPanel.getGraphSplitPane().resetDivider();
	}

	// set toolbar, panel and this controller to mode
	private void setToolbarMode(ToolbarMode toolbarMode) {
		if (currentPetrinetPanel != null)
			currentPetrinetPanel.setToolbarMode(toolbarMode);
		getFrame().getToolbar().setToolbarMode(toolbarMode);
		this.toolbarMode = toolbarMode;
	}

	@Override
	public void onNew() {
		setToolbarMode(ToolbarMode.EDITOR);
		setNewPanel(null, false);
		onSetSplitPanesDefault();
	}

	@Override
	public void onOpen() {

		JTabbedPane tabbedPane = mainFrame.getTabbedPane();

		// if there are no tabs open in new tab
		if (tabbedPane.getTabCount() == 0) {
			onOpenInNewTab();
			return;
		}

		// let user choose file
		File file = getFile();
		// no file provided
		if (file == null)
			return;

		setNewPanel(file, false);

	}

	@Override
	public void onOpenInNewTab() {

		// let user choose file
		File file = getFile();

		// no file has been provided
		if (file == null)
			return;

		setNewPanel(file, true);
	}

	// open current working directory and let user choose file
	private File getFile() {

		// the file chooser
		JFileChooser fileChooser = new JFileChooser();
		setFileChooserFilter(fileChooser);
		fileChooser.setCurrentDirectory(workingDirectory);

		// catch result
		int result = fileChooser.showOpenDialog(mainFrame);

		// if file was chosen get it and update current working directory
		if (result == 0) {
			File file = fileChooser.getSelectedFile();
			workingDirectory = file.getParentFile();
			return file;
		}
		return null;
	}

	// get string with filename formatted for tabs (truncated)
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

		// if no panel is open return
		if (currentPetrinetPanel == null)
			return;

		// get controller
		PetrinetViewerController controller = currentPetrinetPanel.getPetrinetViewerController();

		// if no file has been opened return
		if (controller.getCurrentFile() == null)
			return;

		// reload
		setNewPanel(controller.getCurrentFile(), false);

	}

	@Override
	public void onSave() {
		// if no panel is open return
		if (currentPetrinetPanel == null)
			return;

		// get the controller
		PetrinetViewerController controller = currentPetrinetPanel.getPetrinetViewerController();

		// if no file is defined switch to save as
		if (controller.getCurrentFile() == null)
			onSaveAs();

		// write changes and update status label
		controller.writeToFile();
		setStatusLabel();
	}

	@Override
	public void onSaveAs() {

		// safety measure
		if (currentPetrinetPanel == null)
			return;

		// get the controller
		PetrinetViewerController controller = currentPetrinetPanel.getPetrinetViewerController();

		// create a file chooser for getting a file, assigning a filter and setting it
		// to the current working directory
		JFileChooser fileChooser = new JFileChooser();
		setFileChooserFilter(fileChooser);
		fileChooser.setCurrentDirectory(workingDirectory);

		// get file
		int result = fileChooser.showOpenDialog(mainFrame);

		// if file was chosen, save to given path
		if (result == 0) {
			File file = fileChooser.getSelectedFile();
			// reset working directory
			workingDirectory = file.getParentFile();
			controller.writeToFile(file);
			// update status label
			setStatusLabel();
		}

	}

	@Override
	public void onAnalyseMany() {

		// get files to analyse from user
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(true);
		setFileChooserFilter(fileChooser);
		fileChooser.setCurrentDirectory(workingDirectory);

		int result = fileChooser.showOpenDialog(mainFrame);

		// if file chooser was aborted do nothing
		if (!(result == JFileChooser.APPROVE_OPTION))
			return;

		// retrieve files
		File[] files = fileChooser.getSelectedFiles();

		// update working directory
		workingDirectory = files[0].getParentFile();

		// array holding results
		String[][] results = new String[files.length][3];

		// iterate through files and get resutls, catch parsing errors
		for (int i = 0; i < files.length; i++) {
			try {
				results[i] = (new PetrinetAnalyser(files[i])).getResults();
			} catch (PetrinetException e) {
				JOptionPane.showMessageDialog(null,
						"Could not parse file " + files[i].getName() + " -> " + e.getMessage(), "",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}

		// print results
		mainFrame.print(printResults(results));

	}

	// print results provided as array of array of strings in three columns and
	// strings.length rows
	private static String printResults(String[][] strings) {
		String[] header = { "File", " Bounded? ", " Nodes/Edges -- Path length; m, m'" };

		// initialize maximum lengths for columns with header
		int max1 = header[0].length(), max2 = header[1].length(), max3 = header[2].length();

		// get the maximum length of each column by looping through all rows
		for (String[] s : strings) {
			max1 = Math.max(max1, s[0].length());
			max2 = Math.max(max2, s[1].length());
			max3 = Math.max(max3, s[2].length());
		}

		// format the string
		String format = "%-" + max1 + "s|%-" + max2 + "s|%-" + max3 + "s\n";

		// create string builder and append header plus line consisting of '-'
		StringBuilder sb = new StringBuilder();
		sb.append(formatStringForAnalysesOutput(header, format));
		// fill up header array with '-'
		header[0] = String.format("%-" + max1 + "s", " ").replace(' ', '-');
		header[1] = String.format("%-" + max2 + "s", " ").replace(' ', '-');
		header[2] = String.format("%-" + max3 + "s", " ").replace(' ', '-');
		sb.append(formatStringForAnalysesOutput(header, format));

		// append all rows
		for (String[] s : strings)
			sb.append(formatStringForAnalysesOutput(s, format));

		// append new lines and return
		sb.append("\n\n");
		return sb.toString();
	}

	// checks whether array is too long or too short and formats the string
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

	// filter for returning only files ending with ".pnml"
	private void setFileChooserFilter(JFileChooser fileChooser) {
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
		// if no panle is open do nothing
		if (currentPetrinetPanel == null)
			return;

		// ask if changes should be saved, on abort return
		if (currentPetrinetPanel != null && currentPetrinetPanel.getPetrinetViewerController().getFileChanged())
			if (saveDialog())
				return;

		// get tab index and remove tab
		JTabbedPane tabbedPane = getFrame().getTabbedPane();
		int index = tabbedPane.getSelectedIndex();
		tabbedPane.remove(index);

	}

	@Override
	public void onExit() {

		// ask if changes should be saved, on abort return
		if (currentPetrinetPanel != null && currentPetrinetPanel.getPetrinetViewerController().getFileChanged())
			if (saveDialog())
				return;

		System.exit(0);
	}

	// ask if changes should be saved, returns true on abort
	private boolean saveDialog() {
		int input = JOptionPane.showConfirmDialog(null, "There are unsaved changes. Would you like to save?");

		// 0=yes, 1=no, 2=cancel
		switch (input) {
		case 0:
			onSave();
			return false;
		case 2:
			return true;
		}
		return false;
	}

	@Override
	public void onOpenEditor() {
		setToolbarMode(ToolbarMode.EDITOR);
	}

	@Override
	public void onCloseEditor() {

		if (currentPetrinetPanel != null
				&& !currentPetrinetPanel.getPetrinetViewerController().getPetrinet().isConnected()) {
			JOptionPane.showMessageDialog(mainFrame,
					"Petrinet is not connected. You can still save changes but the petrinet can not be shown.",
					"Information", JOptionPane.PLAIN_MESSAGE);
			return;
		}
		setToolbarMode(ToolbarMode.VIEWER);
	}

	@Override
	public void onInfo() {

		JOptionPane.showMessageDialog(mainFrame, "java.version = " + System.getProperty("java.version")
				+ "\n\nuser.dir = " + System.getProperty("user.dir") + "\n", "Information", JOptionPane.PLAIN_MESSAGE);
	}

	private enum FileEnum {
		NEXT_FILE, PREVIOUS_FILE;
	}

	@Override
	public void onPrevious() {

		// safety measure
		if (currentPetrinetPanel == null)
			return;

		// get previous file
		File previousFile = getFileNextToCurrentFile(FileEnum.PREVIOUS_FILE);

		// set new panel if there is a previous file
		if (previousFile != null) {
			// ask if changes should be saved, on abort return
			if (currentPetrinetPanel != null && currentPetrinetPanel.getPetrinetViewerController().getFileChanged())
				if (saveDialog())
					return;
			setNewPanel(previousFile, false);

		}

	}

	@Override
	public void onNext() {
		// safety measure
		if (currentPetrinetPanel == null)
			return;
		// get previous file
		File nextFile = getFileNextToCurrentFile(FileEnum.NEXT_FILE);
		// set new panel if there is a previous file
		if (nextFile != null) {
			// ask if changes should be saved, on abort return
			if (currentPetrinetPanel != null && currentPetrinetPanel.getPetrinetViewerController().getFileChanged())
				if (saveDialog())
					return;
			setNewPanel(nextFile, false);
		}
	}

	// get previous or next file adjacent to the current file in alphabetical order
	private File getFileNextToCurrentFile(FileEnum fileEnum) {

		// get the controller
		PetrinetViewerController controller = currentPetrinetPanel.getPetrinetViewerController();

		// get current file
		File currentFile = controller.getCurrentFile();

		// safety measure
		if (currentFile == null || !currentFile.exists())
			return null;

		// get directory
		File directory = currentFile.getParentFile();

		// safety measure
		if (directory == null || !directory.isDirectory())
			return null;

		// get all files from directory as array
		File[] files = directory.listFiles();

		// safety measure
		if (files == null)
			return null;

		// tree to sort files
		TreeMap<String, File> tree = new TreeMap<String, File>(String.CASE_INSENSITIVE_ORDER);

		// put files in tree
		for (File f : files)
			if (f.getName().endsWith(".pnml"))
				tree.put(f.getName(), f);

		// get the next/previous file
		String soughtFileString = null;
		if (fileEnum == FileEnum.NEXT_FILE)
			soughtFileString = tree.higherKey(currentFile.getName());
		if (fileEnum == FileEnum.PREVIOUS_FILE)
			soughtFileString = tree.lowerKey(currentFile.getName());

		// if no file was found return
		if (soughtFileString == null)
			return null;

		// get next file and return it
		File nextFile = tree.get(soughtFileString);
		return nextFile;
	}

	@Override
	public void onResetPetrinet() {

		// safety measure
		if (currentPetrinetPanel == null)
			return;

		// get controller and reset petrinet
		PetrinetViewerController controller = currentPetrinetPanel.getPetrinetViewerController();
		controller.resetPetrinet();
	}

	// EDITOR RELATED METHODS
	// TODO save alert on previous / next / open
	@Override
	public void onIncrement() {

		// if there is no panel, return
		if (currentPetrinetPanel == null)
			return;

		// try incrementing the place and get whether there has been a change -> if so,
		// set the status label
		boolean changed = currentPetrinetPanel.getEditor().incrementMarkedPlace();
		if (changed)
			setStatusLabel();

	}

	@Override
	public void onDecrement() {

		// if there is no panel, return
		if (currentPetrinetPanel == null)
			return;

		// try incrementing the place and get whether there has been a change -> if so,
		// set the status label
		boolean changed = currentPetrinetPanel.getEditor().decrementMarkedPlace();
		if (changed)
			setStatusLabel();
	}

	@Override
	public void onAddPlace() {

		if (currentPetrinetPanel == null)
			return;

		// id for element
		String id = null;

		// get id from user
		id = JOptionPane.showInputDialog(null, "Enter id for place:");

		// safety measure
		if (id == null)
			return;
		// show error message if id is empty
		if (id.trim().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Invalid id: the id cannot be empty.", "",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		// try adding the place and catch duplicate id exception
		try {
			currentPetrinetPanel.getEditor().addPlace(id);
		} catch (DuplicateIdException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// set the status label
		setStatusLabel();
	}

	@Override
	public void onAddTransition() {

		if (currentPetrinetPanel == null)
			return;

		// id for element
		String id = null;

		// get id from user
		id = JOptionPane.showInputDialog(null, "Enter id for transition:");

		// safety measure
		if (id == null)
			return;

		// show error message if id is empty
		if (id.trim().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Invalid id: the id cannot be empty.", "",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// try adding the transition and catch duplicate id exception
		try {
			currentPetrinetPanel.getEditor().addTransition(id);
		} catch (DuplicateIdException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);

			return;
		}

		// set the status label
		setStatusLabel();
	}

	@Override
	public void onRemoveComponent() {
		if (currentPetrinetPanel == null)
			return;
		currentPetrinetPanel.getEditor().removeComponent();
		setStatusLabel();
	}

	@Override
	public void onAddEdge() {
		// safety measure
		if (currentPetrinetPanel == null)
			return;

		// get the editor
		PetrinetEditorController editor = currentPetrinetPanel.getEditor();

		// get the toolbar
		PetrinetToolbar toolbar = mainFrame.getToolbar();

		// if editor is already in add edge mode, abort adding edge, update toolbar and
		// return
		if (editor.addsEdge()) {
			editor.abortAddEdge();
			toolbar.toggleAddEdgeButton();
			return;
		}

		// id for edge
		String id = null;

		// get id from user
		id = JOptionPane.showInputDialog(null, "Enter id for edge:");

		// safety measure
		if (id == null)
			return;

		// show error message if id is empty
		if (id.trim().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Invalid id: the id cannot be empty.", "",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// try invoking add edge and catch duplicate id exception
		try {
			editor.addEdge(id);
			toolbar.toggleAddEdgeButton();

		} catch (DuplicateIdException e) {
			JOptionPane.showMessageDialog(null, "Invalid id: the id already exists.", "",
					JOptionPane.INFORMATION_MESSAGE);
		}

		// set the status label
		setStatusLabel();
	}

	@Override
	public void onEdgeAdded() {
		getFrame().getToolbar().toggleAddEdgeButton();
		setStatusLabel();

	}

	@Override
	public void onRemoveEdge() {
		// safety measure
		if (currentPetrinetPanel == null)
			return;

		// get the editor
		PetrinetEditorController editor = currentPetrinetPanel.getEditor();

		// get the toolbar
		PetrinetToolbar toolbar = mainFrame.getToolbar();

		editor.removeEdge();
		toolbar.toggleRemoveEdgeButton();
	}

	@Override
	public void onEdgeRemoved() {
		getFrame().getToolbar().toggleRemoveEdgeButton();
		setStatusLabel();

	}

	@Override
	public void onAddLabel() {

		if (currentPetrinetPanel == null)
			return;

		boolean changed = currentPetrinetPanel.getEditor().setLabel();

		if (changed)
			setStatusLabel();
	}

	@Override
	public void onZoomInPetrinet() {
		// safety measure
		if (currentPetrinetPanel == null)
			return;

		currentPetrinetPanel.zoomInPetrinet();
	}

	@Override
	public void onZoomOutPetrinet() {
		// safety measure
		if (currentPetrinetPanel == null)
			return;

		currentPetrinetPanel.zoomOutPetrinet();
	}

	// REACHABILITY GRAPH RELATED METHODS

	@Override
	public void onAnalyse() {

		// safety measure
		if (currentPetrinetPanel == null)
			return;

		// analyse the petrinet and get the analyser
		PetrinetAnalyser analyser = currentPetrinetPanel.getAnalyser();

		// get result of analysis
		String[][] result = { analyser.getResults() };

		// print results
		mainFrame.print(printResults(result));

		// show information to user
		JOptionPane.showMessageDialog(null, "The petrinet is " + (analyser.isBounded() ? "bounded" : "unbounded") + ".",
				"", JOptionPane.INFORMATION_MESSAGE);

	}

	@Override
	public void onReset() {

		// safety measure
		if (currentPetrinetPanel == null)
			return;
		currentPetrinetPanel.resetReachabilityGraph();
	}

	@Override
	public void onClearTextArea() {
		mainFrame.clearTextArea();
	}

	@Override
	public void onUndo() {
		// safety measure
		if (currentPetrinetPanel == null)
			return;
		currentPetrinetPanel.undo();
	}

	@Override
	public void onRedo() {
		// safety measure
		if (currentPetrinetPanel == null)
			return;
		currentPetrinetPanel.redo();
	}

	@Override
	public void onZoomInReachability() {
		// safety measure
		if (currentPetrinetPanel == null)
			return;

		currentPetrinetPanel.zoomInReachability();
	}

	@Override
	public void onZoomOutReachability() {
		// safety measure
		if (currentPetrinetPanel == null)
			return;

		currentPetrinetPanel.zoomOutReachability();
	}

	@Override
	public void onToggleTreeLayout() {
		// layout is already of type TREE
		if (layoutType == LayoutType.TREE)
			return;

		layoutType = LayoutType.TREE;

		// update every panel in the tabbed pane to tree layout
		if (mainFrame.getTabbedPane().getTabCount() != 0) {
			for (Component comp : mainFrame.getTabbedPane().getComponents())
				((PetrinetPanel) comp).setLayoutType(layoutType);
		}

		// set the toolbar buttons
		getFrame().getToolbar().setToolbarTo(currentPetrinetPanel, layoutType);
	}

	@Override
	public void onToggleCircleLayout() {

		layoutType = LayoutType.CIRCLE;

		// update every panel in the tabbed pane to circle layout
		if (mainFrame.getTabbedPane().getTabCount() != 0) {
			for (Component comp : mainFrame.getTabbedPane().getComponents())
				((PetrinetPanel) comp).setLayoutType(layoutType);
		}

		// set the toolbar buttons
		getFrame().getToolbar().setToolbarTo(currentPetrinetPanel, layoutType);
	}

	@Override
	public void onToggleAutoLayout() {

		// layout is already of type AUTOMATIC
		if (layoutType == LayoutType.AUTOMATIC)
			return;

		layoutType = LayoutType.AUTOMATIC;

		// update every panel in the tabbed pane to automatic layout
		if (mainFrame.getTabbedPane().getTabCount() != 0) {
			for (Component comp : mainFrame.getTabbedPane().getComponents())
				((PetrinetPanel) comp).setLayoutType(layoutType);
		}
		// set the toolbar buttons
		getFrame().getToolbar().setToolbarTo(currentPetrinetPanel, layoutType);
	}

	@Override
	public void onSetUndoButton(boolean highlight) {
		mainFrame.getToolbar().setUndoButton(highlight);
	}

	@Override
	public void onSetRedoButton(boolean highlight) {
		mainFrame.getToolbar().setRedoButton(highlight);
	}

	// DESIGN/WINDOW RELATED METHODS

	@Override
	public void onSetSplitPanesDefault() {

		// get the split pane in the main frame
		ResizableSplitPane mainSplitPane = mainFrame.getSplitPane();

		// set divider ratio to default and reset divider
		mainSplitPane.setDividerRatio(SPLIT_PANE_DEFAULT_RATIO);
		mainSplitPane.resetDivider();

		// safety measure
		if (currentPetrinetPanel == null)
			return;

		// update divider for the split pane in the current petrinet panel
		ResizableSplitPane graphSplitPane = currentPetrinetPanel.getGraphSplitPane();
		graphSplitPane.setDividerRatio(GRAPH_SPLIT_PANE_DEFAULT_RATIO);
		graphSplitPane.resetDivider();
	}

	@Override
	public void onChaneLookAndFeel() {
		mainFrame.changeLookAndFeel();

		// reset the split pane for every petrinet panel in the tabbed pane -> if
		// components are not redone they are not displayed properly
		JTabbedPane tabbedPane = mainFrame.getTabbedPane();
		for (Component comp : tabbedPane.getComponents())
			((PetrinetPanel) comp).setSplitPane();
		setToolbarMode(toolbarMode);
	}

	@Override
	public void onReadjustDividers() {
		if (currentPetrinetPanel != null)
			currentPetrinetPanel.getGraphSplitPane().resetDivider();
		mainFrame.getSplitPane().resetDivider();
	}

	@Override
	public void resetUndoRedoButtons() {
		mainFrame.getToolbar().resetUndoRedoButtons();
	}

	@Override
	public void enableAutomaticBoundednessCheck() {
		for (int i = 0; i < mainFrame.getTabbedPane().getComponentCount(); i++)
			((PetrinetPanelInterface) mainFrame.getTabbedPane().getComponentAt(i)).getReachabilityGraph()
					.setShowBoundedness(true);
		showBoundedness = true;

	}

	@Override
	public void disableAutomaticBoundednessCheck() {
		for (int i = 0; i < mainFrame.getTabbedPane().getComponentCount(); i++)
			((PetrinetPanelInterface) mainFrame.getTabbedPane().getComponentAt(i)).getReachabilityGraph()
					.setShowBoundedness(false);
		showBoundedness = false;
	}

	/**
	 * Gets whether boundedness is shown in real time.
	 * 
	 * @return whether boudedness is shown in real time
	 */
	public boolean getShowBoundedness() {
		return showBoundedness;
	}
}
