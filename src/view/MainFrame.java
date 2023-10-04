package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.graphstream.ui.swing_viewer.ViewPanel;

import control.PetrinetController;
import datamodel.Petrinet;

public class MainFrame extends JFrame {

	private JPanel menu;
	private GraphSplitPane splitPane;
	
	private PetrinetController controller;
	private JLabel statusLabel;

	public MainFrame(String title) {
		super(title);

		this.controller = new PetrinetController(this);

		updateSplitPane(controller);
		
		menu = new Menu(this, controller);

		JPanel visualization = new JPanel();

		add(menu, BorderLayout.NORTH);
		add(visualization, BorderLayout.CENTER);
		
		// Erzeuge ein Label, welches als Statuszeile dient, ...
		// ... und zeige dort ein paar hilfreiche Systeminfos an, ...
		statusLabel = new JLabel("java.version = " + System.getProperty("java.version") + "  |  user.dir = "
				+ System.getProperty("user.dir"));
		// ... und füge es zum Haupt-Frame hinzu
		this.add(statusLabel, BorderLayout.SOUTH);

		final JFrame finalFrame = this;
	
		
//		f.setSize(400, 240);
//	      f.setLocationRelativeTo(null);
		// bestimme eine geeignete Fenstergröße in Abhängigkeit von der
		// Bildschirmauflösung
		double heightPerc = 0.6; // relative Höhe des Fensters bzgl. der der Bildschirmhöhe (1.0), hier also 60 %
		double aspectRatio = 16.0 / 10.0; // Seitenverhältnis des Fensters
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int h = (int) (screenSize.height * heightPerc);
		int w = (int) (h * aspectRatio);
		setBounds((screenSize.width - w) / 2, (screenSize.height - h) / 2, w, h);

		// Konfiguriere weitere Parameter des Haupt-Frame
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	public void updateSplitPane(PetrinetController controller) {
		
		
		
		// Füge das JPanel zum Haupt-Frame hinzu
		if (splitPane != null) {
			this.remove(splitPane);
		}
		
		splitPane = new GraphSplitPane(this, JSplitPane.HORIZONTAL_SPLIT, controller);
		
		this.add(splitPane, BorderLayout.CENTER);
		this.revalidate();
		
		
	}
	
}
