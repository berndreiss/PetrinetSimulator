package listeners;

/**
 * <p>
 * The listener interface for receiving events that need to toggle certain
 * buttons of the toolbar.
 * </p>
 *
 * <p>
 * These buttons are adding/removing edges and the un-/redo buttons.
 * </p>
 */
public interface ToolbarToggleListener {

	/**
	 * An edge has been added and the add edge button needs to be reset.
	 */
	void onEdgeAdded();

	/**
	 * An edge has been removed and the remove edge button needs to be reset.
	 */
	void onEdgeRemoved();

	/**
	 * The state of being able to redo steps has changed and the button needs to be
	 * toggled.
	 */
	void onRedoChanged();

	/**
	 * The state of being able to undo steps has changed and the button needs to be
	 * toggled.
	 */
	void onUndoChanged();
}
