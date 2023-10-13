package view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;



public class ResizableSplitPane extends JSplitPane {

	private static final long serialVersionUID = 1L;
	private double defaultDividerRatio = 0.5;
	private JFrame parent;
	private Dimension preferredSize;

	public ResizableSplitPane(JFrame parent, int splitOrientation) {
		this(parent, splitOrientation, new JPanel(), new JPanel());
	}
	public ResizableSplitPane(JFrame parent, int splitOrientation, Component left, Component right) {
		super(splitOrientation, left, right);

		this.parent = parent;
		this.defaultDividerRatio = 0.5;

		initialize();

	}


	@Override
	public void setRightComponent(Component comp) {
		super.setRightComponent(comp);
		if (parent != null)
			initialize();
	}
	
	@Override
	public void setLeftComponent(Component comp) {
		super.setLeftComponent(comp);
		if (parent != null)
			initialize();
	}

	private void initialize() {
		preferredSize = new Dimension((int) (parent.getWidth() / 2 - 10), (int) (parent.getHeight() * MainFrame.GRAPH_PERCENT));
		
		Dimension zeroSize = new Dimension(0, 0);
		
		Component left = getLeftComponent();
		Component right = getRightComponent();

		left.setPreferredSize(preferredSize);
		left.setMinimumSize(zeroSize);
		left.addComponentListener(new PanelResizedAdapter());
		
		right.setPreferredSize(preferredSize);
		right.setMinimumSize(zeroSize);
		
		
		parent.addComponentListener(new FrameResizeAdapter());		
		setDividerLocation(defaultDividerRatio);

	}
	public void setDefaultRatio(double ratio) {
		this.defaultDividerRatio = ratio;
	}


	private class FrameResizeAdapter extends ComponentAdapter {

		@Override
		public void componentResized(ComponentEvent e) {
			setDividerLocation(defaultDividerRatio);

		}

	}

	private class PanelResizedAdapter extends ComponentAdapter {

		@Override
		public void componentResized(ComponentEvent e) {
			if (getOrientation() == JSplitPane.HORIZONTAL_SPLIT)
				defaultDividerRatio = (double) getLeftComponent().getWidth()
						/ (getLeftComponent().getWidth() + getRightComponent().getWidth());
			else
				defaultDividerRatio = (double) getLeftComponent().getHeight()
						/ (getLeftComponent().getHeight() + getRightComponent().getHeight());

			getLeftComponent().repaint();// arrows on edges are not rendering properly without repaint
			getRightComponent().repaint();// arrows on edges are not rendering properly without repaint
		}

	}

}
