package control;

import gui.MainFrame;

/**
 * <p>
 * A controller defining methods used by a petrinet toolbar.
 * </p>
 */
public interface PetrinetToolbarController extends PetrinetMenuController {

	// PETRINET RELATED METHODS

	/**
	 * Opens the previous file in the current directory. Do nothing if current file
	 * is first one.
	 */
	void onPrevious();

	/**
	 * Opens the next file in the current directory. Do nothing if current file is
	 * last one.
	 */
	void onNext();

	/**
	 * Increments the marked place in the petrinet.
	 */
	void onIncrement();

	/**
	 * Decrements the marked place in the petrinet.
	 */
	void onDecrement();

	// BEGIN VIEWER SPECIFIC METHODS

	/**
	 * Resets the petrinet to its initial markings.
	 */
	void onResetPetrinet();

	/**
	 * Zooms into the petrinet.
	 */
	void onZoomInPetrinet();

	/**
	 * Zooms out of the petrinet.
	 */
	void onZoomOutPetrinet();

	// END VIEWER SPECIFIC METHODS

	// BEGIN EDITOR SPECIFIC METHODS

	/**
	 * Gets an id and adds a new place to the petrinet.
	 */
	void onAddPlace();

	/**
	 * Gets an id and adds a new transition to the petrinet.
	 */
	void onAddTransition();

	/**
	 * Removes an element from the petrinet.
	 */
	void onRemoveComponent();

	/**
	 * Gets an id and adds a new edge to the petrinet.
	 */
	void onAddEdge();

	/**
	 * Removes an edge from the petrinet.
	 */
	void onRemoveEdge();

	/**
	 * Adds a new label to an element of the petrinet.
	 */
	void onAddLabel();

	// END EDITOR SPECIFIC METHODS

	// REACHABILITY GRAPH RELATED METHODS

	// BEGIN VIEWER SPECIFIC METHODS

	/**
	 * Analyses the currently opened petrinet and determine if it is bounded /
	 * unbounded.
	 */
	void onAnalyse();

	/**
	 * Resets the reachability graph and therefore also resets the petrinet.
	 */
	void onReset();

	/**
	 * Undoes the last step in the reachability graph if there is one.
	 */
	void onUndo();

	/**
	 * Redoes the last step in the reachability graph if there is one.
	 */
	void onRedo();

	/**
	 * Clears the text area in the {@link MainFrame}.
	 */
	void onClearTextArea();

	/**
	 * Zoom into the reachability graph.
	 */
	void onZoomInReachability();

	/**
	 * Zoom out of the reachability graph.
	 */
	void onZoomOutReachability();

	/**
	 * Chooses the auto layout provided by the
	 * <a href="https://graphstream-project.org/">GraphStream</a> library for the
	 * reachability graph.
	 */
	void onToggleAutoLayout();

	/**
	 * Chooses the tree layout for the reachability graph.
	 */
	void onToggleTreeLayout();

	/**
	 * Chooses the circle layout for the reachability graph.
	 */
	void onToggleCircleLayout();

	// END VIEWER SPECIFIC METHODS

	/**
	 * Resets the dividers of the split panes to the default ratio.
	 */
	void onSetSplitPanesDefault();

	/**
	 * Resets the dividers of the split panes to the current ratio.
	 */
	void onReadjustDividers();
}
