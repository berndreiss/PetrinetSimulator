package view;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.graphstream.ui.swing_viewer.ViewPanel;

public class GraphSplitPane extends JSplitPane {

	private JPanel petrinetPanel;
	private JPanel reachabilityPanel;
	public GraphSplitPane(JFrame mainFrame, JPanel petrinetPanel, JPanel reachabilityPanel) {
		super(JSplitPane.HORIZONTAL_SPLIT, petrinetPanel, reachabilityPanel);
		this.petrinetPanel = petrinetPanel;
		this.reachabilityPanel = reachabilityPanel;
		resetSize(mainFrame);
	}
	
	public void resetSize(JFrame mainFrame) {
		Dimension size = new Dimension((int) (mainFrame.getWidth()/2-10), (int) (mainFrame.getHeight()*0.8));
		petrinetPanel.setPreferredSize(size);
		reachabilityPanel.setPreferredSize(size);
		revalidate();

	}
}
