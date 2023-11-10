package reachabilityGraphLayout;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Node;

/**
 * <p>
 * Class representing a node in the {@link Layout}.
 * </p>
 * 
 * <p>
 * Because nodes are implemented as rectangles in the layout the LayoutNode
 * extends the abstract rectangle class. It therefore also implements the
 * {@link LayoutPointInterface} where the point is represented by the center of
 * the rectangle (and therefore by the (x,y) coordinates of the nodes in the
 * GraphStream graph).
 * </p>
 */
class LayoutNode extends AbstractLayoutRectangle {

	/** The GraphStream node. */
	private Node node;

	/** The parent node. */
	private LayoutNode parent;

	/** The level in the layout hierarchy the node is in. */
	private int level;

	/** The layout instance the node is in. */
	private Layout layout;

	/**
	 * Instantiates a new layout node.
	 *
	 * @param node   The GraphStream node.
	 * @param parent The parent layout node.
	 * @param level  The level in the layout hierarchy.
	 * @param layout The layout instance the node is in.
	 */
	LayoutNode(Node node, LayoutNode parent, int level, Layout layout) {
		this.node = node;
		this.parent = parent;
		this.level = level;
		this.layout = layout;

		
		//TODO how to handle node == null? -> problem with getX/getY
		
		
		if (node != null)
			layout.addNodeToLevel(this);
	}

	/**
	 * Get the GraphStream node.
	 * 
	 * @return the GraphStream node
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * Get a tag for the node. The tag consists of the parent tag (if the parent !=
	 * null) plus the own index. Nodes in each level can then be sorted by their
	 * parent and own index so that nodes are closer to their parent creating less
	 * intersections between edges.
	 *
	 * @return the tag for the node
	 */
	public String getTag() {
		return parent == null ? "" : parent.getTag() + layout.listHierarchy.get(level).indexOf(this);
	}

	/**
	 * Gets the level.
	 *
	 * @return the level of the node in the layout hierarchy
	 */
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
	public double getX() {
		return Toolkit.nodePosition(node)[0];
	}

	@Override
	public double getY() {
		return Toolkit.nodePosition(node)[1];
	}
}