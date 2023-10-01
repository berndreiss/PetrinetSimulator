package view;

import java.util.Map;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

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
 * <b>Achtung:</b><br> Beachten Sie an dieser Stelle bitte die Hinweise aus der
 * Aufgabenstellung bezüglich der Trennung <i>eigener Datenstrukturen zur
 * Repräsentation eines Graphen</i> (Petrinetz oder Erreichbarkeitsgraphen) und
 * der <i>graphischen Darstellung eines Graphen</i> mittels der
 * Graphenvisualisierungsbibliothek GraphStream.
 * </p>
 * 
 * <p>
 * In diesem Beispielprogramm soll lediglich die Verwendung der
 * Graphenvisualisierungsbibliothek GraphStream vorgestellt werden. Eine
 * geeignete Datenstruktur zur Repräsentation eines Graphen - wie in der
 * Aufgabenstellung erwähnt - ist somit hier nicht Teil der Programmstruktur.
 * In Ihrer eigenen Lösung müssen Sie diese Anforderung selber realisieren.
 * </p>
 * 
 * @author ProPra-Team FernUni Hagen
 */
public class DemoGraph extends MultiGraph {

	/** 
	 * URL-Angabe zur css-Datei, in der das Layout des Graphen angegeben ist. 
	*/
	private static String CSS_FILE = "url(" + DemoGraph.class.getResource("/graph.css") + ")"; // diese Variante der Pfadangabe funktioniert auch aus einem JAR heraus
	
	/**
	 * der SpriteManager des Graphen
	 */	
	private SpriteManager spriteMan;
	
	/**
	 * Sprite in X-Form, das zur Markierung das zuletzt angeklickten Knotens dient
	 */
	private Sprite spriteMark;
    
	/**
	 * Im Konstruktor der Klasse DemoGraph wird ein Graph mit fünf Knoten und
	 * insgesamt sieben gerichteten Kanten erzeugt. Zwei Multi-Kanten gehen von A
	 * nach C. Zwei entgegengesetzte Kanten gehen von C nach D bzw. von D nach
	 * C.
	 */
	public DemoGraph() {
		super("Beispiel");

		// Angabe einer css-Datei für das Layout des Graphen
		this.setAttribute("ui.stylesheet", CSS_FILE);

		// einen SpriteManger für diesen Graphen erzeugen
		spriteMan = new SpriteManager(this);
				
		// Erzeugen von Knoten
//		Node nodeA = this.addNode("n1");
//		nodeA.setAttribute("ui.label", "A");
//		nodeA.setAttribute("xy", 100, 50);
//
//		Node nodeB = this.addNode("n2");
//		nodeB.setAttribute("ui.label", "B");
//		nodeB.setAttribute("xy", 200, 50);
//
//		Node nodeC = this.addNode("n3");
//		nodeC.setAttribute("ui.label", "C");
//		nodeC.setAttribute("xy", 300, 50);
//
//		Node nodeD = this.addNode("n4");
//		nodeD.setAttribute("ui.label", "D");
//		nodeD.setAttribute("xy", 200, 100);
//
//		Node nodeE = this.addNode("n5");
//		nodeE.setAttribute("ui.label", "E");
//		nodeE.setAttribute("xy", 200, 0);
//		nodeE.setAttribute("ui.class", "bild");
//
//		// Erzeugen von Kanten und Sprites, die als Label benutzt werden
//		Edge edge1 = this.addEdge("k1", nodeA, nodeB, true);
//		Sprite s1 = spriteMan.addSprite("s1");
//		s1.attachToEdge(edge1.getId());
//		s1.setPosition(0.5);
//		s1.setAttribute("ui.label", "Kante 1");
//		s1.setAttribute("ui.class", "edgeLabel");
//
//		Edge edge2 = this.addEdge("k2", nodeC, nodeB, true);
//		Sprite s2 = spriteMan.addSprite("s2");
//		s2.attachToEdge(edge2.getId());
//		s2.setPosition(0.5);
//		s2.setAttribute("ui.label", "Kante 2");
//		s2.setAttribute("ui.class", "edgeLabel");
//
//		Edge edge3a = this.addEdge("k3a", nodeA, nodeD, true);
//		Sprite s3a = spriteMan.addSprite("s3a");
//		s3a.attachToEdge(edge3a.getId());
//		s3a.setPosition(0.45);
//		s3a.setAttribute("ui.label", "Kante 3a");
//		s3a.setAttribute("ui.class", "edgeLabel");
//
//		Edge edge3b = this.addEdge("k3b", nodeA, nodeD, true);
//		Sprite s3b = spriteMan.addSprite("s3b");
//		s3b.attachToEdge(edge3b.getId());
//		s3b.setPosition(0.55);
//		s3b.setAttribute("ui.label", "Kante 3b");
//		s3b.setAttribute("ui.class", "edgeLabel");
//
//		Edge edge4 = this.addEdge("k4", nodeD, nodeC, true);
//		Sprite s4 = spriteMan.addSprite("s4");
//		s4.attachToEdge(edge4.getId());
//		s4.setPosition(0.75);
//		s4.setAttribute("ui.label", "Kante 4");
//		s4.setAttribute("ui.class", "edgeLabel");
//
//		Edge edge5 = this.addEdge("k5", nodeC, nodeD, true);
//		Sprite s5 = spriteMan.addSprite("s5");
//		s5.attachToEdge(edge5.getId());
//		s5.setPosition(0.75);
//		s5.setAttribute("ui.label", "Kante 5");
//		s5.setAttribute("ui.class", "edgeLabel");
//
//		Edge edge6 = this.addEdge("k6", nodeB, nodeE, true);
//		Sprite s6 = spriteMan.addSprite("s6");
//		s6.attachToEdge(edge6.getId());
//		s6.setPosition(0.5);
//		s6.setAttribute("ui.label", "Kante 6");
//		s6.setAttribute("ui.class", "edgeLabel");
		
	}
	
	public Node addPlace(Place p) {
		
		//TODO: handle place already exists
		Node node = this.addNode(p.getId());
		node.setAttribute("ui.label", p.numberOfTokens() == 0?"":p.numberOfTokens());
		node.setAttribute("ui.class", "place");
		node.setAttribute("xy", p.getX(), p.getY());
		return node;
	}
	
	public Node addTransition(Transition t) {
		Node node = this.addNode(t.getId());
//		node.setAttribute("ui.label", t.getName());
//		this.toggleNodeHighlight(t.getId());
		node.setAttribute("ui.class", "transition");
		node.setAttribute("xy", t.getX(), t.getY());
		
		
		Map<String, PetrinetElement> preset = t.getInputs();
		Map<String, PetrinetElement> postset = t.getOutputs();

		//TODO: for both for loops handle places already added
		for (String s: preset.keySet()) {
			Place p = (Place) preset.get(s);
			addEdge(p.getId()+t.getId(),this.getNode(p.getId()), node);
		}
		for (String s: postset.keySet()) {
			Place p = (Place) postset.get(s);
			addEdge(t.getId()+p.getId(), node,this.getNode(p.getId()));
		}

		return node;

	}

	public Edge addEdge(String name, Node a, Node b) {
		Edge edge = this.addEdge(name, a, b, true);
		return edge;
	}
	
	/**
	 * Liefert einen Text zu einem Knoten, der für die Ausgabe verwendet wird.
	 * 
	 * @param id
	 *            Id des Knotens, zu dem der Text ermittelt werden soll
	 * @return Ausgabe-Text zu einem Knoten
	 */
	public String getOutputText(String id) {
		Node node = this.getNode(id);
		return new String("Der Knoten \"" + node.getAttribute("ui.label")
				+ "\" hat die ID \"" + node.getId() + "\"");
	}

	/**
	 * Das Hervorheben des Knotens wegnehmen oder setzen.
	 * 
	 * @param id
	 *            Id des Knotens, bei dem das Hervorheben getauscht werden soll
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
	 * @param id
	 * 				Id des angeklickten Knotens
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
}