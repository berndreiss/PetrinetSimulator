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
	 * @param l1 the l 1
	 * @param other the l 2
	 * @return the layout point
	 */
	public LayoutPoint findIntersection(LayoutLine other) {

		LayoutPoint p1 = this.a;
		LayoutPoint p2 = this.b;
		LayoutPoint p3 = other.a;
		LayoutPoint p4 = other.b;

		Double m1, m2, c1, c2;

		// Calculate the slopes, handling vertical lines
		if (p2.x - p1.x == 0) {
			m1 = null;
		} else {
			m1 = (p2.y - p1.y) / (p2.x - p1.x);
		}

		if (p4.x - p3.x == 0) {
			m2 = null;
		} else {
			m2 = (p4.y - p3.y) / (p4.x - p3.x);
		}

		// If both lines are vertical
		if (m1 == null && m2 == null) {
			return null; // The lines overlap
		}
		// If only one line is vertical
		if (m1 == null) {
			c2 = p3.y - m2 * p3.x;
			return new LayoutPoint(p1.x, m2 * p1.x + c2);
		}
		if (m2 == null) {
			c1 = p1.y - m1 * p1.x;
			return new LayoutPoint(p3.x, m1 * p3.x + c1);
		}

		c1 = p1.y - m1 * p1.x;
		c2 = p3.y - m2 * p3.x;

		// If lines are parallel
		if (m1.equals(m2)) {
			return null;
		}

		double xIntersection = (c2 - c1) / (m1 - m2);
		double yIntersection = m1 * xIntersection + c1;

		return new LayoutPoint(xIntersection, yIntersection);
	}
}
