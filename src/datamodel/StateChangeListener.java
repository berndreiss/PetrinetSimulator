package datamodel;

public interface StateChangeListener {

	void onSetCurrent(PetrinetState state);
	void onAdd(PetrinetState state, PetrinetState predecessor, Transition t);
	void onRemove(PetrinetState state);
	void onRemoveEdge(PetrinetState stateSource, PetrinetState stateTarget, Transition t);
}
