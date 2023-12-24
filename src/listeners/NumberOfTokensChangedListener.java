package listeners;

/**
 * <p>
 * The listener interface for receiving events signifying the number of tokens
 * for a place has changed.
 * </p>
 * 
 * <p>
 * Informs the petrinet that the number of tokens has changed for a place.
 * </p>
 *
 */
public interface NumberOfTokensChangedListener {

	/**
	 * On number of tokens changed inform the petrinet.
	 *
	 * @param newNumber the new number of tokens
	 */
	void onNumberChanged(int newNumber);
}
