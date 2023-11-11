package core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import exceptions.DuplicateIdException;
import exceptions.InvalidEdgeOperationException;
import listeners.PetrinetComponentChangedListener;
import listeners.PetrinetStateChangedListener;
import util.IterableMap;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * Class representing a petrinet.
 * </p>
 * 
 * <p>
 * It contains a set of places and transitions. It keeps track of all ids and
 * ensures no duplicate ids are added. When adding edges it also ensures that
 * they are only added between transitions and places. Since edges are not a
 * data structure on their own it also keeps track of original edge ids (needed
 * for saving).
 * </p>
 */
public class Petrinet {

	/**
	 * A Comparator for sorting the places (stored in a custom TreeMap structure).
	 */
	private static final Comparator<String> TREE_COMPARATOR = String.CASE_INSENSITIVE_ORDER;

	/** Set of transitions. */
	private IterableMap<String, Transition> transitions = new IterableMap<String, Transition>();

	/** Set of places. */
	private IterableMap<String, Place> places = new IterableMap<String, Place>(TREE_COMPARATOR);

	/** The original ids of the edges between transitions and places. */
	private IterableMap<String, String> originalArcIds = new IterableMap<String, String>();

	/**
	 * Listen for changes in the state of the petrinet (happens when transitions are
	 * fired).
	 */
	private PetrinetStateChangedListener petrinetStateChangedListener;

	/**
	 * Listen for changes in components of the petrinet (e.g. when tokens in places
	 * are in-/decremented).
	 */
	private PetrinetComponentChangedListener petrinetComponentChangedListener;

	/**
	 * Sets the petrinet change listener.
	 *
	 * @param petrinetChangedListener The new petrinet change listener.
	 */
	public void setPetrinetChangeListener(PetrinetStateChangedListener petrinetChangedListener) {
		this.petrinetStateChangedListener = petrinetChangedListener;
	}

	/**
	 * Sets the petrinet component changed listener.
	 *
	 * @param petrinetComponentChangedListener The new petrinet component changed
	 *                                         listener.
	 */
	public void setPetrinetComponentChangedListener(PetrinetComponentChangedListener petrinetComponentChangedListener) {
		this.petrinetComponentChangedListener = petrinetComponentChangedListener;
	}

	/**
	 * Gets the activated transitions.
	 *
	 * @return the activated transitions
	 */
	public Iterable<Transition> getActivatedTransitions() {
		ArrayList<Transition> activatedTransitions = new ArrayList<Transition>();

		for (Transition t : getTransitions())
			if (t.isActivated())
				activatedTransitions.add(t);

		return activatedTransitions;
	}

	/**
	 * Calculates (x,y) coordinates right above the left most element in the
	 * petrinet and sets the element that is being added to that coordinates.
	 *
	 * @param petrinetElement The new element that is being added.
	 */
	public void setAddedElementPosition(PetrinetElement petrinetElement) {

		// if there is no element or the element is the only one in the petrinet return
		if (petrinetElement == null || (places.size() + transitions.size()) == 1)
			return;

		// keep track of (x,y)
		double x = Double.MAX_VALUE;
		double y = -Double.MAX_VALUE;

		// go through all places and add all left most
		List<PetrinetElement> mostLeftElements = new ArrayList<PetrinetElement>();
		for (Place p : places) {
			// do not consider the added element itself
			if (p == petrinetElement)
				continue;

			// add place if it has the same x value as the left most element
			if (p.getX() == x)
				mostLeftElements.add(p);

			// if place if further left clear most left elements and add (we got a new max)
			if (p.getX() < x) {
				x = p.getX();
				mostLeftElements.clear();
				mostLeftElements.add(p);
			}

		}

		// do the same as for places above
		for (Transition t : transitions) {

			if (t == petrinetElement)
				continue;

			if (t.getX() == x)
				mostLeftElements.add(t);

			if (t.getX() < x) {
				x = t.getX();
				mostLeftElements.clear();
				mostLeftElements.add(t);
			}

		}

		// from all the left most elements get the upper most
		PetrinetElement leftHightestElement = null;
		for (PetrinetElement p : mostLeftElements) {
			if (p.getY() > y) {
				leftHightestElement = p;
				y = p.getY();
			}
		}
		
		// set added element just above the left upper most element in the petrinet
		setCoordinates(petrinetElement.getId(), leftHightestElement.getX(), leftHightestElement.getY() + 20);

	}

	// turns petrinet element into string and calls containsElementWithId(String id)
	private boolean containsElementWithId(PetrinetElement petrinetElement) {
		return containsElementWithId(petrinetElement.getId());
	}

	/**
	 * Check whether petrinet contains element with id.
	 *
	 * @param id The id which to check.
	 * @return true, if id exists
	 */
	public boolean containsElementWithId(String id) {
		// ^ := lor -> returns true if any of the statements is true
		return places.get(id) != null ^ transitions.get(id) != null ^ originalArcIds.containsValue(id);
	}

	/**
	 * Gets the transitions.
	 *
	 * @return the transitions
	 */
	public Iterable<Transition> getTransitions() {
		return transitions;
	}

	/**
	 * Gets the places.
	 *
	 * @return the places
	 */
	public Iterable<Place> getPlaces() {
		return places;
	}

	/**
	 * Gets a petrinet element. Returns null if element does not exist.
	 *
	 * @param id The id for which to get the petrinet element.
	 * @return the petrinet element corresponding to the id
	 */
	public PetrinetElement getPetrinetElement(String id) {
		PetrinetElement element = places.get(id);
		if (element == null)
			element = transitions.get(id);
		return element;
	}

	/**
	 * Sets the coordinates of an element corresponding to the id.
	 *
	 * @param id The id of the petrinet element for which to set coordinates.
	 * @param x  The x coordinate.
	 * @param y  The y coordinate.
	 */
	public void setCoordinates(String id, double x, double y) {
		PetrinetElement element = getPetrinetElement(id);

		if (element == null)
			return;

		element.setX(x);
		element.setY(y);

		// inform the component changed listener
		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onPetrinetElementCoordinatesChanged(element);

	}

	
	//TODO continue from here
	/**
	 * Removes a petrinet element with the corresponding id. Also removes the id from the orignal arc ids.
	 *
	 * @param id The id of the element to be removed.
	 */
	public void removePetrinetElement(String id) {
		PetrinetElement element = getPetrinetElement(id);

		if (element == null)
			return;

		if (element instanceof Place) {

			Place p = (Place) element;

			ArrayList<Transition> inputs = new ArrayList<Transition>();
			ArrayList<Transition> outputs = new ArrayList<Transition>();

			for (Transition t : p.getInputs())
				inputs.add(t);

			for (Transition t : inputs)
				try {
					removeEdge(t, p);
				} catch (InvalidEdgeOperationException e) {
					System.out.println("Could not remove edge " + t.getId() + p.getId() + " -> " + e.getMessage());
					e.printStackTrace();
				}

			for (Transition t : p.getOutputs())
				outputs.add(t);

			for (Transition t : outputs)
				try {
					removeEdge(p, t);
				} catch (InvalidEdgeOperationException e) {
					System.out.println("Could not remove edge " + p.getId() + t.getId() + " -> " + e.getMessage());
					e.printStackTrace();
				}

			places.remove(p.getId());
		}
		if (element instanceof Transition) {
			Transition t = (Transition) element;
			ArrayList<Place> inputs = new ArrayList<Place>();
			ArrayList<Place> outputs = new ArrayList<Place>();

			for (Place p : t.getInputs())
				inputs.add(p);

			for (Place p : inputs)
				try {
					removeEdge(p, t);
				} catch (InvalidEdgeOperationException e) {
					System.out.println("Could not remove edge " + p.getId() + t.getId() + " -> " + e.getMessage());
					e.printStackTrace();
				}

			for (Place p : t.getOutputs())
				outputs.add(p);

			for (Place p : outputs)
				try {
					removeEdge(t, p);
				} catch (InvalidEdgeOperationException e) {
					System.out.println("Could not remove edge " + t.getId() + p.getId() + " -> " + e.getMessage());
					e.printStackTrace();
				}

			transitions.remove(t.getId());

		}

		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onComponentChanged(places.size() > 0 ? this : null);// removes initialstate
																								// from
																								// ReachabilityGraph
		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onPetrinetElementRemoved(element);

	}

	/**
	 * Adds the transition.
	 *
	 * @param id the id
	 * @return the transition
	 * @throws DuplicateIdException the duplicate id exception
	 */
	public Transition addTransition(String id) throws DuplicateIdException {

		if (id == null)
			return null;

		if (transitions.containsKey(id) || places.containsKey(id))
			throw new DuplicateIdException("Duplicate ID: place \"" + id + "\" already exists.");

		Transition t = new Transition(id);

		transitions.put(id, t);

		t.setTransitionStateListener(() -> {
			if (petrinetComponentChangedListener != null)
				petrinetComponentChangedListener.onTransitionStateChanged(t);

		});

		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onPetrinetElementAdded(t);

		return t;
	}

	/**
	 * Adds the place.
	 *
	 * @param id the id
	 * @return the place
	 * @throws DuplicateIdException the duplicate id exception
	 */
	public Place addPlace(String id) throws DuplicateIdException {

		if (id == null)
			return null;

		if (transitions.containsKey(id) || places.containsKey(id))
			throw new DuplicateIdException("Duplicate ID: place \"" + id + "\" already exists.");
		Place p = new Place(id);
		places.put(id, p);
		p.setNumberOfTokensListener(newNumber -> {
			if (petrinetComponentChangedListener != null)
				petrinetComponentChangedListener.onPlaceTokenCountChanged(p);

		});

		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onComponentChanged(this);
		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onPetrinetElementAdded(p);
		return p;
	}

	/**
	 * Sets the tokens.
	 *
	 * @param id             the id
	 * @param numberOfTokens the number of tokens
	 */
	void setTokens(String id, int numberOfTokens) {
		if (!places.containsKey(id))
			return;
		Place p = places.get(id);

		if (p.getNumberOfTokens() == numberOfTokens)
			return;

		p.setNumberOfTokens(numberOfTokens);

		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onComponentChanged(this);

	}

	/**
	 * Adds the edge.
	 *
	 * @param source the source
	 * @param target the target
	 * @param id     the id
	 * @throws InvalidEdgeOperationException the invalid edge operation exception
	 * @throws DuplicateIdException
	 */
	public void addEdge(String source, String target, String id)
			throws InvalidEdgeOperationException, DuplicateIdException {

		PetrinetElement sourceElement = getPetrinetElement(source);

		if (sourceElement == null)
			throw new InvalidEdgeOperationException("Invalid edge operation: Source \"" + source + "\" is missing.");

		PetrinetElement targetElement = getPetrinetElement(target);

		if (targetElement == null || !containsElementWithId(target))
			throw new InvalidEdgeOperationException("Invalid edge operation: Target \"" + target + "\" is missing.");

		addEdge(sourceElement, targetElement, id);
	}

	/**
	 * Adds the edge.
	 *
	 * @param source the source
	 * @param target the target
	 * @param id     the id
	 * @throws InvalidEdgeOperationException the invalid edge operation exception
	 * @throws DuplicateIdException
	 */
	public void addEdge(PetrinetElement source, PetrinetElement target, String id)
			throws InvalidEdgeOperationException, DuplicateIdException {

		if (containsElementWithId(id))
			throw new DuplicateIdException("Invalid edge operation: id \"" + id + "\" already exists.");

		if (source == null || !containsElementWithId(source))
			throw new InvalidEdgeOperationException("Invalid edge operation: Source" + source == null ? ""
					: " \"" + source.getId() + "\"" + " is missing.");

		if (target == null || !containsElementWithId(target))
			throw new InvalidEdgeOperationException("Invalid edge operation: Target " + target == null ? ""
					: " \"" + target.getId() + "\"" + " is missing.");

		if ((source instanceof Place && target instanceof Place)
				|| (source instanceof Transition && target instanceof Transition))
			throw new InvalidEdgeOperationException("Invalid edge operation for given elements (" + source.getId()
					+ ", " + target.getId() + "): cannot connect two places or two transitions.");

		if (originalArcIds.containsKey(source.getId() + target.getId()))
			throw new InvalidEdgeOperationException("Invalid edge operation: Edge already exists.");

		originalArcIds.put(source.getId() + target.getId(), id);

		if (source instanceof Transition)
			((Transition) source).addOutput((Place) target);
		else
			((Transition) target).addInput((Place) source);

		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onEdgeAdded(source, target, id);
		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onComponentChanged(this);
	}

	/**
	 * Removes the edge.
	 *
	 * @param source the source
	 * @param target the target
	 * @throws InvalidEdgeOperationException the invalid edge operation exception
	 */
	public void removeEdge(PetrinetElement source, PetrinetElement target) throws InvalidEdgeOperationException {

		if (originalArcIds.get(source.getId() + target.getId()) == null)
			throw new InvalidEdgeOperationException("Invalid edge operation: Edge does not exist.");

		if (source instanceof Place) {
			Place place = (Place) source;
			Transition transition = (Transition) target;
			place.removeOutput(transition);
			transition.removeInput(place);
		} else {
			Place place = (Place) target;
			Transition transition = (Transition) source;
			place.removeInput(transition);
			transition.removeOutput(place);

		}

		String key = source.getId() + target.getId();

		originalArcIds.remove(key);

		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onEdgeRemoved(source, target);
		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onComponentChanged(this);

	}

	private boolean isTransition(String id) {
		if (transitions.containsKey(id))
			return true;
		return false;
	}

	/**
	 * Fire transition.
	 *
	 * @param id the id
	 */
	public void fireTransition(String id, boolean skippable) {
		if (!isTransition(id))
			return;

		Transition t = transitions.get(id);

		if (t == null)
			return;

		boolean fired = t.fire();

		if (!fired)
			return;
		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onTransitionFire(t);

	}

	/**
	 * Gets the original arc id.
	 *
	 * @param arcId the arc id
	 * @return the original arc id
	 */
	public String getOriginalArcId(String arcId) {
		return originalArcIds.get(arcId);
	}

	/**
	 * Sets the state.
	 *
	 * @param state the new state
	 */
	public void setState(PetrinetState state) {

		if (state == null)
			return;

		Iterator<Integer> integerIt = state.getPlaceTokens();

		if (places.size() == state.placeTokensSize()) {
			for (Place p : places) {
				p.setNumberOfTokens(integerIt.next());
			}

		}

	}

	/**
	 * Prints the Petrinet to the terminal. Mainly for debugging purposes.
	 */
	public void print() {
		System.out.println("Places:");
		for (String s : places.keySet()) {
			Place p = places.get(s);
			System.out.println(p.getId() + ", " + p.getName() + ", " + p.getNumberOfTokens());
		}
		System.out.println("Transitions:");
		for (String s : transitions.keySet()) {
			Transition t = transitions.get(s);
			System.out.println(t.getId() + ", " + t.getName());
			for (Place p : t.getInputs()) {
				System.out.println(p.getId() + " " + p.getNumberOfTokens());
			}
		}

	}

	/**
	 * Increment place.
	 *
	 * @param markedPlace the marked place
	 * @return true, if successful
	 */
	public boolean incrementPlace(PetrinetElement markedPlace) {
		if (markedPlace == null)
			return false;
		if (!places.containsKey(markedPlace.getId()))// should not happen -> here for safety reasons
			return false;

		Place p = (Place) markedPlace;
		p.incrementTokens();

		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onComponentChanged(this);

		return true;
	}

	/**
	 * Decrement place.
	 *
	 * @param markedPlace the marked place
	 * @return true, if successful
	 */
	public boolean decrementPlace(PetrinetElement markedPlace) {
		if (markedPlace == null)
			return false;

		if (!places.containsKey(markedPlace.getId()))// should not happen -> here for safety reasons
			return false;

		Place p = (Place) markedPlace;
		boolean decremented = p.decrementTokens();
		if (!decremented)
			return false;
		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onComponentChanged(this);

		return true;
	}

	/**
	 * Gets the state string.
	 *
	 * @return the state string
	 */
	public String getStateString() {
		StringBuilder sb = new StringBuilder();

		sb.append("(");

		for (Place p : getPlaces()) {
			sb.append(p.getNumberOfTokens() + "|");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Sets the petrinet element name.
	 *
	 * @param id   the id
	 * @param name the name
	 */
	public void setPetrinetElementName(String id, String name) {
		PetrinetElement element = getPetrinetElement(id);
		element.setName(name);
		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onPetrinetElementLabelChanged(element);

	}

	/**
	 * Checks for places.
	 *
	 * @return true, if successful
	 */
	boolean hasPlaces() {

		return places.size() > 0;
	}

}
