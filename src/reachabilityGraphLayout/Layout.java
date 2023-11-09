package reachabilityGraphLayout;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import core.PetrinetState;
import core.Transition;
import util.IterableMap;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * This class implements the custom layouts in {@link LayoutType} for the
 * <a href="https://graphstream-project.org/">GraphStream</a> library.
 * </p>
 */
public class Layout {

	/**
	 * The Constant NODE_SIZE -> experience has shown that smaller widths produce
	 * the best results.
	 */
	public final static Dimension NODE_SIZE = new Dimension(30, 30);

	/**
	 * The Constant SPRITE_SIZE -> experience has shown that smaller widths produce
	 * the best results.
	 */
	public final static Dimension SPRITE_SIZE = new Dimension(30, 30);

	/**
	 * The Constant MINIMAL_SIZE -> used for creating artificial nodes for spreading
	 * existing nodes apart.
	 */
	private final static Dimension MINIMAL_SIZE = new Dimension(10, 10);

	/**
	 * The Constant MINIMAL_EDGE_LENGTH -> edges can not be shorter than this value.
	 */
	private final static int MINIMAL_EDGE_LENGTH = 200;

	/** The base values for calculating x and y values. */
	private Dimension positioningBasis = new Dimension(1000, 1000);

	/** The layout type used (TREE by default). */
	private LayoutType layoutType = LayoutType.TREE;

	/** The number of most nodes in a row. */
	private static int maxRowCount = 0;

	/** The hierarchy containing all rows of nodes. */
	List<List<LayoutNode>> listHierarchy = new ArrayList<List<LayoutNode>>();

	/** The sprite manager of the GraphStream graph. */
	private SpriteManager spriteMan;

	/** A map keeping track of all nodes. */
	private IterableMap<String, LayoutNode> nodeMap = new IterableMap<String, LayoutNode>();
	/** A map keeping track of all edges. */
	private IterableMap<String, IterableMap<String, LayoutEdge>> edgeMap = new IterableMap<String, IterableMap<String, LayoutEdge>>();

	/** Defines how much the edges in the CIRCLE layout are spread apart. */
	private int circleSpreadRatio = 3;
	/**
	 * A list of all graphical objects including nodes an sprites (represented by
	 * edges).
	 */
	private List<AbstractLayoutRectangle> graphicalObjectList = new ArrayList<AbstractLayoutRectangle>();

	/**
	 * A list of all edges that go over the next level in the hierarchy meaning they
	 * potentially intersect with other edges (if they only go to the next level it
	 * is highly likely that they intersect because edges in each level are sorted
	 * according to parent).
	 */
	private List<LayoutEdge> potentialCulprits = new ArrayList<LayoutEdge>();

	/** Directions used for shifting nodes in the TREE layout. */
	private enum Direction {
		LEFT, RIGHT;
	}

	/**
	 * Instantiates a new layout.
	 *
	 * @param spriteMan  The sprite manager of the GraphStream graph.
	 * @param layoutType The custom layout type used.
	 */
	public Layout(SpriteManager spriteMan, LayoutType layoutType) {
		this.spriteMan = spriteMan;
		this.layoutType = layoutType;
	}

//	/**
//	 * Adds a new node and / or edge to the layout -> if only a node is passed it is
//	 * added without and edge; if two nodes are passed and either node does not
//	 * exist in the layout it is created and an edge is added; if both exist only
//	 * the edge is added.
//	 *
//	 * @param source         The source node for the new edge.
//	 * @param target         The target node for the new edge.
//	 * @param transition     The transition which created the edge.
//	 * @param levelToAddFrom The level from which the transition was fired from.
//	 */
//	public void add(Node source, Node target, Transition transition, int levelToAddFrom) {
//
//		// if no nodes are passed abort
//		if (source == null && target == null)
//			return;
//
//		// if only one node is passed add a node and return
//		if (source == null || target == null) {
//			// get the non null node
//			Node node = source == null ? target : source;
//
//			// if it does not already exist create the node and add it to the layout
//			if (!nodeMap.containsKey(node.getId())) {
//				LayoutNode layoutNode = new LayoutNode(node, null, 0, this);
//				nodeMap.put(node.getId(), layoutNode);
//				graphicalObjectList.add(layoutNode);
//			}
//			return;
//
//		}
//
//		// get the nodes from the node map
//		LayoutNode layoutSource = nodeMap.get(source.getId());
//		LayoutNode layoutTarget = nodeMap.get(target.getId());
//
//		// if both do not exist add them both to the layout
//		if (layoutSource == null && layoutTarget == null) {
//			// add source without a parent
//			layoutSource = new LayoutNode(source, null, levelToAddFrom, this);
//			// add target with source as parent
//			layoutTarget = new LayoutNode(target, layoutSource, levelToAddFrom + 1, this);
//
//			nodeMap.put(source.getId(), layoutSource);
//			nodeMap.put(target.getId(), layoutTarget);
//			graphicalObjectList.add(layoutSource);
//			graphicalObjectList.add(layoutTarget);
//		}
//
//		// if source == null add only source
//		else if (layoutSource == null) {
//			layoutSource = new LayoutNode(source, null, levelToAddFrom, this);
//
//			nodeMap.put(source.getId(), layoutSource);
//			graphicalObjectList.add(layoutSource);
//		}
//		// otherwise add only target
//		else {
//			layoutTarget = new LayoutNode(target, layoutSource, levelToAddFrom + 1, this);
//
//			nodeMap.put(target.getId(), layoutTarget);
//			graphicalObjectList.add(layoutTarget);
//
//		}
//
//		// get the edge string and according edge list from edge map
//		String edgeString = source.getId() + target.getId();
//		IterableMap<String, LayoutEdge> edgeList = edgeMap.get(edgeString);
//
//		// if edge list does not exist add a new entry to the map
//		if (edgeList == null) {
//			edgeMap.put(edgeString, new IterableMap<String, LayoutEdge>());
//			edgeList = edgeMap.get(edgeString);
//		}
//
//		// if edge created by transition does already exist abort
//		if (edgeList.containsKey(transition.getId()))
//			return;
//
//		// get the sprite from the sprite manager
//		Sprite spriteToAdd = spriteMan.getSprite("s" + source.getId() + target.getId() + transition.getId());
//
//		// create a new layout edge and add it to the layout
//		LayoutEdge layoutEdge = new LayoutEdge(layoutSource, layoutTarget, spriteToAdd);
//		edgeList.put(transition.getId(), layoutEdge);
//		graphicalObjectList.add(layoutEdge);
//		
//		// check if it goes beyong the previous / next level and add to potential culprits if so
//		if (!(Math.abs(layoutSource.getLevel() - layoutTarget.getLevel()) <= 1)) 
//			potentialCulprits.add(layoutEdge);
//
//		repaintNodes(true);
//
//	}

	/**
	 * Adds the.
	 *
	 * @param source     the source
	 * @param target     the target
	 * @param transition the transition
	 */
	public void add(Node source, Node target, Transition transition, int levelToAddFrom) {

		if (source == null && target == null)
			return;

		if (source == null || target == null) {
			Node node = source == null ? target : source;

			LayoutNode layoutNode = new LayoutNode(node, null, 0, this);

			if (!nodeMap.containsKey(node.getId())) {
				nodeMap.put(node.getId(), layoutNode);
				graphicalObjectList.add(layoutNode);
			}
			return;

		}

		LayoutNode layoutSource = nodeMap.get(source.getId());
		LayoutNode layoutTarget = nodeMap.get(target.getId());

		if (layoutSource == null && layoutTarget == null) {
			layoutSource = new LayoutNode(source, null, levelToAddFrom, this);
			layoutTarget = new LayoutNode(target, layoutSource, levelToAddFrom + 1, this);

			nodeMap.put(source.getId(), layoutSource);
			nodeMap.put(target.getId(), layoutTarget);

			graphicalObjectList.add(layoutSource);
			graphicalObjectList.add(layoutTarget);
		}

		else if (layoutSource == null) {
			layoutSource = new LayoutNode(source, null, levelToAddFrom, this);

			nodeMap.put(source.getId(), layoutSource);
			graphicalObjectList.add(layoutSource);
		}

		else if (layoutTarget == null) {

			layoutTarget = new LayoutNode(target, layoutSource, levelToAddFrom + 1, this);

			nodeMap.put(target.getId(), layoutTarget);
			graphicalObjectList.add(layoutTarget);

		}

		String edgeString = source.getId() + target.getId();
		IterableMap<String, LayoutEdge> edgeList = edgeMap.get(edgeString);

		if (edgeList == null) {
			edgeMap.put(edgeString, new IterableMap<String, LayoutEdge>());
			edgeList = edgeMap.get(edgeString);
		}

		if (edgeList.containsKey(transition.getId()))
			return;
		Sprite spriteToAdd = spriteMan.getSprite("s" + source.getId() + target.getId() + transition.getId());

		LayoutEdge layoutEdge = new LayoutEdge(layoutSource, layoutTarget, spriteToAdd);
		graphicalObjectList.add(layoutEdge);
		if (!(Math.abs(layoutSource.getLevel() - layoutTarget.getLevel()) <= 1)) {

			potentialCulprits.add(layoutEdge);
		}

		edgeList.put(transition.getId(), layoutEdge);

		repaintNodes(true);

	}
	private boolean checkEdgeIntersection(LayoutEdge layoutEdge) {

		LayoutNode layoutSource = layoutEdge.source;
		LayoutNode layoutTarget = layoutEdge.target;

		int startIndex = Math.min(layoutSource.getLevel(), layoutTarget.getLevel());

		int finalIndex = Math.max(layoutSource.getLevel(), layoutTarget.getLevel());

		List<LayoutNode> intersectingNodes = new ArrayList<LayoutNode>();

		boolean nonNullNodesIntersecting = false;

		for (int i = startIndex + 1; i < finalIndex; i++) {

			List<LayoutNode> nodeList = listHierarchy.get(i);

			if (nodeList.size() == 0)
				continue;

			for (int j = 0; j < nodeList.size(); j++) {

				LayoutNode ln = nodeList.get(j);

				LayoutRectangle currentNodePosition = new LayoutRectangle(getWidth(nodeList.size(), j), getHeight(i));

				if (currentNodePosition.edgeIntersectsRectangle(layoutEdge) && layoutSource.getNode() != ln.getNode()
						&& layoutTarget.getNode() != ln.getNode()) {
					intersectingNodes.add(ln);
					if (ln.getNode() != null)
						nonNullNodesIntersecting = true;
					continue;
				}

				// if the LayoutNode is a blank node also check the middle -> if it intersects
				// we might want to push the node left/right regardless, because there have been
				// intersectios before and the nodes might need to be pushed further
				if (ln.getNode() == null) {
					LayoutRectangle neighboringNodePosition = null;

					if (j > 0) {
						neighboringNodePosition = new LayoutRectangle(getWidth(nodeList.size(), j - 1), getHeight(i));
					}

					if (j < nodeList.size() - 1) {
						neighboringNodePosition = new LayoutRectangle(getWidth(nodeList.size(), j + 1), getHeight(i));
					}

					if (neighboringNodePosition == null)
						continue;

					LayoutRectangle middlePosition = new LayoutRectangle(
							(currentNodePosition.getX() + neighboringNodePosition.getX()) / 2,
							(currentNodePosition.getY() + neighboringNodePosition.getY()) / 2);

					if (middlePosition.edgeIntersectsRectangle(layoutEdge) && layoutSource.getNode() != ln.getNode()
							&& layoutTarget.getNode() != ln.getNode()) {
						intersectingNodes.add(ln);
						continue;
					}

				}

			}
		}

		if (!nonNullNodesIntersecting || intersectingNodes.size() == 0)
			return false;

		Direction direction = getDirection(intersectingNodes.get(0));

		for (LayoutNode ln : intersectingNodes) {

			List<LayoutNode> nodeList = listHierarchy.get(ln.getLevel());

			if (direction == Direction.LEFT) {
				nodeList.add(nodeList.indexOf(ln) + 1, new LayoutNode(null, null, ln.getLevel(), this));
				if (nodeList.size() > maxRowCount)
					maxRowCount = nodeList.size();
			} else {
				nodeList.add(nodeList.indexOf(ln), new LayoutNode(null, null, ln.getLevel(), this));
				if (nodeList.size() > maxRowCount)
					maxRowCount = nodeList.size();
			}
		}
		return true;
	}

	private Direction getDirection(LayoutNode layoutNode) {

		List<LayoutNode> nodeList = listHierarchy.get(layoutNode.getLevel());

		int index = nodeList.indexOf(layoutNode);

		return index <= (double) (nodeList.size() - 1) / 2 ? Direction.LEFT : Direction.RIGHT;

	}

	/**
	 * Adds the node to level.
	 *
	 * @param node the node
	 */
	protected void addNodeToLevel(LayoutNode node) {

		int lastIndex = listHierarchy.size() - 1;
		while (lastIndex < node.getLevel()) {
			lastIndex++;
			listHierarchy.add(new ArrayList<LayoutNode>());
		}

		List<LayoutNode> nodeList = listHierarchy.get(node.getLevel());

		boolean added = false;

		for (LayoutNode ln : nodeList) {
			if (ln.getNode() == null)
				continue;

			if (ln.getTag().compareTo(node.getTag()) < 0)
				continue;

			nodeList.add(nodeList.indexOf(ln), node);
			added = true;

			break;
		}

		if (!added)
			nodeList.add(node);

		if (nodeList.size() > maxRowCount)
			maxRowCount = nodeList.size();

		boolean hasEmpty = true;

		while (hasEmpty) {

			for (int i = 0; i < nodeList.size(); i++) {
				LayoutNode ln = nodeList.get(i);
				if (ln.getNode() == null) {
					nodeList.remove(i);
					hasEmpty = true;
					break;
				}
				hasEmpty = false;
			}
		}

		// TODO check all dependencies

	}

	/**
	 * 
	 */
	public void repaintNodes(boolean beautify) {

		if (layoutType == LayoutType.CIRCLE) {
			circlify();
			return;
		}

		for (List<LayoutNode> nodeList : listHierarchy) {

			if (nodeList.size() == 0)
				continue;

			for (int i = 0; i < nodeList.size(); i++) {

				LayoutNode node = nodeList.get(i);

				if (node.getNode() == null)
					continue;

				node.getNode().setAttribute("xy", getWidth(nodeList.size(), i), getHeight(node.getLevel()));
			}

		}
		if (beautify)
			beautify();

	}

	private void beautify() {

		removeBlankNodes();

		boolean workToBeDone = false;

		int loops = 0;// break after 20 loops as a safety measure
		do {
			loops++;
			for (LayoutEdge edge : potentialCulprits) {
				workToBeDone = checkEdgeIntersection(edge);
				if (workToBeDone) {
					repaintNodes(false);
					break;
				}

			}
		} while (workToBeDone && loops < 20);

		spreadSprites();
	}

	private void spreadSprites() {

		for (IterableMap<String, LayoutEdge> specificEdgeMap : edgeMap)
			for (LayoutEdge le : specificEdgeMap)
				le.sprite.setPosition(0.5, 0.5, 0);

		Map<String, IterableMap<String, LayoutEdge>> handledMaps = new HashMap<String, IterableMap<String, LayoutEdge>>();

		for (String s : edgeMap.keySet()) {

			if (handledMaps.containsKey(s))
				continue;

			IterableMap<String, LayoutEdge> specificEdgeMap = edgeMap.get(s);

			LayoutNode source = specificEdgeMap.iterator().next().source;
			LayoutNode target = specificEdgeMap.iterator().next().target;

			String oppositeEdgesString = target.getNode().getId() + source.getNode().getId();

			if (handledMaps.containsKey(oppositeEdgesString))
				continue;
			IterableMap<String, LayoutEdge> oppositeEdgesMap = edgeMap
					.get(target.getNode().getId() + source.getNode().getId());

			// handle multiple edges between two nodes going into different directions ->
			// does not handle cases where more than two edges are involved
			if (oppositeEdgesMap != null) {
				for (LayoutEdge le : specificEdgeMap)
					le.sprite.setPosition(0.33, 0.33, 0);
				for (LayoutEdge le : oppositeEdgesMap)
					le.sprite.setPosition(0.33, 0.33, 0);
			}

			// handle multiple edges between two nodes all goind into the same direction ->
			// handles arbitrarily many
			if (specificEdgeMap.size() > 1) {

				double modificatorUnit = 1.0 / specificEdgeMap.size();

				int index = 1;

				for (LayoutEdge le : specificEdgeMap) {
					Sprite sprite = le.sprite;
					sprite.setPosition(sprite.getX() * modificatorUnit * index, sprite.getY() * modificatorUnit * index,
							sprite.getZ() * modificatorUnit * index);
					index++;

				}
			}
			handledMaps.put(s, edgeMap.get(s));
			handledMaps.put(oppositeEdgesString, oppositeEdgesMap);
		}

		Collections.sort(graphicalObjectList);

		LayoutEdge lastEdge = null;

		for (int i = 0; i < graphicalObjectList.size() - 1; i++) {
			if (!(graphicalObjectList.get(i) instanceof LayoutEdge))
				continue;

			LayoutEdge edge = (LayoutEdge) graphicalObjectList.get(i);

			if (edge.length() < MINIMAL_EDGE_LENGTH)
				continue;

			if (lastEdge == null) {
				lastEdge = edge;
				continue;
			}

			LayoutRectangle middlePoint1 = new LayoutRectangle(lastEdge.getX(), lastEdge.getY(), MINIMAL_SIZE);
			LayoutRectangle middlePoint2 = new LayoutRectangle(edge.getX(), edge.getY(), MINIMAL_SIZE);

			if (middlePoint1.rectanglesIntersect(middlePoint2)) {
				lastEdge.sprite.setPosition(0.33, 0.33, 0);
				edge.sprite.setPosition(0.33, 0.33, 0);
			}

			lastEdge = edge;

		}

		for (IterableMap<String, LayoutEdge> edgeMap : edgeMap)
			for (LayoutEdge le : edgeMap) {

				if (le.length() < MINIMAL_EDGE_LENGTH)
					continue;

				Sprite sprite = le.sprite;

				final int POWER_MAX = 1;
				double power = 1;

				int counter = 0;

				while (checkSingleSprite(le, 0, graphicalObjectList.size() - 1) && power < POWER_MAX) {

					Collections.sort(graphicalObjectList);

					if (counter == (int) Math.pow(2.0, power)) {
						power++;
						counter = 0;
					}

					int sign = 1;

					if (counter % 2 == 0)
						sign = -1;

					double factor = ((counter / 2) * 2 + 1) / Math.pow(2, power);

					double newX = sprite.getX() + sprite.getX() * factor * sign;
					double newY = sprite.getY() + sprite.getY() * factor * sign;
					double newZ = sprite.getZ() + sprite.getZ() * factor * sign;
					sprite.setPosition(newX, newY, newZ);

					counter++;

				}
			}

	}

	private boolean checkSingleSprite(LayoutEdge layoutEdge, int begin, int end) {

		if (begin > end)
			return false;

		int m = (begin + end) / 2;

		AbstractLayoutRectangle gom = graphicalObjectList.get(m);

		if (gom != layoutEdge && gom.rectanglesIntersect(layoutEdge))
			return true;

		if (gom.compareTo(layoutEdge) >= 0)
			return checkSingleSprite(layoutEdge, begin, m - 1);
		else
			return checkSingleSprite(layoutEdge, m + 1, end);

	}

	private void removeBlankNodes() {

		for (List<LayoutNode> nodeList : listHierarchy) {
			List<LayoutNode> nodesToRemove = new ArrayList<LayoutNode>();

			for (LayoutNode ln : nodeList)
				if (ln.getNode() == null)
					nodesToRemove.add(ln);

			for (LayoutNode ln : nodesToRemove)
				nodeList.remove(ln);
		}
		resetMaxRow();
		if (layoutType == LayoutType.TREE)
			repaintNodes(false);
	}

	private double getHeight(int index) {

		if (listHierarchy.size() == 1)
			return -positioningBasis.getHeight() / 2;

		return -positioningBasis.getHeight() * ((double) index / (listHierarchy.size() - 1));
	}

	private double getWidth(int listSize, int index) {

		if (listSize == 1)
			return positioningBasis.getWidth() / 2;

		return positioningBasis.getWidth() * (maxRowCount - listSize + index * 2) / (2 * maxRowCount - 2);
	}

	private void circlify() {
		removeBlankNodes();
		List<LayoutNode> nodesTotal = new ArrayList<LayoutNode>();

		for (int i = 0; i < listHierarchy.size(); i++) {
			List<LayoutNode> nodeList = listHierarchy.get(i);
			nodesTotal.addAll(nodeList);
		}

		if (nodesTotal.size() <= 1)
			return;

		LayoutNode[] nodesJoined = new LayoutNode[nodesTotal.size()];

		int indexOffset = 0;

		int j = 0;
		int totalCounter = 0;

		while (j * circleSpreadRatio + indexOffset < nodesTotal.size()) {
			nodesJoined[j * circleSpreadRatio + indexOffset] = nodesTotal.get(totalCounter);

			if (((j + 1) * circleSpreadRatio + indexOffset) >= nodesTotal.size() && (indexOffset + 1) < circleSpreadRatio) {
				j = 0;
				indexOffset++;
			} else
				j++;
			totalCounter++;
		}

		double a = 2;
		double b = 1;
		for (int i = 0; i < nodesJoined.length; i++) {

			LayoutNode layoutNode = nodesJoined[i];

			if (layoutNode.getNode() == null) {
				continue;
			}
			Node node = nodesJoined[i].getNode();

			// start at Pi/2 (90°) and go around the circle step by step
			double angle = (Math.PI / 2) + (2 * Math.PI / nodesJoined.length) * i;

			// calculate coordinates -> see
			// https://www.mathopenref.com/coordparamellipse.html
			double x = a * Math.cos(angle);
			double y = b * Math.sin(angle);

			node.setAttribute("xy", x, y);

		}

		for (Sprite s : spriteMan)
			s.setPosition(0.4);

	}

	/**
	 * Removes the edge.
	 *
	 * @param stateSource the state source
	 * @param stateTarget the state target
	 * @param t           the t
	 */
	public void removeEdge(PetrinetState stateSource, PetrinetState stateTarget, Transition t) {
		IterableMap<String, LayoutEdge> specificEdgeMap = edgeMap.get(stateSource.getState() + stateTarget.getState());
		if (specificEdgeMap == null)
			return;

		LayoutEdge edge = specificEdgeMap.get(t.getId());

		if (edge == null)
			return;

		specificEdgeMap.remove(t.getId());

		if (specificEdgeMap.size() == 0)
			edgeMap.remove(stateSource.getState() + stateTarget.getState());

		graphicalObjectList.remove(edge);

		if (potentialCulprits.contains(edge))
			potentialCulprits.remove(edge);

		repaintNodes(true);

	}

	/**
	 * Removes the node.
	 *
	 * @param node the node
	 */
	public void removeNode(Node node) {
		LayoutNode layoutNode = nodeMap.get(node.getId());
		if (layoutNode == null)
			return;

		nodeMap.remove(node.getId());
		List<LayoutNode> nodeList = listHierarchy.get(layoutNode.getLevel());
		nodeList.remove(layoutNode);

		// TODO should also handle nodeList.size()==0, but it should not be a big
		// problem, except for maybe having empty

		graphicalObjectList.remove(layoutNode);
		removeBlankNodes();
		repaintNodes(true);

	}

	private void resetMaxRow() {

		maxRowCount = 0;
		for (List<LayoutNode> nodeList : listHierarchy)
			if (nodeList.size() > maxRowCount)
				maxRowCount = nodeList.size();
	}

	/**
	 * Sets the layout type.
	 *
	 * @param layoutType the new layout type
	 */
	public void setLayoutType(LayoutType layoutType) {
		
		if (layoutType == LayoutType.CIRCLE && this.layoutType == LayoutType.CIRCLE)
			circleSpreadRatio = (circleSpreadRatio + 1) % 3;
		else
		this.layoutType = layoutType;
		repaintNodes(true);

	}

}