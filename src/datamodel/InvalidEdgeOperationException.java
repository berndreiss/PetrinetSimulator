package datamodel;

import control.PetrinetException;

public class InvalidEdgeOperationException extends PetrinetException{

	private static final long serialVersionUID = 1L;

	public InvalidEdgeOperationException(String message) {
		super(message);
	}
}
