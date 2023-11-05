package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.EnumSet;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import org.graphstream.graph.Graph;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.util.InteractiveElement;

import control.MainController;
import control.PetrinetViewerController;
import control.PetrinetEditorController;
import core.Petrinet;
import core.PetrinetAnalyser;
import core.PetrinetElement;
import core.Transition;
import exceptions.PetrinetException;
import reachabilityGraphLayout.LayoutType;

/**
 * <p>
 * A {@link JPanel} presenting a {@link Petrinet} with its according
 * reachability graph loaded from a file. It serves on the one hand as a
 * intermediary between the main controller and the petrinet controller and on
 * the other mediates clicks on the {@link PetrinetGraph} between the
 * {@link PetrinetViewerController} and the {@link PetrinetEditorController}.
 * According to the {@link ToolbarMode} it is currently set to.
 * </p>
 * 
 * It holds a horizontal {@link ResizableSplitPane} containing a
 * {@link GraphStreamPetrinetGraph} on the left and a {@link ReachabilityGraph}
 * on the right. Creates and holds a {@link PetrinetViewerController} managing
 * all interactions with the data model. The graphs are implemented using the
 * <a href="https://graphstream-project.org/">GraphStream</a> library.
 * Additionally creates an {@link PetrinetEditorController} through which the
 * user can edit the {@link Petrinet} using the {@link PetrinetToolbar}.
 * Additionally it deals with the problem of GraphStream graphs arrow heads not
 * adjusting correctly when resizing components or adding / removing elements.
 */
public class PetrinetPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/** Toolbar mode (EDITOR / VIEWER) the panel is in. By default set to VIEWER. */
	private ToolbarMode toolbarMode = ToolbarMode.VIEWER;

	/** split pane holding petrinet and reachability graph */
	private ResizableSplitPane graphSplitPane;

	/** */
	private GraphStreamPetrinetGraph petrinetGraph;
	/** */
	private ReachabilityGraph reachabilityGraph;

	/**
	 * panel for the petrinet -> if the view panel for the GraphStream graph is not
	 * put inside another panel the layout is unstable
	 */
	private JPanel petrinetPanel;

	/**
	 * panel for the reachability graph -> if the view panel for the GraphStream
	 * graph is not put inside another panel the layout is unstable
	 */
	private JPanel reachabilityPanel;

	/** view panel holding the viewer for the GraphStream graph */
	private ViewPanel petrinetViewPanel;
	/** view panel holding the viewer for the GraphStream graph */
	private ViewPanel reachabilityViewPanel;

	/** controller managing the interaction with the data model */
	private PetrinetViewerController petrinetController;
	/** controller managing the interaction with toolbar and menu */
	private MainController mainController;
	/** controller managing changes to the petrinet */
	private PetrinetEditorController editor;

	/**
	 * Keep track whether petrinetController is analysing -> do not adjust arrow
	 * heads until finished
	 */
	private boolean analysing = false;

	/**
	 * keeping track whether it is the first time adjusting the arrow heads since
	 * there are problems adjusting them the first time after creating the view
	 * panel for some reason
	 */
	private boolean firstTimeArrowHeadAdjusting = true;

	/**
	 * Instantiates a new petrinet panel.
	 *
	 * @param mainController The controller serving as an interface to the main GUI
	 *                       components.
	 * @param file           The pnml file from which contents will be loaded. If
	 *                       null goes into {@link ToolbarMode} EDITOR.
	 * @param layoutType     The layout type used.
	 * @throws PetrinetException If there is a problem reading a file an exception
	 *                           is thrown.
	 */
	public PetrinetPanel(MainController mainController, File file, LayoutType layoutType) throws PetrinetException {

		this.petrinetController = new PetrinetViewerController(file, mainController);
		this.mainController = mainController;

		this.petrinetGraph = new GraphStreamPetrinetGraph(petrinetController.getPetrinet());

		this.editor = new PetrinetEditorController(petrinetController, petrinetGraph, mainController);

		this.setLayout(new BorderLayout());

		// TODO REMOVE?
		// linking the petrinet controller to the toolbar via the main controller ->
		// needed for toggling the layout and un-/redo buttons
//		petrinetController.setToolbarToggleListener(mainController);

		petrinetPanel = new JPanel();
		petrinetPanel.setLayout(new BorderLayout());

		// get view panel for the petrinet graph and add it to the JPanel
		this.petrinetViewPanel = initGraphStreamView(petrinetGraph, petrinetPanel);
		petrinetPanel.add(petrinetViewPanel, BorderLayout.CENTER);

		reachabilityPanel = new JPanel();
		reachabilityPanel.setLayout(new BorderLayout());

		// sets up the split pane -> has own method so that on look and feel change
		// needs to be reset, otherwise divider does not register mouse events
		setSplitPane();

		// set up reachability panel -> has own method so that on layout changes between
		// auto layout and custom layouts can be reset
		setReachabilityPanel(layoutType);

		// replays the graph and adjusts auto layout -> otherwise it does not render
		// properly for some reason
		if (layoutType == LayoutType.AUTOMATIC)
			adjustArrowHeads();
	}

	/*
	 * set up the reachability panel, removing the reachability view panel if it has
	 * been added and getting the GraphStream view panel, adding it to the split
	 * pane
	 */
	private void setReachabilityPanel(LayoutType layoutType) {

		if (reachabilityPanel.getComponentCount() != 0)
			reachabilityPanel.remove(reachabilityViewPanel);

		reachabilityGraph = new ReachabilityGraph(petrinetController.getReachabilityGraphModel(), layoutType);

		reachabilityViewPanel = initGraphStreamView(reachabilityGraph, reachabilityPanel);
		reachabilityPanel.add(reachabilityViewPanel, BorderLayout.CENTER);

		firstTimeArrowHeadAdjusting = true;

	}

	/**
	 * (Re)sets the split pane containing the petrinet graph and the reachability
	 * graph.
	 */
	public void setSplitPane() {

		// variable for storing old divider ratio in case the split pane has already
		// been instantiated
		Double oldDividerRatio = null;

		// if split pane has been instantiated remove it and store divider ratio
		if (graphSplitPane != null) {
			remove(graphSplitPane);
			oldDividerRatio = graphSplitPane.getDividerRatio();
		}

		// create new split pane, add it and eventually set old divider ratio
		graphSplitPane = new ResizableSplitPane(mainController.getFrame(), JSplitPane.HORIZONTAL_SPLIT, petrinetPanel,
				reachabilityPanel);
		add(graphSplitPane, BorderLayout.CENTER);

		if (oldDividerRatio != null)
			graphSplitPane.setDividerRatio(oldDividerRatio);
	}

	/**
	 * Sets the layout type for the reachability graph.
	 *
	 * @param layoutType The new layout type to use with the panel.
	 */
	public void setLayoutType(LayoutType layoutType) {

		// reset the reachability panel if there is a change from or to auto layout,
		// only update reachability graph otherwise
		if (layoutType == LayoutType.AUTOMATIC || reachabilityGraph.getLayoutType() == LayoutType.AUTOMATIC) {
			setReachabilityPanel(layoutType);

		} else {
			reachabilityGraph.setLayoutType(layoutType);
			resetReachabilityZoom();

		}

		// adjust the arrow heads since they become detached from the nodes but let
		// GraphStream do its thing first
		SwingUtilities.invokeLater(() -> adjustArrowHeads());
	}

	/**
	 * 
	 * Analyse whether the current petrinet is bounded or unbounded. In the process
	 * also adjusts the arrow heads in the GraphStream graph.
	 * 
	 * @return a petrinet analyser
	 * 
	 */
	public PetrinetAnalyser analyse() {

		// do not adjust arrow heads while analysing but adjust them afterwards
		this.analysing = true;
		PetrinetAnalyser analyser = petrinetController.analyse();
		this.analysing = false;
		SwingUtilities.invokeLater(() -> adjustArrowHeads());
		return analyser;
	}

	/**
	 * Gets the graph split pane containing the petrinet graph panel and the
	 * reachabilty graph panel.
	 *
	 * @return the graph split pane
	 */
	public ResizableSplitPane getGraphSplitPane() {
		return graphSplitPane;
	}

	/**
	 * Zoom into the petrinet graph.
	 */
	public void zoomInPetrinet() {
		zoomIn(petrinetViewPanel);
	}

	/**
	 * Zoom out of the petrinet graph.
	 */
	public void zoomOutPetrinet() {
		zoomOut(petrinetViewPanel);
	}

	/**
	 * Zoom into the reachability graph.
	 */
	public void zoomInReachability() {
		// disable zoom if there's only one node in graph, since there are problems with
		// nodes disappearing and a zoom on one node does not make any difference
		if (reachabilityGraph.hasLessThanTwoNodes())
			return;
		zoomIn(reachabilityViewPanel);
	}

	/**
	 * Zoom out reachability.
	 */
	public void zoomOutReachability() {
		// disable zoom if there's only one node in graph, since there are problems with
		// nodes disappearing and a zoom on one node does not make any difference
		if (reachabilityGraph.hasLessThanTwoNodes())
			return;
		zoomOut(reachabilityViewPanel);
	}

	/**
	 * 
	 * Sets the zoom factor of the reachability graph back to 1.0.
	 * 
	 */

	public void resetReachabilityZoom() {

		double zoom = reachabilityViewPanel.getCamera().getViewPercent();

		if (zoom != 1.0)
			reachabilityViewPanel.getCamera().resetView();
	}

	// zoom into the view panel provided
	private void zoomIn(ViewPanel viewPanel) {

		if (viewPanel == null)// safety check
			return;

		double zoom = viewPanel.getCamera().getViewPercent();

		if (zoom > 0.1) {
			viewPanel.getCamera().setViewPercent(zoom - 0.1);
			// adjust the arrow heads since they don't readjust according to zoom
			adjustArrowHeads();
		}
	}

	// zoom out of the view panel provided
	private void zoomOut(ViewPanel viewPanel) {
		if (viewPanel == null)
			return;

		double zoom = viewPanel.getCamera().getViewPercent();

		viewPanel.getCamera().setViewPercent(zoom + 0.1);

		// adjust the arrow heads since they don't readjust according to zoom
		adjustArrowHeads();
	}

	/**
	 * Gets the petrinet controller.
	 *
	 * @return the petrinet controller
	 */
	public PetrinetViewerController getPetrinetController() {
		return petrinetController;
	}

	/**
	 * Gets the editor.
	 *
	 * @return the editor
	 */
	public PetrinetEditorController getEditor() {
		return editor;
	}

	/**
	 * Gets the toolbar mode.
	 *
	 * @return the toolbar mode
	 */
	public ToolbarMode getToolbarMode() {

		return toolbarMode;
	}

	/**
	 * Sets the toolbar mode. If it changes to EDITOR resets the reachability graph.
	 * If it changes to VIEWER handles marked transitions (since transitions cannot
	 * be marked in VIEWER mode).
	 *
	 * @param toolbarMode The toolbar mode to use.
	 */
	public void setToolbarMode(ToolbarMode toolbarMode) {

		if (this.toolbarMode == toolbarMode)
			return;

		// if switching to EDITOR reset reachability graph
		if (toolbarMode == ToolbarMode.EDITOR) {
			petrinetController.resetReachabilityGraph();
			editor = new PetrinetEditorController(petrinetController, petrinetGraph, mainController);
		}
		// if switching to VIEWER make sure no transitions are marked anymore
		if (toolbarMode == ToolbarMode.VIEWER) {
			PetrinetElement marekdElement = petrinetGraph.getMarkedNode();

			// toggle marked node if it is a transition
			if (marekdElement != null && marekdElement instanceof Transition)
				petrinetGraph.toggleNodeMark(marekdElement);
		}

		// set toolbar mode
		this.toolbarMode = toolbarMode;
	}

	/*
	 * Returns a view panel containing a GraphStream graph. Also sets up listeners
	 * for click on nodes and dragging of nodes to communicate to the controllers.
	 * Additionally implements a componentResized listener on the panel containing
	 * the view panel. Since GraphStream has problems adjusting the arrow heads when
	 * components get resized the graph has to be replayed when the parent changes
	 * size.
	 * 
	 */
	private ViewPanel initGraphStreamView(Graph graph, Component parent) {

		// create viewer with thread model for Swing
		SwingViewer viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

		// better quality and smooth edges (antialias)
		graph.setAttribute("ui.quality");
		graph.setAttribute("ui.antialias");

		// set auto layout for reachability graphs if it has the layout type
		if (graph instanceof ReachabilityGraph && ((ReachabilityGraph) graph).getLayoutType() == LayoutType.AUTOMATIC)
			viewer.enableAutoLayout();
		else
			viewer.disableAutoLayout();

		ViewPanel viewPanel = (ViewPanel) viewer.addDefaultView(false);

		// create viewer pipe to be informed about events
		ViewerPipe viewerPipe = viewer.newViewerPipe();

		// set of elements to listen for in the graph (for our purposes only nodes)
		EnumSet<InteractiveElement> enumSet = EnumSet.of(InteractiveElement.NODE);

		// listen for clicks on nodes and refer events to controller
		viewPanel.addMouseListener(new MouseAdapter() {

			// keep track of clicked element to listen for coordinate changes
			private GraphicElement element;
			private Double x;
			private Double y;

			@Override
			public void mousePressed(MouseEvent me) {
				// register element if mouse is pressed
				element = viewPanel.findGraphicElementAt(enumSet, me.getX(), me.getY());
				if (element != null) {
					x = element.getX();
					y = element.getY();
				}
				viewerPipe.pump();

			}

			@Override
			public void mouseReleased(MouseEvent me) {

				if (x != null && y != null && element != null) {
					String id = element.getId();

					// refer mouse click to the right method in the right controller
					if (graph instanceof ReachabilityGraph) {
						petrinetController.onReachabilityGraphNodeClicked(id);
					} else {

						// if graph is instance of petrinet graph check whether node has been dragged or
						// clicked
						if (element != null)
							// if node has been dragged, give corresponding coordinates to controller
							if (element.getX() != x || element.getY() != y) {
								petrinetController.onPetrinetNodeDragged(id, element.getX(), element.getY());
							}
							//
							else {

								// transfer the click event to the right controller
								if (toolbarMode == ToolbarMode.VIEWER) {

									// if node is transition controller handles the event and null is returned;
									// otherwise a place is returned and the event has to be referred back to the
									// graph
									PetrinetElement pe = petrinetController.onPetrinetNodeClicked(id);

									// refer event back to the graph
									if (pe != null)
										petrinetGraph.toggleNodeMark(pe);

								}

								if (toolbarMode == ToolbarMode.EDITOR)
									editor.clickedNodeInGraph(id);
							}
						x = null;
						y = null;
					}
				}
				viewerPipe.pump();
			}

		});
		// enable zooming via mouse wheel
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

		// add component resized on panel containing view panel -> when it is resized
		// the graph is replayed so that arrow heads are adjusted
		if (parent != null) {
			parent.addComponentListener(new ComponentAdapter() {

				@Override
				public void componentResized(ComponentEvent e) {
					viewer.replayGraph(graph);
				}
			});
		}

		// replay listener to be able to manually replay the graph on certain events
		// (e.g. adding nodes). For some reason only works when called via the
		// componenResized listener above, therefore using the adjustArrowHeads method
		if (graph instanceof ReachabilityGraph)
			((ReachabilityGraph) graph).setReplayGraphListener(() -> adjustArrowHeads());

		return viewPanel;

	}

	// for some reason replayGraph() does only work in resetting the arrow heads by
	// resizing the frame and invoking the method via the ComponentListener
	private void adjustArrowHeads() {

		// if analysing do not adjust -> invokes unnecassary waiting time
		if (analysing)
			return;

		// wait a moment for GraphStream to do its thing -> otherwise frame might resize
		// too early and the arrow heads do not align
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// resize the mainframe slightly and set size back to trigger componentResized
		// listener in method initGraphStreamView
		JFrame parent = mainController.getFrame();
		Dimension currentSize = parent.getSize();
		parent.setSize(currentSize.width + 1, currentSize.height);
		parent.setSize(currentSize);

		if (firstTimeArrowHeadAdjusting) {
			firstTimeArrowHeadAdjusting = false;
			SwingUtilities.invokeLater(() -> adjustArrowHeads());

		}

	}

}
