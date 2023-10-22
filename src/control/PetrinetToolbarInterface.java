package control;

public interface PetrinetToolbarInterface {

	void onOpen();
	
	void onSave();
	
	void onPrevious();
	
	void onNext();
	
	void onRestart();
	
	void onPlus();
	
	void onMinus();
	
	void onReset();
	
	void onAnalyse();
	
	void onClear();
	
	void onUndo();
	
	void onRedo();

	void onSetDefault();
	
	void onZoomIn();
	
	void onZoomOut();
	
	void onOpenEditor();

	
	
	//Additional functions for Editor
	void onAddPlace();

	void onAddTransition();
	
	void onAddEdge();
	
	void onAddLabel();
	
	void onRemoveEdge();
	
	void onRemoveComponent();
	
	void onCloseEditor();

	//Additional functions for ReachabilityGraph
	
	void onZoomInReachability();

	void onZoomOutReachability();

	void onToggleAutoLayout();

	
}
