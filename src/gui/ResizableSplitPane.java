package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

// TODO: Auto-generated Javadoc
/**
 * The Class ResizableSplitPane.
 */
public class ResizableSplitPane extends JSplitPane {

	private static final long serialVersionUID = 1L;
	private double defaultDividerRatio = 0.5;
	private MainFrame parent;
	private Dimension preferredSize;
	private int toolbarOffSet;
	private double sizeReference; // keeps count of current width/height so that the divider ratio is not updated,
									// if parent has only been modified in height/width (since that does not affect
									// the divider ratio)
	private boolean frameResized = false;

	/**
	 * Instantiates a new resizable split pane.
	 *
	 * @param parent           the parent
	 * @param splitOrientation the split orientation
	 */
	ResizableSplitPane(MainFrame parent, int splitOrientation) {
		this(parent, splitOrientation, new JPanel(), new JPanel());
	}

	/**
	 * Instantiates a new resizable split pane.
	 *
	 * @param parent           the parent
	 * @param splitOrientation the split orientation
	 * @param left             the left
	 * @param right            the right
	 */
	public ResizableSplitPane(MainFrame parent, int splitOrientation, Component left, Component right) {
		super(splitOrientation, left, right);

		this.parent = parent;
		this.defaultDividerRatio = 0.5;

		initialize();

	}

	/**
	 * Sets the right component.
	 *
	 * @param comp the new right component
	 */
	@Override
	public void setRightComponent(Component comp) {
		super.setRightComponent(comp);
		if (parent != null)
			initialize();
	}

	/**
	 * Sets the left component.
	 *
	 * @param comp the new left component
	 */
	@Override
	public void setLeftComponent(Component comp) {
		super.setLeftComponent(comp);
		if (parent != null)
			initialize();
	}

	private void initialize() {

		toolbarOffSet = 0;
		if (parent.getToolbar() != null && parent.getToolbar().getOrientation() == SwingConstants.VERTICAL) {
			toolbarOffSet += parent.getToolbar().getWidth();
		}
		preferredSize = new Dimension((int) (parent.getWidth() / 2 - 7 - toolbarOffSet / 2),
				(int) (parent.getHeight() * MainFrame.GRAPH_PERCENT));

		if (getOrientation() == JSplitPane.VERTICAL_SPLIT)
			sizeReference = preferredSize.getHeight();
		else
			sizeReference = preferredSize.getWidth();

		if (getOrientation() == JSplitPane.HORIZONTAL_SPLIT)
			System.out.println("INITIALIZE ->" + sizeReference);
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
//		SwingUtilities.invokeLater(() -> setDividerLocation(defaultDividerRatio));//wait for all pending Swing events to be processed, so the size of left and right components is set correctly and it doesn't collide to the left
//		setDividerLocation(defaultDividerRatio);

	}

	/**
	 * Sets the default ratio.
	 *
	 * @param ratio the new default ratio
	 */
	public void setDefaultRatio(double ratio) {
		this.defaultDividerRatio = ratio;
	}

	/**
	 * Reset divider.
	 */
	public void resetDivider() {
		initialize();
	}

	private class FrameResizeAdapter extends ComponentAdapter {

		@Override
		public void componentResized(ComponentEvent e) {
			frameResized = true;
			System.out.println(e);
			setDividerLocation(defaultDividerRatio);
			System.out.println(defaultDividerRatio);

		}

	}

	private class PanelResizedAdapter extends ComponentAdapter {

		@Override
		public void componentResized(ComponentEvent e) {

			System.out.println(frameResized);
//			if (parent.getSize() != parentReference)
//				return;
			if (frameResized) {
				frameResized = false;
				return;
			}
			if (getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {

				System.out.println(e.getSource());
				System.out.println("COMPONENT RESIZED ->" + sizeReference);
				System.out.println("COMPONENT RESIZED COMP HEIGHT ->" + e.getComponent().getHeight());
				System.out.println("COMPONENT RESIZED COMP WIDTH ->" + e.getComponent().getWidth());
			}
			if (getOrientation() == JSplitPane.HORIZONTAL_SPLIT && e.getComponent().getWidth() != sizeReference) {
				defaultDividerRatio = (double) getLeftComponent().getWidth()
						/ (getLeftComponent().getWidth() + getRightComponent().getWidth());
				sizeReference = e.getComponent().getWidth();
				System.out.println("TEST");
			}
			if (getOrientation() == JSplitPane.VERTICAL_SPLIT && getLeftComponent().getHeight() != sizeReference) {
				defaultDividerRatio = (double) getLeftComponent().getHeight()
						/ (getLeftComponent().getHeight() + getRightComponent().getHeight());
				sizeReference = e.getComponent().getHeight();
			}
			if (getOrientation() == JSplitPane.HORIZONTAL_SPLIT)
				System.out.println(defaultDividerRatio);

		}

	}

}
