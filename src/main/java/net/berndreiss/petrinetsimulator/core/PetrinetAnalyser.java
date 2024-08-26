package net.berndreiss.petrinetsimulator.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.berndreiss.petrinetsimulator.control.PetrinetViewerController;
import net.berndreiss.petrinetsimulator.exceptions.PetrinetException;

/**
 * <p>
 * Class holding methods for analyzing a petrinet and returning results..
 * </p>
 * 
 * <p>
 * The petrinet can either be passed as a pnml file or as an instance of
 * {@link PetrinetViewerController}. The former also modifies the reachability
 * graph in the provided instance.
 * </p>
 */
public class PetrinetAnalyser {

	/** The controller used for simulating the petrinet. */
	private PetrinetViewerController controller;

	/** The petrinet itself. */
	private Petrinet petrinet;

	/** The reachability graph model. */
	private ReachabilityGraphModel reachabilityGraphModel;

	/** True if petrinet is bounded. */
	private boolean bounded = true;

	/** Number of edges in the reachability graph. */
	private int numberOfEdges;
	/** Number of nodes in the reachability graph. */
	private int numberOfNodes;

	/** Detected path proving petrinet is unbounded. */
	private List<String> transitionsToMMarked;
	/** The first node in the path above. */
	private String m;
	/** The last node in the path above. */
	private String mMarked;

	/**
	 * Instantiates a new petrinet analyser.
	 *
	 * @param file the file to be analyzed
	 * @throws PetrinetException thrown if file contains an invalid petrinet
	 *                           structure
	 */
	public PetrinetAnalyser(File file) throws PetrinetException {
		this(new PetrinetViewerController(file, null, null));
	}

	/**
	 * Instantiates a new petrinet analyser.
	 *
	 * @param controller the controller containing the petrinet to be analyzed and
	 *                   the reachability graph to show results
	 */
	public PetrinetAnalyser(PetrinetViewerController controller) {
		this.controller = controller;
		reachabilityGraphModel = controller.getReachabilityGraphModel();

		// make all steps in the analysis skippable (meaning that the analysis is
		// handled as one action in the un-/redo queue)
		reachabilityGraphModel.setSkippableMode(true);
		analyze();

		// if petrinet is not bounded reset reachability graph build path proving
		// unboundedness and set beginning and end point markers of the path
		if (!bounded) {
			updateReachabilityGraph();
			m = reachabilityGraphModel.getLastStateOnUnboundednessPath().getM().getState();
			mMarked = reachabilityGraphModel.getLastStateOnUnboundednessPath().getState();

		}

		// reset reachability graph model to normal mode
		reachabilityGraphModel.setSkippableMode(false);

		// reset petrinet
		controller.resetPetrinet();

	}

	// analyze the given petrinet
	private void analyze() {

		petrinet = controller.getPetrinet();
		reachabilityGraphModel.setInitial();
		Set<PetrinetState> visited = new HashSet<PetrinetState>();
		analyseState(reachabilityGraphModel.getCurrentPetrinetState(), visited);

	}

	// from the initial state build reachability graph step by step -> in each step
	// the function is called recursively for every activated transition in the
	// state the petrinet is in; if a state has already been visited return
	private void analyseState(PetrinetState state, Set<PetrinetState> visited) {

		// if bounded state has been found before abort
		if (!bounded)
			return;

		// return if state has been visited
		if (visited.contains(state))
			return;

		// increment number of nodes in the reachability state -> number of states in
		// the petrinet == number of nodes in the reachability graph
		numberOfNodes++;

		visited.add(state);
		petrinet.setState(state);

		// fire every activated transition in the given state, check whether the state
		// is bounded and if so call function recursively for new state
		for (Transition t : petrinet.getActivatedTransitions()) {

			// fire transition
			petrinet.fireTransition(t.getId());

			// check whether new state is bounded and abort analysis if not
			boolean stateBounded = controller.getReachabilityGraphModel().checkIfCurrentStateIsBounded();
			if (!stateBounded) {
				bounded = false;
				return;

			}

			// increment number of edges
			numberOfEdges++;

			// analyze new state
			analyseState(reachabilityGraphModel.getCurrentPetrinetState(), visited);

			// reset reachability graph model and petrinet
			reachabilityGraphModel.setCurrentState(state);
			petrinet.setState(state);

		}

	}

	/**
	 * Gets boundedness.
	 *
	 * @return true, if petrinet is bounded
	 */
	public boolean isBounded() {
		return bounded;
	}

	/**
	 * Gets the count of states (nodes) in the reachability graph.
	 *
	 * @return the state count of the reachability graph
	 */
	public int getStateCount() {
		return numberOfNodes;
	}

	/**
	 * Gets the number of edges in the reachability graph.
	 *
	 * @return the edge count
	 */
	public int getEdgeCount() {
		return numberOfEdges;
	}

	/**
	 * Gets the first node on the path proving unboudedness.
	 *
	 * @return the m
	 */
	public String getM() {
		return m;
	}

	/**
	 * Gets the last node on the path proving unboudedness
	 *
	 * @return the m'
	 */
	public String getMMarked() {
		return mMarked;
	}

	/**
	 * Gets the transitions on the path proving unboudedness.
	 *
	 * @return the transitions on path to m'
	 */
	public List<String> getTransitionsToMMarked() {
		return transitionsToMMarked;
	}

	// reset the petrinet; if it is unbounded reset reachability graph and build
	// path proving unboudedness
	private void updateReachabilityGraph() {

		controller.resetPetrinet();

		PetrinetState invalidState = reachabilityGraphModel.getLastStateOnUnboundednessPath();

		if (invalidState != null) {

			m = invalidState.getM().getState();
			mMarked = invalidState.getState();
			transitionsToMMarked = new ArrayList<String>();

			// get path from initial state to m
			List<PetrinetState> pathToM = invalidState.getM()
					.getPathFromOtherState(reachabilityGraphModel.getInitialState());

			// get path from m to m'
			List<PetrinetState> pathToMMarked = invalidState.getPathFromOtherState(invalidState.getM());

			// keeping track of current and initial state
			PetrinetState currentState = reachabilityGraphModel.getInitialState();
			PetrinetState initialState = reachabilityGraphModel.getInitialState();

			// keep track of transitions being used
			ArrayList<Transition> transitionList = new ArrayList<Transition>();

			// keep track of current transition being used
			Transition transition;

			// get all transitions on the path to m
			if (pathToM != null) {
				for (PetrinetState nextState : pathToM) {

					// since there can be multiple transitions leading from one state to the other
					// simply choose the first one
					transition = currentState.getFirstSuccessorTransition(nextState);
					transitionList.add(transition);
					transitionsToMMarked.add(transition.getId());
					currentState = nextState;
				}

			}

			// get transition from current state to m (if m and initial state are not the
			// same)
			if (initialState != invalidState.getM()) {
				transition = currentState.getFirstSuccessorTransition(invalidState.getM());
				transitionList.add(transition);
				transitionsToMMarked.add(transition.getId());
				currentState = invalidState.getM();
			}

			// get all transitions on the path to m'
			if (pathToMMarked != null) {
				for (PetrinetState nextState : pathToMMarked) {
					transition = currentState.getFirstSuccessorTransition(nextState);
					transitionsToMMarked.add(transition.getId());
					transitionList.add(transition);
					currentState = nextState;
				}
			}
			// add the transition from the current state to m'
			transition = currentState.getFirstSuccessorTransition(invalidState);
			if (transition != null)
				transitionsToMMarked.add(transition.getId());
			transitionList.add(transition);

			// reset the reachability graph and build path
			reachabilityGraphModel.reset();
			for (Transition t : transitionList) {
				if (t != null)
					petrinet.fireTransition(t.getId());
			}
			return;
		}
	}

	/**
	 * Gets the results of the analysis as {[File], [Bounded?], [Nodes/Edges -- Path
	 * length; m, m']}.
	 *
	 * @return the results
	 */
	public String[] getResults() {
		String[] strings = { "", "", "" };

		File file = controller.getCurrentFile();

		if (file == null)
			return strings;

		// File
		StringBuilder sb = new StringBuilder();
		sb.append(file.getName() + " ");
		strings[0] = sb.toString();

		// Bounded?
		sb = new StringBuilder();
		sb.append(isBounded() ? " yes" : " no");
		strings[1] = sb.toString();

		// Nodes/Edges -- Path length; m, m'
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
