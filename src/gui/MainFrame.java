package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import control.MainController;
import control.PetrinetMenuController;
import control.PetrinetPanel;

/**
 * <p>
 * This is the main frame of the program.
 * </p>
 * <p>
 * In the {@link BorderLayout} there is a vertical {@link ResizableSplitPane} in
 * the CENTER containing a {@link JTextArea} in the bottom and a
 * {@link JTabbedPane} in the top half. In the SOUTH it holds a {@link JLabel}
 * representing the status of the program. In the NORTH it holds a
 * {@link PetrinetToolbar} that can be moved and reattached to the EAST or WEST.
 * Additionally the menu bar is occupied by an implementation of the
 * {@link PetrinetMenuController}.
 * </p>
 */
public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	/** Represents the default height of the graph relative to the split pane. */
	public static final double SPLIT_PANE_DEFAULT_RATIO = 0.8;

	/**
	 * Represents the default width of the left half of the graph split pane (see
	 * {@link GraphStreamPetrinetPanel}).
	 */
	public static final double GRAPH_SPLIT_PANE_DEFAULT_RATIO = 0.5;

	/** split pane containing tabbed pane and text area -> is placed in CENTER */
	private ResizableSplitPane splitPane;

	/** tabbed pane containing graphs */
	private JTabbedPane tabbedPane;

	/** scroll pane containing the text area */
	private JScrollPane scrollPane;

	/** text area where text can be printed to */
	private JTextArea textArea;

	/** status label -> is placed in SOUTH */
	private JLabel statusLabel;

	/** toolbar -> is placed in NORTH */
	private PetrinetToolbar toolbar;

	/** controller managing user interactions */
	private MainController controller;

	/**
	 * Instantiates a new main frame.
	 *
	 * @param title title being shown in the title bar
	 */
	public MainFrame(String title) {
		super(title);

		// change look and feel to Nimbus
		changeLookAndFeel();

		setLayout(new BorderLayout());

		tabbedPane = new JTabbedPane();

		// instantiate split pane, add tabbed pane, instantiate other components, add
		// them and add the split pane to the frames CENTER
		setSplitPane();

		statusLabel = new JLabel();
		add(statusLabel, BorderLayout.SOUTH);

		// controller is instantiated after the status label so that it can set it in
		// the constructor
		controller = new MainController(this);

		// menu and toolbar are instantiated after the controller so that an instance
		// can be passed to the constructor
		JMenuBar menuBar = new PetrinetMenu(controller);
		setJMenuBar(menuBar);

		toolbar = new PetrinetToolbar(controller, this);
		add(toolbar, BorderLayout.NORTH);
		//make toolbar non detachable
		toolbar.setFloatable(false);
		
		//open up an example (needs to be done after toolbar has been initialized)
		controller.openExample();
		
		// set up the frame and show it
		setLocationRelativeTo(null);
		double heightPerc = 0.7;
		double aspectRatio = 16.0 / 10.0;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int h = (int) (screenSize.height * heightPerc);
		int w = (int) (h * aspectRatio);
		setBounds((screenSize.width - w) / 2, (screenSize.height - h) / 2, w, h);
		this.setMinimumSize(new Dimension(1250, 800));// min size so that all buttons are shown correctly
		this.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {

				for (Component comp : tabbedPane.getComponents()) {
					
					PetrinetPanel panel = (PetrinetPanel) comp;
					if (panel != null
							&& panel.getPetrinetViewerController().getFileChanged())
						if (controller.saveDialog(panel.getPetrinetViewerController()))
							return;
				}
				System.exit(0);
			}

		});
		this.setVisible(true);

	}

	// removes the split pane if it has been instantiated and adds tabbed pane and
	// text area (inside scroll pane to it -> needed when look and feel is changed
	// because mouse events on the divider are not registered anymore otherwise
	private void setSplitPane() {

		// get text that is already in text area if it has been instantiated
		String oldText = textArea == null ? null : textArea.getText();

		// variable for getting old ratio of the divider of the split pane if it has
		// been instantiated -> since we are reinstantiating it, it would be set to
		// SPLIT_PANE_DEFAULT_RATIO otherwise
		Double oldDividerRatio = null;

		// if split pane has been instantiated remove it and get divider ratio
		if (splitPane != null) {
			remove(splitPane);
			oldDividerRatio = splitPane.getDividerRatio();
		}
		// create new text area and set font to monospaced so that all characters have
		// the same fixed length, clear text if already instantiated
		if (textArea == null) {
			textArea = new JTextArea();
			textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		} else
			clearTextArea();

		// create scroll pane and set it to auto scroll
		scrollPane = new JScrollPane(textArea);
		scrollPane.setAutoscrolls(true);

		// instantiate split pane add it to the frame and set the divider ratio
		splitPane = new ResizableSplitPane(this, JSplitPane.VERTICAL_SPLIT, tabbedPane, scrollPane);
		add(splitPane, BorderLayout.CENTER);
		splitPane.setDividerRatio(oldDividerRatio == null ? SPLIT_PANE_DEFAULT_RATIO : oldDividerRatio);

		// wait for all components to be shown and print old text so the scrollbar jumps
		// to the bottom -> comes into play when look and feel is changed and the
		// textarea was not empty
		SwingUtilities.invokeLater(() -> print(oldText));
	}

	/**
	 * Returns the split pane in the center of the frame.
	 *
	 * @return the split pane
	 */
	public ResizableSplitPane getSplitPane() {
		return splitPane;
	}

	/**
	 * Prints to the text area in the bottom half of the split pane.
	 *
	 * @param text text to be printed
	 */
	public void print(String text) {
		textArea.append(text);
	}

	/**
	 * Clears all text from the text area in the bottom half of the split pane.
	 */
	public void clearTextArea() {
		textArea.setText("");
	}

	/**
	 * Sets the status label.
	 *
	 * @param status new status label to be set
	 */
	public void setStatusLabel(String status) {
		statusLabel.setText(status);
	}

	/**
	 * Gets the tabbed pane in the upper half of the split pane.
	 *
	 * @return the tabbed pane
	 */
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	/**
	 * Gets the toolbar.
	 *
	 * @return the toolbar
	 */
	public PetrinetToolbar getToolbar() {
		return toolbar;
	}

	/**
	 * Changes the look and feel (LAF) of the program. By default the LAF is set to
	 * Nimbus but it can be switched to Metal. Whenever the method is called it
	 * switches between the two.
	 */
	public void changeLookAndFeel() {

		// get the look and feel and set string to the other
		LookAndFeel laf = UIManager.getLookAndFeel();
		String lafString = laf.getName().equals("Nimbus") ? "Metal" : "Nimbus";

		// loop through all look and feels of the UIManager: if Nimbus is not installed
		// the default look and feel will be used -> avoids calling look and feel that
		// is not installed
		for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
			if (lafString.equals(info.getName())) {
				try {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
				break;
			}
		}

		// update UI components -> needs to be done before split pane is readded,
		// otherwise divider does not register mouse events
		SwingUtilities.updateComponentTreeUI(this);
		revalidate();
		repaint();

		// check whether toolbar has been instantiated and reset it to previous state
		if (toolbar != null) {
			// get last docking place being either NORTH, EAST or SOUTH
			String dockingPlace = toolbar.getDockingPlace();

			// remove it and readd it, set orientation if necessary
			remove(toolbar);
			toolbar = new PetrinetToolbar(controller, this);
			//make toolbar non detachable
			toolbar.setFloatable(false);

			if (dockingPlace == null)
				add(toolbar, BorderLayout.NORTH);
			else {
				if (!dockingPlace.equals(BorderLayout.NORTH))
					toolbar.setOrientation(JToolBar.VERTICAL);
				add(toolbar, dockingPlace);
			}

			// reset buttons to previous state
			toolbar.setToolbarTo(controller.getCurrentPanel(), controller.getLayoutType());
		}
		// check whether splitPane has been instantiate and reset it if not -> otherwise
		// the divider mouse events do not register for some reason
		if (splitPane != null)
			setSplitPane();

	}

}
