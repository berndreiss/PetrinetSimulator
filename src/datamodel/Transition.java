package datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import util.IterableHashMap;

/**
 * Class representing transitions in petri nets. Every transition has a set of
 * places (see {@link Place}) serve as input (preset) and a set of places (see
 * {@link Place}) that serve as output (postset). Transitions may be activated,
 * if they are active, meaning every place that serves as an input has a token.
 * See also {@link Petrinet}.
 */
public class Transition extends PetrinetElement {

	private IterableHashMap<String, Place> inputs;// set of places that serve as input
	private IterableHashMap<String, Place> outputs;// set of places that serve as output

	private boolean active;//TODO change all occurrences to "activated"
	private TransitionActiveListener transitionActiveListener;

	/**
	 * A new instance of Transition is created. If arguments for preset and postset
	 * are not passed preset and postset are initialized as {@link HashSet}.
	 * 
	 * @param id   Id of the transition.
	 * @param name Name of the transition.
	 */
	public Transition(String id) {
		this(id, "");
	}

	/**
	 * A new instance of Transition is created. If arguments for preset and postset
	 * are not passed preset and postset are initialized as {@link HashSet}.
	 * 
	 * @param id   Id of the transition.
	 * @param name Name of the transition.
	 */
	public Transition(String id, String name) {
		this(id, name, new IterableHashMap<String, Place>(), new IterableHashMap<String, Place>());
	}

	/**
	 * A new instance of Transition is created. Initial sets of input places
	 * (preset) and output places (postset) are passed along.
	 * 
	 * @param id      Id of the transition.
	 * @param name    Name of the transition.
	 * @param preset  {@link Set} of initial input places.
	 * @param postset {@link Set} of initial output places.
	 */
	public Transition(String id, String name, IterableHashMap<String, Place> inputs, IterableHashMap<String, Place> outputs) {
		this.id = id;
		this.name = name;
		this.inputs = inputs;
		this.outputs = outputs;
		this.active = checkActive();
	}

	/**
	 * Activates a transition if it is active. If any place in the set of inputs
	 * does not have tokens, it does not activate. Otherwise it Decrements the
	 * number of tokens for all places in the input and increments the number of
	 * tokens for all places in the output.
	 * 
	 * @return
	 */
	public boolean fire() {
		// if transition is not active, return immediately
		if (!checkActive())
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

	public boolean isActive() {
		return active;
	}

	protected void updateActivationStatus() {
		setActive(checkActive());
	}

	public void setTransitionActiveListener(TransitionActiveListener transitionActiveListener) {
		this.transitionActiveListener = transitionActiveListener;
	}

	// returns false if one of the places in the input does not have tokens
	// returns true otherwise
	private boolean checkActive() {

		if (inputs.isEmpty())
			return true;

		for (String s : inputs.keySet()) {
			Place p = inputs.get(s);
			if (!p.hasTokens())
				return false;
		}
		return true;
	}

	/**
	 * Adds a place to the set of input places (preset).
	 * 
	 * @param p {@link Place} to be added as an Input.
	 */
	protected void addInput(Place p) {
		inputs.put(p.id, p);
		p.outputs.put(this.id, this);
		setActive(checkActive());

	}

	private void setActive(boolean active) {

		if (this.active == active)
			return;

		this.active = active;
		if (transitionActiveListener != null)
			transitionActiveListener.onStateChanged(active);
	}

	/**
	 * Adds a place to the set of output places (postset).
	 * 
	 * @param p {@link Place} to be added as an Output.
	 */
	protected void addOutput(Place p) {
		outputs.put(p.id, p);
		p.inputs.put(this.id, this);
	}

	public Iterable<Place> getInputs() {
		return inputs;
	}

	public Iterable<Place> getOutputs() {
		return outputs;
	}

	public void remove(PetrinetComponentChangedListener petrinetComponentChangedListener) {
		
		ArrayList<String> inputStrings = new ArrayList<String>();
		
		for (Place p: inputs)
			inputStrings.add(p.getId());
		
		for(String s: inputStrings) {
			removeInput(inputs.get(s));
			if (petrinetComponentChangedListener != null)
				petrinetComponentChangedListener.onEdgeRemoved(s + getId());
		}
		
		ArrayList<String> outputStrings = new ArrayList<String>();
		
		for (Place p: outputs)
			outputStrings.add(p.getId());

		for(String s: outputStrings) {
			removeOutput(outputs.get(s));
			if (petrinetComponentChangedListener != null)
				petrinetComponentChangedListener.onEdgeRemoved(getId() + s);
		}

	}

	public void removeInput(Place p) {
		if (!inputs.containsKey(p.getId()))
			return;
		inputs.remove(p.getId());
		p.removeOutput(this);
		
	}
	
	public void removeOutput(Place p) {
		if (!outputs.containsKey(p.getId()))
			return;
		outputs.remove(p.getId());
		p.removeInput(this);
	}
	
	
}
