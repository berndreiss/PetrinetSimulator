package util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import datamodel.DuplicateIdException;
import datamodel.Petrinet;
import datamodel.PetrinetElement;
import datamodel.Place;
import datamodel.Transition;
import propra.pnml.PNMLWopedParser;

public class PNMLParser extends PNMLWopedParser{

	private Map<String, Arc> arcs = new HashMap<String, Arc>();
	private Petrinet petrinet;
	
	public PNMLParser(File pnml, Petrinet petrinet) {
		super(pnml);
		this.petrinet = petrinet;
		this.initParser();
		this.parse();
		handleTransitions();
	}
	
	
	
	@Override
	public void newTransition(final String id) {
		try {
			petrinet.addTransition(new Transition(id));
		} catch (DuplicateIdException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void newPlace(final String id) {
		try {
			petrinet.addPlace(new Place(id));
		} catch (DuplicateIdException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void newArc(final String id, final String source, final String target) {
		arcs.put(id, new Arc(id, source, target));
	}

	public void setPosition(final String id, final String x, final String y) {
		float xF = Float.parseFloat(x);
		float yF = Float.parseFloat(y);
		
		PetrinetElement element = petrinet.getPetrinetElement(id);
		
		if (element != null) {
			element.setX(xF);
			element.setY(-yF);
		}
			
	
	}

	@Override
	public void setName(final String id, final String name) {
		PetrinetElement element = petrinet.getPetrinetElement(id);
		
		if (element != null)
			element.setName(name);
	}
	
	@Override
	public void setTokens(final String id, final String tokens) {

		petrinet.setTokens(id, Integer.parseInt(tokens));
	}

	
	
	private void handleTransitions(){
		
		for (String s: arcs.keySet()) {
			
			Arc a = arcs.get(s);
			
			String source = a.getSource();
			String target = a.getTarget();

			petrinet.addOriginalArcId(source, target, a.getId());
			
			Transition transition;
			Place place;
						
			if (petrinet.getTransition(source) != null) {
				try {
					petrinet.addOutput(petrinet.getPlace(target), petrinet.getTransition(source));
				} catch (DuplicateIdException e) {
					e.printStackTrace();
				};
			} else {
				try {
					petrinet.addInput(petrinet.getPlace(source), petrinet.getTransition(target));
				} catch (DuplicateIdException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	
	private class Arc{
		private String id;
		private String source;
		private String target;
		
		public Arc(String id, String source, String target) {
			this.id = id;
			this.source = source;
			this.target = target;
		}
		
		public String getId() {
			return id;
		}
		
		public String getSource() {
			return source;
		}
		
		public String getTarget() {
			return target;
		}
	}
}
