package util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import datamodel.Arc;
import datamodel.Petrinet;
import datamodel.PetrinetElement;
import datamodel.Place;
import datamodel.Transition;
import propra.pnml.PNMLWopedParser;

public class PNMLParser extends PNMLWopedParser{

	private Map<String, Place> places = new HashMap<String, Place>();
	private Map<String, Transition> transitions = new HashMap<String, Transition>();
	private Map<String, Arc> arcs = new HashMap<String, Arc>();
	
	public PNMLParser(File pnml) {
		super(pnml);
	}
	
	
	
	@Override
	public void newTransition(final String id) {
		transitions.put(id, new Transition(id));
	}
	
	@Override
	public void newPlace(final String id) {
		System.out.println("TEST");
		places.put(id, new Place(id));
	}
	
	@Override
	public void newArc(final String id, final String source, final String target) {
		arcs.put(id, new Arc(id, source, target));
	}

	public void setPosition(final String id, final String x, final String y) {
		float xF = Float.parseFloat(x);
		float yF = Float.parseFloat(y);
		
		PetrinetElement element = getElement(id);
		
		if (element != null) {
			element.setX(xF);
			element.setY(yF);
		}
			
	
	}

	@Override
	public void setName(final String id, final String name) {
		PetrinetElement element = getElement(id);
		
		if (element != null)
			element.setName(name);
	}
	
	@Override
	public void setTokens(final String id, final String tokens) {
		Place place = (Place) places.get(id);
		
		if (place != null)
			place.setNumberOfTokens(Integer.parseInt(tokens));
	}

	private PetrinetElement getElement(String id){
		if (arcs.containsKey(id))
			return arcs.get(id);
		if (transitions.containsKey(id))
			return transitions.get(id);
		if (places.containsKey(id))
			return places.get(id);
		return null;
	}
	
	public Map<String, Place> getPlaces(){
		return places;
	}
	
	public Map<String, Transition> getTransitions(){
		
		for (String s: arcs.keySet()) {
			
			Arc a = arcs.get(s);
			
			String source = a.getSource();
			String target = a.getTarget();
			
			Transition transition;
			Place place;
			
			if (transitions.containsKey(source)) {
				transition = transitions.get(source);
				place = places.get(target);
				transition.addOutput(place);
			} else {
				transition = transitions.get(target);
				place = places.get(source);
				transition.addInput(place);
			}
		}
		
		return transitions;
	}
}
