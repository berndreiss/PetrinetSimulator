package listeners;
//TODO comments!

/**
 * 
 */
public interface ClickListener {

	/**	*/
	void onTransitionClicked(String id);
	/**	*/
	void onPetrinetNodeDragged(String id, double x, double y);
	/**	*/
	void onReachabilityGraphNodeClicked(String id);
}
