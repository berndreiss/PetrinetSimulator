package core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import control.PetrinetController;
import exceptions.PetrinetException;

// TODO: Auto-generated Javadoc
/**
 * The Class PetrinetAnalyser.
 */
public class PetrinetAnalyser {

	private PetrinetController controller;

	private Petrinet petrinet;

	private ReachabilityGraphModel reachabilityGraphModel;

	private boolean bounded = true;

	private int edges;
	private int nodes;

	private List<String> transitionsToMMarked;
	private String m;
	private String mMarked;

	
	/**
	 * Instantiates a new petrinet analyser.
	 *
	 * @param file the file
	 * @throws PetrinetException the petrinet exception
	 */
	public PetrinetAnalyser(File file) throws PetrinetException{
		this.controller = new PetrinetController(file, true);
	}
	
	/**
	 * Instantiates a new petrinet analyser.
	 *
	 * @param controller the controller
	 */
	public PetrinetAnalyser(PetrinetController controller) {
		this.controller = controller;
		reachabilityGraphModel = controller.getReachabilityGraphModel();

		analyse();
		if (!bounded) {
			updateReachabilityGraph();
			m = reachabilityGraphModel.getInvalidState().getM().getState();
			mMarked = reachabilityGraphModel.getInvalidState().getState();
			
		}
		controller.resetPetrinet();

	}

	private void analyse() {

		reachabilityGraphModel.reset();
		petrinet = controller.getPetrinet();


		Set<PetrinetState> visited = new HashSet<PetrinetState>();
		analyseState(reachabilityGraphModel.getCurrentPetrinetState(), visited);

	}

	private void analyseState(PetrinetState state, Set<PetrinetState> visited) {

		if (visited.contains(state))
			return;
		nodes++;

		visited.add(state);
		petrinet.setState(state);

		for (Transition t : petrinet.getActiveTransitions()) {
			petrinet.fireTransition(t.getId());
			
			boolean stateValid = controller.getReachabilityGraphModel().checkIfCurrentStateIsBackwardsValid();
			if (!stateValid) {
				bounded = false;
				return;

			}
			edges++;
			analyseState(reachabilityGraphModel.getCurrentPetrinetState(), visited);
			reachabilityGraphModel.setCurrentState(state);
			petrinet.setState(state);

		}

	}

	/**
	 * Checks if is bounded.
	 *
	 * @return true, if is bounded
	 */
	public boolean isBounded() {
		return bounded;
	}

	/**
	 * Gets the state count.
	 *
	 * @return the state count
	 */
	public int getStateCount() {
		return nodes;
	}

	/**
	 * Gets the edge count.
	 *
	 * @return the edge count
	 */
	public int getEdgeCount() {
		return edges;
	}

	/**
	 * Gets the m.
	 *
	 * @return the m
	 */
	public String getM() {
		return m;
	}

	/**
	 * Gets the m marked.
	 *
	 * @return the m marked
	 */
	public String getMMarked() {
		return mMarked;
	}

	/**
	 * Gets the transitions to M marked.
	 *
	 * @return the transitions to M marked
	 */
	public List<String> getTransitionsToMMarked() {
		return transitionsToMMarked;
	}

	private void updateReachabilityGraph() {

		PetrinetState invalidState = reachabilityGraphModel.getInvalidState();

		if (invalidState != null) {

			m = invalidState.getM().getState();
			mMarked = invalidState.getState();
			transitionsToMMarked = new ArrayList<String>();

			List<PetrinetState> pathToM = invalidState.getPathToOtherState(invalidState.getM());

			List<PetrinetState> pathToInitial = invalidState.getM()
					.getPathToOtherState(reachabilityGraphModel.getInitialState());
			PetrinetState currentState = reachabilityGraphModel.getInitialState();
			PetrinetState initialState = reachabilityGraphModel.getInitialState();

			ArrayList<Transition> transitionList = new ArrayList<Transition>();

			Transition transition;

			if (pathToInitial != null) {
				for (PetrinetState nextState : pathToInitial) {

					transition = currentState.getFirstSuccessorTransition(nextState);

					transitionList.add(transition);

					currentState = nextState;
				}

			}

			if (initialState != invalidState.getM()) {
				transition = currentState.getFirstSuccessorTransition(invalidState.getM());

				transitionList.add(transition);

				currentState = invalidState.getM();
			}
			if (pathToM != null) {
				for (PetrinetState nextState : pathToM) {

					transition = currentState.getFirstSuccessorTransition(nextState);
					transitionsToMMarked.add(transition.getId());

					transitionList.add(transition);

					currentState = nextState;
				}
			}
			transition = currentState.getFirstSuccessorTransition(invalidState);

			if (transition != null)
				transitionsToMMarked.add(transition.getId());

			transitionList.add(transition);

			reachabilityGraphModel.reset();

			for (Transition t : transitionList) {
				if (t != null)
					petrinet.fireTransition(t.getId());
			}
			return;
		}
		controller.resetPetrinet();

	}

	/**
	 * Gets the results.
	 *
	 * @return the results
	 */
	public String[] getResults() {
		String[] strings = { "", "", "" };

		File file = controller.getCurrentFile();
		
		if (file == null)
			return strings;

		StringBuilder sb = new StringBuilder();

		sb.append(file.getName() + " ");
		strings[0] = sb.toString();

		sb = new StringBuilder();
		sb.append(isBounded() ? " yes" : " no");
		strings[1] = sb.toString();

		sb = new StringBuilder();

		if (!isBounded()) {
			sb.append(" " + getTransitionsToMMarked().size());
			sb.append(": (");
			for (String s : getTransitionsToMMarked())
				sb.append(s + ",");
			sb.deleteCharAt(sb.length() - 1);
			sb.append(");");

			sb.append(" ");
			sb.append(getM());
			sb.append(", ");
			sb.append(getMMarked());

		} else {

			sb.append(" " + getStateCount() + " / " + getEdgeCount());
		}
		strings[2] = sb.toString();
		return strings;
	}


}
