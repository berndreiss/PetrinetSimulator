package core;


import control.PetrinetController;

// TODO: Auto-generated Javadoc
/**
 * The Class PetrinetQueue.
 */
public class PetrinetQueue {

	private PetrinetState state;
	private Transition transition = null;
	private Added stateAdded = Added.NOTHING;

	private PetrinetQueue lastState = null;
	private PetrinetQueue nextState = null;

	private PetrinetController petrinetController;

	/**
	 * Instantiates a new petrinet queue.
	 *
	 * @param state the state
	 * @param petrinetController the petrinet controller
	 */
	public PetrinetQueue(PetrinetState state, PetrinetController petrinetController) {
		this.state = state;
		this.petrinetController = petrinetController;

	}

	private PetrinetQueue(PetrinetState state, Added stateAdded, Transition transition,
			PetrinetController petrinetController) {
		this.state = state;
		this.stateAdded = stateAdded;
		this.transition = transition;
		this.petrinetController = petrinetController;
		this.lastState = petrinetController.getPetrinetQueue();
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
	 * @param state the state
	 * @param stateAdded the state added
	 * @param transition the transition
	 */
	public void push(PetrinetState state, Added stateAdded, Transition transition) {

		PetrinetQueue currentState = petrinetController.getPetrinetQueue();
		if (currentState.isFirstState() && petrinetController.getToolbarToggleListener() != null)
			petrinetController.getToolbarToggleListener().onUndoChanged();
		if (currentState.hasNext() && petrinetController.getToolbarToggleListener() != null)
			petrinetController.getToolbarToggleListener().onRedoChanged();

		currentState.nextState = new PetrinetQueue(state, stateAdded, transition, petrinetController);
		petrinetController.setPetrinetQueue(currentState.nextState);
	}


	/**
	 * Go back.
	 */
	public void goBack() {
		PetrinetQueue currentState = petrinetController.getPetrinetQueue();
		if (currentState.isFirstState())
			return;

		if (!currentState.hasNext())
			petrinetController.getToolbarToggleListener().onRedoChanged();

		if (currentState.stateAdded == Added.STATE) {
			petrinetController.getReachabilityGraphModel().removeState(currentState.getState());
		} else if (currentState.stateAdded == Added.EDGE) {
			petrinetController.getReachabilityGraphModel().removeEdge(currentState.lastState.getState(),
					currentState.getState(), currentState.getTransition());
		}

		petrinetController.setPetrinetQueue(currentState.lastState);

		petrinetController.getPetrinet().setState(currentState.lastState.getState());
		petrinetController.getReachabilityGraphModel().setCurrentState(currentState.lastState.getState());

		if (currentState.lastState.isFirstState() && petrinetController.getToolbarToggleListener() != null)
			petrinetController.getToolbarToggleListener().onUndoChanged();

	}

	/**
	 * Go forward.
	 */
	public void goForward() {

		PetrinetQueue currentState = petrinetController.getPetrinetQueue();

		if (!currentState.hasNext())
			return;

		if (currentState.isFirstState())
			petrinetController.getToolbarToggleListener().onUndoChanged();

		petrinetController.setPetrinetQueue(currentState.nextState);

		currentState = currentState.nextState;
		Petrinet petrinet = petrinetController.getPetrinet();
		ReachabilityGraphModel reachabilityGraphModel = petrinetController.getReachabilityGraphModel();
		if (currentState.stateAdded() != Added.NOTHING) {

			petrinet.setState(currentState.getState());

			reachabilityGraphModel.addNewState(petrinet, currentState.getTransition());
			currentState.state = reachabilityGraphModel.getState(petrinet.getStateString());// since a new instance has
																							// been created, the state
																							// has to be updated
		} else {
			petrinet.setState(currentState.getState());
			reachabilityGraphModel.setCurrentState(currentState.getState());
		}

		if (!currentState.hasNext())
			petrinetController.getToolbarToggleListener().onRedoChanged();

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
		PetrinetQueue currentState = petrinetController.getPetrinetQueue();

		if (!currentState.isFirstState() && petrinetController.getToolbarToggleListener() != null)
			petrinetController.getToolbarToggleListener().onUndoChanged();
		if (currentState.hasNext() && petrinetController.getToolbarToggleListener() != null)
			petrinetController.getToolbarToggleListener().onRedoChanged();

	}

	/**
	 * Rewind.
	 */
	public void rewind() {

		if (petrinetController.getPetrinetQueue().lastState == null)
			return;

		if (petrinetController.getPetrinetQueue().nextState == null)
			if (petrinetController.getToolbarToggleListener() != null)
				petrinetController.getToolbarToggleListener().onRedoChanged();
			
		while (petrinetController.getPetrinetQueue().lastState != null)
			petrinetController.setPetrinetQueue(petrinetController.getPetrinetQueue().lastState);

		if (petrinetController.getToolbarToggleListener() != null)
			petrinetController.getToolbarToggleListener().onUndoChanged();
	}
}
