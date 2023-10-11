package datamodel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import util.IterableHashMap;
import util.IterableTreeMap;

public class Petrinet {

	public static final Comparator<String> TREE_COMPARATOR = String.CASE_INSENSITIVE_ORDER;

	private IterableHashMap<String, Transition> transitions;// set of transitions represented by a map since the Set interface does
												// not provide a get function
	private IterableTreeMap<String, Place> places;// set of places represented by a map since the Set interface does not provide a
										// get function
	private IterableHashMap<String, String> originalArcIds;

	private PetrinetChangeListener petrinetChangeListener;
	
	public void setPetrinetChangeListener(PetrinetChangeListener petrinetChangeListener) {
		this.petrinetChangeListener = petrinetChangeListener;
	}

	public Petrinet() {
		this.transitions = new IterableHashMap<String, Transition>();
		this.places = new IterableTreeMap<String, Place>(TREE_COMPARATOR);
		this.originalArcIds = new IterableHashMap<String, String>();
	}

	
	public Iterable<Transition> getActiveTransitions(){
		ArrayList<Transition> activeTransitions = new ArrayList<Transition>();
		
		for (Transition t : getTransitions())
			if (t.isActive())
				activeTransitions.add(t);
		
		return activeTransitions;
	}
			
	
	public Iterable<Transition> getTransitions() {
		return transitions;
	}

	public Iterable<Place> getPlaces() {
		return places;
	}

	public Place getPlace(String id) {
		return places.get(id);
	}

	public Transition getTransition(String id) {
		return transitions.get(id);
	}

	public PetrinetElement getPetrinetElement(String id) {
		PetrinetElement element = places.get(id);
		if (element == null)
			element = transitions.get(id);
		return element;
	}

	public void setTokens(String id, int numberOfTokens) {
		if (!places.containsKey(id))
			return;
		Place p = places.get(id);

		if (p.getNumberOfTokens() != numberOfTokens)
			if (petrinetChangeListener != null)
				petrinetChangeListener.onChanged(this);;

		p.setNumberOfTokens(numberOfTokens);

	}

	public void addTransition(Transition t) throws DuplicateIdException {
		if (transitions.containsKey(t.getId()) || places.containsKey(t.getId()))
			throw new DuplicateIdException("Id already exists");
		transitions.put(t.getId(), t);
	}

	public void addPlace(Place p) throws DuplicateIdException {
		if (transitions.containsKey(p.getId()) || places.containsKey(p.getId()))
			throw new DuplicateIdException("Id already exists");
		places.put(p.getId(), p);
		if (petrinetChangeListener != null)
			petrinetChangeListener.onChanged(this);;
	}

	public void addInput(Place p, Transition t) throws DuplicateIdException {
		if (!places.containsKey(p.getId())) {//needs to be here in case arcs are added before places from files
			addPlace(p);
			if (petrinetChangeListener != null)
				petrinetChangeListener.onChanged(this);
		}
		if (!transitions.containsKey(t.getId()))//needs to be here in case arcs are added before places from files
			addTransition(t);
		t.addInput(p);
	}

	public void addOutput(Place p, Transition t) throws DuplicateIdException {
		if (!places.containsKey(p.getId())) {//needs to be here in case arcs are added before places from files
			addPlace(p);
			if (petrinetChangeListener != null)
				petrinetChangeListener.onChanged(this);
		}
		if (!transitions.containsKey(t.getId()))//needs to be here in case arcs are added before places from files
			addTransition(t);
		t.addOutput(p);
	}

	private boolean isTransition(String id) {
		if (transitions.containsKey(id))
			return true;
		return false;
	}

	public Petrinet fireTransition(String id) {
		if (!isTransition(id))
			return this;

		Transition t = transitions.get(id);
		boolean fired = t.fire();

		if (fired && petrinetChangeListener != null)
			petrinetChangeListener.onTransitionFire(t);

		return this;
	}

	public String getOriginalArcId(String arcId) {
		return originalArcIds.get(arcId);
	}

	public void addOriginalArcId(String source, String target, String id) {
		originalArcIds.put(source + target, id);
	}


	public void setState(PetrinetState state) {
		Iterator<Integer> integerIt = state.getPlaceTokens();

		if (places.size() == state.placeTokensSize()) {
			for (Place p: places) {
				p.setNumberOfTokens(integerIt.next());
			}
				
			
		}
			
		
	}

	/**
	 * Prints the Petrinet to the terminal. Mainly for debugging purposes.
	 */
	public void print() {
		System.out.println("Places:");
		for (String s : places.keySet()) {
			Place p = places.get(s);
			System.out.println(p.getId() + ", " + p.getName() + ", " + p.getNumberOfTokens());
		}
		System.out.println("Transitions:");
		for (String s : transitions.keySet()) {
			Transition t = transitions.get(s);
			System.out.println(t.getId() + ", " + t.getName());
			Map<String, Place> places = t.getInputs();
			for (String st : places.keySet()) {
				Place p = places.get(st);
				System.out.println(p.id + " " + p.getNumberOfTokens());
			}
		}

	}

	public void incrementPlace(String markedPlace) {
		if (!places.containsKey(markedPlace))//should not happen -> here for safety reasons
			return;
		Place p = places.get(markedPlace);
		p.incrementTokens();
		if (petrinetChangeListener != null)
			petrinetChangeListener.onChanged(this);
	}

	public void decrementPlace(String markedPlace) {
		if (!places.containsKey(markedPlace))//should not happen -> here for safety reasons
			return;
		Place p = places.get(markedPlace);
		boolean decremented = p.decrementTokens();	
		if (petrinetChangeListener != null && decremented)
			petrinetChangeListener.onChanged(this);

	}
	
	public String getStateString() {
	StringBuilder sb = new StringBuilder();
		
		sb.append("(");

		for (Place p: getPlaces()) {
			sb.append(p.getNumberOfTokens()+"|");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(")");
		return sb.toString();
	}
	

}
