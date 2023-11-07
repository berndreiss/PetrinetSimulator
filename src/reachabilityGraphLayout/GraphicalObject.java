package reachabilityGraphLayout;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Node;

/**
 * <p>
 * Class representing an abstract graphical object.
 * </p>
 * 
 * <p>
 * It can represent any quadrangle.
 * </p>
 */
abstract class GraphicalObject implements Comparable<GraphicalObject> {
	
	/**
	 * Get the left lower corner of the object.
	 *
	 * @return the left lower corner
	 */
	abstract LayoutPoint leftLowerCorner();

	/**
	 * Get the left upper corner of the object.
	 *
	 * @return the left upper corner
	 */
	abstract LayoutPoint leftUpperCorner();

	/**
	 * Get the right lower corner of the object.
	 *
	 * @return the right lower corner
	 */
	abstract LayoutPoint rightLowerCorner();

	/**
	 * Get the right upper corner of the object.
	 *
	 * @return the right upper corner
	 */
	abstract LayoutPoint rightUpperCorner();

	/**
	 * Get the x coordinate of the center point.
	 *
	 * @return the x coordinate of the center point.
	 */
	abstract double getCenterX();

	/**
	 * Get the y coordinate of the center point.
	 *
	 * @return the y coordinate of the center point.
	 */
	abstract double getCenterY();

	/**
	 * Get the left side of the object.
	 *
	 * @return the left side
	 */
	LayoutLine leftSide() {
		return new LayoutLine(leftLowerCorner(), leftUpperCorner());
	}

	/**
	 * Get the right side of the object.
	 *
	 * @return the right side
	 */
	LayoutLine rightSide() {
		return new LayoutLine(rightLowerCorner(), rightUpperCorner());
	}

	/**
	 * Get the upper side of the object.
	 *
	 * @return the layout line
	 */
	LayoutLine upperSide() {
		return new LayoutLine(leftUpperCorner(), rightUpperCorner());
	}

	/**
	 * Get the lower side of the object.
	 *
	 * @return the layout line
	 */
	public LayoutLine lowerSide() {
		return new LayoutLine(leftLowerCorner(), rightLowerCorner());
	}

	/**
	 * Compare the centers of two object primarily by their x value and secondarily by the y value.
	 *
	 * @param go the other graphical object
	 * @return -1 if left/lower, 1 if right/upper, 0 if the same
	 */
	@Override
	public int compareTo(GraphicalObject go) {
		if (this.getCenterX() < go.getCenterX())
			return -1;

		if (this.getCenterX() > go.getCenterX())
			return 1;

		if (this.getCenterY() > go.getCenterY())
			return -1;
		if (this.getCenterY() < go.getCenterY())
			return 1;

		return 0;
	}
	
	/**
	 * Graphical objects intersect.
	 *
	 * @param go1 the go 1
	 * @param other the go 2
	 * @return true, if successful
	 */
	public boolean graphicalObjectsIntersect(GraphicalObject other) {

		if (pointIsInsideGraphicalObject(other.leftLowerCorner()))
			return true;

		if (pointIsInsideGraphicalObject(other.leftUpperCorner()))
			return true;

		if (pointIsInsideGraphicalObject(other.rightLowerCorner()))
			return true;

		if (pointIsInsideGraphicalObject(other.rightUpperCorner()))
			return true;

		return false;
	}

	/**
	 * Point is inside graphical object.
	 *
	 * @param go the go
	 * @param p the p
	 * @return true, if successful
	 */
	public boolean pointIsInsideGraphicalObject(LayoutPoint p) {
		if (p == null)
			return false;

		if (p.y <= leftUpperCorner().y && p.y >= leftLowerCorner().y && p.x <= rightLowerCorner().x
				&& p.x >= leftLowerCorner().x)
			return true;

		return false;
	}

	/**
	 * Edge intersects graphical object.
	 *
	 * @param layoutEdge the layout edge
	 * @param go the go
	 * @return true, if successful
	 */
	public boolean edgeIntersectsGraphicalObject(LayoutEdge layoutEdge) {

		if (layoutEdge == null)
			return false;

		Node source = layoutEdge.source.node;
		Node target = layoutEdge.target.node;

		double[] sourcePosition = Toolkit.nodePosition(source);
		double[] targetPosition = Toolkit.nodePosition(target);

		LayoutPoint a = new LayoutPoint(sourcePosition[0], sourcePosition[1]);
		LayoutPoint b = new LayoutPoint(targetPosition[0], targetPosition[1]);

		LayoutLine edge = new LayoutLine(a, b);

		LayoutPoint intersectionPoint;

		intersectionPoint = edge.findIntersection(leftSide());

		if (intersectionPoint != null && intersectionPoint.x >= Math.min(edge.a.x, edge.b.x)
				&& intersectionPoint.x <= Math.max(edge.a.x, edge.b.x)
				&& pointIsInsideGraphicalObject(intersectionPoint))
			return true;

		intersectionPoint = edge.findIntersection(rightSide());

		if (intersectionPoint != null && intersectionPoint.x >= Math.min(edge.a.x, edge.b.x)
				&& intersectionPoint.x <= Math.max(edge.a.x, edge.b.x)
				&& pointIsInsideGraphicalObject(intersectionPoint))
			return true;

		intersectionPoint = edge.findIntersection(lowerSide());

		if (intersectionPoint != null && intersectionPoint.y >= Math.min(edge.a.y, edge.b.y)
				&& intersectionPoint.y <= Math.max(edge.a.y, edge.b.y)
				&& pointIsInsideGraphicalObject(intersectionPoint))
			return true;

		intersectionPoint = edge.findIntersection(upperSide());
		if (intersectionPoint != null && intersectionPoint.y >= Math.min(edge.a.y, edge.b.y)
				&& intersectionPoint.y <= Math.max(edge.a.y, edge.b.y)
				&& pointIsInsideGraphicalObject(intersectionPoint))
			return true;

		return false;

	}

}
