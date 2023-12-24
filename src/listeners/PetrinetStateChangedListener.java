package listeners;

import core.Petrinet;
import core.ReachabilityGraph;
import core.Transition;

/**
 * <p>
 * 
 * The listener interface for receiving events signifying the state of a
 * petrinet has changed.
 * </p>
 * <p>
 * It informs a {@link ReachabilityGraph} that the state of a petrinet has
 * changed.
 * </p>
 */
public interface PetrinetStateChangedListener {

	/**
	 * When a transition has been fired the reachability graph model has to check
	 * whether it needs add a node / edge and set its View to the current state.
	 *
	 * @param transition the transition that has been fired
	 */
	void onTransitionFire(Transition transition);

	/**
	 * When a component of the petrinet has changed the reachability graph model has
	 * to reset itself.
	 *
	 * @param petrinet the new petrinet with changed components
	 */
	void onPetrinetChanged(Petrinet petrinet);

}
