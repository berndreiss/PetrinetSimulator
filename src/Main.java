import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;

import org.graphstream.graph.Node;

import datamodel.DuplicateIdException;
import datamodel.Petrinet;
import datamodel.Place;
import datamodel.Transition;
import propra.pnml.PNMLWopedParser;
import util.PNMLParser;
import view.DemoFrame;
import view.MainFrame;
import view.PetrinetPanel;
import view.PetrinetGraph;

public class Main {

	public static void main(String[] args) throws DuplicateIdException {

		System.setProperty("sun.java2d.uiScale", "1.0");

		// Frame erzeugen
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainFrame("Bernd Reiß 3223442");
				
	
			}
		});
		

	
		
		
	}

}
