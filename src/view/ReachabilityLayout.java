package view;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import datamodel.Transition;
import util.IterableMap;

public class ReachabilityLayout {

//	private final static Dimension NODE_SIZE = new Dimension(125, 30);

	private final static Dimension NODE_SIZE = new Dimension(30, 30);
	private final static Dimension SPRITE_SIZE = new Dimension(30, 30);
	private Dimension screenSize = new Dimension(1000, 500);

	private static int maxRowCount = 0;

	private List<List<LayoutNode>> listHierarchy = new ArrayList<List<LayoutNode>>();

	private SpriteManager spriteMan;

	private IterableMap<String, LayoutNode> nodeMap = new IterableMap<String, LayoutNode>();
	private IterableMap<String, IterableMap<String, LayoutEdge>> edgeMap = new IterableMap<String, IterableMap<String, LayoutEdge>>();
	List<GraphicalObject> graphicalObjectList = new ArrayList<GraphicalObject>();

	private List<LayoutEdge> potentialCulprits = new ArrayList<LayoutEdge>();

	private enum Direction {
		LEFT, RIGHT;
	}

	public ReachabilityLayout(SpriteManager spriteMan) {
		this.spriteMan = spriteMan;
	}

	public void add(Node node) {
		this.add(node, null, null);
	}

	public void add(Node source, Node target, Transition transition) {

		if (source == null && target == null)
			return;

		if (source == null || target == null) {
			Node node = source == null ? target : source;

			LayoutNode layoutNode = new LayoutNode(node, null, 0);

			if (!nodeMap.containsKey(node.getId())) {
				nodeMap.put(node.getId(), layoutNode);
				graphicalObjectList.add(layoutNode);
			}
			return;

		}

		LayoutNode layoutSource = nodeMap.get(source.getId());
		LayoutNode layoutTarget = nodeMap.get(target.getId());

		if (layoutSource == null && layoutTarget == null) {
			layoutSource = new LayoutNode(source, null, 0);
			layoutTarget = new LayoutNode(target, layoutSource, 1);

			nodeMap.put(source.getId(), layoutSource);
			nodeMap.put(target.getId(), layoutTarget);

			graphicalObjectList.add(layoutSource);
			graphicalObjectList.add(layoutTarget);
//			layoutSource.successors.add(layoutTarget);
//			layoutTarget.predecessors.add(layoutSource);
		}

		else if (layoutSource == null) {
			layoutSource = new LayoutNode(source, null, 0);

			nodeMap.put(source.getId(), layoutSource);
			graphicalObjectList.add(layoutSource);
//			layoutSource.upwardsSuccessors.add(layoutTarget);
//			layoutTarget.predecessors.add(layoutSource);
		}

		else if (layoutTarget == null) {

			layoutTarget = new LayoutNode(target, layoutSource, layoutSource.level + 1);

			nodeMap.put(target.getId(), layoutTarget);
			graphicalObjectList.add(layoutTarget);
//			layoutSource.successors.add(layoutTarget);
//			layoutTarget.predecessors.add(layoutSource);

		}

//		if (Toolkit.nodePosition(source)[0] <= Toolkit.nodePosition(target)[0]) {
//
//			if (!layoutSource.upwardsSuccessors.contains(layoutTarget))
//				layoutSource.upwardsSuccessors.add(layoutTarget);
//		} else {
//			if (!layoutSource.successors.contains(layoutTarget))
//				layoutSource.successors.add(layoutTarget);
//		}
//		if (!layoutTarget.predecessors.contains(layoutSource))
//			layoutTarget.predecessors.add(layoutSource);

		String edgeString = source.getId() + target.getId();
		IterableMap<String, LayoutEdge> edgeList = edgeMap.get(edgeString);

		if (edgeList == null) {
			edgeMap.put(edgeString, new IterableMap<String, LayoutEdge>());
			edgeList = edgeMap.get(edgeString);
		}

		if (edgeList.containsKey(transition.getId()))
			return;

		LayoutEdge layoutEdge = new LayoutEdge(layoutSource, layoutTarget, transition);

		if (!(Math.abs(layoutSource.level - layoutTarget.level) <= 1)) {

			potentialCulprits.add(layoutEdge);
		}

		edgeList.put(transition.getId(), layoutEdge);

		IterableMap<String, LayoutEdge> oppositeEdgesList = edgeMap.get(target.getId() + source.getId());

		if (oppositeEdgesList != null) {
			for (LayoutEdge le : edgeList)
				le.sprite.setPosition(0.33, 0.33, 0);
			for (LayoutEdge le : oppositeEdgesList)
				le.sprite.setPosition(0.33, 0.33, 0);
		}

		if (edgeList.size() > 1) {

			double modificatorUnit = 1.0 / edgeList.size();

			int index = 1;

			for (LayoutEdge le : edgeList) {
				Sprite sprite = le.sprite;
				sprite.setPosition(sprite.getX() * modificatorUnit * index, sprite.getY() * modificatorUnit * index,
						sprite.getZ() * modificatorUnit * index);
				index++;

			}
		}
		beautify();

	}

	private void checkNodeAgainstPotentialCulprits(LayoutNode node) {

	}

	private boolean checkEdgeIntersection(LayoutEdge layoutEdge) {

		LayoutNode layoutSource = layoutEdge.source;
		LayoutNode layoutTarget = layoutEdge.target;

		int startIndex = Math.min(layoutSource.level, layoutTarget.level);

		int finalIndex = Math.max(layoutSource.level, layoutTarget.level);

		List<LayoutNode> intersectingNodes = new ArrayList<ReachabilityLayout.LayoutNode>();

		boolean nonNullNodesIntersecting = false;

		for (int i = startIndex + 1; i < finalIndex; i++) {

			List<LayoutNode> nodeList = listHierarchy.get(i);

			if (nodeList.size() == 0)
				continue;

			for (int j = 0; j < nodeList.size(); j++) {

				LayoutNode ln = nodeList.get(j);

				LayoutPoint currentNodePosition = new LayoutPoint(getWidth(nodeList.size(), j), getHeight(i));

				if (edgeIntersectsGraphicalObject(layoutEdge, currentNodePosition) && layoutSource.node != ln.node
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

					if (edgeIntersectsGraphicalObject(layoutEdge, middlePosition) && layoutSource.node != ln.node
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

			List<LayoutNode> nodeList = listHierarchy.get(ln.level);

			if (direction == Direction.LEFT) {
				nodeList.add(nodeList.indexOf(ln) + 1, new LayoutNode(null, null, ln.level));
				if (nodeList.size() > maxRowCount)
					maxRowCount = nodeList.size();
			} else {
				nodeList.add(nodeList.indexOf(ln), new LayoutNode(null, null, ln.level));
				if (nodeList.size() > maxRowCount)
					maxRowCount = nodeList.size();
			}
		}
		return true;
	}

	private Direction getDirection(LayoutNode layoutNode) {

		List<LayoutNode> nodeList = listHierarchy.get(layoutNode.level);

		int index = nodeList.indexOf(layoutNode);

		return index <= (double) (nodeList.size() - 1) / 2 ? Direction.LEFT : Direction.RIGHT;

	}

	private void addNodeToLevel(LayoutNode node, int level) {

		if (listHierarchy.size() < level)
			return;

		if (listHierarchy.size() == level)
			listHierarchy.add(new ArrayList<LayoutNode>());

		List<LayoutNode> nodeList = listHierarchy.get(level);

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

		for (List<LayoutNode> nodeList : listHierarchy) {

			if (nodeList.size() == 0)
				continue;

			for (int i = 0; i < nodeList.size(); i++) {

				LayoutNode node = nodeList.get(i);

				if (node.node == null)
					continue;

				node.node.setAttribute("xy", getWidth(nodeList.size(), i), getHeight(node.level));
			}

		}

//		int level = 0;
//
//		for (List<LayoutNode> nodeList : listHierarchy) {
//			System.out.println("LEVEL " + level);
//			for (LayoutNode ln : nodeList) {
//				System.out.println(ln.node == null ? "null" : ln.node.getId());
//			}
//			level++;
//		}
//

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

		System.out.println("Loops performed in beautify: " + loops);

		spreadSprites();
	}

	private void spreadSprites() {

		Collections.sort(graphicalObjectList);
		
		for (IterableMap<String, LayoutEdge> edgeMap: edgeMap)
				for (LayoutEdge le: edgeMap)
					checkSingleSprite(le, 0, graphicalObjectList.size()-1);
	}

	private boolean checkSingleSprite(LayoutEdge layoutEdge, int begin, int end) {

		if (begin > end)
			return false;
		
		int m = (begin + end)/2;
		
		GraphicalObject gom = graphicalObjectList.get(m);
		
		if (graphicalObjectsIntersect(gom, layoutEdge)) {

			System.out.println("INTERSECTION");
			
			Sprite sprite = layoutEdge.sprite;
			
			final int POWER_MAX = 4;
			double power = 1;
			
			int counter = 0;
			
			do {
		
				Collections.sort(graphicalObjectList);

				if (counter == (int) Math.pow(2.0, power)) {
					power++;
					counter = 0;
				}
				
				int sign = 1;
				
				if (counter%2==0)
					sign = -1;
				
				double factor = ((counter/2)*2+1)/Math.pow(2, power);
				
				double newX = sprite.getX() + sprite.getX() *factor*sign;
				double newY = sprite.getY() + sprite.getY() *factor*sign;
				double newZ = sprite.getZ() + sprite.getZ() *factor*sign;
				sprite.setPosition(sprite.getX()*factor*sign);
				
				counter++;
				
			}while (checkSingleSprite(layoutEdge, 0, graphicalObjectList.size()-1) && power < POWER_MAX);
			
			if (power == POWER_MAX)
				return true;
			
			checkSingleSprite(layoutEdge, begin, m-1);
			checkSingleSprite(layoutEdge, m+1, end);
		}else {
			if (gom.compareTo(layoutEdge) >= 0)
				return checkSingleSprite(layoutEdge, begin, m-1);
			else
				return checkSingleSprite(layoutEdge, m+1, end);
		}
			
		
		return false;

	}

	private void removeBlankNodes() {

		for (List<LayoutNode> nodeList : listHierarchy) {
			List<LayoutNode> nodesToRemove = new ArrayList<ReachabilityLayout.LayoutNode>();

			for (LayoutNode ln : nodeList)
				if (ln.node == null)
					nodesToRemove.add(ln);

			for (LayoutNode ln : nodesToRemove)
				nodeList.remove(ln);
		}
		repaintNodes();
	}

	private double getHeight(int index) {
		return -(screenSize.getHeight()) / (listHierarchy.size() > 1 ? listHierarchy.size() - 1 : 1) * index;
	}

	private double getWidth(int listSize, int index) {

		if (listSize == 1)
			return screenSize.getWidth() / 2;

		return screenSize.getWidth() * (maxRowCount - listSize + index * 2) / (2 * maxRowCount - 2);
	}

	private void circlify() {

		List<LayoutNode> nodesTotal = new ArrayList<ReachabilityLayout.LayoutNode>();
		List<LayoutNode> left = new ArrayList<ReachabilityLayout.LayoutNode>();
		List<LayoutNode> right = new ArrayList<ReachabilityLayout.LayoutNode>();

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

//		for (int i = nodesTotal.size() / 2; i < nodesTotal.size() - 1; i++) {
//			nodesJoined[(i - nodesTotal.size() / 2) * 2 + 1] = nodesTotal.get(i);
//		}

//		int j = 0;
//		
//		System.out.println(left.size());
//		System.out.println(right.size());
//		
//		while (j<left.size() && j< right.size()) {
//			
//			nodesJoined.add(left.get(j));
//			nodesJoined.add(right.get(j));
//			j++;
//			
//		}

//		if (j<left.size())
//			nodesJoined.add(left.get(j));
//		if (j<right.size())
//			nodesJoined.add(right.get(j));
//			
//		System.out.println(nodesJoined.size());

//		left.addAll(right);

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

	private abstract class GraphicalObject implements Comparable<GraphicalObject> {
		abstract LayoutPoint leftLowerCorner();

		abstract LayoutPoint leftUpperCorner();

		abstract LayoutPoint rightLowerCorner();

		abstract LayoutPoint rightUpperCorner();

		abstract double getX();

		abstract double getY();

		public Line leftSide() {
			return new Line(leftLowerCorner(), leftUpperCorner());
		}

		public Line rightSide() {
			return new Line(rightLowerCorner(), rightUpperCorner());
		}

		public Line upperSide() {
			return new Line(leftUpperCorner(), rightUpperCorner());
		}

		public Line lowerSide() {
			return new Line(leftLowerCorner(), rightLowerCorner());
		}

		@Override
		public int compareTo(GraphicalObject go) {
			if (this.getX() < go.getX())
				return -1;

			if (this.getX() > go.getX())
				return 1;

			if (this.getY() > go.getY())
				return -1;
			if (this.getY() < go.getY())
				return 1;

			return 0;
		}
	}

	private class LayoutNode extends GraphicalObject {

		public List<LayoutNode> predecessors = new ArrayList<ReachabilityLayout.LayoutNode>();
		public List<LayoutNode> successors = new ArrayList<ReachabilityLayout.LayoutNode>();
		public List<LayoutNode> upwardsSuccessors = new ArrayList<ReachabilityLayout.LayoutNode>();
		public Node node;
		public LayoutNode parent;
		public List<LayoutNode> children = new ArrayList<ReachabilityLayout.LayoutNode>();

		public int level;

		public LayoutNode(Node node, LayoutNode parent, int level) {
			this.node = node;
			this.parent = parent;
			this.level = level;

			if (parent != null)
				parent.children.add(this);

			if (node != null)
				addNodeToLevel(this, level);
		}

		public String getTag() {
			return parent == null ? "" : parent.getTag() + listHierarchy.get(level).indexOf(this);
		}

		@Override
		public LayoutPoint leftLowerCorner() {
			double[] coordinates = Toolkit.nodePosition(node);
			return new LayoutPoint(coordinates[0] - NODE_SIZE.getWidth() / 2,
					coordinates[1] - NODE_SIZE.getHeight() / 2);
		}

		@Override
		public LayoutPoint leftUpperCorner() {
			double[] coordinates = Toolkit.nodePosition(node);
			return new LayoutPoint(coordinates[0] - NODE_SIZE.getWidth() / 2,
					coordinates[1] + NODE_SIZE.getHeight() / 2);
		}

		@Override
		public LayoutPoint rightLowerCorner() {
			double[] coordinates = Toolkit.nodePosition(node);
			return new LayoutPoint(coordinates[0] + NODE_SIZE.getWidth() / 2,
					coordinates[1] - NODE_SIZE.getHeight() / 2);
		}

		@Override
		public LayoutPoint rightUpperCorner() {
			double[] coordinates = Toolkit.nodePosition(node);
			return new LayoutPoint(coordinates[0] + NODE_SIZE.getWidth() / 2,
					coordinates[1] + NODE_SIZE.getHeight() / 2);
		}

		@Override
		double getX() {
			return Toolkit.nodePosition(node)[0];
		}

		@Override
		double getY() {
			return Toolkit.nodePosition(node)[1];
		}
	}

	private class LayoutEdge extends GraphicalObject {
		LayoutNode source;
		LayoutNode target;

		Sprite sprite;

		public LayoutEdge(LayoutNode source, LayoutNode target, Transition t) {
			this.source = source;
			this.target = target;
			this.sprite = spriteMan.getSprite("s" + source.node.getId() + target.node.getId() + t.getId());
		}

		@Override
		public LayoutPoint leftLowerCorner() {

			return new LayoutPoint(sprite.getX() - SPRITE_SIZE.getWidth() / 2,
					sprite.getY() - SPRITE_SIZE.getHeight() / 2);
		}

		@Override
		public LayoutPoint leftUpperCorner() {
			return new LayoutPoint(sprite.getX() - SPRITE_SIZE.getWidth() / 2,
					sprite.getY() + SPRITE_SIZE.getHeight() / 2);
		}

		@Override
		public LayoutPoint rightLowerCorner() {
			return new LayoutPoint(sprite.getX() + SPRITE_SIZE.getWidth() / 2,
					sprite.getY() - SPRITE_SIZE.getHeight() / 2);
		}

		@Override
		public LayoutPoint rightUpperCorner() {
			return new LayoutPoint(sprite.getX() + SPRITE_SIZE.getWidth() / 2,
					sprite.getY() + SPRITE_SIZE.getHeight() / 2);
		}

		@Override
		double getX() {
			return (source.getX() + target.getX()) * sprite.getX();
		}

		@Override
		double getY() {
			return (source.getY() + target.getY()) * sprite.getY();
		}
	}

	public void setScreenSize(Dimension screenSize) {
		this.screenSize = screenSize;
		repaintNodes();
	}

	private boolean graphicalObjectsIntersect(GraphicalObject go1, GraphicalObject go2) {

		if (!pointIsInsideGraphicalObject(go1, go2.leftLowerCorner()))
			return false;

		if (!pointIsInsideGraphicalObject(go1, go2.leftUpperCorner()))
			return false;

		if (!pointIsInsideGraphicalObject(go1, go2.rightLowerCorner()))
			return false;

		if (!pointIsInsideGraphicalObject(go1, go2.rightUpperCorner()))
			return false;

		return true;
	}

	private boolean pointIsInsideGraphicalObject(GraphicalObject go, LayoutPoint p) {
		if (go == null || p == null)
			return false;

		if (p.y <= go.leftUpperCorner().y && p.y >= go.leftLowerCorner().y && p.x <= go.rightLowerCorner().x
				&& p.x >= go.leftLowerCorner().x)
			return true;

		return false;
	}

	private boolean edgeIntersectsGraphicalObject(LayoutEdge layoutEdge, GraphicalObject go) {

		if (go == null || layoutEdge == null)
			return false;

		Node source = layoutEdge.source.node;
		Node target = layoutEdge.target.node;

		double[] sourcePosition = Toolkit.nodePosition(source);
		double[] targetPosition = Toolkit.nodePosition(target);

		LayoutPoint a = new LayoutPoint(sourcePosition[0], sourcePosition[1]);
		LayoutPoint b = new LayoutPoint(targetPosition[0], targetPosition[1]);

		Line edge = new Line(a, b);

		LayoutPoint intersectionPoint;

		intersectionPoint = findIntersection(edge, go.leftSide());

		if (intersectionPoint != null && intersectionPoint.x >= Math.min(edge.a.x, edge.b.x)
				&& intersectionPoint.x <= Math.max(edge.a.x, edge.b.x)
				&& pointIsInsideGraphicalObject(go, intersectionPoint))
			return true;

		intersectionPoint = findIntersection(edge, go.rightSide());

		if (intersectionPoint != null && intersectionPoint.x >= Math.min(edge.a.x, edge.b.x)
				&& intersectionPoint.x <= Math.max(edge.a.x, edge.b.x)
				&& pointIsInsideGraphicalObject(go, intersectionPoint))
			return true;

		intersectionPoint = findIntersection(edge, go.lowerSide());

		if (intersectionPoint != null && intersectionPoint.y >= Math.min(edge.a.y, edge.b.y)
				&& intersectionPoint.y <= Math.max(edge.a.y, edge.b.y)
				&& pointIsInsideGraphicalObject(go, intersectionPoint))
			return true;

		intersectionPoint = findIntersection(edge, go.upperSide());
		if (intersectionPoint != null && intersectionPoint.y >= Math.min(edge.a.y, edge.b.y)
				&& intersectionPoint.y <= Math.max(edge.a.y, edge.b.y)
				&& pointIsInsideGraphicalObject(go, intersectionPoint))
			return true;

		return false;

	}

	private class LayoutPoint extends GraphicalObject {
		double x, y;

		LayoutPoint(double x, double y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public LayoutPoint leftLowerCorner() {
			return new LayoutPoint(x - NODE_SIZE.getWidth() / 2, y - NODE_SIZE.getHeight() / 2);
		}

		@Override
		public LayoutPoint leftUpperCorner() {
			return new LayoutPoint(x - NODE_SIZE.getWidth() / 2, y + NODE_SIZE.getHeight() / 2);
		}

		@Override
		public LayoutPoint rightLowerCorner() {
			return new LayoutPoint(x + NODE_SIZE.getWidth() / 2, y - NODE_SIZE.getHeight() / 2);
		}

		@Override
		public LayoutPoint rightUpperCorner() {
			return new LayoutPoint(x + NODE_SIZE.getWidth() / 2, y + NODE_SIZE.getHeight() / 2);
		}

		@Override
		double getX() {
			return x;
		}

		@Override
		double getY() {
			return y;
		}

	}

	private class Line {
		LayoutPoint a;
		LayoutPoint b;

		Line(LayoutPoint a, LayoutPoint b) {
			this.a = a;
			this.b = b;
		}

	}

	public Double getSlope(LayoutEdge edge) {

		double[] sourcePosition = Toolkit.nodePosition(edge.source.node);
		double[] targetPosition = Toolkit.nodePosition(edge.target.node);

		LayoutPoint p1 = new LayoutPoint(sourcePosition[0], sourcePosition[1]);
		LayoutPoint p2 = new LayoutPoint(targetPosition[0], targetPosition[1]);
		return getSlope(p1, p2);
	}

	private Double getSlope(LayoutPoint P1, LayoutPoint P2) {
		if (P1.x == P2.x) {
			return null; // undefined slope for vertical lines
		}
		return (P2.y - P1.y) / (P2.x - P1.x);
	}

	private LayoutPoint findIntersection(Line l1, Line l2) {

		LayoutPoint p1 = l1.a;
		LayoutPoint p2 = l1.b;
		LayoutPoint p3 = l2.a;
		LayoutPoint p4 = l2.b;

		Double m1, m2, c1, c2;

		// Calculate the slopes, handling vertical lines
		if (p2.x - p1.x == 0) {
			m1 = null;
		} else {
			m1 = (p2.y - p1.y) / (p2.x - p1.x);
		}

		if (p4.x - p3.x == 0) {
			m2 = null;
		} else {
			m2 = (p4.y - p3.y) / (p4.x - p3.x);
		}

		// If both lines are vertical
		if (m1 == null && m2 == null) {
			return null; // The lines overlap
		}
		// If only one line is vertical
		if (m1 == null) {
			c2 = p3.y - m2 * p3.x;
			return new LayoutPoint(p1.x, m2 * p1.x + c2);
		}
		if (m2 == null) {
			c1 = p1.y - m1 * p1.x;
			return new LayoutPoint(p3.x, m1 * p3.x + c1);
		}

		c1 = p1.y - m1 * p1.x;
		c2 = p3.y - m2 * p3.x;

		// If lines are parallel
		if (m1.equals(m2)) {
			return null;
		}

		double xIntersection = (c2 - c1) / (m1 - m2);
		double yIntersection = m1 * xIntersection + c1;

		return new LayoutPoint(xIntersection, yIntersection);
	}

}