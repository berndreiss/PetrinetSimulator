package datamodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class representing transitions in petri nets. Every transition has a set of places (see {@link Place}) serve as input (preset) and a set of places (see {@link Place}) that serve as output (postset). 
 * Transitions may be activated, if they are active, meaning every place that serves as an input has a token. See also {@link Petrinet}.
 */
public class Transition implements Comparable<Transition> {

	private String id;
	private String name;
	
	private Set<Place> preset;//set of places that serve as input
	private Set<Place> postset;//set of places that serve as output
	
	/**
	 * A new instance of Transition is created. If arguments for preset and postset are passed preset and postset are initialized as {@link HashSet}.
	 * @param id Id of the transition.
	 * @param name Name of the transition.
	 */
	public Transition(String id, String name) {
		this.id = id;
		this.name = name;
		preset = new HashSet<Place>();
		postset = new HashSet<Place>();
	}
	
	/**
	 * A new instance of Transition is created. Initial sets of input places (preset) and output places (postset) are passed along. 
	 * @param id Id of the transition.
	 * @param name Name of the transition.
	 * @param preset {@link Set} of initial input places.
	 * @param postset {@link Set} of initial output places.
	 */
	public Transition(String id, String name, Set<Place> preset, Set<Place> postset) {
		this.id = id;
		this.name = name;
		this.preset = preset;
		this.postset = postset;
	}
	
	/**
	 * Returns the id of the transition.
	 * @return Id of the transition.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns the name of the transition.
	 * @return Name of the transition.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Adds a place to the set of input places (preset).
	 * @param p {@link Place} to be added as an Input.
	 */
	public void addInput(Place p) {
		preset.add(p);
	}

	/**
	 * Adds a place to the set of output places (postset).
	 * @param p {@link Place} to be added as an Output.
	 */
	public void addOutput(Place p) {
		postset.add(p);
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
		for (Place p: preset) {
			try {
				p.decrementTokens();
				returnList.add(p);
			} catch (OutOfTokensException e) {
				e.printStackTrace();
			}
		}
		//increment tokens
		for (Place p: postset) {
			p.incrementTokens();
			returnList.add(p);
		}
		return returnList;
	}
	
	//returns false if one of the places in the input does not have tokens
	//returns true otherwise
	private boolean isActive() {
		
		for (Place p: preset)
			if (!p.hasTokens())
				return false;
		
		return true;
	}

	@Override
	public int compareTo(Transition o) {
		if (this.id.equals(o.id))
			return 0;
		
		return 1;
	}
}
