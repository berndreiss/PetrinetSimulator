package gui;

import java.awt.BorderLayout;
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
import javax.swing.JFrame;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.SwingPropertyChangeSupport;

import org.apache.commons.math3.geometry.enclosing.SupportBallGenerator;

import control.PetrinetController;
import control.PetrinetGraphEditor;
import core.PetrinetQueue;
import reachabilityGraphLayout.LayoutType;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * A toolbar for interactions with petrinets via a
 * {@link PetrinetToolbarInterface} representing the current state of a
 * {@link PetrinetPanel}.
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

	private String lastDockingPlace = BorderLayout.NORTH;

	/**
	 * Instantiates a new petrinet toolbar.
	 *
	 * @param mainController The controller linking all the actions to the petrinet
	 *                       panel.
	 */
	PetrinetToolbar(PetrinetToolbarInterface mainController, JFrame parent) {

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

		// listen to the a- / detachement of the toolbar -> if not attached to the NORTH
		// add separator 
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
						setLayout(new BoxLayout((JToolBar) evt.getSource(), BoxLayout.X_AXIS));

						horizontalStrut.setVisible(true);
						horizontalGlue.setVisible(true);

						verticalStrut.setVisible(false);
						verticalGlueLower.setVisible(false);
						verticalGlueUpper.setVisible(false);

						separator.setOrientation(SwingConstants.VERTICAL);

						if (!startUp) 
							separator.setVisible(true);

						if (startUp)
							startUp = false;
						mainController.onReadjustDividers();

					} else if (getOrientation() == SwingConstants.VERTICAL) {
						setLayout(new BoxLayout((JToolBar) evt.getSource(), BoxLayout.Y_AXIS));

						SwingUtilities.invokeLater(() -> {
							Component eastComponent = parent.getContentPane().getComponentAt(parent.getWidth() - 1,
									parent.getHeight() / 2);

							if (eastComponent instanceof PetrinetToolbar)
								lastDockingPlace = BorderLayout.EAST;
							Component westComponent = parent.getContentPane().getComponentAt(0, parent.getHeight() / 2);
							if (westComponent instanceof PetrinetToolbar)
								lastDockingPlace = BorderLayout.WEST;

						});

						horizontalStrut.setVisible(false);
						horizontalGlue.setVisible(false);

						verticalStrut.setVisible(true);
						verticalGlueLower.setVisible(true);
						verticalGlueUpper.setVisible(true);

						separator.setOrientation(SwingConstants.HORIZONTAL);
						separator.setVisible(true);
						startUp = true;
						mainController.onReadjustDividers();
					} else {
						lastDockingPlace = BorderLayout.NORTH;
						setLayout(new BoxLayout((JToolBar) evt.getSource(), BoxLayout.X_AXIS));
						separator.setVisible(false);

						horizontalStrut.setVisible(true);
						horizontalGlue.setVisible(true);

						verticalStrut.setVisible(false);
						verticalGlueLower.setVisible(false);
						verticalGlueUpper.setVisible(false);

						startUp = false;
						mainController.onReadjustDividers();
					}

				}
			}
		});

		ToolbarButton openButton = new ToolbarButton(ToolbarImage.OPEN, e -> mainController.onOpen(), "Open file",
				"open");

		buttonDefaultColor = openButton.getBackground();

		JButton saveButton = new ToolbarButton(ToolbarImage.SAVE, e -> mainController.onSave(), "Save file", "save");

		JButton previousButton = new ToolbarButton(ToolbarImage.LEFT, e -> mainController.onPrevious(),
				"Open the previous file", "previous");

		JButton nextButton = new ToolbarButton(ToolbarImage.RIGHT, e -> mainController.onNext(), "Open the next file",
				"next");

		restartButton = new ToolbarButton(ToolbarImage.RESTART, e -> mainController.onRestart(),
				"Reset the petrinet graph", "Reset p");

		JButton plusButton = new ToolbarButton(ToolbarImage.PLUS, e -> mainController.onPlus(),
				"Adds a token to a selected place", "plus");

		JButton minusButton = new ToolbarButton(ToolbarImage.MINUS, e -> mainController.onMinus(),
				"Removes a token from a selected place", "minus");

		resetButton = new ToolbarButton(ToolbarImage.RESET, e -> mainController.onReset(),
				"Reset the reachability graph", "reset r");

		analyseButton = new ToolbarButton(ToolbarImage.ANALYSE, e -> mainController.onAnalyse(),
				"Analyse petrinet and create reachability graph", "analyse");

		clearTextButton = new ToolbarButton(ToolbarImage.CLEAR_TEXT, e -> mainController.onClear(), "Clear text area",
				"clear");

		JButton setDefaultButton = new ToolbarButton(ToolbarImage.DEFAULT, e -> mainController.onSetDefault(),
				"Reset split panes", "reset pane");

		undoButton = new ToolbarButton(ToolbarImage.UNDO, e -> mainController.onUndo(), "Undo last step", "undo");

		redoButton = new ToolbarButton(ToolbarImage.REDO, e -> mainController.onRedo(), "Redo last step", "redo");

		JButton zoomInButton = new ToolbarButton(ToolbarImage.ZOOM_IN, e -> mainController.onZoomIn(), "Zoom in",
				"zoom in");

		JButton zoomOutButton = new ToolbarButton(ToolbarImage.ZOOM_OUT, e -> mainController.onZoomOut(), "Zoom out",
				"zoom out");

		changeDesignButton = new ToolbarButton(ToolbarImage.DESIGN, e -> mainController.onChangeDesign(),
				"Change between Metal and Nimbus feel and look",
				"change design");
		openEditorButton = new ToolbarButton(ToolbarImage.EDITOR, e -> mainController.onOpenEditor(),
				"Switch to editor", "editor");

		// Editor specific Buttons

		addPlaceButton = new ToolbarButton(ToolbarImage.ADD_PLACE, e -> mainController.onAddPlace(), "Add place",
				"add place");

		addTransitionButton = new ToolbarButton(ToolbarImage.ADD_TRANSITION, e -> mainController.onAddTransition(),
				"Add transition", "add trans");

		deleteComponentButton = new ToolbarButton(ToolbarImage.DELETE_COMPONENT,
				e -> mainController.onRemoveComponent(), "Delete component", "delete");

		addEdgeButton = new ToolbarButton(ToolbarImage.ADD_EDGE, e -> mainController.onAddEdge(), "Add edge",
				"add edge");

		removeEdgeButton = new ToolbarButton(ToolbarImage.REMOVE_EDGE, e -> mainController.onRemoveEdge(),
				"Remove an edge: choose source first and then target", "remove edge");

		addLabelButton = new ToolbarButton(ToolbarImage.ADD_LABEL, e -> mainController.onAddLabel(),
				"Add label to element", "add label");

		closeEditorButton = new ToolbarButton(ToolbarImage.OPEN_VIEWER, e -> mainController.onCloseEditor(),
				"Switch to viewer", "close editor");

		zoomInReachabilityButton = new ToolbarButton(ToolbarImage.ZOOM_IN, e -> mainController.onZoomInReachability(),
				"Zoom in", "zoom in");

		zoomOutReachabilityButton = new ToolbarButton(ToolbarImage.ZOOM_OUT,
				e -> mainController.onZoomOutReachability(), "Zoom out", "zoom out");

		toggleTreeLayoutButton = new ToolbarButton(ToolbarImage.TREE_LAYOUT, e -> mainController.onToggleTreeLayout(),
				"Turn tree base layout on -> will give the best experience (resets graph)", "tree-layout");

		toggleCircleLayoutButton = new ToolbarButton(ToolbarImage.CIRCLE_LAYOUT,
				e -> mainController.onToggleCircleLayout(),
				"Turn circe based layout on -> might not be as beautiful but may be more fun (resets graph)",
				"circle-layout");

		toggleAutoLayoutButton = new ToolbarButton(ToolbarImage.AUTO_LAYOUT, e -> mainController.onToggleAutoLayout(),
				"Turn auto layout on -> auto layout provided by GraphStream (resets graph)", "auto-layout");

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

		toggleTreeLayoutButton();
		setToolbarMode(ToolbarMode.VIEWER);
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
	public void setToolbarTo(PetrinetPanel panel, LayoutType layoutType) {

		JButton button = new JButton();

		buttonDefaultColor = button.getBackground();

		LookAndFeel laf = UIManager.getLookAndFeel();

		if (laf.getName().equals("Nimbus"))
			buttonHighlightColor = new Color(190, 185, 180);
		else
			buttonHighlightColor = Color.LIGHT_GRAY;

		resetButtons();

		if (layoutType == LayoutType.AUTOMATIC)
			toggleAutoLayoutButton();
		if (layoutType == LayoutType.CIRCLE)
			toggleCircleLayoutButton();
		if (layoutType == LayoutType.TREE)
			toggleTreeLayoutButton();

		if (panel == null)
			return;

		PetrinetGraphEditor editor = panel.getEditor();
		if (editor.addsEdge())
			toggleAddEdgeButton();
		if (editor.removesEdge())
			toggleRemoveEdgeButton();

		PetrinetController controller = panel.getPetrinetController();

		PetrinetQueue queue = controller.getPetrinetQueue();

		if (queue == null)
			return;

		if (!queue.isFirstState())
			toggleUndoButton();
		if (queue.hasNext())
			toggleRedoButton();

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

	/**
	 * 
	 * @return
	 */
	public String getLastDockingPlace() {
		return lastDockingPlace;
	}
/**
 * 
 */
	public void setVertical() {
		// TODO Auto-generated method stub
		
	}

}
