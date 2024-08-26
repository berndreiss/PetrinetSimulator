package net.berndreiss.petrinetsimulator.exceptions;

/**
 * <p>
 * The Class DuplicateIdException.
 * </p>
 * 
 * <p>
 * Is thrown when an id already exists in the petrinet.
 * </p>
 */
public class DuplicateIdException extends PetrinetException {

	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new duplicate id exception.
	 *
	 * @param message the message
	 */
	public DuplicateIdException(String message) {
		super(message);
	}

}
