package reachabilityGraphLayout;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;

// TODO: Auto-generated Javadoc
/**
 * The Class LayoutEdge.
 */
class LayoutEdge extends AbstractLayoutRectangle {

	/** The source. */
	LayoutNode source;

	/** The target. */
	LayoutNode target;

	/** The sprite. */
	Sprite sprite;

	/**
	 * Instantiates a new layout edge.
	 *
	 * @param source the source
	 * @param target the target
	 * @param sprite the sprite
	 */
	LayoutEdge(LayoutNode source, LayoutNode target, Sprite sprite) {
		this.source = source;
		this.target = target;
		this.sprite = sprite;
	}

	/**
	 * Gets the sprite.
	 *
	 * @return the sprite
	 */
	public Sprite getSprite() {
		return sprite;
	}

	/**
	 * Left lower corner.
	 *
	 * @return the layout point
	 */
	@Override
	public LayoutPoint leftLowerCorner() {

		return new LayoutPoint(getX() - Layout.SPRITE_SIZE.getWidth() / 2, getY() - Layout.SPRITE_SIZE.getHeight() / 2);
	}

	/**
	 * Left upper corner.
	 *
	 * @return the layout point
	 */
	@Override
	public LayoutPoint leftUpperCorner() {
		return new LayoutPoint(getX() - Layout.SPRITE_SIZE.getWidth() / 2, getY() + Layout.SPRITE_SIZE.getHeight() / 2);
	}

	/**
	 * Right lower corner.
	 *
	 * @return the layout point
	 */
	@Override
	public LayoutPoint rightLowerCorner() {
		return new LayoutPoint(getX() + Layout.SPRITE_SIZE.getWidth() / 2, getY() - Layout.SPRITE_SIZE.getHeight() / 2);
	}

	/**
	 * Right upper corner.
	 *
	 * @return the layout point
	 */
	@Override
	public LayoutPoint rightUpperCorner() {
		return new LayoutPoint(getX() + Layout.SPRITE_SIZE.getWidth() / 2, getY() + Layout.SPRITE_SIZE.getHeight() / 2);
	}

	@Override
	public double getX() {
		return source.getX() + (target.getX() - source.getX()) * sprite.getX();
	}

	@Override
	public double getY() {
		return source.getY() + (target.getY() - source.getY()) * sprite.getY();
	}

	/**
	 * Length.
	 *
	 * @return the double
	 */
	double length() {// returns the square root of (x_2-x_1)^2 + (y_2-y_1)^2 (Pythagorean Theorem)
		return Math.sqrt(Math.pow((target.getX() - source.getX()), 2) + Math.pow((target.getY() - source.getY()), 2));
	}

	/**
	 * 
	 * @return
	 */
	public LayoutLine getEdgeLine() {
		Node source = this.source.node;
		Node target = this.target.node;
		double[] sourcePosition = Toolkit.nodePosition(source);
		double[] targetPosition = Toolkit.nodePosition(target);
		LayoutPoint a = new LayoutPoint(sourcePosition[0], sourcePosition[1]);
		LayoutPoint b = new LayoutPoint(targetPosition[0], targetPosition[1]);
		return new LayoutLine(a, b);
	}
}
