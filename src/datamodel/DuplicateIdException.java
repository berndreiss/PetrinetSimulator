package datamodel;

import control.PetrinetException;

public class DuplicateIdException extends PetrinetException {

	private static final long serialVersionUID = 1L;
	
	public DuplicateIdException(String message) {
		super(message);
	}

}
