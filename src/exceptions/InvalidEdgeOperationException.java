package exceptions;

/**
 * <p>
 * The Class InvalidEdgeOperationException.
 * </p>
 * <p>
 * Gets thrown when an edge operation is invalid -> if source or target do not
 * exist, if both source and target are transitions/places or if the edge
 * already exists.
 * </p>
 */
public class InvalidEdgeOperationException extends PetrinetException {

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
