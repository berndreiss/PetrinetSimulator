package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicSplitPaneUI;

// TODO: Auto-generated Javadoc
/**
 * This is a subclass of {@link JSplitPane} but the divider stays in place even
 * if the parent container is resized, hence the name resizable split pane. The
 * split pane is listening to the parent container and on resize resets the
 * divider. The split can be horizontal or vertical.
 */
public class ResizableSplitPane extends JSplitPane {

	private static final long serialVersionUID = 1L;

	/** ratio of the divider -> is updated when divider is moved*/
	private double dividerRatio = 0.5;
	/** the parent container*/
	private MainFrame parent;
	/** offset that is considered when toolbar is docked to EAST or WEST of parent*/
	private int toolbarOffSet;

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
		Dimension preferredSize = new Dimension((int) (parent.getWidth() / 2 - 7 - toolbarOffSet / 2),
				(int) (parent.getHeight() * MainFrame.GRAPH_PERCENT));

		Dimension zeroSize = new Dimension(0, 0);

		Component left = getLeftComponent();
		Component right = getRightComponent();

		left.setPreferredSize(preferredSize);
		left.setMinimumSize(zeroSize);

		right.setPreferredSize(preferredSize);
		right.setMinimumSize(zeroSize);

		parent.addComponentListener(new FrameResizeAdapter());

		// wait for all pending Swing events
		// to be processed, so the size of
		// left and right components is set
		// correctly and it doesn't collide
		// to the left
		SwingUtilities.invokeLater(() -> setDividerLocation(dividerRatio));

		BasicSplitPaneUI ui = (BasicSplitPaneUI) getUI();
		ui.getDivider().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent me) {
				// CAUTION: mouse events on the divider are not registered after the look and
				// feel has been changed -> components need to be reinstantiated for this to
				// work!
				if (getOrientation() == JSplitPane.HORIZONTAL_SPLIT)
					dividerRatio = (double) getDividerLocation() / (getWidth() - getDividerSize());
				else
					dividerRatio = (double) getDividerLocation() / (getHeight() - getDividerSize());
			}
		});

		this.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, e -> {

		});
	}

	/**
	 * Sets the divider ratio to a default ratio.
	 *
	 * @param ratio the new divider ratio
	 */
	public void setDividerRatio(double ratio) {
		this.dividerRatio = ratio;
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

			setDividerLocation(dividerRatio);

		}

	}

	/**
	 * 
	 * @return
	 */
	public Double getDividerRatio() {
		return dividerRatio;
	}

}
