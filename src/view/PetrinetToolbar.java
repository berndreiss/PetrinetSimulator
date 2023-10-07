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

	private static final String IMAGE_ROOT_FOLDER = "/images/PetrinetToolbar/";

	private PetrinetController controller;

	public PetrinetToolbar(PetrinetController controller) {
		this.controller = controller;

		this.setMinimumSize(new Dimension(this.getWidth(), this.getHeight() * 2));

		JButton previousButton = makeToolbarButton(ToolbarImage.LEFT, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				File currentFile = controller.getCurrentFile();
				if (currentFile == null || !currentFile.exists())
					return;
				
				File directory = currentFile.getParentFile();
				
				if (directory == null || !directory.isDirectory())
					return;
				
				File[] files = directory.listFiles();
				
				TreeMap<String, File> tree = new TreeMap<String, File>(String.CASE_INSENSITIVE_ORDER);
				
				
				for (File f: files)
					if (f.getName().contains(".pnml"))
						tree.put(f.getName(), f);
				
				String previousFileString = tree.lowerKey(currentFile.getName());
	
				if (previousFileString == null)
					return;
				
				File previousFile = tree.get(previousFileString);

				if (previousFile != null)
					controller.onFileOpen(previousFile);
					
			}
			
		}, "Open the previous file", "previous");

		JButton nextButton = makeToolbarButton(ToolbarImage.RIGHT, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File currentFile = controller.getCurrentFile();
				if (currentFile == null || !currentFile.exists())
					return;
				
				File directory = currentFile.getParentFile();
				
				if (directory == null || !directory.isDirectory())
					return;
				
				File[] files = directory.listFiles();
				
				TreeMap<String, File> tree = new TreeMap<String, File>(String.CASE_INSENSITIVE_ORDER);
				
				
				for (File f: files)
					if (f.getName().contains(".pnml"))
						tree.put(f.getName(), f);
				
				String nextFileString = tree.higherKey(currentFile.getName());
	
				if (nextFileString == null)
					return;
				
				File nextFile = tree.get(nextFileString);

				if (nextFile != null)
					controller.onFileOpen(nextFile);
			}
		}, "Open the next file", "next");

		JButton restartButton = makeToolbarButton(ToolbarImage.RESTART, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				controller.resetPetrinet();
			}
		}, "Reset the petrinet graph", "Reset p");

		JButton plusButton = makeToolbarButton(ToolbarImage.PLUS, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String markedPlace = controller.getPetrinetGraph().getMarkedNode();

				if (markedPlace == null)
					return;

				controller.incrementPlace(markedPlace);

				controller.getPetrinet().setCurrenStateOriginalState();
				controller.resetPetrinet();
			}
		}, "Adds a token to a selected place", "plus");

		JButton minusButton = makeToolbarButton(ToolbarImage.MINUS, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String markedPlace = controller.getPetrinetGraph().getMarkedNode();

				if (markedPlace == null)
					return;

				controller.decrementPlace(markedPlace);

				controller.getPetrinet().setCurrenStateOriginalState();
				controller.resetPetrinet();				
			}
		}, "Removes a token from a selected place", "minus");
		
		JButton resetButton = makeToolbarButton(ToolbarImage.RESET, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.resetReachabilityGraph();
			}
		}, "Reset the reachability graph", "Reset r");
		
		JButton undoButton = makeToolbarButton(ToolbarImage.UNDO, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

			}
		}, "Undo last step", "undo");

		this.add(previousButton);
		this.add(nextButton);
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
		RESTART("restart"), RESET("delete"), PLUS("plus"), MINUS("minus"), LEFT("left"), RIGHT("right"), UNDO("undo");

		private String name;

		ToolbarImage(String name) {
			this.name = name;

		}

		@Override
		public String toString() {
			return name;
		}

	}

}
