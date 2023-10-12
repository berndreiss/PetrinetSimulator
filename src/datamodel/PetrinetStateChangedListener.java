package datamodel;

public interface PetrinetStateChangedListener {
	void onTransitionFire(Transition t);
	void onComponentChanged(Petrinet petrinet);
}
