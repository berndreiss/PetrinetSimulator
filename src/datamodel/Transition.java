package datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class representing transitions in petri nets. Every transition has a set of places (see {@link Place}) serve as input (preset) and a set of places (see {@link Place}) that serve as output (postset). 
 * Transitions may be activated, if they are active, meaning every place that serves as an input has a token. See also {@link Petrinet}.
 */
public class Transition extends PetrinetElement {

	private Map<String, Place> inputs = new HashMap<String, Place>();//set of places that serve as input
	private Map<String, Place> outputs = new HashMap<String, Place>();//set of places that serve as output

	

	/**
	 * A new instance of Transition is created. If arguments for preset and postset are not passed preset and postset are initialized as {@link HashSet}.
	 * @param id Id of the transition.
	 * @param name Name of the transition.
	 */
	public Transition(String id) {
		this.id = id;
	}

	/**
	 * A new instance of Transition is created. If arguments for preset and postset are not passed preset and postset are initialized as {@link HashSet}.
	 * @param id Id of the transition.
	 * @param name Name of the transition.
	 */
	public Transition(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	/**
	 * A new instance of Transition is created. Initial sets of input places (preset) and output places (postset) are passed along. 
	 * @param id Id of the transition.
	 * @param name Name of the transition.
	 * @param preset {@link Set} of initial input places.
	 * @param postset {@link Set} of initial output places.
	 */
	public Transition(String id, String name, Map<String, Place> inputs, Map<String, Place> outputs) {
		this.id = id;
		this.name = name;
		this.inputs = inputs;
		this.outputs = outputs;
	}
	
	

	
	/**
	 * Activates a transition if it is active. If any place in the set of inputs does not have tokens, it does not activate. 
	 * Otherwise it Decrements the number of tokens for all places in the input and increments the number of tokens for all places in the output.  
	 * @return
	 */
	public List<Place> activate() {
		ArrayList<Place> returnList = new ArrayList<Place>();
		//if transition is not active, return immediately
		if (!isActive())
			return returnList;

		//decrement tokens
		for (String s: inputs.keySet()) {
			Place p = (Place) inputs.get(s);
			try {
				p.decrementTokens();
				returnList.add(p);
			} catch (OutOfTokensException e) {
				e.printStackTrace();
			}
		}
		//increment tokens
		for (String s: outputs.keySet()) {
			Place p = (Place) outputs.get(s);
			p.incrementTokens();
			returnList.add(p);
		}
		return returnList;
	}
	
	//returns false if one of the places in the input does not have tokens
	//returns true otherwise
	private boolean isActive() {
		
		for (String s: inputs.keySet()) {
			Place p = (Place) inputs.get(s);
			if (!p.hasTokens())
				return false;
		}
		return true;
	}
	
	/**
	 * Adds a place to the set of input places (preset).
	 * @param p {@link Place} to be added as an Input.
	 */
	public void addInput(Place p) {
		inputs.put(p.id, p);
		p.outputs.put(this.id, this);
	}

	/**
	 * Adds a place to the set of output places (postset).
	 * @param p {@link Place} to be added as an Output.
	 */
	public void addOutput(Place p) {
		outputs.put(p.id,p);
		p.inputs.put(this.id, this);
	}

	public Map<String, Place> getInputs(){
		return inputs;
	}
	
	public Map<String, Place> getOutputs(){
		return outputs;
	}

}
