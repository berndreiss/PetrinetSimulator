package control;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import datamodel.Transition;
import util.PNMLParser;
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
	
	private MainFrame mainFrame;
	
	private File currentFile;
	
	private boolean fileChanged;

	
	public PetrinetController(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		this.petrinetGraph = new PetrinetGraph(this);
		init();
	}
	
	private void init() {
		this.reachabilityGraph = new ReachabilityGraph("Default");
		this.reachabilityGraphModel = new ReachabilityGraphModel(this);
		reachabilityGraph.addState(reachabilityGraphModel.getCurrentState(), ""); //add initial state

		petrinet.addPetrinetStateChangedListener(t->{
			ReachabilityState state =  reachabilityGraphModel.addNewState(petrinet.getState());
			reachabilityGraph.addState(reachabilityGraphModel.getCurrentState(), t==null?"":t.getId());
			
			ReachabilityState m = reachabilityGraphModel.checkIfStateIsBackwardsValid(state);
			
			if (m != null) {
				reachabilityGraph.markStatesInvalid(m.getState(), state.getState());
			}
			mainFrame.getGraphPane().getRightComponent().repaint();
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
		return reachabilityGraph; //TODOchange when ready!
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

		this.currentFile = file;
		

		mainFrame.setStatusLabel(file.getName());
		this.petrinet = new Petrinet();
		this.petrinetGraph = new PetrinetGraph(this);
		
		new PNMLParser(file, this.petrinet);

		init();

		Iterator<Transition> transitions = petrinet.getTransitions();
		Iterator<Place> places = petrinet.getPlaces();
		
		
		while(places.hasNext())
			petrinetGraph.addPlace(places.next());

		while(transitions.hasNext())
			petrinetGraph.addTransition(transitions.next());
		
		petrinet.setCurrenStateOriginalState();
		
		mainFrame.updateGraphSplitPane(this);
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
}
