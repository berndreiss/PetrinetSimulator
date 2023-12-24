package reachabilityGraphLayout;

import org.graphstream.ui.spriteManager.Sprite;

/**
 * <p>
 * Class representing an edge in the layout.
 * </p>
 * 
 * <p>
 * Because it contains a GraphStream sprite it extends the abstract rectangle
 * class.
 * </p>
 */
public class LayoutEdge extends AbstractLayoutRectangle {

	/** The source node of the edge. */
	LayoutNode source;

	/** The target node of the edge. */
	LayoutNode target;

	/** The sprite from GraphStream */
	Sprite sprite;

	/**
	 * Instantiates a new layout edge.
	 *
	 * @param source the source node
	 * @param target the target node
	 * @param sprite the sprite from GraphStream
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

	@Override
	public LayoutPoint leftLowerCorner() {

		return new LayoutPoint(getX() - Layout.SPRITE_SIZE.getWidth() / 2, getY() - Layout.SPRITE_SIZE.getHeight() / 2);
	}

	@Override
	public LayoutPoint leftUpperCorner() {
		return new LayoutPoint(getX() - Layout.SPRITE_SIZE.getWidth() / 2, getY() + Layout.SPRITE_SIZE.getHeight() / 2);
	}

	@Override
	public LayoutPoint rightLowerCorner() {
		return new LayoutPoint(getX() + Layout.SPRITE_SIZE.getWidth() / 2, getY() - Layout.SPRITE_SIZE.getHeight() / 2);
	}

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
	 * Gets the length calculated using Pythagorean Theorem: the square root of
	 * (x_2-x_1)^2 + (y_2-y_1)^2.
	 *
	 * @return the length of the edge
	 */
	double length() {
		return Math.sqrt(Math.pow((target.getX() - source.getX()), 2) + Math.pow((target.getY() - source.getY()), 2));
	}

	/**
	 * Gets the edge as an instance of LayoutLine.
	 * 
	 * @return the edge as a layout line
	 */
	public LayoutLine getEdgeLine() {
		return new LayoutLine(source, target);
	}

}
