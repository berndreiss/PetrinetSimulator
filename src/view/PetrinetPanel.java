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
		controller.onFileOpen(getCurrentFile());
	}
	
	public File getCurrentFile() {
		return controller.getCurrentFile();
	}

}
