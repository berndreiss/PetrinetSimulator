package reachabilityGraphLayout;

import java.util.ArrayList;
import java.util.List;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Node;

public class LayoutNode extends GraphicalObject {

	public Node node;
	public LayoutNode parent;
	public List<LayoutNode> children = new ArrayList<LayoutNode>();

	private int level;

	private Layout layout;
	
	public LayoutNode(Node node, LayoutNode parent, int level, Layout layout) {
		this.node = node;
		this.parent = parent;
		this.level = level;
		this.layout = layout;

		if (parent != null)
			parent.children.add(this);

		if (node != null)
			layout.addNodeToLevel(this);
	}

	public String getTag() {
		return parent == null ? "" : parent.getTag() + layout.listHierarchy.get(level).indexOf(this);
	}
	
	public int getLevel() {
		return level;
	}

	@Override
	public LayoutPoint leftLowerCorner() {
		double[] coordinates = Toolkit.nodePosition(node);
		return new LayoutPoint(coordinates[0] - Layout.NODE_SIZE.getWidth() / 2,
				coordinates[1] - Layout.NODE_SIZE.getHeight() / 2);
	}

	@Override
	public LayoutPoint leftUpperCorner() {
		double[] coordinates = Toolkit.nodePosition(node);
		return new LayoutPoint(coordinates[0] - Layout.NODE_SIZE.getWidth() / 2,
				coordinates[1] + Layout.NODE_SIZE.getHeight() / 2);
	}

	@Override
	public LayoutPoint rightLowerCorner() {
		double[] coordinates = Toolkit.nodePosition(node);
		return new LayoutPoint(coordinates[0] + Layout.NODE_SIZE.getWidth() / 2,
				coordinates[1] - Layout.NODE_SIZE.getHeight() / 2);
	}

	@Override
	public LayoutPoint rightUpperCorner() {
		double[] coordinates = Toolkit.nodePosition(node);
		return new LayoutPoint(coordinates[0] + Layout.NODE_SIZE.getWidth() / 2,
				coordinates[1] + Layout.NODE_SIZE.getHeight() / 2);
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