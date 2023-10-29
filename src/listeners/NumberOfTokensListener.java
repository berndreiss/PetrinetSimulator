package listeners;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving numberOfTokens events.
 * The class that is interested in processing a numberOfTokens
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addNumberOfTokensListener<code> method. When
 * the numberOfTokens event occurs, that object's appropriate
 * method is invoked.
 *
 * @see NumberOfTokensEvent
 */
public interface NumberOfTokensListener {

	/**
	 * Number changed.
	 *
	 * @param newNumber the new number
	 */
	void numberChanged(int newNumber);
}
