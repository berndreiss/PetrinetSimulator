package reachabilityGraphLayout;

/**
 * <p>
 * Class defining methods for a rectangle (and implementing some).
 * </p>
 */
abstract class AbstractLayoutRectangle implements Comparable<AbstractLayoutRectangle>, LayoutPointInterface {

	/**
	 * Gets the left lower corner of the object.
	 *
	 * @return the left lower corner
	 */
	public abstract LayoutPoint leftLowerCorner();

	/**
	 * Gets the left upper corner of the object.
	 *
	 * @return the left upper corner
	 */
	public abstract LayoutPoint leftUpperCorner();

	/**
	 * Gets the right lower corner of the object.
	 *
	 * @return the right lower corner
	 */
	public abstract LayoutPoint rightLowerCorner();

	/**
	 * Gets the right upper corner of the object.
	 *
	 * @return the right upper corner
	 */
	public abstract LayoutPoint rightUpperCorner();

	/**
	 * Gets the left side of the object.
	 *
	 * @return the left side
	 */
	private LayoutLine leftSide() {
		return new LayoutLine(leftLowerCorner(), leftUpperCorner());
	}

	/**
	 * Gets the right side of the object.
	 *
	 * @return the right side
	 */
	private LayoutLine rightSide() {
		return new LayoutLine(rightLowerCorner(), rightUpperCorner());
	}

	/**
	 * Gets the upper side of the object.
	 *
	 * @return the layout line
	 */
	private LayoutLine upperSide() {
		return new LayoutLine(leftUpperCorner(), rightUpperCorner());
	}

	/**
	 * Gets the lower side of the object.
	 *
	 * @return the layout line
	 */
	private LayoutLine lowerSide() {
		return new LayoutLine(leftLowerCorner(), rightLowerCorner());
	}

	/**
	 * Compares the centers of two rectangles primarily by their x value and
	 * secondarily by the y value.
	 *
	 * @param other the other rectangle
	 * @return -1 if left/lower, 1 if right/upper, 0 if the same
	 */
	@Override
	public int compareTo(AbstractLayoutRectangle other) {
		if (this.getX() < other.getX())
			return -1;

		if (this.getX() > other.getX())
			return 1;

		if (this.getY() > other.getY())
			return -1;
		if (this.getY() < other.getY())
			return 1;

		return 0;
	}

	/**
	 * Checks whether the given rectangle intersects another rectangle.
	 *
	 * @param other the other rectangle
	 * @return true, if rectangles intersect, false if not
	 */
	boolean rectanglesIntersect(AbstractLayoutRectangle other) {

		// check whether any of the corners of the other rectangle is inside this
		// rectangle, if so return true, if non is inside return false

		if (pointIsInsideRectangle(other.leftLowerCorner()))
			return true;

		if (pointIsInsideRectangle(other.leftUpperCorner()))
			return true;

		if (pointIsInsideRectangle(other.rightLowerCorner()))
			return true;

		if (pointIsInsideRectangle(other.rightUpperCorner()))
			return true;

		return false;
	}

	/**
	 * Checks whether a point is inside the rectangle.
	 *
	 * @param p the point to be checked
	 * @return true, if it lies inside the rectangle, false if not
	 */
	private boolean pointIsInsideRectangle(LayoutPointInterface p) {
		if (p == null)// safety check
			return false;

		// check whether y coordinate of point lies within the coordinates of upper and
		// lower corners (which one does not matter, as they are the same), do the same
		// with the x coordinate
		if (p.getY() <= leftUpperCorner().getY() && p.getY() >= leftLowerCorner().getY()
				&& p.getX() <= rightLowerCorner().getX() && p.getX() >= leftLowerCorner().getX())
			return true;

		// if above check failed return false
		return false;
	}

	/**
	 * Checks whether edge intersects rectangle.
	 *
	 * @param layoutEdge the edge to be checked
	 * @return true, if it intersects, false if not
	 */
	boolean edgeIntersectsRectangle(LayoutEdge layoutEdge) {

		if (layoutEdge == null)// safety check
			return false;

		// get source and target of edge as points and create a LayoutEdge
		LayoutLine edge = layoutEdge.getEdgeLine();

		// check edge against all sides of the rectangle -> first get the intersection
		// point: if it exists is on the edge and lies within the rectangle return true,
		// if non of the sides have intersection points that lie on the edge and within
		// the rectangle return false
		// keep track of current intersection point
		LayoutPointInterface intersectionPoint;

		// check left side
		intersectionPoint = edge.findIntersectionPoint(leftSide());
		if (intersectionPoint != null && intersectionPoint.getX() >= edge.getXMin()
				&& intersectionPoint.getX() <= edge.getXMax() && pointIsInsideRectangle(intersectionPoint))
			return true;

		// check right side
		intersectionPoint = edge.findIntersectionPoint(rightSide());
		if (intersectionPoint != null && intersectionPoint.getX() >= edge.getXMin()
				&& intersectionPoint.getX() <= edge.getXMax() && pointIsInsideRectangle(intersectionPoint))
			return true;

		// check lower side
		intersectionPoint = edge.findIntersectionPoint(lowerSide());
		if (intersectionPoint != null && intersectionPoint.getY() >= edge.getYMin()
				&& intersectionPoint.getY() <= edge.getYMax() && pointIsInsideRectangle(intersectionPoint))
			return true;

		// check upper side
		intersectionPoint = edge.findIntersectionPoint(upperSide());
		if (intersectionPoint != null && intersectionPoint.getY() >= edge.getYMin()
				&& intersectionPoint.getY() <= edge.getYMax() && pointIsInsideRectangle(intersectionPoint))
			return true;

		// all checks failed
		return false;

	}

}
