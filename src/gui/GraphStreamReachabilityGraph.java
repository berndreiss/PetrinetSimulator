package gui;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import core.ReachabilityGraphUndoQueue;
import core.ReachabilityGraphUndoQueueState;
import core.PetrinetState;
import core.ReachabilityGraph;
import core.Transition;
import listeners.ReachabilityStateChangeListener;
import listeners.AdjustArrowHeadsListener;
import reachabilityGraphLayout.Layout;
import reachabilityGraphLayout.LayoutType;

//TODO arrow heads not adjusting when adding to auto layout
/**
 *
 * <p>
 * A <a href="https://graphstream-project.org/">GraphStream</a> implementation
 * of {@link ReachabilityGraph}.
 * </p>
 * 
 * <p>
 * The graph listens to changes in the reachability graph passed to it via a
 * {@link ReachabilityStateChangeListener}. If the reachability graph model is
 * not empty it adds all containing components. The graph implements custom
 * {@link Layout}s which can be changed (see also {@link LayoutType}).
 * </p>
 */
public class GraphStreamReachabilityGraph extends MultiGraph {

	/** The CSS file for the GraphStream graph */
	private static String CSS_FILE = "url(" + GraphStreamPetrinetGraph.class.getResource("/reachability_graph.css")
			+ ")";
	/**
	 * Listens for certain instances the graph needs to be replayed -> needed for
	 * adjusting arrow heads (see also {@link PetrinetPanel})
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

	/**
	 * Instantiates a new reachability graph.
	 *
	 * @param reachabilityGraphModel The model the graph represents.
	 * @param layoutType             The layout type the graph is set to.
	 */
	public GraphStreamReachabilityGraph(ReachabilityGraph reachabilityGraphModel, LayoutType layoutType) {
		super("Reachability Graph");

		this.layoutType = layoutType;

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
		reachabilityGraphModel.setStateChangeListener(new ReachabilityStateChangeListener() {

			@Override
			public void onSetCurrent(PetrinetState state) {

				if (state != null)
					setCurrent(getNode(state.getState()));

				
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
				setCurrentEdge(edge);
			}

		});
		// add existing states and edges in the right order
		while (queue.goForward()) {
		}
		
		//set queue to original current state
		queue.setToState(currentState);

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
		if (initialNode == null) {
			initialNode = node;
		}

		// set node current if necessary
		if (resetCurrent)
			setCurrent(node);

		// highlight the node according to its status
		setHighlight(node);

		// if there is no predecessor skip adding edge
		if (predecessor == null)
			return node;

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
		String edgeString = stateSource.getState() + stateTarget.getState() + t.getId();
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

		// remove node and sprite
		Node node = removeNode(state.getState());
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

		// if node is current node it needs to be highlighted
		if (node == currentNode) {

			// if node is initial node it needs to be signified
			if (node == initialNode) {

				// handle case M (mix of initial color and color for M)
				if (node == nodeM) {
					node.setAttribute("ui.class", "initial_m_highlight");
					return;
				}

				// handle case M Mark (mix of initial color and color for M Mark)
				if (node == nodeMMark) {
					node.setAttribute("ui.class", "initial_m_mark_highlight");
					return;
				}

				node.setAttribute("ui.class", "initial_highlight");
				return;

			}

			// handle case M -> different color for M
			if (node == nodeM) {
				node.setAttribute("ui.class", "m_highlight");
				return;
			}

			// handle case M Mark -> different color for M Mark
			if (node == nodeMMark) {
				node.setAttribute("ui.class", "m_mark_highlight");
				return;
			}

			node.setAttribute("ui.class", "highlight");
			return;
		}

		// case when node is initial node but not current node
		if (node == initialNode) {
			// handle case M (mix of initial color and color for M)
			if (node == nodeM) {
				node.setAttribute("ui.class", "initial_m");
				return;
			}
			// handle case M Mark (mix of initial color and color for M Mark)
			if (node == nodeMMark) {
				node.setAttribute("ui.class", "initial_m_mark");
				return;
			}

			node.setAttribute("ui.class", "initial");
			return;

		}
		// handle case M -> different color for M
		if (node == nodeM) {
			node.setAttribute("ui.class", "m");
			return;
		}
		// handle case M Mark -> different color for M Mark
		if (node == nodeMMark) {
			node.setAttribute("ui.class", "m_mark");
			return;
		}

		node.setAttribute("ui.class", "node");

	}

	/**
	 * Sets listener providing methods for adjusting arrowheads.
	 *
	 * @param AdjustArrowHeadsListener Listener for instances where arrow heads need
	 *                                 to be adjusted.
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
	 * @param layoutType The new layout type.
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
	 * Check whether graph has less than two nodes.
	 * 
	 * @return true if 0 or 1 nodes, false otherwise
	 */
	public boolean hasLessThanTwoNodes() {
		return nodeCount < 2;
	}
	
}