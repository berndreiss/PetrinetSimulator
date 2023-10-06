package control;

import java.awt.BorderLayout;
import java.awt.Button;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;

import datamodel.Petrinet;
import datamodel.PetrinetStateChangedListener;
import datamodel.Place;
import datamodel.ReachabilityGraphModel;
import datamodel.ReachabilityState;
import datamodel.Transition;
import util.PNMLParser;
import view.MainFrame;
import view.PetrinetGraph;
import view.ReachabilityGraph;
import view.GraphStreamView;

public class PetrinetController {

	private Petrinet petrinet = new Petrinet();
	private PetrinetGraph petrinetGraph;
	private ReachabilityGraph reachabilityGraph;
	private ReachabilityGraphModel reachabilityGraphModel;
	
	private MainFrame mainFrame;

	
	public PetrinetController(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		init();
	}
	
	private void init() {
		this.petrinetGraph = new PetrinetGraph(this);
		this.reachabilityGraph = new ReachabilityGraph("Default");
		this.reachabilityGraphModel = new ReachabilityGraphModel(this);
		reachabilityGraph.addState(reachabilityGraphModel.getCurrentState(), ""); //add initial state
		petrinet.addPetrinetStateChangedListener(t->{
			ReachabilityState state =  reachabilityGraphModel.addNewState(petrinet);
			reachabilityGraph.addState(reachabilityGraphModel.getCurrentState(), t==null?"":t.getId());
			
			ReachabilityState m = reachabilityGraphModel.checkIfStateIsBackwardsValid(state);
			
			if (m != null) {
				reachabilityGraph.markStatesInvalid(m.getState(), state.getState());
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
		return reachabilityGraph; //TODOchange when ready!
	}
	
	public void clickNodeInGraph(String id) {
		List<Place> placesChanged = petrinet.activate(id);
		
	}
	
	public void onFileOpen(File file) {

		this.petrinet = new Petrinet();
		new PNMLParser(file, this.petrinet);

		init();

		System.out.println(petrinet== null);



		
		Iterator<Transition> transitions = petrinet.getTransitions();
		Iterator<Place> places = petrinet.getPlaces();
		
		
		while(places.hasNext())
			petrinetGraph.addPlace(places.next());

		while(transitions.hasNext())
			petrinetGraph.addTransition(transitions.next());
		mainFrame.updateSplitPane(this);
	}

		

}
