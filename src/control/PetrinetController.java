package control;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.print.DocFlavor.READER;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;

import datamodel.Petrinet;
import datamodel.PetrinetElement;
import datamodel.PetrinetState;
import datamodel.TransitionFiredListener;
import datamodel.Place;
import datamodel.ReachabilityGraphModel;
import datamodel.ReachabilityState;
import datamodel.ReachabilityStateChangeListener;
import datamodel.Transition;
import util.PNMLParser;
import util.PetrinetAnalyser;
import view.MainFrame;
import view.PetrinetGraph;
import view.ReachabilityGraph;
import view.ResizableSplitPane;
import view.GraphStreamView;

public class PetrinetController {

	private Petrinet petrinet = new Petrinet();
	private PetrinetGraph petrinetGraph;
	private ReachabilityGraph reachabilityGraph;
	private ReachabilityGraphModel reachabilityGraphModel;

	private InvalidStateEncounteredListener invalidStateEncounteredListener;

	private MainFrame mainFrame;

	private File currentFile;

	private boolean fileChanged;
	private boolean headless;
	private boolean analysed;

	private List<String> transitionsToMMarked;
	private String m;
	private String mMarked;

	public PetrinetController(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		this.petrinetGraph = new PetrinetGraph(this);
		init();
	}

	private void init() {
		this.reachabilityGraphModel = new ReachabilityGraphModel(this);

		reachabilityGraphModel.setReachabilityStateChangeListener(new ReachabilityStateChangeListener() {

			@Override
			public void onChange(ReachabilityState state) {
			}

			@Override
			public void onAdd(ReachabilityState state) {
				if (!headless)
					mainFrame.repaintGraphs(1);
			}
		});

		if (!headless)
			this.reachabilityGraph = new ReachabilityGraph(this);

		if (petrinet.getState().placeTokensSize() > 0)
			reachabilityGraph.addState(reachabilityGraphModel.getCurrentState().getState(), null); // add initial state

		petrinet.addPetrinetStateChangedListener(t -> {
			ReachabilityState state = reachabilityGraphModel.addNewState(petrinet.getState(), t);
			reachabilityGraphModel.checkIfCurrentStateIsBackwardsValid();
			ReachabilityState m = reachabilityGraphModel.getCurrentState().getM();

			if (m != null)
				state.setM(m);

			if (!headless) {
				reachabilityGraph.addState(reachabilityGraphModel.getCurrentState().getState(), t);

				if (m != null) {
					reachabilityGraph.markStatesInvalid(m.getState(), state.getState());
				}
				mainFrame.getGraphPane().getRightComponent().repaint();
			}
		});
		mainFrame.updateGraphSplitPane(this);

	}

	public Petrinet getPetrinet() {
		return petrinet;
	}

	public PetrinetGraph getPetrinetGraph() {
		return petrinetGraph;
	}

	public ReachabilityGraph getReachabilityGraph() {
		return reachabilityGraph; // TODOchange when ready!
	}

	public void reload() {
		if (currentFile != null)
			onFileOpen(currentFile);
	}

	public void clickNodeInGraph(String id) {

		PetrinetElement pe = petrinet.getPetrinetElement(id);

		if (pe instanceof Transition)
			petrinet.fireTransition(id);
		if (pe instanceof Place)
			petrinetGraph.toggleNodeMark(id);
	}

	public void onFileOpen(File file) {

		if (file == null)
			return;

		mainFrame.clearText();
		this.currentFile = file;

		mainFrame.setStatusLabel(file.getName());
		this.petrinet = new Petrinet();

		new PNMLParser(file, this.petrinet);

		init();

		petrinet.setCurrenStateOriginalState();

		if (!headless) {
			this.petrinetGraph = new PetrinetGraph(this);

			for (Place p : petrinet.getPlaces())
				petrinetGraph.addPlace(p);

			for (Transition t : petrinet.getTransitions())
				petrinetGraph.addTransition(t);

			mainFrame.updateGraphSplitPane(this);
		}
	}

	public void resetPetrinet() {
		petrinet.reset();

		PetrinetState state = petrinet.getState();
		reachabilityGraph.setCurrentState(state);
		reachabilityGraphModel.setCurrentState(state);
	}

	public Object closeCurrent() {
		// TODO Auto-generated method stub
		return null;
	}

	public void incrementPlace(String markedPlace) {
		petrinet.incrementPlace(markedPlace);
		resetReachabilityGraph();
		if (!fileChanged) {
			fileChanged = true;
			mainFrame.setStatusLabel("*" + currentFile.getName());
		}
	}

	public void repaint() {
		ResizableSplitPane splitPane = (ResizableSplitPane) mainFrame.getGraphPane();
		splitPane.getLeftComponent().repaint();
		splitPane.getRightComponent().repaint();
	}

	public MainFrame getFrame() {
		return mainFrame;
	}

	public File getCurrentFile() {
		return currentFile;
	}

	public void decrementPlace(String markedPlace) {
		petrinet.decrementPlace(markedPlace);
		if (!headless)
			resetReachabilityGraph();

		if (!fileChanged) {
			fileChanged = true;
			mainFrame.setStatusLabel("*" + currentFile.getName());
		}
	}

	public void resetReachabilityGraph() {
		petrinet.reset();
		init();
	}

	public void reachabilityNodeClicked(String id) {
		ReachabilityState state = reachabilityGraphModel.getState(id);

		PetrinetState newCurrentState = state.getPetrinetState();
		petrinet.setState(newCurrentState);
		reachabilityGraphModel.setCurrentState(newCurrentState);
		reachabilityGraph.setCurrentState(newCurrentState);
	}

	public void analyse() {
		resetReachabilityGraph();
		PetrinetAnalyser analyser = new PetrinetAnalyser(this);
		analyser.analyse();
		this.analysed = true;
		mainFrame.print(getResults());
	}

	public String getResults() {
		if (!analysed)
			return "";
		boolean isValid = reachabilityGraphModel.getInvalidState() == null;
		StringBuilder sb = new StringBuilder();

		sb.append(currentFile.getName());
		sb.append(" | ");
		sb.append(isValid?"ja":"nein");
		sb.append(" | ");
		if (!isValid) {
			sb.append(transitionsToMMarked.size());
			sb.append(": (");
			for (String s : transitionsToMMarked)
				sb.append(s + ",");
			sb.deleteCharAt(sb.length() - 1);
			sb.append(");");

			sb.append(" ");
			sb.append(m);
			sb.append(" ");
			sb.append(mMarked);

		} else {

			int stateCount = 0;
			int edgeCount = 0;

			Set<String> edges = new HashSet<String>();

			for (ReachabilityState rs : reachabilityGraphModel.getStates()) {

				for (ReachabilityState ss : rs.getSuccessors()) {
					for (Transition t : rs.getSuccessorTransitions(ss)) {
						String edgesString = rs.getState() + ss.getState() + t.getId();
						if (!edges.contains(edgesString)) {
							edges.add(edgesString);
							edgeCount++;
						}
					}
				}

				stateCount++;
			}
			sb.append(stateCount + " / " + edgeCount);
		}

		return sb.toString();
	}

	public boolean stateIsValid() {
		return reachabilityGraphModel.getCurrentState().getM() == null;
	}

	public void setState(PetrinetState petrinetState) {

		petrinet.setState(petrinetState);
		PetrinetState state = petrinet.getState();
		reachabilityGraph.setCurrentState(state);
		reachabilityGraphModel.setCurrentState(state);

	}

	public void setHeadless(boolean headless) {
		this.headless = headless;

	}

	public boolean getHeadless() {
		return headless;
	}

	public void updateReachabilityGraph() {

		ReachabilityState invalidState = reachabilityGraphModel.getInvalidState();
		if (invalidState != null) {

			m = invalidState.getM().getState();
			mMarked = invalidState.getState();
			transitionsToMMarked = new ArrayList<String>();

			this.reachabilityGraph = new ReachabilityGraph(this);
			reachabilityGraphModel.reset();

			reachabilityGraph.addState(reachabilityGraphModel.getCurrentState().getState(), null);
			List<ReachabilityState> pathToM = invalidState.getListToOtherState(invalidState.getM());

			List<ReachabilityState> pathToInitial = invalidState.getM()
					.getListToOtherState(reachabilityGraphModel.getInitialState());
			ReachabilityState currentState = reachabilityGraphModel.getCurrentState();

			if (pathToInitial != null) {

				for (int i = pathToInitial.size() - 1; i >= 0; i--) {

					ReachabilityState nextState = pathToInitial.get(i);

					Transition transition = currentState.getFirstSuccessorTransition(nextState);

					reachabilityGraph.addState(nextState.getState(), transition);

					reachabilityGraph.setCurrentState(nextState.getPetrinetState());
					currentState = pathToInitial.get(i);
				}

			}

			for (int i = pathToM.size() - 1; i >= 0; i--) {

				ReachabilityState nextState = pathToM.get(i);

				Transition transition = currentState.getFirstSuccessorTransition(nextState);
				transitionsToMMarked.add(transition.getId());
				reachabilityGraph.addState(nextState.getState(), transition);

				reachabilityGraph.setCurrentState(nextState.getPetrinetState());
				currentState = pathToM.get(i);
			}

			Transition transition = currentState.getFirstSuccessorTransition(invalidState);

			if (transition != null)
				transitionsToMMarked.add(transition.getId());

			reachabilityGraph.addState(invalidState.getState(), transition);

			reachabilityGraph.markStatesInvalid(invalidState.getM().getState(), invalidState.getState());
			reachabilityGraphModel.reset();
			reachabilityGraph.setCurrentState(reachabilityGraphModel.getCurrentPetrinetState());

			mainFrame.updateGraphSplitPane(this);

			return;
		}
	}

}
