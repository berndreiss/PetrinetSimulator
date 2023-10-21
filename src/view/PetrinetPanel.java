package view;

import java.awt.BorderLayout;
import java.io.File;
import java.util.HashSet;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.graphstream.ui.swing_viewer.ViewPanel;

import control.MainController;
import control.PetrinetController;
import control.PetrinetException;
import datamodel.PetrinetState;

public class PetrinetPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private ResizableSplitPane graphSplitPane;
	
	private ViewPanel petrinetViewPanel;
	private ViewPanel reachabilityViewPanel;


	private PetrinetController controller;

	public PetrinetPanel(MainController mainController) throws PetrinetException {
		this(mainController, null, false);
	}

	public PetrinetPanel(MainController mainController, File file) throws PetrinetException {
		this(mainController, file, false);
	}

	public PetrinetPanel(MainController mainController, File file, boolean headless) throws PetrinetException {

		this.controller = new PetrinetController(file, headless);

		controller.getEditor().setOnEditedListener(mainController);
		
		this.petrinetViewPanel = GraphStreamView.initGraphStreamView(controller.getPetrinetGraph(), controller, mainController.getFrame());
		this.reachabilityViewPanel = GraphStreamView.initGraphStreamView(controller.getReachabilityGraph(), controller, mainController.getFrame());

		setLayout(new BorderLayout());

		if (!headless) {
			graphSplitPane = new ResizableSplitPane(mainController.getFrame(), JSplitPane.HORIZONTAL_SPLIT, petrinetViewPanel, reachabilityViewPanel);
			add(graphSplitPane, BorderLayout.CENTER);
		} 

		
		

	}

	public ResizableSplitPane getGraphSplitPane() {
		return graphSplitPane;
	}
	
	public void zoomIn() {
		double zoom = petrinetViewPanel.getCamera().getViewPercent();
		
		if (zoom > 0.1)
			petrinetViewPanel.getCamera().setViewPercent(zoom-0.1);
	}

	public void zoomOut() {
		double zoom = petrinetViewPanel.getCamera().getViewPercent();
		
		petrinetViewPanel.getCamera().setViewPercent(zoom+0.1);
	}
//	public void repaintGraphs(int i) {
//		if (i == 0) {
//			graphSplitPane.getLeftComponent().repaint();
//			return;
//		}
//		if (i == 1) {
//			graphSplitPane.getRightComponent().repaint();
//			return;
//		}
//		graphSplitPane.getLeftComponent().repaint();
//		graphSplitPane.getRightComponent().repaint();
//
//	}

//	public JSplitPane getGraphPane() {
//		return graphSplitPane;
//	}
//	


//	public void incrementPlace() {
//		String markedPlace = controller.getPetrinetGraph().getMarkedNode();
//
//		if (markedPlace == null)
//			return;
//
//		controller.incrementPlace(markedPlace);
//
//	}

//	public void decrementPlace() {
//
//		String markedPlace = controller.getPetrinetGraph().getMarkedNode();
//
//		if (markedPlace == null)
//			return;
//
//		controller.decrementPlace(markedPlace);
//
//
//	}

//	public void resetPetrinet() {
//		controller.resetPetrinet();
//	}

	public PetrinetController getController() {
		return controller;
	}

	public boolean nodeMarked() {
		return controller.getPetrinetGraph().getMarkedNode() != null;
	}

//	public String[] analyse() {
//		return controller.analyse();
//		
//	}
	
//	public void reloadFile() {
//		controller.reload();
//	}
	
//	public File getCurrentFile() {
//		return controller.getCurrentFile();
//	}

}
