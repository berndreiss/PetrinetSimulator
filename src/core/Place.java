package core;

import listeners.NumberOfTokensChangedListener;
import util.IterableMap;

// TODO: Auto-generated Javadoc
/**
 * Class that represents places in petri nets. Each place has a number of
 * tokens, an id and a name.
 */
public class Place extends PetrinetElement {

	private int numberOfTokens;

	private NumberOfTokensChangedListener numberOfTokensListener;

	private IterableMap<String, Transition> outputs = new IterableMap<String, Transition>();// set of places
																							// that serve as
																							// output
	private IterableMap<String, Transition> inputs = new IterableMap<String, Transition>();// set of places
	// that represent inputs from transitions

	/**
	 * Instantiates a new place.
	 *
	 * @param id the id
	 */
	protected Place(String id) {
		super(id);
	}

	/**
	 * Returns true if place has tokens.
	 * 
	 * @return number of tokens > 0.
	 */
	boolean hasTokens() {
		return numberOfTokens > 0;
	}

	/**
	 * Returns the number of tokens currently at the place.
	 * 
	 * @return Number of tokens.
	 */
	public int getNumberOfTokens() {
		return numberOfTokens;
	}

	/**
	 * Sets the number of tokens.
	 *
	 * @param numberOfTokens the new number of tokens
	 */
	protected void setNumberOfTokens(int numberOfTokens) {

		boolean hadNoTokens = hasTokens() ? false : true;

		this.numberOfTokens = numberOfTokens;

		if (numberOfTokens == 0) {
			for (String s : outputs.keySet()) {
				Transition t = outputs.get(s);
				t.updateActivationStatus();
			}
		}

		if (hadNoTokens && numberOfTokens > 0) {
			for (String s : outputs.keySet()) {
				Transition t = outputs.get(s);
				t.updateActivationStatus();

			}
		}

		if (numberOfTokensListener != null)
			numberOfTokensListener.onNumberChanged(numberOfTokens);

	}

	/**
	 * Sets the number of tokens listener.
	 *
	 * @param numberOfTokensListener the new number of tokens listener
	 */
	public void setNumberOfTokensListener(NumberOfTokensChangedListener numberOfTokensListener) {
		this.numberOfTokensListener = numberOfTokensListener;
	}

	/**
	 * Increments the number of tokens by 1.
	 */
	protected void incrementTokens() {
		setNumberOfTokens(numberOfTokens + 1);
	}

	/**
	 * Decrements the number of tokens by 1.
	 *
	 * @return true, if successful
	 */
	protected boolean decrementTokens() {

		if (numberOfTokens <= 0) {
			System.out.println("There are no tokens in place with ID \"" + this.getId() + "\"");
			return false;
		}

		setNumberOfTokens(numberOfTokens - 1);
		return true;
	}

	/**
	 * Adds a place to the set of output places (postset).
	 *
	 * @param t the t
	 */
	protected void addOutput(Transition t) {
		outputs.put(t.getId(), t);
	}

	/**
	 * Adds the input.
	 *
	 * @param t the t
	 */
	protected void addInput(Transition t) {
		inputs.put(t.getId(), t);
	}

	/**
	 * Gets the outputs.
	 *
	 * @return the outputs
	 */
	protected Iterable<Transition> getOutputs() {
		return outputs;
	}

	/**
	 * Gets the inputs.
	 *
	 * @return the inputs
	 */
	protected Iterable<Transition> getInputs() {
		return inputs;
	}

	/**
	 * Removes the output.
	 *
	 * @param transition the transition
	 */
	void removeOutput(Transition transition) {
		if (!outputs.containsKey(transition.getId()))
			return;
		outputs.remove(transition.getId());
		transition.removeInput(this);
	}

	/**
	 * Removes the input.
	 *
	 * @param transition the transition
	 */
	void removeInput(Transition transition) {
		if (!inputs.containsKey(transition.getId()))
			return;
		inputs.remove(transition.getId());
		transition.removeOutput(this);
	}

}
