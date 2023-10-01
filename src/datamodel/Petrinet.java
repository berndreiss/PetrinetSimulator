package datamodel;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Petrinet {
	private Transition currentTransition;
	private Map<String, Transition> transitions;//set of transitions represented by a map since the Set interface does not provide a get function
	private Map<String,Place> places;//set of places represented by a map since the Set interface does not provide a get function
	
	public Petrinet(Map<String,Transition> transitions, Map<String, Place> places, Transition startingTransition) {
		this.transitions = transitions;
		this.places = places;
		this.currentTransition = startingTransition;
	}
	
	public Petrinet() {
		this.transitions = new HashMap<String, Transition>();
		this.places = new HashMap<String, Place>();
	}

	
	public Map<String, Transition> getTransitions(){
		return transitions;
	}
	
	public Map<String, Place> getPlaces(){
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
		return t.activate();
		

	}
	
	public void print() {
		System.out.println("Places:");
		for (String s : places.keySet()) {
			Place p = places.get(s);
			System.out.println(p.getId() + ", " + p.getName() + ", " + p.numberOfTokens());
		}
		System.out.println("Transitions:");
		for (String s: transitions.keySet()) {
			Transition t = transitions.get(s);
			System.out.println(t.getId() + ", " + t.getName());
		}
		
		
	}
	
}
