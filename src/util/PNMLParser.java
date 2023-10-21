package util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import control.PetrinetException;
import datamodel.DuplicateIdException;
import datamodel.InvalidEdgeOperationException;
import datamodel.Petrinet;
import datamodel.Place;
import datamodel.Transition;
import propra.pnml.PNMLWopedParser;

public class PNMLParser extends PNMLWopedParser {

	private Map<String, Arc> arcs = new HashMap<String, Arc>();
	private List<String> places = new ArrayList<String>();
	private List<String> transitions = new ArrayList<String>();
	private Petrinet petrinet;

	private boolean idCheck = true;
	
	
	public PNMLParser(File pnml) throws PetrinetException {
		this(pnml, null);
	}
	
	public PNMLParser(File pnml, Petrinet petrinet) throws PetrinetException {
		super(pnml);
		if (petrinet == null)
			this.petrinet = new Petrinet();
		else
			this.petrinet = petrinet;
		this.initParser();
		this.parse();

		for (String s: places)
			petrinet.addPlace(s);
		for (String s: transitions)
			petrinet.addTransition(s);
		
		idCheck = false;
		this.initParser();
		this.parse();
		
		handleTransitions();
	}

	@Override
	public void newTransition(final String id) {
		if (idCheck)
			transitions.add(id);
	}

	@Override
	public void newPlace(final String id) {
		
		if (idCheck)
			places.add(id);
	}

	@Override
	public void newArc(final String id, final String source, final String target) {
		if (idCheck)
			return;
		arcs.put(id, new Arc(id, source, target));
	}

	public void setPosition(final String id, final String x, final String y) {
		if (idCheck)
			return;
		float xF = Float.parseFloat(x);
		float yF = Float.parseFloat(y);

		petrinet.setCoordinates(id, xF, -yF);

	}

	@Override
	public void setName(final String id, final String name) {
		if (idCheck)
			return;
		
		petrinet.setPetrinetElementName(id, name);

	}

	@Override
	public void setTokens(final String id, final String tokens) {
		if (idCheck)
			return;
		
		petrinet.setTokens(id, Integer.parseInt(tokens));
	}

	private void handleTransitions() throws InvalidEdgeOperationException {
		if (idCheck)
			return;
		
		for (String s : arcs.keySet()) {

			Arc a = arcs.get(s);

			String source = a.getSource();
			String target = a.getTarget();

			petrinet.addEdge(source, target, a.id);
		}

	}

	private class Arc {
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
	
	public Petrinet getPetrinet() {
		return petrinet;
	}
}
