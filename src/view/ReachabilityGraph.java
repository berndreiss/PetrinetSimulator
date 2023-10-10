package view;

import javax.swing.JPanel;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;
import org.graphstream.ui.swing_viewer.ViewPanel;

import control.PetrinetController;
import datamodel.PetrinetState;
import datamodel.StateChangeListener;
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
	
	private ViewPanel viewPanel;

	public ReachabilityGraph(PetrinetController controller) {
		super("");
		this.controller = controller;

		// TODO: mark inital node
		// Angabe einer css-Datei für das Layout des Graphen
		this.setAttribute("ui.stylesheet", CSS_FILE);

		// einen SpriteManger für diesen Graphen erzeugen
		spriteMan = new SpriteManager(this);
		
		init();

	}

	private void init() {
		initialNode = addState(controller.getReachabilityGraphModel().getInitialState(),null,null);
		setHighlight(initialNode);
		
		controller.getReachabilityGraphModel().setStateChangeListener(new StateChangeListener() {
			
			@Override
			public void onSetInitial(PetrinetState state) {
				initialNode = addState(state,null, null);
				setHighlight(initialNode);
			}
			
			@Override
			public void onSetCurrent(PetrinetState state) {
				setCurrentState(state);
			}
			
			@Override
			public void onRemoveEdge(PetrinetState stateSource, PetrinetState stateTarget, Transition t) {
				removeStateEdge(stateSource, stateTarget, t);
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
					setHighlight(mOld);
					setHighlight(nodeMMarked);
				}
				
			}
			
		
			@Override
			public void onAdd(PetrinetState state, PetrinetState predecessor, Transition t) {
				addState(state, predecessor, t);
			}

			@Override
			public void onMarkInvalid(PetrinetState m, PetrinetState mMarked) {
				markStatesInvalid(m.getState(), mMarked.getState());
			}
		});
	}
	
	public void reinitialize() {
		removeNode(initialNode);
		init();
	}
	
	public void setViewPanel(ViewPanel viewPanel) {
		this.viewPanel = viewPanel;
	}

	private Node addState(PetrinetState state, PetrinetState predecessor, Transition t) {
//TODO can i remove all headlesses?
		
		if (controller.getHeadless())
			return null;
		
		Node node;
		String id = state.getState();
		String transitionLabel = t==null?"":PetrinetGraph.getElementLabel(t);

		if (currentNode == null) {
			node = this.addNode(id);
			node.setAttribute("ui.label", id);
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

		if (predecessor == null)
			return node;
		
		Node predNode = getNode(predecessor.getState());
		if (this.getEdge(predecessor.getState() + id +transitionLabel) == null) {
			Edge e = this.addEdge(predecessor.getState() + id+transitionLabel, predNode, node, true);
			Sprite sprite = spriteMan.addSprite("s" + e.getId());
			sprite.setAttribute("ui.class", "edgeLabel");
			sprite.setAttribute("ui.label", transitionLabel);

			sprite.attachToEdge(e.getId());
			sprite.setPosition(0.5);

		}

		return node;
	}

	private void setCurrentState(PetrinetState state) {
		if (controller.getHeadless())
			return;
		
		setCurrent(getNode(state.getState()));
	}

	private void markStatesInvalid(String m, String mMark) {
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

	private void setCurrent(Node node) {
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
	
	private void removeStateEdge(PetrinetState stateSource, PetrinetState stateTarget, Transition t) {
		String edgeString = stateSource.getState() + stateTarget.getState() + PetrinetGraph.getElementLabel(t);
		removeEdge(edgeString);
		spriteMan.removeSprite("s" + edgeString);
	}
	
	private Node removeState(PetrinetState state) {
		spriteMan.removeSprite("s" + state.getState());
		return removeNode(state.getState());
		
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
