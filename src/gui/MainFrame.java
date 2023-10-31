package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import control.MainController;

// TODO: Auto-generated Javadoc
/**
 * This is the main frame of the program. In the {@link BorderLayout} there is a
 * vertical {@link ResizableSplitPane} in the CENTER containing a
 * {@link JTextArea} in the bottom and a {@link JTabbedPane} in the top half. In
 * the SOUTH it holds a {@link JLabel} representing the status of the program.
 * In the NORTH it holds a {@link PetrinetToolbar} that can be moved and
 * reattached to the EAST or WEST. Additionally the menu bar is occupied by an
 * instance of {@link PetrinetMenu}.
 */
public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	/** Represents the height of the graph relative to the frame. */
	static final double GRAPH_PERCENT = 0.5;

	/** Represents the default height of the graph relative to the split pane. */
	public static final double SPLIT_PANE_DEFAULT_RATIO = 0.8;

	/**
	 * Represents the default width of the left half of the graph split pane (see
	 * {@link PetrinetPanel}).
	 */
	public static final double GRAPH_SPLIT_PANE_DEFAULT_RATIO = 0.5;

	// split pane containing tabbed pane and text area -> is placed in CENTER
	private ResizableSplitPane splitPane;

	// tabbed pane containing graphs
	private JTabbedPane tabbedPane;

	// scroll pane containing the text area
	private JScrollPane scrollPane;
	private JTextArea textArea;

	// status label -> is placed in SOUTH
	private JLabel statusLabel;

	// toolbar -> is placed in NORTH
	private PetrinetToolbar toolbar;

	// controller managing user interactions
	private MainController controller;

	/**
	 * Instantiates a new main frame.
	 *
	 * @param title Title being shown in the title bar.
	 */
	public MainFrame(String title) {
		super(title);

		// change look and feel to Nimbus
		changeLookAndFeel();

		setLayout(new BorderLayout());

		textArea = new JTextArea();

		// set font monospaced so that all characters have the same fixed length
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

		scrollPane = new JScrollPane(textArea);

		tabbedPane = new JTabbedPane();

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

		toolbar = new PetrinetToolbar(controller);
		add(toolbar, BorderLayout.NORTH);

		// set up the frame and show it
		setLocationRelativeTo(null);
		double heightPerc = 0.7;
		double aspectRatio = 16.0 / 10.0;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int h = (int) (screenSize.height * heightPerc);
		int w = (int) (h * aspectRatio);
		setBounds((screenSize.width - w) / 2, (screenSize.height - h) / 2, w, h);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

		// TODO find workaround for certain look and feels causing JSplitPane dividers
		// not registering MouseEvents (remove feature until resolved)

	}

	//removes the split pane if it has been instantiated and adds 
	private void setSplitPane() {

		if (splitPane != null)
			remove(splitPane);
		
		splitPane = new ResizableSplitPane(this, JSplitPane.VERTICAL_SPLIT, tabbedPane, scrollPane);
		add(splitPane, BorderLayout.CENTER);
		splitPane.setDefaultRatio(SPLIT_PANE_DEFAULT_RATIO);

	}

	/**
	 * Returns the split pane in the center of the frame.
	 *
	 * @return the split pane.
	 */
	public ResizableSplitPane getSplitPane() {
		return splitPane;
	}

	/**
	 * Prints to the text area in the bottom half of the split pane.
	 *
	 * @param text Text to be printed.
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
	 * @param status New status label to be set.
	 */
	public void setStatusLabel(String status) {
		statusLabel.setText(status);
	}

	/**
	 * Gets the tabbed pane in the upper half of the split pane.
	 *
	 * @return the tabbed pane.
	 */
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	/**
	 * Gets the toolbar.
	 *
	 * @return the toolbar.
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

		
		// update UI components
		SwingUtilities.updateComponentTreeUI(this);
		revalidate();
		repaint();

		//check whethers toolbar has been instantiated
		if (toolbar == null)
			return;
		
		// reset the toolbar buttons
		toolbar.setToolbarTo(controller.getCurrentPanel(), controller.getLayoutType());

		//check whether splitPane has been instantiated
		if (splitPane == null)
			return;

		
		//reset the split pane -> otherwise the divider mouse events do not register for some reason
		setSplitPane(); 

	}

}
