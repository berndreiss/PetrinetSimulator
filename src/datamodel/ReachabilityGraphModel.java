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
		setNewCurrentState(petrinetStates.get(state.getState()), true);
		
	}

	public PetrinetState addNewState(Petrinet petrinet, Transition t) {

		PetrinetState petrinetState;
		String petrinetStateString = petrinet.getStateString();

		if (petrinetStates.containsKey(petrinetStateString)) {
			petrinetState = petrinetStates.get(petrinetStateString);
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

		if (stateChangeListener != null)
			stateChangeListener.onAdd(petrinetState, currentState, t);

		setNewCurrentState(petrinetState, false);

		return petrinetState;
	}

	private void setNewCurrentState(PetrinetState newState, boolean reset) {
		this.currentState = newState;
		if (stateChangeListener != null)
			stateChangeListener.onSetCurrent(newState, reset);

	}

	public boolean checkIfCurrentStateIsBackwardsValid() {

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

			petrinetStates.put(initialState.getState(), initialState);
//			stateChangeListener.onSetInitial(initialState);
			setCurrentState(initialState);
		}

	}

	private void removeStateFromGraph(PetrinetState ps) {
		if (stateChangeListener != null)
			stateChangeListener.onRemove(ps);
	}

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

	public void setInitial() {
		setCurrentState(initialState);
	}
//	
//	public void print() {
//		System.out.println("STATES: ");
//		for (PetrinetState ps: petrinetStates)
//			ps.print();
//	}

}
