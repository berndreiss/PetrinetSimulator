package core;

import listeners.TransitionStateListener;
import util.IterableMap;

/**
 * <p>
 * Class representing transitions in petrinets.
 * </p>
 * 
 * <p>
 * Every transition has a set of places (see {@link Place}) that serve as input
 * (preset) and a set of places that serve as output (postset). The places know
 * their input and output transitions too and are synchronized when adding or
 * removing transitions. Transitions may be activated, if they are activated,
 * meaning every place that serves as an input has a token. See also
 * {@link Petrinet}.
 * </p>
 */
public class Transition extends PetrinetElement {
	/** Set of places that serve as input. */
	private IterableMap<String, Place> inputs = new IterableMap<String, Place>();

	/** Set of places that serve as output. */
	private IterableMap<String, Place> outputs = new IterableMap<String, Place>();

	/** Activation status of the transition. */
	private boolean activated;

	/** Listens for changes in the activation status of the transition. */
	private TransitionStateListener transitionStateListener;

	/**
	 * A new instance of Transition is created.
	 * 
	 * @param id ID of the transition.
	 */
	Transition(String id) {
		super(id);
		this.activated = checkActivated();
	}

	/**
	 * Fires a transition if it is activated. If any place in the set of inputs does
	 * not have tokens, it does not fire. Otherwise it decrements the number of
	 * tokens for all places in inputs and increments the number of tokens for all
	 * places outputs.
	 *
	 * @return true, if transition fired
	 */
	boolean fire() {
		// if transition is not activated, return immediately
		if (!checkActivated())
			return false;

		// decrement tokens
		for (String s : inputs.keySet()) {
			Place p = (Place) inputs.get(s);
			p.decrementTokens();

		}
		// increment tokens
		for (String s : outputs.keySet()) {
			Place p = (Place) outputs.get(s);
			p.incrementTokens();
		}

		return true;
	}

	/**
	 * Checks if transition is activated.
	 *
	 * @return true, if is activated
	 */
	public boolean isActivated() {
		return activated;
	}

	/**
	 * Checks the activation status and update it.
	 */
	void updateActivationStatus() {
		setActivated(checkActivated());
	}

	/**
	 * Sets the transition state listener.
	 *
	 * @param transitionStateListener the new transition state listener
	 */
	public void setTransitionStateListener(TransitionStateListener transitionStateListener) {
		this.transitionStateListener = transitionStateListener;
	}

	// returns false if one of the places in the input does not have tokens
	// returns true otherwise
	private boolean checkActivated() {
		if (inputs.isEmpty())
			return true;

		for (Place p : inputs)
			if (!p.hasTokens())
				return false;

		return true;
	}

	/**
	 * Adds a place to the set of input places (preset).
	 * 
	 * @param place place to be added as input
	 */
	void addInput(Place place) {

		if (inputs.containsKey(place.getId()))
			return;
		inputs.put(place.getId(), place);
		setActivated(checkActivated());
		place.addOutput(this);

	}

	// set the activation status
	private void setActivated(boolean activated) {

		// if the status is the same return
		if (this.activated == activated)
			return;

		this.activated = activated;

		// inform the state change listener
		if (transitionStateListener != null)
			transitionStateListener.onStateChanged();
	}

	/**
	 * Adds a place to the set of output places (postset).
	 * 
	 * @param place place to be added as an output
	 */
	void addOutput(Place place) {
		if (outputs.containsKey(place.getId()))
			return;
		outputs.put(place.getId(), place);

		// synchronize place
		place.addInput(this);
	}

	/**
	 * Gets the input places.
	 *
	 * @return the input places
	 */
	public Iterable<Place> getInputs() {
		return inputs;
	}

	/**
	 * Gets the output places.
	 *
	 * @return the output places
	 */
	public Iterable<Place> getOutputs() {
		return outputs;
	}

	/**
	 * Removes a place from the inputs (preset).
	 *
	 * @param place the place to be removed
	 */
	void removeInput(Place place) {
		if (!inputs.containsKey(place.getId()))
			return;
		inputs.remove(place.getId());

		// activation status might have changed
		updateActivationStatus();

		// synchronize place
		place.removeOutput(this);

	}

	/**
	 * Removes a place from the outputs (postset).
	 *
	 * @param place the place to be removed
	 */
	void removeOutput(Place place) {
		if (!outputs.containsKey(place.getId()))
			return;
		outputs.remove(place.getId());

		// synchronize place
		place.removeInput(this);
	}

}
