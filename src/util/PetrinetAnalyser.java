package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import control.PetrinetController;
import datamodel.Petrinet;
import datamodel.PetrinetState;
import datamodel.ReachabilityGraphModel;
import datamodel.Transition;

public class PetrinetAnalyser {
	
	private PetrinetController controller;
	
	private Petrinet petrinet;
	
	private ReachabilityGraphModel reachabilityGraphModel;
	
	private boolean finite = true;
	
	private int edges;
	private int nodes;
	
	private List<String> transitionsToMMarked;
	private String m;
	private String mMarked;

	
	public PetrinetAnalyser(PetrinetController controller) {
		this.controller = controller;
		analyse();
		if (!finite) {
			updateReachabilityGraph();
			m = reachabilityGraphModel.getInvalidState().getM().getState();
			mMarked = reachabilityGraphModel.getInvalidState().getState();
		}
	}

	
	private boolean analyse() {
		
	
		
		reachabilityGraphModel = controller.getReachabilityGraphModel();
		reachabilityGraphModel.reset();
		petrinet = controller.getPetrinet();
		
		Set<PetrinetState> visited = new HashSet<PetrinetState>();
		
		return analyseState(reachabilityGraphModel.getCurrentPetrinetState(), visited);
		

	}
	
	private boolean analyseState(PetrinetState state, Set<PetrinetState> visited) {

		if (visited.contains(state))
			return true;

		nodes++;
		
		visited.add(state);
		petrinet.setState(state);
		
		for (Transition t: petrinet.getActiveTransitions()) {
			petrinet.fireTransition(t.getId());
			boolean stateValid = controller.getReachabilityGraphModel().checkIfCurrentStateIsBackwardsValid();
			if (!stateValid) {
				finite = false;
				return false;
			}
			edges++;
			analyseState(reachabilityGraphModel.getCurrentPetrinetState(), visited);
			reachabilityGraphModel.setCurrentState(state);
			petrinet.setState(state);
			
		}
		
		return true;
	}
	
	public boolean isFinite() {
		return finite;
	}


	public int getStateCount() {
		return nodes;
	}
	
	public int getEdgeCount() {
		return edges;
	}
	
	public String getM() {
		return m;
	}
	
	public String getMMarked() {
		return mMarked;
	}
	
	public List<String> getTransitionsToMMarked(){
		return transitionsToMMarked;
	}
	
	private void updateReachabilityGraph() {

		PetrinetState invalidState = reachabilityGraphModel.getInvalidState();
		if (invalidState != null) {

			m = invalidState.getM().getState();
			mMarked = invalidState.getState();
			transitionsToMMarked = new ArrayList<String>();

			List<PetrinetState> pathToM = invalidState.getPathToOtherState(invalidState.getM());

			List<PetrinetState> pathToInitial = invalidState.getM()
					.getPathToOtherState(reachabilityGraphModel.getInitialState());
			PetrinetState currentState = reachabilityGraphModel.getInitialState();
			PetrinetState initialState = reachabilityGraphModel.getInitialState();

			ArrayList<Transition> transitionList = new ArrayList<Transition>();

			Transition transition;

			if (pathToInitial != null) {
				for (PetrinetState nextState : pathToInitial) {

					transition = currentState.getFirstSuccessorTransition(nextState);

					transitionList.add(transition);

					currentState = nextState;
				}

			}

			if (initialState != invalidState.getM()) {
				transition = currentState.getFirstSuccessorTransition(invalidState.getM());

				transitionList.add(transition);

				currentState = invalidState.getM();
			}
			if (pathToM != null) {
				for (PetrinetState nextState : pathToM) {

					transition = currentState.getFirstSuccessorTransition(nextState);
					transitionsToMMarked.add(transition.getId());

					transitionList.add(transition);

					currentState = nextState;
				}
			}
			transition = currentState.getFirstSuccessorTransition(invalidState);

			if (transition != null)
				transitionsToMMarked.add(transition.getId());

			transitionList.add(transition);


			reachabilityGraphModel.reset();
			
			for (Transition t : transitionList) {
				if (t != null)
					petrinet.fireTransition(t.getId());
			}
			return;
		}
	}

}
