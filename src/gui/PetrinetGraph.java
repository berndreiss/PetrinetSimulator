package gui;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import control.PetrinetController;
import core.Petrinet;
import core.PetrinetElement;
import core.Place;
import core.Transition;
import listeners.PetrinetComponentChangedListener;
import listeners.PetrinetStateChangedListener;

// TODO: Auto-generated Javadoc
/**
 *
 * <p>
 * A GraphStream graph that can represent an instance of {@link Petrinet}.
 * </p>
 * 
 * <p>
 * The graph listens to changes in the petrinet via a {@link PetrinetComponentChangedListener}. 
 * </p>
 */
public class PetrinetGraph extends MultiGraph {

	/** URL referencing CSS file */
	private static String CSS_FILE = "url(" + PetrinetGraph.class.getResource("/petrinet_graph.css") + ")";

	/** The graphs sprite mangager */
	private SpriteManager spriteMan;

	/** Keeps track of the last node that has been marked in the graph */
	private PetrinetElement markedNode;

	/**
	 * Instantiates a new petrinet Graph.
	 *
	 * @param petrinet A controller holding the petrinet.
	 */
	public PetrinetGraph(Petrinet petrinet) {
		super("Petrinet");
		// Angabe einer css-Datei für das Layout des Graphen
		this.setAttribute("ui.stylesheet", CSS_FILE);

		// einen SpriteManger für diesen Graphen erzeugen
		spriteMan = new SpriteManager(this);

		//handle elements in the petrinet if it is not empty
		for (Place p: petrinet.getPlaces())
			addPlace(p);
		
		for (Transition t: petrinet.getTransitions()) {
			addTransition(t);
			
			for (Place p: t.getInputs())
				addPetrinetEdge(getNode(p.getId()), getNode(t.getId()), petrinet.getOriginalArcId(p.getId()+t.getId()));
			for (Place p: t.getOutputs())
				addPetrinetEdge(getNode(t.getId()), getNode(p.getId()), petrinet.getOriginalArcId(t.getId()+p.getId()));
		}
		
		petrinet.setPetrinetComponentChangedListener(new PetrinetComponentChangedListener() {

			@Override
			public void onTransitionStateChanged(Transition transition) {
				Node node = getNode(transition.getId());

				if (node == null)
					return;
				setTransitionNormal(transition);
			}

			@Override
			public void onSetPetrinetElementName(PetrinetElement element) {
				Sprite sprite = spriteMan.getSprite("s" + element.getId());
				if (sprite == null)
					return;
				sprite.setAttribute("ui.label", getElementLabel(element));
			}

			@Override
			public void onPlaceTokenCountChanged(Place place) {
				Node node = getNode(place.getId());
				if (node == null)
					return;
				node.setAttribute("ui.label", placeTokenLabel(place.getNumberOfTokens()));
				Sprite sprite = spriteMan.getSprite("s" + place.getId());

				sprite.setAttribute("ui.label", getElementLabel(place));
			}

			@Override
			public void onPetrinetElementSetCoordinates(PetrinetElement element) {
				Node node = getNode(element.getId());
				if (node == null)
					return;

				node.setAttribute("xy", element.getX(), element.getY());
			}

			@Override
			public void onPetrinetElementRemoved(PetrinetElement element) {
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
			public void onEdgeRemoved(String edge) {
				Edge e = removeEdge(edge);
				if (e == null)
					return;
				spriteMan.removeSprite("s" + edge);
			}

			@Override
			public void onEdgeAdded(PetrinetElement source, PetrinetElement target, String id) {
				Node sourceNode = getNode(source.getId());
				Node targetNode = getNode(target.getId());

				if (sourceNode == null || targetNode == null)
					return;

				addPetrinetEdge(sourceNode, targetNode, id);
			}
		});

	}

	private Node addPetrinetElement(PetrinetElement e) {

		Node node = this.addNode(e.getId());
		node.setAttribute("xy", e.getX(), e.getY());

		Sprite sprite = spriteMan.addSprite("s" + e.getId());
		sprite.setAttribute("ui.class", "nodeLabel");
		sprite.setAttribute("ui.label", getElementLabel(e));
		sprite.attachToNode(node.getId());

		return node;
	}

	private Node addPlace(Place p) {

		// return the node if it already exists
		if (this.getNode(p.getId()) != null)
			return this.getNode(p.getId());

		Node node = addPetrinetElement(p);
		node.setAttribute("ui.class", "place");

		String label = placeTokenLabel(p.getNumberOfTokens());
		node.setAttribute("ui.label", label);

		return node;
	}

	private Node addTransition(Transition t) {

		if (this.getNode(t.getId()) != null)
			return this.getNode(t.getId());

		Node node = this.addPetrinetElement(t);

		setTransitionNormal(t);

		return node;

	}

	private Edge addPetrinetEdge(Node a, Node b, String id) {

		String name = a.getId() + b.getId();
		Edge edge = this.addEdge(name, a, b, true);

		Sprite sprite = spriteMan.addSprite("s" + name);
		sprite.setAttribute("ui.class", "edgeLabel");
		sprite.setAttribute("ui.label", "[" + id + "]");
		sprite.attachToEdge(name);
		sprite.setPosition(0.5);

		return edge;
	}

	/**
	 * Place token label.
	 *
	 * @param numberOfTokens the number of tokens
	 * @return the string
	 */
	private static String placeTokenLabel(int numberOfTokens) {
		if (numberOfTokens == 0)
			return "";

		if (numberOfTokens > 9)
			return ">9";
		return String.valueOf(numberOfTokens);
	}

	/**
	 * Toggle node mark.
	 *
	 * @param pe the pe
	 */
	public void toggleNodeMark(PetrinetElement pe) {

		if (pe == null) {
			if (markedNode != null) {
				if (markedNode instanceof Transition)
					setTransitionNormal(markedNode);
				else
					getNode(markedNode.getId()).setAttribute("ui.class", "place");

				markedNode = null;
			}

			return;
		}
		System.out.println(pe == null);

		if (pe == markedNode) {
			if (pe instanceof Transition)
				setTransitionNormal(pe);
			else
				getNode(pe.getId()).setAttribute("ui.class", "place");
			markedNode = null;
			return;
		}

		if (markedNode != null) {
			if (markedNode instanceof Transition)
				setTransitionNormal(markedNode);
			else
				getNode(markedNode.getId()).setAttribute("ui.class", "place");

			if (pe instanceof Transition)
				getNode(pe.getId()).setAttribute("ui.class", "transition_edit");
			else
				getNode(pe.getId()).setAttribute("ui.class", "place_highlight");

			markedNode = pe;
			return;
		}

		if (pe instanceof Transition)
			getNode(pe.getId()).setAttribute("ui.class", "transition_edit");
		else
			getNode(pe.getId()).setAttribute("ui.class", "place_highlight");

		markedNode = pe;
	}

	/**
	 * Gets the marked node.
	 *
	 * @return the marked node
	 */
	public PetrinetElement getMarkedNode() {

		return markedNode;
	}

	/**
	 * Gets the element label.
	 *
	 * @param e the e
	 * @return the element label
	 */
	public static String getElementLabel(PetrinetElement e) {

		String base = "[" + e.getId() + "] " + e.getName();
		if (e instanceof Place)
			return base + " <" + ((Place) e).getNumberOfTokens() + ">";

		return base;

	}

	/**
	 * Sets the transition normal.
	 *
	 * @param pe the new transition normal
	 */
	public void setTransitionNormal(PetrinetElement pe) {
		if (pe == null || !(pe instanceof Transition))
			return;

		Transition t = (Transition) pe;

		String id = t.getId();
		Node node = getNode(id);
		if (t.isActive())
			node.setAttribute("ui.class", "transition_activated");
		else
			node.setAttribute("ui.class", "transition");
	}

}