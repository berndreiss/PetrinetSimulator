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
 * The Class Layout.
 */
public class Layout {

//	private final static Dimension NODE_SIZE = new Dimension(125, 30);

	/** The Constant NODE_SIZE. */
protected final static Dimension NODE_SIZE = new Dimension(30, 30);
	
	/** The Constant SPRITE_SIZE. */
	protected final static Dimension SPRITE_SIZE = new Dimension(30, 30);
	
	/** The Constant MINIMAL_SIZE. */
	private final static Dimension MINIMAL_SIZE = new Dimension(10, 10);
	
	/** The Constant MINIMAL_EDGE_LENGTH. */
	private final static int MINIMAL_EDGE_LENGTH = 200;

	/** The screen size. */
	private Dimension screenSize = new Dimension(1000, 500);

	private LayoutTypes layoutType = LayoutTypes.TREE;

	private static int maxRowCount = 0;

	/** The list hierarchy. */
	List<List<LayoutNode>> listHierarchy = new ArrayList<List<LayoutNode>>();

	private SpriteManager spriteMan;

	private IterableMap<String, LayoutNode> nodeMap = new IterableMap<String, LayoutNode>();
	private IterableMap<String, IterableMap<String, LayoutEdge>> edgeMap = new IterableMap<String, IterableMap<String, LayoutEdge>>();
	
	/** The graphical object list. */
	private List<GraphicalObject> graphicalObjectList = new ArrayList<GraphicalObject>();

	private List<LayoutEdge> potentialCulprits = new ArrayList<LayoutEdge>();

	private enum Direction {
		LEFT, RIGHT;
	}

	/**
	 * Instantiates a new layout.
	 *
	 * @param spriteMan the sprite man
	 */
	public Layout(SpriteManager spriteMan) {
		this.spriteMan = spriteMan;
	}

	/**
	 * Adds the.
	 *
	 * @param node the node
	 */
	public void add(Node node) {
		this.add(node, null, null);
	}

	/**
	 * Adds the.
	 *
	 * @param source the source
	 * @param target the target
	 * @param transition the transition
	 */
	public void add(Node source, Node target, Transition transition) {
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
			layoutSource = new LayoutNode(source, null, 0, this);
			layoutTarget = new LayoutNode(target, layoutSource, 1, this);

			nodeMap.put(source.getId(), layoutSource);
			nodeMap.put(target.getId(), layoutTarget);

			graphicalObjectList.add(layoutSource);
			graphicalObjectList.add(layoutTarget);
		}

		else if (layoutSource == null) {
			layoutSource = new LayoutNode(source, null, 0, this);

			nodeMap.put(source.getId(), layoutSource);
			graphicalObjectList.add(layoutSource);
		}

		else if (layoutTarget == null) {

			layoutTarget = new LayoutNode(target, layoutSource, layoutSource.getLevel() + 1, this);

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

//		IterableMap<String, LayoutEdge> oppositeEdgesList = edgeMap.get(target.getId() + source.getId());
//
//		if (oppositeEdgesList != null) {
//			for (LayoutEdge le : edgeList)
//				le.sprite.setPosition(0.33, 0.33, 0);
//			for (LayoutEdge le : oppositeEdgesList)
//				le.sprite.setPosition(0.33, 0.33, 0);
//		}
//
//		if (edgeList.size() > 1) {
//
//			double modificatorUnit = 1.0 / edgeList.size();
//
//			int index = 1;
//
//			for (LayoutEdge le : edgeList) {
//				Sprite sprite = le.getSprite();
//				sprite.setPosition(sprite.getX() * modificatorUnit * index, sprite.getY() * modificatorUnit * index,
//						sprite.getZ() * modificatorUnit * index);
//				index++;
//
//			}
//		}
		repaintNodes();
		if (layoutType == LayoutTypes.TREE)
			beautify();
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

				LayoutPoint currentNodePosition = new LayoutPoint(getWidth(nodeList.size(), j), getHeight(i));

				if (GraphicsOperations.edgeIntersectsGraphicalObject(layoutEdge, currentNodePosition) && layoutSource.node != ln.node
						&& layoutTarget.node != ln.node) {
					intersectingNodes.add(ln);
					if (ln.node != null)
						nonNullNodesIntersecting = true;
					continue;
				}

				// if the LayoutNode is a blank node also check the middle -> if it intersects
				// we might want to push the node left/right regardless, because there have been
				// intersectios before and the nodes might need to be pushed further
				if (ln.node == null) {
					LayoutPoint neighboringNodePosition = null;

					if (j > 0) {
						neighboringNodePosition = new LayoutPoint(getWidth(nodeList.size(), j - 1), getHeight(i));
					}

					if (j < nodeList.size() - 1) {
						neighboringNodePosition = new LayoutPoint(getWidth(nodeList.size(), j + 1), getHeight(i));
					}

					if (neighboringNodePosition == null)
						continue;

					LayoutPoint middlePosition = new LayoutPoint(
							(currentNodePosition.x + neighboringNodePosition.x) / 2,
							(currentNodePosition.y + neighboringNodePosition.y) / 2);

					if (GraphicsOperations.edgeIntersectsGraphicalObject(layoutEdge, middlePosition) && layoutSource.node != ln.node
							&& layoutTarget.node != ln.node) {
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

		if (listHierarchy.size() < node.getLevel())
			return;

		if (listHierarchy.size() == node.getLevel())
			listHierarchy.add(new ArrayList<LayoutNode>());

		List<LayoutNode> nodeList = listHierarchy.get(node.getLevel());

		boolean added = false;

		for (LayoutNode ln : nodeList) {
			if (ln.node == null)
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
				if (ln.node == null) {
					nodeList.remove(i);
					hasEmpty = true;
					break;
				}
				hasEmpty = false;
			}
		}

		// TODO check all dependencies

	}

	private void repaintNodes() {

		if (layoutType == LayoutTypes.CIRCLE) {
			circlify();
			return;
		}

		for (List<LayoutNode> nodeList : listHierarchy) {

			if (nodeList.size() == 0)
				continue;

			for (int i = 0; i < nodeList.size(); i++) {

				LayoutNode node = nodeList.get(i);

				if (node.node == null)
					continue;

				node.node.setAttribute("xy", getWidth(nodeList.size(), i), getHeight(node.getLevel()));
			}

		}

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
					repaintNodes();
					break;
				}

			}
		} while (workToBeDone && loops < 20);

//		System.out.println("Loops performed in beautify: " + loops);

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

			String oppositeEdgesString = target.node.getId() + source.node.getId();

			if (handledMaps.containsKey(oppositeEdgesString))
				continue;
			IterableMap<String, LayoutEdge> oppositeEdgesMap = edgeMap.get(target.node.getId() + source.node.getId());

			if (oppositeEdgesMap != null) {
				for (LayoutEdge le : specificEdgeMap)
					le.sprite.setPosition(0.33, 0.33, 0);
				for (LayoutEdge le : oppositeEdgesMap)
					le.sprite.setPosition(0.33, 0.33, 0);
			}

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

			LayoutPoint middlePoint1 = new LayoutPoint(lastEdge.getX(), lastEdge.getY(), MINIMAL_SIZE);
			LayoutPoint middlePoint2 = new LayoutPoint(edge.getX(), edge.getY(), MINIMAL_SIZE);

			if (GraphicsOperations.graphicalObjectsIntersect(middlePoint1, middlePoint2)) {
				lastEdge.sprite.setPosition(0.25, 0.25, 0);
				edge.sprite.setPosition(0.25, 0.25, 0);
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

		GraphicalObject gom = graphicalObjectList.get(m);

		if (gom != layoutEdge && GraphicsOperations.graphicalObjectsIntersect(gom, layoutEdge)) 
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
				if (ln.node == null)
					nodesToRemove.add(ln);

			for (LayoutNode ln : nodesToRemove)
				nodeList.remove(ln);
		}
		resetMaxRow();
		if (layoutType == LayoutTypes.TREE)
			repaintNodes();
	}

	private double getHeight(int index) {
		
		if (listHierarchy.size() == 1)
			return -screenSize.getHeight()/2;

		return -screenSize.getHeight() * ((double) index / (listHierarchy.size() - 1));
	}

	private double getWidth(int listSize, int index) {

		if (listSize == 1)
			return screenSize.getWidth() / 2;

		return screenSize.getWidth() * (maxRowCount - listSize + index * 2) / (2 * maxRowCount - 2);
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

		int circleSpacing = 3;
		int indexOffset = 0;

		int j = 0;
		int totalCounter = 0;

		while (j * circleSpacing + indexOffset < nodesTotal.size()) {
			nodesJoined[j * circleSpacing + indexOffset] = nodesTotal.get(totalCounter);

			if (((j + 1) * circleSpacing + indexOffset) >= nodesTotal.size() && (indexOffset + 1) < circleSpacing) {
				j = 0;
				indexOffset++;
			} else
				j++;
			totalCounter++;
		}

		double a = screenSize.getWidth();
		double b = screenSize.getHeight();
		for (int i = 0; i < nodesJoined.length; i++) {

			LayoutNode layoutNode = nodesJoined[i];

			if (layoutNode.node == null) {
				continue;
			}
			Node node = nodesJoined[i].node;

			double angle = (Math.PI / 2) + (2 * Math.PI / nodesJoined.length) * i;

			double x = a * Math.cos(angle);
			double y = b * Math.sin(angle);

			node.setAttribute("xy", x, y);

		}

		for (Sprite s : spriteMan)
			s.setPosition(0.4);

	}

	// TODO when removing node maxRowCount has to be recalculated

	/**
	 * Sets the screen size.
	 *
	 * @param screenSize the new screen size
	 */
	public void setScreenSize(Dimension screenSize) {
		this.screenSize = screenSize;
		System.out.println(screenSize);
		repaintNodes();
	}



	/**
	 * Removes the edge.
	 *
	 * @param stateSource the state source
	 * @param stateTarget the state target
	 * @param t the t
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

		repaintNodes();
		if (layoutType == LayoutTypes.TREE)
			beautify();

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
				
		//TODO should also handle nodeList.size()==0, but it should not be a big problem, except for maybe having empty 
		
		graphicalObjectList.remove(layoutNode);
		removeBlankNodes();
		repaintNodes();
		if (layoutType == LayoutTypes.TREE)
			beautify();

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
	public void setLayoutType(LayoutTypes layoutType) {
		this.layoutType = layoutType;
	}

}