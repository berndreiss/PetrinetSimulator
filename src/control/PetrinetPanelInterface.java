package control;

import javax.swing.JPanel;

import core.Petrinet;
import core.PetrinetAnalyser;
import gui.PetrinetGraph;
import gui.ResizableSplitPane;
import gui.ToolbarMode;

/**
  * <p>
 * An interface for a {@link JPanel} presenting a {@link Petrinet} with its according
 * reachability graph loaded from a file. It serves on the one hand as a
 * intermediary between the main controller and the petrinet controller and on
 * the other mediates clicks on the {@link PetrinetGraph} between the
 * {@link PetrinetViewerController} and the {@link PetrinetEditorController}.
 * According to the {@link ToolbarMode} it is currently set to.
 * </p>
 */
public interface PetrinetPanelInterface {

	/**
	 * Gets the toolbar mode.
	 *
	 * @return the toolbar mode
	 */
	ToolbarMode getToolbarMode();

	/**
	 * Gets the petrinet controller.
	 *
	 * @return the petrinet controller
	 */
	PetrinetViewerController getPetrinetViewerController();

	/**
	 * Sets the toolbar mode. If it changes to EDITOR resets the reachability graph.
	 * If it changes to VIEWER handles marked transitions (since transitions cannot
	 * be marked in VIEWER mode).
	 *
	 * @param toolbarMode The toolbar mode to use.
	 */
	void setToolbarMode(ToolbarMode toolbarMode);

	/**
	 * Gets the editor.
	 *
	 * @return the editor
	 */
	PetrinetEditorController getEditor();

	/**
	 * Zoom into the petrinet graph.
	 */
	void zoomInPetrinet();
	/**
	 * Zoom out of the petrinet graph.
	 */
	void zoomOutPetrinet();
	
	/**
	 * 
	 * Analyse whether the current petrinet is bounded or unbounded. In the process
	 * also adjusts the arrow heads in the GraphStream graph.
	 * 
	 * @return a petrinet analyser
	 * 
	 */
	PetrinetAnalyser getAnalyser();

	/**
	 * Reset the reachability graph.
	 */
	void resetReachabilityGraph();

	/**
	 * Undo the last step in the reachability graph shown.
	 */

	void undo();
	/**
	 * Redo the last step in the reachability graph shown.
	 */
	void redo();
	/**
	 * Zoom into the reachability graph.
	 */
	void zoomInReachability();
	/**
	 * Zoom out reachability.
	 */
	void zoomOutReachability();

	/**
	 * Gets the graph split pane containing the petrinet graph panel and the
	 * reachabilty graph panel.
	 *
	 * @return the graph split pane
	 */
	ResizableSplitPane getGraphSplitPane();

}
