package listeners;

public interface ClickListener {

	void onPetrinetNodeClicked(String id);
	void onPetrinetNodeDragged(String id, double x, double y);
	void onReachabilityGraphNodeClicked(String id);
}
