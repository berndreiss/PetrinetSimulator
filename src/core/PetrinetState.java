package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import listeners.ReachabilityStateChangedListener;
import util.IterableMap;

/**
 * <p>
 * Class representing the state of a petrinet.
 * </p>
 * 
 * <p>
 * The state of a petrinet is represented by the marking of its places.
 * Additionally this class contains the predecessor and successor states
 * (including transition leading from / to those states) for the given instance
 * and information about whether this state lies on the end of a path proving
 * the petrinet aís unbounded (the beginning path being saved as 'm').
 * </p>
 */
public class PetrinetState {

	/** The given state of the petrinet as a string. */
	private String state;
	/** The places and their tokens represented by Integers. */
	private List<Integer> placeTokens = new ArrayList<Integer>();

	/**
	 * First state on the path proving petrinet is unbounded. Null if non has been
	 * found.
	 */
	private PetrinetState m;

	/** All direct predecessors leading to this state. */
	private IterableMap<String, PetrinetState> predecessors = new IterableMap<String, PetrinetState>();
	/** All direct successors reachable from this state. */
	private IterableMap<String, PetrinetState> successors = new IterableMap<String, PetrinetState>();
	/**
	 * All transitions leading to and from this state (key being
	 * predecessor+successor).
	 */
	private HashMap<String, List<Transition>> transitionMap = new HashMap<String, List<Transition>>();
	/**
	 * Steps being taken from an initial state -> initial state being defined as a
	 * state without predecessors. Needed for the tree layout.
	 */
	private int level;

	/**
	 * Instantiates a new petrinet state.
	 *
	 * @param petrinet              The petrinet for which to save the current
	 *                              state.
	 * @param stepsFromInitialState Number of steps being taken from initial state.
	 */
	PetrinetState(Petrinet petrinet, int stepsFromInitialState) {

		// get the tokens on all places
		for (Place p : petrinet.getPlaces())
			placeTokens.add(p.getNumberOfTokens());

		// get state as string
		this.state = petrinet.getStateString();
		this.level = stepsFromInitialState;
	}

	/**
	 * Get the state as string.
	 *
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * Get the place tokens.
	 *
	 * @return the place tokens
	 */
	public Iterator<Integer> getPlaceTokens() {
		return placeTokens.iterator();
	}

	/**
	 * Get the number of places.
	 *
	 * @return the number of places
	 */
	int numberOfPlaces() {
		return placeTokens.size();
	}

	/**
	 * Get the predecessors.
	 *
	 * @return the predecessors
	 */
	public Iterable<PetrinetState> getPredecessors() {
		return predecessors;
	}

	/**
	 * Get the successors.
	 *
	 * @return the successors
	 */
	public Iterable<PetrinetState> getSuccessors() {
		return successors;
	}

	/**
	 * Get the number of steps being taken from the initial state.
	 *
	 * @return number of steps from the initial state
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Add a new successor.
	 *
	 * @param newSuccessor The new successor to be added.
	 * @param transition   The transition being fired to get to the successor.
	 * @return true, if successor has been added, false if transition already
	 *         existed for successor
	 */
	public boolean addSuccessor(PetrinetState newSuccessor, Transition transition) {

		if (newSuccessor == null)
			return false;

		boolean added = false;

		// add successor to map
		successors.put(newSuccessor.getState(), newSuccessor);

		// get the string for the transition map
		String mapString = this.getState() + newSuccessor.getState();

		// get the transition list for the successor
		List<Transition> transitionList = transitionMap.get(mapString);

		// if it does not exist, create it
		if (transitionList == null) {
			transitionList = new ArrayList<Transition>();
			transitionMap.put(mapString, transitionList);

		}

		// if the list does not contain the transition, add it
		if (!transitionList.contains(transition)) {
			transitionList.add(transition);
			added = true;
		}

		// synchronize successor
		newSuccessor.addPredecessor(this, transition);

		return added;
	}

	/**
	 * Gets the first transition for a given successor.
	 *
	 * @param successor The successor for which to return a transition.
	 * @return the first transition for successor
	 */
	public Transition getFirstSuccessorTransition(PetrinetState successor) {
		if (!successors.containsKey(successor.getState()))
			return null;
		// return the first transition
		for (Transition t : transitionMap.get(this.getState() + successor.getState()))
			return t;
		return null;
	}

	/**
	 * Add a new predecessor.
	 *
	 * @param newPredecessor The new predecessor to be added.
	 * @param transition     The transition being fired to get to the predecessor.
	 * @return true, if predecessor has been added, false if transition already
	 *         existed for predecessor
	 */
	private boolean addPredecessor(PetrinetState newPredecessor, Transition transition) {

		if (newPredecessor == null)
			return false;

		boolean added = false;

		// add successor to map
		predecessors.put(newPredecessor.getState(), newPredecessor);

		// get the string for the transition map
		String mapString = newPredecessor.getState() + this.getState();

		// get the transition list for the predecessor
		List<Transition> transitionList = transitionMap.get(mapString);

		// if it does not exist, create it
		if (transitionList == null) {
			transitionList = new ArrayList<Transition>();
			transitionMap.put(mapString, transitionList);
		}
		// if the list does not contain the transition, add it
		if (!transitionList.contains(transition)) {
			transitionList.add(transition);
			added = true;
		}

		return added;
	}

	/**
	 * Set m -> state marking the start of a path proving that petrinet is unbound.
	 *
	 * @param m                   The start of the path.
	 * @param stateChangeListener the state change listener
	 */
	void setM(PetrinetState m) {
		this.m = m;
	}

	/**
	 * Gets the m state marking the start of a path proving that petrinet is
	 * unbound.
	 *
	 * @return the m
	 */
	public PetrinetState getM() {
		return m;
	}

	/**
	 * Checks state if is bigger than other -> if any token on a place in the given
	 * state is bigger than in the other while all other tokens being at least the
	 * same, return true.
	 *
	 * @param other The other state to compare to
	 * @return true, if all tokens are at least the same and at least one is bigger
	 */
	boolean isBiggerThan(PetrinetState other) {
		// comparator looks at tokens of places of this state and compares it to tokens
		// of the other state -> if tokens of this is smaller, return false
		return compareStates(other, (thisToken, otherToken) -> thisToken < otherToken);
	}

	// see description in isBiggerThan() -> comparator being used to be able to also
	// implement isSmallerThan()
	private boolean compareStates(PetrinetState other, ReachabilityComparator comparator) {
		// the two states do not represent the same petrinet -> abort
		if (numberOfPlaces() != other.numberOfPlaces())
			return false;

		// flag marking if there have been differences
		boolean differenceInItemFlag = false;

		// iterator for places in both petrinets -> guaranteed to be the same size, see
		// above, furthermore it is also guaranteed, that the places are always in the
		// same order, since they are ordered by name (see class Petrinet)
		Iterator<Integer> thisIt = this.getPlaceTokens();
		Iterator<Integer> otherIt = other.getPlaceTokens();

		// compare the two states place by place
		while (thisIt.hasNext()) {
			int thisPlaceTokens = thisIt.next();
			int otherPlaceTokens = otherIt.next();

			// compare the two places if comparison yields true (see ReachabilityComparator)
			if (comparator.compare(thisPlaceTokens, otherPlaceTokens))
				return false;

			// mark flag for difference if the two tokens are not the same
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
	 * Gets the path from other state if it exists (excluding this and the other
	 * state), null otherwise.
	 *
	 * @param other The other path from which to return path.
	 * @return the path from other state
	 */
	public List<PetrinetState> getPathFromOtherState(PetrinetState other) {

		List<List<PetrinetState>> paths = new ArrayList<List<PetrinetState>>();

		for (PetrinetState rs : this.getPredecessors()) {
			paths.add(getPathFromOther(rs, other, new HashSet<PetrinetState>(), new ArrayList<PetrinetState>()));
		}

		List<PetrinetState> list = null;

		for (List<PetrinetState> l : paths) {
			if (l == null)
				continue;
			
			if (list == null) {
				list = l;
				continue;
			}
			
			if (l.size() < list.size())
				list = l;
		}

		// return empty list if it is null, the list otherwise
		return list == null ? new ArrayList<PetrinetState>() : list;
	}

	// crawls backward recursively through list of all predecessors and returns the
	// path from the other state if path exists -> the path is handed down by the
	// function and states are added step by step
	private List<PetrinetState> getPathFromOther(PetrinetState state, PetrinetState other, Set<PetrinetState> visited,
			ArrayList<PetrinetState> path) {

		// base case
		if (visited.contains(state))
			return null;

		// other state has been found
		if (state == other)
			return path;

		// add state to visited
		if (state != this)
			visited.add(state);

		// crawl through all predecessors of the given state
		for (PetrinetState rs : state.getPredecessors()) {
			// get path to predecessor as list (states will be added to path)
			List<PetrinetState> list = getPathFromOther(rs, other, visited, path);

			// if list is not null see if we are on a circle (if path already contains given
			// state)
			if (list != null) {

				// handle circles
				if (path.contains(state)) {// circle!
					path.clear();
					return path;
				}
				// add given state to path and return it
				path.add(state);
				return path;
			}
		}
		return null;
	}

	/**
	 * Check for edges.
	 *
	 * @return true, if state has edges
	 */
	boolean hasEdges() {
		return transitionMap.size() > 0;
	}

	/**
	 * Removes a predecessor.
	 *
	 * @param predecessor         The predecessor state to be removed.
	 * @param stateChangeListener the state change listener listening for the
	 *                            removal of transitions
	 */
	private void removePredecessor(PetrinetState predecessor, ReachabilityStateChangedListener stateChangeListener) {
		if (!predecessors.containsKey(predecessor.getState()))
			return;

		List<Transition> transitions = transitionMap.get(predecessor.getState() + getState());

		if (transitions == null)// case when PetrinetState was its own successor/predecessor
			return;

		// remove all transitions
		for (Transition t : transitions)
			if (stateChangeListener != null)
				stateChangeListener.onRemoveEdge(predecessor, this, t);

		// remove the entry in the map
		transitionMap.remove(predecessor.getState() + getState());

		// remove from predecessors
		predecessors.remove(predecessor.getState());

		// synchronize
		predecessor.removeSuccessor(this, null);

	}

	/**
	 * Removes a successor.
	 *
	 * @param successor           The successor state to be removed.
	 * @param stateChangeListener the state change listener listening for the
	 *                            removal of transitions
	 */
	public void removeSuccessor(PetrinetState successor, ReachabilityStateChangedListener stateChangeListener) {

		if (!successors.containsKey(successor.getState()))
			return;

		List<Transition> transitions = transitionMap.get(getState() + successor.getState());

		if (transitions == null)// case when PetrinetState was its own successor/predecessor
			return;

		// remove all transitions
		for (Transition t : transitions)
			if (stateChangeListener != null)
				stateChangeListener.onRemoveEdge(this, successor, t);

		// remove entry from map
		transitionMap.remove(getState() + successor.getState());

		// remove from successors
		successors.remove(successor.getState());

		// synchronize
		successor.removePredecessor(this, null);

	}

	/**
	 * Removes a predecessor edge.
	 *
	 * @param predecessor         The predecessor of the edge.
	 * @param transition          The transition of the edge.
	 * @param stateChangeListener the state change listener listening for edge
	 *                            removals
	 */
	void removePredecessorEdge(PetrinetState predecessor, Transition transition,
			ReachabilityStateChangedListener stateChangeListener) {
		if (stateChangeListener != null)// only update graph on predecessor edge removal -> otherwise edge would be
										// removed two times and graphstream throws exception
			stateChangeListener.onRemoveEdge(predecessor, this, transition);

		List<Transition> transitions = transitionMap.get(predecessor.getState() + getState());

		if (transitions == null)// could be the case if there is an edge from a state to itself and edge has
								// been removed
			return;
		transitions.remove(transition);

		if (transitions.size() == 0)
			transitionMap.remove(predecessor.getState() + getState());
		predecessor.removeSuccessorEdge(this, transition);
	}

	// removes the successor edge.
	private void removeSuccessorEdge(PetrinetState successor, Transition transition) {
		List<Transition> transitions = transitionMap.get(getState() + successor.getState());

		if (transitions == null)// could be the case if there is an edge from a state to itself and edge has
								// been removed
			return;

		transitions.remove(transition);

		if (transitions.size() == 0)
			transitionMap.remove(getState() + successor.getState());
	}

	/**
	 * Get number of predecessors.
	 *
	 * @return the predecessors size
	 */
	public int getPredecessorsSize() {
		return predecessors.size();
	}

	/**
	 * Get number of successors.
	 *
	 * @return the successors size
	 */
	public int getSuccessorsSize() {
		return successors.size();
	}

	/**
	 * Remove all predecessors.
	 *
	 * @param stateChangeListener the state change listener
	 */
	public void removeAllPredecessors(ReachabilityStateChangedListener stateChangeListener) {

		// temporary list for strings since while iterating items can't be remove
		ArrayList<String> predecessorStrings = new ArrayList<String>();

		for (String s : predecessors.keySet())
			predecessorStrings.add(s);

		for (String s : predecessorStrings)
			removePredecessor(predecessors.get(s), stateChangeListener);

	}

	/**
	 * Remove all successors.
	 *
	 * @param stateChangeListener the state change listener
	 */
	public void removeAllSuccessors(ReachabilityStateChangedListener stateChangeListener) {

		// temporary list for strings since while iterating items can't be remove
		ArrayList<String> successorStrings = new ArrayList<String>();

		for (String s : successors.keySet())
			successorStrings.add(s);

		for (String s : successorStrings)
			removeSuccessor(successors.get(s), stateChangeListener);

	}

	/**
	 * Get transitions one can fire in this state to get to the successor state.
	 * 
	 * @param successor The target state for which transitions should be returned.
	 * @return transitions leading to successor state
	 */

	public List<Transition> getTransitions(PetrinetState successor) {
		return transitionMap.get(this.getState() + successor.getState());
	}

}
