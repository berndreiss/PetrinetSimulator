package datamodel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Petrinet {

	public static final Comparator<String> TREE_COMPARATOR = String.CASE_INSENSITIVE_ORDER;

	private Map<String, Transition> transitions;// set of transitions represented by a map since the Set interface does
												// not provide a get function
	private Map<String, Place> places;// set of places represented by a map since the Set interface does not provide a
										// get function
	private Map<String, String> originalArcIds;

	private PetrinetStateChangedListener petrinetStateChangedListener;

	public void addPetrinetStateChangedListener(PetrinetStateChangedListener petrinetStateChangedListener) {
		this.petrinetStateChangedListener = petrinetStateChangedListener;
	}

	public Petrinet() {
		this.transitions = new HashMap<String, Transition>();
		this.places = new TreeMap<String, Place>(TREE_COMPARATOR);
		this.originalArcIds = new HashMap<String, String>();
	}

	public void setTransitions(Map<String, Transition> transitions) {
		this.transitions = transitions;
	}

	public Map<String, Transition> getTransitions() {
		return transitions;
	}

	public Map<String, String> getOriginalArcIds(){
		return originalArcIds;
	}
	
	public void setPlaces(Map<String, Place> places) {
		this.places = places;

		for (String s : places.keySet())
			System.out.println(s);
	}

	public Map<String, Place> getPlaces() {
		return places;
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
		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onChange(null);
	}

	public void addInput(Place p, Transition t) throws DuplicateIdException {
		if (!places.containsKey(p.getId()))
			addPlace(p);
		if (!transitions.containsKey(t.getId()))
			addTransition(t);
		t.addInput(p);
	}

	public void addOutput(Place p, Transition t) throws DuplicateIdException {
		if (!places.containsKey(p.getId()))
			addPlace(p);
		if (!transitions.containsKey(t.getId()))
			addTransition(t);
		t.addOutput(p);
	}

	private boolean isTransition(String id) {
		if (transitions.containsKey(id))
			return true;
		return false;
	}

	public List<Place> activate(String id) {
		if (!isTransition(id))
			return new ArrayList<Place>();

		Transition t = transitions.get(id);
		List<Place> changedPlaces = t.activate();

		if (changedPlaces.size() > 0 && petrinetStateChangedListener != null)
			petrinetStateChangedListener.onChange(t);

		return changedPlaces;

	}

	public String getOriginalArcId(String arcId) {
		return originalArcIds.get(arcId);
	}

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
		}

	}

}
