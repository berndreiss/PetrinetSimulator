package view;

import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.util.HashSet;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.graphstream.ui.swing_viewer.ViewPanel;

import ReachabilityGraphLayout.LayoutTypes;
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
	private MainController mainController;

	public PetrinetPanel(MainController mainController, LayoutTypes layoutType) throws PetrinetException {
		this(mainController, null, layoutType, false);
	}

	public PetrinetPanel(MainController mainController, File file, LayoutTypes layoutType) throws PetrinetException {
		this(mainController, file, layoutType, false);
	}

	public PetrinetPanel(MainController mainController, File file, LayoutTypes layoutType, boolean headless)
			throws PetrinetException {

		this.controller = new PetrinetController(file, headless);
		this.mainController = mainController;
		
		
		controller.setToolbarToggleListener(mainController);

		this.petrinetViewPanel = GraphStreamView.initGraphStreamView(controller.getPetrinetGraph(), controller,
				mainController.getFrame(), null);

		JPanel petrinetPanel = new JPanel();
		petrinetPanel.setLayout(new BorderLayout());
		petrinetPanel.add(petrinetViewPanel, BorderLayout.CENTER);

		
		setLayout(new BorderLayout());

		if (!headless) {

			controller.getReachabilityGraph().setLayoutType(layoutType);
			graphSplitPane = new ResizableSplitPane(mainController.getFrame(), JSplitPane.HORIZONTAL_SPLIT,
					petrinetPanel, new JPanel());
			add(graphSplitPane, BorderLayout.CENTER);
			setReachabilityPanel(layoutType);
			mainController.getFrame().addComponentListener(new ComponentAdapter() {
				
				
				@Override
				public void componentResized(ComponentEvent e) {
					GraphStreamView.adjustArrowHeads();
				}				
			});

		}

	}

	private void setReachabilityPanel(LayoutTypes layoutType) {
//		JPanel panel = (JPanel) graphSplitPane.getRightComponent();
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		reachabilityViewPanel = GraphStreamView.initGraphStreamView(controller.getReachabilityGraph(), controller, panel, layoutType);
		panel.add(reachabilityViewPanel, BorderLayout.CENTER);
		graphSplitPane.setVisible(false);
		graphSplitPane.remove(graphSplitPane.getRightComponent());

		controller.resetReachabilityGraph();
		graphSplitPane.setRightComponent(panel);
graphSplitPane.setVisible(true);


	}
	public ResizableSplitPane getGraphSplitPane() {
		return graphSplitPane;
	}

	public void zoomInPetrinet() {
		zoomIn(petrinetViewPanel);
	}

	public void zoomOutPetrinet() {
		zoomOut(petrinetViewPanel);
	}

	private void zoomIn(ViewPanel viewPanel) {
		double zoom = viewPanel.getCamera().getViewPercent();

		if (zoom > 0.1)
			viewPanel.getCamera().setViewPercent(zoom - 0.1);

	}

	private void zoomOut(ViewPanel viewPanel) {
		double zoom = viewPanel.getCamera().getViewPercent();

		viewPanel.getCamera().setViewPercent(zoom + 0.1);
	}

	public void setLayoutType(LayoutTypes layoutType) {
		if (controller.getHeadless())
			return;

		controller.getReachabilityGraph().setLayoutType(layoutType);

		setReachabilityPanel(layoutType);
	}

	public PetrinetController getController() {
		return controller;
	}

	public boolean nodeMarked() {
		return controller.getPetrinetGraph().getMarkedNode() != null;
	}

	public void zoomInReachability() {
		zoomIn(reachabilityViewPanel);
	}

	public void zoomOutReachability() {
		zoomOut(reachabilityViewPanel);
	}

}
