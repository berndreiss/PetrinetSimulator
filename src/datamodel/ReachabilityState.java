package datamodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ReachabilityState{

	private PetrinetState petrinetState;
	
	private List<ReachabilityState> predecessors;
	private List<ReachabilityState> successors;

	public ReachabilityState(PetrinetState petrinetState, ReachabilityState predecessor) {
		this.petrinetState = petrinetState;
		
		predecessors = new ArrayList<ReachabilityState>();
		successors = new ArrayList<ReachabilityState>();
		if (predecessor != null)
			predecessors.add(predecessor);
	}

	public String getState() {
		return petrinetState.getState();
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
		if (this.petrinetState.placeTokensSize() != other.petrinetState.placeTokensSize())
			return false;
		
		boolean differenceInItemFlag = false;
		
		Iterator<Integer> thisIt = this.petrinetState.getPlaceTokens();
		Iterator<Integer> otherIt = other.petrinetState.getPlaceTokens();
		
		while (thisIt.hasNext()) {
			int thisPlaceTokens = thisIt.next();
			int otherPlaceTokens = otherIt.next();
			
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


