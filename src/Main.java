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
import javax.swing.filechooser.FileFilter;

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

		Petrinet p = new Petrinet();
		// Graph erzeugen
		DemoGraph graph = new DemoGraph();

		
		JFileChooser fileChooser = new JFileChooser();
		
		fileChooser.setFileFilter(new FileFilter() {
			
			@Override
			public String getDescription() {
				return "PNML files (.pnml)";
			}
			
			@Override
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				
				return f.getName().endsWith(".pnml");
			}
		});
		
		fileChooser.setCurrentDirectory(new File("/home/bernd/eclipse-workspace/ProPra-WS23-Basis/Beispiele/"));



		// Frame erzeugen
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DemoFrame frame	= new DemoFrame("ProPra-WS23-Demo", graph,p);

				int result = fileChooser.showOpenDialog(frame);
				File file = fileChooser.getSelectedFile();

				PNMLParser parser = new PNMLParser(file);
				p.setTransitions(parser.getTransitions());
				p.setPlaces(parser.getPlaces());

				Map<String, Transition> transitions = p.getTransitions();
				Map<String, Place> places = p.getPlaces();
				
				for (String s: places.keySet()) {
					graph.addPlace(places.get(s));
				}
					
				for (String s: transitions.keySet()) {
					graph.addTransition(transitions.get(s));
				}
				
				p.print();

			}
		});
		

	
		
		
	}

}
