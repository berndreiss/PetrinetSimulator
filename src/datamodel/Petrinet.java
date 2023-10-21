package datamodel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import util.IterableMap;

public class Petrinet {

	// TODO edge ids are not removed when edge removed!!!!!!!!!!!!!!!!!!

	public static final Comparator<String> TREE_COMPARATOR = String.CASE_INSENSITIVE_ORDER;

	private IterableMap<String, Transition> transitions;// set of transitions represented by a map since the Set
														// interface does
	// not provide a get function
	private IterableMap<String, Place> places;// set of places represented by a map since the Set interface does not
												// provide a
	// get function
	private IterableMap<String, String> originalArcIds;

	private PetrinetStateChangedListener petrinetStateChangedListener;
	private PetrinetComponentChangedListener petrinetComponentChangedListener;

	public void setPetrinetChangeListener(PetrinetStateChangedListener petrinetChangedListener) {
		this.petrinetStateChangedListener = petrinetChangedListener;
	}

	public void setPetrinetComponentChangedListener(PetrinetComponentChangedListener petrinetComponentChangedListener) {
		this.petrinetComponentChangedListener = petrinetComponentChangedListener;
	}

	public Petrinet() {
		this.transitions = new IterableMap<String, Transition>();
		this.places = new IterableMap<String, Place>(TREE_COMPARATOR);
		this.originalArcIds = new IterableMap<String, String>();
	}

	public Iterable<Transition> getActiveTransitions() {
		ArrayList<Transition> activeTransitions = new ArrayList<Transition>();

		for (Transition t : getTransitions())
			if (t.isActive())
				activeTransitions.add(t);

		return activeTransitions;
	}

	// set element relative to others
	public void setAddedElementPosition(PetrinetElement pe) {

		if (pe == null || containsElementWithId(pe) || (places.size() + transitions.size()) == 1)
			return;

		double x = Double.MAX_VALUE;
		double y = -Double.MAX_VALUE;

		List<PetrinetElement> mostLeftElements = new ArrayList<PetrinetElement>();
		for (Place p : places) {
			if (p == pe)
				continue;
//			x += p.getX();
//			y += p.getY();
			if (p.getX() == x)
				mostLeftElements.add(p);

			if (p.getX() < x) {
				x = p.getX();
				mostLeftElements.clear();
				mostLeftElements.add(p);
			}

		}

		for (Transition t : transitions) {

			if (t == pe)
				continue;

			// x += t.getX();
//			y += t.getY();

			if (t.getX() == x)
				mostLeftElements.add(t);

			if (t.getX() < x) {
				x = t.getX();
				mostLeftElements.clear();
				mostLeftElements.add(t);
			}

		}

//		int size = places.size() + transitions.size()-1;
//		if (size > 0) {
//		if (size == 1)
//			x += 100;
//		else
//			x /= size;
//		y /= size;
//		}
		PetrinetElement leftHightestElement = null;

		for (PetrinetElement p : mostLeftElements) {
			if (p.getY() > y) {
				leftHightestElement = p;
				y = p.getY();
			}
		}

		setCoordinates(pe.getId(), leftHightestElement.getX(), leftHightestElement.getY() + 20);

	}

	public boolean containsElementWithId(PetrinetElement pe) {
		return containsElementWithId(pe.getId());
	}

	public boolean containsElementWithId(String id) {
		return places.get(id) != null ^ transitions.get(id) != null;
	}

	public Iterable<Transition> getTransitions() {
		return transitions;
	}

	public Iterable<Place> getPlaces() {
		return places;
	}

	public Place getPlace(String id) {
		return places.get(id);
	}

	public Transition getTransition(String id) {
		return transitions.get(id);
	}

	public PetrinetElement getPetrinetElement(String id) {
		PetrinetElement element = places.get(id);
		if (element == null)
			element = transitions.get(id);
		return element;
	}

	public void setCoordinates(String id, double x, double y) {
		PetrinetElement element = getPetrinetElement(id);

		if (element == null)
			return;

		element.setX(x);
		element.setY(y);

		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onPetrinetElementSetCoordinates(element);

	}

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

	public Transition addTransition(String id) throws DuplicateIdException {

		if (id == null)
			return null;

		if (transitions.containsKey(id) || places.containsKey(id))
			throw new DuplicateIdException("Duplicate ID: place \"" + id + "\" already exists.");

		Transition t = transitions.put(id, new Transition(id));

		t.setTransitionActiveListener(activated -> {
			if (petrinetComponentChangedListener != null)
				petrinetComponentChangedListener.onTransitionStateChanged(t);

		});

		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onPetrinetElementAdded(t);

		return t;
	}

	public Place addPlace(String id) throws DuplicateIdException {

		if (id == null)
			return null;

		if (transitions.containsKey(id) || places.containsKey(id))
			throw new DuplicateIdException("Duplicate ID: place \"" + id + "\" already exists.");
		Place p = places.put(id, new Place(id));

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

	public void setTokens(String id, int numberOfTokens) {
		if (!places.containsKey(id))
			return;
		Place p = places.get(id);

		if (p.getNumberOfTokens() == numberOfTokens)
			return;

		p.setNumberOfTokens(numberOfTokens);

		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onComponentChanged(this);

	}

	public void addEdge(String source, String target, String id) throws InvalidEdgeOperationException {

		PetrinetElement sourceElement = getPetrinetElement(source);

		if (sourceElement == null)
			throw new InvalidEdgeOperationException("Invalid edge operation: Source \"" + source + "\" is missing.");

		PetrinetElement targetElement = getPetrinetElement(target);

		if (targetElement == null || !containsElementWithId(target))
			throw new InvalidEdgeOperationException("Invalid edge operation: Target \"" + target + "\" is missing.");

		addEdge(sourceElement, targetElement, id);
	}

	public void addEdge(PetrinetElement source, PetrinetElement target, String id)
			throws InvalidEdgeOperationException {

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
			petrinetComponentChangedListener.onEdgeRemoved(key);
		if (petrinetStateChangedListener != null)
			petrinetStateChangedListener.onComponentChanged(this);

	}

	private boolean isTransition(String id) {
		if (transitions.containsKey(id))
			return true;
		return false;
	}

	public void fireTransition(String id) {
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

	public String getOriginalArcId(String arcId) {
		return originalArcIds.get(arcId);
	}

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

	public void setPetrinetElementName(String id, String name) {
		PetrinetElement element = getPetrinetElement(id);
		element.setName(name);
		if (petrinetComponentChangedListener != null)
			petrinetComponentChangedListener.onSetPetrinetElementName(element);

	}

	public boolean hasPlaces() {

		return places.size() > 0;
	}

	public boolean hasEdgeWithId(String id) {
		for (String s : originalArcIds.values())
			if (s.equals(id))
				return true;
		return false;
	}

}
