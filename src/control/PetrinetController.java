package control;

import java.awt.BorderLayout;
import java.awt.Button;
import java.io.File;
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
		reachabilityGraph.addState(reachabilityGraphModel.getCurrentStateId(), ""); //add initial state
		petrinet.addPetrinetStateChangedListener(t->
			reachabilityGraph.addState(reachabilityGraphModel.getCurrentStateId(), t==null?"":t.getId())
		);
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
		
		for (Place p: placesChanged) {
			Node place = petrinetGraph.getNode(p.getId());
			place.setAttribute("ui.label", PetrinetGraph.placeTokenLabel(p.getNumberOfTokens()));
			Sprite sprite = petrinetGraph.getSpriteManager().getSprite("s" + p.getId());
			sprite.setAttribute("ui.label", "[" + p.getId() + "] " + p.getName() + " <" + p.getNumberOfTokens() + ">" );
		}
	}
	
	public void onFileOpen(File file) {

		this.petrinet = new Petrinet();
		new PNMLParser(file, this.petrinet);

		init();

		System.out.println(petrinet== null);



		
		Map<String, Transition> transitions = petrinet.getTransitions();
		Map<String, Place> places = petrinet.getPlaces();
		
		for (String s: places.keySet()) {
			petrinetGraph.addPlace(places.get(s));
		}
			
		for (String s: transitions.keySet()) {
			petrinetGraph.addTransition(transitions.get(s));
		}
		mainFrame.updateSplitPane(this);
	}

	public void onAddNode() {
		
		Node node = reachabilityGraph.addNode("1");
		node.setAttribute("x", 261.0);
		node.setAttribute("y", 0.0);
		Node node2 = reachabilityGraph.addNode("2");
		node.setAttribute("x", 261.0);
		node.setAttribute("y", 0.0);
		node2.setAttribute("x", 261.0);
		node2.setAttribute("y", 261.0);
		Node node3 = reachabilityGraph.addNode("3");
		node.setAttribute("x", 261.0);
		node.setAttribute("y", 0.0);
		node3.setAttribute("x", 261.0);
		node3.setAttribute("y", 261.0);

		reachabilityGraph.addEdge("e1", "1", "2");
		reachabilityGraph.addEdge("e2", "1", "3");
		node.setAttribute("x", 261.0);
		node.setAttribute("y", 0.0);

	}
}
