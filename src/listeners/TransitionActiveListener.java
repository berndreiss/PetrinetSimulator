package listeners;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving transitionActive events.
 * The class that is interested in processing a transitionActive
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addTransitionActiveListener<code> method. When
 * the transitionActive event occurs, that object's appropriate
 * method is invoked.
 *
 * @see TransitionActiveEvent
 */
public interface TransitionActiveListener {

	/**
	 * On state changed.
	 *
	 * @param active the active
	 */
	void onStateChanged(boolean active);
}
