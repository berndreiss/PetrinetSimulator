package reachabilityGraphLayout;

import java.util.ArrayList;
import java.util.List;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Node;

// TODO: Auto-generated Javadoc
/**
 * The Class LayoutNode.
 */
class LayoutNode extends GraphicalObject {

	/** The node. */
	Node node;
	
	/** The parent. */
	private LayoutNode parent;
	
	/** The children. */
	private List<LayoutNode> children = new ArrayList<LayoutNode>();

	private int level;

	private Layout layout;
	
	/**
	 * Instantiates a new layout node.
	 *
	 * @param node the node
	 * @param parent the parent
	 * @param level the level
	 * @param layout the layout
	 */
	LayoutNode(Node node, LayoutNode parent, int level, Layout layout) {
		this.node = node;
		this.parent = parent;
		this.level = level;
		this.layout = layout;

		if (parent != null)
			parent.children.add(this);

		if (node != null)
			layout.addNodeToLevel(this);
	}

	/**
	 * Gets the tag.
	 *
	 * @return the tag
	 */
	public String getTag() {
		return parent == null ? "" : parent.getTag() + layout.listHierarchy.get(level).indexOf(this);
	}
	
	/**
	 * Gets the level.
	 *
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Left lower corner.
	 *
	 * @return the layout point
	 */
	@Override
	public LayoutPoint leftLowerCorner() {
		double[] coordinates = Toolkit.nodePosition(node);
		return new LayoutPoint(coordinates[0] - Layout.NODE_SIZE.getWidth() / 2,
				coordinates[1] - Layout.NODE_SIZE.getHeight() / 2);
	}

	/**
	 * Left upper corner.
	 *
	 * @return the layout point
	 */
	@Override
	public LayoutPoint leftUpperCorner() {
		double[] coordinates = Toolkit.nodePosition(node);
		return new LayoutPoint(coordinates[0] - Layout.NODE_SIZE.getWidth() / 2,
				coordinates[1] + Layout.NODE_SIZE.getHeight() / 2);
	}

	/**
	 * Right lower corner.
	 *
	 * @return the layout point
	 */
	@Override
	public LayoutPoint rightLowerCorner() {
		double[] coordinates = Toolkit.nodePosition(node);
		return new LayoutPoint(coordinates[0] + Layout.NODE_SIZE.getWidth() / 2,
				coordinates[1] - Layout.NODE_SIZE.getHeight() / 2);
	}

	/**
	 * Right upper corner.
	 *
	 * @return the layout point
	 */
	@Override
	public LayoutPoint rightUpperCorner() {
		double[] coordinates = Toolkit.nodePosition(node);
		return new LayoutPoint(coordinates[0] + Layout.NODE_SIZE.getWidth() / 2,
				coordinates[1] + Layout.NODE_SIZE.getHeight() / 2);
	}

	/**
	 * Gets the x.
	 *
	 * @return the x
	 */
	@Override
	double getX() {
		return Toolkit.nodePosition(node)[0];
	}

	/**
	 * Gets the y.
	 *
	 * @return the y
	 */
	@Override
	double getY() {
		return Toolkit.nodePosition(node)[1];
	}
}