package listeners;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving replayGraph events.
 * The class that is interested in processing a replayGraph
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addReplayGraphListener<code> method. When
 * the replayGraph event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ReplayGraphEvent
 */
public interface ReplayGraphListener {
	
	/**
	 * On graph replay.
	 */
	void onGraphReplay();
}
