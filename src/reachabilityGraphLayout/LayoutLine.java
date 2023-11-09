package reachabilityGraphLayout;

/**
 * <p>
 * Class representing a line.
 * </p>
 */
public class LayoutLine {

	/** The first point defining the line. */
	private LayoutPointInterface a;

	/** The second point defining the line. */
	private LayoutPointInterface b;

	/**
	 * Instantiates a new layout line.
	 *
	 * @param a the a
	 * @param b the b
	 */
	LayoutLine(LayoutPointInterface a, LayoutPointInterface b) {
		this.a = a;
		this.b = b;
	}

	/**
	 * Finds the intersection between two straight lines (code adapted from <a href=
	 * "https://www.geeksforgeeks.org/program-for-point-of-intersection-of-two-lines/">GeeksForGeeks</a>)
	 * which does not have to be on either line. Will return null if lines are
	 * parallel (including overlapping).
	 *
	 * @param other The line for which to find intersection.
	 * @return the point where the lines intersect, null if there is no unambiguous
	 *         single point
	 */
	public LayoutPointInterface findIntersectionPoint(LayoutLine other) {

		LayoutPointInterface p1 = this.a;
		LayoutPointInterface p2 = this.b;
		LayoutPointInterface p3 = other.a;
		LayoutPointInterface p4 = other.b;

		// Line p1p2 represented as a1x + b1y = c1
		double a1 = p2.getX() - p1.getY();
		double b1 = p1.getX() - p2.getX();
		double c1 = a1 * (p1.getX()) + b1 * (p1.getY());

		// Line p3p4 represented as a2x + b2y = c2
		double a2 = p4.getY() - p3.getY();
		double b2 = p3.getX() - p4.getX();
		double c2 = a2 * (p3.getX()) + b2 * (p3.getY());

		double determinant = a1 * b2 - a2 * b1;

		if (determinant == 0)
			return null;

		double x = (b2 * c1 - b1 * c2) / determinant;
		double y = (a1 * c2 - a2 * c1) / determinant;
		return new LayoutPoint(x, y);

	}

	/**
	 * Get the biggest x coordinate on the line.
	 * 
	 * @return biggest x coordinate
	 */
	public double getXMax() {
		return Math.max(a.getX(), b.getX());
	}

	/**
	 * Get the smallest x coordinate on the line.
	 * 
	 * @return smallest x coordinate
	 */
	public double getXMin() {
		return Math.min(a.getX(), b.getX());
	}

	/**
	 * Get the biggest y coordinate on the line.
	 * 
	 * @return biggest y coordinate
	 */
	public double getYMax() {
		return Math.max(a.getY(), b.getY());
	}

	/**
	 * Get the smallest y coordinate on the line.
	 * 
	 * @return smallest y coordinate
	 */
	public double getYMin() {
		return Math.min(a.getY(), b.getY());
	}
}
