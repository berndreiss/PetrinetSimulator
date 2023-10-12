package view;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.graphstream.ui.swing_viewer.ViewPanel;

import control.MainController;
import control.PetrinetController;

public class PetrinetPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private ResizableSplitPane graphSplitPane;

	private PetrinetController controller;

	public PetrinetPanel(MainController mainController) {
		this(mainController, null, false);
	}

	public PetrinetPanel(MainController mainController, File file) {
		this(mainController, file, false);
	}

	public PetrinetPanel(MainController mainController, File file, boolean headless) {

		this.controller = new PetrinetController(file, headless);

		setLayout(new BorderLayout());

		if (!headless) {
			ViewPanel left = controller.getPetrinetViewPanel();
			ViewPanel right = controller.getReachabilityViewPanel();
			graphSplitPane = new ResizableSplitPane(mainController.getFrame(), JSplitPane.HORIZONTAL_SPLIT, left, right);
			add(graphSplitPane, BorderLayout.CENTER);
		} 
	}


	public void repaintGraphs(int i) {
		if (i == 0) {
			graphSplitPane.getLeftComponent().repaint();
			return;
		}
		if (i == 1) {
			graphSplitPane.getRightComponent().repaint();
			return;
		}
		graphSplitPane.getLeftComponent().repaint();
		graphSplitPane.getRightComponent().repaint();

	}

	public JSplitPane getGraphPane() {
		return graphSplitPane;
	}
	


	public void incrementPlace() {
		String markedPlace = controller.getPetrinetGraph().getMarkedNode();

		if (markedPlace == null)
			return;

		controller.incrementPlace(markedPlace);

	}

	public void decrementPlace() {

		String markedPlace = controller.getPetrinetGraph().getMarkedNode();

		if (markedPlace == null)
			return;

		controller.decrementPlace(markedPlace);


	}

	public void resetPetrinet() {
		controller.resetPetrinet();
	}

	public PetrinetController getController() {
		return controller;
	}

	public String[] analyse() {
		return controller.analyse();
		
	}
	
	public void reloadFile() {
		controller.reload();
	}
	
	public File getCurrentFile() {
		return controller.getCurrentFile();
	}

}
