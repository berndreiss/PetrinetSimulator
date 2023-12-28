package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import control.PetrinetViewerController;
import control.PetrinetEditorController;
import control.PetrinetPanelInterface;
import core.ReachabilityGraphUndoQueue;
import core.ReachabilityGraphUndoQueueState;
import reachabilityGraphLayout.LayoutType;

/**
 * <p>
 * A toolbar for interactions with petrinets via a
 * {@link PetrinetToolbarController} representing the current state of a
 * {@link PetrinetPanelInterface}.
 * </p>
 * 
 * <p>
 * 
 * There are two possible {@link ToolbarMode}s that the toolbar can represent:
 * EDITOR and VIEWER. In either mode certain buttons are set visible /
 * invisible. Additionally certain buttons (e.g. undo/redo buttons) can be
 * toggled meaning they can be highlighted.
 * 
 * </p>
 */
public class PetrinetToolbar extends JToolBar {

	private static final long serialVersionUID = 1L;

	/**
	 * Keep track of default button color of look and feel so it can be highlighted
	 * and changed back.
	 */
	private Color buttonDefaultColor;
	/** Default highlight color. */
	private Color buttonHighlightColor = Color.LIGHT_GRAY;

	// BUTTONS SHOWN IN VIEWER MODE ONLY
	/** Button to start analysis of the current petrinet. */
	private JButton analyseButton;
	/** Set petrinet to initial markings. */
	private JButton restartButton;
	/** Open the editor. */
	private JButton openEditorButton;
	/** Reset the reachability graph (and therefore the petrinet). */
	private JButton resetButton;
	/** Undo last step -> is highlighted when there are steps to undo. */
	private ToolbarButton undoButton;
	/** Redo last step -> is highlighted when there are steps to redo. */
	private ToolbarButton redoButton;
	/** Clear the text area in the main frame. */
	private JButton clearTextButton;
	/** Zoom into the reachability graph. */
	private JButton zoomInReachabilityButton;
	/** Zoom out of the reachability graph. */
	private JButton zoomOutReachabilityButton;
	/** Choose tree layout for reachability graph. */
	private ToolbarButton toggleTreeLayoutButton;
	/** Choose circle layout for reachability graph. */
	private ToolbarButton toggleCircleLayoutButton;
	/** Choose auto layout for reachability graph. */
	private ToolbarButton toggleAutoLayoutButton;
	/** Change the look and feel. */
	private JButton changeLookAndFeelButton;

	// BUTTONS SHOWN IN EDITOR MODE ONLY

	/** Add a new place to the petrinet. */
	private JButton addPlaceButton;
	/** Add new transition to the petrinet. */
	private JButton addTransitionButton;
	/** Delete marked component. */
	private JButton deleteComponentButton;
	/**
	 * Add a new edge to the petrinet -> is highlighted while adding and is reset
	 * when the adding process is done.
	 */
	private ToolbarButton addEdgeButton;
	/** Remove an edge from the petrinet. */
	private ToolbarButton removeEdgeButton;
	/**
	 * Add a label to an element -> is highlighted while removing and is reset when
	 * the adding process is done.
	 */
	private JButton addLabelButton;
	/** Close the editor. */
	private JButton closeEditorButton;

	/**
	 * Keeps track of the place in the BorderLayout of the main frame where the
	 * toolbar is docked to.
	 */
	private String dockingPlace = BorderLayout.NORTH;

	/**
	 * Instantiates a new petrinet toolbar.
	 *
	 * @param mainController the controller controlling the toolbar
	 */
	PetrinetToolbar(PetrinetToolbarController mainController, JFrame parent) {

		// set to BoxLayout so that there can be glue added to push contents to the end
		// of both sides
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		// glues for horizontal and vertical orientation separating buttons relating to
		// the petrinet graph from buttons relating to the reachability graph -> the
		// vertical orientation has two glues as to push the buttons from the separator
		Component horizontalGlue = Box.createHorizontalGlue();
		Component verticalGlueUpper = Box.createVerticalGlue();
		Component verticalGlueLower = Box.createVerticalGlue();

		// toolbar starts in horizontal position
		verticalGlueLower.setVisible(false);
		verticalGlueUpper.setVisible(false);

		// separator -> only visible when toolbar is detached or attached to the sides
		// because buttons become more cramped together and the separator helps dividing
		// buttons relating to the petrinet graph from buttons relating to the
		// reachability graph
		JSeparator separator = new JSeparator();
		separator.setVisible(false);

		// add a little space to the left / upper end of the toolbar
		Component horizontalStrut = Box.createHorizontalStrut(10);
		add(horizontalStrut);
		Component verticalStrut = Box.createVerticalStrut(10);
		add(verticalStrut);
		verticalStrut.setVisible(false);

		// listen to the docking / undocking of the toolbar -> if not attached to the
		// NORTH
		// add separator
		addPropertyChangeListener(new PropertyChangeListener() {

			// on adding the toolbar to the frame for the first time the property change
			// event does not signify that the toolbar has been attached (see below); this
			// boolean signifies a first time attachment to the NORTH
			boolean startUp = true;

			@Override
			public void propertyChange(PropertyChangeEvent evt) {

				// get attachment events, determine whether and where the toolbar has
				// docked and handle events accordingly
				if ("ancestor".equals(evt.getPropertyName())) {

					// toolbar has detached -> return
					if (evt.getNewValue() == null) {
						return;
					}

					// get new value of ancestor event
					String eventString = evt.getNewValue().toString();

					// for safety measures
					if (!eventString.contains("JPanel"))
						return;

					// get x and y values of the event (0,0) signifies the toolbar has been detached
					// unless it is added to the frame for the first time in which case the startUp
					// boolean above signifies it is attached
					int eventX = Integer.parseInt(eventString.split("JPanel")[1].split(",")[1]);
					int eventY = Integer.parseInt(eventString.split("JPanel")[1].split(",")[2]);

					// toolbar has undocked (or added to the frame for the first time)
					if (evt.getNewValue() != null && eventX == 0 && eventY == 0) {

						// if toolbar is added to frame for first time set start up boolean and docking
						// place and return
						if (startUp) {
							startUp = false;
							dockingPlace = BorderLayout.NORTH;
							return;
						}

						// set components according to orientation
						if (getOrientation() == SwingConstants.HORIZONTAL) {
							setHorizontalComponents();

						} else {
							setVerticalComponents();
						}

						separator.setVisible(true);
						dockingPlace = null;
						mainController.onReadjustDividers();

					}
					// toolbar has docked to EAST or WEST
					else if (getOrientation() == SwingConstants.VERTICAL) {

						// wait for toolbar to finish docking and see if it is in the parents EAST or
						// WEST
						SwingUtilities.invokeLater(() -> {
							Component eastComponent = parent.getContentPane().getComponentAt(parent.getWidth() - 1,
									parent.getHeight() / 2);

							if (eastComponent instanceof PetrinetToolbar)
								dockingPlace = BorderLayout.EAST;
							Component westComponent = parent.getContentPane().getComponentAt(0, parent.getHeight() / 2);
							if (westComponent instanceof PetrinetToolbar)
								dockingPlace = BorderLayout.WEST;

						});

						setVerticalComponents();
						separator.setVisible(true);
						mainController.onReadjustDividers();
						startUp = false;

					}
					// toolbar has docked to the NORTH
					else {
						dockingPlace = BorderLayout.NORTH;
						setHorizontalComponents();
						separator.setVisible(false);
						mainController.onReadjustDividers();
						startUp = false;

					}

				}
			}

			// set layout to orient around the y-axis, set horizontal elements invisible,
			// vertical visible and reorient the separator
			private void setVerticalComponents() {
				setLayout(new BoxLayout(PetrinetToolbar.this, BoxLayout.Y_AXIS));
				horizontalStrut.setVisible(false);
				horizontalGlue.setVisible(false);
				verticalStrut.setVisible(true);
				verticalGlueLower.setVisible(true);
				verticalGlueUpper.setVisible(true);
				separator.setOrientation(SwingConstants.HORIZONTAL);
			}

			// set layout to orient around the x-axis, set horizontal elements visible,
			// vertical invisible and reorient the separator
			private void setHorizontalComponents() {
				setLayout(new BoxLayout(PetrinetToolbar.this, BoxLayout.X_AXIS));
				horizontalStrut.setVisible(true);
				horizontalGlue.setVisible(true);
				verticalStrut.setVisible(false);
				verticalGlueLower.setVisible(false);
				verticalGlueUpper.setVisible(false);
				separator.setOrientation(SwingConstants.VERTICAL);
			}
		});

		// initialize buttons and add them-> for functionality see
		// PetrinetToolbarController

		// PETRINET BUTTONS

		ToolbarButton openButton = new ToolbarButton(ToolbarImage.OPEN, e -> mainController.onOpen(), "Open file",
				"open");

		// get the default button color of the given look and feel
		buttonDefaultColor = openButton.getBackground();

		JButton saveButton = new ToolbarButton(ToolbarImage.SAVE, e -> mainController.onSave(), "Save file", "save");

		JButton previousButton = new ToolbarButton(ToolbarImage.LEFT, e -> mainController.onPrevious(),
				"Open the previous file", "previous");

		JButton nextButton = new ToolbarButton(ToolbarImage.RIGHT, e -> mainController.onNext(), "Open the next file",
				"next");

		JButton plusButton = new ToolbarButton(ToolbarImage.PLUS, e -> mainController.onIncrement(),
				"Adds a token to a selected place", "plus");

		JButton minusButton = new ToolbarButton(ToolbarImage.MINUS, e -> mainController.onDecrement(),
				"Removes a token from a selected place", "minus");

		// START VIEWER SPECIFIC BUTTONS

		restartButton = new ToolbarButton(ToolbarImage.RESTART, e -> mainController.onResetPetrinet(),
				"Reset the petrinet graph", "Reset p");

		JButton zoomInButton = new ToolbarButton(ToolbarImage.ZOOM_IN, e -> mainController.onZoomInPetrinet(),
				"Zoom into petrinet", "zoom in");

		JButton zoomOutButton = new ToolbarButton(ToolbarImage.ZOOM_OUT, e -> mainController.onZoomOutPetrinet(),
				"Zoom out of petrinet", "zoom out");

		openEditorButton = new ToolbarButton(ToolbarImage.EDITOR, e -> mainController.onOpenEditor(),
				"Switch to editor", "editor");

		// END VIEWER SPECIFIC BUTTONS

		// START EDITOR SPECIFIC BUTTONS

		addPlaceButton = new ToolbarButton(ToolbarImage.ADD_PLACE, e -> mainController.onAddPlace(), "Add place",
				"add place");

		addTransitionButton = new ToolbarButton(ToolbarImage.ADD_TRANSITION, e -> mainController.onAddTransition(),
				"Add transition", "add trans");

		deleteComponentButton = new ToolbarButton(ToolbarImage.DELETE_COMPONENT,
				e -> mainController.onRemoveComponent(), "Delete selected component", "delete");

		addEdgeButton = new ToolbarButton(ToolbarImage.ADD_EDGE, e -> mainController.onAddEdge(),
				"Add edge -> select beginning node first and then ending node; have to be of different types",
				"add edge");

		removeEdgeButton = new ToolbarButton(ToolbarImage.REMOVE_EDGE, e -> mainController.onRemoveEdge(),
				"Remove an edge -> select beginning node first and then ending node; edge has to exist", "remove edge");

		addLabelButton = new ToolbarButton(ToolbarImage.ADD_LABEL, e -> mainController.onAddLabel(),
				"Add label to selected element", "add label");

		closeEditorButton = new ToolbarButton(ToolbarImage.OPEN_VIEWER, e -> mainController.onCloseEditor(),
				"Switch to viewer", "close editor");

		// END EDITOR SPECIFIC BUTTONS

		// REACHABILITY GRAPH BUTTONS

		// START VIEWER SPECIFIC BUTTONS

		analyseButton = new ToolbarButton(ToolbarImage.ANALYSE, e -> mainController.onAnalyse(),
				"Analyse petrinet and create reachability graph", "analyse");

		resetButton = new ToolbarButton(ToolbarImage.RESET, e -> mainController.onReset(),
				"Reset the reachability graph", "reset r");

		undoButton = new ToolbarButton(ToolbarImage.UNDO, e -> mainController.onUndo(), "Undo last step", "undo");

		redoButton = new ToolbarButton(ToolbarImage.REDO, e -> mainController.onRedo(), "Redo last step", "redo");

		clearTextButton = new ToolbarButton(ToolbarImage.CLEAR_TEXT, e -> mainController.onClearTextArea(),
				"Clear text area", "clear");

		zoomInReachabilityButton = new ToolbarButton(ToolbarImage.ZOOM_IN, e -> mainController.onZoomInReachability(),
				"Zoom into reachability graph", "zoom in");

		zoomOutReachabilityButton = new ToolbarButton(ToolbarImage.ZOOM_OUT,
				e -> mainController.onZoomOutReachability(), "Zoom out of reachability graph", "zoom out");

		toggleTreeLayoutButton = new ToolbarButton(ToolbarImage.TREE_LAYOUT, e -> mainController.onToggleTreeLayout(),
				"Turn tree base layout on -> will give the best experience", "tree-layout");

		toggleCircleLayoutButton = new ToolbarButton(ToolbarImage.CIRCLE_LAYOUT,
				e -> mainController.onToggleCircleLayout(),
				"Turn circe based layout on -> might not be as beautiful but may be more fun", "circle-layout");

		toggleAutoLayoutButton = new ToolbarButton(ToolbarImage.AUTO_LAYOUT, e -> mainController.onToggleAutoLayout(),
				"Turn auto layout on -> auto layout provided by GraphStream", "auto-layout");

		// END VIEWER SPECIFIC BUTTONS

		JButton setSplitPanesDefaultButton = new ToolbarButton(ToolbarImage.DEFAULT,
				e -> mainController.onSetSplitPanesDefault(), "Reset split panes to default ratio", "reset pane");

		changeLookAndFeelButton = new ToolbarButton(ToolbarImage.LAF, e -> mainController.onChangeLookAndFeel(),
				"Change between Metal and Nimbus feel and look", "change design");

		// PETRINET BUTTONS

		this.add(openButton);
		this.add(saveButton);
		this.add(previousButton);
		this.add(nextButton);
		this.add(plusButton);
		this.add(minusButton);

		// START VIEWER SPECIFIC BUTTONS

		this.add(restartButton);
		this.add(zoomInButton);
		this.add(zoomOutButton);
		this.add(openEditorButton);

		// END VIEWER SPECIFIC BUTTONS

		// START EDITOR SPECIFIC BUTTONS

		this.add(addPlaceButton);
		this.add(addTransitionButton);
		this.add(deleteComponentButton);
		this.add(addEdgeButton);
		this.add(removeEdgeButton);
		this.add(addLabelButton);
		this.add(closeEditorButton);

		// END EDITOR SPECIFIC BUTTONS

		add(horizontalGlue);
		add(verticalGlueUpper);
		add(separator);
		add(verticalGlueLower);

		// REACHABILITY GRAPH BUTTONS

		// START VIEWER SPECIFIC BUTTONS

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

		// END VIEWER SPECIFIC BUTTONS

		this.add(setSplitPanesDefaultButton);
		this.add(changeLookAndFeelButton);

		// set layout to TREE and mode to VIEWER by default
		toggleTreeLayoutButton();
		setToolbarMode(ToolbarMode.VIEWER);
	}

	/**
	 * Sets the toolbar mode. Buttons are set visible / invisible according to mode.
	 *
	 * @param toolbarMode the mode the toolbar is set to
	 */
	public void setToolbarMode(ToolbarMode toolbarMode) {

		// setting viewer buttons
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

		}
		// setting editor buttons also reset add / remove edge buttons if highlighted
		else {
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

			if (addEdgeButton.getColor() == buttonHighlightColor)
				toggleAddEdgeButton();
			if (removeEdgeButton.getColor() == buttonHighlightColor)
				toggleRemoveEdgeButton();
		}
	}

	/**
	 * Toggles add edge button. If it is highlighted unmark it. Is synchronized with
	 * remove edge button -> only one can be hightlighted at a time.
	 */
	public void toggleAddEdgeButton() {
		// button is not highlighted -> highlight it and handle remove edge button
		if (addEdgeButton.getColor() == buttonDefaultColor || addEdgeButton.getColor() == null) {
			// highlight button
			addEdgeButton.setColor(buttonHighlightColor);
			// if remove edge button is highlighted set to default
			if (removeEdgeButton.getColor() == buttonHighlightColor)
				removeEdgeButton.setColor(buttonDefaultColor);
		}
		// button is highlighted -> set to default
		else {
			addEdgeButton.setColor(buttonDefaultColor);
		}
	}

	/**
	 * Toggles remove edge button. If it is highlighted unmark it. Is synchronized
	 * with add edge button -> only one can be hightlighted at a time.
	 */
	public void toggleRemoveEdgeButton() {

		// button is not highlighted -> highlight it and handle add edge button
		if (removeEdgeButton.getColor() == buttonDefaultColor || removeEdgeButton.getColor() == null) {
			// highlight button
			removeEdgeButton.setColor(buttonHighlightColor);
			// if add edge button is highlighted set to default
			if (addEdgeButton.getColor() == buttonHighlightColor)
				addEdgeButton.setColor(buttonDefaultColor);
		}
		// button is highlighted -> set to default
		else
			removeEdgeButton.setColor(buttonDefaultColor);

	}

	/**
	 * Toggles tree layout button. Activate auto tree if not active and highlight
	 * button. Synchronizes with toggle circle / auto layout buttons -> only one can
	 * be highlighted at a time.
	 */
	public void toggleTreeLayoutButton() {
		// button is not highlighted -> highlight it and handle the other buttons
		if (toggleTreeLayoutButton.getColor() == buttonDefaultColor || toggleTreeLayoutButton.getColor() == null) {
			// highlight button
			toggleTreeLayoutButton.setColor(buttonHighlightColor);
			// set others default if highlighted
			if (toggleCircleLayoutButton.getColor() == buttonHighlightColor)
				toggleCircleLayoutButton.setColor(buttonDefaultColor);
			if (toggleAutoLayoutButton.getColor() == buttonHighlightColor)
				toggleAutoLayoutButton.setColor(buttonDefaultColor);
		} else // button is highlighted -> set default
			toggleTreeLayoutButton.setColor(buttonDefaultColor);
	}

	/**
	 * Toggles circle layout button. Activate circle layout if not active and
	 * highlight button. Synchronizes with toggle auto / tree layout buttons -> only
	 * one can be highlighted at a time.
	 */
	public void toggleCircleLayoutButton() {
		// button is not highlighted -> highlight it and handle the other buttons
		if (toggleCircleLayoutButton.getColor() == buttonDefaultColor || toggleCircleLayoutButton.getColor() == null) {
			// highlight button
			toggleCircleLayoutButton.setColor(buttonHighlightColor);
			// set others default if highlighted
			if (toggleTreeLayoutButton.getColor() == buttonHighlightColor)
				toggleTreeLayoutButton.setColor(buttonDefaultColor);
			if (toggleAutoLayoutButton.getColor() == buttonHighlightColor)
				toggleAutoLayoutButton.setColor(buttonDefaultColor);
		} else // button is highlighted -> set default
			toggleCircleLayoutButton.setColor(buttonDefaultColor);
	}

	/**
	 * Toggles auto layout button. Activate auto layout if not active and highlight
	 * button. Synchronizes with toggle circle / tree layout buttons -> only one can
	 * be highlighted at a time.
	 */
	public void toggleAutoLayoutButton() {
		// button is not highlighted -> highlight it and handle the other buttons
		if (toggleAutoLayoutButton.getColor() == buttonDefaultColor || toggleAutoLayoutButton.getColor() == null) {
			// highlight button
			toggleAutoLayoutButton.setColor(buttonHighlightColor);
			// set others default if highlighted
			if (toggleCircleLayoutButton.getColor() == buttonHighlightColor)
				toggleCircleLayoutButton.setColor(buttonDefaultColor);
			if (toggleTreeLayoutButton.getColor() == buttonHighlightColor)
				toggleTreeLayoutButton.setColor(buttonDefaultColor);
		} else // button is highlighted -> set default
			toggleAutoLayoutButton.setColor(buttonDefaultColor);

	}

	// reset all buttons that can be highlighted to default
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
	 * Sets the toolbar to the given panels state. Un- / redo buttons, add / remove
	 * edge buttons and layout buttons are highlighted accordingly.
	 *
	 * @param petrinetPanel the panel the toolbar represents
	 * @param layoutType    the layoutType used
	 */
	public void setToolbarTo(PetrinetPanelInterface petrinetPanel, LayoutType layoutType) {

		// get default color of current look and feel
		JButton button = new JButton();
		buttonDefaultColor = button.getBackground();

		// set highlight colors for look and feel
		LookAndFeel laf = UIManager.getLookAndFeel();
		if (laf.getName().equals("Nimbus"))
			buttonHighlightColor = new Color(190, 185, 180);
		else
			buttonHighlightColor = Color.LIGHT_GRAY;

		// reset all buttons
		resetButtons();

		// highlight correct layout button
		if (layoutType == LayoutType.AUTOMATIC)
			toggleAutoLayoutButton();
		if (layoutType == LayoutType.CIRCLE)
			toggleCircleLayoutButton();
		if (layoutType == LayoutType.TREE)
			toggleTreeLayoutButton();

		if (petrinetPanel == null) // safety check
			return;

		// set add / remove edge buttons
		PetrinetEditorController editor = petrinetPanel.getEditor();
		if (editor.addsEdge())
			toggleAddEdgeButton();
		if (editor.removesEdge())
			toggleRemoveEdgeButton();

		// set undo / redo buttons
		PetrinetViewerController controller = petrinetPanel.getPetrinetViewerController();
		ReachabilityGraphUndoQueue queue = controller.getPetrinetQueue();

		if (queue == null) // safety check
			return;

		// get current state of the queue
		ReachabilityGraphUndoQueueState currentState = queue.getCurrentState();

		if (currentState == null) // safety check
			return;

		setUndoButton(!currentState.isFirst());
		setRedoButton(currentState.hasNext());

	}

	/**
	 * Toggles redo button. If it is not highlighted, highlight it. Set to default
	 * color otherwise.
	 * 
	 * @param highlight if true, buttons are highlighted, highlighting removed
	 *                  otherwise
	 */
	public void setRedoButton(boolean highlight) {

		if (highlight)
			redoButton.setColor(buttonHighlightColor);
		else
			redoButton.setColor(buttonDefaultColor);
	}

	/**
	 * Toggles undo button. If it is not highlighted, highlight it. Set to default
	 * color otherwise.
	 * 
	 * @param highlight true if undo button is highlighted
	 */
	public void setUndoButton(boolean highlight) {
		if (highlight)
			undoButton.setColor(buttonHighlightColor);
		else
			undoButton.setColor(buttonDefaultColor);
	}

	/**
	 * Get the place the toolbar is currently docked in.
	 * 
	 * @return any of {Borderlayout.NORTH, Borderlayout.EAST, Borderlayout.WEST}
	 */
	public String getDockingPlace() {
		return dockingPlace;
	}

	/**
	 * Reset the un-/redo buttons to not be highlighted.
	 */
	public void resetUndoRedoButtons() {
		undoButton.setColor(buttonDefaultColor);
		redoButton.setColor(buttonDefaultColor);
	}

}
