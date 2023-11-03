package core;

import java.util.ArrayList;
import java.util.List;

import listeners.ReachabilityStateChangeListener;
import util.IterableMap;

// TODO: Auto-generated Javadoc
/**
 * The Class ReachabilityGraphModel.
 */
public class ReachabilityGraphModel {

	private PetrinetState currentState;

	private String currentEdge = null;

	private PetrinetState invalidState;

	private PetrinetState initialState;

	private IterableMap<String, PetrinetState> petrinetStates;

	private ReachabilityStateChangeListener stateChangeListener;

	/**
	 * Instantiates a new reachability graph model.
	 *
	 * @param petrinet the petrinet
	 */
	public ReachabilityGraphModel(Petrinet petrinet) {

		petrinetStates = new IterableMap<String, PetrinetState>();
		addNewState(petrinet, null);
	}

	/**
	 * Gets the states.
	 *
	 * @return the states
	 */
	public Iterable<PetrinetState> getStates() {
		return petrinetStates;
	}

	/**
	 * Gets the state.
	 *
	 * @param state the state
	 * @return the state
	 */
	public PetrinetState getState(String state) {
		return petrinetStates.get(state);
	}

	/**
	 * Gets the current petrinet state.
	 *
	 * @return the current petrinet state
	 */
	public PetrinetState getCurrentPetrinetState() {
		return currentState;
	}

	/**
	 * Gets the current state.
	 *
	 * @return the current state
	 */
	public PetrinetState getCurrentState() {
		return currentState;
	}

	/**
	 * Sets the current state.
	 *
	 * @param state the new current state
	 */
	public void setCurrentState(PetrinetState state) {
		setNewCurrentState(petrinetStates.get(state.getState()), true); // TODO Why did I get the state out of the
	}
	
	/**
	 * 
	 * @param edge
	 */
	public void setCurrentEdge(String edge) {
		this.currentEdge = edge;
		stateChangeListener.onSetCurrentEdge(edge);
	}

	/**
	 * Adds the new state.
	 *
	 * @param petrinet the petrinet
	 * @param t        the t
	 * @return the added
	 */
	public Added addNewState(Petrinet petrinet, Transition t) {

		if (petrinet == null || !petrinet.hasPlaces())
			return null;

		Added added = Added.NOTHING;

		PetrinetState petrinetState;
		String petrinetStateString = petrinet.getStateString();

		if (petrinetStates.containsKey(petrinetStateString)) {
			petrinetState = petrinetStates.get(petrinetStateString);
		} else {
			added = Added.STATE;
			petrinetState = new PetrinetState(petrinet, currentState == null ? 0 : currentState.getLevel() + 1);
			petrinetStates.put(petrinetStateString, petrinetState);
		}

		if (currentState != null) {

			boolean addedEdgePred = petrinetState.addPredecessor(currentState, t);
			boolean addedEdgeSucc = currentState.addSuccessor(petrinetState, t);

			if (added == Added.NOTHING && (addedEdgePred || addedEdgeSucc))
				added = Added.EDGE;
		}

		if (t == null) {
			initialState = petrinetState;

		}

		if (stateChangeListener != null)
			stateChangeListener.onAdd(petrinetState, currentState, t);

		if (t != null && currentState != null)
			currentEdge = currentState.getState() + petrinetState.getState() + t.getId();

		setNewCurrentState(petrinetState, false);
		checkIfCurrentStateIsBackwardsValid();
		return added;
	}

	private void setNewCurrentState(PetrinetState newState, boolean reset) {
		this.currentState = newState;
		if (stateChangeListener != null)
			stateChangeListener.onSetCurrent(newState, reset);

	}

	/**
	 * Check if current state is backwards valid.
	 *
	 * @return true, if successful
	 */
	boolean checkIfCurrentStateIsBackwardsValid() {

		if (currentState.getPredecessorsSize() == 0)
			return true;

		for (PetrinetState s : currentState.getPredecessors()) {
			PetrinetState state = checkIfStateIsBackwardsValid(s, new ArrayList<PetrinetState>(), currentState);

			if (state != null) {
				invalidState = currentState;
				currentState.setM(state, stateChangeListener);
				return false;
			}
		}
		return true;

	}

	private PetrinetState checkIfStateIsBackwardsValid(PetrinetState state, List<PetrinetState> visitedStates,
			PetrinetState originalState) {

		if (visitedStates.contains(state))
			return null;

		visitedStates.add(state);

		if (originalState.isBiggerThan(state)) {
			return state;
		}

		for (PetrinetState s : state.getPredecessors()) {
			PetrinetState newState = checkIfStateIsBackwardsValid(s, visitedStates, originalState);
			if (newState != null)
				return newState;
		}

		return null;

	}

	/**
	 * Sets the state change listener.
	 *
	 * @param reachabilityStateChangeListener the new state change listener
	 */
	public void setStateChangeListener(ReachabilityStateChangeListener reachabilityStateChangeListener) {
		this.stateChangeListener = reachabilityStateChangeListener;
	}

	/**
	 * Gets the invalid state.
	 *
	 * @return the invalid state
	 */
	public PetrinetState getInvalidState() {
		return invalidState;
	}

	/**
	 * Gets the initial state.
	 *
	 * @return the initial state
	 */
	public PetrinetState getInitialState() {
		return initialState;
	}

	/**
	 * Reset.
	 */
	public void reset() {
		reset(null);
	}

	private void reset(Petrinet petrinet) {

		if (petrinetStates.size() == 0)
			return;

		if (petrinetStates.size() == 1) {
			if (initialState.hasEdges())
				initialState.removeAllPredecessors(stateChangeListener);
			return;
		}

		for (PetrinetState ps : petrinetStates) {
			ps.removeAllPredecessors(stateChangeListener);
			ps.removeAllSuccessors(stateChangeListener);
			if (ps == initialState)
				continue;
			removeStateFromGraph(ps);
		}

		petrinetStates.clear();
		if (petrinet != null) {
			addNewState(petrinet, null);
		} else {
			if (initialState != null) {
				petrinetStates.put(initialState.getState(), initialState);
				setCurrentState(initialState);
			}
		}
	}

	private void removeStateFromGraph(PetrinetState ps) {
		if (stateChangeListener != null)
			stateChangeListener.onRemove(ps);
	}

	/**
	 * Removes the state.
	 *
	 * @param state the state
	 */
	public void removeState(PetrinetState state) {
		if (currentState == state)
			currentState = null;

		if (state.hasEdges()) {
			state.removeAllPredecessors(stateChangeListener);
			if (state != initialState)
				state.removeAllSuccessors(stateChangeListener);
		}
		petrinetStates.remove(state.getState());
		if (stateChangeListener != null)
			stateChangeListener.onRemove(state);
	}

	/**
	 * Sets the initial.
	 */
	public void setInitial() {
		setCurrentState(initialState);
		setCurrentEdge(null);
	}
//	
//	public void print() {
//		System.out.println("STATES: ");
//		for (PetrinetState ps: petrinetStates)
//			ps.print();
//	}

	/**
	 * Removes the edge.
	 *
	 * @param lastState  the last state
	 * @param state      the state
	 * @param transition the transition
	 */
	void removeEdge(PetrinetState lastState, PetrinetState state, Transition transition) {
		lastState.removeSuccessorEdge(state, transition, stateChangeListener);
		state.removePredecessorEdge(lastState, transition, stateChangeListener);

		if ((lastState.getState() + state.getState() + transition.getId()).equals(currentEdge))
			currentEdge = null;
	}

	// TODO REMOVE
	
	/**
	 * Prints the.
	 */
	public void print() {
		for (PetrinetState ps : petrinetStates)
			ps.print();
	}

	/**
	 * 
	 * @return
	 */
	public String getCurrentEdge() {
		return currentEdge;
	}

}
