package reachabilityGraphLayout;


// TODO: Auto-generated Javadoc
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
}
