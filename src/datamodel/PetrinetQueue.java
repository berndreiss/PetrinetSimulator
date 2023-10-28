package datamodel;

import java.lang.ModuleLayer.Controller;

import control.PetrinetController;
import util.ToolbarToggleListener;

public class PetrinetQueue {

	private PetrinetState state;
	private Transition transition = null;
	private Added stateAdded = Added.NOTHING;

	private PetrinetQueue lastState = null;
	private PetrinetQueue nextState = null;

	private PetrinetController petrinetController;

	public PetrinetQueue(PetrinetState state, PetrinetController petrinetController) {
		this.state = state;
		this.petrinetController = petrinetController;

	}

	private PetrinetQueue(PetrinetState state, Added stateAdded, Transition transition, PetrinetController petrinetController) {
		this.state = state;
		this.stateAdded = stateAdded;
		this.transition = transition;
		this.petrinetController = petrinetController;
		this.lastState = petrinetController.getPetrinetQueue();
	}

	public PetrinetState getState() {
		return state;
	}

	public Transition getTransition() {
		return transition;
	}

	public Added stateAdded() {
		return stateAdded;
	}

	public void push(PetrinetState state, Added stateAdded, Transition transition) {

		PetrinetQueue currentState = petrinetController.getPetrinetQueue();
		if (currentState.isFirstState() && petrinetController.getToolbarToggleListener() != null) 
				petrinetController.getToolbarToggleListener().onUndoChanged();
		if (currentState.hasNext() && petrinetController.getToolbarToggleListener() != null)
			petrinetController.getToolbarToggleListener().onRedoChanged();

		
		currentState.nextState = new PetrinetQueue(state, stateAdded, transition, petrinetController);
		petrinetController.setPetrinetQueue(currentState.nextState);
	}

	private void print(PetrinetQueue state) {
		System.out.println(getState().getState() + ", Added: " + stateAdded + ", Transition: "
				+ (getTransition() == null ? "null" : getTransition().getId())
				+ (state == petrinetController.getPetrinetQueue() ? " (CURRENT)" : ""));
	}

	// TODO filechanged is not changed before boolean is passed
	// TODO Edge is not added for example 115
	private void printAll() {

//		PetrinetQueue tempState = firstState;
//		while (tempState != null) {
//			tempState.print(tempState);
//			tempState = tempState.nextState;
//		}
//
//		System.out.println("PETRINET: " + petrinetController.getPetrinet().getStateString());
//		petrinetController.getReachabilityGraphModel().print();
//		System.out.println();

//		petrinetController.getReachabilityGraph().print();
	}

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

	public boolean isFirstState() {
		return lastState == null;

	}

	public boolean hasNext() {
		return nextState != null;
	}

	public void resetButtons() {
		PetrinetQueue currentState = petrinetController.getPetrinetQueue();

		if (!currentState.isFirstState() && petrinetController.getToolbarToggleListener() != null)
			petrinetController.getToolbarToggleListener().onUndoChanged();
		if (currentState.hasNext() && petrinetController.getToolbarToggleListener() != null)
			petrinetController.getToolbarToggleListener().onRedoChanged();

	}
}
