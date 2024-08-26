package net.berndreiss.petrinetsimulator.gui;

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

import net.berndreiss.petrinetsimulator.control.MainController;
import net.berndreiss.petrinetsimulator.control.PetrinetViewerController;
import net.berndreiss.petrinetsimulator.control.PetrinetEditorController;
import net.berndreiss.petrinetsimulator.control.PetrinetPanel;
import net.berndreiss.petrinetsimulator.core.Petrinet;
import net.berndreiss.petrinetsimulator.core.PetrinetAnalyser;
import net.berndreiss.petrinetsimulator.core.PetrinetElement;
import net.berndreiss.petrinetsimulator.core.Transition;
import net.berndreiss.petrinetsimulator.exceptions.PetrinetException;
import net.berndreiss.petrinetsimulator.reachabilityGraphLayout.LayoutType;

/**
 * <p>
 * An implementation of the {@link PetrinetPanel}.
 * </p>
 * 
 * <p>
 * It holds a horizontal {@link ResizableSplitPane} containing an implementation
 * of the {@link PetrinetGraph} on the left and a
 * {@link GraphStreamReachabilityGraph} on the right. Creates and holds a
 * {@link PetrinetViewerController} managing all interactions with the data
 * model. The graphs are implemented using the
 * <a href="https://graphstream-project.org/">GraphStream</a> library.
 * Additionally creates an {@link PetrinetEditorController} through which the
 * user can edit the {@link Petrinet} using the {@link PetrinetToolbar}.
 * Additionally it deals with the problem of GraphStream graphs arrow heads not
 * adjusting correctly when resizing components or adding / removing elements.
 * </p>
 */
public class GraphStreamPetrinetPanel extends JPanel implements PetrinetPanel {

	private static final long serialVersionUID = 1L;

	/** Toolbar mode (EDITOR / VIEWER) the panel is in. By default set to VIEWER. */
	private ToolbarMode toolbarMode = ToolbarMode.VIEWER;

	/** Split pane holding petrinet and reachability graph. */
	private ResizableSplitPane graphSplitPane;

	/** A GraphStream implementation of PetrinetGraph. */
	private GraphStreamPetrinetGraph petrinetGraph;
	/** A GraphStream implementation of ReachabilityGraph. */
	private GraphStreamReachabilityGraph reachabilityGraph;

	/**
	 * Panel for the petrinet -> if the view panel for the GraphStream graph is not
	 * put inside another panel the layout is unstable.
	 */
	private JPanel petrinetPanel;

	/**
	 * Panel for the reachability graph -> if the view panel for the GraphStream
	 * graph is not put inside another panel the layout is unstable.
	 */
	private JPanel reachabilityPanel;

	/** View panel holding the viewer for the GraphStream graph. */
	private ViewPanel petrinetViewPanel;
	/** View panel holding the viewer for the GraphStream graph. */
	private ViewPanel reachabilityViewPanel;

	/** Controller managing the interaction with the data model. */
	private PetrinetViewerController petrinetViewerController;
	/** Controller managing the interaction with toolbar and menu. */
	private MainController mainController;
	/** Controller managing changes to the petrinet. */
	private PetrinetEditorController editor;

	/**
	 * Keep track whether arrow head should be adjusted -> takes time which
	 * accumulates when doing multiple tasks in quick succession (e.g. when
	 * analyzing a petrinet).
	 */
	private boolean adjustArrowHeads = true;

	/**
	 * Keeping track whether it is the first time adjusting the arrow heads since
	 * there are problems adjusting them the first time after creating the view
	 * panel for some reason.
	 */
	private boolean firstTimeArrowHeadAdjusting = true;

	/**
	 * Instantiates a new petrinet panel.
	 *
	 * @param mainController the controller serving as an interface to the main GUI
	 *                       components
	 * @param file           the pnml file from which contents will be loaded, if
	 *                       null goes into {@link ToolbarMode} EDITOR
	 * @param layoutType     the layout type used
	 * @param toolbarMode    the toolbar mode that is being used
	 * @throws PetrinetException if there is a problem reading a file an exception
	 *                           is thrown
	 */
	public GraphStreamPetrinetPanel(MainController mainController, File file, LayoutType layoutType, ToolbarMode toolbarMode)
			throws PetrinetException {

		this.toolbarMode = toolbarMode;
		this.petrinetViewerController = new PetrinetViewerController(file, mainController, toolbarMode);
		this.mainController = mainController;

		this.petrinetGraph = new GraphStreamPetrinetGraph(petrinetViewerController.getPetrinet());

		this.editor = new PetrinetEditorController(petrinetViewerController, petrinetGraph, mainController);

		this.setLayout(new BorderLayout());

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
		setReachabilityPanel(layoutType, false);

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
	private void setReachabilityPanel(LayoutType layoutType, boolean showPath) {

		if (reachabilityPanel.getComponentCount() != 0)
			reachabilityPanel.remove(reachabilityViewPanel);

		reachabilityGraph = new GraphStreamReachabilityGraph(petrinetViewerController.getReachabilityGraphModel(),
				layoutType, mainController.getShowBoundedness(), showPath);

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
	 * @param layoutType the new layout type to use with the panel
	 */
	public void setLayoutType(LayoutType layoutType) {

		// reset the reachability panel if there is a change from or to auto layout,
		// only update reachability graph otherwise
		if (layoutType == LayoutType.AUTOMATIC || reachabilityGraph.getLayoutType() == LayoutType.AUTOMATIC)
			setReachabilityPanel(layoutType, reachabilityGraph.pathShown());

		else {
			reachabilityGraph.setLayoutType(layoutType);
			resetReachabilityZoom();

		}

		// adjust the arrow heads since they become detached from the nodes but let
		// GraphStream do its thing first
		SwingUtilities.invokeLater(() -> adjustArrowHeads());
	}

	@Override
	public PetrinetAnalyser getAnalyser() {

		boolean originalShowBoundedness = reachabilityGraph.getShowBoundedness();
		reachabilityGraph.setShowBoundedness(true);
		// do not adjust arrow heads while analysing but adjust them afterwards
		this.adjustArrowHeads = false;
		PetrinetAnalyser analyser = petrinetViewerController.analyse();
		this.adjustArrowHeads = true;
		SwingUtilities.invokeLater(() -> adjustArrowHeads());
		reachabilityGraph.setShowBoundedness(originalShowBoundedness);
		return analyser;
	}

	@Override
	public ResizableSplitPane getGraphSplitPane() {
		return graphSplitPane;
	}

	@Override
	public void zoomInPetrinet() {
		zoomIn(petrinetViewPanel);
	}

	@Override
	public void zoomOutPetrinet() {
		zoomOut(petrinetViewPanel);
	}

	@Override
	public void zoomInReachability() {
		// disable zoom if there's only one node in graph, since there are problems with
		// nodes disappearing and a zoom on one node does not make any difference
		if (reachabilityGraph.hasLessThanTwoNodes())
			return;
		zoomIn(reachabilityViewPanel);
	}

	@Override
	public void zoomOutReachability() {
		// disable zoom if there's only one node in graph, since there are problems with
		// nodes disappearing and a zoom on one node does not make any difference
		if (reachabilityGraph.hasLessThanTwoNodes())
			return;
		zoomOut(reachabilityViewPanel);
	}

	// Sets the zoom factor of the reachability graph back to 1.0.
	private void resetReachabilityZoom() {

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

	@Override
	public PetrinetViewerController getPetrinetViewerController() {
		return petrinetViewerController;
	}

	@Override
	public PetrinetEditorController getEditor() {
		return editor;
	}

	@Override
	public ToolbarMode getToolbarMode() {

		return toolbarMode;
	}

	@Override
	public void setToolbarMode(ToolbarMode toolbarMode) {

		if (this.toolbarMode == toolbarMode)
			return;

		// if switching to EDITOR reset reachability graph
		if (toolbarMode == ToolbarMode.EDITOR) {
			petrinetViewerController.resetReachabilityGraph();
			editor = new PetrinetEditorController(petrinetViewerController, petrinetGraph, mainController);
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
		if (graph instanceof GraphStreamReachabilityGraph
				&& ((GraphStreamReachabilityGraph) graph).getLayoutType() == LayoutType.AUTOMATIC)
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
					if (graph instanceof GraphStreamReachabilityGraph) {
						petrinetViewerController.onReachabilityGraphNodeClicked(id);
					} else {

						// if graph is instance of petrinet graph check whether node has been dragged or
						// clicked
						if (element != null)
							// if node has been dragged, give corresponding coordinates to controller
							if (element.getX() != x || element.getY() != y) {
								petrinetViewerController.onPetrinetNodeDragged(id, element.getX(), element.getY());
							}
							//
							else {

								// transfer the click event to the right controller
								if (toolbarMode == ToolbarMode.VIEWER) {

									// if node is transition controller handles the event and null is returned;
									// otherwise a place is returned and the event has to be referred back to the
									// graph
									PetrinetElement pe = petrinetViewerController.onPetrinetNodeClicked(id);

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
		if (graph instanceof GraphStreamReachabilityGraph)
			((GraphStreamReachabilityGraph) graph).setAdjustArrowHeadsListener(() -> adjustArrowHeads());

		return viewPanel;

	}

	// for some reason replayGraph() does only work in resetting the arrow heads by
	// resizing the frame and invoking the method via the ComponentListener
	private void adjustArrowHeads() {

		// if analysing do not adjust -> invokes unnecassary waiting time
		if (!adjustArrowHeads)
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

	@Override
	public void resetReachabilityGraph() {
		resetReachabilityZoom();
		adjustArrowHeads = false;
		petrinetViewerController.resetReachabilityGraph();
		adjustArrowHeads = true;
	}

	@Override
	public void undo() {
		adjustArrowHeads = false;
		petrinetViewerController.getPetrinetQueue().goBack();
		adjustArrowHeads = true;
		adjustArrowHeads();
	}

	@Override
	public void redo() {
		adjustArrowHeads = false;
		petrinetViewerController.getPetrinetQueue().goForward();
		adjustArrowHeads = true;
		adjustArrowHeads();
	}

	@Override
	public ReachabilityGraph getReachabilityGraph() {
		return reachabilityGraph;
	}
}
