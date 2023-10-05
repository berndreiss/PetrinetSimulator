package datamodel;

import java.util.ArrayList;
import java.util.List;

public class ReachabilityState {

	private String state;
	private List<Integer> placeTokens;
	private List<ReachabilityState> predecessors;
	private List<ReachabilityState> successors;

	public ReachabilityState(String state, List<Integer> placeTokens, ReachabilityState predecessor) {
		this.state = state;
		this.placeTokens = placeTokens;

		predecessors = new ArrayList<ReachabilityState>();
		successors = new ArrayList<ReachabilityState>();
		if (predecessor != null)
			predecessors.add(predecessor);
	}

	public String getState() {
		return state;
	}

	public List<ReachabilityState> getPredecessors() {
		return predecessors;
	}

	public List<ReachabilityState> getSuccessors() {
		return successors;
	}

	public void addSuccessor(ReachabilityState newSuccessor) {
		successors.add(newSuccessor);
	}

	public void addPredecessor(ReachabilityState newPredecessor) {
		predecessors.add(newPredecessor);
	}

	
	public boolean isBiggerThan(ReachabilityState other) {
	
		return compareStates(other, (i,j)->i<j);
	}
	
	public boolean isSmallerThan(ReachabilityState other) {
		return compareStates(other, (i,j)->i>j);
	}
	private boolean compareStates(ReachabilityState other, ReachabilityComparator comparator) {
		if (this.placeTokens.size() != other.placeTokens.size())
			return false;
		
		boolean differenceInItemFlag = false;
		
		for (int i=0; i<placeTokens.size();i++) {
			int thisPlaceTokens = this.placeTokens.get(i);
			int otherPlaceTokens = other.placeTokens.get(i);
			
			if (comparator.compare(thisPlaceTokens, otherPlaceTokens))
				return false;
			
			if (!(thisPlaceTokens==otherPlaceTokens))
				differenceInItemFlag = true;
		}
		
		if (differenceInItemFlag)
			return true;
		
		return false;	
	}


	private interface ReachabilityComparator{
		
		boolean compare(int i, int j);
	}

}


