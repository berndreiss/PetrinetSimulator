package gui;

// TODO: Auto-generated Javadoc
/**
 * The Interface PetrinetToolbarInterface.
 */
public interface PetrinetToolbarInterface {

	/**
	 * On open.
	 */
	void onOpen();
	
	/**
	 * On save.
	 */
	void onSave();
	
	/**
	 * On previous.
	 */
	void onPrevious();
	
	/**
	 * On next.
	 */
	void onNext();
	
	/**
	 * On restart.
	 */
	void onRestart();
	
	/**
	 * On plus.
	 */
	void onPlus();
	
	/**
	 * On minus.
	 */
	void onMinus();
	
	/**
	 * On reset.
	 */
	void onReset();
	
	/**
	 * On analyse.
	 */
	void onAnalyse();
	
	/**
	 * On clear.
	 */
	void onClear();
	
	/**
	 * On undo.
	 */
	void onUndo();
	
	/**
	 * On redo.
	 */
	void onRedo();

	/**
	 * On set default.
	 */
	void onSetDefault();
	
	/**
	 * On zoom in.
	 */
	void onZoomIn();
	
	/**
	 * On zoom out.
	 */
	void onZoomOut();
	
	/**
	 * On open editor.
	 */
	void onOpenEditor();

	
	
	/**
	 * On add place.
	 */
	//Additional functions for Editor
	void onAddPlace();

	/**
	 * On add transition.
	 */
	void onAddTransition();
	
	/**
	 * On add edge.
	 */
	void onAddEdge();
	
	/**
	 * On add label.
	 */
	void onAddLabel();
	
	/**
	 * On remove edge.
	 */
	void onRemoveEdge();
	
	/**
	 * On remove component.
	 */
	void onRemoveComponent();
	
	/**
	 * On close editor.
	 */
	void onCloseEditor();

	//Additional functions for ReachabilityGraph
	
	/**
	 * On zoom in reachability.
	 */
	void onZoomInReachability();

	/**
	 * On zoom out reachability.
	 */
	void onZoomOutReachability();

	/**
	 * On toggle auto layout.
	 */
	void onToggleAutoLayout();

	/**
	 * On toggle tree layout.
	 */
	void onToggleTreeLayout();

	/**
	 * On toggle circle layout.
	 */
	void onToggleCircleLayout();

	/**
	 * Change design.
	 */
	void changeDesign();

	
}
