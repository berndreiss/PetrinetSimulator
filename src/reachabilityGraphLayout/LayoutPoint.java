package reachabilityGraphLayout;

/**
 * 
 */
public class LayoutPoint implements LayoutPointInterface{
	/** */
	private double x,y;
	/**
	 * @param x 
	 * @param y 
	 * 
	 */
	public LayoutPoint(double x, double y) {
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
