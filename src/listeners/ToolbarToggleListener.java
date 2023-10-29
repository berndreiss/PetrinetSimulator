package listeners;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving toolbarToggle events.
 * The class that is interested in processing a toolbarToggle
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addToolbarToggleListener<code> method. When
 * the toolbarToggle event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ToolbarToggleEvent
 */
public interface ToolbarToggleListener {
	
	/**
	 * On edge added.
	 */
	void onEdgeAdded();
	
	/**
	 * On edge removed.
	 */
	void onEdgeRemoved();
	
	/**
	 * On redo changed.
	 */
	void onRedoChanged();
	
	/**
	 * On undo changed.
	 */
	void onUndoChanged();
}
