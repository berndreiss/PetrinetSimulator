package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.EnumSet;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.graphstream.graph.Graph;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.util.InteractiveElement;

import control.MainController;
import control.PetrinetController;
import core.Petrinet;
import core.PetrinetElement;
import core.PetrinetGraph;
import core.ReachabilityGraph;
import exceptions.PetrinetException;
import reachabilityGraphLayout.LayoutTypes;

// TODO: Auto-generated Javadoc
/**
 * The Class PetrinetPanel.
 */
public class PetrinetPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private ResizableSplitPane graphSplitPane;

	private JPanel petrinetViewPanel;
	private JPanel reachabilityViewPanel;
	private ViewPanel petrinetView;
	private ViewPanel reachabilityView;

	private PetrinetController controller;

	/**
	 * Instantiates a new petrinet panel.
	 *
	 * @param mainController the main controller
	 * @param file           the file
	 * @param layoutType     the layout type
	 * @throws PetrinetException the petrinet exception
	 */
	public PetrinetPanel(MainController mainController, File file, LayoutTypes layoutType) throws PetrinetException {
		this(mainController, file, layoutType, false);
	}

	/**
	 * Instantiates a new petrinet panel.
	 *
	 * @param mainController the main controller
	 * @param file           the file
	 * @param layoutType     the layout type
	 * @param headless       the headless
	 * @throws PetrinetException the petrinet exception
	 */
	private PetrinetPanel(MainController mainController, File file, LayoutTypes layoutType, boolean headless)
			throws PetrinetException {

		this.controller = new PetrinetController(file, headless);

		controller.setToolbarToggleListener(mainController);

		this.petrinetView = initGraphStreamView(controller.getPetrinetGraph());

		petrinetViewPanel = new JPanel();
		petrinetViewPanel.setLayout(new BorderLayout());
		petrinetViewPanel.add(petrinetView, BorderLayout.CENTER);

		reachabilityViewPanel = new JPanel();
		setLayout(new BorderLayout());

		if (!headless) {

			controller.getReachabilityGraph().setLayoutType(layoutType);
			graphSplitPane = new ResizableSplitPane(mainController.getFrame(), JSplitPane.HORIZONTAL_SPLIT,
					petrinetViewPanel, reachabilityViewPanel);

			add(graphSplitPane, BorderLayout.CENTER);
			setReachabilityPanel(layoutType);
			mainController.getFrame().addComponentListener(new ComponentAdapter() {

				@Override
				public void componentResized(ComponentEvent e) {
					adjustArrowHeads();
				}
			});

		}

	}

	private void setReachabilityPanel(LayoutTypes layoutType) {

		JPanel panel = (JPanel) graphSplitPane.getRightComponent();

		if (panel.getComponentCount() != 0) {
			panel.remove(reachabilityView);
		}

		panel.setLayout(new BorderLayout());

		reachabilityView = initGraphStreamView(controller.getReachabilityGraph());
		panel.add(reachabilityView, BorderLayout.CENTER);

		controller.resetReachabilityGraph();

	}

	/**
	 * Gets the graph split pane.
	 *
	 * @return the graph split pane
	 */
	public ResizableSplitPane getGraphSplitPane() {
		return graphSplitPane;
	}

	/**
	 * Zoom in petrinet.
	 */
	public void zoomInPetrinet() {
		zoomIn(petrinetView);
	}

	/**
	 * Zoom out petrinet.
	 */
	public void zoomOutPetrinet() {
		zoomOut(petrinetView);
	}

	/**
	 * Zoom in reachability.
	 */
	public void zoomInReachability() {
		if (controller.getReachabilityGraph().hasLessThanTwoNodes())// disable zoom if there's only one node in graph,
																	// since there are problems with nodes disappearing
																	// and a zoom on one node does not make any
																	// difference
			return;
		zoomIn(reachabilityView);
	}

	/**
	 * Zoom out reachability.
	 */
	public void zoomOutReachability() {
		if (controller.getReachabilityGraph().hasLessThanTwoNodes())// disable zoom if there's only one node in graph,
																	// since there are problems with nodes disappearing
																	// and a zoom on one node does not make any
																	// difference
			return;
		zoomOut(reachabilityView);
	}

	public void resetReachabilityZoom() {

		double zoom = reachabilityView.getCamera().getViewPercent();

		if (zoom != 1.0)
			reachabilityView.getCamera().resetView();
	}

	private void zoomIn(ViewPanel viewPanel) {

		if (viewPanel == null)
			return;

		double zoom = viewPanel.getCamera().getViewPercent();

		if (zoom > 0.1) {
			viewPanel.getCamera().setViewPercent(zoom - 0.1);
			adjustArrowHeads();
		}
	}

	private void zoomOut(ViewPanel viewPanel) {
		if (viewPanel == null)
			return;

		double zoom = viewPanel.getCamera().getViewPercent();

		viewPanel.getCamera().setViewPercent(zoom + 0.1);
		adjustArrowHeads();
	}

	/**
	 * Sets the layout type.
	 *
	 * @param layoutType the new layout type
	 */
	public void setLayoutType(LayoutTypes layoutType) {
		if (controller.getHeadless())
			return;

		controller.getReachabilityGraph().setLayoutType(layoutType);

		setReachabilityPanel(layoutType);
	}

	/**
	 * Gets the controller.
	 *
	 * @return the controller
	 */
	public PetrinetController getController() {
		return controller;
	}

	/**
	 * Node marked.
	 *
	 * @return true, if successful
	 */
	public boolean nodeMarked() {
		return controller.getPetrinetGraph().getMarkedNode() != null;
	}

	private ViewPanel initGraphStreamView(Graph graph) {

		// Erzeuge Viewer mit passendem Threading-Model für Zusammenspiel mit
		// Swing
		SwingViewer viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

		// bessere Darstellungsqualität und Antialiasing (Kantenglättung) aktivieren
		// HINWEIS: Damit diese Attribute eine Auswirkung haben, müssen sie NACH
		// Erzeugung des SwingViewer gesetzt werden
		graph.setAttribute("ui.quality");
		graph.setAttribute("ui.antialias");

		if (graph instanceof ReachabilityGraph && ((ReachabilityGraph) graph).getLayoutType() == LayoutTypes.AUTOMATIC)
			viewer.enableAutoLayout();
		else
			viewer.disableAutoLayout();
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

		if (graph instanceof ReachabilityGraph && reachabilityViewPanel != null) {
			reachabilityViewPanel.addComponentListener(new ComponentAdapter() {

				@Override
				public void componentResized(ComponentEvent e) {

					if (graph instanceof PetrinetGraph
							|| ((ReachabilityGraph) graph).getLayoutType() == LayoutTypes.AUTOMATIC)
						return;

					controller.getReachabilityGraph().setScreenSize(reachabilityViewPanel.getSize());
					viewer.replayGraph(graph);

				}

			});
		}

		if (graph instanceof ReachabilityGraph)
			((ReachabilityGraph) graph).setReplayGraphListener(() -> adjustArrowHeads());

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

					if (graph instanceof ReachabilityGraph) {
						controller.onReachabilityGraphNodeClicked(id);
					} else {

						Petrinet petrinet = controller.getPetrinet();
						PetrinetElement p = petrinet.getPetrinetElement(id);
						double x = element.getX();
						double y = element.getY();
						if (p != null)
							if (p.getX() != x || p.getY() != y) {

								controller.onPetrinetNodeDragged(id, x, y);
							} else {

								if (graph instanceof PetrinetGraph)
									controller.onPetrinetNodeClicked(id);
							}
						element = null;
					}
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

	// for some reason replayGraph() does only work by resizing the panel

	private void adjustArrowHeads() {
		Dimension currentSize = reachabilityViewPanel.getSize();
		reachabilityViewPanel.setSize(currentSize.width + 1, currentSize.height);
		reachabilityViewPanel.setSize(currentSize);
	}
}
