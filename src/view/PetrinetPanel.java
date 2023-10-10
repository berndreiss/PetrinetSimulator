package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.EnumSet;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import org.graphstream.graph.Graph;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.util.InteractiveElement;

import control.MainController;
import control.PetrinetController;
import datamodel.Petrinet;
import datamodel.PetrinetElement;

public class PetrinetPanel extends JPanel {

	private MainController mainController;

	private ResizableSplitPane graphSplitPane;

	private PetrinetController controller;

	public PetrinetPanel(MainController mainController) {
		this(mainController, null, false);
	}

	public PetrinetPanel(MainController mainController, File file) {
		this(mainController, file, false);
	}

	public PetrinetPanel(MainController mainController, File file, boolean headless) {
		this.mainController = mainController;

		this.controller = new PetrinetController(this, file, headless);

		setLayout(new BorderLayout());

		if (!headless) {
			ViewPanel left = controller.getPetrinetViewPanel();
			ViewPanel right = controller.getReachabilityViewPanel();
			graphSplitPane = new ResizableSplitPane(mainController.getFrame(), JSplitPane.HORIZONTAL_SPLIT, left, right);
			add(graphSplitPane, BorderLayout.CENTER);
		} else {
			JPanel dummyPanel = new JPanel();
			dummyPanel.setPreferredSize(new Dimension(mainController.getFrame().getWidth(), (int) (mainController.getFrame().getWidth()*MainFrame.GRAPH_PERCENT)));
			add(dummyPanel, BorderLayout.CENTER);
		}
//		updateGraphSplitPane(controller);

//		splitPane.setGetComponentInterface(new GetComponentInterface() {
//
//			@Override
//			public Component getRightComponent() {
//				return scrollPane;
//			}
//
//			@Override
//			public Component getLeftComponent() {
//				return graphSplitPane;
//			}
//		});

	}

	public void updateGraphSplitPane() {

//		ResizableSplitPane splitPane = mainController.getFrame().getSplitPane();
//
//		// Füge das JPanel zum Haupt-Frame hinzu
//		if (graphSplitPane != null) {
//			splitPane.remove(graphSplitPane);
//		}
//
//
////		viewerPipePetrinet.pump();
//
//		splitPane.revalidate();
//		this.revalidate();

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

	public static String formatStringForAnalysesOutput(String[] strings) {

		if (strings.length != 3) {
			if (strings.length > 3)
				System.out.println("String-Array is too long.");
			else
				System.out.println("String-Array is too short.");
			return null;
		}
		StringBuilder sb = new StringBuilder();

		String format = "%-50s | %-10s | %-50s\n";

		return String.format(format, strings[0], " " + strings[1], " " + strings[2]);
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

	public void analyse() {
		controller.analyse();
	}

	public PetrinetController getController() {
		return controller;
	}

	public String getResult() {

		return formatStringForAnalysesOutput(controller.getResults());
	}

}
