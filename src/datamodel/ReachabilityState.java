package datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ReachabilityState {

	private PetrinetState petrinetState;

	private ReachabilityState m;

	private List<ReachabilityState> predecessors;
	private List<ReachabilityState> successors;

	private Map<String, List<Transition>> transitionMap;

	public ReachabilityState(PetrinetState petrinetState, ReachabilityState predecessor) {
		this.petrinetState = petrinetState;

		transitionMap = new HashMap<String, List<Transition>>();
		predecessors = new ArrayList<ReachabilityState>();
		successors = new ArrayList<ReachabilityState>();
		if (predecessor != null)
			predecessors.add(predecessor);
	}

	public PetrinetState getPetrinetState() {
		return this.petrinetState;
	}

	public String getState() {
		return petrinetState.getState();
	}

	public List<ReachabilityState> getPredecessors() {
		return predecessors;
	}

	public List<ReachabilityState> getSuccessors() {
		return successors;
	}

	public void addSuccessor(ReachabilityState newSuccessor, Transition t) {
		if (newSuccessor == this)
			return;
		successors.add(newSuccessor);
		String mapString = this.getState() + newSuccessor.getState();
		if (!transitionMap.containsKey(mapString)) 
			transitionMap.put(mapString, new ArrayList<Transition>());
		
		List<Transition> transitionList = transitionMap.get(mapString);
		if (!transitionList.contains(t))
			transitionList.add(t);
	}

	public Iterable<Transition> getSuccessorTransitions(ReachabilityState successor) {
		if (!successors.contains(successor))
			return new ArrayList<Transition>();
		List<Transition> transitionList = transitionMap.get(this.getState() + successor.getState());
		return transitionList;
	}
	public Transition getFirstSuccessorTransition(ReachabilityState successor) {
		if (!successors.contains(successor))
			return null;
		for (Transition t: transitionMap.get(this.getState() + successor.getState()))
			return t;
		return null;
	}

	public void addPredecessor(ReachabilityState newPredecessor, Transition t) {
		if (newPredecessor == null)
			return;
		predecessors.add(newPredecessor);
		String mapString = newPredecessor.getState()+this.getState();
		if (!transitionMap.containsKey(mapString)) 
			transitionMap.put(mapString, new ArrayList<Transition>());
		
		List<Transition> transitionList = transitionMap.get(mapString);
		if (!transitionList.contains(t))
			transitionList.add(t);
	}

	public Iterable<Transition> getPredecessorTransitions(ReachabilityState predecessor) {
		if (!predecessors.contains(predecessor))
			return new ArrayList<Transition>();
		List<Transition> transitionList = transitionMap.get(predecessor.getState() + this.getState());
		return transitionList;
	}

	public void setM(ReachabilityState m) {
		this.m = m;
	}

	public ReachabilityState getM() {
		return m;
	}

	public boolean isBiggerThan(ReachabilityState other) {

		return compareStates(other, (i, j) -> i < j);
	}

	public boolean isSmallerThan(ReachabilityState other) {
		return compareStates(other, (i, j) -> i > j);
	}

	private boolean compareStates(ReachabilityState other, ReachabilityComparator comparator) {
		if (this.petrinetState.placeTokensSize() != other.petrinetState.placeTokensSize())
			return false;

		boolean differenceInItemFlag = false;

		Iterator<Integer> thisIt = this.petrinetState.getPlaceTokens();
		Iterator<Integer> otherIt = other.petrinetState.getPlaceTokens();

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

	

	public List<ReachabilityState> getListToOtherState(ReachabilityState other){
		
		return getListToOther(this, other, new ArrayList<ReachabilityState>());
	}

	private List<ReachabilityState> getListToOther(ReachabilityState state, ReachabilityState other, List<ReachabilityState> visited){
		if (visited.contains(state))
			return null;
		
		if (state == other) {
			return visited;
		}
		visited.add(state);
		for (ReachabilityState rs: state.getPredecessors()) {
			List<ReachabilityState> list = getListToOther(rs, other, visited);
			if (list != null)
				return visited;
			visited.remove(rs);
		}
		return null;
	}

}
