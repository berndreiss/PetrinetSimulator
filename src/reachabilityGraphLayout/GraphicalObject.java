package reachabilityGraphLayout;


public abstract class GraphicalObject implements Comparable<GraphicalObject> {
	abstract LayoutPoint leftLowerCorner();

	abstract LayoutPoint leftUpperCorner();

	abstract LayoutPoint rightLowerCorner();

	abstract LayoutPoint rightUpperCorner();

	abstract double getX();

	abstract double getY();

	public LayoutLine leftSide() {
		return new LayoutLine(leftLowerCorner(), leftUpperCorner());
	}

	public LayoutLine rightSide() {
		return new LayoutLine(rightLowerCorner(), rightUpperCorner());
	}

	public LayoutLine upperSide() {
		return new LayoutLine(leftUpperCorner(), rightUpperCorner());
	}

	public LayoutLine lowerSide() {
		return new LayoutLine(leftLowerCorner(), rightLowerCorner());
	}

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
