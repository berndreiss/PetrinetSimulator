package view;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.graphstream.ui.swing_viewer.ViewPanel;

public class GraphSplitPane extends JSplitPane {

	private JPanel petrinetPanel;
	private JPanel reachabilityPanel;
	private double defaultDividerRatio = 0.5;
	private double currentDividerRatio = 0.5;
	

	public GraphSplitPane(JFrame parent, int splitOrientation, JPanel petrinetPanel, JPanel reachabilityPanel) {
		super(splitOrientation, petrinetPanel, reachabilityPanel);
		this.defaultDividerRatio = 0.5;
		this.currentDividerRatio = this.defaultDividerRatio;
		this.setResizeWeight(defaultDividerRatio);
		this.petrinetPanel = petrinetPanel;
		this.reachabilityPanel = reachabilityPanel;
		parent.addComponentListener(new Di);
	}
	
	public void resetSize(JFrame mainFrame) {
		Dimension size = new Dimension((int) (mainFrame.getWidth()/2-10), (int) (mainFrame.getHeight()*0.8));
		petrinetPanel.setPreferredSize(size);
		reachabilityPanel.setPreferredSize(size);
		revalidate();

	}
	
	public void setDividerRatio(double ratio) {
		this.dividerRatio = ratio;
	}
	
	public double getDividerRatio() {
		return dividerRatio;
	}
}
