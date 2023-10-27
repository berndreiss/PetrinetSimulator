package ReachabilityGraphLayout;

import java.awt.Dimension;
import java.util.List;

import org.graphstream.ui.spriteManager.SpriteManager;

import util.IterableMap;

public class LayoutPoint extends GraphicalObject {
	double x, y;
	
	private Dimension POINT_SIZE;
	
	LayoutPoint(double x, double y){
		this(x, y, Layout.NODE_SIZE);
	}

	LayoutPoint(double x, double y, Dimension dimension) {
		this.x = x;
		this.y = y;
		this.POINT_SIZE = dimension;
	}

	@Override
	public LayoutPoint leftLowerCorner() {
		return new LayoutPoint(x - POINT_SIZE.getWidth() / 2, y - POINT_SIZE.getHeight() / 2);
	}

	@Override
	public LayoutPoint leftUpperCorner() {
		return new LayoutPoint(x - POINT_SIZE.getWidth() / 2, y + POINT_SIZE.getHeight() / 2);
	}

	@Override
	public LayoutPoint rightLowerCorner() {
		return new LayoutPoint(x + POINT_SIZE.getWidth() / 2, y - POINT_SIZE.getHeight() / 2);
	}

	@Override
	public LayoutPoint rightUpperCorner() {
		return new LayoutPoint(x + POINT_SIZE.getWidth() / 2, y + POINT_SIZE.getHeight() / 2);
	}

	@Override
	double getX() {
		return x;
	}

	@Override
	double getY() {
		return y;
	}

}
