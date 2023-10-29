package listeners;

import core.PetrinetState;
import core.Transition;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving reachabilityStateChange events.
 * The class that is interested in processing a reachabilityStateChange
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addReachabilityStateChangeListener<code> method. When
 * the reachabilityStateChange event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ReachabilityStateChangeEvent
 */
public interface ReachabilityStateChangeListener {

	/**
	 * On set current.
	 *
	 * @param state the state
	 * @param reset the reset
	 */
	void onSetCurrent(PetrinetState state, boolean reset);
	
	/**
	 * On mark invalid.
	 *
	 * @param m the m
	 * @param mMarked the m marked
	 */
	void onMarkInvalid(PetrinetState m, PetrinetState mMarked);
	
	/**
	 * On add.
	 *
	 * @param state the state
	 * @param predecessor the predecessor
	 * @param t the t
	 */
	void onAdd(PetrinetState state, PetrinetState predecessor, Transition t);
	
	/**
	 * On remove.
	 *
	 * @param state the state
	 */
	void onRemove(PetrinetState state);
	
	/**
	 * On remove edge.
	 *
	 * @param stateSource the state source
	 * @param stateTarget the state target
	 * @param t the t
	 */
	void onRemoveEdge(PetrinetState stateSource, PetrinetState stateTarget, Transition t);
}
