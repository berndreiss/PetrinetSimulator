package listeners;

import core.PetrinetElement;
import core.Place;
import core.Transition;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving petrinetComponentChanged events.
 * The class that is interested in processing a petrinetComponentChanged
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addPetrinetComponentChangedListener<code> method. When
 * the petrinetComponentChanged event occurs, that object's appropriate
 * method is invoked.
 *
 * @see PetrinetComponentChangedEvent
 */
public interface PetrinetComponentChangedListener {

	/**
	 * On petrinet element added.
	 *
	 * @param element the element
	 */
	void onPetrinetElementAdded(PetrinetElement element);
	
	/**
	 * On petrinet element set coordinates.
	 *
	 * @param element the element
	 */
	void onPetrinetElementCoordinatesChanged(PetrinetElement element);
	
	/**
	 * On petrinet element removed.
	 *
	 * @param element the element
	 */
	void onPetrinetElementRemoved(PetrinetElement element);
	
	/**
	 * On edge added.
	 *
	 * @param source the source
	 * @param target the target
	 * @param id the id
	 */
	void onEdgeAdded(PetrinetElement source, PetrinetElement target, String id);
	
	/**
	 * On edge removed.
	 *
	 * @param edge the edge
	 */
	void onEdgeRemoved(String edge);
	
	/**
	 * On place token count changed.
	 *
	 * @param place the place
	 */
	void onPlaceTokenCountChanged(Place place);
	
	/**
	 * On set petrinet element name.
	 *
	 * @param element the element
	 */
	void onPetrinetElementLabelChanged(PetrinetElement element);
	
	/**
	 * On transition state changed.
	 *
	 * @param transition the transition
	 */
	void onTransitionStateChanged(Transition transition);
}
