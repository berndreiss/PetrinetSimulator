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

	private IterableHashMap<String, Transition> transitions;// set of transitions represented by a map since the Set
															// interface does
	// not provide a get function
	private IterableTreeMap<String, Place> places;// set of places represented by a map since the Set interface does not
													// provide a
	// get function
	private IterableHashMap<String, String> originalArcIds;

	private PetrinetStateChangedListener petrinetStateChangedListener;
	private PetrinetComponentChangedListener petrinetComponentChangedListener;

	public void setPetrinetChangeListener(PetrinetStateChangedListener petrinetChangedListener) {
		this.petrinetStateChangedListener = petrinetChangedListener;
	}

	public void setPetrinetComponentChangedListener(PetrinetComponentChangedListener petrinetComponentChangedListener) {
		this.petrinetComponentChangedListener = petrinetComponentChangedListener;
	}

	public Petrinet() {
		this.transitions = new IterableHashMap<String, Transition>();
		this.places = new IterableTreeMap<String, Place>(TREE_COMPARATOR);
		this.originalArcIds = new IterableHashMap<String, String>();
	}

	public Iterable<Transition> getActiveTransitions() {
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

	public void setCoordinates(String id, float x, float y) {
		PetrinetElement element = getPetrinetElement(id);

		if (element == null)
			return;

		element.setX(x);
		element.setY(y);

		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onPetrinetElementSetCoordinates(element, x, y);

	}

	public void removePetrinetElement(String id) {
		PetrinetElement element = getPetrinetElement(id);

		if (element == null)
			return;
		
		if (element instanceof Place) {
			Place p = (Place) element;				
			p.remove(petrinetComponentChangedListener);
			places.remove(p.getId());
		}
		if (element instanceof Transition) {
			Transition t = (Transition) element;
			t.remove(petrinetComponentChangedListener);
			transitions.remove(t.getId());
			
		}

		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onComponentChanged(places.size() >0?this:null);//removes initialstate from ReachabilityGraph
		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onPetrinetElementRemoved(element);

	}

	public void addTransition(Transition t) throws DuplicateIdException {
		if (transitions.containsKey(t.getId()) || places.containsKey(t.getId()))
			throw new DuplicateIdException("Id already exists");
		transitions.put(t.getId(), t);

		t.setTransitionActiveListener(activated -> {
				if (petrinetComponentChangedListener != null)
					petrinetComponentChangedListener.onTransitionStateChanged(t);
			
		});
		
		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onPetrinetElementAdded(t);

	}

	public void addPlace(Place p) throws DuplicateIdException {
		if (transitions.containsKey(p.getId()) || places.containsKey(p.getId()))
			throw new DuplicateIdException("Id already exists");
		places.put(p.getId(), p);

		p.setNumberOfTokensListener(newNumber -> {
			if (petrinetComponentChangedListener != null)
				petrinetComponentChangedListener.onPlaceTokenCountChanged(p);

		});

		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onComponentChanged(this);
		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onPetrinetElementAdded(p);

	}

	public void setTokens(String id, int numberOfTokens) {
		if (!places.containsKey(id))
			return;
		Place p = places.get(id);

		if (p.getNumberOfTokens() == numberOfTokens)
			return;

		p.setNumberOfTokens(numberOfTokens);

		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onComponentChanged(this);

	}

	public void addInput(Place p, Transition t, String id) throws DuplicateIdException {
		if (!places.containsKey(p.getId())) {// needs to be here in case arcs are added before places from files
			addPlace(p);
			if (petrinetStateChangedListener != null)
				petrinetStateChangedListener.onComponentChanged(this);
		}
		if (!transitions.containsKey(t.getId()))// needs to be here in case arcs are added before places from files
			addTransition(t);
		t.addInput(p);
		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onEdgeAdded(p, t, id);
		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onComponentChanged(this);
	}

	public void addOutput(Place p, Transition t, String id) throws DuplicateIdException {
		if (!places.containsKey(p.getId())) {// needs to be here in case arcs are added before places from files
			addPlace(p);
		}
		if (!transitions.containsKey(t.getId()))// needs to be here in case arcs are added before places from files
			addTransition(t);
		t.addOutput(p);

		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onEdgeAdded(t, p, id);
		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onComponentChanged(this);

	}

	public void removeEdge(String source, String target) {

		// TODO implement
		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onEdgeRemoved(source + target);
		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onComponentChanged(this);

	}

	private boolean isTransition(String id) {
		if (transitions.containsKey(id))
			return true;
		return false;
	}

	public void fireTransition(String id) {
		if (!isTransition(id))
			return;

		Transition t = transitions.get(id);
		boolean fired = t.fire();

		if (!fired)
			return;
		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onTransitionFire(t);

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
			for (Place p : places) {
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
			for (Place p: t.getInputs()) {
				System.out.println(p.id + " " + p.getNumberOfTokens());
			}
		}

	}

	public void incrementPlace(String markedPlace) {
		if (!places.containsKey(markedPlace))// should not happen -> here for safety reasons
			return;

		Place p = places.get(markedPlace);
		p.incrementTokens();

		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onComponentChanged(this);

	}

	public void decrementPlace(String markedPlace) {
		if (!places.containsKey(markedPlace))// should not happen -> here for safety reasons
			return;
		Place p = places.get(markedPlace);
		boolean decremented = p.decrementTokens();
		if (!decremented)
			return;
		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onComponentChanged(this);

	}

	public String getStateString() {
		StringBuilder sb = new StringBuilder();

		sb.append("(");

		for (Place p : getPlaces()) {
			sb.append(p.getNumberOfTokens() + "|");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");
		return sb.toString();
	}

	public void setPetrinetElementName(String id, String name) {
		PetrinetElement element = getPetrinetElement(id);
		element.setName(name);
		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onSetPetrinetElementName(element);

	}
	
	public boolean hasPlaces() {
		
		return places.size()>0;
	}

}
