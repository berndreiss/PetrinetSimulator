package gui;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import core.Petrinet;
import core.PetrinetElement;
import core.Place;
import core.Transition;
import listeners.PetrinetComponentChangedListener;

// TODO: Auto-generated Javadoc
/**
 *
 * <p>
 * A <a href="https://graphstream-project.org/">GraphStream</a> implementation of {@link PetrinetGraph}.
 * </p>
 * 
 * <p>
 * The graph listens to changes in the petrinet passed to it via a
 * {@link PetrinetComponentChangedListener}. If the petrinet is not empty it
 * adds all containing components. Nodes can also be marked / unmarked by toggling
 * them.
 * </p>
 */
public class GraphStreamPetrinetGraph extends MultiGraph implements PetrinetGraph{

	/** URL referencing CSS file */
	private static String CSS_FILE = "url(" + GraphStreamPetrinetGraph.class.getResource("/petrinet_graph.css") + ")";

	/** The graphs sprite mangager */
	private SpriteManager spriteMan;

	/** Keeps track of the last node that has been marked in the graph */
	private PetrinetElement markedNode;

	/**
	 * Instantiates a new petrinet graph.
	 *
	 * @param petrinet The petrinet to be visualized / listened to.
	 */
	public GraphStreamPetrinetGraph(Petrinet petrinet) {
		super("Petrinet");

		// set CSS file for the graph
		this.setAttribute("ui.stylesheet", CSS_FILE);

		// instantiate sprite manager
		spriteMan = new SpriteManager(this);

		// handle elements in the petrinet if it is not empty
		for (Place p : petrinet.getPlaces())// add places
			addPlace(p);

		for (Transition t : petrinet.getTransitions()) {// add transitions
			addTransition(t);

			for (Place p : t.getInputs())// add edges from places to transitions
				addPetrinetEdge(getNode(p.getId()), getNode(t.getId()),
						petrinet.getOriginalArcId(p.getId() + t.getId()));// get the original id for edge labels

			for (Place p : t.getOutputs())// add edges from transitions to places
				addPetrinetEdge(getNode(t.getId()), getNode(p.getId()),
						petrinet.getOriginalArcId(t.getId() + p.getId()));// get the original id for edge labels

			/*
			 * for information on the internal handling of ids on nodes/edges see comments
			 * at methods addPetrinetEdge and addPetrinetElement
			 */
		}

		// set the listener for the petrinet
		petrinet.setPetrinetComponentChangedListener(new PetrinetComponentChangedListener() {

			@Override
			public void onTransitionStateChanged(Transition transition) {
				setTransitionHighlight(transition);// aborts if transition == null or node does not exist
			}

			@Override
			public void onPetrinetElementLabelChanged(PetrinetElement element) {

				// get sprite of petrinet elements node and set it to the according label
				Sprite sprite = spriteMan.getSprite("s" + element.getId());
				if (sprite == null)
					return;
				sprite.setAttribute("ui.label", getElementLabel(element));
			}

			@Override
			public void onPlaceTokenCountChanged(Place place) {

				// get the node and its sprite and change the labels -> abort if node does not
				// exist
				Node node = getNode(place.getId());
				if (node == null)
					return;
				node.setAttribute("ui.label", placeTokenLabel(place.getNumberOfTokens()));
				Sprite sprite = spriteMan.getSprite("s" + place.getId());
				sprite.setAttribute("ui.label", getElementLabel(place));
			}

			@Override
			public void onPetrinetElementCoordinatesChanged(PetrinetElement element) {

				// get node and set coordinates -> abort if node does not exist
				Node node = getNode(element.getId());
				if (node == null)
					return;
				node.setAttribute("xy", element.getX(), element.getY());
			}

			@Override
			public void onPetrinetElementRemoved(PetrinetElement element) {

				// remove node and its sprite if they exist -> also set markedNode to null if it
				// was the removed element
				Node node = removeNode(element.getId());
				if (node == null)
					return;
				spriteMan.removeSprite("s" + element.getId());
				if (markedNode == element)
					markedNode = null;

			}

			@Override
			public void onPetrinetElementAdded(PetrinetElement element) {

				if (element instanceof Place)
					addPlace((Place) element);
				if (element instanceof Transition)
					addTransition((Transition) element);

			}

			@Override
			public void onEdgeRemoved(PetrinetElement source, PetrinetElement target) {

				String edgeString = source.getId() + target.getId();
				
				// remove edge and its sprite if they exist
				Edge edge = removeEdge(edgeString);
				if (edge == null)
					return;
				spriteMan.removeSprite("s" + edgeString);
			}

			@Override
			public void onEdgeAdded(PetrinetElement source, PetrinetElement target, String id) {

				// add edge if both source and target exist
				Node sourceNode = getNode(source.getId());
				Node targetNode = getNode(target.getId());

				if (sourceNode == null || targetNode == null)
					return;

				addPetrinetEdge(sourceNode, targetNode, id);
			}
		});

	}

	// adds a node for a place -> adds the number of places to the node
	private Node addPlace(Place p) {

		// return the node if it already exists
		if (this.getNode(p.getId()) != null)
			return this.getNode(p.getId());

		// create node and set ui.class
		Node node = addPetrinetElement(p);
		node.setAttribute("ui.class", "place");

		// get the formatted label and set it
		String label = placeTokenLabel(p.getNumberOfTokens());
		node.setAttribute("ui.label", label);

		return node;
	}

	// adds a node for a transition -> handles highlighting activated transitions
	private Node addTransition(Transition t) {

		// return the node if it already exists
		if (this.getNode(t.getId()) != null)
			return this.getNode(t.getId());

		// create node and set highlighting
		Node node = this.addPetrinetElement(t);
		setTransitionHighlight(t);

		return node;

	}

	/*
	 * NOTE ON IDS: since all elements in the petrinet having a unique id is an
	 * invariant of the petrinet nodes can safely be added by using this id
	 */

	// adds a node with attributes that are the same for both places and transitions
	private Node addPetrinetElement(PetrinetElement e) {

		// add node and set coordinates
		Node node = this.addNode(e.getId());
		node.setAttribute("xy", e.getX(), e.getY());

		// create sprite, set ui.class and label and attach it to the node
		Sprite sprite = spriteMan.addSprite("s" + e.getId());
		sprite.setAttribute("ui.class", "nodeLabel");
		sprite.setAttribute("ui.label", getElementLabel(e));
		sprite.attachToNode(node.getId());

		return node;
	}

	/*
	 * NOTE ON IDS: internally the id of nodes are handled by combining the ids of
	 * the source and the target. Since both ids are unique the new id is unique
	 * also (see comment for method addPetrinetElement).
	 */

	// adds a new edge with a sprite label with the original id as a label
	private Edge addPetrinetEdge(Node source, Node target, String originalId) {

		// get id for edge and add it
		String id = source.getId() + target.getId();
		Edge edge = addEdge(id, source, target, true);

		// set up sprite and attach it to the edge
		Sprite sprite = spriteMan.addSprite("s" + id);
		sprite.setAttribute("ui.class", "edgeLabel");
		sprite.setAttribute("ui.label", "[" + originalId + "]");
		sprite.attachToEdge(id);
		sprite.setPosition(0.5);

		return edge;
	}

	/*
	 * returns a string formatted to represent tokens in places if numberOfTokens ==
	 * 0 -> "" if numberOfTokens > 9 -> ">9" number of tokens otherwise
	 */
	private static String placeTokenLabel(int numberOfTokens) {
		if (numberOfTokens == 0)
			return "";

		if (numberOfTokens > 9)
			return ">9";
		return String.valueOf(numberOfTokens);
	}

	@Override
	public void toggleNodeMark(PetrinetElement petrinetElement) {

		// if there is a marked node unmark it
		if (markedNode != null) {

			// call setTransitionHighlight for transitions (being activated/not activated
			// complicates things, see method) and simply reset ui.class for places
			if (markedNode instanceof Transition)
				setTransitionHighlight((Transition) markedNode);
			else
				getNode(markedNode.getId()).setAttribute("ui.class", "place");
		}

		// case provided element == null or marked element -> set marked node to null
		if (petrinetElement == null || petrinetElement == markedNode) {
			markedNode = null;
			return;
		}

		// otherwise set element to according ui.class and set the marked node
		if (petrinetElement instanceof Transition)
			getNode(petrinetElement.getId()).setAttribute("ui.class", "transition_edit");
		else
			getNode(petrinetElement.getId()).setAttribute("ui.class", "place_highlight");

		markedNode = petrinetElement;
	}

	@Override
	public PetrinetElement getMarkedNode() {

		return markedNode;
	}

	/**
	 * Gets the element label -> for transitions "[id] label" is returned, for
	 * places "[id] label &lt;numberOfTokens&gt;" is returned.
	 *
	 * @param element The petrinet element for which to return a label.
	 * @return the element label: "[id] label" for transitions, "[id] label
	 *         &lt;numberOfTokens&gt;" for places
	 */
	public static String getElementLabel(PetrinetElement element) {

		String base = "[" + element.getId() + "] " + element.getName();
		if (element instanceof Place)
			return base + " <" + ((Place) element).getNumberOfTokens() + ">";

		return base;

	}

	// sets the node of a transition to the according ui.class according to whether
	// it is activated or not -> aborts when t == null or node does not exist
	private void setTransitionHighlight(Transition t) {
		if (t == null)// safety check
			return;

		Node node = getNode(t.getId());

		if (node == null)// safety check
			return;

		if (t.isActivated())
			node.setAttribute("ui.class", "transition_activated");
		else
			node.setAttribute("ui.class", "transition");
	}

}