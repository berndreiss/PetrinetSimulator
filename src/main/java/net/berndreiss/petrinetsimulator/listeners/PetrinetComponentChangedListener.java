package net.berndreiss.petrinetsimulator.listeners;

import net.berndreiss.petrinetsimulator.core.PetrinetElement;
import net.berndreiss.petrinetsimulator.core.Place;
import net.berndreiss.petrinetsimulator.core.Transition;
import net.berndreiss.petrinetsimulator.gui.PetrinetGraph;

/**
 * <p>
 * The listener interface for receiving events signifying components of a
 * petrinet have changed.
 * </p>
 *
 * <p>
 * It provides methods for informing the {@link PetrinetGraph} that the data
 * model of the petrinet has changed and therefore has to be updated.
 * </p>
 */
public interface PetrinetComponentChangedListener {

	/**
	 * When an element has been added it should be added to the
	 * {@link PetrinetGraph} also.
	 *
	 * @param element the element to be added
	 */
	void onPetrinetElementAdded(PetrinetElement element);

	/**
	 * When coordinates are set for an element they should be updated in the
	 * {@link PetrinetGraph} also.
	 *
	 * @param element the element for which coordinates need to be reset
	 */
	void onPetrinetElementCoordinatesChanged(PetrinetElement element);

	/**
	 * When an element is removed it should be removed from the
	 * {@link PetrinetGraph} also.
	 *
	 * @param element the element to be removed
	 */
	void onPetrinetElementRemoved(PetrinetElement element);

	/**
	 * When an edge is added it should be added in the {@link PetrinetGraph} also.
	 *
	 * @param source the source element of the edge
	 * @param target the target element of the edge
	 * @param id     the id of the edge
	 */
	void onEdgeAdded(PetrinetElement source, PetrinetElement target, String id);

	/**
	 * When an edge is removed it should be removed from the {@link PetrinetGraph}
	 * also.
	 * 
	 * @param source the source element for the edge to be removed
	 * @param target the targets element for the edge to be removed
	 */
	void onEdgeRemoved(PetrinetElement source, PetrinetElement target);

	/**
	 * When the count of tokens of a place has changed it needs to be changed in the
	 * {@link PetrinetGraph} also.
	 *
	 * @param place the place for which the number of tokens needs to be updated
	 */
	void onPlaceTokenCountChanged(Place place);

	/**
	 * When a label for an element is changed it needs to be changed in the
	 * {@link PetrinetGraph} also.
	 *
	 * @param element the element for which the label needs to be changed
	 */
	void onPetrinetElementLabelChanged(PetrinetElement element);

	/**
	 * When a transition is (de-)activated the {@link PetrinetGraph} needs to be
	 * updated.
	 *
	 * @param transition the transition for which the state has changed
	 */
	void onTransitionStateChanged(Transition transition);
}
