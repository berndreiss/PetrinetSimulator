package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 * <p>
 * A subclass of {@link JSplitPane} but the divider stays in place even if the
 * parent container is resized.
 * </p>
 * <p>
 * The split pane is listening to the parent container and on resize resets the
 * divider. The split can be horizontal or vertical.
 * </p>
 */
public class ResizableSplitPane extends JSplitPane {

	private static final long serialVersionUID = 1L;

	/** ratio of the divider -> is updated when divider is moved */
	private double dividerRatio = 0.5;
	/** the parent container */
	private MainFrame parent;
	/**
	 * offset that is considered when toolbar is docked to EAST or WEST of parent
	 */
	private int toolbarOffSet;

	/**
	 * Instantiates a new resizable split pane.
	 *
	 * @param parent           The parent container.
	 * @param splitOrientation The split orientation.
	 * @param left             The left component to be set.
	 * @param right            The right component to be set.
	 */
	public ResizableSplitPane(MainFrame parent, int splitOrientation, Component left, Component right) {
		super(splitOrientation, left, right);
		this.parent = parent;
		if (parent != null)// safety check
			initialize();
	}

	// initialize the divider ratio and set the divider -> is called again when
	// divider needs be reset
	private void initialize() {

		toolbarOffSet = 0;

		PetrinetToolbar toolbar = parent.getToolbar();

		// if toolbar != null get docking place
		String dockingPlace = toolbar == null ? null : toolbar.getDockingPlace();

		// if docking place != null and toolbar docked to EAST or WEST set toolbarOffset
		if (dockingPlace != null && (dockingPlace.equals(BorderLayout.EAST) || dockingPlace.equals(BorderLayout.WEST)))
			toolbarOffSet += parent.getToolbar().getWidth();

		Component left = getLeftComponent();
		Component right = getRightComponent();

		// set left and right component to take up half the width if split horizontally
		// -> right component takes up too much space otherwise
		// for some reason 7 is the magic number that when subtracted from either half
		// lets the divider sit perfectly in the middle (getDividerSize() did not do the
		// trick...
		if (getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
			Dimension preferredSize = new Dimension((int) (parent.getWidth() / 2 - 7 - toolbarOffSet / 2), 0);
			left.setPreferredSize(preferredSize);
			right.setPreferredSize(preferredSize);
		}

		// set minimum size to (0,0) so divider can be set all the way to the left /
		// right
		Dimension zeroSize = new Dimension(0, 0);
		left.setMinimumSize(zeroSize);
		right.setMinimumSize(zeroSize);

		// reset dividerLocation on parent resized
		parent.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {

				setDividerLocation(dividerRatio);

			}

		});

		// wait for all pending Swing events
		// to be processed, so the size of
		// left and right components is set
		// correctly and it doesn't collide
		// to the left
		SwingUtilities.invokeLater(() -> setDividerLocation(dividerRatio));

		// reset the divider ratio whenever divider is moved
		BasicSplitPaneUI ui = (BasicSplitPaneUI) getUI();
		ui.getDivider().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent me) {
				// CAUTION: mouse events on the divider are not registered after the look and
				// feel has been changed -> split pane needs to be reinstantiated for this to
				// work!
				if (getOrientation() == JSplitPane.HORIZONTAL_SPLIT)
					dividerRatio = (double) getDividerLocation() / (getWidth() - getDividerSize());
				else
					dividerRatio = (double) getDividerLocation() / (getHeight() - getDividerSize());
			}
		});

	}

	/**
	 * Sets the right component.
	 *
	 * @param comp The new right component.
	 */
	@Override
	public void setRightComponent(Component comp) {
		super.setRightComponent(comp);
		if (parent != null)// safety check
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
		if (parent != null)// safety check
			initialize();
	}

	/**
	 * Sets the divider ratio.
	 *
	 * @param ratio The new divider ratio.
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

	/**
	 * Get the divider ratio.
	 * 
	 * @return the divider ratio
	 */
	public Double getDividerRatio() {
		return dividerRatio;
	}

}
