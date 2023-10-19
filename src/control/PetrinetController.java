package control;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.graphstream.algorithm.util.FibonacciHeap.Node;
import org.graphstream.ui.swing_viewer.ViewPanel;

import datamodel.Petrinet;
import datamodel.PetrinetElement;
import datamodel.PetrinetState;
import datamodel.PetrinetStateChangedListener;
import datamodel.Place;
import datamodel.ReachabilityGraphModel;
import datamodel.Transition;
import propra.pnml.PNMLWopedWriter;
import util.Editor;
import util.PNMLParser;
import util.PetrinetAnalyser;
import view.PetrinetGraph;
import view.ReachabilityGraph;
import view.GraphStreamView;

public class PetrinetController {

	private Petrinet petrinet = new Petrinet();
	private PetrinetGraph petrinetGraph;
	private ReachabilityGraph reachabilityGraph;
	private ReachabilityGraphModel reachabilityGraphModel;
	
	private Editor editor;

	private File file;

	private boolean fileChanged;
	private boolean headless;

	private ToolbarMode toolbarMode = ToolbarMode.VIEWER;


	public PetrinetController(File file, boolean headless) {
		this.headless = headless;
		this.file = file;
		this.editor = new Editor(this);
		init();
	}

	private void init() {
		this.petrinet = new Petrinet();
		if (!headless)
			this.petrinetGraph = new PetrinetGraph(petrinet);

		if (file != null)
			new PNMLParser(file, petrinet);
		
		if (!headless) {
		}
		this.reachabilityGraphModel = new ReachabilityGraphModel(petrinet);

		if (!headless) {

			this.reachabilityGraph = new ReachabilityGraph(this);

		}

		petrinet.setPetrinetChangeListener(new PetrinetStateChangedListener() {

			@Override
			public void onTransitionFire(Transition t) {
				reachabilityGraphModel.addNewState(petrinet, t);
			}

			@Override
			public void onComponentChanged(Petrinet petrinet) {
				reachabilityGraphModel.reset();
				PetrinetState initialState = reachabilityGraphModel.getInitialState();
				if (initialState != null)
					reachabilityGraphModel.removeState(initialState);
				reachabilityGraphModel.addNewState(petrinet, null);
			}
		});
		

	}

	public Editor getEditor() {
		return editor;
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

	public ReachabilityGraphModel getReachabilityGraphModel() {
		return reachabilityGraphModel;
	}


	public void clickNodeInGraph(String id) {

		PetrinetElement pe = petrinet.getPetrinetElement(id);

		if (toolbarMode == ToolbarMode.VIEWER) {
			if (pe instanceof Transition)
				petrinet.fireTransition(id);
			if (pe instanceof Place)
				petrinetGraph.toggleNodeMark(pe);
		} 
		if (toolbarMode == ToolbarMode.EDITOR)
			editor.clickedNodeInGraph(pe);
	}

	public void resetPetrinet() {
		petrinet.setState(reachabilityGraphModel.getInitialState());
		reachabilityGraphModel.setInitial();
	}

	public Object closeCurrent() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean incrementMarkedPlace() {

		
		boolean changed = petrinet.incrementPlace(petrinetGraph.getMarkedNode());

		if (changed && !fileChanged) {
			fileChanged = true;
		}
		
		return changed;
	}

	public boolean decrementMarkedPlace() {
		boolean changed = petrinet.decrementPlace(petrinetGraph.getMarkedNode());

		if (changed && !fileChanged) {
			fileChanged = true;
		}
		
		return changed;
	}

	public File getCurrentFile() {
		return file;
	}

	public void resetReachabilityGraph() {
		petrinet.setState(reachabilityGraphModel.getInitialState());
		reachabilityGraphModel.reset();
	}

	public void reachabilityNodeClicked(String id) {
		PetrinetState state = reachabilityGraphModel.getState(id);
		petrinet.setState(state);
		reachabilityGraphModel.setCurrentState(state);
	}

	public String[] analyse() {

		PetrinetAnalyser analyser = new PetrinetAnalyser(this);

		if (!headless)
			reachabilityGraph.analysisCompleted();
		
		return getResults(analyser);


		
	}

	private String[] getResults(PetrinetAnalyser analyser) {
		String[] strings = { "", "", "" };

		if (file == null)
			return strings;

		StringBuilder sb = new StringBuilder();

		sb.append(file.getName() + " ");
		strings[0] = sb.toString();

		sb = new StringBuilder();
		sb.append(analyser.isFinite() ? " yes" : " no");
		strings[1] = sb.toString();

		sb = new StringBuilder();

		if (!analyser.isFinite()) {
			sb.append(" " + analyser.getTransitionsToMMarked().size());
			sb.append(": (");
			for (String s : analyser.getTransitionsToMMarked())
				sb.append(s + ",");
			sb.deleteCharAt(sb.length() - 1);
			sb.append(");");

			sb.append(" ");
			sb.append(analyser.getM());
			sb.append(" ");
			sb.append(analyser.getMMarked());

		} else {

			sb.append(" " + analyser.getStateCount() + " / " + analyser.getEdgeCount());
		}
		strings[2] = sb.toString();
		return strings;
	}

	public boolean getHeadless() {
		return headless;
	}

	public boolean getFileChanged() {
		return fileChanged;
	}

	
	public ToolbarMode getToolbarMode() {
		
		return toolbarMode;
	}
	

	public void setToolbarMode(ToolbarMode toolbarMode) {
		if (toolbarMode == ToolbarMode.EDITOR)
			resetReachabilityGraph();
		if (toolbarMode == ToolbarMode.VIEWER) {
			PetrinetElement toggledElement = petrinetGraph.getMarkedNode();
			if (toggledElement != null && toggledElement instanceof Transition)
				petrinetGraph.toggleNodeMark(toggledElement);
		}
		this.toolbarMode = toolbarMode;
	}

	

	public void writeToFile() {
		writeToFile(getCurrentFile());
	}
	
	public void writeToFile(File file) {

		PNMLWopedWriter writer = new PNMLWopedWriter(file);
		writer.startXMLDocument();

		for (Place p: petrinet.getPlaces())
			writer.addPlace(p.getId(), p.getName(), String.valueOf(p.getX()), String.valueOf(-p.getY()), String.valueOf(p.getNumberOfTokens()));
		for (Transition t: petrinet.getTransitions()) {
			writer.addTransition(t.getId(), t.getName(), String.valueOf(t.getX()), String.valueOf(-t.getY()));
			for (Place p: t.getInputs()) 
				writer.addArc(petrinet.getOriginalArcId(p.getId()+t.getId()), p.getId(), t.getId());
			for (Place p: t.getOutputs()) 
				writer.addArc(petrinet.getOriginalArcId(t.getId()+p.getId()), t.getId(),p.getId());
		}
		
		writer.finishXMLDocument();
		
		this.file = file;
			
		fileChanged = false;
		
		
		
	}

	public void mergeWith(File file) {
		if (file != null)
			new PNMLParser(file, this.petrinet);
		
	}

	public void setFileChanged(boolean changed) {
		fileChanged = changed;
	}

	public void setLabel(String label) {
		PetrinetElement markedNode = petrinetGraph.getMarkedNode();
		if (markedNode == null)
			return;
		
		if (markedNode.getName().equals(label))
			return;
		
		petrinet.setPetrinetElementName(markedNode.getId(), label);
	
		setFileChanged(true);
	}
	

}
