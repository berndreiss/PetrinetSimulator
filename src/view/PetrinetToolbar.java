package view;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;

import control.PetrinetController;

public class PetrinetToolbar extends JToolBar {

	private static final long serialVersionUID = 1L;

	private static final String IMAGE_ROOT_FOLDER = "/resources/images/PetrinetToolbar/";

	private PetrinetController controller;

	public PetrinetToolbar(PetrinetController controller) {
		this.controller = controller;

		// TODO add set view to default button

		this.setMinimumSize(new Dimension(this.getWidth(), this.getHeight() * 2));

		JButton previousButton = makeToolbarButton(ToolbarImage.LEFT,
				e -> controller.onFileOpen(getPreviousFile(controller)), "Open the previous file", "previous");

		JButton nextButton = makeToolbarButton(ToolbarImage.RIGHT, e -> controller.onFileOpen(getNextFile(controller)),
				"Open the next file", "next");

		JButton restartButton = makeToolbarButton(ToolbarImage.RESTART, e -> controller.resetPetrinet(),
				"Reset the petrinet graph", "Reset p");

		JButton plusButton = makeToolbarButton(ToolbarImage.PLUS, e -> {
			String markedPlace = controller.getPetrinetGraph().getMarkedNode();

			if (markedPlace == null)
				return;

			controller.incrementPlace(markedPlace);

			controller.getPetrinet().setCurrenStateOriginalState();
			controller.resetPetrinet();
		}, "Adds a token to a selected place", "plus");

		JButton minusButton = makeToolbarButton(ToolbarImage.MINUS, e -> {
			String markedPlace = controller.getPetrinetGraph().getMarkedNode();

			if (markedPlace == null)
				return;

			controller.decrementPlace(markedPlace);

			controller.getPetrinet().setCurrenStateOriginalState();
			controller.resetPetrinet();
		}, "Removes a token from a selected place", "minus");

		JButton resetButton = makeToolbarButton(ToolbarImage.RESET, e -> controller.resetReachabilityGraph(), "Reset the reachability graph", "reset r");

		JButton analyseButton = makeToolbarButton(ToolbarImage.ANALYSE, e -> controller.analyse(), "Analyse petrinet and create reachability graph", "analyse");
		
		JButton undoButton = makeToolbarButton(ToolbarImage.UNDO, e->{}, "Undo last step", "undo");

		this.add(previousButton);
		this.add(nextButton);
		this.add(analyseButton);
		this.add(restartButton);
		this.add(plusButton);
		this.add(minusButton);
		this.add(resetButton);
		this.add(undoButton);
	}

	private JButton makeToolbarButton(ToolbarImage toolbarImage, ActionListener actionListener, String toolTipText,
			String altText) {
		String imgLocation = IMAGE_ROOT_FOLDER + toolbarImage + ".png";
		String imagePath = System.getProperty("user.dir");

		System.out.println(imagePath);
		System.out.println("HERE");
		
		JButton button = new JButton() {

			// Changing the getToolTipLocation() method to show it above the button because
			// it keeps interfering with the rendering of GraphStreams
			@Override
			public Point getToolTipLocation(MouseEvent event) {
				// Display the tooltip 5 pixels above the button's top edge
				return new Point(event.getX(), -getToolTipText().length());
			}
		};
		button.addActionListener(actionListener);
		button.setToolTipText(toolTipText);

		int sizeInt = 30;

		Dimension size = new Dimension(sizeInt, sizeInt);

		ImageIcon icon = new ImageIcon(imagePath + imgLocation, altText);

		if (imagePath != null) {
			button.setIcon(icon);
		} else {
			button.setText(altText);
			System.err.println("Resource not found: " + imgLocation);
		}

		button.setMaximumSize(size);
		button.setSize(size);
		button.setPreferredSize(size);

		// repaint graph components when mouse leaves because tooltips mess with graph
		// rendering
		button.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent me) {

				ToolTipManager.sharedInstance().setEnabled(true);

			}

			@Override
			public void mouseExited(MouseEvent me) {
				ToolTipManager.sharedInstance().setEnabled(false);
//				controller.getFrame().updateSplitPane(controller);
			}
		});
		return button;

	}

	private enum ToolbarImage {
		ANALYSE("stats"), RESTART("restart"), RESET("delete"), PLUS("plus"), MINUS("minus"), LEFT("left"), RIGHT("right"),
		UNDO("undo");

		private String name;

		ToolbarImage(String name) {
			this.name = name;

		}

		@Override
		public String toString() {
			return name;
		}

	}

	private File getPreviousFile(PetrinetController controller) {
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

	private File getNextFile(PetrinetController controller) {
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

}
