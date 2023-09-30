package datamodel;
/**
 * Class that represents places in petri nets. Each place has a number of tokens, an id and a name.
 */
public class Place implements Comparable<Place> {

	private int numberOfTokens;
	private String id;
	private String name;
	
	public Place(String id, String name, int initialTokens) {
		this.id = id;
		this.name = name;
		this.numberOfTokens = initialTokens;
	}
	
	public Place(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	/**
	 * Returns the id of the place.
	 * @return Id of given place.
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Returns the name of the place.
	 * @return Name of given place.
	 */
	public String getName() {
		return name;
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

	@Override
	public int compareTo(Place o) {
		if (this.id.equals(o.id))
			return 0;
		
		return 1;
	}
	
}
