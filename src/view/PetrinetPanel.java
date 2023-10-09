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
			ViewPanel left = initGraphStreamView(controller.getPetrinetGraph());
			ViewPanel right = initGraphStreamView(controller.getReachabilityGraph());
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

		controller.getPetrinet().setCurrenStateOriginalState();
		controller.resetPetrinet();
	}

	public void decrementPlace() {

		String markedPlace = controller.getPetrinetGraph().getMarkedNode();

		if (markedPlace == null)
			return;

		controller.decrementPlace(markedPlace);

		controller.getPetrinet().setCurrenStateOriginalState();
		controller.resetPetrinet();
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

	public ViewPanel initGraphStreamView(Graph graph) {// TODO change to private

		// Erzeuge Viewer mit passendem Threading-Model für Zusammenspiel mit
		// Swing
		SwingViewer viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

		// bessere Darstellungsqualität und Antialiasing (Kantenglättung) aktivieren
		// HINWEIS: Damit diese Attribute eine Auswirkung haben, müssen sie NACH
		// Erzeugung des SwingViewer gesetzt werden
		graph.setAttribute("ui.quality");
		graph.setAttribute("ui.antialias");

		// Das Auto-Layout für den Graphen kann aktiviert oder deaktiviert
		// werden.
		// Auto-Layout deaktivieren: Die explizit hinzugefügten Koordinaten
		// werden genutzt (wie in DemoGraph).
		// Achtung: Falls keine Koordinaten definiert wurden, liegen alle Knoten
		// übereinander.)
		if (graph instanceof PetrinetGraph)
			viewer.disableAutoLayout();
		else
			viewer.enableAutoLayout();
		// Auto-Layout aktivieren: GraphStream generiert ein möglichst
		// übersichtliches Layout
		// (und ignoriert hinzugefügte Koordinaten)
		// viewer.enableAutoLayout();

		// Eine DefaultView zum Viewer hinzufügen, die jedoch nicht automatisch
		// in einen JFrame integriert werden soll (daher Parameter "false"). Das
		// zurückgelieferte ViewPanel ist eine Unterklasse von JPanel, so dass
		// es später einfach in unsere Swing-GUI integriert werden kann. Es gilt
		// folgende Vererbungshierarchie:
		// DefaultView extends ViewPanel extends JPanel implements View
		// Hinweis:
		// In den Tutorials wird "View" als Rückgabetyp angegeben, es ist
		// aber ein "ViewPanel" (und somit auch ein JPanel).
		ViewPanel viewPanel = (ViewPanel) viewer.addDefaultView(false);

		// Neue ViewerPipe erzeugen, um über Ereignisse des Viewer informiert
		// werden zu können
		ViewerPipe viewerPipe = viewer.newViewerPipe();

		if (graph instanceof PetrinetGraph) {
			// Neuen ClickListener erzeugen, der als ViewerListener auf Ereignisse
			// der View reagieren kann
			ClickListener clickListener = new ClickListener(controller);

			// clickListener als ViewerListener bei der viewerPipe anmelden
			viewerPipe.addViewerListener(clickListener);

		}

		if (graph instanceof ReachabilityGraph) {
			viewerPipe.addViewerListener(new ViewerListener() {

				@Override
				public void viewClosed(String viewName) {
				}

				@Override
				public void mouseOver(String id) {
				}

				@Override
				public void mouseLeft(String id) {
				}

				@Override
				public void buttonReleased(String id) {
				}

				@Override
				public void buttonPushed(String id) {
					controller.reachabilityNodeClicked(id);
				}
			});

		}

		EnumSet<InteractiveElement> enumSet = EnumSet.of(InteractiveElement.NODE);

		viewPanel.addMouseListener(new MouseAdapter() {

			private GraphicElement element;

			@Override
			public void mousePressed(MouseEvent me) {
				element = viewPanel.findGraphicElementAt(enumSet, me.getX(), me.getY());
				viewerPipe.pump();
			}

			@Override
			public void mouseReleased(MouseEvent me) {

				if (element != null) {
					String id = element.getId();
					PetrinetElement p = controller.getPetrinet().getPetrinetElement(id);
					double x = element.getX();
					double y = element.getY();
					if (p != null)
						if (p.getX() != x || p.getY() != y) {
							p.setX(x);
							p.setY(y);
						}
					element = null;
				}

				viewerPipe.pump();
			}
		});
		// Zoom per Mausrad ermöglichen
		viewPanel.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				double zoomLevel = viewPanel.getCamera().getViewPercent();
				if (e.getWheelRotation() == -1) {
					zoomLevel -= 0.1;
					if (zoomLevel < 0.1) {
						zoomLevel = 0.1;
					}
				}
				if (e.getWheelRotation() == 1) {
					zoomLevel += 0.1;
				}
				viewPanel.getCamera().setViewPercent(zoomLevel);
			}
		});

		return viewPanel;

	}

	public String getResult() {

		return formatStringForAnalysesOutput(controller.getResults());
	}

}
