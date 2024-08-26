package net.berndreiss.petrinetsimulator.control;

import javax.swing.JPanel;

import net.berndreiss.petrinetsimulator.core.Petrinet;
import net.berndreiss.petrinetsimulator.core.PetrinetAnalyser;
import net.berndreiss.petrinetsimulator.gui.PetrinetGraph;
import net.berndreiss.petrinetsimulator.gui.ReachabilityGraph;
import net.berndreiss.petrinetsimulator.gui.ResizableSplitPane;
import net.berndreiss.petrinetsimulator.gui.ToolbarMode;

/**
 * <p>
 * An interface for a {@link JPanel} presenting a {@link Petrinet} with its
 * according reachability graph loaded from a file. It serves on the one hand as
 * a intermediary between the main controller and the petrinet controller and on
 * the other mediates clicks on the {@link PetrinetGraph} between the
 * {@link PetrinetViewerController} and the {@link PetrinetEditorController}.
 * According to the {@link ToolbarMode} it is currently set to.
 * </p>
 */
public interface PetrinetPanel {

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
	 * @param toolbarMode the toolbar mode to use
	 */
	void setToolbarMode(ToolbarMode toolbarMode);

	/**
	 * Gets the editor.
	 *
	 * @return the editor
	 */
	PetrinetEditorController getEditor();

	/**
	 * Zooms into the petrinet graph.
	 */
	void zoomInPetrinet();

	/**
	 * Zooms out of the petrinet graph.
	 */
	void zoomOutPetrinet();

	/**
	 * 
	 * Analyses whether the current petrinet is bounded or unbounded. In the process
	 * also adjusts the arrow heads in the <a href="https://graphstream-project.org/">GraphStream</a> graph.
	 * 
	 * @return a petrinet analyser
	 * 
	 */
	PetrinetAnalyser getAnalyser();

	/**
	 * Resets the reachability graph.
	 */
	void resetReachabilityGraph();

	/**
	 * Undoes the last step in the reachability graph shown.
	 */

	void undo();

	/**
	 * Redoes the last step in the reachability graph shown.
	 */
	void redo();

	/**
	 * Zooms into the reachability graph.
	 */
	void zoomInReachability();

	/**
	 * Zooms out of the reachability graph.
	 */
	void zoomOutReachability();

	/**
	 * Gets the graph split pane containing the petrinet graph panel and the
	 * reachabilty graph panel.
	 *
	 * @return the graph split pane
	 */
	ResizableSplitPane getGraphSplitPane();

	/**
	 * Gets the reachability graph.
	 * 
	 * @return the reachability graph
	 */
	public ReachabilityGraph getReachabilityGraph();

}
