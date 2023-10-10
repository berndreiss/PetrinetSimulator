package datamodel;

public interface PetrinetChangeListener {
	void onTransitionFire(Transition t);
	void onChanged(Petrinet petrinet);
}
