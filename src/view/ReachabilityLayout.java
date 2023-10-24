package view;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.layout.Layout;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import util.IterableMap;

public class ReachabilityLayout {

	private final static Dimension NODE_SIZE = new Dimension(125, 30);

	private Dimension screenSize = new Dimension(1000, 500);

	private static int maxRowCount = 0;

	private List<List<LayoutNode>> listHierarchy;

	private IterableMap<String, LayoutNode> nodeMap;

	private SpriteManager spriteMan;

	private List<LayoutEdge> potentialCulprits = new ArrayList<ReachabilityLayout.LayoutEdge>();

	public ReachabilityLayout(SpriteManager spriteMan) {
		this.spriteMan = spriteMan;
		listHierarchy = new ArrayList<List<LayoutNode>>();
		nodeMap = new IterableMap<String, ReachabilityLayout.LayoutNode>();

//		leftLowerBoundaryNode = graph.addNode(UUID.randomUUID().toString());
//		leftUpperBoundaryNode = graph.addNode(UUID.randomUUID().toString());
//		rightLowerBoundaryNode = graph.addNode(UUID.randomUUID().toString());
//		rightUpperBoundaryNode = graph.addNode(UUID.randomUUID().toString());

//		leftLowerBoundaryNode.setAttribute("ui.class", "blank");
//		leftUpperBoundaryNode.setAttribute("ui.class", "blank");
//		rightLowerBoundaryNode.setAttribute("ui.class", "blank");
//		rightUpperBoundaryNode.setAttribute("ui.class", "blank");
	}

	public void add(Node node) {
		this.add(node, null);
	}

	public void add(Node source, Node target) {

		if (source == null && target == null)
			return;

		if (source == null || target == null) {
			Node node = source == null ? target : source;

			if (!nodeMap.containsKey(node.getId()))
				nodeMap.put(node.getId(), new LayoutNode(node, null, 0));
			return;

		}

		LayoutNode layoutSource = nodeMap.get(source.getId());
		LayoutNode layoutTarget = nodeMap.get(target.getId());

		if (layoutSource == null && layoutTarget == null) {
			layoutSource = new LayoutNode(source, null, 0);
			layoutTarget = new LayoutNode(target, layoutSource, 1);

			nodeMap.put(source.getId(), layoutSource);
			nodeMap.put(target.getId(), layoutTarget);

			layoutSource.successors.add(layoutTarget);
			layoutTarget.predecessors.add(layoutSource);
		}

		else if (layoutSource == null) {
			layoutSource = new LayoutNode(source, null, 0);

			nodeMap.put(source.getId(), layoutSource);

			layoutSource.upwardsSuccessors.add(layoutTarget);
			layoutTarget.predecessors.add(layoutSource);
		}

		else if (layoutTarget == null) {

			layoutTarget = new LayoutNode(target, layoutSource, layoutSource.level + 1);

			nodeMap.put(target.getId(), layoutTarget);

			layoutSource.successors.add(layoutTarget);
			layoutTarget.predecessors.add(layoutSource);

		}

		if (Toolkit.nodePosition(source)[0] <= Toolkit.nodePosition(target)[0]) {

			if (!layoutSource.upwardsSuccessors.contains(layoutTarget))
				layoutSource.upwardsSuccessors.add(layoutTarget);
		} else {
			if (!layoutSource.successors.contains(layoutTarget))
				layoutSource.successors.add(layoutTarget);
		}
		if (!layoutTarget.predecessors.contains(layoutSource))
			layoutTarget.predecessors.add(layoutSource);

		repaintNodes();

		if (Math.abs(layoutSource.level - layoutTarget.level) <= 1)
			return;

		LayoutEdge layoutEdge = new LayoutEdge(layoutSource, layoutTarget);

		potentialCulprits.add(layoutEdge);

		boolean modified = checkEdgeIntersection(layoutEdge);

		if (modified)
			repaintNodes();

		int loops = 0;
		while (modified = true && loops < 3) {
			loops++;
			for (LayoutEdge edge : potentialCulprits) {
				modified = checkEdgeIntersection(layoutEdge);
				if (modified)
					repaintNodes();

			}
		}

	}

	private boolean checkEdgeIntersection(LayoutEdge layoutEdge) {

		LayoutNode layoutSource = layoutEdge.source;
		LayoutNode layoutTarget = layoutEdge.target;

		int startIndex = Math.min(layoutSource.level, layoutTarget.level);

		int finalIndex = Math.max(layoutSource.level, layoutTarget.level);

		for (int i = startIndex + 1; i < finalIndex; i++) {

			List<LayoutNode> nodeList = listHierarchy.get(i);

			for (int j = 0; j < nodeList.size(); j++) {

				LayoutNode ln = nodeList.get(j);

				if (ln.node == null)
					continue;

				if (edgeIntersectsNode(layoutEdge, ln.node) && layoutSource.node != ln.node
						&& layoutTarget.node != ln.node) {

//					if (finalIndex - startIndex == 2) {
//
//						swapNodes(ln, layoutTarget);
//						modified = true;
//						break;
//					}

					System.out.println(j);
					System.out.println(nodeList.size());
					System.out.println(j <= (double) (nodeList.size() - 1) / 2);

					if (leftIsEmpty(nodeList, j)) {
						nodeList.add(j + 1, new LayoutNode(null, null, i));
						return true;
					}

					if (rightIsEmpty(nodeList, j)) {
						nodeList.add(j, new LayoutNode(null, null, i));
						return true;
					}

					if (j <= (double) (nodeList.size() - 1) / 2) {
						System.out.println("Here");
						nodeList.add(j + 1, new LayoutNode(null, null, i));
						return true;
					} else {
						nodeList.add(j, new LayoutNode(null, null, i));
						return true;
					}
				}
			}

		}
		return false;
	}

	private boolean leftIsEmpty(List<LayoutNode> nodeList, int startingIndex) {

		while (startingIndex >= 0) {
			if (nodeList.get(startingIndex).node != null)
				return false;
			startingIndex--;
		}
		return true;

	}

	private boolean rightIsEmpty(List<LayoutNode> nodeList, int startingIndex) {

		while (startingIndex < nodeList.size()) {
			if (nodeList.get(startingIndex).node != null)
				return false;
			startingIndex++;
		}
		return true;

	}

	private void swapNodes(LayoutNode a, LayoutNode b) {
		int indexA = listHierarchy.get(a.level).indexOf(a);
		int indexB = listHierarchy.get(b.level).indexOf(b);

		listHierarchy.get(indexA).remove(a);
		listHierarchy.get(indexA).add(indexA, b);
		listHierarchy.get(indexB).remove(b);
		listHierarchy.get(indexB).add(indexB, a);

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
		
		//TODO check all dependencies

	}

	private void repaintNodes() {

		int lvl = 0;

//		leftMostX = screenSize.getWidth() / 2;
//		rightMostX = leftMostX;
//		topMostY = screenSize.getHeight() / 2;
//		bottomMostY = topMostY;

//		double blankXOffSet = screenSize.getWidth() / 2;
//		double blankYOffSet = screenSize.getHeight() / 2;
//
//		leftLowerBoundaryNode.setAttribute("xy", leftMostX - blankXOffSet, bottomMostY - blankYOffSet);
//		leftUpperBoundaryNode.setAttribute("xy", leftMostX - blankXOffSet, topMostY + blankYOffSet);
//		rightLowerBoundaryNode.setAttribute("xy", rightMostX + blankXOffSet, bottomMostY -blankYOffSet);
//		rightUpperBoundaryNode.setAttribute("xy", rightMostX + blankXOffSet, topMostY + blankYOffSet);

		double heightUnit = (screenSize.getHeight()) / (listHierarchy.size() > 1 ? listHierarchy.size() - 1 : 1);

		int levelMaximum = (int) (screenSize.getHeight() / (NODE_SIZE.getHeight() * 2));

//		double additionalSpace = (listHierarchy.size() > LEVEL_MAXIMUM ? listHierarchy.size() : 0)  * 50;
		double additionalSpace = 0;

//		circlify();
//		if (true)
//			return;
		if (listHierarchy.size() > levelMaximum) {
//			circlify();
//			return;
		}

		for (List<LayoutNode> nodeList : listHierarchy) {

			if (nodeList.size() == 0)
				continue;

//			double additionalNodeSpace = (additionalSpace / nodeList.size())
//					* (nodeList.get(0).level % 2 == 0 ? 1 : -1);

			int currentLevel = listHierarchy.indexOf(nodeList);

			double additionalNodeSpace = 0;

			if (nodeList.size() == 1) {

//				if (listHierarchy.indexOf(nodeList) > 0) {
//					if (listHierarchy.size() > LEVEL_MAXIMUM && !addedOffsetLastLevel && listHierarchy.get(listHierarchy.indexOf(nodeList) - 1).size() == 1) {
//						ADDITIONAL_LEVEL_OFFSET = -LEVEL_OFFSET;
//						addedOffsetLastLevel = true;
//					}
//					else
//						addedOffsetLastLevel = false;
//				}
//				nodeList.get(0).node.setAttribute("xy", 0 + additionalNodeSpace,
//						-(LEVEL_OFFSET * listHierarchy.indexOf(nodeList) + ADDITIONAL_LEVEL_OFFSET));

				nodeList.get(0).node.setAttribute("xy", screenSize.getWidth() / 2,
						-(heightUnit * (listHierarchy.indexOf(nodeList))));
				continue;
			}

//			double lengthOfRow = 0;
//
//			List<Double> spacing = new ArrayList<Double>();
//
//			for (int i = 0; i < nodeList.size(); i++) {
//				LayoutNode node = nodeList.get(i);
//
//				double spaceOfNode = getSpaceNeedeForNode(node) + additionalNodeSpace;
//
//				spacing.add(spaceOfNode);
//
//				lengthOfRow += spaceOfNode;
//
//			}
//
//			double weightCount = 0;

			double widthUnit = (screenSize.getWidth()) / (nodeList.size() - 1);

			for (int i = 0; i < nodeList.size(); i++) {

				LayoutNode node = nodeList.get(i);

//				weightCount += spacing.get(i);

				if (node.node == null)
					continue;

				node.node.setAttribute("xy", (i) * widthUnit, -((node.level) * heightUnit));

			}

//			for (int i = 0; i < nodeList.size(); i++) {
//
//				startingX += spacing.get(i / 2);
//
//				LayoutNode node = nodeList.get(i);
//
//				node.node.setAttribute("xy", startingX, -(node.level * LEVEL_OFFSET + ADDITIONAL_LEVEL_OFFSET));
//
//				startingX += spacing.get(i / 2);
//			}

//			int additionalLayerOffset = 10;

//			for (LayoutNode n : nodeList) {
//				if (nodeList.indexOf(n) < (double) (nodeList.size()/2))
//					n.node.setAttribute("xy", xCounter-additionalLayerOffset*listHierarchy.size(), -LEVEL_OFFSET * listHierarchy.indexOf(nodeList));
//				if (nodeList.indexOf(n) > (double) (nodeList.size()/2))
//					n.node.setAttribute("xy", xCounter+additionalLayerOffset*listHierarchy.size(), -LEVEL_OFFSET * listHierarchy.indexOf(nodeList));
//				else
//					n.node.setAttribute("xy", xCounter, -LEVEL_OFFSET * listHierarchy.indexOf(nodeList));
//					
//				xCounter += xOffset;
//			}
		}

//		System.out.println("\n");
//		addedOffsetLastLevel = false;

		int level = 0;

		for (List<LayoutNode> nodeList : listHierarchy) {
			System.out.println("LEVEL " + level);
			for (LayoutNode ln : nodeList) {
				System.out.println(ln.node == null ? "null" : ln.node.getId());
			}
			level++;
		}

		System.out.println();
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

	private double getSpaceNeedeForNode(LayoutNode node) {
		if (node.successors.size() == 0)
			return 1;

		double spaceForNode = 0;
		for (LayoutNode ln : node.children)
			spaceForNode += getSpaceNeedeForNode(ln);

		return spaceForNode;

	}

	private class LayoutNode {

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
	}

	private class LayoutEdge {
		LayoutNode source;
		LayoutNode target;

		public LayoutEdge(LayoutNode source, LayoutNode target) {
			this.source = source;
			this.target = target;
		}
	}

	public void setScreenSize(Dimension screenSize) {
		this.screenSize = screenSize;
		repaintNodes();
	}

	private boolean edgeIntersectsNode(LayoutEdge layoutEdge, Node node) {

		Node source = layoutEdge.source.node;
		Node target = layoutEdge.target.node;

		if (node == null)
			return false;

		double[] sourcePosition = Toolkit.nodePosition(source);
		double[] targetPosition = Toolkit.nodePosition(target);

		double[] nodePosition = Toolkit.nodePosition(node);
		Point a = new Point(sourcePosition[0], sourcePosition[1]);
		Point b = new Point(targetPosition[0], targetPosition[1]);

		Line edge = new Line(a, b);

		Point leftLowerCorner = new Point(nodePosition[0] - NODE_SIZE.getWidth() / 2,
				nodePosition[1] - NODE_SIZE.getHeight() / 2);
		Point leftUpperCorner = new Point(nodePosition[0] - NODE_SIZE.getWidth() / 2,
				nodePosition[1] + NODE_SIZE.getHeight() / 2);
		Point rightLowerCorner = new Point(nodePosition[0] + NODE_SIZE.getWidth() / 2,
				nodePosition[1] - NODE_SIZE.getHeight() / 2);
		Point rightUpperCorner = new Point(nodePosition[0] + NODE_SIZE.getWidth() / 2,
				nodePosition[1] + NODE_SIZE.getHeight() / 2);

		Point intersectionPoint;

		Line leftSide = new Line(leftLowerCorner, leftUpperCorner);
		intersectionPoint = findIntersection(edge, leftSide);

		if (intersectionPoint != null && intersectionPoint.y <= leftUpperCorner.y
				&& intersectionPoint.y >= leftLowerCorner.y && intersectionPoint.x >= Math.min(edge.a.x, edge.b.x)
				&& intersectionPoint.x <= Math.max(edge.a.x, edge.b.x))
			return true;

		Line rightSide = new Line(rightLowerCorner, rightUpperCorner);
		intersectionPoint = findIntersection(edge, rightSide);

		if (intersectionPoint != null && intersectionPoint.y <= rightUpperCorner.y
				&& intersectionPoint.y >= rightLowerCorner.y && intersectionPoint.x >= Math.min(edge.a.x, edge.b.x)
				&& intersectionPoint.x <= Math.max(edge.a.x, edge.b.x))
			return true;

		Line lowerSide = new Line(leftLowerCorner, rightLowerCorner);
		intersectionPoint = findIntersection(edge, lowerSide);

		if (intersectionPoint != null && intersectionPoint.x <= rightLowerCorner.x
				&& intersectionPoint.x >= leftLowerCorner.x && intersectionPoint.y >= Math.min(edge.a.y, edge.b.y)
				&& intersectionPoint.y <= Math.max(edge.a.y, edge.b.y))
			return true;

		Line upperSide = new Line(leftUpperCorner, rightUpperCorner);
		intersectionPoint = findIntersection(edge, upperSide);
		if (intersectionPoint != null && intersectionPoint.x <= rightUpperCorner.x
				&& intersectionPoint.x >= leftUpperCorner.x && intersectionPoint.y >= Math.min(edge.a.y, edge.b.y)
				&& intersectionPoint.y <= Math.max(edge.a.y, edge.b.y))
			return true;

		return false;

	}

	static class Point {
		double x, y;

		Point(double x, double y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "(" + x + ", " + y + ")";
		}
	}

	static class Line {
		Point a;
		Point b;

		Line(Point a, Point b) {
			this.a = a;
			this.b = b;
		}

	}

	public static Double getSlope(Point P1, Point P2) {
		if (P1.x == P2.x) {
			return null; // undefined slope for vertical lines
		}
		return (P2.y - P1.y) / (P2.x - P1.x);
	}

	public static Point findIntersection(Line l1, Line l2) {

		Point p1 = l1.a;
		Point p2 = l1.b;
		Point p3 = l2.a;
		Point p4 = l2.b;

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
			return new Point(p1.x, m2 * p1.x + c2);
		}
		if (m2 == null) {
			c1 = p1.y - m1 * p1.x;
			return new Point(p3.x, m1 * p3.x + c1);
		}

		c1 = p1.y - m1 * p1.x;
		c2 = p3.y - m2 * p3.x;

		// If lines are parallel
		if (m1.equals(m2)) {
			return null;
		}

		double xIntersection = (c2 - c1) / (m1 - m2);
		double yIntersection = m1 * xIntersection + c1;

		return new Point(xIntersection, yIntersection);
	}

}