import org.graphstream.graph.Node;

import datamodel.DuplicateIdException;
import datamodel.Petrinet;
import datamodel.Place;
import datamodel.Transition;
import view.DemoFrame;
import view.DemoGraph;

public class Main {

	public static void main(String[] args) throws DuplicateIdException {

		Place p1 = new Place("p1", "p1", 1);
		Place p2 = new Place("p2", "p2",1);
		Place p3 = new Place("p3", "p3",0);
		Transition t1 = new Transition("t1", "t1");
		Transition t2 = new Transition("t2", "t2");

		Petrinet p = new Petrinet();

		p.addInput(p1, t1);
		p.addOutput(p2, t1);

		p.addInput(p2, t2);
		p.addOutput(p3, t2);

		p.print();


		// Graph erzeugen
		DemoGraph graph = new DemoGraph();

		// Frame erzeugen
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new DemoFrame("ProPra-WS23-Demo", graph, p);
			}
		});
		
		Node p1n =  graph.addPlace(p1, 0, 50);
		Node p2n = graph.addPlace(p2, 50, 50);
		Node p3n = graph.addPlace(p3, 100, 50);
		Node t1n = graph.addTransition(t1, 25, 50);
		Node t2n = graph.addTransition(t2, 75, 50);
		graph.addEdge("1", p1n, t1n);
		graph.addEdge("2", t1n, p2n);
		graph.addEdge("3", p2n, t2n);
		graph.addEdge("4", t2n, p3n);
	}

}
