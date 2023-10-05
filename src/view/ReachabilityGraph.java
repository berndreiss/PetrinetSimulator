package view;

import javax.swing.JPanel;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

public class ReachabilityGraph extends MultiGraph {

	private static String CSS_FILE = "url(" + PetrinetGraph.class.getResource("/reachability_graph.css") + ")";

	private SpriteManager spriteMan;

	private Sprite spriteMark;

	private Node currentNode;

	public ReachabilityGraph(String id) {
		super(id);

		// Angabe einer css-Datei für das Layout des Graphen
		this.setAttribute("ui.stylesheet", CSS_FILE);

		// einen SpriteManger für diesen Graphen erzeugen
		spriteMan = new SpriteManager(this);

		SpringBox layout = new SpringBox(false);

	}

	public Node addState(String id, String transitionId) {

		Node node;

		if (currentNode == null) {
			node = this.addNode(id);
			node.setAttribute("ui.label", id);
			currentNode = node;
			return node;
		}
		if (this.getNode(id) != null) {

			node = getNode(id);
			// return currentNode if new state is currentNode
			if (node.getId().equals(currentNode.getId()))
				return currentNode;


		} else {
			node = addNode(id);
			node.setAttribute("ui.label", id);

		}
		if (this.getEdge(currentNode.getId()+id) == null) {
			Edge e = this.addEdge(currentNode.getId()+id, currentNode, node, true);
			Sprite sprite = spriteMan.addSprite("s" + e.getId());
			sprite.setAttribute("ui.class", "edgeLabel");
			sprite.setAttribute("ui.label", transitionId);

			sprite.attachToEdge(e.getId());
			sprite.setPosition(0.5);
			System.out.println(e.isDirected());

		}
		currentNode = node;

		return node;
	}

	public void markStatesInvalid(String m, String mMark) {
		Node nodeM = this.getNode(m);
		Node nodeMMark = this.getNode(mMark);
		
		nodeM.setAttribute("ui.class", "m");
		nodeMMark.setAttribute("ui.class", "m_mark");
	}
}
