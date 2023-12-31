package core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import exceptions.DuplicateIdException;
import exceptions.InvalidEdgeOperationException;
import exceptions.PetrinetException;
import gui.PetrinetGraph;
import listeners.PetrinetComponentChangedListener;
import listeners.PetrinetStateChangedListener;
import util.IterableMap;

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
 * 
 * <p>
 * Listeners serve as a means for communicating with the
 * {@link ReachabilityGraph} ({@link PetrinetStateChangedListener}) and the
 * {@link PetrinetGraph} ({@link PetrinetComponentChangedListener}). For more
 * information on the listeners also refer to ProgramDocumentation.pdf.
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
	 * Listens for changes in the state of the petrinet (happens when transitions
	 * are fired).
	 */
	private PetrinetStateChangedListener petrinetStateChangedListener;

	/**
	 * Listens for changes in components of the petrinet (e.g. when tokens in places
	 * are in-/decremented).
	 */
	private PetrinetComponentChangedListener petrinetComponentChangedListener;

	/**
	 * Sets the petrinet change listener.
	 *
	 * @param petrinetChangedListener the new petrinet change listener
	 */
	public void setPetrinetChangeListener(PetrinetStateChangedListener petrinetChangedListener) {
		this.petrinetStateChangedListener = petrinetChangedListener;
	}

	/**
	 * Sets the petrinet component changed listener.
	 *
	 * @param petrinetComponentChangedListener the new petrinet component changed
	 *                                         listener
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
	 * petrinet and sets the element that is being added to these coordinates.
	 *
	 * @param petrinetElement the new element that is being added
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
	 * Checks whether petrinet contains element with id.
	 *
	 * @param id the id which to check
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
	 * @param id the id for which to get the petrinet element
	 * @return the petrinet element corresponding to the id, null if not found
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
	 * @param id the id of the petrinet element for which to set coordinates
	 * @param x  the x coordinate
	 * @param y  the y coordinate
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

	/**
	 * Removes a petrinet element with the corresponding id. Also removes the id
	 * from the orignal arc ids.
	 *
	 * @param id the id of the element to be removed
	 */
	public void removePetrinetElement(String id) {
		PetrinetElement element = getPetrinetElement(id);

		if (element == null)
			return;

		// handle place
		if (element instanceof Place) {

			Place p = (Place) element;

			// extra lists because inputs and outputs are modified when removing edge (see
			// removeEdge())
			ArrayList<Transition> inputs = new ArrayList<Transition>();
			ArrayList<Transition> outputs = new ArrayList<Transition>();

			// handle inputs
			for (Transition t : p.getInputs())
				inputs.add(t);
			for (Transition t : inputs)
				try {
					removeEdge(t, p);
				} catch (InvalidEdgeOperationException e) {
					System.out.println("Could not remove edge " + t.getId() + p.getId() + " -> " + e.getMessage());
					e.printStackTrace();
				}

			// handle outputs
			for (Transition t : p.getOutputs())
				outputs.add(t);
			for (Transition t : outputs)
				try {
					removeEdge(p, t);
				} catch (InvalidEdgeOperationException e) {
					System.out.println("Could not remove edge " + p.getId() + t.getId() + " -> " + e.getMessage());
					e.printStackTrace();
				}

			// remove place
			places.remove(p.getId());
		}

		// handle transition
		if (element instanceof Transition) {
			Transition t = (Transition) element;

			// extra lists because inputs and outputs are modified when removing edge (see
			// removeEdge())
			ArrayList<Place> inputs = new ArrayList<Place>();
			ArrayList<Place> outputs = new ArrayList<Place>();

			// handle inputs
			for (Place p : t.getInputs())
				inputs.add(p);

			for (Place p : inputs)
				try {
					removeEdge(p, t);
				} catch (InvalidEdgeOperationException e) {
					System.out.println("Could not remove edge " + p.getId() + t.getId() + " -> " + e.getMessage());
					e.printStackTrace();
				}

			// handle outputs
			for (Place p : t.getOutputs())
				outputs.add(p);

			for (Place p : outputs)
				try {
					removeEdge(t, p);
				} catch (InvalidEdgeOperationException e) {
					System.out.println("Could not remove edge " + t.getId() + p.getId() + " -> " + e.getMessage());
					e.printStackTrace();
				}

			// remove transition
			transitions.remove(t.getId());

		}

		// inform listeners
		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onPetrinetChanged(places.size() > 0 ? this : null);
		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onPetrinetElementRemoved(element);

	}

	/**
	 * Adds a transition.
	 *
	 * @param id the id of the transition
	 * @return the transition
	 * @throws DuplicateIdException thrown if id already exists
	 */
	public Transition addTransition(String id) throws DuplicateIdException {

		if (id == null)
			return null;

		if (containsElementWithId(id))
			throw new DuplicateIdException("Duplicate ID: place \"" + id + "\" already exists.");

		Transition t = new Transition(id);

		transitions.put(id, t);

		// link the petrinet component changed listener to the transition state changed
		// listener
		t.setTransitionStateListener(() -> {
			if (petrinetComponentChangedListener != null)
				petrinetComponentChangedListener.onTransitionStateChanged(t);

		});

		// inform component changed listener.
		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onPetrinetElementAdded(t);

		return t;
	}

	/**
	 * Adds a place.
	 *
	 * @param id the id of the place
	 * @return the place
	 * @throws DuplicateIdException thrown if id already exists
	 */
	public Place addPlace(String id) throws DuplicateIdException {

		if (id == null)
			return null;

		if (containsElementWithId(id))
			throw new DuplicateIdException("Duplicate ID: place \"" + id + "\" already exists.");

		Place p = new Place(id);
		places.put(id, p);

		// link the component and state changed listener to the place token count
		// changed listener
		p.setNumberOfTokensListener(newNumber -> {

			if (petrinetComponentChangedListener != null)
				petrinetComponentChangedListener.onPlaceTokenCountChanged(p);

		});

		// inform the listener that the state of the petrinet has changed
		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onPetrinetChanged(this);

		// inform the listener that an element has changed
		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onPetrinetElementAdded(p);
		return p;
	}

	/**
	 * Sets the number of tokens for the place with the id provided.
	 *
	 * @param id             the id of the place
	 * @param numberOfTokens the number of tokens to be set
	 * @throws PetrinetException thrown if number of tokens is negative
	 */
	void setTokens(String id, int numberOfTokens) throws PetrinetException {

		// abort if place does not exist
		if (!places.containsKey(id))
			return;

		if (numberOfTokens < 0)
			throw new PetrinetException("number of tokens has to be positive.");

		Place p = places.get(id);

		// if number of tokens is the same as before abort
		if (p.getNumberOfTokens() == numberOfTokens)
			return;

		p.setNumberOfTokens(numberOfTokens);

		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onPetrinetChanged(this);

	}

	/**
	 * Adds an edge.
	 *
	 * @param source the source of the edge
	 * @param target the target of the edge
	 * @param id     the id for the edge
	 * @throws InvalidEdgeOperationException thrown if source or target do not
	 *                                       exist, if both source and target are
	 *                                       transitions/places or if the edge
	 *                                       already exists
	 * @throws DuplicateIdException          thrown if id already exists
	 */
	void addEdge(String source, String target, String id) throws InvalidEdgeOperationException, DuplicateIdException {

		PetrinetElement sourceElement = getPetrinetElement(source);

		if (sourceElement == null)
			throw new InvalidEdgeOperationException("Invalid edge operation: Source \"" + source + "\" is missing.");

		PetrinetElement targetElement = getPetrinetElement(target);

		if (targetElement == null || !containsElementWithId(target))
			throw new InvalidEdgeOperationException("Invalid edge operation: Target \"" + target + "\" is missing.");

		addEdge(sourceElement, targetElement, id);
	}

	/**
	 * Adds an edge.
	 *
	 * @param source the source of the edge
	 * @param target the target of the edge
	 * @param id     the id for the edge
	 * @throws InvalidEdgeOperationException thrown if source or target do not
	 *                                       exist, if both source and target are
	 *                                       transitions/places or if the edge
	 *                                       already exists
	 * @throws DuplicateIdException          thrown if id already exists
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

		// add edge
		originalArcIds.put(source.getId() + target.getId(), id);
		if (source instanceof Transition)
			((Transition) source).addOutput((Place) target);
		else
			((Transition) target).addInput((Place) source);

		// inform listeners
		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onEdgeAdded(source, target, id);
		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onPetrinetChanged(this);
	}

	/**
	 * Removes an edge with the given source and target.
	 *
	 * @param source the source of the edge
	 * @param target the target of the edge
	 * @throws InvalidEdgeOperationException gets thrown if edge does not exist
	 */
	public void removeEdge(PetrinetElement source, PetrinetElement target) throws InvalidEdgeOperationException {

		// if edge does not exist throw exception
		if (originalArcIds.get(source.getId() + target.getId()) == null)
			throw new InvalidEdgeOperationException("Invalid edge operation: Edge does not exist.");

		// handle cases: either source or target can be of type place
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

		// remove original arc id
		String key = source.getId() + target.getId();
		originalArcIds.remove(key);

		// inform listeners
		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onEdgeRemoved(source, target);
		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onPetrinetChanged(this);

	}

	// checks if id is whithin transitions
	private boolean isTransition(String id) {
		if (transitions.containsKey(id))
			return true;
		return false;
	}

	/**
	 * Fires transition if it is activated.
	 *
	 * @param id the id of the transition to be fired
	 */
	public void fireTransition(String id) {

		// if there is no transition with given id abort
		if (!isTransition(id))
			return;

		Transition t = transitions.get(id);

		// safety measure
		if (t == null)
			return;

		boolean fired = t.fire();

		// if transition was not activated and thus has not been fired return
		if (!fired)
			return;

		// inform the state changed listener
		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onTransitionFire(t);

	}

	/**
	 * Gets the original arc id from the edge id.
	 *
	 * @param edgeId the id of the edge (sourceId + targetId)
	 * @return the original arc id
	 */
	public String getOriginalArcId(String edgeId) {
		return originalArcIds.get(edgeId);
	}

	/**
	 * Sets petrinet to the given state.
	 *
	 * @param state the new state
	 */
	public void setState(PetrinetState state) {

		if (state == null)
			return;

		Iterator<Integer> integerIt = state.getPlaceTokens();

		if (places.size() == state.numberOfPlaces())
			for (Place p : places)
				p.setNumberOfTokens(integerIt.next());

	}

	/**
	 * Increments given place.
	 *
	 * @param place the place to be incremented
	 * @return true, if successful
	 */
	public boolean incrementPlace(PetrinetElement place) {
		if (place == null)
			return false;
		if (!places.containsKey(place.getId()) || !(place instanceof Place))// should not happen -> here for safety
																			// reasons
			return false;

		Place p = (Place) place;
		p.incrementTokens();

		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onPetrinetChanged(this);

		return true;
	}

	/**
	 * Decrements given place.
	 *
	 * @param place the place to be decremented
	 * @return true, if successful
	 */
	public boolean decrementPlace(PetrinetElement place) {
		if (place == null)
			return false;

		if (!places.containsKey(place.getId()) || !(place instanceof Place))// should not happen -> here for safety
																			// reasons
			return false;

		Place p = (Place) place;
		boolean decremented = p.decrementTokens();
		if (!decremented)
			return false;

		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onPetrinetChanged(this);

		return true;
	}

	/**
	 * Gets the state of the petrinet as string.
	 *
	 * @return the state string
	 */
	public String getStateString() {
		StringBuilder sb = new StringBuilder();

		sb.append("(");

		for (Place p : getPlaces())
			sb.append(p.getNumberOfTokens() + "|");

		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");
		return sb.toString();
	}

	/**
	 * Sets the petrinet element name.
	 *
	 * @param id    the id of the element
	 * @param label the new label for the element
	 */
	public void setPetrinetElementLabel(String id, String label) {
		PetrinetElement element = getPetrinetElement(id);
		element.setName(label);
		// inform the listener
		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onPetrinetElementLabelChanged(element);

	}

	/**
	 * Checks for places -> if petrinet has no places it is stateless.
	 *
	 * @return true, if the petrinet has places
	 */
	boolean hasPlaces() {

		return places.size() > 0;
	}

	/**
	 * Checks whether all components of the given petrinet are connected to each
	 * other.
	 * 
	 * @return true, if petrinet is connected
	 */
	public boolean isConnected() {

		// empty net or a net with only places or only transitions is treated as not
		// connected
		if (places.isEmpty() && transitions.isEmpty()) {
			return false;
		}

//		if (places.size() + transitions.size() == 1)
//			return true;

		// get starting point
		PetrinetElement startElement = places.iterator().next();

		// keep track of visited elements
		Set<PetrinetElement> visited = new HashSet<PetrinetElement>();

		// stack for DFS
		Stack<PetrinetElement> stack = new Stack<PetrinetElement>();
		stack.push(startElement);

		// do DFS
		while (!stack.isEmpty()) {
			PetrinetElement current = stack.pop();
			if (!visited.contains(current)) {
				visited.add(current);

				// add connected elements to the stack
				if (current instanceof Place) {
					Place place = (Place) current;
					place.getInputs().forEach(transition -> stack.push(transition));
					place.getOutputs().forEach(transition -> stack.push(transition));
				} else if (current instanceof Transition) {
					Transition transition = (Transition) current;
					transition.getInputs().forEach(place -> stack.push(place));
					transition.getOutputs().forEach(place -> stack.push(place));
				}
			}
		}

		// Check if all elements were visited
		return visited.containsAll(places.castToSet()) && visited.containsAll(transitions.castToSet());
	}

}
