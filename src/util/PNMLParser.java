package util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import datamodel.Petrinet;
import datamodel.Place;
import datamodel.Transition;
import propra.pnml.PNMLWopedParser;

public class PNMLParser extends PNMLWopedParser {

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
			petrinet.addTransition(new Transition(id));
	}

	@Override
	public void newPlace(final String id) {
			petrinet.addPlace(new Place(id));
	}

	@Override
	public void newArc(final String id, final String source, final String target) {
		arcs.put(id, new Arc(id, source, target));
	}

	public void setPosition(final String id, final String x, final String y) {
		float xF = Float.parseFloat(x);
		float yF = Float.parseFloat(y);

		petrinet.setCoordinates(id, xF, -yF);

	}

	@Override
	public void setName(final String id, final String name) {
		
		petrinet.setPetrinetElementName(id, name);

	}

	@Override
	public void setTokens(final String id, final String tokens) {

		petrinet.setTokens(id, Integer.parseInt(tokens));
	}

	private void handleTransitions() {

		for (String s : arcs.keySet()) {

			Arc a = arcs.get(s);

			String source = a.getSource();
			String target = a.getTarget();

			if (petrinet.getTransition(source) != null) {
					petrinet.addOutput(petrinet.getPlace(target), petrinet.getTransition(source), a.getId());
			} else {
					petrinet.addInput(petrinet.getPlace(source), petrinet.getTransition(target), a.getId());
			}
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
}
