package gui;

/**
 * <p>
 * A controller defining methods used by a petrinet toolbar.
 * </p>
 */
public interface PetrinetToolbarController extends PetrinetMenuController {

	// PETRINET RELATED METHODS

	/**
	 * Open the previous file in the current directory. Do nothing if current file
	 * is first one.
	 */
	void onPrevious();

	/**
	 * Open the next file in the current directory. Do nothing if current file is
	 * last one.
	 */
	void onNext();

	/**
	 * Increments a marked place in the petrinet.
	 */
	void onIncrement();

	/**
	 * Decrement a marked place in the petrinet. If the place has no marks does
	 * nothing.
	 */
	void onDecrement();

	// BEGIN VIEWER SPECIFIC METHODS

	/**
	 * Resets the petrinet to its initial markings.
	 */
	void onResetPetrinet();

	/**
	 * Zoom in into the petrinet.
	 */
	void onZoomInPetrinet();

	/**
	 * Zoom out of the petrinet.
	 */
	void onZoomOutPetrinet();

	// END VIEWER SPECIFIC METHODS

	// BEGIN EDITOR SPECIFIC METHODS

	/**
	 * Add a new place to the petrinet.
	 */
	void onAddPlace();

	/**
	 * Add a new transition to the petrinet.
	 */
	void onAddTransition();

	/**
	 * Remove an element from the petrinet.
	 */
	void onRemoveComponent();

	/**
	 * Add a new edge to the petrinet.
	 */
	void onAddEdge();

	/**
	 * Remove an edge from the petrinet.
	 */
	void onRemoveEdge();

	/**
	 * Add a new label to the petrinet.
	 */
	void onAddLabel();

	// END EDITOR SPECIFIC METHODS

	// REACHABILITY GRAPH RELATED METHODS

	// BEGIN VIEWER SPECIFIC METHODS
	
	/**
	 * Analyse the currently opened petrinet and determine if it is bounded /
	 * unbounded.
	 */
	void onAnalyse();

	/**
	 * Resets the reachability graph and therefore also resets the petrinet.
	 */
	void onReset();

	/**
	 * Undo the last step in the reachability graph if there is one.
	 */
	void onUndo();

	/**
	 * Redo the last step in the reachability graph if there is one.
	 */
	void onRedo();

	/**
	 * Clears the text area in the main frame.
	 */
	void onClearTextArea();

	/**
	 * On zoom in reachability.
	 */
	void onZoomInReachability();

	/**
	 * On zoom out reachability.
	 */
	void onZoomOutReachability();

	/**
	 * Choose the auto layout provided by the <a href="https://graphstream-project.org/">GraphStream</a> library for the reachability graph.	 
	 */
	void onToggleAutoLayout();

	/**
	 * Choose the tree layout for the reachability graph.
	 */
	void onToggleTreeLayout();

	/**
	 * Choose the circle layout for the reachability graph.
	 */
	void onToggleCircleLayout();

	// END VIEWER SPECIFIC METHODS

	/**
	 * Reset the dividers of the split panes to the default ratio.
	 */
	void onSetSplitPanesDefault();

	/**
	 * Reset the dividers of the split panes to the current ratio.
	 */
	void onReadjustDividers();
}
