package ReachabilityGraphLayout;

import org.graphstream.ui.spriteManager.Sprite;

import datamodel.Transition;

public class LayoutEdge extends GraphicalObject {
	LayoutNode source;
	LayoutNode target;

	Sprite sprite;

	public LayoutEdge(LayoutNode source, LayoutNode target, Sprite sprite) {
		this.source = source;
		this.target = target;
		this.sprite = sprite;
	}

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
	double getX() {
		return source.getX() + (target.getX() - source.getX()) * sprite.getX();
	}

	@Override
	double getY() {
		return source.getY() + (target.getY() - source.getY()) * sprite.getY();
	}

	double length() {// returns the squareroot of (x_2-x_1)^2 + (y_2-y_1)^2
		return Math.sqrt(Math.pow((target.getX() - source.getX()), 2) + Math.pow((target.getY() - source.getY()), 2));
	}

}
