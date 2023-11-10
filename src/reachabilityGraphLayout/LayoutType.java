package reachabilityGraphLayout;

/**
 * <p>
 * Enumerates the different available layout types.
 * </p>
 */
public enum LayoutType {

	/**
	 * The auto layout provided by GraphStream (see this <a href=
	 * "https://graphstream-project.org/doc/Tutorials/Graph-Visualisation/1.0/">link</a>).
	 */
	AUTOMATIC,
	/**
	 * A layout arranging nodes around a circle (or to be more specific around an
	 * ellipse). It is not the clearest representation for every graph but it can be
	 * useful sometimes. It also produces nice shapes. On choosing the layout type
	 * multiple times the user can change the ratio for which edges are spread apart
	 * in a given graph.
	 */
	CIRCLE,
	/**
	 * The tree layout arranges the nodes in a tree representing a level hierarchy.
	 * Top most is the starting node and every step needed to to reach another node
	 * increases the depth of the tree. If a node already exists an edge to that
	 * node is created meaning there can also be upwards edges. Therefore the layout
	 * does not represent a tree in the mathematical sense. This is the clearest
	 * layout for big graphs and is implemented as the standard choice.
	 */
	TREE;
}
