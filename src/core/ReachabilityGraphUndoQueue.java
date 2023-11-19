package core;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import listeners.ToolbarToggleListener;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * Class representing a queue
 * </p>
 * The Class PetrinetQueue.
 */
public class ReachabilityGraphUndoQueue {


	/** The reachability graph. */
	private ReachabilityGraph reachabilityGraph;
	/** The listener for the toolbar buttons. */
	private ToolbarToggleListener toolbarToggleListener;
	/** */
	private ReachabilityGraphUndoQueueState currentState = null;
	
	/**
	 * Instantiates a new undo queue.
	 *
	 * @param reachabilityGraph     The reachability graph for which this queue is
	 *                              used.
	 * @param toolbarToggleListener The listener for the toolbar buttons.
	 */
	public ReachabilityGraphUndoQueue(ReachabilityGraph reachabilityGraph,
			ToolbarToggleListener toolbarToggleListener) {
		this.reachabilityGraph = reachabilityGraph;
		this.toolbarToggleListener = toolbarToggleListener;

	}


	/**
	 * 
	 * @param state
	 * @param edge
	 * @param stateAdded
	 * @param transition
	 * @param skippable
	 */
	public void push(PetrinetState state, String edge, AddedType stateAdded, Transition transition,
			boolean skippable) {

//		System.out.println("PUSHING " + state.getState() + ", " + currentEdge + ", " + stateAdded + ", "
//				+ (transition == null ? "null" : transition.getId()) + ", " + skippable);
		
			ReachabilityGraphUndoQueueState newState = new ReachabilityGraphUndoQueueState(currentState, state, edge, stateAdded, transition, skippable);
		
			if (currentState == null && toolbarToggleListener != null)
				toolbarToggleListener.onUndoChanged();

			

			if (currentState != null) {
				currentState.setNextState(newState);
				if (currentState.hasNext() && toolbarToggleListener != null)
					toolbarToggleListener.onRedoChanged();
			}
			currentState = newState;

	}

	/**
	 * Go back.
	 */
	public void goBack() {
		System.out.println("GOING BACK...");
		reachabilityGraph.setPushing(false);

		// TODO example 177 Analyse graph -> undo -> redo -> undo REMOVESTATE EDGE ->
		// elementNotFoundException
		// ALSO example 118 -> "(1|1)(2|0)t2" not found
		if (currentState.isFirst())
			return;

		if (!currentState.hasNext() && toolbarToggleListener != null)
			toolbarToggleListener.onRedoChanged();

		if (currentState.getAdded() == AddedType.STATE) {
			reachabilityGraph.removeState(currentState.getState());
		} else if (currentState.getAdded() == AddedType.EDGE) {
			reachabilityGraph.removeEdge(currentState.getLast().getState(), currentState.getState(),
					currentState.getTransition());
		}

		// change current state
		currentState = currentState.getLast();
		reachabilityGraph.getPetrinet().setState(currentState.getState());
		reachabilityGraph.setCurrentState(currentState.getState());
		reachabilityGraph.setCurrentEdge(currentState.getEdge());

		if (currentState.isFirst() && toolbarToggleListener != null)
			toolbarToggleListener.onUndoChanged();

		if (currentState.isSkippable())
			goBack();
		System.out
				.println(currentState.getState() + ", " + currentState.getEdge() + ", " + currentState.getAdded()
						+ ", " + (currentState.getTransition() == null ? "null" : currentState.getTransition().getId())
						+ ", " + currentState.isSkippable());
		// printAll();
		reachabilityGraph.setPushing(true);

	}


	/**
	 * Go forward.
	 */
	public boolean goForward() {

		if (!currentState.hasNext())
			return false;
		reachabilityGraph.setPushing(false);

		if (currentState.isFirst() && toolbarToggleListener != null)
			toolbarToggleListener.onUndoChanged();

		currentState = currentState.getNext();

		System.out.println("CURRENT STATE: " + currentState.getState().getState());

		Petrinet petrinet = reachabilityGraph.getPetrinet();

		petrinet.setState(currentState.getState());

		System.out.println("PETRINET: " + petrinet.getStateString());
		if (currentState.stateAdded() != AddedType.NOTHING) {
			System.out.println("TRANSITION: " + currentState.getTransition().getId());
			reachabilityGraph.addNewState(petrinet, currentState.getTransition());
		} else
			reachabilityGraph.setCurrentState(currentState.getState());

		System.out.println();
		if (!currentState.hasNext() && toolbarToggleListener != null)
			toolbarToggleListener.onRedoChanged();

		if (currentState.isSkippable())
			goForward();
		reachabilityGraph.setPushing(true);

		return true;
	}


	/**
	 * Reset buttons.
	 */
	public void resetButtons() {

		if (!currentState.isFirst() && toolbarToggleListener != null)
			toolbarToggleListener.onUndoChanged();
		if (currentState.hasNext() && toolbarToggleListener != null)
			toolbarToggleListener.onRedoChanged();

	}

	/**
	 * Rewind.
	 */
	public void rewind() {

		while (!currentState.isFirst())
			goBack();
	}

	/**
	 * Rewind.
	 */
	public void rewindSilent() {

		while (!currentState.isFirst())
			currentState = currentState.getLast();

		reachabilityGraph.setPushing(false);

		reachabilityGraph.getPetrinet().setState(currentState.getState());
		reachabilityGraph.setCurrentState(currentState.getState());
		reachabilityGraph.setCurrentEdge(currentState.getEdge());

		reachabilityGraph.setPushing(true);

	}

	

	private void print() {
//		System.out.println((state == null ? "null" : state.getState()) + ", " + currentEdge + ", " + stateAdded + ", "
//				+ (transition == null ? "null" : transition.getId()));
	}

	private void printAll() {
		rewind();
		System.out.println("START");
		do {
			reachabilityGraph.getUndoQueue().print();
		} while (goForward());

	}


	public ReachabilityGraphUndoQueueState getCurrentState() {
		return currentState;
	}

}
