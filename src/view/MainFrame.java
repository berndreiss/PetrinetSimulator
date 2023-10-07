package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import org.graphstream.ui.swing_viewer.ViewPanel;

import control.PetrinetController;
import datamodel.Petrinet;

public class MainFrame extends JFrame {

	private ResizableSplitPane graphSplitPane;
	private ResizableSplitPane splitPane;
	
	private JTextArea textArea;
	
	private PetrinetController controller;
	private JLabel statusLabel;

	
	public MainFrame(String title) {
		super(title);

		textArea = new JTextArea();
		
		splitPane = new ResizableSplitPane(this, JSplitPane.VERTICAL_SPLIT);
		
		this.controller = new PetrinetController(this);

		updateGraphSplitPane(controller);
		
		splitPane.setGetComponentInterface(new GetComponentInterface() {
			
			@Override
			public Component getRightComponent() {
				return textArea;
			}
			
			@Override
			public Component getLeftComponent() {
				return graphSplitPane;
			}
		});
		
		
		JMenuBar menuBar = new PetrinetMenu(this, controller);
		
		
		this.setJMenuBar(menuBar);
		
		JToolBar toolbar = new PetrinetToolbar(controller);
		
		toolbar.setFloatable(false);
		
		this.add(toolbar, BorderLayout.NORTH);
		
		add(splitPane, BorderLayout.CENTER);
		
		// Erzeuge ein Label, welches als Statuszeile dient, ...
		// ... und zeige dort ein paar hilfreiche Systeminfos an, ...
		statusLabel = new JLabel("java.version = " + System.getProperty("java.version") + "  |  user.dir = "
				+ System.getProperty("user.dir"));
		// ... und füge es zum Haupt-Frame hinzu
		this.add(statusLabel, BorderLayout.SOUTH);

		
//		f.setSize(400, 240);
//	      f.setLocationRelativeTo(null);
		// bestimme eine geeignete Fenstergröße in Abhängigkeit von der
		// Bildschirmauflösung
		double heightPerc = 0.7; // relative Höhe des Fensters bzgl. der der Bildschirmhöhe (1.0), hier also 60 %
		double aspectRatio = 16.0 / 10.0; // Seitenverhältnis des Fensters
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int h = (int) (screenSize.height * heightPerc);
		int w = (int) (h * aspectRatio);
		setBounds((screenSize.width - w) / 2, (screenSize.height - h) / 2, w, h);

		// Konfiguriere weitere Parameter des Haupt-Frame
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public void setStatusLabel(String status) {
		statusLabel.setText(status);
	}
	
	public void updateGraphSplitPane(PetrinetController controller) {
		
		
		// Füge das JPanel zum Haupt-Frame hinzu
		if (graphSplitPane != null) {
			splitPane.remove(graphSplitPane);
		}
		
		graphSplitPane = new ResizableSplitPane(this, JSplitPane.HORIZONTAL_SPLIT, new GetComponentInterface() {
			
			@Override
			public Component getRightComponent() {
				return 	GraphStreamView.initGraphStreamView(controller.getReachabilityGraph(), controller);

			}
			
			@Override
			public Component getLeftComponent() {
				return 	GraphStreamView.initGraphStreamView(controller.getPetrinetGraph(), controller);
			}
		});
		
		if (splitPane.getGetComponentInterface() != null)
			splitPane.onResized();
		splitPane.revalidate();
		this.revalidate();
		
		
	}
	
	public JSplitPane getGraphPane() {
		return graphSplitPane;
	}
}
