package exceptions;

/**
 * The Class InvalidEdgeOperationException.
 */
public class InvalidEdgeOperationException extends PetrinetException{

	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new invalid edge operation exception.
	 *
	 * @param message the message
	 */
	public InvalidEdgeOperationException(String message) {
		super(message);
	}
}
