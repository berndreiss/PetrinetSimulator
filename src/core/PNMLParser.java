package core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.DuplicateIdException;
import exceptions.InvalidEdgeOperationException;
import exceptions.PetrinetException;
import propra.pnml.PNMLWopedParser;

/**
 * The Class PNMLParser.
 */
public class PNMLParser extends PNMLWopedParser {

	private Map<String, Arc> arcs = new HashMap<String, Arc>();
	private List<String> places = new ArrayList<String>();
	private List<String> transitions = new ArrayList<String>();
	private Map<String, Integer> tokensMap = new HashMap<String, Integer>();
	private Petrinet petrinet;

	private boolean idCheck = true;

	private String duplicateArcId = null;

	/**
	 * Instantiates a new PNML parser.
	 *
	 * @param pnml     the pnml file
	 * @param petrinet the petrinet
	 * @throws PetrinetException thrown when adding elements
	 */
	public PNMLParser(File pnml, Petrinet petrinet) throws PetrinetException {
		super(pnml);
		if (petrinet == null)
			this.petrinet = new Petrinet();
		else
			this.petrinet = petrinet;
		this.initParser();

		// get transitions and places in temporary data structure
		this.parse();

		// add places and transitions to petrinet -> done seperately so
		// PetrinetException can be thrown
		for (String s : places)
			petrinet.addPlace(s);
		for (String s : transitions)
			petrinet.addTransition(s);

		idCheck = false;
		this.initParser();
		this.parse();

		// duplicate ids in arcs have to be checked separately as a second arc with the
		// same id would simply overwrite the old one in the map
		if (duplicateArcId != null)
			throw new DuplicateIdException("arc with id \"" + duplicateArcId + "\" already exists.");

		for (String place : tokensMap.keySet())
			petrinet.setTokens(place, tokensMap.get(place));

		handleTransitions();
	}

	/**
	 * New transition.
	 *
	 * @param id the id
	 */
	@Override
	public void newTransition(final String id) {
		if (idCheck)
			transitions.add(id);
	}

	/**
	 * New place.
	 *
	 * @param id the id
	 */
	@Override
	public void newPlace(final String id) {

		if (idCheck)
			places.add(id);
	}

	/**
	 * New arc.
	 *
	 * @param id     the id
	 * @param source the source
	 * @param target the target
	 */
	@Override
	public void newArc(final String id, final String source, final String target) {
		if (idCheck)
			return;

		if (arcs.get(id) != null)
			duplicateArcId = id;

		arcs.put(id, new Arc(id, source, target));
	}

	/**
	 * Sets the position.
	 *
	 * @param id the id
	 * @param x  the x
	 * @param y  the y
	 */
	public void setPosition(final String id, final String x, final String y) {
		if (idCheck)
			return;
		float xF = Float.parseFloat(x);
		float yF = Float.parseFloat(y);

		petrinet.setCoordinates(id, xF, -yF);

	}

	/**
	 * Sets the name.
	 *
	 * @param id   the id
	 * @param name the name
	 */
	@Override
	public void setName(final String id, final String name) {
		if (idCheck)
			return;

		petrinet.setPetrinetElementLabel(id, name);

	}

	/**
	 * Sets the tokens.
	 *
	 * @param id     the id
	 * @param tokens the tokens
	 */
	@Override
	public void setTokens(final String id, final String tokens) {
		if (idCheck)
			return;

		tokensMap.put(id, Integer.parseInt(tokens));
	}

	private void handleTransitions() throws InvalidEdgeOperationException, DuplicateIdException {
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

		public String getSource() {
			return source;
		}

		public String getTarget() {
			return target;
		}
	}

	/**
	 * Gets the petrinet.
	 *
	 * @return the petrinet
	 */
	public Petrinet getPetrinet() {
		return petrinet;
	}
}
