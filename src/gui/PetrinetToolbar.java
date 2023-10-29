package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import control.PetrinetController;
import core.Editor;
import core.PetrinetQueue;
import reachabilityGraphLayout.LayoutTypes;

// TODO: Auto-generated Javadoc
/**
 * The Class PetrinetToolbar.
 */
public class PetrinetToolbar extends JToolBar {

	private static final long serialVersionUID = 1L;

	private static final String IMAGE_ROOT_FOLDER = "/resources/images/Toolbar/";

	private Color buttonDefaultColor;

	private Color buttonHighlightColor = Color.LIGHT_GRAY;

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
	private ToolbarButton toggleTreeLayoutButton;
	private ToolbarButton toggleCircleLayoutButton;
	private ToolbarButton toggleAutoLayoutButton;
	private JButton changeDesignButton;

	private boolean startUp = true;

	/**
	 * Instantiates a new petrinet toolbar.
	 *
	 * @param controller the controller
	 */
	PetrinetToolbar(PetrinetToolbarInterface controller) {

//		setFloatable(false);

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		Component horizontalGlue = Box.createHorizontalGlue();
		Component verticalGlueUpper = Box.createVerticalGlue();
		Component verticalGlueLower = Box.createVerticalGlue();
		verticalGlueLower.setVisible(false);
		verticalGlueUpper.setVisible(false);

		JSeparator separator = new JSeparator();
		separator.setVisible(false);
		Component horizontalStrut = Box.createHorizontalStrut(10);
		add(horizontalStrut);
		Component verticalStrut = Box.createVerticalStrut(10);
		add(verticalStrut);
		verticalStrut.setVisible(false);

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

						horizontalStrut.setVisible(true);
						horizontalGlue.setVisible(true);

						verticalStrut.setVisible(false);
						verticalGlueLower.setVisible(false);
						verticalGlueUpper.setVisible(false);

						separator.setOrientation(SwingConstants.VERTICAL);

						if (!startUp) {
							separator.setVisible(true);
							if (changeDesignButton != null)
								changeDesignButton.setVisible(false);

						}
						if (startUp)
							startUp = false;
						controller.onSetDefault();

					} else if (getOrientation() == SwingConstants.VERTICAL) {
						if (changeDesignButton != null)
							changeDesignButton.setVisible(false);
						controller.onSetDefault();
						setLayout(new BoxLayout((JToolBar) evt.getSource(), BoxLayout.Y_AXIS));

						horizontalStrut.setVisible(false);
						horizontalGlue.setVisible(false);

						verticalStrut.setVisible(true);
						verticalGlueLower.setVisible(true);
						verticalGlueUpper.setVisible(true);

						separator.setOrientation(SwingConstants.HORIZONTAL);
						separator.setVisible(true);
						startUp = true;
						controller.onSetDefault();
					} else {
						if (changeDesignButton != null)
							changeDesignButton.setVisible(true);
						setLayout(new BoxLayout((JToolBar) evt.getSource(), BoxLayout.X_AXIS));
						separator.setVisible(false);

						horizontalStrut.setVisible(true);
						horizontalGlue.setVisible(true);

						verticalStrut.setVisible(false);
						verticalGlueLower.setVisible(false);
						verticalGlueUpper.setVisible(false);

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

		changeDesignButton = new ToolbarButton(ToolbarImage.DESIGN, e -> controller.changeDesign(),
				"Change between Metal and Nimbus feel and look (Caution: can only be changed if toolbar is attached to the top -> button will disappear if toolbar is detached)",
				"change design");
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

		this.add(openEditorButton);
		this.add(closeEditorButton);

		add(horizontalGlue);
		add(verticalGlueUpper);
		add(separator);
		add(verticalGlueLower);

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
		this.add(setDefaultButton);

		this.add(changeDesignButton);

		setToolbarMode(ToolbarMode.VIEWER);
		toggleTreeLayoutButton();

	}

	/**
	 * Sets the toolbar mode.
	 *
	 * @param toolbarMode the new toolbar mode
	 */
	public void setToolbarMode(ToolbarMode toolbarMode) {

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

	/**
	 * The Enum ToolbarImage.
	 */
	private enum ToolbarImage {

		/** The open. */
		OPEN("folder"),
		/** The save. */
		SAVE("save"),
		/** The analyse. */
		ANALYSE("stats"),
		/** The restart. */
		RESTART("restart"),
		/** The reset. */
		RESET("delete"),
		/** The plus. */
		PLUS("plus"),

		/** The minus. */
		MINUS("minus"),
		/** The left. */
		LEFT("left"),
		/** The right. */
		RIGHT("right"),
		/** The undo. */
		UNDO("undo"),
		/** The redo. */
		REDO("redo"),
		/** The clear text. */
		CLEAR_TEXT("input"),
		/** The editor. */
		EDITOR("edit"),

		/** The default. */
		DEFAULT("layout"),
		/** The zoom in. */
		ZOOM_IN("zoom-in"),
		/** The zoom out. */
		ZOOM_OUT("zoom-out"),

		/** The add place. */
		// additional images for Editor
		ADD_PLACE("add-circle"),
		/** The add transition. */
		ADD_TRANSITION("add-square"),
		/** The add edge. */
		ADD_EDGE("arc"),

		/** The delete component. */
		DELETE_COMPONENT("erase"),
		/** The open viewer. */
		OPEN_VIEWER("eye"),
		/** The remove edge. */
		REMOVE_EDGE("remove-edge"),

		/** The add label. */
		ADD_LABEL("label"),
		/** The auto layout. */
		AUTO_LAYOUT("auto-layout"),

		/** The tree layout. */
		TREE_LAYOUT("tree-layout"),
		/** The circle layout. */
		CIRCLE_LAYOUT("circle-layout"),
		/** The design. */
		DESIGN("design");

		private String name;

		/**
		 * Instantiates a new toolbar image.
		 *
		 * @param name the name
		 */
		ToolbarImage(String name) {
			this.name = name;

		}

		/**
		 * To string.
		 *
		 * @return the string
		 */
		@Override
		public String toString() {
			return name;
		}

	}

	/**
	 * Toggle add edge button.
	 */
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

	/**
	 * Toggle remove edge button.
	 */
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

	/**
	 * Toggle auto layout button.
	 */
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

	/**
	 * Toggle tree layout button.
	 */
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

	/**
	 * Toggle circle layout button.
	 */
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

	/**
	 * Reset buttons.
	 */
	private void resetButtons() {

		addEdgeButton.setColor(buttonDefaultColor);
		removeEdgeButton.setColor(buttonDefaultColor);
		undoButton.setColor(buttonDefaultColor);
		redoButton.setColor(buttonDefaultColor);

		toggleAutoLayoutButton.setColor(buttonDefaultColor);
		toggleCircleLayoutButton.setColor(buttonDefaultColor);
		toggleTreeLayoutButton.setColor(buttonDefaultColor);

	}

	/**
	 * Sets the toolbar to.
	 *
	 * @param panel      the panel
	 * @param layoutType the layout type
	 */
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

	/**
	 * Toggle redo button.
	 */
	public void toggleRedoButton() {

		if (redoButton.getColor() == buttonDefaultColor || undoButton.getColor() == null)
			redoButton.setColor(buttonHighlightColor);
		else
			redoButton.setColor(buttonDefaultColor);
	}

	/**
	 * Toggle undo button.
	 */
	public void toggleUndoButton() {
		if (undoButton.getColor() == buttonDefaultColor || undoButton.getColor() == null)
			undoButton.setColor(buttonHighlightColor);
		else
			undoButton.setColor(buttonDefaultColor);
	}

}
