package datamodel;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that represents places in petri nets. Each place has a number of tokens, an id and a name.
 */
public class Place extends PetrinetElement {

	private int numberOfTokens;
	
	private NumberOfTokensListener numberOfTokensListener;
	
	protected Map<String, Transition> inputs = new HashMap<String, Transition>();//set of places that serve as input
	protected Map<String, Transition> outputs = new HashMap<String, Transition>();//set of places that serve as output

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
	public int getNumberOfTokens() {
		return numberOfTokens;
	}
	
	public void setNumberOfTokens(int numberOfTokens) {
		this.numberOfTokens = numberOfTokens;
		if (numberOfTokensListener != null)
			numberOfTokensListener.numberChanged(numberOfTokens);
	}
	
	/**
	 * Increments the number of tokens by 1.
	 */
	public void incrementTokens() {
		setNumberOfTokens(numberOfTokens+1);
	}
	
	/**
	 * Decrements the number of tokens by 1.
	 * @throws OutOfTokensException Throws Exception when there are no tokens left.
	 */
	public void decrementTokens() throws OutOfTokensException {
		
		if (numberOfTokens <= 0)
			throw new OutOfTokensException("There are no tokens in place with ID \"" + id + "\"");
				
		
		setNumberOfTokens(numberOfTokens-1);

	}

	/**
	 * Adds a place to the set of input places (preset).
	 * @param p {@link Place} to be added as an Input.
	 */
	public void addInput(Transition t) {
		inputs.put(t.id, t);
	}

	/**
	 * Adds a place to the set of output places (postset).
	 * @param p {@link Place} to be added as an Output.
	 */
	public void addOutput(Transition t) {
		outputs.put(t.id,t);
	}

	public Map<String, Transition> getInputs(){
		return inputs;
	}
	
	public Map<String, Transition> getOutputs(){
		return outputs;
	}
	

}


