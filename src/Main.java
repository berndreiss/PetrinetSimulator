import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;

import org.graphstream.graph.Node;

import datamodel.DuplicateIdException;
import datamodel.Petrinet;
import datamodel.Place;
import datamodel.Transition;
import propra.pnml.PNMLWopedParser;
import util.PNMLParser;
import view.DemoFrame;
import view.DemoGraph;

public class Main {

	public static void main(String[] args) throws DuplicateIdException {

		JFileChooser fileChooser = new JFileChooser();
		
		
		
		
//		Place p0 = new Place("p0", "p0", 0);
//		Place p1 = new Place("p1", "p1", 2);
//		Place p2 = new Place("p2", "p2",1);
//		Place p3 = new Place("p3", "p3",0);
//		Transition t1 = new Transition("t1", "t1");
//		Transition t2 = new Transition("t2", "t2");
//		Transition t3 = new Transition("t3", "t3");

//		Petrinet p = new Petrinet();

//		p.addInput(p0, t1);
//		p.addInput(p1, t1);
//		p.addOutput(p2, t1);
//
//		p.addOutput(p0, t2);
//		
//		p.addInput(p2, t2);
//		p.addOutput(p3, t2);
//
//		p.addInput(p1, t3);
//		p.addOutput(p3, t3);
//
//		p.print();
//


		File file2 = new File("/home/bernd/eclipse-workspace/ProPra-WS23-Basis/Beispiele/110-B1-N01-A00-EineStelleZweiMarken.pnml");
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file2)));
			String line = "";
			while ((line = br.readLine()) != null )
				System.out.println(line);
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(file2.toString());
//		PNMLParser parser = new PNMLParser(file2);

		PNMLWopedParser parser = new PNMLWopedParser(file2);
//		Petrinet p = new Petrinet(parser.getTransitions(), parser.getPlaces(), null);
		Petrinet p = new Petrinet();
		// Graph erzeugen
		DemoGraph graph = new DemoGraph();

		DemoFrame frame	= new DemoFrame("ProPra-WS23-Demo", graph, p);
		
		// Frame erzeugen
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
			}
		});
		
//		fileChooser.showOpenDialog(frame);
//
//		File file = fileChooser.getSelectedFile();

		Map<String, Transition> transitions = p.getTransitions();
		Map<String, Place> places = p.getPlaces();
		
		for (String s: places.keySet()) {
			graph.addPlace(places.get(s));
		}
			
		for (String s: transitions.keySet()) {
			graph.addTransition(transitions.get(s));
		}
		
		p.print();
		
//		Node p0n =  graph.addPlace(p0, 0, 75);
//		Node p1n =  graph.addPlace(p1, 0, 25);
//		Node p2n = graph.addPlace(p2, 50, 50);
//		Node p3n = graph.addPlace(p3, 100, 50);
//		Node t1n = graph.addTransition(t1, 25, 50);
//		Node t2n = graph.addTransition(t2, 75, 50);
//		Node t3n = graph.addTransition(t3, 50, 25);
//		graph.addEdge("0", p0n, t1n);
//		graph.addEdge("1", p1n, t1n);
//		graph.addEdge("2", t1n, p2n);
//		graph.addEdge("3", p2n, t2n);
//		graph.addEdge("4", t2n, p3n);
//		graph.addEdge("5", t2n, p0n);
//		graph.addEdge("6", p1n, t3n);
//		graph.addEdge("7", t3n, p3n);
		
	}

}
