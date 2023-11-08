package reachabilityGraphLayout;

// TODO: Auto-generated Javadoc
/**
 * The Class LayoutLine.
 */
class LayoutLine {

	/** The a. */
	protected LayoutPoint a;

	/** The b. */
	protected LayoutPoint b;

	/**
	 * Instantiates a new layout line.
	 *
	 * @param a the a
	 * @param b the b
	 */
	LayoutLine(LayoutPoint a, LayoutPoint b) {
		this.a = a;
		this.b = b;
	}

	/**
	 * Find intersection.
	 *
	 * @param l1    the l 1
	 * @param other the l 2
	 * @return the layout point
	 */
	public LayoutPoint findIntersectionPoint(LayoutLine other) {

		// https://www.geeksforgeeks.org/program-for-point-of-intersection-of-two-lines/
		
		LayoutPoint p1 = this.a;
		LayoutPoint p2 = this.b;
		LayoutPoint p3 = other.a;
		LayoutPoint p4 = other.b;

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
		
//		Double s1, s2, c1 = 0.0, c2 = 0.0;
//
//		// Calculate the slopes, handling vertical lines
//		if (p2.x - p1.x == 0) {
//			s1 = null;
//		} else {
//			s1 = (p2.y - p1.y) / (p2.x - p1.x);
//		}
//
//		if (p4.x - p3.x == 0) {
//			s2 = null;
//		} else {
//			s2 = (p4.y - p3.y) / (p4.x - p3.x);
//		}
//
//		// If both lines are vertical there are non or infinitely many intersection
//		// points
//		if (s1 == null && s2 == null)
//			return null;
//
//		if (s1 != null)
//			c1 = p1.y - s1 * p1.x;
//		if (s2 != null)
//			c2 = p3.y - s2 * p3.x;
//
//		// If only one line is vertical
//		if (s1 == null)
//			return new LayoutPoint(p1.x, s2 * p1.x + c2);
//		if (s2 == null)
//			return new LayoutPoint(p3.x, s1 * p3.x + c1);
//
//		// If both lines are horizontal there are non or infinitely many intersection
//		// points
//		if (s1.equals(s2))
//			return null;
//
//		double xIntersection = (c2 - c1) / (s1 - s2);
//		double yIntersection = s1 * xIntersection + c1;
//
//		return new LayoutPoint(xIntersection, yIntersection);
	}
}
