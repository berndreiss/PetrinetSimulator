package net.berndreiss.petrinetsimulator.listeners;

import net.berndreiss.petrinetsimulator.core.PetrinetState;
import net.berndreiss.petrinetsimulator.core.Transition;
import net.berndreiss.petrinetsimulator.gui.ReachabilityGraph;

/**
 * <p>
 * The listener interface for receiving events that signify that the state of
 * the reachability graph has changed.
 * </p>
 * <p>
 * It informs the View it needs to be updated.
 * </p>
 */
public interface ReachabilityStateChangedListener {

	/**
	 * The {@link ReachabilityGraph} needs to set a new state to the current state.
	 *
	 * @param state the state which is the new current state
	 */
	void onSetCurrent(PetrinetState state);

	/**
	 * The {@link ReachabilityGraph} needs to mark the starting and ending nodes of
	 * the path signifying that the graph is unbounded.
	 *
	 * @param m       the starting node of the path
	 * @param mMarked the endong node of the path
	 */
	void onMarkUnboundedPath(PetrinetState m, PetrinetState mMarked);

	/**
	 * The {@link ReachabilityGraph} needs to add a new State to it including an
	 * edge from the predecessor with a label signifying the transition that has
	 * been fired.
	 *
	 * @param state       the state to be added
	 * @param predecessor the predecessor of the state to be added
	 * @param transition  the transition that has been fired
	 */
	void onAdd(PetrinetState state, PetrinetState predecessor, Transition transition);

	/**
	 * A state needs to be removed from the {@link ReachabilityGraph}.
	 *
	 * @param state the state to be removed
	 */
	void onRemove(PetrinetState state);

	/**
	 * An edge needs to be removed from the {@link ReachabilityGraph}.
	 *
	 * @param stateSource the source state of the edge to be removed
	 * @param stateTarget the target state of the edge to be removed
	 * @param transition  the transition which has been fired when the edge was
	 *                    added
	 */
	void onRemoveEdge(PetrinetState stateSource, PetrinetState stateTarget, Transition transition);

	/**
	 * 
	 * The {@link ReachabilityGraph} needs to mark the edge as the current edge.
	 * 
	 * @param edge the edge to be marked as the current edge
	 */
	void onSetCurrentEdge(String edge);

	/**
	 * The current edge is being reset.
	 */
	void onResetCurrentEdge();

	/**
	 * Resets the path proving unboundedness in the graph.
	 */
	void onResetPath();

	/**
	 * Adds a state to the path proving unboundedness.
	 * 
	 * @param state state to be added
	 */
	void onAddToPath(PetrinetState state);
}
