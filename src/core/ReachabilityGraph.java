package core;

import java.util.ArrayList;
import java.util.List;

import listeners.PetrinetStateChangedListener;
import listeners.ReachabilityStateChangedListener;
import listeners.ToolbarChangedListener;
import util.IterableMap;

/**
 * <p>
 * Class representing a reachability graph.
 * </p>
 * 
 * <p>
 * A reachability graph basically consists of the states a petrinet can be in
 * and edges between those states being represented by transitions being fired.
 * While the graph itself is being represented by the net of
 * {@link PetrinetState}s this class holds information about the current state,
 * current edge, validity of the petrinet (concerning its boundedness) and
 * methods for adding and removing states. Additionally it provides a
 * {@link ReachabilityStateChangedListener} communicating changes to the graph
 * and a {@link ReachabilityGraphUndoQueue} recording steps. Changes can be
 * stopped to be pushed on the queue or marked as skippable.
 * </p>
 */
public class ReachabilityGraph {

	/** The petrinet this reachability graph is linked to. */
	private Petrinet petrinet;

	/** The currently active state in the reachability graph. */
	private PetrinetState currentState;

	/** The edge currently being active in the reachability graph. */
	private String currentEdge = null;

	/** State marking unboundedness (m'). */
	private PetrinetState invalidState;

	/** The initial petrinet state. */
	private PetrinetState initialState;

	/** Map of all petrinet states in the reachability graph. */
	private IterableMap<String, PetrinetState> petrinetStates;

	/** State change listener. */
	private ReachabilityStateChangedListener stateChangeListener;

	/** Queue recording changes made to the reachability graph. */
	private ReachabilityGraphUndoQueue undoQueue;

	/**
	 * If skippable mode is on all steps in the undo queue are flagged as skippable
	 * meaning they are skipped when un-/redoing steps.
	 */
	private boolean skippableMode = false;

	/** Only if this flag is set steps are pushed on to the undoQueue. */
	private boolean pushing = true;

	/**
	 * Instantiates a new reachability graph model.
	 *
	 * @param petrinet              the petrinet being linked to the reachability
	 *                              graph
	 * @param toolbarToggleListener the toolbar listener for un-/redo buttons
	 */
	public ReachabilityGraph(Petrinet petrinet, ToolbarChangedListener toolbarToggleListener) {

		this.petrinet = petrinet;

		undoQueue = new ReachabilityGraphUndoQueue(this, toolbarToggleListener);
		petrinetStates = new IterableMap<String, PetrinetState>();

		// add initial state
		addNewState(petrinet, null);

		// link the reachability graph to the petrinet state change listener
		petrinet.setPetrinetChangeListener(new PetrinetStateChangedListener() {

			@Override
			public void onTransitionFire(Transition t) {
				addNewState(petrinet, t);
			}

			@Override
			public void onPetrinetChanged(Petrinet petrinet) {

				undoQueue = new ReachabilityGraphUndoQueue(ReachabilityGraph.this, toolbarToggleListener);

				makeEmpty();
				if (initialState != null) {
					removeState(initialState);
					stateChangeListener.onRemove(initialState);
				}
				initialState = null;
				currentState = null;
				currentEdge = null;
				addNewState(petrinet, null);
				toolbarToggleListener.resetUndoRedoButtons();
			}

		});

	}

	/**
	 * Gets the states currently in the reachability graph.
	 *
	 * @return the states in the reachability graph
	 */
	public Iterable<PetrinetState> getStates() {
		return petrinetStates;
	}

	/**
	 * Gets the state represented by the string.
	 *
	 * @param state the state to get
	 * @return the state represented by the string
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
	 * Sets the current state.
	 *
	 * @param petrinetState the new current state
	 */
	public void setCurrentState(PetrinetState petrinetState) {

		setNewCurrentState(petrinetStates.get(petrinetState.getState()));
		// current edge needs to be reset
		setCurrentEdge(null);
		if (stateChangeListener != null)
			stateChangeListener.onResetCurrentEdge();

		// push change on to the undo queue
		if (pushing)
			undoQueue.push(currentState, currentEdge, AddedType.NOTHING, null, skippableMode);
	}

	/**
	 * Sets the current edge.
	 * 
	 * @param edge the edge to set active
	 */
	public void setCurrentEdge(String edge) {
		this.currentEdge = edge;
		if (stateChangeListener != null)
			stateChangeListener.onSetCurrentEdge(edge);
	}

	/**
	 * Adds a new state.
	 *
	 * @param petrinet   the petrinet for which to add the current state from
	 * @param transition the transition that has been fired
	 * @return the state that has been added (null if non has been added)
	 */
	PetrinetState addNewState(Petrinet petrinet, Transition transition) {

		// if there is no petrinet or the petrinet does not have places return
		if (petrinet == null || !petrinet.hasPlaces())
			return null;

		// by default assume nothing has been added
		AddedType added = AddedType.NOTHING;

		// get the current state of the petrinet as string
		String petrinetStateString = petrinet.getStateString();

		// the petrinet state to be added
		PetrinetState petrinetState;

		// if the reachability graph already contains the state get it, add it otherwise
		if (petrinetStates.containsKey(petrinetStateString))
			petrinetState = petrinetStates.get(petrinetStateString);
		else {
			// update added
			added = AddedType.STATE;
			// create a new petrinet state and add it to the map of states (the level of
			// steps is the current level incremented, 0 if the current state is null)
			petrinetState = new PetrinetState(petrinet, currentState == null ? 0 : currentState.getLevel() + 1);
			petrinetStates.put(petrinetStateString, petrinetState);
		}

		// if current state exists add edges to and from predecessor
		if (currentState != null) {

			boolean addedEdgeSucc = currentState.addSuccessor(petrinetState, transition);

			// update added if edges have been added (false if they already existed)
			if (added == AddedType.NOTHING && (addedEdgeSucc))
				added = AddedType.EDGE;
		}

		// if the initial state does not exist, this is the initial state
		if (initialState == null)
			initialState = petrinetState;

		// inform the listener
		if (stateChangeListener != null)
			stateChangeListener.onAdd(petrinetState, currentState, transition);

		// update current edge
		if (transition != null && currentState != null)
			currentEdge = currentState.getState() + petrinetState.getState() + transition.getId();

		// set the current state
		setNewCurrentState(petrinetState);

		// check if current state is on path signifying unboundedness
		if (invalidState == null)
			checkIfCurrentStateIsBounded();

		// push onto undo queue
		if (pushing)
			undoQueue.push(currentState, currentEdge, added, transition, skippableMode);

		return petrinetState;

	}

	// set the current state and inform the listener
	private void setNewCurrentState(PetrinetState newState) {
		this.currentState = newState;
		if (stateChangeListener != null)
			stateChangeListener.onSetCurrent(newState);

	}

	/**
	 * Checks if current state is bounded by backwards checking whether any state is
	 * smaller than the given state (see PetrinetState).
	 *
	 * @return true, if state is bounded
	 */
	boolean checkIfCurrentStateIsBounded() {

		// for every predecessor state call check function
		for (PetrinetState s : currentState.getPredecessors()) {

			// get path that is beginning of path marking this state as unbounded (returns
			// m)
			PetrinetState state = checkIfStateIsBounded(s, new ArrayList<PetrinetState>(), currentState);

			// if state exists set invalid state and inform listener
			if (state != null) {

				invalidState = currentState;
				currentState.setM(state);

				if (stateChangeListener != null)
					stateChangeListener.onResetPath();

				List<PetrinetState> pathMMarked = currentState.getPathFromOtherState(state);

				List<PetrinetState> pathM = state.getPathFromOtherState(initialState);

				if (stateChangeListener != null) {
					if (pathMMarked != null)
						for (PetrinetState ps : pathMMarked)
							stateChangeListener.onAddToPath(ps);
					if (pathM != null)
						for (PetrinetState ps : pathM)
							stateChangeListener.onAddToPath(ps);
				}

				if (stateChangeListener != null)
					stateChangeListener.onMarkUnboundedPath(state, currentState);

				return false;
			}
		}
		return true;

	}

	// recursively backwards checks for state that marks the original state as
	// unbouded
	private PetrinetState checkIfStateIsBounded(PetrinetState state, List<PetrinetState> visitedStates,
			PetrinetState originalState) {

		// base case
		if (visitedStates.contains(state))
			return null;

		// keep track of visited states
		visitedStates.add(state);

		// compare states
		if (originalState.isBiggerThan(state)) {
			return state;
		}
		// recursively check all predecessors
		for (PetrinetState s : state.getPredecessors()) {

			PetrinetState newState = checkIfStateIsBounded(s, visitedStates, originalState);

			// if state has been found return state
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
	public void setStateChangeListener(ReachabilityStateChangedListener reachabilityStateChangeListener) {
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
	 * Resets the reachability graph.
	 */
	public void reset() {
		// reset undo queue and buttons in the toolbar
		if (undoQueue != null)
			undoQueue.reset();

		makeEmpty();

		if (initialState != null) {
			petrinetStates.put(initialState.getState(), initialState);
			pushing = false;
			setCurrentState(initialState);
			pushing = true;
		}

	}

	// empty all states and edges except for initial state
	private void makeEmpty() {
		// if there are not states return
		if (petrinetStates.size() == 0)
			return;

		// if only one state exists check for edges and return
		if (petrinetStates.size() == 1) {
			if (initialState.hasEdges())
				initialState.removeAllPredecessors(stateChangeListener);
			return;
		}

		// remove edges of every petrinet state -> if we remove edges first and then
		// clear the map we can simply iterate over the map
		for (PetrinetState ps : petrinetStates) {
			// remove edges to predecessors
			ps.removeAllPredecessors(stateChangeListener);
			// remove edges to successors
			ps.removeAllSuccessors(stateChangeListener);
			// if state is initial state continue -> when initial state is not removed the
			// GUI doesn't have to worry about marking it
			if (ps == initialState)
				continue;
			// inform listener that state has been removed
			if (stateChangeListener != null)
				stateChangeListener.onRemove(ps);

		}

		// clear all states and reset the initial state
		petrinetStates.clear();
	}

	/**
	 * Removes state from reachability graph.
	 *
	 * @param state the state to be removed
	 */
	void removeState(PetrinetState state) {

		// safety measure
		if (state == null)
			return;

		// handle state being current state
		if (currentState == state)
			currentState = null;

		// reset invalid state if it is identical to state
		if (invalidState == state)
			invalidState = null;

		// remove edges
		if (state.hasEdges()) {
			state.removeAllPredecessors(stateChangeListener);
			state.removeAllSuccessors(stateChangeListener);
		}

		// remove from map
		petrinetStates.remove(state.getState());
		if (stateChangeListener != null)
			stateChangeListener.onRemove(state);
	}

	/**
	 * Sets the reachability graph to the initial state.
	 */
	public void setInitial() {
		setCurrentState(initialState);
		setCurrentEdge(null);
	}

	/**
	 * Removes an edge.
	 *
	 * @param source     the source state of the edge
	 * @param target     the target state of the edge
	 * @param transition the transition that has been fired
	 */
	void removeEdge(PetrinetState source, PetrinetState target, Transition transition) {
		target.removePredecessorEdge(source, transition, stateChangeListener);

		if ((source.getState() + target.getState() + transition.getId()).equals(currentEdge))
			currentEdge = null;
	}

	/**
	 * Gets current edge.
	 * 
	 * @return the current edge
	 */
	public String getCurrentEdge() {
		return currentEdge;
	}

	/**
	 * 
	 * Gets the petrinet associated with the reachability graph.
	 * 
	 * @return the petrinet
	 */
	public Petrinet getPetrinet() {
		return petrinet;
	}

	/**
	 * 
	 * The undo queue.
	 * 
	 * @return the undo queue
	 */
	public ReachabilityGraphUndoQueue getUndoQueue() {
		return undoQueue;
	}

	/**
	 * Sets the undo queue.
	 * 
	 * @param undoQueue the queue to be set
	 */
	public void setPetrinetQueue(ReachabilityGraphUndoQueue undoQueue) {
		this.undoQueue = undoQueue;
	}

	/**
	 * If set true all changes made to the reachability graph are flagged as
	 * skippable an will be skipped when re-/undoing changes. False by default.
	 * 
	 * @param skippableMode if true, added steps are flagged skippable in the undo
	 *                      queue
	 */
	public void setSkippableMode(boolean skippableMode) {
		this.skippableMode = skippableMode;
	}

	/**
	 * When set to true all changes are pushed onto the undo queue. True by default.
	 * 
	 * @param pushing if true, changes will be pushed to the undo queue
	 */
	public void setPushing(boolean pushing) {
		this.pushing = pushing;

	}

}
