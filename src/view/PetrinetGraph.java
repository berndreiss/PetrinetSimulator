package view;

import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.camera.Camera;

import datamodel.NumberOfTokensListener;
import datamodel.PetrinetElement;
import datamodel.Place;
import datamodel.Transition;
import datamodel.TransitionActiveListener;
import control.PetrinetController;

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

	private PetrinetController controller;

	private int padding;

	private String markedNode;

	/**
	 * Im Konstruktor der Klasse DemoGraph wird ein Graph mit fünf Knoten und
	 * insgesamt sieben gerichteten Kanten erzeugt. Zwei Multi-Kanten gehen von A
	 * nach C. Zwei entgegengesetzte Kanten gehen von C nach D bzw. von D nach C.
	 */
	public PetrinetGraph(PetrinetController controller) {
		super("Beispiel");
		this.controller = controller;
		// Angabe einer css-Datei für das Layout des Graphen
		this.setAttribute("ui.stylesheet", CSS_FILE);

		// einen SpriteManger für diesen Graphen erzeugen
		spriteMan = new SpriteManager(this);

		SpringBox layout = new SpringBox(false);

	}

	public SpriteManager getSpriteManager() {
		return spriteMan;
	}

	private Node addPetrinetElement(PetrinetElement e, String spriteAddition) {
		Node node = this.addNode(e.getId());
		node.setAttribute("xy", e.getX(), e.getY());

		Sprite sprite = spriteMan.addSprite("s" + e.getId());
		sprite.setAttribute("ui.class", "nodeLabel");
		sprite.setAttribute("ui.label", "[" + e.getId() + "] " + e.getName() + spriteAddition);
		sprite.attachToNode(node.getId());
		sprite.setPosition(0.5);

		return node;
	}

	public Node addPlace(Place p) {

		// return the node if it already exists
		if (this.getNode(p.getId()) != null)
			return this.getNode(p.getId());

		Node node = addPetrinetElement(p, " <" + p.getNumberOfTokens() + ">");
		node.setAttribute("ui.class", "place");

		String label = placeTokenLabel(p.getNumberOfTokens());
		node.setAttribute("ui.label", label);

		p.setNumberOfTokensListener(new NumberOfTokensListener() {

			@Override
			public void numberChanged(int newNumber) {
				node.setAttribute("ui.label", placeTokenLabel(p.getNumberOfTokens()));
				Sprite sprite = spriteMan.getSprite("s" + p.getId());
				sprite.setAttribute("ui.label",
						"[" + p.getId() + "] " + p.getName() + " <" + p.getNumberOfTokens() + ">");

				// System.out.println(sprite.getLabel(sprite.getId()));

			}
		});

		return node;
	}

	public Node addTransition(Transition t) {
		if (this.getNode(t.getId()) != null)
			return this.getNode(t.getId());

		Node node = this.addPetrinetElement(t, "");

		setTransition(node, t.isActive());

		Map<String, Place> preset = t.getInputs();
		Map<String, Place> postset = t.getOutputs();

		for (String s : preset.keySet()) {
			Place p = (Place) preset.get(s);
			Node pNode = this.addPlace(p);
			addEdge(p.getId() + t.getId(), pNode, node);
		}
		for (String s : postset.keySet()) {
			Place p = (Place) postset.get(s);
			Node pNode = this.addPlace(p);
			addEdge(t.getId() + p.getId(), node, pNode);
		}

		t.setTransitionActiveListener(new TransitionActiveListener() {

			@Override
			public void stateChanges(boolean active) {
				setTransition(node, active);
			}
		});
		return node;

	}

	private void setTransition(Node node, boolean active) {
		if (active)
			node.setAttribute("ui.class", "transition_active");
		else
			node.setAttribute("ui.class", "transition");

	}

	public Edge addEdge(String name, Node a, Node b) {
		Edge edge = this.addEdge(name, a, b, true);

//		Edge edge1 = this.addEdge(name+"1", a, b, true);
		Sprite sprite = spriteMan.addSprite("s" + name);
		sprite.setAttribute("ui.class", "edgeLabel");
		sprite.setAttribute("ui.label", "[" + controller.getPetrinet().getOriginalArcId(name) + "]");
		sprite.attachToEdge(name);
		sprite.setPosition(0.5);

		return edge;
	}

	/**
	 * Liefert einen Text zu einem Knoten, der für die Ausgabe verwendet wird.
	 * 
	 * @param id Id des Knotens, zu dem der Text ermittelt werden soll
	 * @return Ausgabe-Text zu einem Knoten
	 */
	public String getOutputText(String id) {
		Node node = this.getNode(id);
		return new String("Der Knoten \"" + node.getAttribute("ui.label") + "\" hat die ID \"" + node.getId() + "\"");
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

	public void toggleNodeMark(String id) {

		if (id == null) {
			if (markedNode != null) {
				getNode(markedNode).setAttribute("ui.class", "place");
				markedNode = null;
			}
			return;
		}


		if (id.equals(markedNode)) {
			getNode(id).setAttribute("ui.class", "place");
			markedNode = null;
		} else {
			if (markedNode != null)
				getNode(markedNode).setAttribute("ui.class", "place");
			getNode(id).setAttribute("ui.class", "place_highlight");
			markedNode = id;
		}

	}
	
	public String getMarkedNode() {
		return markedNode;
	}

	public void reset() {
		toggleNodeMark(null);
		
	}

}