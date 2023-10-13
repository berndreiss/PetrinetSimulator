package datamodel;

public interface PetrinetComponentChangedListener {

	void onPetrinetElementAdded(PetrinetElement element);
	void onPetrinetElementSetCoordinates(PetrinetElement element, float x, float y);
	void onPetrinetElementRemoved(PetrinetElement element);
	void onEdgeAdded(PetrinetElement source, PetrinetElement target, String id);
	void onEdgeRemoved(String edge);
	void onPlaceTokenCountChanged(Place place);
	void onSetPetrinetElementName(PetrinetElement element);
	void onTransitionStateChanged(Transition transition);
}
