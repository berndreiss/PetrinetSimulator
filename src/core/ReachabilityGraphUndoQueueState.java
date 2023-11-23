package core;

import listeners.ToolbarButtonListener;

//TODO add comments
/**
 * <p>
 * 
 * </p>
 */
public class ReachabilityGraphUndoQueueState {

	/** The currently active petrinet state of the reachability graph. */
	private PetrinetState state;
	/** The currently active edge of the reachability graph. */
	private String currentEdge;
	/** The transition being fired to get here. */
	private Transition transition = null;
	/** The type that has been added. */
	private AddedType stateAdded = AddedType.NOTHING;
	/** True if this step can be skipped on un-/redo. */
	private boolean skippable;

	/** The state before this. */
	private ReachabilityGraphUndoQueueState lastState = null;
	/** The state after this. */
	private ReachabilityGraphUndoQueueState nextState = null;

	/**
	 * 
	 * @param state
	 * @param currentEdge
	 * @param stateAdded
	 * @param transition
	 * @param skippable
	 */
	public ReachabilityGraphUndoQueueState(ReachabilityGraphUndoQueueState lastState, PetrinetState state,
			String currentEdge, AddedType stateAdded, Transition transition, boolean skippable) {

		this.lastState = lastState;
		this.state = state;
		this.currentEdge = currentEdge;
		this.stateAdded = stateAdded;
		this.transition = transition;
		this.skippable = skippable;

	}

	/**
	 * 
	 * @return
	 */
	public AddedType getAdded() {
		return stateAdded;
	}

	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public PetrinetState getState() {
		return state;
	}

	/**
	 * Gets the transition.
	 *
	 * @return the transition
	 */
	public Transition getTransition() {
		return transition;
	}

	/**
	 * State added.
	 *
	 * @return the added
	 */
	public AddedType stateAdded() {
		return stateAdded;
	}

	/**
	 * 
	 */
	public String getEdge() {
		return currentEdge;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isSkippable() {
		return skippable;
	}

	/**
	 * 
	 * @param nextState
	 */
	public void setNextState(ReachabilityGraphUndoQueueState nextState) {
		this.nextState = nextState;
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasNext() {
		return nextState != null;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isFirst() {
		return lastState == null;
	}

	/**
	 * 
	 * @return
	 */
	public ReachabilityGraphUndoQueueState getLast() {
		return lastState;
	}

	/**
	 * 
	 * @return
	 */
	public ReachabilityGraphUndoQueueState getNext() {
		return nextState;
	}

	/**
	 * 
	 * @param newPetrinetState
	 */
	public void setPetrinetState(PetrinetState newPetrinetState) {
		this.state = newPetrinetState;
	}
}
