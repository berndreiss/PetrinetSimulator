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
	 * Defines how much the edges in the CIRCLE layout are spread apart at maximum.
	 */
	private static final int MAX_CIRCLE_SPREAD_RATIO = 5;

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
	 * @param spriteMan  the sprite manager of the GraphStream graph
	 * @param layoutType the custom layout type used
	 */
	public Layout(SpriteManager spriteMan, LayoutType layoutType) {
		this.spriteMan = spriteMan;
		this.layoutType = layoutType;
	}

	/**
	 * Adds a new node and / or edge to the layout -> if only a node is passed it is
	 * added without and edge; if two nodes are passed and either node does not
	 * exist in the layout it is created and an edge is added; if both exist only
	 * the edge is added.
	 *
	 * @param source         the source node for the new edge
	 * @param target         the target node for the new edge
	 * @param transition     the transition which created the edge
	 * @param levelToAddFrom the level from which the transition was fired from
	 */
	public void add(Node source, Node target, Transition transition, int levelToAddFrom) {

		// if no nodes are passed abort
		if (source == null && target == null)
			return;

		// if only one node is passed add a node and return
		if (source == null || target == null) {
			// get the non null node
			Node node = source == null ? target : source;

			// if it does not already exist create the node and add it to the layout
			if (!nodeMap.containsKey(node.getId())) {
				LayoutNode layoutNode = new LayoutNode(node, null, 0, this);
				nodeMap.put(node.getId(), layoutNode);
				graphicalObjectList.add(layoutNode);
			}
			return;

		}

		// get the nodes from the node map
		LayoutNode layoutSource = nodeMap.get(source.getId());
		LayoutNode layoutTarget = nodeMap.get(target.getId());

		// if both do not exist add them both to the layout
		if (layoutSource == null && layoutTarget == null) {
			// add source without a parent
			layoutSource = new LayoutNode(source, null, levelToAddFrom, this);
			// add target with source as parent
			layoutTarget = new LayoutNode(target, layoutSource, levelToAddFrom + 1, this);

			nodeMap.put(source.getId(), layoutSource);
			nodeMap.put(target.getId(), layoutTarget);
			graphicalObjectList.add(layoutSource);
			graphicalObjectList.add(layoutTarget);
		}

		// if source == null add only source
		else if (layoutSource == null) {
			layoutSource = new LayoutNode(source, null, levelToAddFrom, this);

			nodeMap.put(source.getId(), layoutSource);
			graphicalObjectList.add(layoutSource);
		}
		// otherwise add only target
		else if (layoutTarget == null) {
			layoutTarget = new LayoutNode(target, layoutSource, levelToAddFrom + 1, this);

			nodeMap.put(target.getId(), layoutTarget);
			graphicalObjectList.add(layoutTarget);

		}

		// get the edge string and according edge list from edge map
		String edgeString = source.getId() + target.getId();
		IterableMap<String, LayoutEdge> edgeList = edgeMap.get(edgeString);

		// if edge list does not exist add a new entry to the map
		if (edgeList == null) {
			edgeMap.put(edgeString, new IterableMap<String, LayoutEdge>());
			edgeList = edgeMap.get(edgeString);
		}

		// if edge created by transition does already exist abort
		if (edgeList.containsKey(transition.getId()))
			return;

		// get the sprite from the sprite manager
		Sprite spriteToAdd = spriteMan.getSprite("s" + source.getId() + target.getId() + transition.getId());

		// create a new layout edge and add it to the layout
		LayoutEdge layoutEdge = new LayoutEdge(layoutSource, layoutTarget, spriteToAdd);
		edgeList.put(transition.getId(), layoutEdge);
		graphicalObjectList.add(layoutEdge);

		// check if it goes beyong the previous / next level and add to potential
		// culprits if so
		if (!(Math.abs(layoutSource.getLevel() - layoutTarget.getLevel()) <= 1))
			potentialCulprits.add(layoutEdge);

		repaintNodes(true);

	}

	/**
	 * Adds the node to its level at the according index. The index is chosen
	 * primarily in accordance with the parent (for more information see
	 * LayoutNode).
	 *
	 * @param node the node to be added
	 */
	public void addNodeToLevel(LayoutNode node) {

		int lastIndex = listHierarchy.size() - 1;

		// if for some reason levels do not exist create empty lists for those levels
		while (lastIndex < node.getLevel()) {
			lastIndex++;
			listHierarchy.add(new ArrayList<LayoutNode>());
		}

		// the list for the level in the hierarchy
		List<LayoutNode> nodeList = listHierarchy.get(node.getLevel());

		// keep track of whether the node has been added
		boolean added = false;

		// get the correct place for the node in the list -> the list is ordered by the
		// node tags which consist of the tag of the parent and its own place in the
		// list (at this point it does not matter, that it does not have an index yet
		// because the main goal is to group all nodes belonging to the same parent
		// together to avoid unnecessary intersections between edges); skip blank nodes
		for (LayoutNode ln : nodeList) {
			if (ln.getNode() == null)
				continue;

			if (ln.getTag().compareTo(node.getTag()) < 0)
				continue;

			nodeList.add(nodeList.indexOf(ln), node);
			added = true;

			break;
		}

		// add if node has not yet been added
		if (!added)
			nodeList.add(node);

		// update max row count
		if (nodeList.size() > maxRowCount)
			maxRowCount = nodeList.size();

	}

	// repaint nodes according to the layout type
	private void repaintNodes(boolean beautify) {

		//
		if (layoutType == LayoutType.CIRCLE) {
			circlify();
			return;
		}

		if (layoutType == LayoutType.TREE) {
			treeify(beautify);
			return;
		}
	}

	// repaint the nodes -> x and y values are calculated dynamically according to
	// the level in the hierarchy and the index in the list on the level -> the
	// formulas for calculating x and y are defined in the methods getX and getY
	private void treeify(boolean beautify) {
		for (List<LayoutNode> nodeList : listHierarchy) {

			if (nodeList.size() == 0)
				continue;

			for (int i = 0; i < nodeList.size(); i++) {

				LayoutNode node = nodeList.get(i);

				if (node.getNode() == null)
					continue;

				node.getNode().setAttribute("xy", getX(nodeList.size(), i), getY(node.getLevel()));
			}

		}

		// spread nodes and sprites to minimize intersections
		if (beautify)
			beautify();

	}

	// spread the nodes evenly along the y axis
	private double getY(int index) {

		if (listHierarchy.size() == 1)
			return -positioningBasis.getHeight() / 2;

		return -positioningBasis.getHeight() * ((double) index / (listHierarchy.size() - 1));
	}

	// spread the nodes evenly along the x axis
	private double getX(int listSize, int index) {

		if (listSize == 1)
			return positioningBasis.getWidth() / 2;

		return positioningBasis.getWidth() * (maxRowCount - listSize + index * 2) / (2 * maxRowCount - 2);
	}

	// spread nodes and sprites to minimize intersections
	private void beautify() {

		// start with a blank sleet
		removeBlankNodes();

		boolean workToBeDone = false;
		int loops = 0;// break after 20 loops as a safety measure

		// as long as we find edges that intersect with nodes add blank nodes to spread
		// them apart and recalculate node positions
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

		// spread the sprites
		spreadSprites();
	}

	/*
	 * Checks whether an edge intersects with any nodes. If so it pushes the nodes
	 * to the LEFT or RIGHT by adding blank nodes in its place.
	 * 
	 */
	private boolean checkEdgeIntersection(LayoutEdge layoutEdge) {

		// get source and target of the edge
		LayoutNode layoutSource = layoutEdge.source;
		LayoutNode layoutTarget = layoutEdge.target;

		// get the two levels as start and final index
		int startIndex = Math.min(layoutSource.getLevel(), layoutTarget.getLevel());
		int finalIndex = Math.max(layoutSource.getLevel(), layoutTarget.getLevel());

		// keep track of all nodes intersecting with the edge
		List<LayoutNode> intersectingNodes = new ArrayList<LayoutNode>();

		// keep track of whether any null nodes are intersecting the edge
		boolean nonNullNodesIntersecting = false;

		// check all nodes on all levels between source and target in the hierarchy
		for (int i = startIndex + 1; i < finalIndex; i++) {

			// get the node list of the current level in the hierarchy
			List<LayoutNode> nodeList = listHierarchy.get(i);

			// if there are no nodes there's nothing to be done
			if (nodeList.size() == 0)
				continue;

			// for every node in the list
			for (int j = 0; j < nodeList.size(); j++) {

				// get node at current position
				LayoutNode ln = nodeList.get(j);

				// get a rectangle representing the current position -> if a blank node has been
				// added at the position it does not bear coordinates because its GraphStream
				// node == null
				LayoutRectangle currentNodePosition = new LayoutRectangle(getX(nodeList.size(), j), getY(i));

				// if the edge is intersecting the current position and the element at the
				// position is neither the source nor the target add it to the intersecting
				// nodes
				if (currentNodePosition.edgeIntersectsRectangle(layoutEdge) && layoutSource.getNode() != ln.getNode()
						&& layoutTarget.getNode() != ln.getNode()) {
					intersectingNodes.add(ln);

					// if the layout node was not a blank node set the non null nodes intersecting
					// marker
					if (ln.getNode() != null)
						nonNullNodesIntersecting = true;
					continue;
				}

				/*-
				 * EXPLANATION FOR THE CODE FOLLOWING BELOW
				 * 
				 * If the layout node is a blank node also check the middle to the neighboring
				 * node in the push direction -> if it intersects the middle we might want to 
				 * push the node left/right regardless, because there have been intersections 
				 * before and the nodes might need to be pushed further. 
				 * 
				 * Consider the following example:
				 *
				 * Step 1: between the source s and the target t lies the intersecting node x on level 2:
				 * 
				 * 1   t
				 * 2   x   
				 * 3   s
				 *
				 * Step 2: a blank node [b] is added on level 2 and x is pushed to the right creating blank space in the middle:
				 *  
				 * 1      t
				 * 2  [b]    x  
				 * 3      s
				 *
				 * Step 3: a new source s' and target t' are checked with the old source and target intersecting:
				 * 
				 * 0      t'
				 * 1      t
				 * 2  [b]    x  
				 * 3      s
				 * 4      s'
				 *
				 * Step 4: both are pushed to the right by adding blank nodes [b'] on level 1 and 3 but now x is between them again:
				 *
				 * 0      t'
				 * 1  [b']  t  
				 * 2  [b]   x  
				 * 3  [b']  s
				 * 4      s'
				 *
				 *  If instead in step 3 we checked whether the middle between [b] and x intersects we would have known that we have to 
				 *  push x further. This is achieved by adding another blank node ['b'] 
				 *
				 * 0         t'
				 * 1    [b']     t  
				 * 2 [b]   ['b']    x   
				 * 3    [b']     s
				 * 4         s'
				 *      
				 *  Now there are no intersections anymore except for blank, non visible nodes
				 *      
				 */

				if (ln.getNode() == null) {
					LayoutRectangle neighboringNodePosition = null;

					if (getDirection(ln) == Direction.LEFT && j > 0) {
						neighboringNodePosition = new LayoutRectangle(getX(nodeList.size(), j - 1), getY(i));
					}

					if (getDirection(ln) == Direction.RIGHT && j < nodeList.size() - 1) {
						neighboringNodePosition = new LayoutRectangle(getX(nodeList.size(), j + 1), getY(i));
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

		// if only blank nodes intersected or intersecting nodes list is empty there is
		// nothing to be done
		if (!nonNullNodesIntersecting || intersectingNodes.size() == 0)
			return false;

		// get the direction to push the intersecting nodes in (any node in the list can
		// be used for this)
		Direction direction = getDirection(intersectingNodes.get(0));

		// push all intersecting nodes to the RIGHT / LEFT
		for (LayoutNode ln : intersectingNodes) {

			// get the node list for the level of the node in the hierarchy
			List<LayoutNode> nodeList = listHierarchy.get(ln.getLevel());

			// add a blank node and update max row count
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

	// returns the direction nodes should be pushed in: if the index of a node is in
	// the first half of the list push to the LEFT, if it is in the second half push
	// to the RIGHT, push LEFT by default
	private Direction getDirection(LayoutNode layoutNode) {

		List<LayoutNode> nodeList = listHierarchy.get(layoutNode.getLevel());
		int index = nodeList.indexOf(layoutNode);
		return index <= (double) (nodeList.size() - 1) / 2 ? Direction.LEFT : Direction.RIGHT;

	}

	/*
	 * When spreading the sprites there are two steps to be taken: 1. Parallel edges
	 * between the same source and target are handled. 2. Edges crossing each other
	 * need to be handled if sprites intersect.
	 * 
	 * The method implemented here is by no means perfect but aims to minimize the
	 * overlapping of nodes and sprites.
	 */
	private void spreadSprites() {

		// reset all sprites to start with a blank sleet
		for (IterableMap<String, LayoutEdge> specificEdgeMap : edgeMap)
			for (LayoutEdge le : specificEdgeMap)
				le.sprite.setPosition(0.5, 0.5, 0);// also defining the y value helps them spread more evenly

		// keep track of handled edges
		Map<String, IterableMap<String, LayoutEdge>> handledMaps = new HashMap<String, IterableMap<String, LayoutEdge>>();

		// Step 1: for every edge check whether its sprite needs to be moved because of
		// parallel edges between the same source and target
		for (String s : edgeMap.keySet()) {

			if (handledMaps.containsKey(s))
				continue;

			// get all the edges between the two nodes (can be multiple because of different
			// transitions)
			IterableMap<String, LayoutEdge> specificEdgeMap = edgeMap.get(s);

			// get source and target
			LayoutNode source = specificEdgeMap.iterator().next().source;
			LayoutNode target = specificEdgeMap.iterator().next().target;

			// get the opposite edge string (edge between same nodes but going in the other
			// direction)
			String oppositeEdgesString = target.getNode().getId() + source.getNode().getId();

			// if opposite edges have been handled continue
			if (handledMaps.containsKey(oppositeEdgesString))
				continue;

			// get all opposite edges
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

			// handle multiple edges between two nodes all going into the same direction ->
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

		// sort nodes and sprites by x and y values
		Collections.sort(graphicalObjectList);

		// keep track of the last edge
		LayoutEdge lastEdge = null;

		// check whether sprites overlap other sprites on crossing edges (not parallel)
		for (int i = 0; i < graphicalObjectList.size() - 1; i++) {

			// only check edges (since they are the only GraphicalObjects containing
			// sprites)
			if (!(graphicalObjectList.get(i) instanceof LayoutEdge))
				continue;

			LayoutEdge edge = (LayoutEdge) graphicalObjectList.get(i);

			// if the edges length is below the minimum ignore the edge -> otherwise sprites
			// will start to overlap nodes because there is not enough space on the edge to
			// move them
			if (edge.length() < MINIMAL_EDGE_LENGTH)
				continue;

			// if the last edge == null we are checking the first edge and can't compare it
			// to anything
			if (lastEdge == null) {
				lastEdge = edge;
				continue;
			}

			// get the two sprites as rectangles
			LayoutRectangle middlePoint1 = new LayoutRectangle(lastEdge.getX(), lastEdge.getY(), MINIMAL_SIZE);
			LayoutRectangle middlePoint2 = new LayoutRectangle(edge.getX(), edge.getY(), MINIMAL_SIZE);

			// if the two sprites overlap spread them apart
			if (middlePoint1.rectanglesIntersect(middlePoint2)) {
				lastEdge.sprite.setPosition(0.33, 0.33, 0);
				edge.sprite.setPosition(0.33, 0.33, 0);
			}

			// update last edge
			lastEdge = edge;

		}

	}

	// remove all blank nodes
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

	// reset the max row -> checking all levels for the biggest number of nodes
	private void resetMaxRow() {

		maxRowCount = 0;
		for (List<LayoutNode> nodeList : listHierarchy)
			if (nodeList.size() > maxRowCount)
				maxRowCount = nodeList.size();
	}

	/*-
	 * EXPLANATION FOR CODE FOLLOWING BELOW
	 * 
	 * Arrange the nodes in a circle (to be more precise in an ellipse) -> since the
	 * nodes are aligned around the circle level by level edges tend to cluster at
	 * circles scope. This can be met by moving the nodes around the circle and there-
	 * fore moving the edges (since they are attached to the nodes). The idea is to 
	 * start at the first node and than spread them evenly around the circle one by
	 * one. 
	 * 
	 * An example (left are the initial positions and right are the positions after
	 * shifting the nodes around):
	 * 
	 * 0: 0
	 * 1: 2
	 * 2: 4
	 * 3: 6
	 * ...
	 * 
	 * The ratio by which the edges are spread among the nodes is defined by the
	 * circleSpreadRatio. In the example above the ratio would be 2. If the ratio 
	 * is 1 the nodes stay where they are.
	 * 
	 * If we come full circle we start from the first empty space. For example:
	 * 
	 * 0: 0
	 * 1: 2
	 * 2: 1
	 * 3: 3
	 * 
	 * To achieve this we have a counter starting from 0 and an indexOffset.
	 * The position in our example is calculated by counter * 2 + indexOffset.
	 * If we exceed the length of the array (in our example beginning at 4) 
	 * we reset the counter to 0 and increment the offset.
	 * 
	 * 0: 0 * 2 + 0 = 0
	 * 1: 1 * 2 + 0 = 2
	 * 2: 2 * 2 + 0 = 4 (position does not exist (array length exceeded) -> counter = 0, indexOffset++
	 * 2: 0 * 2 + 1 = 1
	 * 3: 1 * 2 + 1 = 3 (final element)
	 * 
	 * 2 represents the circleSpreadRatio -> the algorithm works with any number.
	 * 
	 */
	private void circlify() {

		// begin with a clean sleet
		removeBlankNodes();

		// add all nodes into one list level by level
		List<LayoutNode> nodesTotal = new ArrayList<LayoutNode>();
		for (int i = 0; i < listHierarchy.size(); i++) {
			List<LayoutNode> nodeList = listHierarchy.get(i);
			nodesTotal.addAll(nodeList);
		}

		// if there is only one node ther is nothing to do
		if (nodesTotal.size() <= 1)
			return;

		// array able to hold all nodes -> arrays are easier to handle, when assigning
		LayoutNode[] nodesJoined = new LayoutNode[nodesTotal.size()];

		// keep track of the current node
		int totalCounter = 0;

		// see explanation above
		int indexOffset = 0;
		int counter = 0;

		// while the current position is smaller than the size of the array (the last
		// element will always be assigned to the last place) do algorithm -> for an
		// explanation see above
		while (counter * circleSpreadRatio + indexOffset < nodesTotal.size()) {

			// assign node
			nodesJoined[counter * circleSpreadRatio + indexOffset] = nodesTotal.get(totalCounter);

			// we've come full circle
			if (((counter + 1) * circleSpreadRatio + indexOffset) >= nodesTotal.size()
					&& (indexOffset + 1) < circleSpreadRatio) {
				counter = 0;
				indexOffset++;
			} else
				counter++;
			totalCounter++;
		}

		// with the nodes spread out, create the ellipse
		double a = 2; // first radius
		double b = 1; // second radius

		for (int i = 0; i < nodesJoined.length; i++) {

			LayoutNode layoutNode = nodesJoined[i];

			// should not happen -> here as a safety measure
			if (layoutNode.getNode() == null) {
				continue;
			}

			// get the GraphStream node
			Node node = nodesJoined[i].getNode();

			// start at Pi/2 (90°) and go around the circle step by step
			double angle = (Math.PI / 2) + (2 * Math.PI / nodesJoined.length) * i;

			// calculate coordinates -> see
			// https://www.mathopenref.com/coordparamellipse.html
			double x = a * Math.cos(angle);
			double y = b * Math.sin(angle);

			node.setAttribute("xy", x, y);

		}
		// set sprites position at 0.4 so they do not intersect as much
		for (Sprite s : spriteMan)
			s.setPosition(0.4);

	}

	/**
	 * Removes an edge from the layout.
	 *
	 * @param stateSource the source node of the edge
	 * @param stateTarget the target node of the edge
	 * @param transition  the transition belonging to the edge
	 */
	public void removeEdge(PetrinetState stateSource, PetrinetState stateTarget, Transition transition) {

		// get map of edges between the two nodes and abort if there are none
		IterableMap<String, LayoutEdge> specificEdgeMap = edgeMap.get(stateSource.getState() + stateTarget.getState());
		if (specificEdgeMap == null)
			return;

		// get the specific edge and abort if it does not exist
		LayoutEdge edge = specificEdgeMap.get(transition.getId());
		if (edge == null)
			return;

		// remove edge from the specific map for the two nodes and remove the specific
		// map from the general map if it was the only edge
		specificEdgeMap.remove(transition.getId());
		if (specificEdgeMap.size() == 0)
			edgeMap.remove(stateSource.getState() + stateTarget.getState());

		// remove edge from the graphical objects list
		graphicalObjectList.remove(edge);

		// remove edge from the potential culprit (if it was a potential culprit)
		if (potentialCulprits.contains(edge))
			potentialCulprits.remove(edge);

		// repaint
		repaintNodes(true);
	}

	/**
	 * Removes a node from the layout. DOES NOT REMOVE EDGES ATTACHED TO THE NODE ->
	 * NEED TO BE REMOVED MANUALLY.
	 *
	 * @param node the node to be removed
	 */
	public void removeNode(Node node) {

		// get the node and abort if it does not exist
		LayoutNode layoutNode = nodeMap.get(node.getId());
		if (layoutNode == null)
			return;

		// remove node from map and from the level in the hierarchy
		nodeMap.remove(node.getId());
		List<LayoutNode> nodeList = listHierarchy.get(layoutNode.getLevel());
		nodeList.remove(layoutNode);

		// remove node from graphical objects list
		graphicalObjectList.remove(layoutNode);

		// repaint
		repaintNodes(true);

	}

	/**
	 * Sets the layout type. If CIRCLE is already set and passed again it increases
	 * the circle spread ratio.
	 *
	 * @param layoutType the new layout type to be set
	 */
	public void setLayoutType(LayoutType layoutType) {

		if (layoutType == LayoutType.CIRCLE && this.layoutType == LayoutType.CIRCLE) {
			circleSpreadRatio = (circleSpreadRatio + 1) % MAX_CIRCLE_SPREAD_RATIO;
			if (circleSpreadRatio == 0)
				circleSpreadRatio = MAX_CIRCLE_SPREAD_RATIO;
		} else
			this.layoutType = layoutType;
		repaintNodes(true);

	}

}