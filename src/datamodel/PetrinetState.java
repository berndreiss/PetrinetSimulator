package datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import util.IterableMap;

public class PetrinetState {

	private String state;
	private List<Integer> placeTokens;

	private PetrinetState m;

	private IterableMap<String, PetrinetState> predecessors;
	private IterableMap<String, PetrinetState> successors;

	private HashMap<String, List<Transition>> transitionMap;
	
	private int level;

	public PetrinetState(Petrinet petrinet, int level) {

		transitionMap = new HashMap<String, List<Transition>>();
		predecessors = new IterableMap<String, PetrinetState>();
		successors = new IterableMap<String, PetrinetState>();
		placeTokens = new ArrayList<Integer>();

		for (Place p : petrinet.getPlaces()) {
			placeTokens.add(p.getNumberOfTokens());
		}

		this.state = petrinet.getStateString();
		this.level = level;
	}

	public String getState() {
		return state;
	}

	public Iterator<Integer> getPlaceTokens() {
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
	
	public int getLevel() {
		return level;
	}

	public void addSuccessor(PetrinetState newSuccessor, Transition t) {
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
		for (Transition t : transitionMap.get(this.getState() + successor.getState()))
			return t;
		return null;
	}

	public void addPredecessor(PetrinetState newPredecessor, Transition t) {
		if (newPredecessor == null)
			return;
		predecessors.put(newPredecessor.getState(), newPredecessor);
		String mapString = newPredecessor.getState() + this.getState();
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

	public void setM(PetrinetState m, ReachabilityStateChangeListener stateChangeListener) {
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

	public List<PetrinetState> getPathToOtherState(PetrinetState other) {

		List<PetrinetState> list = getPathToOther(this, other, new HashSet<PetrinetState>(), new ArrayList<PetrinetState>());
		if (list.contains(this))
			list.remove(this);
		return list;
	}

	private List<PetrinetState> getPathToOther(PetrinetState state, PetrinetState other, Set<PetrinetState> visited,
			ArrayList<PetrinetState> path) {
		if (visited.contains(state))
			return null;

		if (state == other) {
			return path;
		}
		if (state != this)
			visited.add(state);
		for (PetrinetState rs : state.getPredecessors()) {
			List<PetrinetState> list = getPathToOther(rs, other, visited, path);
			if (list != null) {
				
				if (path.contains(state)) {//circle!
					path.clear();
					return path;
				}
				path.add(state);
				return path;
			}
		}
		return null;
	}

	public boolean hasEdges() {
		return transitionMap.size() >0;
	}
	
	public void removePredecessor(PetrinetState petrinetState, ReachabilityStateChangeListener stateChangeListener) {
		if (!predecessors.containsKey(petrinetState.getState()))
			return;

		List<Transition> transitions = transitionMap.get(petrinetState.getState() + getState());

		if (transitions == null)//case when PetrinetState was its own successor/predecessor
			return;

		for (Transition t : transitions) {
			if (stateChangeListener != null)
				stateChangeListener.onRemoveEdge(petrinetState, this, t);
		}
		transitionMap.remove(petrinetState.getState() + getState());

		predecessors.remove(petrinetState.getState());

		petrinetState.removeSuccessor(this, null);

	}

	public void removeSuccessor(PetrinetState petrinetState, ReachabilityStateChangeListener stateChangeListener) {

		if (!successors.containsKey(petrinetState.getState()))
			return;

		List<Transition> transitions = transitionMap.get(getState() + petrinetState.getState());

		if (transitions == null)//case when PetrinetState was its own successor/predecessor
			return;
		
		for (Transition t : transitions) {
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

	public void removeAllPredecessors(ReachabilityStateChangeListener stateChangeListener) {
		ArrayList<String> predecessorStrings = new ArrayList<String>();
		
		for (String s: predecessors.keySet()) 
			predecessorStrings.add(s);
		
		for (String s: predecessorStrings)
			removePredecessor(predecessors.get(s), stateChangeListener);
		
	}
	public void removeAllSuccessors(ReachabilityStateChangeListener stateChangeListener) {
		ArrayList<String> successorStrings = new ArrayList<String>();
		
		for (String s: successors.keySet())
			successorStrings.add(s);
		
		for (String s: successorStrings)
			removeSuccessor(successors.get(s), stateChangeListener);
		
	}

//	public void print() {
//		System.out.println("STATE: " + getState());
//		System.out.println("PREDECESSORS: ");
//		for (PetrinetState ps: predecessors)
//			ps.print();
//		
//		System.out.println("SUCCESSORS:");
//		for (PetrinetState ps: successors)
//			ps.print();
//		
//		System.out.println("TRANSITION MAP:");
//		
//		for (String s : transitionMap.keySet())
//			System.out.println(s);
//			
//	}
}
