package net.berndreiss.petrinetsimulator.listeners;

import net.berndreiss.petrinetsimulator.core.Petrinet;
import net.berndreiss.petrinetsimulator.core.ReachabilityGraphModel;
import net.berndreiss.petrinetsimulator.core.Transition;
import net.berndreiss.petrinetsimulator.gui.ReachabilityGraph;

/**
 * <p>
 * 
 * The listener interface for receiving events signifying the state of a
 * petrinet has changed.
 * </p>
 * <p>
 * It informs a {@link ReachabilityGraphModel} that the state of a petrinet has
 * changed.
 * </p>
 */
public interface PetrinetStateChangedListener {

	/**
	 * When a transition has been fired the {@link ReachabilityGraphModel} has to
	 * check whether it needs to add a node / edge and set its
	 * {@link ReachabilityGraph} to the current state.
	 *
	 * @param transition the transition that has been fired
	 */
	void onTransitionFire(Transition transition);

	/**
	 * When a component of the petrinet has changed the {@link ReachabilityGraphModel} has
	 * to reset itself.
	 *
	 * @param petrinet the new petrinet with changed components
	 */
	void onPetrinetChanged(Petrinet petrinet);

}
