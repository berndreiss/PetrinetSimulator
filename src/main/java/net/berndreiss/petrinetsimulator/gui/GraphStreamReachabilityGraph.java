package net.berndreiss.petrinetsimulator.gui;

import java.util.ArrayList;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import net.berndreiss.petrinetsimulator.core.ReachabilityGraphUndoQueue;
import net.berndreiss.petrinetsimulator.core.ReachabilityGraphUndoQueueState;
import net.berndreiss.petrinetsimulator.core.PetrinetState;
import net.berndreiss.petrinetsimulator.core.ReachabilityGraphModel;
import net.berndreiss.petrinetsimulator.core.Transition;
import net.berndreiss.petrinetsimulator.listeners.ReachabilityStateChangedListener;
import net.berndreiss.petrinetsimulator.listeners.AdjustArrowHeadsListener;
import net.berndreiss.petrinetsimulator.reachabilityGraphLayout.Layout;
import net.berndreiss.petrinetsimulator.reachabilityGraphLayout.LayoutType;

/**
 *
 * <p>
 * A <a href="https://graphstream-project.org/">GraphStream</a> implementation
 * of {@link ReachabilityGraphModel}.
 * </p>
 * 
 * <p>
 * The graph listens to changes in the reachability graph passed to it via a
 * {@link ReachabilityStateChangedListener}. If the reachability graph model is
 * not empty it adds all containing components. The graph implements custom
 * {@link Layout}s which can be changed (see also {@link LayoutType}).
 * </p>
 */
public class GraphStreamReachabilityGraph extends MultiGraph implements ReachabilityGraph {

	/** The CSS file for the GraphStream graph */
	private String CSS_FILE = "url(" + getClass().getResource("/reachability_graph.css") + ")";
	/**
	 * Listens for certain instances the graph needs to be replayed -> needed for
	 * adjusting arrow heads (see also {@link GraphStreamPetrinetPanel})
	 */
	private AdjustArrowHeadsListener adjustArrowHeadsListener;
	/** The sprite manager */
	private SpriteManager spriteMan;
	/** The first node added -> is visually different from the other nodes */
	private Node initialNode;
	/** The current node -> is visually highlighted */
	private Node currentNode;
	/** The current edge -> is visually highlighted */
	private Edge currentEdge;
	/**
	 * The starting state on the path from which unboundedness has been detected ->
	 * is visually different from other nodes
	 */
	private Node nodeM;
	/**
	 * /** The ending state on the path from which unboundedness has been detected
	 * -> is visually different from other nodes
	 */
	private Node nodeMMark;
	/** The layout manager managing custom layouts */
	private Layout layoutManager;
	/** The layout type */
	private LayoutType layoutType = LayoutType.TREE;
	/** Flag whether boundedness is shown. */
	private boolean showBoundedness = true;
	/** Nodes on the path to unboundedness. */
	private ArrayList<Node> nodesOnPath = new ArrayList<Node>();
	/**
	 * If path signifying boundedness exists and showBoundedness was set true, on
	 * the next action update nodes that need highlighting
	 */
	private boolean updateBoundednessHighlighting = false;

	/**
	 * Instantiates a new reachability graph.
	 *
	 * @param reachabilityGraphModel the model the graph represents
	 * @param layoutType             the layout type the graph is set to
	 * @param showBoundedness        determines whether boundedness is shown or not
	 * @param pathShown              true, it reachability graph is not empty and
	 *                               path has been shown before
	 */
	GraphStreamReachabilityGraph(ReachabilityGraphModel reachabilityGraphModel, LayoutType layoutType,
			boolean showBoundedness, boolean pathShown) {
		super("Reachability Graph");

		this.layoutType = layoutType;
		this.showBoundedness = showBoundedness;

		// set the CSS file
		this.setAttribute("ui.stylesheet", CSS_FILE);

		// get the queue for the reachability states -> allows adding states of a
		// reachability model in the order they have been added in the first place ->
		// important when switching from custom layouts to auto layout and vice versa
		// because the graph is reinstantiated
		ReachabilityGraphUndoQueue queue = reachabilityGraphModel.getUndoQueue();

		// keep track of current state
		ReachabilityGraphUndoQueueState currentState = queue.getCurrentState();

		// rewind the queue to the beginning (silent because old states might still
		// think another graph exists and make changes to it when rewinding the queue)
		queue.rewindSilent();

		// einen SpriteManger für diesen Graphen erzeugen
		spriteMan = new SpriteManager(this);

		// set the layout manager
		if (layoutType != LayoutType.AUTOMATIC)
			layoutManager = new Layout(spriteMan, layoutType);

		// set the initial state if it exists (might not exist when the editor opens a
		// completely new file)
		PetrinetState initialState = reachabilityGraphModel.getInitialState();
		if (initialState != null) {
			initialNode = addState(reachabilityGraphModel.getInitialState(), null, null, true);
			setHighlight(initialNode);
		}

		// set the state change listener
		reachabilityGraphModel.setStateChangeListener(new ReachabilityStateChangedListener() {

			@Override
			public void onSetCurrent(PetrinetState state) {

				if (state != null)
					setCurrent(getNode(state.getState()));

				if (updateBoundednessHighlighting) {
					highlightPath();
					updateBoundednessHighlighting = false;
				}

			}

			@Override
			public void onResetCurrentEdge() {
				// reset current edge if necessary
				if (currentEdge == null)
					return;
				currentEdge.setAttribute("ui.class", "edge");
				currentEdge = null;

			}

			@Override
			public void onRemoveEdge(PetrinetState stateSource, PetrinetState stateTarget, Transition t) {
				Edge removedEdge = removeStateEdge(stateSource, stateTarget, t);
				if (removedEdge == currentEdge)
					currentEdge = null;

			}

			@Override
			public void onRemove(PetrinetState state) {
				if (updateBoundednessHighlighting) {
					highlightPath();
					updateBoundednessHighlighting = false;
				}

				Node node = removeState(state);
				if (currentNode == node)
					currentNode = null;
				if (initialNode == node)
					initialNode = null;
				if (nodeM == node || nodeMMark == node) {
					Node mOld = nodeM;
					Node mMarkedOld = nodeMMark;

					nodeM = null;
					nodeMMark = null;
					if (nodeM == node)
						setHighlight(mMarkedOld);
					else
						setHighlight(mOld);
				}

				adjustArrowheads();
			}

			@Override
			public void onAdd(PetrinetState state, PetrinetState predecessor, Transition t) {

				if (updateBoundednessHighlighting) {
					highlightPath();
					updateBoundednessHighlighting = false;
				}

				addState(state, predecessor, t, true);

				// reset arrow heads
				adjustArrowheads();

			}

			@Override
			public void onMarkUnboundedPath(PetrinetState m, PetrinetState mMarked) {
				markStatesUnbounded(m.getState(), mMarked.getState());
			}

			@Override
			public void onSetCurrentEdge(String edge) {
				if (updateBoundednessHighlighting) {
					highlightPath();
					updateBoundednessHighlighting = false;
				}
				setCurrentEdge(edge);
			}

			@Override
			public void onResetPath() {
				ArrayList<Node> pathNodes = new ArrayList<Node>();

				for (Node n : nodesOnPath)
					pathNodes.add(n);

				nodesOnPath.clear();

				for (Node n : pathNodes)
					n.setAttribute("ui.class", "node");

			}

			@Override
			public void onAddToPath(PetrinetState state) {
				Node node = getNode(state.getState());
				nodesOnPath.add(node);
				setHighlight(node);
			}

		});
		// add existing states and edges in the right order until encountering current
		// state
		while (queue.getCurrentState() != currentState && queue.goForward()) {
		}

		PetrinetState invalidState = reachabilityGraphModel.getLastStateOnUnboundednessPath();

		// handle reachability graphs that are unbounded
		if (invalidState != null) {
			// get the nodes m and m' and highlight them
			nodeMMark = getNode(invalidState.getState());
			nodeM = getNode(invalidState.getM().getState());

			// get paths from m' to m and from m to initial node
			for (PetrinetState ps : invalidState.getPathFromOtherState(invalidState.getM())) {
				nodesOnPath.add(getNode(ps.getState()));
			}
			for (PetrinetState ps : invalidState.getM()
					.getPathFromOtherState(reachabilityGraphModel.getInitialState())) {
				nodesOnPath.add(getNode(ps.getState()));
			}

			// highlight path
			if (showBoundedness || pathShown) {
				boolean boundednessOld = showBoundedness;
				this.showBoundedness = true;
				highlightPath();
				this.showBoundedness = boundednessOld;
			}

		}

	}

	// adds a state with an edge going from the predecessor to the new state
	// is resetCurrent == true set new state and edge to current
	private Node addState(PetrinetState newState, PetrinetState predecessor, Transition t, boolean resetCurrent) {

		// node for new state
		Node node;

		// get id for new state
		String id = newState.getState();

		// get id from transition ("" if it is null)
		String transitionId = t == null ? "" : t.getId();

		// check if node with id already exists and create if necessary
		node = this.getNode(id);
		if (node == null) {
			node = addNode(id);
			node.setAttribute("ui.label", id);
		}

		// if it is first node added set initial node
		if (initialNode == null) 
			initialNode = node;

		// set node current if necessary
		if (resetCurrent)
			setCurrent(node);

		// highlight the node according to its status
		setHighlight(node);

		// if there is no predecessor skip adding edge
		if (predecessor != null) {

			// check if predecessor node exists and add if necessary
			Node predNode = getNode(predecessor.getState());
			if (predNode == null) {
				predNode = addNode(predecessor.getState());
				predNode.setAttribute("ui.label", predecessor.getState());
			}

			// check if the new edge exists and add if necessary
			Edge newEdge = getEdge(predecessor.getState() + id + transitionId);

			if (newEdge == null) {
				newEdge = this.addEdge(predecessor.getState() + id + transitionId, predNode, node, true);
				// set sprite to the edge having the transition as a label
				Sprite sprite = spriteMan.addSprite("s" + newEdge.getId());
				sprite.setAttribute("ui.class", "edgeLabel");
				String label = GraphStreamPetrinetGraph.getElementLabel(t);
				sprite.setAttribute("ui.label", label);
				sprite.attachToEdge(newEdge.getId());
				sprite.setPosition(0.5);
			}

			// set new edge to current if necessary
			if (resetCurrent) {
				newEdge.setAttribute("ui.class", "highlight");
				if (currentEdge != null && currentEdge != newEdge)
					currentEdge.setAttribute("ui.class", "edge");

				currentEdge = newEdge;
			}
		}
		
		// add the node to custom layout if one is set
		if (layoutType != LayoutType.AUTOMATIC) 
			layoutManager.add(getNode(predecessor == null ? null : predecessor.getState()), node, t,
					predecessor == null ? 0 : predecessor.getLevel());
		
		return node;
	}

	// set edge to current
	private void setCurrentEdge(String edgeString) {

		// unmark the currently highlighted edge
		if (currentEdge != null)
			currentEdge.setAttribute("ui.class", "edge");

		// if edgeString == null abort
		if (edgeString == null) {
			currentEdge = null;
			return;
		}

		Edge edge = getEdge(edgeString);

		// if edge does not exist abort
		if (edge == null)
			return;

		// highlight and set new current edge
		edge.setAttribute("ui.class", "highlight");
		currentEdge = edge;
	}

	// mark beginning and ending nodes on the path showing unboundedness
	private void markStatesUnbounded(String m, String mMark) {

		// get old marked nodes in order to unmark them if they exist
		Node oldM = nodeM;
		Node oldMMarked = nodeMMark;

		// set new marked nodes
		nodeM = this.getNode(m);
		nodeMMark = this.getNode(mMark);

		// unmark old and mark new nodes
		setHighlight(oldM);
		setHighlight(oldMMarked);
		if (!showBoundedness)
			return;
		setHighlight(nodeM);
		setHighlight(nodeMMark);

	}

	// set a node to be the current node
	private void setCurrent(Node node) {

		if (node == null)
			return;

		// if node and current node are not the same unmark old current node and set and
		// mark new one
		if (node != currentNode) {
			Node oldCurrent = currentNode;
			currentNode = node;
			setHighlight(currentNode);
			setHighlight(oldCurrent);// if it was null the method just returns
		}

	}

	// remove an edge from the graph
	private Edge removeStateEdge(PetrinetState stateSource, PetrinetState stateTarget, Transition t) {

		// remove edge and sprite
		String edgeString = getEdgeId(stateSource, stateTarget, t);
		Edge edge = getEdge(edgeString);
		if (edge == null)
			return null;
		Edge removedEdge = removeEdge(edgeString);
		spriteMan.removeSprite("s" + edgeString);

		// remove edge from layout manager if custom layout is set
		if (layoutType != LayoutType.AUTOMATIC)
			layoutManager.removeEdge(stateSource, stateTarget, t);

		return removedEdge;
	}

	// remove a state from the graph
	private Node removeState(PetrinetState state) {

		// check if node exists
		Node node = getNode(state.getState());

		// safety check
		if (node == null)
			return null;

		if (initialNode == node)
			initialNode = null;

		// handle marked nodes (m, m' and nodes on path)
		if (nodeMMark == node || nodeM == node) {
			Node mTemp = nodeM;
			Node mMarkTemp = nodeMMark;
			nodeMMark = null;
			nodeM = null;
			setHighlight(mTemp);
			setHighlight(mMarkTemp);

			ArrayList<Node> pathNodes = new ArrayList<Node>();
			for (Node n : nodesOnPath) {
				pathNodes.add(n);
			}

			for (Node n : pathNodes) {
				nodesOnPath.remove(n);
				setHighlight(n);
			}
		}

		// remove node and sprite
		node = removeNode(state.getState());
		spriteMan.removeSprite("s" + state.getState());

		// remove node from layout manager if custom layout is set
		if (layoutType != LayoutType.AUTOMATIC)
			layoutManager.removeNode(node);

		return node;

	}

	// set node to according ui.class
	private void setHighlight(Node node) {

		// safety check
		if (node == null)
			return;

		String nodeClass = (String) node.getAttribute("ui.class");
		// if node is current node it needs to be highlighted
		if (node == currentNode) {

			// if node is initial node it needs to be signified
			if (node == initialNode) {

				// handle case M (mix of initial color and color for M)
				if ((node == nodeM && showBoundedness) || nodeClass != null && nodeClass.equals("initial_m")) {
					node.setAttribute("ui.class", "initial_m_highlight");
					return;
				}

				// handle case M Mark (mix of initial color and color for M Mark)
				if ((node == nodeMMark && showBoundedness) || nodeClass != null && nodeClass.equals("initial_m_mark")) {
					node.setAttribute("ui.class", "initial_m_mark_highlight");
					return;
				}

				node.setAttribute("ui.class", "initial_highlight");
				return;

			}

			// handle case M -> different color for M
			if ((node == nodeM && showBoundedness) || nodeClass != null && nodeClass.equals("m")) {
				node.setAttribute("ui.class", "m_highlight");
				return;
			}

			// handle case M Mark -> different color for M Mark
			if ((node == nodeMMark && showBoundedness) || nodeClass != null && nodeClass.equals("m_mark")) {
				node.setAttribute("ui.class", "m_mark_highlight");
				return;
			}

			if ((nodesOnPath.contains(node) && showBoundedness) || nodeClass != null && nodeClass.equals("path")) {
				node.setAttribute("ui.class", "path_highlight");
				return;
			}

			node.setAttribute("ui.class", "highlight");
			return;
		}

		// case when node is initial node but not current node
		if (node == initialNode) {
			// handle case M (mix of initial color and color for M)
			if ((node == nodeM && showBoundedness) || nodeClass != null && nodeClass.equals("initial_m_highlight")) {
				node.setAttribute("ui.class", "initial_m");
				return;
			}
			// handle case M Mark (mix of initial color and color for M Mark)
			if ((node == nodeMMark && showBoundedness)
					|| nodeClass != null && nodeClass.equals("initial_m_mark_highlight")) {
				node.setAttribute("ui.class", "initial_m_mark");
				return;
			}

			node.setAttribute("ui.class", "initial");
			return;

		}
		// handle case M -> different color for M
		if ((node == nodeM && showBoundedness) || nodeClass != null && nodeClass.equals("m_highlight")) {
			node.setAttribute("ui.class", "m");
			return;
		}
		// handle case M Mark -> different color for M Mark
		if ((node == nodeMMark && showBoundedness) || nodeClass != null && nodeClass.equals("m_mark_highlight")) {
			node.setAttribute("ui.class", "m_mark");
			return;
		}

		if ((nodesOnPath.contains(node) && showBoundedness)
				|| nodeClass != null && nodeClass.equals("path_highlight")) {

			node.setAttribute("ui.class", "path");
			return;
		}

		node.setAttribute("ui.class", "node");

	}

	// returns the id of an edge defined by its source, target and transition
	private String getEdgeId(PetrinetState source, PetrinetState target, Transition t) {
		return source.getState() + target.getState() + t.getId();

	}

	/**
	 * Sets listener providing methods for adjusting arrowheads.
	 *
	 * @param AdjustArrowHeadsListener listener for instances where arrow heads need
	 *                                 to be adjusted
	 */
	public void setAdjustArrowHeadsListener(AdjustArrowHeadsListener AdjustArrowHeadsListener) {
		this.adjustArrowHeadsListener = AdjustArrowHeadsListener;
	}

	// invokes the adjustArrowHeadsListener if it has been set
	private void adjustArrowheads() {
		if (adjustArrowHeadsListener == null)
			return;
		adjustArrowHeadsListener.onAdjustArrowHeads();
	}

	/**
	 * Sets the layout type.
	 *
	 * @param layoutType the new layout type
	 */
	public void setLayoutType(LayoutType layoutType) {
		this.layoutType = layoutType;
		if (layoutType != LayoutType.AUTOMATIC) {
			layoutManager.setLayoutType(layoutType);
			adjustArrowheads();
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
	 * Checks whether graph has less than two nodes.
	 * 
	 * @return true if 0 or 1 nodes, false otherwise
	 */
	boolean hasLessThanTwoNodes() {
		return nodeCount < 2;
	}

	@Override
	public void setShowBoundedness(boolean show) {
		showBoundedness = show;

		if (show)
			updateBoundednessHighlighting = true;
	}

	// highlight nodes m, m' and path
	private void highlightPath() {

		if (nodeM != null)
			setHighlight(nodeM);

		if (nodeMMark != null)
			setHighlight(nodeMMark);

		for (Node n : nodesOnPath)
			setHighlight(n);
	}

	@Override
	public boolean getShowBoundedness() {
		return showBoundedness;

	}

	/**
	 * 
	 * Gets whether path is currently shown or not. returns true if path is shown
	 * 
	 * @return true, if path is shown
	 */
	public boolean pathShown() {
		if (nodeM == null)
			return false;

		String mClass = (String) nodeM.getAttribute("ui.class");
		if (mClass.equals("initial_m") || mClass.equals("initial_m_highlight") || mClass.equals("m")
				|| mClass.equals("m_highlight"))
			return true;
		return false;
	}
}