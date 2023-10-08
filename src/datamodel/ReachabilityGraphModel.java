package datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import control.PetrinetController;
import util.IterableHashMap;

public class ReachabilityGraphModel {

	private ReachabilityState currentState;

	private ReachabilityState invalidState;
	
	private ReachabilityState initialState;

	private IterableHashMap<String, ReachabilityState> reachabilityStates;

	private ReachabilityStateChangeListener reachabilityStateChangeListener;

	public ReachabilityGraphModel(PetrinetController controller) {

		reachabilityStates = new IterableHashMap<String, ReachabilityState>();
		initialState = addNewState(controller.getPetrinet().getState(), null);
	}

	public Iterable<ReachabilityState> getStates() {
		return reachabilityStates;
	}

	public ReachabilityState getState(String state) {
		return reachabilityStates.get(state);
	}

	public PetrinetState getCurrentPetrinetState() {
		return currentState.getPetrinetState();
	}

	public ReachabilityState getCurrentState() {
		return currentState;
	}

	public void setCurrentState(PetrinetState state) {
		setNewCurrentState(reachabilityStates.get(state.getState()));
	}

	public ReachabilityState addNewState(PetrinetState petrinetState, Transition t) {

		String newStateString = petrinetState.getState();
		ReachabilityState newState;
		if (reachabilityStates.containsKey(newStateString)) {
			newState = reachabilityStates.get(newStateString);
			newState.addPredecessor(currentState, t);
		} else {
			newState = new ReachabilityState(petrinetState, currentState);
			reachabilityStates.put(newStateString, newState);
		}
		if (currentState != null)
			currentState.addSuccessor(newState, t);

		setNewCurrentState(newState);
		if (reachabilityStateChangeListener != null)
			reachabilityStateChangeListener.onAdd(newState);
		return currentState;
	}

	private void setNewCurrentState(ReachabilityState newState) {
		this.currentState = newState;
		if (reachabilityStateChangeListener != null)
			reachabilityStateChangeListener.onChange(newState);

	}

	public void checkIfCurrentStateIsBackwardsValid() {

		if (currentState.getPredecessors().size() == 0)
			return;

		for (ReachabilityState s : currentState.getPredecessors()) {
			ReachabilityState state = checkIfStateIsBackwardsValid(s, new ArrayList<ReachabilityState>(),
					currentState);

			if (state != null) {
				invalidState = currentState;
				currentState.setM(state);
				return;
			}
		}

	}

	private ReachabilityState checkIfStateIsBackwardsValid(ReachabilityState state,
			List<ReachabilityState> visitedStates, ReachabilityState originalState) {

		
		if (visitedStates.contains(state))
			return null;

		visitedStates.add(state);

		if (originalState.isBiggerThan(state)) {
			return state;
		}

		for (ReachabilityState s : state.getPredecessors()) {
			ReachabilityState newState = checkIfStateIsBackwardsValid(s, visitedStates, originalState);
			if (newState != null)
				return newState;
		}

		return null;

	}

	public boolean hasState(String id) {
		return reachabilityStates.containsKey(id);
	}

	public void setReachabilityStateChangeListener(ReachabilityStateChangeListener reachabilityStateChangeListener) {
		this.reachabilityStateChangeListener = reachabilityStateChangeListener;
	}

	public ReachabilityState getInvalidState() {
		return invalidState;
	}

	public ReachabilityState getInitialState() {
		return initialState;
	}

	public void reset() {
		setCurrentState(initialState.getPetrinetState());
	}
}
