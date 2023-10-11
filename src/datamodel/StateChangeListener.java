package datamodel;

public interface StateChangeListener {

	void onSetCurrent(PetrinetState state, boolean reset);
	void onMarkInvalid(PetrinetState m, PetrinetState mMarked);
	void onAdd(PetrinetState state, PetrinetState predecessor, Transition t);
	void onRemove(PetrinetState state);
	void onRemoveEdge(PetrinetState stateSource, PetrinetState stateTarget, Transition t);
}
