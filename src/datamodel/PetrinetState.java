package datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import util.IterableHashMap;

public class PetrinetState {

	private String state;
	private List<Integer> placeTokens;

	
	private PetrinetState m;

	private IterableHashMap<String, PetrinetState> predecessors;
	private IterableHashMap<String, PetrinetState> successors;

	private HashMap<String, List<Transition>> transitionMap;

	public PetrinetState(Petrinet petrinet) {

		transitionMap = new HashMap<String, List<Transition>>();
		predecessors = new IterableHashMap<String, PetrinetState>();
		successors = new IterableHashMap<String, PetrinetState>();
		placeTokens = new ArrayList<Integer>();
		
	
		for (Place p: petrinet.getPlaces()) {
			placeTokens.add(p.getNumberOfTokens());
		}

		
		this.state = petrinet.getStateString();
		
	}

	public String getState() {
		return state;
	}
	
	public Iterator<Integer> getPlaceTokens(){
		return placeTokens.iterator();
	}
	
	public int placeTokensSize() {
		return placeTokens.size();
	}

	public Iterable<PetrinetState> getPredecessors() {
		return predecessors;
	}

	public Iterable<PetrinetState> getSuccessors() {
		return successors;
	}

	public void addSuccessor(PetrinetState newSuccessor, Transition t) {
		if (newSuccessor == this)
			return;
		successors.put(newSuccessor.getState(), newSuccessor);
		String mapString = this.getState() + newSuccessor.getState();
		if (!transitionMap.containsKey(mapString)) 
			transitionMap.put(mapString, new ArrayList<Transition>());
		
		List<Transition> transitionList = transitionMap.get(mapString);
		if (!transitionList.contains(t))
			transitionList.add(t);
	}

	public Iterable<Transition> getSuccessorTransitions(PetrinetState successor) {
		if (!successors.containsKey(successor.getState()))
			return new ArrayList<Transition>();
		List<Transition> transitionList = transitionMap.get(this.getState() + successor.getState());
		return transitionList;
	}
	public Transition getFirstSuccessorTransition(PetrinetState successor) {
		if (!successors.containsKey(successor.getState()))
			return null;
		for (Transition t: transitionMap.get(this.getState() + successor.getState()))
			return t;
		return null;
	}

	public void addPredecessor(PetrinetState newPredecessor, Transition t) {
		if (newPredecessor == null)
			return;
		predecessors.put(newPredecessor.getState(), newPredecessor);
		String mapString = newPredecessor.getState()+this.getState();
		if (!transitionMap.containsKey(mapString)) 
			transitionMap.put(mapString, new ArrayList<Transition>());
		
		List<Transition> transitionList = transitionMap.get(mapString);
		if (!transitionList.contains(t))
			transitionList.add(t);
	}

	public Iterable<Transition> getPredecessorTransitions(PetrinetState predecessor) {
		if (!predecessors.containsKey(predecessor.getState()))
			return new ArrayList<Transition>();
		List<Transition> transitionList = transitionMap.get(predecessor.getState() + this.getState());
		return transitionList;
	}

	public void setM(PetrinetState m, StateChangeListener stateChangeListener) {
		this.m = m;
		if (stateChangeListener != null)
			stateChangeListener.onMarkInvalid(m, this);
	}

	public PetrinetState getM() {
		return m;
	}

	public boolean isBiggerThan(PetrinetState other) {

		return compareStates(other, (i, j) -> i < j);
	}

	public boolean isSmallerThan(PetrinetState other) {
		return compareStates(other, (i, j) -> i > j);
	}

	private boolean compareStates(PetrinetState other, ReachabilityComparator comparator) {
		if (placeTokensSize() != other.placeTokensSize())
			return false;

		boolean differenceInItemFlag = false;

		Iterator<Integer> thisIt = this.getPlaceTokens();
		Iterator<Integer> otherIt = other.getPlaceTokens();

		while (thisIt.hasNext()) {
			int thisPlaceTokens = thisIt.next();
			int otherPlaceTokens = otherIt.next();

			if (comparator.compare(thisPlaceTokens, otherPlaceTokens))
				return false;

			if (!(thisPlaceTokens == otherPlaceTokens))
				differenceInItemFlag = true;
		}

		if (differenceInItemFlag)
			return true;

		return false;
	}

	private interface ReachabilityComparator {

		boolean compare(int i, int j);
	}

	

	public List<PetrinetState> getListToOtherState(PetrinetState other){
		
		return getListToOther(this, other, new ArrayList<PetrinetState>());
	}

	private List<PetrinetState> getListToOther(PetrinetState state, PetrinetState other, List<PetrinetState> visited){
		if (visited.contains(state))
			return null;
		
		if (state == other) {
			return visited;
		}
		visited.add(state);
		for (PetrinetState rs: state.getPredecessors()) {
			List<PetrinetState> list = getListToOther(rs, other, visited);
			if (list != null)
				return visited;
			visited.remove(rs);
		}
		return null;
	}
	
	public Iterable<String> getTransitionMapKeys(){
		return transitionMap.keySet();
	}

	public void removePredecessor(PetrinetState petrinetState, StateChangeListener stateChangeListener) {
		if (!predecessors.containsKey(petrinetState.getState()))
				return;
		
		List<Transition> transitions = transitionMap.get(petrinetState.getState() + getState());
		
		for (Transition t: transitions) {
			if (stateChangeListener != null)
				stateChangeListener.onRemoveEdge(petrinetState, this, t);
		}
		transitionMap.remove(petrinetState.getState() + getState());
		
		predecessors.remove(petrinetState.getState());
		
		petrinetState.removeSuccessor(this, null);
		
	}

	public void removeSuccessor(PetrinetState petrinetState, StateChangeListener stateChangeListener) {
	
		if (!successors.containsKey(petrinetState.getState()))
			return;
		
		List<Transition> transitions = transitionMap.get(getState() + petrinetState.getState());
		
		for (Transition t: transitions) {
			if (stateChangeListener != null)
				stateChangeListener.onRemoveEdge(this, petrinetState, t);
		}
		transitionMap.remove(getState() + petrinetState.getState());
		
		successors.remove(petrinetState.getState());
		
		petrinetState.removePredecessor(this, null);

		
	}

	public int getPredecessorsSize() {
		return predecessors.size();
	}
	
	public int getSuccessorsSize() {
		return successors.size();
	}

}
