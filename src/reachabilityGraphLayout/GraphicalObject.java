package reachabilityGraphLayout;


// TODO: Auto-generated Javadoc
/**
 * The Class GraphicalObject.
 */
abstract class GraphicalObject implements Comparable<GraphicalObject> {
	
	/**
	 * Left lower corner.
	 *
	 * @return the layout point
	 */
	abstract LayoutPoint leftLowerCorner();

	/**
	 * Left upper corner.
	 *
	 * @return the layout point
	 */
	abstract LayoutPoint leftUpperCorner();

	/**
	 * Right lower corner.
	 *
	 * @return the layout point
	 */
	abstract LayoutPoint rightLowerCorner();

	/**
	 * Right upper corner.
	 *
	 * @return the layout point
	 */
	abstract LayoutPoint rightUpperCorner();

	/**
	 * Gets the x.
	 *
	 * @return the x
	 */
	abstract double getX();

	/**
	 * Gets the y.
	 *
	 * @return the y
	 */
	abstract double getY();

	/**
	 * Left side.
	 *
	 * @return the layout line
	 */
	LayoutLine leftSide() {
		return new LayoutLine(leftLowerCorner(), leftUpperCorner());
	}

	/**
	 * Right side.
	 *
	 * @return the layout line
	 */
	LayoutLine rightSide() {
		return new LayoutLine(rightLowerCorner(), rightUpperCorner());
	}

	/**
	 * Upper side.
	 *
	 * @return the layout line
	 */
	LayoutLine upperSide() {
		return new LayoutLine(leftUpperCorner(), rightUpperCorner());
	}

	/**
	 * Lower side.
	 *
	 * @return the layout line
	 */
	public LayoutLine lowerSide() {
		return new LayoutLine(leftLowerCorner(), rightLowerCorner());
	}

	/**
	 * Compare to.
	 *
	 * @param go the go
	 * @return the int
	 */
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
