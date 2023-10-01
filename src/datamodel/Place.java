package datamodel;
/**
 * Class that represents places in petri nets. Each place has a number of tokens, an id and a name.
 */
public class Place extends PetrinetElement {

	private int numberOfTokens;
	
	public Place(String id, String name, int initialTokens) {
		this.id = id;
		this.name = name;
		this.numberOfTokens = initialTokens;
	}
	
	public Place(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public Place(String id) {
		this.id = id;
	}

	
	/**
	 * Returns true if place has tokens.
	 * @return number of tokens > 0.
	 */
	public boolean hasTokens() {
		return numberOfTokens > 0;
	}
	/**
	 * Returns the number of tokens currently at the place.
	 * @return Number of tokens.
	 */
	public int numberOfTokens() {
		return numberOfTokens;
	}
	
	public void setNumberOfTokens(int numberOfTokens) {
		this.numberOfTokens = numberOfTokens;
	}
	
	/**
	 * Increments the number of tokens by 1.
	 */
	public void incrementTokens() {
		numberOfTokens++;
	}
	
	/**
	 * Decrements the number of tokens by 1.
	 * @throws OutOfTokensException Throws Exception when there are no tokens left.
	 */
	public void decrementTokens() throws OutOfTokensException {
		
		if (numberOfTokens <= 0)
			throw new OutOfTokensException("There are no tokens in place with ID \"" + id + "\"");
				
		numberOfTokens--;
	}

	
}
