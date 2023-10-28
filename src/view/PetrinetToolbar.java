package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import ReachabilityGraphLayout.LayoutTypes;
import control.PetrinetController;
import control.PetrinetToolbarInterface;
import control.ToolbarMode;
import datamodel.PetrinetQueue;
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
	private ToolbarButton undoButton;
	private ToolbarButton redoButton;
	private JButton clearTextButton;
	private JButton openEditorButton;

	private JButton addPlaceButton;
	private JButton addTransitionButton;
	private JButton deleteComponentButton;
	private ToolbarButton addEdgeButton;
	private ToolbarButton removeEdgeButton;
	private JButton addLabelButton;
	private JButton closeEditorButton;

	private JButton zoomInReachabilityButton;
	private JButton zoomOutReachabilityButton;
	private ToolbarButton toggleTreeLayoutButton;// TODO find Icon
	private ToolbarButton toggleCircleLayoutButton;// TODO find Icon
	private ToolbarButton toggleAutoLayoutButton;// TODO find Icon

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

						if (!startUp) 
							separator.setVisible(true);

						
						if (startUp)
							startUp = false;
						controller.onSetDefault();

					} else if (getOrientation() == SwingConstants.VERTICAL) {
						controller.onSetDefault();
						setLayout(new BoxLayout((JToolBar) evt.getSource(), BoxLayout.Y_AXIS));
						glue.setVisible(false);
						separator.setOrientation(SwingConstants.HORIZONTAL);
						separator.setVisible(true);
						startUp = true;
						controller.onSetDefault();
					} else {
						setLayout(new BoxLayout((JToolBar) evt.getSource(), BoxLayout.X_AXIS));
						separator.setVisible(false);
						glue.setVisible(true);
						startUp = false;
						controller.onSetDefault();
					}

				}
			}
		});

		ToolbarButton openButton = new ToolbarButton(ToolbarImage.OPEN, e -> controller.onOpen(), "Open file", "open");

		buttonDefaultColor = openButton.getBackground();

		JButton saveButton = new ToolbarButton(ToolbarImage.SAVE, e -> controller.onSave(), "Save file", "save");

		JButton previousButton = new ToolbarButton(ToolbarImage.LEFT, e -> controller.onPrevious(),
				"Open the previous file", "previous");

		JButton nextButton = new ToolbarButton(ToolbarImage.RIGHT, e -> controller.onNext(), "Open the next file",
				"next");

		restartButton = new ToolbarButton(ToolbarImage.RESTART, e -> controller.onRestart(), "Reset the petrinet graph",
				"Reset p");

		JButton plusButton = new ToolbarButton(ToolbarImage.PLUS, e -> controller.onPlus(),
				"Adds a token to a selected place", "plus");

		JButton minusButton = new ToolbarButton(ToolbarImage.MINUS, e -> controller.onMinus(),
				"Removes a token from a selected place", "minus");

		resetButton = new ToolbarButton(ToolbarImage.RESET, e -> controller.onReset(), "Reset the reachability graph",
				"reset r");

		analyseButton = new ToolbarButton(ToolbarImage.ANALYSE, e -> controller.onAnalyse(),
				"Analyse petrinet and create reachability graph", "analyse");

		clearTextButton = new ToolbarButton(ToolbarImage.CLEAR_TEXT, e -> controller.onClear(), "Clear text area",
				"clear");

		JButton setDefaultButton = new ToolbarButton(ToolbarImage.DEFAULT, e -> controller.onSetDefault(),
				"Reset split panes", "reset pane");

		undoButton = new ToolbarButton(ToolbarImage.UNDO, e -> controller.onUndo(), "Undo last step", "undo");

		redoButton = new ToolbarButton(ToolbarImage.REDO, e -> controller.onRedo(), "Redo last step", "redo");

		JButton zoomInButton = new ToolbarButton(ToolbarImage.ZOOM_IN, e -> controller.onZoomIn(), "Zoom in",
				"zoom in");

		JButton zoomOutButton = new ToolbarButton(ToolbarImage.ZOOM_OUT, e -> controller.onZoomOut(), "Zoom out",
				"zoom out");

		JButton changeDesignButton = new ToolbarButton(ToolbarImage.DESIGN, e -> controller.changeDesign(),
				"Change between Metal and Nimbus feel and look", "change design");
		openEditorButton = new ToolbarButton(ToolbarImage.EDITOR, e -> controller.onOpenEditor(), "Switch to editor",
				"editor");

		// Editor specific Buttons

		addPlaceButton = new ToolbarButton(ToolbarImage.ADD_PLACE, e -> controller.onAddPlace(), "Add place",
				"add place");

		addTransitionButton = new ToolbarButton(ToolbarImage.ADD_TRANSITION, e -> controller.onAddTransition(),
				"Add transition", "add trans");

		deleteComponentButton = new ToolbarButton(ToolbarImage.DELETE_COMPONENT, e -> controller.onRemoveComponent(),
				"Delete component", "delete");

		addEdgeButton = new ToolbarButton(ToolbarImage.ADD_EDGE, e -> controller.onAddEdge(), "Add edge", "add edge");

		removeEdgeButton = new ToolbarButton(ToolbarImage.REMOVE_EDGE, e -> controller.onRemoveEdge(),
				"Remove an edge: choose source first and then target", "remove edge");

		addLabelButton = new ToolbarButton(ToolbarImage.ADD_LABEL, e -> controller.onAddLabel(), "Add label to element",
				"add label");

		closeEditorButton = new ToolbarButton(ToolbarImage.OPEN_VIEWER, e -> controller.onCloseEditor(),
				"Switch to viewer", "close editor");

		zoomInReachabilityButton = new ToolbarButton(ToolbarImage.ZOOM_IN, e -> controller.onZoomInReachability(),
				"Zoom in", "zoom in");

		zoomOutReachabilityButton = new ToolbarButton(ToolbarImage.ZOOM_OUT, e -> controller.onZoomOutReachability(),
				"Zoom out", "zoom out");

		toggleTreeLayoutButton = new ToolbarButton(ToolbarImage.TREE_LAYOUT, e -> controller.onToggleTreeLayout(),
				"Turn tree base layout on -> will give the best experience", "tree-layout");

		toggleCircleLayoutButton = new ToolbarButton(ToolbarImage.CIRCLE_LAYOUT, e -> controller.onToggleCircleLayout(),
				"Turn circe based layout on -> might not be as beautiful but may be more fun", "circle-layout");

		toggleAutoLayoutButton = new ToolbarButton(ToolbarImage.AUTO_LAYOUT, e -> controller.onToggleAutoLayout(),
				"Turn auto layout on -> auto layout provided by GraphStream", "auto-layout");

		this.add(openButton);
		this.add(saveButton);
		this.add(previousButton);
		this.add(nextButton);
		this.add(plusButton);
		this.add(minusButton);

		// Viewer alternative buttons

		this.add(restartButton);

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
		this.add(changeDesignButton);

		this.add(openEditorButton);
		this.add(closeEditorButton);

		add(glue);
		add(separator);

		// ReachabilityGraph buttons

		this.add(analyseButton);
		this.add(resetButton);
		this.add(undoButton);
		this.add(redoButton);
		this.add(clearTextButton);

		this.add(zoomInReachabilityButton);
		this.add(zoomOutReachabilityButton);
		this.add(toggleTreeLayoutButton);
		this.add(toggleCircleLayoutButton);
		this.add(toggleAutoLayoutButton);
		setToolbarMode(ToolbarMode.VIEWER);
		toggleTreeLayoutButton();

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

	private class ToolbarButton extends JButton {

		private static final long serialVersionUID = 1L;

		private String altText;

		private Color color = null;

		public ToolbarButton(ToolbarImage toolbarImage, ActionListener actionListener, String toolTipText,
				String altText) {

			this.altText = altText;

			setImage(toolbarImage);

			addActionListener(actionListener);
			setToolTipText(toolTipText);

			int sizeInt = 30;

			Dimension size = new Dimension(sizeInt, sizeInt);
			setMaximumSize(size);
			setSize(size);
			setPreferredSize(size);

		}

		@Override
		protected void paintComponent(Graphics g) {
			if (color != null) {
				((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setColor(color);
				g.fillRoundRect(0, 0, 30, 30, 20, 20);
			}
			super.paintComponent(g);
		}

		public void setImage(ToolbarImage toolbarImage) {

			String imagePath = System.getProperty("user.dir");

			String imgLocation = imagePath(toolbarImage);

			ImageIcon icon = new ImageIcon(imagePath + imgLocation, altText);

			if (imagePath != null) {
				setIcon(icon);
			} else {
				setText(altText);
				System.err.println("Resource not found: " + imgLocation);
			}

		}

		public void setColor(Color color) {
			LookAndFeel laf = UIManager.getLookAndFeel();
			this.color = color;
			if (laf.getName().equals("Nimbus"))
				repaint();
			else
				setBackground(color);
		}

		public Color getColor() {
			return color;
		}
	}

	private String imagePath(ToolbarImage toolbarImage) {
		return IMAGE_ROOT_FOLDER + toolbarImage + ".png";

	}

	public enum ToolbarImage {
		OPEN("folder"), SAVE("save"), ANALYSE("stats"), RESTART("restart"), RESET("delete"), PLUS("plus"),
		MINUS("minus"), LEFT("left"), RIGHT("right"), UNDO("undo"), REDO("redo"), CLEAR_TEXT("input"), EDITOR("edit"),
		DEFAULT("layout"), ZOOM_IN("zoom-in"), ZOOM_OUT("zoom-out"),

		// additional images for Editor
		ADD_PLACE("add-circle"), ADD_TRANSITION("add-square"), ADD_EDGE("arc"), ADD_EDGE_HIGHLIGHT("arc-highlight"),
		DELETE_COMPONENT("erase"), OPEN_VIEWER("eye"), REMOVE_EDGE("remove-edge"),
		REMOVE_EDGE_HIGHLIGHT("remove-edge-highlight"), ADD_LABEL("label"), AUTO_LAYOUT("auto-layout"),
		TREE_LAYOUT("tree-layout"), CIRCLE_LAYOUT("circle-layout"), DESIGN("design");

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

		if (addEdgeButton.getColor() == buttonDefaultColor || addEdgeButton.getColor() == null) {
			addEdgeButton.setColor(buttonHighlightColor);
			if (removeEdgeButton.getColor() == buttonHighlightColor) {
				removeEdgeButton.setColor(buttonDefaultColor);

			}
		} else {
			addEdgeButton.setColor(buttonDefaultColor);
		}
	}

	public void toggleRemoveEdgeButton() {
		if (removeEdgeButton.getColor() == buttonDefaultColor || removeEdgeButton.getColor() == null) {
			removeEdgeButton.setColor(buttonHighlightColor);
			if (addEdgeButton.getColor() == buttonHighlightColor) {
				addEdgeButton.setColor(buttonDefaultColor);
			}
		} else {
			removeEdgeButton.setColor(buttonDefaultColor);
		}
	}

	public void toggleAutoLayoutButton() {

		if (toggleAutoLayoutButton.getColor() == buttonDefaultColor || toggleAutoLayoutButton.getColor() == null) {
			toggleAutoLayoutButton.setColor(buttonHighlightColor);

			if (toggleCircleLayoutButton.getColor() == buttonHighlightColor)
				toggleCircleLayoutButton.setColor(buttonDefaultColor);
			if (toggleTreeLayoutButton.getColor() == buttonHighlightColor)
				toggleTreeLayoutButton.setColor(buttonDefaultColor);

		} else {
			toggleAutoLayoutButton.setColor(buttonDefaultColor);
		}
	}

	public void toggleTreeLayoutButton() {

		if (toggleTreeLayoutButton.getColor() == buttonDefaultColor || toggleTreeLayoutButton.getColor() == null) {
			toggleTreeLayoutButton.setColor(buttonHighlightColor);
			if (toggleCircleLayoutButton.getColor() == buttonHighlightColor)
				toggleCircleLayoutButton.setColor(buttonDefaultColor);
			if (toggleAutoLayoutButton.getColor() == buttonHighlightColor)
				toggleAutoLayoutButton.setColor(buttonDefaultColor);

		} else {
			toggleTreeLayoutButton.setColor(buttonDefaultColor);
		}
	}

	public void toggleCircleLayoutButton() {

		if (toggleCircleLayoutButton.getColor() == buttonDefaultColor || toggleCircleLayoutButton.getColor() == null) {
			toggleCircleLayoutButton.setColor(buttonHighlightColor);
			if (toggleTreeLayoutButton.getColor() == buttonHighlightColor)
				toggleTreeLayoutButton.setColor(buttonDefaultColor);
			if (toggleAutoLayoutButton.getColor() == buttonHighlightColor)
				toggleAutoLayoutButton.setColor(buttonDefaultColor);

		} else {
			toggleCircleLayoutButton.setColor(buttonDefaultColor);
		}
	}

	public void resetButtons() {

		addEdgeButton.setColor(buttonDefaultColor);
		removeEdgeButton.setColor(buttonDefaultColor);
		undoButton.setColor(buttonDefaultColor);
		redoButton.setColor(buttonDefaultColor);

		toggleAutoLayoutButton.setColor(buttonDefaultColor);
		toggleCircleLayoutButton.setColor(buttonDefaultColor);
		toggleTreeLayoutButton.setColor(buttonDefaultColor);

	}

	public void setToolbarTo(PetrinetPanel panel, LayoutTypes layoutType) {

		JButton button = new JButton();
		
		buttonDefaultColor = button.getBackground();
		
		LookAndFeel laf = UIManager.getLookAndFeel();

		if (laf.getName().equals("Nimbus"))
			buttonHighlightColor = new Color(190, 185, 180);
		else
			buttonHighlightColor = Color.LIGHT_GRAY;

		resetButtons();

		if (layoutType == LayoutTypes.AUTOMATIC)
			toggleAutoLayoutButton();
		if (layoutType == LayoutTypes.CIRCLE)
			toggleCircleLayoutButton();
		if (layoutType == LayoutTypes.TREE)
			toggleTreeLayoutButton();

		if (panel == null)
			return;

		PetrinetController controller = panel.getController();

		Editor editor = controller.getEditor();
		if (editor.addsEdge())
			toggleAddEdgeButton();
		if (editor.removesEdge())
			toggleRemoveEdgeButton();

		PetrinetQueue queue = controller.getPetrinetQueue();

		if (!queue.isFirstState())
			toggleUndoButton();
		if (queue.hasNext())
			toggleRedoButton();

		setToolbarMode(controller.getToolbarMode());
	}

	public void toggleRedoButton() {

		if (redoButton.getColor() == buttonDefaultColor || undoButton.getColor() == null)
			redoButton.setColor(buttonHighlightColor);
		else
			redoButton.setColor(buttonDefaultColor);
	}

	public void toggleUndoButton() {
		if (undoButton.getColor() == buttonDefaultColor || undoButton.getColor() == null)
			undoButton.setColor(buttonHighlightColor);
		else
			undoButton.setColor(buttonDefaultColor);
	}

}
