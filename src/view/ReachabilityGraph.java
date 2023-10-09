package view;

import javax.swing.JPanel;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import control.PetrinetController;
import datamodel.PetrinetState;
import datamodel.PetrinetState;
import datamodel.Transition;

public class ReachabilityGraph extends MultiGraph {

	private static String CSS_FILE = "url(" + PetrinetGraph.class.getResource("/reachability_graph.css") + ")";

	private SpriteManager spriteMan;

	private Sprite spriteMark;

	private Node initialNode;

	private Node currentNode;

	private Node nodeM;

	private Node nodeMMarked;
	private PetrinetController controller;

	public ReachabilityGraph(PetrinetController controller) {
		super("");
		this.controller = controller;

		// TODO: mark inital node
		// Angabe einer css-Datei für das Layout des Graphen
		this.setAttribute("ui.stylesheet", CSS_FILE);

		// einen SpriteManger für diesen Graphen erzeugen
		spriteMan = new SpriteManager(this);

	}

	public Node addState(String id, Transition t) {

		String transitionLabel = t==null?"":PetrinetGraph.getElementLabel(t);
		if (controller.getHeadless())
			return null;
		
		Node node;

		if (currentNode == null) {
			node = this.addNode(id);
			node.setAttribute("ui.class", "initial");
			node.setAttribute("ui.label", id);
			initialNode = node;
			setCurrent(node);
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
		if (this.getEdge(currentNode.getId() + id +transitionLabel) == null) {
			Edge e = this.addEdge(currentNode.getId() + id+transitionLabel, currentNode, node, true);
			Sprite sprite = spriteMan.addSprite("s" + e.getId());
			sprite.setAttribute("ui.class", "edgeLabel");
			sprite.setAttribute("ui.label", transitionLabel);

			sprite.attachToEdge(e.getId());
			sprite.setPosition(0.5);

		}
		setCurrent(node);

		return node;
	}

	public void setCurrentState(PetrinetState state) {
		if (controller.getHeadless())
			return;
		
		setCurrent(getNode(state.getState()));
	}

	public void markStatesInvalid(String m, String mMark) {
		if (controller.getHeadless())
			return;
		
		Node oldM = nodeM;
		Node oldMMarked = nodeMMarked;

		nodeM = this.getNode(m);
		nodeMMarked = this.getNode(mMark);

		setHighlight(oldM);
		setHighlight(oldMMarked);
		setHighlight(nodeM);
		setHighlight(nodeMMarked);
	}

	public void setCurrent(Node node) {
		if (controller.getHeadless())
			return;
		
		if (node == null)
			return;

		if (currentNode == null) {
			currentNode = node;
			setHighlight(currentNode);
			return;
		}

		if (node != currentNode) {
			Node oldCurrent = currentNode;
			currentNode = node;
			setHighlight(currentNode);
			setHighlight(oldCurrent);
			
		}

	}

	private void setHighlight(Node node) {
		if (controller.getHeadless())
			return;
		
		if (node == null)
			return;
		
		if (node == currentNode) {

			if (node == initialNode) {
				if (node == nodeM) {
					node.setAttribute("ui.class", "initial_m_highlight");
					return;
				}
				if (node == nodeMMarked) {
					node.setAttribute("ui.class", "initial_m_mark_highlight");
					return;
				}

				node.setAttribute("ui.class", "initial_highlight");
				return;

			}
			if (node == nodeM) {
				node.setAttribute("ui.class", "m_highlight");
				return;
			}
			if (node == nodeMMarked) {
				node.setAttribute("ui.class", "m_mark_highlight");
				return;
			}

			node.setAttribute("ui.class", "highlight");
			return;
		}
	
		if (node == initialNode) {
			if (node == nodeM) {
				node.setAttribute("ui.class", "initial_m");
				return;
			}
			if (node == nodeMMarked) {
				node.setAttribute("ui.class", "initial_m_mark");
				return;
			}
			node.setAttribute("ui.class", "initial");
			return;

		}
		if (node == nodeM) {
			node.setAttribute("ui.class", "m");
			return;
		}
		if (node == nodeMMarked) {
			node.setAttribute("ui.class", "m_mark");
			return;
		}
		node.setAttribute("ui.class", "node");

	}
}
