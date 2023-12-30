package reachabilityGraphLayout;

/**
 * <p>
 * Point with x and y coordinate implemented using double.
 * </p>
 */
class LayoutPoint implements LayoutPointInterface {
	/** The x and y coordinates. */
	private double x, y;

	/**
	 * 
	 * Instantiates a new LayoutPoint.
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * 
	 */
	LayoutPoint(double x, double y) {
		this.x = x;
		this.y = y;
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
