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
 * The Class MainFrame.
 */
public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	/** The Constant GRAPH_PERCENT. */
	static final double GRAPH_PERCENT = 0.5;

	/** The Constant SPLIT_PANE_DEFAULT_RATIO. */
	public static final double SPLIT_PANE_DEFAULT_RATIO = 0.8;
	
	/** The Constant GRAPH_SPLIT_PANE_DEFAULT_RATIO. */
	public static final double GRAPH_SPLIT_PANE_DEFAULT_RATIO = 0.5;

	private ResizableSplitPane splitPane;

	private JTabbedPane tabbedPane;
	private JScrollPane scrollPane;
	private JTextArea textArea;

	private JLabel statusLabel;

	private PetrinetToolbar toolbar;

	private MainController controller;

	
	/**
	 * Instantiates a new main frame.
	 *
	 * @param title the title
	 */
	public MainFrame(String title) {
		super(title);
		
		
		setLayout(new BorderLayout());

		textArea = new JTextArea();
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

		scrollPane = new JScrollPane(textArea);

		tabbedPane = new JTabbedPane();

		splitPane = new ResizableSplitPane(this, JSplitPane.VERTICAL_SPLIT);
		
		splitPane.setLeftComponent(tabbedPane);
		splitPane.setRightComponent(scrollPane);
		splitPane.setDefaultRatio(SPLIT_PANE_DEFAULT_RATIO);
		
		statusLabel = new JLabel();
		
		add(statusLabel, BorderLayout.SOUTH);

		controller = new MainController(this);

		JMenuBar menuBar = new PetrinetMenu(controller);
		
		toolbar = new PetrinetToolbar(controller);

		setJMenuBar(menuBar);

		add(toolbar, BorderLayout.NORTH);
		
		add(splitPane, BorderLayout.CENTER);

	    setLocationRelativeTo(null);

	    double heightPerc = 0.7; 
		double aspectRatio = 16.0 / 10.0; 
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int h = (int) (screenSize.height * heightPerc);
		int w = (int) (h * aspectRatio);
		setBounds((screenSize.width - w) / 2, (screenSize.height - h) / 2, w, h);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

		changeLookAndFeel();

	}

	/**
	 * Gets the split pane.
	 *
	 * @return the split pane
	 */
	public ResizableSplitPane getSplitPane() {
		return splitPane;
	}

	/**
	 * Prints the.
	 *
	 * @param s the s
	 */
	public void print(String s) {
		textArea.append(s);
	}

	/**
	 * Clear text area.
	 */
	public void clearTextArea() {
		textArea.setText("");
	}

	/**
	 * Sets the status label.
	 *
	 * @param status the new status label
	 */
	public void setStatusLabel(String status) {
		statusLabel.setText(status);
	}
	
	
	/**
	 * Gets the tabbed pane.
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
	 * Change look and feel.
	 */
	public void changeLookAndFeel() {

		LookAndFeel laf = UIManager.getLookAndFeel();

		String lafString = laf.getName().equals("Nimbus") ? "Metal" : "Nimbus";
		
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
		SwingUtilities.updateComponentTreeUI(this);
		revalidate();
		repaint();

		toolbar.setToolbarTo(controller.getCurrentPanel(), controller.getLayoutType());
		controller.onSetDefault();
}
	
}
