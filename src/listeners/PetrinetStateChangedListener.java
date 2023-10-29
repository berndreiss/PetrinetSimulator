package listeners;

import core.Petrinet;
import core.Transition;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving petrinetStateChanged events.
 * The class that is interested in processing a petrinetStateChanged
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addPetrinetStateChangedListener<code> method. When
 * the petrinetStateChanged event occurs, that object's appropriate
 * method is invoked.
 *
 * @see PetrinetStateChangedEvent
 */
public interface PetrinetStateChangedListener {
	
	/**
	 * On transition fire.
	 *
	 * @param t the t
	 */
	void onTransitionFire(Transition t);
	
	/**
	 * On component changed.
	 *
	 * @param petrinet the petrinet
	 */
	void onComponentChanged(Petrinet petrinet);
}
