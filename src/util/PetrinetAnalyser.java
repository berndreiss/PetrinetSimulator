package util;

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
	
	private Set<String> handledSet;
	
	private PetrinetState originalState;
	
	private boolean invalidStateEncountered;
	
	public PetrinetAnalyser(PetrinetController controller) {
		this.controller = controller;
		this.handledSet = new HashSet<String>();
	}

	
	public boolean analyse() {
		
		
		
		this.originalState = controller.getReachabilityGraphModel().getInitialState();

		Petrinet initialPetrinet = controller.getPetrinet();
		
		ReachabilityGraphModel reachabilityGraphModel = controller.getReachabilityGraphModel();
		
		List<String> transitionList = initialPetrinet.getActiveTransitions();
		
		for (String s: transitionList) {
			initialPetrinet.fireTransition(s);
			if (!controller.stateIsValid()) {
				controller.resetPetrinet();
				controller.updateReachabilityGraph();
				return false;
			}
			analyseState(reachabilityGraphModel.getCurrentState(), s);
			if (!controller.stateIsValid()) {
				controller.resetPetrinet();
				controller.updateReachabilityGraph();
				return false;
			}
			controller.resetPetrinet();
			
		}
		controller.updateReachabilityGraph();
		return true;
	}
	
	private void analyseState(PetrinetState petrinetState, String transition) {
		String fireString = petrinetState.getState() + transition;
		
		if (handledSet.contains(fireString))
			return;

		handledSet.add(fireString);

		Petrinet petrinet = controller.getPetrinet();
		
		List<String> transitionList = petrinet.getActiveTransitions();

		
		for (String s: transitionList) {
			petrinet.fireTransition(s);
			if (!controller.stateIsValid())
				return;
			analyseState(petrinetState, s);
			controller.setState(petrinetState);
			
			
		}
		return;
	}
}
