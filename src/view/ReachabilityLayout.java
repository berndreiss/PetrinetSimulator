package view;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.graphstream.algorithm.Toolkit;
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

	public ReachabilityLayout(SpriteManager spriteMan) {
		this.spriteMan = spriteMan;
		listHierarchy = new ArrayList<List<LayoutNode>>();
		nodeMap = new IterableMap<String, ReachabilityLayout.LayoutNode>();

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
			repaintNodes();
			return;
		}

		if (layoutSource == null) {
			layoutSource = new LayoutNode(source, null, 0);

			nodeMap.put(source.getId(), layoutSource);

			layoutSource.upwardsSuccessors.add(layoutTarget);
			layoutTarget.predecessors.add(layoutSource);
			repaintNodes();
			return;
		}

		if (layoutTarget == null) {

			layoutTarget = new LayoutNode(target, layoutSource, layoutSource.level + 1);

			nodeMap.put(target.getId(), layoutTarget);

			layoutSource.successors.add(layoutTarget);
			layoutTarget.predecessors.add(layoutSource);

			repaintNodes();
			return;
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

//		for (List<LayoutNode> nodeList: listHierarchy) {
//			System.out.println("LEVEL " + lvl++);
//			for (LayoutNode node: nodeList)
//				System.out.println(node.node.getId());
//		}

	}

	private void addNodeToLevel(LayoutNode node, int level) {

		if (listHierarchy.size() < level)
			return;

		if (listHierarchy.size() == level)
			listHierarchy.add(new ArrayList<LayoutNode>());

		List<LayoutNode> nodeList = listHierarchy.get(level);

		boolean added = false;

		for (LayoutNode ln : nodeList) {
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

	}

	private void repaintNodes() {

		int lvl = 0;

		

		double heightUnit = (screenSize.getHeight()) / (listHierarchy.size()>1? listHierarchy.size()- 1:1);


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

			double lengthOfRow = 0;

			List<Double> spacing = new ArrayList<Double>();

			for (int i = 0; i < nodeList.size(); i++) {
				LayoutNode node = nodeList.get(i);

				double spaceOfNode = getSpaceNeedeForNode(node) + additionalNodeSpace;

				spacing.add(spaceOfNode);

				lengthOfRow += spaceOfNode;

			}

			double weightCount = 0;
			
			double widthUnit = (screenSize.getWidth()) / (nodeList.size() - 1);

			for (int i = 0; i < nodeList.size(); i++) {

				LayoutNode node = nodeList.get(i);

				weightCount += spacing.get(i);

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
		
		while (j*circleSpacing + indexOffset < nodesTotal.size()) {
			nodesJoined[j*circleSpacing + indexOffset] = nodesTotal.get(totalCounter);

			if (((j+1) *circleSpacing  + indexOffset) >= nodesTotal.size() && (indexOffset + 1) < circleSpacing) {
				j=0;
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

			if (layoutNode == null) {
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

			addNodeToLevel(this, level);
		}

		public String getTag() {
			return parent == null ? "" : parent.getTag() + listHierarchy.get(level).indexOf(this);
		}
	}

	public void setScreenSize(Dimension screenSize) {
		this.screenSize = screenSize;
		repaintNodes();
	}
}
