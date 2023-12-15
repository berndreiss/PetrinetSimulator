package listeners;

import core.PetrinetState;
import core.Transition;

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
	 * The View needs to set a new state to the current state.
	 *
	 * @param state The state which is the new current state.
	 */
	void onSetCurrent(PetrinetState state);

	/**
	 * The View needs to mark the starting and ending nodes of the path signifying
	 * that the graph is unbounded.
	 *
	 * @param m       The starting node of the path.
	 * @param mMarked The endong node of the path.
	 */
	void onMarkUnboundedPath(PetrinetState m, PetrinetState mMarked);

	/**
	 * The View needs to add a new State to it including an edge from the
	 * predecessor with a label signifying the transition that has been fired.
	 *
	 * @param state       The state to be added.
	 * @param predecessor The predecessor of the state to be added.
	 * @param transition  The transition that has been fired.
	 */
	void onAdd(PetrinetState state, PetrinetState predecessor, Transition transition);

	/**
	 * A state needs to be removed from the View.
	 *
	 * @param state The state to be removed.
	 */
	void onRemove(PetrinetState state);

	/**
	 * An edge needs to be removed from the View.
	 *
	 * @param stateSource The source state of the edge to be removed.
	 * @param stateTarget The target state of the edge to be removed.
	 * @param transition  The transition which has been fired when the edge was
	 *                    added.
	 */
	void onRemoveEdge(PetrinetState stateSource, PetrinetState stateTarget, Transition transition);

	/**
	 * 
	 * The View needs to mark the edge as the current edge.
	 * 
	 * @param edge The edge to be marked as the current edge.
	 */
	void onSetCurrentEdge(String edge);

	/**
	 * The current edge is being reset.
	 */
	void onResetCurrentEdge();
	
	/**
	 * Resets the path in the graph.
	 */
	void onResetPath();

	/**
	 * Add a state to the path.
	 */
	void onAddToPath(PetrinetState state);
}
