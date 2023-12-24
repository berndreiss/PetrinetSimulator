package exceptions;

/**
 * <p>
 * The Class PetrinetException.
 * </p>
 * <p>
 * Super class of all exceptions concerning petrinets.
 * </p>
 */
public class PetrinetException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new petrinet exception.
	 *
	 * @param message the message
	 */
	public PetrinetException(String message) {
		super(message);
	}

}
