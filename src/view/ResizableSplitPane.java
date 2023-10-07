package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.graphstream.ui.swing_viewer.ViewPanel;

import control.PetrinetController;

public class ResizableSplitPane extends JSplitPane {

	private double defaultDividerRatio = 0.5;
	private JFrame parent;
	private JSplitPane container;
	private Dimension preferredSize;

	private GetComponentInterface getComponentInterface;

	public ResizableSplitPane(JFrame parent, int splitOrientation) {
		super(splitOrientation, new JPanel(), new JPanel());

		this.parent = parent;
		this.defaultDividerRatio = 0.5;

		init();
	}

	public ResizableSplitPane(JFrame parent, int splitOrientation, GetComponentInterface getComponentInterface) {
		super(splitOrientation, new JPanel(), new JPanel());
		this.parent = parent;
		this.getComponentInterface = getComponentInterface;

		init();
		this.setLeftComponent(new ResizingPanel(getComponentInterface.getLeftComponent(), preferredSize));

		this.setRightComponent(new ResizingPanel(getComponentInterface.getRightComponent(), preferredSize));

	}

	private void init() {
		this.defaultDividerRatio = 0.5;
		preferredSize = new Dimension((int) (parent.getWidth() / 2 - 10), (int) (parent.getHeight() * 0.5));
		parent.addComponentListener(new FrameResizeAdapter());

	}

	public void setDefaultRatio(double ratio) {
		this.defaultDividerRatio = ratio;
	}

	public void setGetComponentInterface(GetComponentInterface getComponentInterface) {
		this.getComponentInterface = getComponentInterface;
		this.setLeftComponent(new ResizingPanel(getComponentInterface.getLeftComponent(), preferredSize));

		this.setRightComponent(new ResizingPanel(getComponentInterface.getRightComponent(), preferredSize));

	}

	private class FrameResizeAdapter extends ComponentAdapter {

		@Override
		public void componentResized(ComponentEvent e) {

			onResized();
		}

	}

	public GetComponentInterface getGetComponentInterface() {
		return getComponentInterface;
	}

	public void onResized() {
		Dimension oldSizeLeft = getLeftComponent().getSize();
		remove(getLeftComponent());
		setLeftComponent(new ResizingPanel(getComponentInterface.getLeftComponent(), oldSizeLeft));
		Dimension oldSizeRight = getRightComponent().getSize();
		remove(getRightComponent());
		setRightComponent(new ResizingPanel(getComponentInterface.getRightComponent(), oldSizeRight));
		setDividerLocation(defaultDividerRatio);
	}

	private class ResizingPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		public ResizingPanel(Component component, Dimension size) {
			this.setLayout(new BorderLayout());
			this.add(component, BorderLayout.CENTER);
			this.addComponentListener(new PanelResizedAdapter());
			Dimension zeroSize = new Dimension(0, 0);

			this.setMinimumSize(zeroSize);

			this.setPreferredSize(size);

//			this.setSize(size);
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
