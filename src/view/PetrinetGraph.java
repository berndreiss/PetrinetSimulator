package view;

import java.util.ArrayList;
import java.util.List;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import datamodel.Petrinet;
import datamodel.PetrinetComponentChangedListener;
import datamodel.PetrinetElement;
import datamodel.Place;
import datamodel.Transition;

/**
 * Die Klasse DemoGraph implementiert einen Graphen mittels GraphStream. Die
 * Klasse erbt von <i>MultiGraph</i> (nicht von Graph), um auch mehrere Kanten
 * zwischen zwei Knoten zu ermöglichen (dies benötigen Sie für den
 * Erreichbarkeitsgraphen).
 * 
 * <p>
 * <b>Achtung:</b><br>
 * Beachten Sie an dieser Stelle bitte die Hinweise aus der Aufgabenstellung
 * bezüglich der Trennung <i>eigener Datenstrukturen zur Repräsentation eines
 * Graphen</i> (Petrinetz oder Erreichbarkeitsgraphen) und der <i>graphischen
 * Darstellung eines Graphen</i> mittels der Graphenvisualisierungsbibliothek
 * GraphStream.
 * </p>
 * 
 * <p>
 * In diesem Beispielprogramm soll lediglich die Verwendung der
 * Graphenvisualisierungsbibliothek GraphStream vorgestellt werden. Eine
 * geeignete Datenstruktur zur Repräsentation eines Graphen - wie in der
 * Aufgabenstellung erwähnt - ist somit hier nicht Teil der Programmstruktur. In
 * Ihrer eigenen Lösung müssen Sie diese Anforderung selber realisieren.
 * </p>
 * 
 * @author ProPra-Team FernUni Hagen
 */
public class PetrinetGraph extends MultiGraph {

	/**
	 * URL-Angabe zur css-Datei, in der das Layout des Graphen angegeben ist.
	 */
	private static String CSS_FILE = "url(" + PetrinetGraph.class.getResource("/petrinet_graph.css") + ")"; // diese
																											// Variante
																											// der
																											// Pfadangabe
																											// funktioniert
																											// auch aus
																											// einem JAR
																											// heraus

	/**
	 * der SpriteManager des Graphen
	 */
	private SpriteManager spriteMan;
	/**
	 * Sprite in X-Form, das zur Markierung das zuletzt angeklickten Knotens dient
	 */
	private Sprite spriteMark;

	private PetrinetElement markedNode;


	/**
	 * Im Konstruktor der Klasse DemoGraph wird ein Graph mit fünf Knoten und
	 * insgesamt sieben gerichteten Kanten erzeugt. Zwei Multi-Kanten gehen von A
	 * nach C. Zwei entgegengesetzte Kanten gehen von C nach D bzw. von D nach C.
	 */
	public PetrinetGraph(Petrinet petrinet) {
		super("Beispiel");
		// Angabe einer css-Datei für das Layout des Graphen
		this.setAttribute("ui.stylesheet", CSS_FILE);

		// einen SpriteManger für diesen Graphen erzeugen
		spriteMan = new SpriteManager(this);


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
//		sprite.setPosition(0.5);

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

//		Edge edge1 = this.addEdge(name+"1", a, b, true);
		Sprite sprite = spriteMan.addSprite("s" + name);
		sprite.setAttribute("ui.class", "edgeLabel");
		sprite.setAttribute("ui.label", "[" + id + "]");
		sprite.attachToEdge(name);
		sprite.setPosition(0.5);

		return edge;
	}

	/**
	 * Das Hervorheben des Knotens wegnehmen oder setzen.
	 * 
	 * @param id Id des Knotens, bei dem das Hervorheben getauscht werden soll
	 */
	public void toggleNodeHighlight(String id) {

		Node node = this.getNode(id);

		if (node.hasAttribute("ui.class")) {
			node.removeAttribute("ui.class");
		} else {
			node.setAttribute("ui.class", "highlight");
		}
	}

	/**
	 * Markiert den zuletzt angeklickten Knoten mit einem X-förmigen Sprite.
	 * 
	 * @param id Id des angeklickten Knotens
	 */
	public void markLastClickedNode(String id) {

		if (spriteMark == null) {
			// Sprite erzeugen, das zur Markierung des zuletzt angeklickten Knotens dient
			spriteMark = spriteMan.addSprite("sMark");
			spriteMark.setAttribute("ui.class", "mark");
		}

		// Sprite auf Knoten setzen
		spriteMark.attachToNode(id);
	}

	public static String placeTokenLabel(int numberOfTokens) {
		if (numberOfTokens == 0)
			return "";

		if (numberOfTokens > 9)
			return ">9";
		return String.valueOf(numberOfTokens);
	}

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
		System.out.println(pe==null);

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

	public PetrinetElement getMarkedNode() {

		return markedNode;
	}

	public void reset() {

		toggleNodeMark(null);

	}

	public static String getElementLabel(PetrinetElement e) {

		String base = "[" + e.getId() + "] " + e.getName();
		if (e instanceof Place)
			return base + " <" + ((Place) e).getNumberOfTokens() + ">";

		return base;

	}

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