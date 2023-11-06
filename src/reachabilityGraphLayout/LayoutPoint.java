package reachabilityGraphLayout;

import java.awt.Dimension;

// TODO: Auto-generated Javadoc
/**
 * The Class LayoutPoint.
 */
class LayoutPoint extends GraphicalObject {
	
	/** The y. */
	double x, y;
	
	private Dimension POINT_SIZE;
	
	/**
	 * Instantiates a new layout point.
	 *
	 * @param x the x
	 * @param y the y
	 */
	LayoutPoint(double x, double y){
		this(x, y, Layout.NODE_SIZE);
	}

	/**
	 * Instantiates a new layout point.
	 *
	 * @param x the x
	 * @param y the y
	 * @param dimension the dimension
	 */
	LayoutPoint(double x, double y, Dimension dimension) {
		this.x = x;
		this.y = y;
		this.POINT_SIZE = dimension;
	}

	/**
	 * Left lower corner.
	 *
	 * @return the layout point
	 */
	@Override
	public LayoutPoint leftLowerCorner() {
		return new LayoutPoint(x - POINT_SIZE.getWidth() / 2, y - POINT_SIZE.getHeight() / 2);
	}

	/**
	 * Left upper corner.
	 *
	 * @return the layout point
	 */
	@Override
	public LayoutPoint leftUpperCorner() {
		return new LayoutPoint(x - POINT_SIZE.getWidth() / 2, y + POINT_SIZE.getHeight() / 2);
	}

	/**
	 * Right lower corner.
	 *
	 * @return the layout point
	 */
	@Override
	public LayoutPoint rightLowerCorner() {
		return new LayoutPoint(x + POINT_SIZE.getWidth() / 2, y - POINT_SIZE.getHeight() / 2);
	}

	/**
	 * Right upper corner.
	 *
	 * @return the layout point
	 */
	@Override
	public LayoutPoint rightUpperCorner() {
		return new LayoutPoint(x + POINT_SIZE.getWidth() / 2, y + POINT_SIZE.getHeight() / 2);
	}

	/**
	 * Gets the x.
	 *
	 * @return the x
	 */
	@Override
	double getCenterX() {
		return x;
	}

	/**
	 * Gets the y.
	 *
	 * @return the y
	 */
	@Override
	double getCenterY() {
		return y;
	}

}
