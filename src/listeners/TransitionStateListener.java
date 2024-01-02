package listeners;

/**
 * <p>
 * The listener interface for receiving events signifying that the state of a
 * transition has changed.
 * </p>
 * <p>
 * Informs the {@link PetrinetComponentChangedListener} that the state of a
 * transition has changed.
 * </p>
 */
public interface TransitionStateListener {

	/**
	 * Informs the {@link PetrinetComponentChangedListener} that the state of a
	 * transition has changed.
	 */
	void onStateChanged();
}
