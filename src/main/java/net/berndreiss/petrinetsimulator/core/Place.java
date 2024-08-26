package net.berndreiss.petrinetsimulator.core;

import net.berndreiss.petrinetsimulator.listeners.NumberOfTokensChangedListener;
import net.berndreiss.petrinetsimulator.util.IterableMap;

/**
 * <p>
 * Class that represents places in petrinets.
 * </p>
 * 
 * <p>
 * Every place always knows the transitions (see {@link Transition}) it is
 * connected to (inputs and outputs). They are synchronized when added and
 * removed.
 * </p>
 * 
 */
public class Place extends PetrinetElement {

	/** The number of tokens a place currently holds. */
	private int numberOfTokens;

	/** A listener for number of tokens changes. */
	private NumberOfTokensChangedListener numberOfTokensListener;

	/** A map of all transitions firing to this place. */
	private IterableMap<String, Transition> inputs = new IterableMap<String, Transition>();

	/** A map of all transitions this place is providing to. */
	private IterableMap<String, Transition> outputs = new IterableMap<String, Transition>();

	/**
	 * Instantiates a new place.
	 *
	 * @param id the id of the place
	 */
	Place(String id) {
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
	 * @return number of tokens
	 */
	public int getNumberOfTokens() {
		return numberOfTokens;
	}

	/**
	 * Sets the number of tokens.
	 *
	 * @param numberOfTokens the new number of tokens
	 */
	public void setNumberOfTokens(int numberOfTokens) {

		// if nothing has changed return
		if (numberOfTokens == this.numberOfTokens)
			return;

		// keep track if it had no tokens before so that transitions in the output can
		// be notified (may have to be activated)
		boolean hadTokens = hasTokens();

		this.numberOfTokens = numberOfTokens;

		// if number of tokens is 0 or it was 0 before inform all transitions in the
		// output
		if (numberOfTokens == 0 || !hadTokens)
			for (Transition t : outputs)
				t.updateActivationStatus();

		// inform the number of tokens listener that the number has changed
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
	void incrementTokens() {
		setNumberOfTokens(numberOfTokens + 1);
	}

	/**
	 * Decrements the number of tokens by 1.
	 *
	 * @return true, if successful, false if the place has no tokens to begin with
	 */
	boolean decrementTokens() {

		if (numberOfTokens <= 0) {
			System.out.println("There are no tokens in place with ID \"" + this.getId() + "\"");
			return false;
		}

		setNumberOfTokens(numberOfTokens - 1);
		return true;
	}

	/**
	 * Adds a transition to the set of transitions in the output.
	 *
	 * @param transition the transition to be added
	 */
	void addOutput(Transition transition) {
		outputs.put(transition.getId(), transition);
	}

	/**
	 * Adds a transition to the set of transitions in the output.
	 *
	 * @param transition the transition to be added
	 */
	void addInput(Transition transition) {
		inputs.put(transition.getId(), transition);
	}

	/**
	 * Gets the transitions in the output.
	 *
	 * @return the transitions in the output
	 */
	public Iterable<Transition> getOutputs() {
		return outputs;
	}

	/**
	 * Gets the transitions in the input.
	 *
	 * @return the transitions in the input
	 */
	public Iterable<Transition> getInputs() {
		return inputs;
	}

	/**
	 * Removes a transition from the outputs. Also removes the place from the
	 * transitions inputs.
	 *
	 * @param transition the transition to be removed
	 */
	void removeOutput(Transition transition) {
		if (!outputs.containsKey(transition.getId()))
			return;
		outputs.remove(transition.getId());

		// synchronize transition
		transition.removeInput(this);
	}

	/**
	 * Removes a transition from the inputs. Also removes the place from the
	 * transitions outputs.
	 *
	 * @param transition the transition to be removed
	 */
	void removeInput(Transition transition) {
		if (!inputs.containsKey(transition.getId()))
			return;
		inputs.remove(transition.getId());

		// synchronize transition
		transition.removeOutput(this);
	}

}
