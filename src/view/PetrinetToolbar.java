package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import control.PetrinetToolbarInterface;
import control.ToolbarMode;
import util.Editor;

public class PetrinetToolbar extends JToolBar {

	private static final long serialVersionUID = 1L;

	private static final String IMAGE_ROOT_FOLDER = "/resources/images/Toolbar/";

	private Color buttonDefaultColor;

	private Color buttonHighlightColor = Color.LIGHT_GRAY;

	private boolean docked = true;

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

	private JButton zoomInReachabilityButton;
	private JButton zoomOutReachabilityButton;
	private JButton toggleTreeLayoutButton;// TODO find Icon
	private JButton toggleCircleLayoutButton;// TODO find Icon
	private JButton toggleAutoLayoutButton;// TODO find Icon
	
	private ToolbarMode toolbarMode = ToolbarMode.VIEWER;

	private boolean startUp = true;
	
	public PetrinetToolbar(PetrinetToolbarInterface controller) {

		LayoutManager layout = this.getLayout();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		Component glue = Box.createHorizontalGlue();
		JSeparator separator = new JSeparator();
		separator.setVisible(false);
		add(Box.createHorizontalStrut(10));
		addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {

				// force toolbar to be horizontal when not docked so that it is detectable, that
				// it has docked to east or west (being vertical)
				// important for setting the divisor correctly in ResizableSplitPane
				if ("ancestor".equals(evt.getPropertyName())) {

					if (evt.getNewValue() == null) {
						return;
					}
					String eventString = evt.getNewValue().toString();

					if (!eventString.contains("JPanel"))
						return;

					int firstNumber = Integer.parseInt(eventString.split("JPanel")[1].split(",")[1]); // TODO what do
																										// these numbers
																										// represent?
					int secondNumber = Integer.parseInt(eventString.split("JPanel")[1].split(",")[2]); // TODO what do
																										// these numbers
																										// represent?

					if (evt.getNewValue() != null && firstNumber == 0 && secondNumber == 0) {
						setOrientation(SwingConstants.HORIZONTAL);
						controller.onSetDefault();
						setLayout(new BoxLayout((JToolBar) evt.getSource(), BoxLayout.X_AXIS));
						glue.setVisible(true);
						separator.setOrientation(SwingConstants.VERTICAL);
						
						if (!startUp) {
							separator.setVisible(true);
							
						}
							if (startUp)
							startUp = false;

					} else if (getOrientation() == SwingConstants.VERTICAL) {
						controller.onSetDefault();
						setLayout(layout);
						glue.setVisible(false);
						separator.setOrientation(SwingConstants.HORIZONTAL);
						separator.setVisible(true);
						startUp = true;
					} else {
						separator.setVisible(false);
						glue.setVisible(true);
						startUp = false;
					}

				}
			}
		});

		JButton openButton = makeToolbarButton(ToolbarImage.OPEN, e -> controller.onOpen(), "Open file", "open");

		buttonDefaultColor = openButton.getBackground();

		JButton saveButton = makeToolbarButton(ToolbarImage.SAVE, e -> controller.onSave(), "Save file", "save");

		JButton previousButton = makeToolbarButton(ToolbarImage.LEFT, e -> controller.onPrevious(),
				"Open the previous file", "previous");

		JButton nextButton = makeToolbarButton(ToolbarImage.RIGHT, e -> controller.onNext(), "Open the next file",
				"next");

		restartButton = makeToolbarButton(ToolbarImage.RESTART, e -> controller.onRestart(), "Reset the petrinet graph",
				"Reset p");

		JButton plusButton = makeToolbarButton(ToolbarImage.PLUS, e -> controller.onPlus(),
				"Adds a token to a selected place", "plus");

		JButton minusButton = makeToolbarButton(ToolbarImage.MINUS, e -> controller.onMinus(),
				"Removes a token from a selected place", "minus");

		resetButton = makeToolbarButton(ToolbarImage.RESET, e -> controller.onReset(), "Reset the reachability graph",
				"reset r");

		analyseButton = makeToolbarButton(ToolbarImage.ANALYSE, e -> controller.onAnalyse(),
				"Analyse petrinet and create reachability graph", "analyse");

		clearTextButton = makeToolbarButton(ToolbarImage.CLEAR_TEXT, e -> controller.onClear(), "Clear text area",
				"clear");

		JButton setDefaultButton = makeToolbarButton(ToolbarImage.DEFAULT, e -> controller.onSetDefault(),
				"Reset split panes", "reset pane");

		undoButton = makeToolbarButton(ToolbarImage.UNDO, e -> controller.onUndo(), "Undo last step", "undo");

		redoButton = makeToolbarButton(ToolbarImage.REDO, e -> controller.onRedo(), "Redo last step", "redo");

		JButton zoomInButton = makeToolbarButton(ToolbarImage.ZOOM_IN, e -> controller.onZoomIn(), "Zoom in",
				"zoom in");

		JButton zoomOutButton = makeToolbarButton(ToolbarImage.ZOOM_OUT, e -> controller.onZoomOut(), "Zoom out",
				"zoom out");

		openEditorButton = makeToolbarButton(ToolbarImage.EDITOR, e -> controller.onOpenEditor(), "Switch to editor",
				"editor");

		// Editor specific Buttons

		addPlaceButton = makeToolbarButton(ToolbarImage.ADD_PLACE, e -> controller.onAddPlace(), "Add place",
				"add place");

		addTransitionButton = makeToolbarButton(ToolbarImage.ADD_TRANSITION, e -> controller.onAddTransition(),
				"Add transition", "add trans");

		deleteComponentButton = makeToolbarButton(ToolbarImage.DELETE_COMPONENT, e -> controller.onRemoveComponent(),
				"Delete component", "delete");

		addEdgeButton = makeToolbarButton(ToolbarImage.ADD_EDGE, e -> controller.onAddEdge(), "Add edge", "add edge");

		removeEdgeButton = makeToolbarButton(ToolbarImage.REMOVE_EDGE, e -> controller.onRemoveEdge(),
				"Remove an edge: choose source first and then target", "remove edge");

		addLabelButton = makeToolbarButton(ToolbarImage.ADD_LABEL, e -> controller.onAddLabel(), "Add label to element",
				"add label");

		closeEditorButton = makeToolbarButton(ToolbarImage.OPEN_VIEWER, e -> controller.onCloseEditor(),
				"Switch to viewer", "close editor");

		zoomInReachabilityButton = makeToolbarButton(ToolbarImage.ZOOM_IN, e -> controller.onZoomInReachability(),
				"Zoom in", "zoom in");

		zoomOutReachabilityButton = makeToolbarButton(ToolbarImage.ZOOM_OUT, e -> controller.onZoomOutReachability(),
				"Zoom out", "zoom out");

		toggleTreeLayoutButton = makeToolbarButton(ToolbarImage.TREE_LAYOUT, e -> controller.onToggleTreeLayout(),
				"Turn tree base layout on -> will give the best experience", "tree-layout");
		
		toggleCircleLayoutButton = makeToolbarButton(ToolbarImage.CIRCLE_LAYOUT, e -> controller.onToggleCircleLayout(),
				"Turn circe based layout on -> might not be as beautiful but may be more fun", "circle-layout");
		
		toggleAutoLayoutButton = makeToolbarButton(ToolbarImage.AUTO_LAYOUT, e -> controller.onToggleAutoLayout(),
				"Turn auto layout on -> auto layout provided by GraphStream", "auto-layout");

		this.add(openButton);
		this.add(saveButton);
		this.add(previousButton);
		this.add(nextButton);
		this.add(plusButton);
		this.add(minusButton);

		// Viewer alternative buttons

		this.add(analyseButton);
		this.add(restartButton);
		this.add(resetButton);
		this.add(undoButton);
		this.add(redoButton);
		this.add(clearTextButton);

		//

		// Editor alternative buttons

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

		add(glue);
		add(separator);

		// ReachabilityGraph buttons

		this.add(zoomInReachabilityButton);
		this.add(zoomOutReachabilityButton);
		this.add(toggleTreeLayoutButton);
		this.add(toggleCircleLayoutButton);
		this.add(toggleAutoLayoutButton);
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

			zoomInReachabilityButton.setVisible(true);
			zoomOutReachabilityButton.setVisible(true);
			toggleTreeLayoutButton.setVisible(true);
			toggleCircleLayoutButton.setVisible(true);
			toggleAutoLayoutButton.setVisible(true);

		} else {
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

			zoomInReachabilityButton.setVisible(false);
			zoomOutReachabilityButton.setVisible(false);
			toggleTreeLayoutButton.setVisible(false);
			toggleCircleLayoutButton.setVisible(false);
			toggleAutoLayoutButton.setVisible(false);

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
		OPEN("folder"), SAVE("save"), ANALYSE("stats"), RESTART("restart"), RESET("delete"), PLUS("plus"),
		MINUS("minus"), LEFT("left"), RIGHT("right"), UNDO("undo"), REDO("redo"), CLEAR_TEXT("input"), EDITOR("edit"),
		DEFAULT("layout"), ZOOM_IN("zoom-in"), ZOOM_OUT("zoom-out"),

		// additional images for Editor
		ADD_PLACE("add-circle"), ADD_TRANSITION("add-square"), ADD_EDGE("arc"), DELETE_COMPONENT("erase"),
		OPEN_VIEWER("eye"), REMOVE_EDGE("remove-edge"), ADD_LABEL("label"), AUTO_LAYOUT("auto-layout"), TREE_LAYOUT("tree-layout"), CIRCLE_LAYOUT("circle-layout");

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
		} else
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
