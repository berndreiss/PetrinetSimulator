package gui;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import core.PetrinetState;
import core.ReachabilityGraphModel;
import core.Transition;
import listeners.ReachabilityStateChangeListener;
import listeners.ReplayGraphListener;
import reachabilityGraphLayout.Layout;
import reachabilityGraphLayout.LayoutType;

// TODO: Auto-generated Javadoc
/**
 * The Class ReachabilityGraph.
 */
public class ReachabilityGraph extends MultiGraph {

	private static String CSS_FILE = "url(" + GraphStreamPetrinetGraph.class.getResource("/reachability_graph.css") + ")";

	private ReplayGraphListener replayGraphListener;

	private SpriteManager spriteMan;

	private Node initialNode;

	private Node currentNode;
	private Edge currentEdge;

	private Node nodeM;

	private Node nodeMMarked;

	private Layout layoutManager;

	private LayoutType layoutType = LayoutType.TREE;

	/**
	 * Instantiates a new reachability graph.
	 *
	 * @param reachabilityGraphModel the controller
	 */
	public ReachabilityGraph(ReachabilityGraphModel reachabilityGraphModel, LayoutType layoutType) {
		super("");

		this.layoutType = layoutType;

		// Angabe einer css-Datei für das Layout des Graphen
		this.setAttribute("ui.stylesheet", CSS_FILE);

		// einen SpriteManger für diesen Graphen erzeugen
		spriteMan = new SpriteManager(this);

		if (layoutType != LayoutType.AUTOMATIC)
			layoutManager = new Layout(spriteMan, layoutType);

		PetrinetState initialState = reachabilityGraphModel.getInitialState();

		if (initialState != null) {

			initialNode = addState(reachabilityGraphModel.getInitialState(), null, null, true);
			setHighlight(initialNode);

			for (PetrinetState ps : reachabilityGraphModel.getStates())
				for (PetrinetState successor : ps.getSuccessors())
					for (Transition t : ps.getTransitions(successor))
						addState(successor, ps, t, false);

		}

		if (reachabilityGraphModel.getCurrentState() != null)
			setCurrent(getNode(reachabilityGraphModel.getCurrentState().getState()));
		if (reachabilityGraphModel.getCurrentEdge() != null)
			getEdge(reachabilityGraphModel.getCurrentEdge()).setAttribute("ui.class", "highlight");

		reachabilityGraphModel.setStateChangeListener(new ReachabilityStateChangeListener() {

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

				replayGraph();
			}

			@Override
			public void onAdd(PetrinetState state, PetrinetState predecessor, Transition t) {
				addState(state, predecessor, t, true);

				replayGraph();
			}

			@Override
			public void onMarkInvalid(PetrinetState m, PetrinetState mMarked) {
				markStatesInvalid(m.getState(), mMarked.getState());
			}

		});

	}

	private Node addState(PetrinetState state, PetrinetState predecessor, Transition t, boolean resetCurrent) {

		Node node;
		String id = state.getState();
		String transitionId = t == null ? "" : t.getId();

		node = this.getNode(id);

		if (node == null) {
			node = addNode(id);

			node.setAttribute("ui.label", id);
		}

		if (initialNode == null) {
			initialNode = node;
		}
		if (resetCurrent) {
			setCurrent(node);
		}

		setHighlight(node);
		if (predecessor == null)
			return node;

		Node predNode = getNode(predecessor.getState());

		if (predNode == null) {
			predNode = addNode(predecessor.getState());
			predNode.setAttribute("ui.label", predecessor.getState());
		}
		Edge newEdge = this.getEdge(predecessor.getState() + id + transitionId);

		if (newEdge == null) {

			newEdge = this.addEdge(predecessor.getState() + id + transitionId, predNode, node, true);

			Sprite sprite = spriteMan.addSprite("s" + newEdge.getId());
			sprite.setAttribute("ui.class", "edgeLabel");
			String label = GraphStreamPetrinetGraph.getElementLabel(t);
			sprite.setAttribute("ui.label", label);
			sprite.attachToEdge(newEdge.getId());
			if (layoutType == LayoutType.AUTOMATIC)
				sprite.setPosition(0.5);
			else
				sprite.setPosition(0.5, 0.5, 0);
		}

		if (resetCurrent) {
			newEdge.setAttribute("ui.class", "highlight");
			if (currentEdge != null && currentEdge != newEdge)
				currentEdge.setAttribute("ui.class", "edge");

			currentEdge = newEdge;
		}
		if (layoutType != LayoutType.AUTOMATIC)
			layoutManager.add(getNode(predecessor == null ? null : predecessor.getState()), node, t,
					predecessor == null ? 0 : predecessor.getLevel());

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
		String edgeString = stateSource.getState() + stateTarget.getState() + t.getId();
		Edge removedEdge = removeEdge(edgeString);
		spriteMan.removeSprite("s" + edgeString);
		if (layoutType != LayoutType.AUTOMATIC)
			layoutManager.removeEdge(stateSource, stateTarget, t);

		return removedEdge;
	}

	private Node removeState(PetrinetState state) {
		spriteMan.removeSprite("s" + state.getState());

		Node node = removeNode(state.getState());

		if (layoutType != LayoutType.AUTOMATIC)
			layoutManager.removeNode(node);

		return node;

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

	/**
	 * Sets the replay graph listener.
	 *
	 * @param replayGraphListener the new replay graph listener
	 */
	public void setReplayGraphListener(ReplayGraphListener replayGraphListener) {
		this.replayGraphListener = replayGraphListener;
	}

	/**
	 * Replay graph.
	 */
	// adjust arrow heads
	private void replayGraph() {
		if (replayGraphListener == null)
			return;
		replayGraphListener.onGraphReplay();

	}

	/**
	 * Sets the layout type.
	 *
	 * @param layoutType the new layout type
	 */
	public void setLayoutType(LayoutType layoutType) {
		this.layoutType = layoutType;
		if (layoutType != LayoutType.AUTOMATIC) {

			if (layoutManager == null)
				layoutManager = new Layout(spriteMan, layoutType);

			layoutManager.setLayoutType(layoutType);
			replayGraph();
		}
	}

	/**
	 * Gets the layout type.
	 *
	 * @return the layout type
	 */
	public LayoutType getLayoutType() {
		return layoutType;
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasLessThanTwoNodes() {
		return nodeCount < 2;
	}

	// TODO REMOVE
	/**
	 * Prints the.
	 */
	public void print() {
		System.out.println("NODES");
		for (Node n : this)
			System.out.println(n.getId());
		System.out.println("EDGES");
		edges().forEach(e -> System.out.println(e.getId()));
		System.out.println("SPRITES");
		for (Sprite s : spriteMan.sprites())
			System.out.println(s.getId());

		System.out.println();
	}
}