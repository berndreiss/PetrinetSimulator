package net.berndreiss.petrinetsimulator.gui;
/**
 * <p>
 * Interface for a graph representing a {@link net.berndreiss.petrinetsimulator.core.ReachabilityGraphModel}.
 * </p>
 */
public interface ReachabilityGraph {

	
	/**
	 * Gets whether boundedness is shown in graph.
	 * 
	 * @return true if boundedness is shown
	 */
	boolean getShowBoundedness();

	/**
	 * If true, boundedness is shown in graph.
	 * 
	 * @param show true if boundedness should be shown
	 */
	void setShowBoundedness(boolean show);
}
