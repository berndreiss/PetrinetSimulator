package view;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import control.PetrinetController;
import datamodel.PetrinetState;
import datamodel.ReachabilityStateChangeListener;
import datamodel.Transition;

public class ReachabilityGraph extends MultiGraph {

	private static String CSS_FILE = "url(" + PetrinetGraph.class.getResource("/reachability_graph.css") + ")";

	private ReplayGraphListener replayGraphListener;

	private SpriteManager spriteMan;

	private Node initialNode;

	private Node currentNode;
	private Edge currentEdge;

	private Node nodeM;

	private Node nodeMMarked;

	private ReachabilityLayout layoutManager;

	public ReachabilityGraph(PetrinetController controller) {
		super("");

		// Angabe einer css-Datei für das Layout des Graphen
		this.setAttribute("ui.stylesheet", CSS_FILE);

		// einen SpriteManger für diesen Graphen erzeugen
		spriteMan = new SpriteManager(this);

		layoutManager = new ReachabilityLayout(spriteMan);

		PetrinetState initialState = controller.getReachabilityGraphModel().getInitialState();
//		this.setAttribute("layout.force", 0.01);
//		this.setAttribute("layout.repulsionFactor", 0.01);

		if (initialState != null) {

			initialNode = addState(controller.getReachabilityGraphModel().getInitialState(), null, null);
			setHighlight(initialNode);
			layoutManager.add(initialNode);

		}

		controller.getReachabilityGraphModel().setStateChangeListener(new ReachabilityStateChangeListener() {

			@Override
			public void onSetCurrent(PetrinetState state, boolean reset) {
				setCurrentState(state);
				if (reset) {
					if (currentEdge == null)
						return;
					currentEdge.setAttribute("ui.class", "edge");
					currentEdge = null;

				}
			}

			@Override
			public void onRemoveEdge(PetrinetState stateSource, PetrinetState stateTarget, Transition t) {
				Edge removedEdge = removeStateEdge(stateSource, stateTarget, t);
				if (removedEdge == currentEdge)
					currentEdge = null;
			}

			@Override
			public void onRemove(PetrinetState state) {
				Node node = removeState(state);
				if (currentNode == node)
					currentNode = null;
				if (initialNode == node)
					initialNode = null;
				if (nodeM == node || nodeMMarked == node) {
					Node mOld = nodeM;
					Node mMarkedOld = nodeMMarked;

					nodeM = null;
					nodeMMarked = null;
					if (nodeM == node)
						setHighlight(mMarkedOld);
					else
						setHighlight(mOld);
				}

			}

			@Override
			public void onAdd(PetrinetState state, PetrinetState predecessor, Transition t) {
				Node node = addState(state, predecessor, t);
				layoutManager.add(getNode(predecessor == null ? null : predecessor.getState()), node);
			}

			@Override
			public void onMarkInvalid(PetrinetState m, PetrinetState mMarked) {
				markStatesInvalid(m.getState(), mMarked.getState());
			}

		});

	}

	public void addingLoop() {
		while (true) {
			String input = JOptionPane.showInputDialog(null, "Enter id for place:");

			if (input.equals(""))
				break;

			Node node = addNode(input.split(",")[0]);

			double x = Double.parseDouble(input.split(",")[1]);
			double y = Double.parseDouble(input.split(",")[2]);
			node.setAttribute("xy", x, y);
		}
	}

	private Node addState(PetrinetState state, PetrinetState predecessor, Transition t) {

		Node node;
		String id = state.getState();
		String transitionLabel = t == null ? "" : PetrinetGraph.getElementLabel(t);

		node = this.getNode(id);

		if (node == null) {
			node = addNode(id);
			node.setAttribute("ui.label", id);
			node.setAttribute("layout.frozen");
		}

		if (initialNode == null) {
			initialNode = node;
		}
		if (currentNode == null) {
			setCurrent(node);
		}

		setHighlight(node);
		if (predecessor == null)
			return node;

		Node predNode = getNode(predecessor.getState());

		Edge newEdge = this.getEdge(predecessor.getState() + id + transitionLabel);

		if (newEdge == null) {

			newEdge = this.addEdge(predecessor.getState() + id + transitionLabel, predNode, node, true);

			Sprite sprite = spriteMan.addSprite("s" + newEdge.getId());
			sprite.setAttribute("ui.class", "edgeLabel");
			sprite.setAttribute("ui.label", transitionLabel);

			sprite.attachToEdge(newEdge.getId());
			sprite.setPosition(0.5);
		}
		newEdge.setAttribute("ui.class", "highlight");

		if (currentEdge != null && currentEdge != newEdge)
			currentEdge.setAttribute("ui.class", "edge");

		currentEdge = newEdge;

		return node;
	}

	private void setCurrentState(PetrinetState state) {

		setCurrent(getNode(state.getState()));

	}

	private void markStatesInvalid(String m, String mMark) {

		Node oldM = nodeM;
		Node oldMMarked = nodeMMarked;

		nodeM = this.getNode(m);
		nodeMMarked = this.getNode(mMark);

		setHighlight(oldM);
		setHighlight(oldMMarked);
		setHighlight(nodeM);
		setHighlight(nodeMMarked);
	}

	private void setCurrent(Node node) {

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

	private Edge removeStateEdge(PetrinetState stateSource, PetrinetState stateTarget, Transition t) {
		String edgeString = stateSource.getState() + stateTarget.getState() + PetrinetGraph.getElementLabel(t);
		Edge removedEdge = removeEdge(edgeString);
		spriteMan.removeSprite("s" + edgeString);
		return removedEdge;
	}

	private Node removeState(PetrinetState state) {
		spriteMan.removeSprite("s" + state.getState());

		return removeNode(state.getState());

	}

	private void setHighlight(Node node) {

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

	public void setReplayGraphListener(ReplayGraphListener replayGraphListener) {
		this.replayGraphListener = replayGraphListener;
	}

	// adjust arrow heads
	public void replayGraph() {
		if (replayGraphListener == null)
			return;
		replayGraphListener.onGraphReplay();

	}

	public void onScreenSizeChanged(Dimension newSize) {
		layoutManager.setScreenSize(newSize);

	}
}
