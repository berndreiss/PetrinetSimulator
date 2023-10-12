package view;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.TreeMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;

import control.PetrinetController;
import control.PetrinetToolbarInterface;

public class PetrinetToolbar extends JToolBar {

	private static final long serialVersionUID = 1L;

	private static final String IMAGE_ROOT_FOLDER = "/resources/images/PetrinetToolbar/";

	public PetrinetToolbar(PetrinetToolbarInterface controller) {

		// TODO add set view to default button

		this.setMinimumSize(new Dimension(this.getWidth(), this.getHeight() * 2));

		JButton previousButton = makeToolbarButton(ToolbarImage.LEFT, e -> controller.onPrevious(), "Open the previous file", "previous");

		JButton nextButton = makeToolbarButton(ToolbarImage.RIGHT, e -> controller.onNext(), "Open the next file", "next");

		JButton restartButton = makeToolbarButton(ToolbarImage.RESTART, e -> controller.onRestart(), "Reset the petrinet graph", "Reset p");

		JButton plusButton = makeToolbarButton(ToolbarImage.PLUS, e -> controller.onPlus(), "Adds a token to a selected place", "plus");

		JButton minusButton = makeToolbarButton(ToolbarImage.MINUS, e -> controller.onMinus(), "Removes a token from a selected place", "minus");

		JButton resetButton = makeToolbarButton(ToolbarImage.RESET, e -> controller.onReset(), "Reset the reachability graph", "reset r");

		JButton analyseButton = makeToolbarButton(ToolbarImage.ANALYSE, e -> controller.onAnalyse(), "Analyse petrinet and create reachability graph", "analyse");
		
		JButton clearTextButton = makeToolbarButton(ToolbarImage.CLEAR_TEXT, e -> controller.onClear(), "Clear text area", "clear");
		
		JButton undoButton = makeToolbarButton(ToolbarImage.UNDO, e->controller.onUndo(), "Undo last step", "undo");
		
		JButton redoButton = makeToolbarButton(ToolbarImage.REDO, e->controller.onRedo(), "Redo last step", "redo");
		
		JButton openEditButton = makeToolbarButton(ToolbarImage.EDITOR, e->controller.onOpenEditor(), "Open Editor", "editor");
		
		this.add(previousButton);
		this.add(nextButton);
		this.add(analyseButton);
		this.add(restartButton);
		this.add(resetButton);
		this.add(plusButton);
		this.add(minusButton);
		this.add(clearTextButton);
		this.add(undoButton);
		this.add(redoButton);
		this.add(openEditButton);
		
	}

	private JButton makeToolbarButton(ToolbarImage toolbarImage, ActionListener actionListener, String toolTipText,
			String altText) {
		String imgLocation = IMAGE_ROOT_FOLDER + toolbarImage + ".png";
		String imagePath = System.getProperty("user.dir");

		
		JButton button = new JButton() {

			//TODO is this issue resolved without overriding the method?
			// Changing the getToolTipLocation() method to show it above the button because
			// it keeps interfering with the rendering of GraphStreams
//			@Override
//			public Point getToolTipLocation(MouseEvent event) {
//				// Display the tooltip 5 pixels above the button's top edge
//				return new Point(event.getX(), -getToolTipText().length());
//			}
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
			}
		});
		return button;

	}

	private enum ToolbarImage {
		ANALYSE("stats"), RESTART("restart"), RESET("delete"), PLUS("plus"), MINUS("minus"), LEFT("left"), RIGHT("right"),
		UNDO("undo"), REDO("redo"),  CLEAR_TEXT("input"), EDITOR("edit");

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
