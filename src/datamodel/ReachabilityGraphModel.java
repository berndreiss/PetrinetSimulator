package datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import control.PetrinetController;


public class ReachabilityGraphModel {

	private String currentStateId;

	private int[] state;
	
	private Map<String, List<ReachabilityNode>> reachabilityGraphModel;
	
	private PetrinetController controller;
	
	public ReachabilityGraphModel(PetrinetController controller) {
		this.controller = controller;
		updateCurrentStateId();
		System.out.println(currentStateId);
		reachabilityGraphModel = new HashMap<String, List<ReachabilityNode>>();
	}
	
	public String getCurrentStateId() {
		updateCurrentStateId();		
		return currentStateId;
	}
	
	private void updateCurrentStateId() {
		StringBuilder sb = new StringBuilder();
		for (String s: controller.getPetrinet().getPlaces().keySet())
			sb.append(controller.getPetrinet().getPlaces().get(s).getNumberOfTokens());
		currentStateId = sb.toString();
	}
	
	public void setCurrentStateId(String id) {
		this.currentStateId = id;
	}
	
	public void addState(String state) {
		reachabilityGraphModel.put(state, new ArrayList<ReachabilityGraphModel.ReachabilityNode>());
	}
	
	public boolean hasState(String id) {
		return reachabilityGraphModel.containsKey(id);
	}
	
	
	
	private class ReachabilityNode{
		
		private List<TransitionNode> successors;
		
		public ReachabilityNode(){
			
		}
		
		
		
		
		private class TransitionNode{
			private Transition transition;
			private ReachabilityNode node;
			
			public TransitionNode(Transition transition, ReachabilityNode node) {
				this.transition = transition;
				this.node = node;
			}
		}
	}
}
