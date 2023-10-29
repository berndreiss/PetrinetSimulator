package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import listeners.ReachabilityStateChangeListener;
import util.IterableMap;

// TODO: Auto-generated Javadoc
/**
 * The Class PetrinetState.
 */
public class PetrinetState {

	private String state;
	private List<Integer> placeTokens;

	private PetrinetState m;

	private IterableMap<String, PetrinetState> predecessors;
	private IterableMap<String, PetrinetState> successors;

	private HashMap<String, List<Transition>> transitionMap;

	private int level;

	/**
	 * Instantiates a new petrinet state.
	 *
	 * @param petrinet the petrinet
	 * @param level the level
	 */
	PetrinetState(Petrinet petrinet, int level) {

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

	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * Gets the place tokens.
	 *
	 * @return the place tokens
	 */
	public Iterator<Integer> getPlaceTokens() {
		return placeTokens.iterator();
	}

	/**
	 * Place tokens size.
	 *
	 * @return the int
	 */
	int placeTokensSize() {
		return placeTokens.size();
	}

	/**
	 * Gets the predecessors.
	 *
	 * @return the predecessors
	 */
	public Iterable<PetrinetState> getPredecessors() {
		return predecessors;
	}

	/**
	 * Gets the successors.
	 *
	 * @return the successors
	 */
	public Iterable<PetrinetState> getSuccessors() {
		return successors;
	}

	/**
	 * Gets the level.
	 *
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Adds the successor.
	 *
	 * @param newSuccessor the new successor
	 * @param t the t
	 * @return true, if successful
	 */
	boolean addSuccessor(PetrinetState newSuccessor, Transition t) {

		boolean added = false;

		successors.put(newSuccessor.getState(), newSuccessor);
		String mapString = this.getState() + newSuccessor.getState();
		if (!transitionMap.containsKey(mapString)) {
			transitionMap.put(mapString, new ArrayList<Transition>());
			added = true;
		}
		List<Transition> transitionList = transitionMap.get(mapString);
		if (!transitionList.contains(t)) {
			transitionList.add(t);
			added = true;
		}
		return added;
	}

	

	/**
	 * Gets the first successor transition.
	 *
	 * @param successor the successor
	 * @return the first successor transition
	 */
	Transition getFirstSuccessorTransition(PetrinetState successor) {
		if (!successors.containsKey(successor.getState()))
			return null;
		for (Transition t : transitionMap.get(this.getState() + successor.getState()))
			return t;
		return null;
	}

	/**
	 * Adds the predecessor.
	 *
	 * @param newPredecessor the new predecessor
	 * @param t the t
	 * @return true, if successful
	 */
	boolean addPredecessor(PetrinetState newPredecessor, Transition t) {

		if (newPredecessor == null)
			return false;
		boolean added = false;

		predecessors.put(newPredecessor.getState(), newPredecessor);
		String mapString = newPredecessor.getState() + this.getState();
		if (!transitionMap.containsKey(mapString)) {
			transitionMap.put(mapString, new ArrayList<Transition>());
			added = true;

		}
		List<Transition> transitionList = transitionMap.get(mapString);
		if (!transitionList.contains(t)) {
			transitionList.add(t);
			added = true;
		}
		return added;
	}

	

	/**
	 * Sets the M.
	 *
	 * @param m the m
	 * @param stateChangeListener the state change listener
	 */
	void setM(PetrinetState m, ReachabilityStateChangeListener stateChangeListener) {
		this.m = m;
		if (stateChangeListener != null)
			stateChangeListener.onMarkInvalid(m, this);
	}

	/**
	 * Gets the m.
	 *
	 * @return the m
	 */
	public PetrinetState getM() {
		return m;
	}

	/**
	 * Checks if is bigger than.
	 *
	 * @param other the other
	 * @return true, if is bigger than
	 */
	boolean isBiggerThan(PetrinetState other) {

		return compareStates(other, (i, j) -> i < j);
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

	/**
	 * Gets the path to other state.
	 *
	 * @param other the other
	 * @return the path to other state
	 */
	List<PetrinetState> getPathToOtherState(PetrinetState other) {

		List<PetrinetState> list = getPathToOther(this, other, new HashSet<PetrinetState>(),
				new ArrayList<PetrinetState>());
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

				if (path.contains(state)) {// circle!
					path.clear();
					return path;
				}
				path.add(state);
				return path;
			}
		}
		return null;
	}

	/**
	 * Checks for edges.
	 *
	 * @return true, if successful
	 */
	boolean hasEdges() {
		return transitionMap.size() > 0;
	}

	/**
	 * Removes the predecessor.
	 *
	 * @param petrinetState the petrinet state
	 * @param stateChangeListener the state change listener
	 */
	private void removePredecessor(PetrinetState petrinetState, ReachabilityStateChangeListener stateChangeListener) {
		if (!predecessors.containsKey(petrinetState.getState()))
			return;

		List<Transition> transitions = transitionMap.get(petrinetState.getState() + getState());

		if (transitions == null)// case when PetrinetState was its own successor/predecessor
			return;

		for (Transition t : transitions) {
			if (stateChangeListener != null)
				stateChangeListener.onRemoveEdge(petrinetState, this, t);
		}
		transitionMap.remove(petrinetState.getState() + getState());

		predecessors.remove(petrinetState.getState());

		petrinetState.removeSuccessor(this, null);

	}

	/**
	 * Removes the successor.
	 *
	 * @param petrinetState the petrinet state
	 * @param stateChangeListener the state change listener
	 */
	private void removeSuccessor(PetrinetState petrinetState, ReachabilityStateChangeListener stateChangeListener) {

		if (!successors.containsKey(petrinetState.getState()))
			return;

		List<Transition> transitions = transitionMap.get(getState() + petrinetState.getState());

		if (transitions == null)// case when PetrinetState was its own successor/predecessor
			return;

		for (Transition t : transitions) {
			if (stateChangeListener != null)
				stateChangeListener.onRemoveEdge(this, petrinetState, t);
		}
		transitionMap.remove(getState() + petrinetState.getState());

		successors.remove(petrinetState.getState());

		petrinetState.removePredecessor(this, null);

	}

	/**
	 * Removes the predecessor edge.
	 *
	 * @param predecessor the predecessor
	 * @param transition the transition
	 * @param stateChangeListener the state change listener
	 */
	void removePredecessorEdge(PetrinetState predecessor, Transition transition,
			ReachabilityStateChangeListener stateChangeListener) {
		if (stateChangeListener != null)// only update graph on predecessor edge removal -> otherwise edge would be
										// removed two times and graphstream throws exception
			stateChangeListener.onRemoveEdge(predecessor, this, transition);

		List<Transition> transitions = transitionMap.get(predecessor.getState() + getState());

		if (transitions == null)//could be the case if there is an edge from a state to itself and edge has been removed
			return;
			transitions.remove(transition);

		if (transitions.size() == 0)
			transitionMap.remove(predecessor.getState() + getState());
	}

	/**
	 * Removes the successor edge.
	 *
	 * @param successor the successor
	 * @param transition the transition
	 * @param stateChangeListener the state change listener
	 */
	void removeSuccessorEdge(PetrinetState successor, Transition transition,
			ReachabilityStateChangeListener stateChangeListener) {

		List<Transition> transitions = transitionMap.get(getState() + successor.getState());

		if (transitions == null)//could be the case if there is an edge from a state to itself and edge has been removed
			return;

		transitions.remove(transition);

		if (transitions.size() == 0)
			transitionMap.remove(getState() + successor.getState());
	}

	/**
	 * Gets the predecessors size.
	 *
	 * @return the predecessors size
	 */
	public int getPredecessorsSize() {
		return predecessors.size();
	}

	/**
	 * Gets the successors size.
	 *
	 * @return the successors size
	 */
	public int getSuccessorsSize() {
		return successors.size();
	}

	/**
	 * Removes the all predecessors.
	 *
	 * @param stateChangeListener the state change listener
	 */
	void removeAllPredecessors(ReachabilityStateChangeListener stateChangeListener) {
		ArrayList<String> predecessorStrings = new ArrayList<String>();

		for (String s : predecessors.keySet())
			predecessorStrings.add(s);

		for (String s : predecessorStrings)
			removePredecessor(predecessors.get(s), stateChangeListener);

	}

	/**
	 * Removes the all successors.
	 *
	 * @param stateChangeListener the state change listener
	 */
	void removeAllSuccessors(ReachabilityStateChangeListener stateChangeListener) {
		ArrayList<String> successorStrings = new ArrayList<String>();

		for (String s : successors.keySet())
			successorStrings.add(s);

		for (String s : successorStrings)
			removeSuccessor(successors.get(s), stateChangeListener);

	}

	//TODO REMOVE
	/**
	 * Prints the.
	 */
	void print() {
		System.out.println("STATE: " + getState());
		System.out.println("PREDECESSORS: ");
		for (PetrinetState ps : predecessors)
			System.out.println(ps.getState());

		System.out.println("SUCCESSORS:");
		for (PetrinetState ps : successors)
			System.out.println(ps.getState());

		System.out.println("TRANSITION MAP:");

		for (String s : transitionMap.keySet())
			for (Transition t : transitionMap.get(s))
				System.out.println(s + t.getId());

	}
}
