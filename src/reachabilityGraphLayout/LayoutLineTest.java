package reachabilityGraphLayout;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 */
public class LayoutLineTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		testLinesIntersect();
		testLinesDoNotIntersect();
		testCoincidentLines();
		testVerticalAndHorizontalLines();
	}
	
    /**
     * 
     */
    @Test
    public static void testLinesIntersect() {
        LayoutPoint a1 = new LayoutPoint(0, 0);
        LayoutPoint b1 = new LayoutPoint(10, 10);
        LayoutLine line1 = new LayoutLine(a1, b1);

        LayoutPoint a2 = new LayoutPoint(0, 10);
        LayoutPoint b2 = new LayoutPoint(10, 0);
        LayoutLine line2 = new LayoutLine(a2, b2);

        LayoutPoint intersection = line1.findIntersectionPoint(line2);

        assertNotNull("Lines should intersect", intersection);
        assertEquals("Intersection X coordinate is incorrect", 5.0, intersection.getX(), 0.001);
        assertEquals("Intersection Y coordinate is incorrect", 5.0, intersection.getY(), 0.001);
        System.out.println("LINE INTERSECTION TEST PASSED");
    }

    /**
     * 
     */
    @Test
    public static void testLinesDoNotIntersect() {
        LayoutPoint a1 = new LayoutPoint(0, 0);
        LayoutPoint b1 = new LayoutPoint(10, 10);
        LayoutLine line1 = new LayoutLine(a1, b1);

        LayoutPoint a2 = new LayoutPoint(0, 1);
        LayoutPoint b2 = new LayoutPoint(10, 11);
        LayoutLine line2 = new LayoutLine(a2, b2);

        LayoutPoint intersection = line1.findIntersectionPoint(line2);

        assertNull("Lines should not intersect", intersection);
        System.out.println("LINE NO INTERSECTION TEST PASSED");
    }

    /**
     * 
     */
    @Test
    public static void testCoincidentLines() {
        LayoutPoint a1 = new LayoutPoint(0, 0);
        LayoutPoint b1 = new LayoutPoint(10, 10);
        LayoutLine line1 = new LayoutLine(a1, b1);

        LayoutPoint a2 = new LayoutPoint(0, 0);
        LayoutPoint b2 = new LayoutPoint(10, 10);
        LayoutLine line2 = new LayoutLine(a2, b2);

        LayoutPoint intersection = line1.findIntersectionPoint(line2);

        assertNull("Coincident lines have infinite intersections", intersection);
        System.out.println("COINCIDENT LINE TEST PASSED");
    }

    /**
     * 
     */
    @Test
    public static void testVerticalAndHorizontalLines() {
        LayoutPoint a1 = new LayoutPoint(5, 0);
        LayoutPoint b1 = new LayoutPoint(5, 10);
        LayoutLine line1 = new LayoutLine(a1, b1);

        LayoutPoint a2 = new LayoutPoint(0, 5);
        LayoutPoint b2 = new LayoutPoint(10, 5);
        LayoutLine line2 = new LayoutLine(a2, b2);

        LayoutPoint intersection = line1.findIntersectionPoint(line2);

        assertNotNull("Lines should intersect", intersection);
        assertEquals("Intersection X coordinate is incorrect", 5.0, intersection.getX(), 0.001);
        assertEquals("Intersection Y coordinate is incorrect", 5.0, intersection.getY(), 0.001);
        System.out.println("VERTICAL AND HORIZONTAL LINE TEST PASSED");
    }
}