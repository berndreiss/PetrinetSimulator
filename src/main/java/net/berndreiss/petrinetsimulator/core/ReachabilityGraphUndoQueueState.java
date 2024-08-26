package net.berndreiss.petrinetsimulator.core;

/**
 * <p>
 * Class representing a state in the undo queue.
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
	private ReachabilityGraphUndoQueueState previousState = null;
	/** The state after this. */
	private ReachabilityGraphUndoQueueState nextState = null;

	/**
	 * Instantiate a new state for the queue.
	 * 
	 * @param previousState The state preceding the current state.
	 * 
	 * @param petrinetState the petrinets state currently active in the reachability
	 *                      graph
	 * @param currentEdge   the edge currently active in the reachability graph
	 * @param stateAdded    defines the type of component that has been added
	 * @param transition    transition that has been fired
	 * @param skippable     if true, state is skippable
	 */
	ReachabilityGraphUndoQueueState(ReachabilityGraphUndoQueueState previousState, PetrinetState petrinetState,
			String currentEdge, AddedType stateAdded, Transition transition, boolean skippable) {

		this.previousState = previousState;
		this.state = petrinetState;
		this.currentEdge = currentEdge;
		this.stateAdded = stateAdded;
		this.transition = transition;
		this.skippable = skippable;

	}

	/**
	 * Gets the petrinet state.
	 *
	 * @return the petrinet state
	 */
	public PetrinetState getPetrinetState() {
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
	 * Gets the type that has been added.
	 *
	 * @return the type added
	 */
	public AddedType getAddedType() {
		return stateAdded;
	}

	/**
	 * Gets the edge currently active in the reachability graph.
	 * 
	 * @return the edge
	 * 
	 */
	public String getEdge() {
		return currentEdge;
	}

	/**
	 * Returns whether state is skippable when un-/redoing steps.
	 * 
	 * @return true, if state is skippable
	 */
	public boolean isSkippable() {
		return skippable;
	}

	/**
	 * Sets the next state.
	 * 
	 * @param nextState state to be set
	 */
	public void setNextState(ReachabilityGraphUndoQueueState nextState) {
		this.nextState = nextState;
	}

	/**
	 * Checks whether there is a next state.
	 * 
	 * @return true, if there is a next state
	 */
	public boolean hasNext() {
		return nextState != null;
	}

	/**
	 * Checks whether current state is first state.
	 * 
	 * @return true, if state is the first
	 */
	public boolean isFirst() {
		return previousState == null;
	}

	/**
	 * Gets the previous state.
	 * 
	 * @return the previous state
	 */
	public ReachabilityGraphUndoQueueState getPrevious() {
		return previousState;
	}

	/**
	 * Gets the next state.
	 * 
	 * @return the next state
	 */
	public ReachabilityGraphUndoQueueState getNext() {
		return nextState;
	}

	/**
	 * Sets the petrinet state.
	 * 
	 * @param newPetrinetState the new petrinet state
	 */
	public void setPetrinetState(PetrinetState newPetrinetState) {
		this.state = newPetrinetState;
	}
}
