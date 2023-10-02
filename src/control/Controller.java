package control;

import java.awt.BorderLayout;
import java.awt.Button;
import java.io.File;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.graphstream.graph.Node;

import datamodel.Petrinet;
import datamodel.Place;
import datamodel.Transition;
import util.PNMLParser;
import view.MainFrame;
import view.PetrinetGraph;
import view.GraphStreamView;

public class Controller {

	private Petrinet petrinet;
	private PetrinetGraph petrinetGraph;
	
	private MainFrame mainFrame;

	public Controller(MainFrame mainFrame, Petrinet petrinet, PetrinetGraph petrinetGraph) {
		this.mainFrame = mainFrame;
		this.petrinet = petrinet;
		this.petrinetGraph = petrinetGraph;
	}
	
	public Petrinet getPetrinet() {
		return petrinet;
	}
	
	public PetrinetGraph getGraph() {
		return petrinetGraph;
	}
	
	public void clickNodeInGraph(String id) {
		List<Place> placesChanged = petrinet.activate(id);
		
		for (Place p: placesChanged) {
			Node place = petrinetGraph.getNode(p.getId());
			place.setAttribute("ui.label", p.numberOfTokens() == 0?"":p.numberOfTokens());
		}
	}
	
	public void onFileOpen(File file) {
		PNMLParser parser = new PNMLParser(file);
		petrinet = new Petrinet();
		petrinetGraph = new PetrinetGraph();
		
		petrinet.setTransitions(parser.getTransitions());
		petrinet.setPlaces(parser.getPlaces());

		Map<String, Transition> transitions = petrinet.getTransitions();
		Map<String, Place> places = petrinet.getPlaces();
		
		for (String s: places.keySet()) {
			petrinetGraph.addPlace(places.get(s));
		}
			
		for (String s: transitions.keySet()) {
			petrinetGraph.addTransition(transitions.get(s));
		}
		
		
//		petrinetPanel.add(new JButton(),BorderLayout.CENTER);
		mainFrame.updateSplitPane(this);
	}

}
