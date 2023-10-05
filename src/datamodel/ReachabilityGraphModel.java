package datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import control.PetrinetController;

public class ReachabilityGraphModel {

	private ReachabilityState currentState;

	private int[] state;

	private Map<String, ReachabilityState> reachabilityStates;

	public ReachabilityGraphModel(PetrinetController controller) {

		reachabilityStates = new HashMap<String, ReachabilityState>();
		addNewState(controller.getPetrinet());
	}

	public ReachabilityState getState(String state) {
		return reachabilityStates.get(state);
	}

	public String getCurrentState() {
		return currentState.getState();
	}

	public ReachabilityState addNewState(Petrinet petrinet) {
		StringBuilder sb = new StringBuilder();

		List<Integer> placeTokens = new ArrayList<Integer>();

		for (String s : petrinet.getPlaces().keySet()) {
			Place p = petrinet.getPlaces().get(s);
			sb.append(p.getNumberOfTokens());
			placeTokens.add(p.getNumberOfTokens());
		}
		String newStateString = sb.toString();
		ReachabilityState newState;
		if (reachabilityStates.containsKey(newStateString)) {
			newState = reachabilityStates.get(newStateString);
			newState.addPredecessor(currentState);
		} else {
			newState = new ReachabilityState(newStateString, placeTokens, currentState);
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
			ReachabilityState tempState =  checkIfStateIsBackwardsValid(s, state);
			
			System.out.println(tempState);
			if (tempState != null)
				return tempState;
		}
		
		return null;
	}

	private ReachabilityState checkIfStateIsBackwardsValid(ReachabilityState state, ReachabilityState originalState) {

		if (state == originalState )
				return null;

		
			
		
		System.out.println(originalState.getState());
		System.out.println(state.getState());
		
		
		if (originalState.isBiggerThan(state)) {
			System.out.println("is bigger");
			System.out.println(state.getState());
			return state;

		}else{
			System.out.println("is not bigger");
		}
			
		for (ReachabilityState s : state.getPredecessors()) {
			ReachabilityState tempState = checkIfStateIsBackwardsValid(s, originalState);
			if (tempState != null)
				return tempState;
		}

		return null;

	}

	public boolean hasState(String id) {
		return reachabilityStates.containsKey(id);
	}

}
