package datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import control.PetrinetController;

public class ReachabilityGraphModel {

	private ReachabilityState currentState;

	
	private int[] state;

	private Map<String, ReachabilityState> reachabilityStates;

	public ReachabilityGraphModel(PetrinetController controller) {

		reachabilityStates = new HashMap<String, ReachabilityState>();
		addNewState(controller.getPetrinet().getState());
	}

	public ReachabilityState getState(String state) {
		return reachabilityStates.get(state);
	}

	public String getCurrentState() {
		return currentState.getState();
	}

	public void setCurrentState(PetrinetState state) {
		currentState = reachabilityStates.get(state.getState());
	}
	
	public ReachabilityState addNewState(PetrinetState petrinetState) {
		
		
		String newStateString = petrinetState.getState();
		ReachabilityState newState;
		if (reachabilityStates.containsKey(newStateString)) {
			newState = reachabilityStates.get(newStateString);
			newState.addPredecessor(currentState);
		} else {
			newState = new ReachabilityState(petrinetState, currentState);
			reachabilityStates.put(newStateString, newState);
		}
		if (currentState != null)
			currentState.addSuccessor(newState);

		currentState = newState;
		return currentState;
	}

	public ReachabilityState checkIfStateIsBackwardsValid(ReachabilityState state) {
	
		if (state.getPredecessors().size() == 0)
			return null;
		
		for (ReachabilityState s: state.getPredecessors()) {
			ReachabilityState tempState =  checkIfStateIsBackwardsValid(s, new HashMap<String, String>(), state);
			
			if (tempState != null)
				return tempState;
		}
		
		return null;
	}

	private ReachabilityState checkIfStateIsBackwardsValid(ReachabilityState state, Map<String,String> visitedStates,  ReachabilityState originalState) {

		
		
		if (visitedStates.containsKey(state.getState()))
				return null;

		visitedStates.put(state.getState(), "");
		
		if (originalState.isBiggerThan(state)) {
			return state;
		}
			
		for (ReachabilityState s : state.getPredecessors()) {
			ReachabilityState tempState = checkIfStateIsBackwardsValid(s, visitedStates, originalState);
			if (tempState != null)
				return tempState;
		}

		return null;

	}

	public boolean hasState(String id) {
		return reachabilityStates.containsKey(id);
	}

}
