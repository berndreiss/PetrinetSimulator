package core;

import listeners.ToolbarToggleListener;

// TODO: Auto-generated Javadoc
/**
 * The Class PetrinetQueue.
 */
public class ReachabilityGraphStateQueue {

	private PetrinetState state;
	private String currentEdge;
	private Transition transition = null;
	private Added stateAdded = Added.NOTHING;
	private boolean skippable;

	private ReachabilityGraphStateQueue lastState = null;
	private ReachabilityGraphStateQueue nextState = null;

	private ReachabilityGraphModel reachabilityGraphModel;
	private ToolbarToggleListener toolbarToggleListener;

	// TODO implement skippable steps

	/**
	 * Instantiates a new petrinet queue.
	 *
	 * @param reachabilityGraphModel the petrinet controller
	 */
	public ReachabilityGraphStateQueue(ReachabilityGraphModel reachabilityGraphModel, ToolbarToggleListener toolbarToggleListener) {
		this.reachabilityGraphModel = reachabilityGraphModel;
		this.toolbarToggleListener = toolbarToggleListener;

	}

	private ReachabilityGraphStateQueue(PetrinetState state, String currentEdge, Added stateAdded, Transition transition,
			ReachabilityGraphModel reachabilityGraphModel, ToolbarToggleListener toolbarToggleListener,
			boolean skippable) {

		this.state = state;
		this.currentEdge = currentEdge;
		this.stateAdded = stateAdded;
		this.transition = transition;
		this.skippable = skippable;
		this.reachabilityGraphModel = reachabilityGraphModel;
		this.toolbarToggleListener = toolbarToggleListener;
		this.lastState = reachabilityGraphModel.getPetrinetQueue();
	}

	/**
	 * 
	 * @return
	 */
	public Added getAdded() {
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
	private Added stateAdded() {
		return stateAdded;
	}

	/**
	 * Push.
	 *
	 * @param state       the state
	 * @param currentEdge
	 * @param stateAdded  the state added
	 * @param transition  the transition
	 */
	public void push(PetrinetState state, String currentEdge, Added stateAdded, Transition transition,
			boolean skippable) {

//		System.out.println("PUSHING " + state.getState() + ", " + currentEdge + ", " + stateAdded + ", "
//				+ (transition == null ? "null" : transition.getId()) + ", " + skippable);
		ReachabilityGraphStateQueue currentState = reachabilityGraphModel.getPetrinetQueue();

		if (currentState.state == null) {

			currentState.state = state;
			currentState.currentEdge = currentEdge;
			currentState.stateAdded = stateAdded;
			currentState.transition = transition;
			currentState.skippable = skippable;
		} else {
			if (currentState.isFirstState() && toolbarToggleListener != null)
				toolbarToggleListener.onUndoChanged();
			if (currentState.hasNext() && toolbarToggleListener != null)
				toolbarToggleListener.onRedoChanged();

			currentState.nextState = new ReachabilityGraphStateQueue(state, currentEdge, stateAdded, transition,
					reachabilityGraphModel, toolbarToggleListener, skippable);
			reachabilityGraphModel.setPetrinetQueue(currentState.nextState);
		}
	}

	/**
	 * Go back.
	 */
	public void goBack() {
		ReachabilityGraphStateQueue currentState = reachabilityGraphModel.getPetrinetQueue();
		if (currentState.isFirstState())
			return;

		if (!currentState.hasNext() && toolbarToggleListener != null)
			toolbarToggleListener.onRedoChanged();

		if (currentState.stateAdded == Added.STATE) {
			reachabilityGraphModel.removeState(currentState.getState());
		} else if (currentState.stateAdded == Added.EDGE) {
			reachabilityGraphModel.removeEdge(currentState.lastState.getState(), currentState.getState(),
					currentState.getTransition());
		}

		//change current state
		currentState = currentState.lastState;
		reachabilityGraphModel.setPetrinetQueue(currentState);
		reachabilityGraphModel.getPetrinet().setState(currentState.getState());
		reachabilityGraphModel.setCurrentState(currentState.getState(), false);
		reachabilityGraphModel.setCurrentEdge(currentState.getCurrentEdge());

		if (currentState.isFirstState() && toolbarToggleListener != null)
			toolbarToggleListener.onUndoChanged();

		if (currentState.isSkippable())
			goBack();
		// printAll();

	}

	private String getCurrentEdge() {
		return currentEdge;
	}

	/**
	 * Go forward.
	 */
	public boolean goForward() {

		ReachabilityGraphStateQueue currentState = reachabilityGraphModel.getPetrinetQueue();

		if (!currentState.hasNext())
			return false;

		if (currentState.isFirstState() && toolbarToggleListener != null)
			toolbarToggleListener.onUndoChanged();

		
		reachabilityGraphModel.setPetrinetQueue(currentState.nextState);

		currentState = currentState.nextState;

		System.out.println("CURRENT STATE: " + currentState.getState().getState());

		Petrinet petrinet = reachabilityGraphModel.getPetrinet();

		petrinet.setState(currentState.getState());

		System.out.println("PETRINET: " + petrinet.getStateString());
		if (currentState.stateAdded() != Added.NOTHING) {
			System.out.println("TRANSITION: " + currentState.getTransition().getId());
			reachabilityGraphModel.addNewState(petrinet, currentState.getTransition(), false);
			currentState.state = reachabilityGraphModel.getState(petrinet.getStateString());// since a new instance has
																							// been created, the state
																							// has to be updated
		} else 
			reachabilityGraphModel.setCurrentState(currentState.getState(), false);

System.out.println();
		if (!currentState.hasNext() && toolbarToggleListener != null)
			toolbarToggleListener.onRedoChanged();

		if (currentState.isSkippable())
			goForward();

		return true;
	}

	/**
	 * Checks if is first state.
	 *
	 * @return true, if is first state
	 */
	public boolean isFirstState() {

		return lastState == null;

	}

	/**
	 * Checks for next.
	 *
	 * @return true, if successful
	 */
	public boolean hasNext() {
		return nextState != null;
	}

	/**
	 * Reset buttons.
	 */
	public void resetButtons() {
		ReachabilityGraphStateQueue currentState = reachabilityGraphModel.getPetrinetQueue();

		if (!currentState.isFirstState() && toolbarToggleListener != null)
			toolbarToggleListener.onUndoChanged();
		if (currentState.hasNext() && toolbarToggleListener != null)
			toolbarToggleListener.onRedoChanged();

	}

	/**
	 * Rewind.
	 */
	public void rewind() {

		while (!reachabilityGraphModel.getPetrinetQueue().isFirstState())
			goBack();
	}

	/**
	 * Rewind.
	 */
	public void rewindSilent() {

		while (!reachabilityGraphModel.getPetrinetQueue().isFirstState())
			reachabilityGraphModel.setPetrinetQueue(reachabilityGraphModel.getPetrinetQueue().lastState);
	}

	/**
	 * 
	 * @return
	 */
	public boolean isSkippable() {
		return skippable;
	}

	private void print() {
		System.out.println((state == null ? "null" : state.getState()) + ", " + currentEdge + ", " + stateAdded + ", "
				+ (transition == null ? "null" : transition.getId()));
	}

	private void printAll() {
		rewind();
		System.out.println("START");
		do {
			reachabilityGraphModel.getPetrinetQueue().print();
		} while (goForward());

	}

}
