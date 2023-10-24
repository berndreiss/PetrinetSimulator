package view;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import control.PetrinetController;
import datamodel.PetrinetState;
import datamodel.ReachabilityStateChangeListener;
import datamodel.Transition;

public class ReachabilityGraphTest extends MultiGraph {

	private static String CSS_FILE = "url(" + PetrinetGraph.class.getResource("/petrinet_graph.css") + ")";

	private ReplayGraphListener replayGraphListener;

	private SpriteManager spriteMan;

	private Node initialNode;

	private Node currentNode;
	private Edge currentEdge;

	private Node nodeM;

	private Node nodeMMarked;

	private ReachabilityLayout layoutManager;

	public ReachabilityGraphTest(PetrinetController controller) {
		super("");

		// Angabe einer css-Datei für das Layout des Graphen
		this.setAttribute("ui.stylesheet", CSS_FILE);

		// einen SpriteManger für diesen Graphen erzeugen
		spriteMan = new SpriteManager(this);

//		layoutManager = new ReachabilityLayout(spriteMan);

 		Node n1 = addNode("1");
		Node n2 = addNode("2");
		Node n3 = addNode("3");
		Node n4 = addNode("4");
		
		int x = 1;
		
		n1.setAttribute("xy", -x,-x);
		n2.setAttribute("xy", x,-x);
		n3.setAttribute("xy", -x,x);
		n4.setAttribute("xy", x,x);
		
		Node n0 = addNode("0");
		n0.setAttribute("xy", 0,0);

	}
	
	public void replayGraph() {
		
	}

	public void onScreenSizeChanged(Dimension d) {
		
	}
	
}
