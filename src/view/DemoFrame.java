package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.graphstream.graph.Node;
import org.graphstream.ui.swing_viewer.*;

import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;

import datamodel.Petrinet;
import datamodel.Place;


/**
 * Die Klasse stellt den Haupt-Frame für eine kleine Anwendung dar, in der
 * beispielhaft die Verwendung der GraphStream-Library gezeigt wird.
 * 
 * <p>
 * Der Haupt-Frame besteht aus einem Panel, in dem der Graph angezeigt wird, und
 * einem Label, das der Ausgabe von Meldungen dient. Diese Komponenten werden
 * mittels BorderLayout angeordnet.
 * </p>
 * 
 * @author ProPra-Team FernUni Hagen
 */
public class DemoFrame extends JFrame {

	/** default serial version ID */
	private static final long serialVersionUID = 1L;

	private Petrinet petrinet;
	
	/** Graph, der angezeigt wird */
	private DemoGraph graph;

	/** In diesem Panel wird der Graph mittels GraphStream angezeigt. */
	private ViewPanel viewPanel;
	
	/** In dieses JPanel wird das ViewPanel eingebettet, da es ohne diese
	 * Einbettung zu unerwarteten Effekten kommen kann (beispielsweise wenn
	 * man mehrere ViewPanel in eine JSplitPane packen
	 * möchte).
	 */
	private JPanel jpnlGraph;

	/** ein Label für die Ausgabe von Text (Statuszeile) */
	private JLabel statusLabel;
	
	/**
	 * Im Konstruktor wird der Haupt-Frame erzeugt und angezeigt.
	 * 
	 * @param titel
	 *            Titel des Frames
	 * @param graph
	 * 			  Referenz auf den Graphen  
	 */
	public DemoFrame(String titel, DemoGraph graph, Petrinet petrinet) {
		super(titel);
		this.petrinet = petrinet;
		this.graph = graph;
		
		// Layout des JFrames setzen
		this.setLayout(new BorderLayout());

		// Erzeuge und initialisiere ein Panel zur Anzeige des Graphen
		initPanelGraph();

		// Einbetten des ViewPanels ins JPanel
		jpnlGraph = new JPanel(new BorderLayout());
		jpnlGraph.add(BorderLayout.CENTER, viewPanel);
		
		// Füge das JPanel zum Haupt-Frame hinzu
		this.add(jpnlGraph, BorderLayout.CENTER);

		// Erzeuge ein Label, welches als Statuszeile dient, ...
		// ... und zeige dort ein paar hilfreiche Systeminfos an, ...
		statusLabel = new JLabel("java.version = " + System.getProperty("java.version") + "  |  user.dir = " + System.getProperty("user.dir"));
		// ... und füge es zum Haupt-Frame hinzu
		this.add(statusLabel, BorderLayout.SOUTH);

		// bestimme eine geeignete Fenstergröße in Abhängigkeit von der Bildschirmauflösung
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

	/**
	 * Erzeuge und initialisiere ein Panel zur Anzeige des Graphen
	 */
	private void initPanelGraph() {

		// Erzeuge Viewer mit passendem Threading-Model für Zusammenspiel mit
		// Swing
		SwingViewer viewer = new SwingViewer(graph,
				Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

		// bessere Darstellungsqualität und Antialiasing (Kantenglättung) aktivieren
		// HINWEIS: Damit diese Attribute eine Auswirkung haben, müssen sie NACH 
		// Erzeugung des SwingViewer gesetzt werden
		graph.setAttribute("ui.quality");
		graph.setAttribute("ui.antialias");
		
		// Das Auto-Layout für den Graphen kann aktiviert oder deaktiviert
		// werden.
		// Auto-Layout deaktivieren: Die explizit hinzugefügten Koordinaten
		// werden genutzt (wie in DemoGraph).
		// Achtung: Falls keine Koordinaten definiert wurden, liegen alle Knoten
		// übereinander.)
		viewer.disableAutoLayout();
		// Auto-Layout aktivieren: GraphStream generiert ein möglichst
		// übersichtliches Layout
		// (und ignoriert hinzugefügte Koordinaten)
		// viewer.enableAutoLayout();

		// Eine DefaultView zum Viewer hinzufügen, die jedoch nicht automatisch
		// in einen JFrame integriert werden soll (daher Parameter "false"). Das
		// zurückgelieferte ViewPanel ist eine Unterklasse von JPanel, so dass
		// es später einfach in unsere Swing-GUI integriert werden kann. Es gilt
		// folgende Vererbungshierarchie:
		// DefaultView extends ViewPanel extends JPanel implements View
		// Hinweis:
		// In den Tutorials wird "View" als Rückgabetyp angegeben, es ist
		// aber ein "ViewPanel" (und somit auch ein JPanel).
		viewPanel = (ViewPanel) viewer.addDefaultView(false);

		// Neue ViewerPipe erzeugen, um über Ereignisse des Viewer informiert
		// werden zu können
		ViewerPipe viewerPipe = viewer.newViewerPipe();

		// Neuen ClickListener erzeugen, der als ViewerListener auf Ereignisse
		// der View reagieren kann
		ClickListener clickListener = new ClickListener(this);

		// clickListener als ViewerListener bei der viewerPipe anmelden
		viewerPipe.addViewerListener(clickListener);

		// Neuen MouseListener beim viewPanel anmelden. Wenn im viewPanel ein
		// Maus-Button gedrückt oder losgelassen wird, dann wird die Methode
		// viewerPipe.pump() aufgerufen, um alle bei der viewerPipe angemeldeten
		// ViewerListener zu informieren (hier also konkret unseren
		// clickListener).
		viewPanel.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent me) {
				System.out.println("DemoFrame - mousePressed: " + me);
				viewerPipe.pump();
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				System.out.println("DemoFrame - mouseReleased: " + me);
				viewerPipe.pump();
			}
		});
		
		
		// Zoom per Mausrad ermöglichen
		viewPanel.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				double zoomLevel = viewPanel.getCamera().getViewPercent();
				if (e.getWheelRotation() == -1) {
					zoomLevel -= 0.1;
					if (zoomLevel < 0.1) {
						zoomLevel = 0.1;
					}
				}
				if (e.getWheelRotation() == 1) {
					zoomLevel += 0.1;
				}
				viewPanel.getCamera().setViewPercent(zoomLevel);
			}
		});
		
	}
	
	
	
	


}
