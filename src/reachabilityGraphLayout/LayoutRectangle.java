package reachabilityGraphLayout;

import java.awt.Dimension;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * Class representing a rectangle.
 * </p>
 */
public class LayoutRectangle extends AbstractLayoutRectangle implements LayoutPointInterface{
	
	/** the coordinates of the center */
	private double x, y;
	
	/**  */
	private Dimension POINT_SIZE;
	
	/**
	 * Instantiates a new layout point.
	 *
	 * @param x the x
	 * @param y the y
	 */
	LayoutRectangle(double x, double y){
		this(x, y, Layout.NODE_SIZE);
	}

	/**
	 * Instantiates a new layout point.
	 *
	 * @param x the x
	 * @param y the y
	 * @param dimension the dimension
	 */
	LayoutRectangle(double x, double y, Dimension dimension) {
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


	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

}
