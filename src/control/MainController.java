package control;

import java.awt.Dimension;
import java.io.File;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.filechooser.FileFilter;

import view.MainFrame;
import view.PetrinetPanel;

public class MainController implements MenuInterface, PetrinetToolbarInterface, EditorToolbarInterface {

	private File lastDirectory;
	private MainFrame parent;

	private PetrinetPanel currentPetrinetPanel;

	public MainController(MainFrame parent) {
		this.parent = parent;
		lastDirectory = new File(System.getProperty("user.dir") + "/../ProPra-WS23-Basis/Beispiele/");
		init();
	}

	private void init() {
		JPanel dummyPanel = new JPanel();
		dummyPanel.setPreferredSize(
				new Dimension(getFrame().getWidth(), (int) (getFrame().getWidth() * MainFrame.GRAPH_PERCENT)));
		parent.getSplitPane().setLeftComponent(dummyPanel);
		parent.setStatusLabel("java.version = " + System.getProperty("java.version") + "  |  user.dir = "
				+ System.getProperty("user.dir"));
	}

	public MainFrame getFrame() {
		return parent;
	}

	private void setNewPanel(File file) {
		if (file == null) {
			parent.setStatusLabel("*New File");
		} else {
			parent.setStatusLabel(file.getName());
		}
		JSplitPane splitPane = parent.getSplitPane();
		splitPane.remove(splitPane.getLeftComponent());
				
		currentPetrinetPanel = new PetrinetPanel(this, file);
		parent.getSplitPane().setLeftComponent(currentPetrinetPanel);
		
		currentPetrinetPanel.getController().setToolbarMode(getFrame().getTooolbarMode());
	}

	private void setToolbarMode(ToolbarMode toolbarMode) {
		PetrinetController controller = currentPetrinetPanel.getController();
		controller.setToolbarMode(toolbarMode);
		getFrame().setToolBarMode(toolbarMode);
	}

	@Override
	public void onNew() {
		setNewPanel(null);
		setToolbarMode(ToolbarMode.EDITOR);
	}

	@Override
	public void onOpen() {
		JFileChooser fileChooser = new JFileChooser();

		setFileChosserFilter(fileChooser);

		fileChooser.setCurrentDirectory(lastDirectory);
		int result = fileChooser.showOpenDialog(parent);

		if (result == 0) {
			File file = fileChooser.getSelectedFile();
			setNewPanel(file);
			lastDirectory = file.getParentFile();
		}

	}

	@Override
	public void onReload() {
		if (currentPetrinetPanel == null)
			return;
		PetrinetController controller = currentPetrinetPanel.getController();

		if (controller.isNewFile())
			return;
		
		setNewPanel(controller.getCurrentFile());
		
	}

	@Override
	public void onSave() {
		if (currentPetrinetPanel == null)
			return;
		PetrinetController controller = currentPetrinetPanel.getController();

		if (controller.isNewFile())
			onSaveAs();

		// TODO implement
	}

	@Override
	public void onSaveAs() {
		if (currentPetrinetPanel == null)
			return;
		PetrinetController controller = currentPetrinetPanel.getController();

		// TODO implement
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
			PetrinetController controller = new PetrinetController(f, true);
			results[counter] = controller.analyse();
			counter++;
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
		currentPetrinetPanel = null;
		init();
		getFrame().setToolBarMode(ToolbarMode.VIEWER);
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

		File previousFile = getPreviousFile();

		if (previousFile != null)
			setNewPanel(previousFile);
	}

	@Override
	public void onNext() {
		if (currentPetrinetPanel == null)
			return;
		File nextFile = getNextFile();

		if (nextFile != null)
			setNewPanel(nextFile);
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

		controller.incrementMarkedPlace();

		parent.setStatusLabel("*" + controller.getCurrentFile().getName());
	}

	@Override
	public void onMinus() {
		if (currentPetrinetPanel == null)
			return;

		PetrinetController controller = currentPetrinetPanel.getController();

		controller.decrementMarkedPlace();

		if (controller.getFileChanged())
			parent.setStatusLabel("*" + controller.getCurrentFile().getName());

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

		controller.analyse();
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

		// TODO Auto-generated method stub

	}

	@Override
	public void onSetDefault() {
		// TODO Auto-generated method stub
	}

	private File getPreviousFile() {
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
			if (f.getName().contains(".pnml"))
				tree.put(f.getName(), f);

		String previousFileString = tree.lowerKey(currentFile.getName());

		if (previousFileString == null)
			return null;

		File previousFile = tree.get(previousFileString);

		return previousFile;
	}

	private File getNextFile() {
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
			if (f.getName().contains(".pnml"))
				tree.put(f.getName(), f);

		String nextFileString = tree.higherKey(currentFile.getName());

		if (nextFileString == null)
			return null;

		File nextFile = tree.get(nextFileString);
		return nextFile;
	}

	@Override
	public void onAddPlace() {
		PetrinetController controller = currentPetrinetPanel.getController();

		String id = null;
		if (!controller.getEditor().addsPlace()) {

			id = JOptionPane.showInputDialog(null, "Enter id for place:");

			if (id == null)
				return;
			if (id.trim().isEmpty()) {
				JOptionPane.showMessageDialog(null, "Invalid id: the id cannot be empty.", "",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		boolean added = controller.getEditor().toggleAddPlace(id);

		if (!added)
			JOptionPane.showMessageDialog(null, "Invalid id: the id already exists.", "",
					JOptionPane.INFORMATION_MESSAGE);

	}

	@Override
	public void onAddTransition() {

		PetrinetController controller = currentPetrinetPanel.getController();

		String id = null;

		if (!controller.getEditor().addsTransition()) {
			id = JOptionPane.showInputDialog(null, "Enter id for transition:");

			if (id == null)
				return;
			if (id.trim().isEmpty()) {
				JOptionPane.showMessageDialog(null, "Invalid id: the id cannot be empty.", "",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		boolean added = controller.getEditor().toggleAddTransition(id);

		if (!added)
			JOptionPane.showMessageDialog(null, "Invalid id: the id already exists.", "",
					JOptionPane.INFORMATION_MESSAGE);

	}

	@Override
	public void onAddEdge() {
		PetrinetController controller = currentPetrinetPanel.getController();
		String id = null;

		if (!controller.getEditor().addsTransition()) {
			id = JOptionPane.showInputDialog(null, "Enter id for transition:");

			if (id == null)
				return;
			if (id.trim().isEmpty()) {
				JOptionPane.showMessageDialog(null, "Invalid id: the id cannot be empty.", "",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		boolean added = controller.getEditor().toggleAddEdge(id);

		if (!added)
			JOptionPane.showMessageDialog(null, "Invalid id: the id already exists.", "",
					JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public void onAddLabel() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemoveComponent() {
		PetrinetController controller = currentPetrinetPanel.getController();

		controller.getEditor().removeComponent();
	}

	@Override
	public void onRemoveEdge() {
		currentPetrinetPanel.getController().getEditor().toggleRemoveEdge();
	}

}
