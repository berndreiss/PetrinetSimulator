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
import javax.swing.JSplitPane;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;

import datamodel.Petrinet;
import datamodel.PetrinetElement;
import datamodel.PetrinetState;
import datamodel.TransitionFiredListener;
import datamodel.Place;
import datamodel.ReachabilityGraphModel;
import datamodel.PetrinetState;
import datamodel.StateChangeListener;
import datamodel.Transition;
import util.PNMLParser;
import util.PetrinetAnalyser;
import view.PetrinetPanel;
import view.PetrinetGraph;
import view.ReachabilityGraph;
import view.ResizableSplitPane;
import view.GetComponentInterface;
import view.GraphStreamView;

public class PetrinetController {

	private Petrinet petrinet = new Petrinet();
	private PetrinetGraph petrinetGraph;
	private ReachabilityGraph reachabilityGraph;
	private ReachabilityGraphModel reachabilityGraphModel;


	private PetrinetPanel petrinetPanel;

	private File currentFile;

	private boolean fileChanged;
	private boolean headless;
	private boolean analysed;

	private List<String> transitionsToMMarked;
	private String m;
	private String mMarked;


	public PetrinetController(PetrinetPanel petrinetPanel, File file, boolean headless) {
		this.petrinetPanel = petrinetPanel;
		this.petrinetGraph = new PetrinetGraph(this);
		this.headless = headless;

		if (file == null)
			init();
		else
			onFileOpen(file);
	}

	private void init() {
		this.reachabilityGraphModel = new ReachabilityGraphModel(this);

		if (!headless)
			this.reachabilityGraph = new ReachabilityGraph(this);

		petrinet.addPetrinetStateChangedListener(t -> {
			PetrinetState state = reachabilityGraphModel.addNewState(petrinet.getState(), t);
			reachabilityGraphModel.checkIfCurrentStateIsBackwardsValid();
			PetrinetState m = reachabilityGraphModel.getCurrentState().getM();

			if (m != null)
				state.setM(m);

			if (!headless) {
				reachabilityGraph.addState(reachabilityGraphModel.getCurrentState().getState(), t);

				if (m != null) {
					reachabilityGraph.markStatesInvalid(m.getState(), state.getState());
				}
				petrinetPanel.getGraphPane().getRightComponent().repaint();
			}
		});

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

		this.currentFile = file;

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

			petrinetPanel.updateGraphSplitPane();
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
		}
	}

	public void repaint() {
		ResizableSplitPane splitPane = (ResizableSplitPane) petrinetPanel.getGraphPane();
		splitPane.getLeftComponent().repaint();
		splitPane.getRightComponent().repaint();
	}

	public PetrinetPanel getFrame() {
		return petrinetPanel;
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
		}
	}

	public void resetReachabilityGraph() {
		petrinet.reset();
		init();
	}

	public void reachabilityNodeClicked(String id) {
		PetrinetState state = reachabilityGraphModel.getState(id);

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
	}

	public String[] getResults() {
		String[] strings = {"","",""};
		if (!analysed || currentFile == null)
			return strings;
		boolean isValid = reachabilityGraphModel.getInvalidState() == null;
		StringBuilder sb = new StringBuilder();

		sb.append(currentFile.getName());
		
		strings[0] = sb.toString();
		
		sb = new StringBuilder();
		sb.append(isValid?"ja":"nein");
		strings[1] = sb.toString();
		
		sb = new StringBuilder();
		
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

			for (PetrinetState rs : reachabilityGraphModel.getStates()) {

				for (PetrinetState ss : rs.getSuccessors()) {
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
		strings[2] = sb.toString();
		return strings;
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

		PetrinetState invalidState = reachabilityGraphModel.getInvalidState();
		if (invalidState != null) {

			m = invalidState.getM().getState();
			mMarked = invalidState.getState();
			transitionsToMMarked = new ArrayList<String>();

			this.reachabilityGraph = new ReachabilityGraph(this);
			reachabilityGraphModel.reset();

			reachabilityGraph.addState(reachabilityGraphModel.getCurrentState().getState(), null);
			List<PetrinetState> pathToM = invalidState.getListToOtherState(invalidState.getM());

			List<PetrinetState> pathToInitial = invalidState.getM()
					.getListToOtherState(reachabilityGraphModel.getInitialState());
			PetrinetState currentState = reachabilityGraphModel.getCurrentState();

			if (pathToInitial != null) {

				for (int i = pathToInitial.size() - 1; i >= 0; i--) {

					PetrinetState nextState = pathToInitial.get(i);

					Transition transition = currentState.getFirstSuccessorTransition(nextState);

					reachabilityGraph.addState(nextState.getState(), transition);

					reachabilityGraph.setCurrentState(nextState.getPetrinetState());
					currentState = pathToInitial.get(i);
				}

			}

			for (int i = pathToM.size() - 1; i >= 0; i--) {

				PetrinetState nextState = pathToM.get(i);

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

			petrinetPanel.updateGraphSplitPane();

			return;
		}
	}
	
	public boolean getFileChanged() {
		return fileChanged;
	}


}
