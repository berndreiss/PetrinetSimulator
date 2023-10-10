package control;

import java.awt.BorderLayout;
import java.io.File;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import view.MainFrame;
import view.PetrinetPanel;

public class MainController implements MenuInterface, PetrinetToolbarInterface{

	private File lastDirectory;
	private File currentFile;
	private MainFrame parent;
	
	private PetrinetPanel petrinetPanel;
	
	public MainController(MainFrame parent) {
		this.parent = parent;
		petrinetPanel = new PetrinetPanel(this, null, true);

		parent.getSplitPane().setLeftComponent(petrinetPanel);
	}
	
	public MainFrame getFrame() {
		return parent;
	}
	
	private void setNewPanel(File file) {
		parent.setStatusLabel(file.getName());
		currentFile = file;
		parent.getSplitPane().remove(petrinetPanel);
		petrinetPanel = new PetrinetPanel(this, file);
		parent.getSplitPane().setLeftComponent(petrinetPanel);
	}
	
	@Override
	public void onOpen() {
		JFileChooser fileChooser = new JFileChooser();
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

		if (lastDirectory == null)
			lastDirectory = new File(System.getProperty("user.dir") + "/../ProPra-WS23-Basis/Beispiele/");

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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAdd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClose() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onExit() {
		System.exit(0);
	}

	@Override
	public void onInfo() {

		JOptionPane.showMessageDialog(parent, "java.version = " + System.getProperty("java.version")
		+ "\n\nuser.dir = " + System.getProperty("user.dir") + "\n", "Information",
		JOptionPane.PLAIN_MESSAGE);
	}


	@Override
	public void onPrevious() {

		File previousFile = getPreviousFile();
		
		if (previousFile != null)
			setNewPanel(previousFile);
	}


	@Override
	public void onNext() {
		File nextFile = getNextFile();
		
		if (nextFile != null)
			setNewPanel(nextFile);
	}


	@Override
	public void onRestart() {
		petrinetPanel.resetPetrinet();
	}


	@Override
	public void onPlus() {
		petrinetPanel.incrementPlace();
		parent.setStatusLabel("*" + currentFile.getName());
	}


	@Override
	public void onMinus() {
		
		if (petrinetPanel.getController().getFileChanged())
			parent.setStatusLabel("*" + currentFile.getName());
		
	}


	@Override
	public void onReset() {
		petrinetPanel.getController().resetReachabilityGraph();
	}


	@Override
	public void onAnalyse() {
		petrinetPanel.analyse();
		parent.print(petrinetPanel.getResult());
	}


	@Override
	public void onClear() {
		parent.clearTextArea();
	}


	@Override
	public void onUndo() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onRedo() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onSetDefault() {
		// TODO Auto-generated method stub
	}
	
	
	private File getPreviousFile() {
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


}
