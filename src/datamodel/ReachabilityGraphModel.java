package datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import control.PetrinetController;
import util.IterableHashMap;

public class ReachabilityGraphModel {

	private PetrinetState currentState;

	private PetrinetState invalidState;

	private PetrinetState initialState;

	private IterableHashMap<String, PetrinetState> petrinetStates;

	private StateChangeListener stateChangeListener;

	public ReachabilityGraphModel(PetrinetController controller) {

		petrinetStates = new IterableHashMap<String, PetrinetState>();
		addNewState(controller.getPetrinet(), null);
	}

	public Iterable<PetrinetState> getStates() {
		return petrinetStates;
	}

	public PetrinetState getState(String state) {
		return petrinetStates.get(state);
	}

	public PetrinetState getCurrentPetrinetState() {
		return currentState;
	}

	public PetrinetState getCurrentState() {
		return currentState;
	}

	public void setCurrentState(PetrinetState state) {
		setNewCurrentState(petrinetStates.get(state.getState()));
		if (stateChangeListener != null)
			stateChangeListener.onSetCurrent(state);
	}

	public PetrinetState addNewState(Petrinet petrinet, Transition t) {
		
		PetrinetState petrinetState;
		String petrinetStateString = petrinet.getStateString();

		if (petrinetStates.containsKey(petrinetStateString)) {
			petrinetState = petrinetStates.get(petrinetStateString);
			if (currentState != null && currentState == petrinetState)
				return currentState;
		} else {
			petrinetState = new PetrinetState(petrinet);
			petrinetStates.put(petrinetStateString, petrinetState);
		}


		if (currentState != null) {
			petrinetState.addPredecessor(currentState, t);
			currentState.addSuccessor(petrinetState, t);
		}
		
		if (t == null) {
			initialState = petrinetState;

		}

		if (stateChangeListener != null && t != null)
			stateChangeListener.onAdd(petrinetState, currentState, t);

		setNewCurrentState(petrinetState);

		return petrinetState;
	}

	private void setNewCurrentState(PetrinetState newState) {
		this.currentState = newState;
		if (stateChangeListener != null)
			stateChangeListener.onSetCurrent(newState);

	}

	public void checkIfCurrentStateIsBackwardsValid() {

		if (currentState.getPredecessorsSize() == 0)
			return;

		for (PetrinetState s : currentState.getPredecessors()) {
			PetrinetState state = checkIfStateIsBackwardsValid(s, new ArrayList<PetrinetState>(), currentState);

			if (state != null) {
				invalidState = currentState;
				currentState.setM(state, stateChangeListener);
				return;
			}
		}

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

	public boolean hasState(String id) {
		return petrinetStates.containsKey(id);
	}

	public void setStateChangeListener(StateChangeListener reachabilityStateChangeListener) {
		this.stateChangeListener = reachabilityStateChangeListener;
	}

	public PetrinetState getInvalidState() {
		return invalidState;
	}

	public PetrinetState getInitialState() {
		return initialState;
	}

	private void removeStateEdges(PetrinetState petrinetState) {
		if (!petrinetStates.containsKey(petrinetState.getState()))
			return;

		for (PetrinetState p : petrinetState.getPredecessors())
			petrinetState.removePredecessor(p, stateChangeListener);

		for (PetrinetState p : petrinetState.getSuccessors())
			petrinetState.removeSuccessor(p, stateChangeListener);

	}

	public void reset() {
		reset(null);
	}

	private void reset(Petrinet petrinet) {
		
		if (petrinetStates.size() <= 1)
			return;
		
		for (PetrinetState ps : petrinetStates) {
			
			removeStateEdges(ps);
			if (ps== initialState)
				continue;
			removeStateFromGraph(ps);
		}

		petrinetStates.clear();

		if (petrinet != null) {
			PetrinetState newState = addNewState(petrinet, null);
			setCurrentState(newState);
		} else {

			petrinetStates.put(initialState.getState(), initialState);
//			stateChangeListener.onSetInitial(initialState);
			setCurrentState(initialState);
		}

	}

	private void removeStateFromGraph(PetrinetState ps) {
		if (stateChangeListener != null)
			stateChangeListener.onRemove(ps);
	}

	public void resetTo(Petrinet petrinet) {
		reset();
		petrinetStates.clear();
		removeStateFromGraph(initialState);
		PetrinetState state = addNewState(petrinet, null);
		stateChangeListener.onSetInitial(state);
	}

	public void setInitial() {
		setCurrentState(initialState);
	}

}
