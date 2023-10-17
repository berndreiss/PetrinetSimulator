package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import control.PetrinetToolbarInterface;
import control.ToolbarMode;
import util.Editor;

public class PetrinetToolbar extends JToolBar {

	private static final long serialVersionUID = 1L;

	
	private static final String IMAGE_ROOT_FOLDER = "/resources/images/Toolbar/";

	
	private Color buttonDefaultColor;

	private Color buttonHighlightColor = Color.LIGHT_GRAY;

	private JButton analyseButton;
	private JButton restartButton;
	private JButton resetButton;
	private JButton undoButton;
	private JButton redoButton;
	private JButton clearTextButton;
	private JButton openEditorButton;

	
	private JButton addPlaceButton;
	private JButton addTransitionButton;
	private JButton deleteComponentButton;
	private JButton addEdgeButton;
	private JButton removeEdgeButton;
	private JButton addLabelButton;
	private JButton closeEditorButton;
	private ToolbarMode toolbarMode = ToolbarMode.VIEWER;


	public PetrinetToolbar(PetrinetToolbarInterface controller) {

		//implement ZOOM
		
		JButton openButton = makeToolbarButton(ToolbarImage.OPEN, e -> controller.onOpen(), "Open file", "open");

		buttonDefaultColor = openButton.getBackground();

		JButton saveButton = makeToolbarButton(ToolbarImage.SAVE, e -> controller.onSave(), "Save file", "save");
		
		JButton previousButton = makeToolbarButton(ToolbarImage.LEFT, e -> controller.onPrevious(), "Open the previous file", "previous");

		JButton nextButton = makeToolbarButton(ToolbarImage.RIGHT, e -> controller.onNext(), "Open the next file", "next");

		restartButton = makeToolbarButton(ToolbarImage.RESTART, e -> controller.onRestart(), "Reset the petrinet graph", "Reset p");

		JButton plusButton = makeToolbarButton(ToolbarImage.PLUS, e -> controller.onPlus(), "Adds a token to a selected place", "plus");

		JButton minusButton = makeToolbarButton(ToolbarImage.MINUS, e -> controller.onMinus(), "Removes a token from a selected place", "minus");

		resetButton = makeToolbarButton(ToolbarImage.RESET, e -> controller.onReset(), "Reset the reachability graph", "reset r");

		analyseButton = makeToolbarButton(ToolbarImage.ANALYSE, e -> controller.onAnalyse(), "Analyse petrinet and create reachability graph", "analyse");
		
		clearTextButton = makeToolbarButton(ToolbarImage.CLEAR_TEXT, e -> controller.onClear(), "Clear text area", "clear");

		JButton setDefaultButton = makeToolbarButton(ToolbarImage.DEFAULT, e -> controller.onSetDefault(), "Reset split panes", "reset pane");
		
		undoButton = makeToolbarButton(ToolbarImage.UNDO, e->controller.onUndo(), "Undo last step", "undo");
		
		redoButton = makeToolbarButton(ToolbarImage.REDO, e->controller.onRedo(), "Redo last step", "redo");
		
		JButton zoomInButton = makeToolbarButton(ToolbarImage.ZOOM_IN, e->controller.onZoomIn(), "Zoom in", "zoom in");

		JButton zoomOutButton = makeToolbarButton(ToolbarImage.ZOOM_OUT, e->controller.onZoomOut(), "Zoom out", "zoom out");

		openEditorButton = makeToolbarButton(ToolbarImage.EDITOR, e->controller.onOpenEditor(), "Switch to editor", "editor");
		
		//Editor specific Buttons
		
		addPlaceButton = makeToolbarButton(ToolbarImage.ADD_PLACE, e -> controller.onAddPlace(), "Add place",
				"add place");

		addTransitionButton = makeToolbarButton(ToolbarImage.ADD_TRANSITION, e -> controller.onAddTransition(),
				"Add transition", "add trans");

		deleteComponentButton = makeToolbarButton(ToolbarImage.DELETE_COMPONENT,
				e -> controller.onRemoveComponent(), "Delete component", "delete");

		addEdgeButton = makeToolbarButton(ToolbarImage.ADD_EDGE, e -> controller.onAddEdge(), "Add edge", "add edge");

		removeEdgeButton = makeToolbarButton(ToolbarImage.REMOVE_EDGE, e -> controller.onRemoveEdge(),
				"Remove an edge: choose source first and then target", "remove edge");

		addLabelButton = makeToolbarButton(ToolbarImage.ADD_LABEL, e->controller.onAddLabel(), "Add label to element", "add label");
		
		closeEditorButton = makeToolbarButton(ToolbarImage.OPEN_VIEWER, e -> controller.onCloseEditor(),
				"Switch to viewer", "close editor");

		
		this.add(openButton);		
		this.add(saveButton);
		this.add(previousButton);
		this.add(nextButton);
		this.add(plusButton);
		this.add(minusButton);

		//Viewer alternative buttons
		
		this.add(analyseButton);
		this.add(restartButton);
		this.add(resetButton);
		this.add(undoButton);
		this.add(redoButton);
		this.add(clearTextButton);
		
		//
		
		//Editor alternative buttons
		
		this.add(addPlaceButton);
		this.add(addTransitionButton);
		this.add(deleteComponentButton);
		this.add(addEdgeButton);
		this.add(removeEdgeButton);
		this.add(addLabelButton);
		//
		
		this.add(zoomInButton);
		this.add(zoomOutButton);
		this.add(setDefaultButton);
		
		
		this.add(openEditorButton);
		this.add(closeEditorButton);
		
		setToolbarMode(ToolbarMode.VIEWER);
		
	}

	public void setToolbarMode(ToolbarMode toolbarMode) {
		this.toolbarMode = toolbarMode;
		
		if (toolbarMode == ToolbarMode.VIEWER) {
			analyseButton.setVisible(true);
			restartButton.setVisible(true);
			resetButton.setVisible(true);
			undoButton.setVisible(true);
			redoButton.setVisible(true);
			clearTextButton.setVisible(true);
			
			addPlaceButton.setVisible(false);
			addTransitionButton.setVisible(false);
			deleteComponentButton.setVisible(false);
			addEdgeButton.setVisible(false);
			removeEdgeButton.setVisible(false);
			addLabelButton.setVisible(false);

			openEditorButton.setVisible(true);
			closeEditorButton.setVisible(false);

		}else {
			analyseButton.setVisible(false);
			restartButton.setVisible(false);
			resetButton.setVisible(false);
			undoButton.setVisible(false);
			redoButton.setVisible(false);
			clearTextButton.setVisible(false);
			
			addPlaceButton.setVisible(true);
			addTransitionButton.setVisible(true);
			deleteComponentButton.setVisible(true);
			addEdgeButton.setVisible(true);
			removeEdgeButton.setVisible(true);
			addLabelButton.setVisible(true);

			openEditorButton.setVisible(false);
			closeEditorButton.setVisible(true);
		}
	}
	

	private JButton makeToolbarButton(ToolbarImage toolbarImage, ActionListener actionListener, String toolTipText,
			String altText) {
		String imgLocation = IMAGE_ROOT_FOLDER + toolbarImage + ".png";
		String imagePath = System.getProperty("user.dir");

		JButton button = new JButton();
		
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
		return button;

	}

	public enum ToolbarImage {
		OPEN("folder"), SAVE("save"), ANALYSE("stats"), RESTART("restart"), RESET("delete"), PLUS("plus"), MINUS("minus"), LEFT("left"),
		RIGHT("right"), UNDO("undo"), REDO("redo"), CLEAR_TEXT("input"), EDITOR("edit"),  DEFAULT("layout"), ZOOM_IN("zoom-in"), ZOOM_OUT("zoom-out"),
		
		//additional images for Editor
		ADD_PLACE("add-circle"), ADD_TRANSITION("add-square"), ADD_EDGE("arc"), DELETE_COMPONENT("erase"), OPEN_VIEWER("eye"), REMOVE_EDGE("remove-edge"), ADD_LABEL("label")
		;

		private String name;

		ToolbarImage(String name) {
			this.name = name;

		}

		@Override
		public String toString() {
			return name;
		}

	}

	public void toggleAddEdgeButton() {
		if (addEdgeButton.getBackground() == buttonDefaultColor) {
			addEdgeButton.setBackground(buttonHighlightColor);
			if (removeEdgeButton.getBackground() == buttonHighlightColor)
				removeEdgeButton.setBackground(buttonDefaultColor);

		} else
			addEdgeButton.setBackground(buttonDefaultColor);
	}

	public void toggleRemoveEdgeButton() {
		if (removeEdgeButton.getBackground() == buttonDefaultColor) {
			removeEdgeButton.setBackground(buttonHighlightColor);
			if (addEdgeButton.getBackground() == buttonHighlightColor)
				addEdgeButton.setBackground(buttonDefaultColor);
		}else
			removeEdgeButton.setBackground(buttonDefaultColor);
	}
	
	public void resetButtons() {
		addEdgeButton.setBackground(buttonDefaultColor);
		removeEdgeButton.setBackground(buttonDefaultColor);
	}
	
	public void setToolbarTo(Editor editor) {
		resetButtons();
		
		if (editor.addsEdge())
			toggleAddEdgeButton();
		if (editor.removesEdge())
			toggleRemoveEdgeButton();
	}

}
