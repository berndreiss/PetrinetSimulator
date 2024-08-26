package net.berndreiss.petrinetsimulator.core;

import net.berndreiss.petrinetsimulator.listeners.ToolbarChangedListener;

/**
 * <p>
 * Class representing an undo queue for a reachability graph.
 * </p>
 */
public class ReachabilityGraphUndoQueue {

	/** The reachability graph. */
	private ReachabilityGraphModel reachabilityGraph;
	/** The listener for the toolbar buttons. */
	private ToolbarChangedListener toolbarButtonListener;
	/** The current state of the queue. */
	private ReachabilityGraphUndoQueueState currentState = null;

	/**
	 * Instantiates a new undo queue.
	 *
	 * @param reachabilityGraph     the reachability graph for which this queue is
	 *                              used
	 * @param toolbarButtonListener the listener for the toolbar buttons
	 */
	ReachabilityGraphUndoQueue(ReachabilityGraphModel reachabilityGraph,
			ToolbarChangedListener toolbarButtonListener) {
		this.reachabilityGraph = reachabilityGraph;
		this.toolbarButtonListener = toolbarButtonListener;

	}

	/**
	 * Pushes a new petrinet state onto the queue.
	 * 
	 * @param state       the state to be added
	 * @param currentEdge the edge being currently active
	 * @param stateAdded  information about whether components have been added
	 * @param transition  the transition that has been fired
	 * @param skippable   true if step can be skipped on un-/redo
	 */
	void push(PetrinetState state, String currentEdge, AddedType stateAdded, Transition transition,
			boolean skippable) {

		// create new state for queue with the current state as predecessor
		ReachabilityGraphUndoQueueState newState = new ReachabilityGraphUndoQueueState(currentState, state, currentEdge,
				stateAdded, transition, skippable);

		// set undo button to not be highlighted if it is the beginning of the queue
		if ((currentState == null || currentState.isFirst()) && toolbarButtonListener != null)
			toolbarButtonListener.onSetUndoButton(true);
		// if the current state exists add new state as successor
		if (currentState != null) {
			// if the current state had a next one we need to set the redo button to be not
			// be highlighted
			if (currentState.hasNext() && toolbarButtonListener != null)
				toolbarButtonListener.onSetRedoButton(false);
			currentState.setNextState(newState);
		}

		// update current state
		currentState = newState;

	}

	/**
	 * Goes a step back in the queue.
	 */
	public void goBack() {

		// if we are at the beginning of the queue we can not go further
		if (currentState.isFirst())
			return;

		// do not push changes made to the reachability graph -> all steps already exist
		reachabilityGraph.setPushing(false);

		// if we are at the end of the queue we have to set the redo button to be
		// highlighted
		if (!currentState.hasNext() && toolbarButtonListener != null)
			toolbarButtonListener.onSetRedoButton(true);

		// UNDO CURRENT STEP
		// if state has been added, remove it
		if (currentState.getAddedType() == AddedType.STATE)
			reachabilityGraph.removeState(currentState.getPetrinetState());
		// if only edge has been added, remove it
		if (currentState.getAddedType() == AddedType.EDGE)
			reachabilityGraph.removeEdge(currentState.getPrevious().getPetrinetState(), currentState.getPetrinetState(),
					currentState.getTransition());

		// change current state and set currently active components in reachability
		// graph
		currentState = currentState.getPrevious();
		reachabilityGraph.getPetrinet().setState(currentState.getPetrinetState());
		reachabilityGraph.setCurrentState(currentState.getPetrinetState());
		reachabilityGraph.setCurrentEdge(currentState.getEdge());

		// if we reached the beginning of the queue set undo button to not be
		// highlighted
		if (currentState.isFirst() && toolbarButtonListener != null)
			toolbarButtonListener.onSetUndoButton(false);

		// if the current step is skippable we need to continue
		if (currentState.isSkippable())
			goBack();

		// reset reachability graph to push changes to the queue
		reachabilityGraph.setPushing(true);

	}

	/**
	 * Goes a step forward in the queue.
	 * 
	 * @return true if a step forward has been taken
	 */
	public boolean goForward() {

		if (currentState == null)
			return false;

		// if there is no next step we can not go further.
		if (!currentState.hasNext())
			return false;

		// do not push changes made to the reachability graph -> all steps already exist
		reachabilityGraph.setPushing(false);

		// if we are at the beginning of the queue we have to set undo button to not be
		// highlighted
		if (currentState.isFirst() && toolbarButtonListener != null)
			toolbarButtonListener.onSetUndoButton(true);

		// update current state
		currentState = currentState.getNext();

		// first reset the petrinet (in order to redo change in step)
		Petrinet petrinet = reachabilityGraph.getPetrinet();
		petrinet.setState(currentState.getPetrinetState());

		// if something has been added, readd state with transition, otherwise only set
		// reachability graph to given state
		if (currentState.getAddedType() != AddedType.NOTHING || currentState.getTransition() != null) {
			// since a new petrinet state is created it needs to be updated in the queue
			// state
			PetrinetState newPetrinetState = reachabilityGraph.addNewState(petrinet, currentState.getTransition());
			currentState.setPetrinetState(newPetrinetState);
		} else
			reachabilityGraph.setCurrentState(currentState.getPetrinetState());

		// if we are at the end of the queue we need to set the redo button to not be
		// highlighted
		if (!currentState.hasNext() && toolbarButtonListener != null)
			toolbarButtonListener.onSetRedoButton(false);

		// if the current step is skippable we need to continue
		if (currentState.isSkippable())
			goForward();

		// reset reachability graph to push changes to the queue
		reachabilityGraph.setPushing(true);

		return true;
	}

	/**
	 * Rewinds the queue, remove everything but initial state and reset the toolbar
	 * buttons.
	 */
	void reset() {

		if (currentState == null)
			return;

		rewind();

		// "cut head off"
		currentState.setNextState(null);

		// reset buttons
		if (toolbarButtonListener != null)
			toolbarButtonListener.resetUndoRedoButtons();

	}

	// Goes to beginning of queue and undo everything.
	private void rewind() {

		if (currentState == null)
			return;

		while (!currentState.isFirst())
			goBack();
	}

	/**
	 * Goes to beginning of queue but without undoing all steps.
	 */
	public void rewindSilent() {

		// case when editor is opened
		if (currentState == null)
			return;

		// get to the first state
		while (!currentState.isFirst())
			currentState = currentState.getPrevious();

		// do not push changes (since they already exist in the queue
		reachabilityGraph.setPushing(false);

		// set current parameters
		reachabilityGraph.getPetrinet().setState(currentState.getPetrinetState());
		reachabilityGraph.setCurrentState(currentState.getPetrinetState());
		reachabilityGraph.setCurrentEdge(currentState.getEdge());

		// reenable pushing
		reachabilityGraph.setPushing(true);

	}

	/**
	 * Gets the current state of the queue
	 * 
	 * @return the current state
	 */
	public ReachabilityGraphUndoQueueState getCurrentState() {
		return currentState;
	}

	/**
	 * Sets reachability graph to given state in the queue if it exists.
	 * 
	 * @param state state to be set
	 */
	public void setToState(ReachabilityGraphUndoQueueState state) {

		if (state == null)
			return;

		// keep track of current state to reset to is state does not exist in queue
		ReachabilityGraphUndoQueueState stateTemp = currentState;
		// rewind the queue
		rewindSilent();

		// if the state is the first state, nothing to be done but setting the toolbar
		// buttons
		if (state.isFirst()) {
			toolbarButtonListener.onSetUndoButton(false);
			toolbarButtonListener.onSetRedoButton(currentState.hasNext());
			return;
		}

		// keep track of whether state exists in queue
		boolean stateExists = false;

		// search for state in queue and in the process redo steps
		while (currentState != state && currentState.hasNext()) {
			goForward();
			if (currentState == state)
				stateExists = true;
		}

		// if state did not exist restore to previous state
		if (!stateExists) {
			rewindSilent();
			while (currentState != stateTemp && currentState.hasNext())
				goForward();

		}

		// handle toolbar buttons
		toolbarButtonListener.onSetUndoButton(true);
		toolbarButtonListener.onSetRedoButton(currentState.hasNext());

	}

}
