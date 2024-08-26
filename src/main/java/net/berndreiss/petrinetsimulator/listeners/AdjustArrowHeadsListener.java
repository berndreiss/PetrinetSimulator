package net.berndreiss.petrinetsimulator.listeners;

/**
 * <p>
 * The listener interface for receiving adjust arrow heads events.
 * </p>
 * <p>
 * Since arrow heads of edges in
 * <a href="https://graphstream-project.org/">GraphStream</a> graphs tend to be
 * misaligned on certain events (e.g. adding/removing nodes) this listener
 * provides a method for adjusting arrow heads.
 * </p>
 */
public interface AdjustArrowHeadsListener {

	/**
	 * Adjusts the arrow heads.
	 */
	void onAdjustArrowHeads();
}
