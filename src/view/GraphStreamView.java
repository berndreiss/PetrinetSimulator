package view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.EnumSet;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.graphstream.graph.Graph;
import org.graphstream.stream.AttributeSink;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.swing_viewer.util.DefaultMouseManager;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.camera.Camera;
import org.graphstream.ui.view.util.InteractiveElement;

import control.PetrinetController;
import datamodel.PetrinetElement;

public class GraphStreamView extends JPanel {

	
	public static ViewPanel initGraphStreamView(Graph graph, PetrinetController controller) {

		// Erzeuge Viewer mit passendem Threading-Model für Zusammenspiel mit
		// Swing
		SwingViewer viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

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
		if (graph instanceof PetrinetGraph)
			viewer.disableAutoLayout();
		else
			viewer.enableAutoLayout();
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
		ViewPanel viewPanel = (ViewPanel) viewer.addDefaultView(false);

		// Neue ViewerPipe erzeugen, um über Ereignisse des Viewer informiert
		// werden zu können
		ViewerPipe viewerPipe = viewer.newViewerPipe();


		
		// Neuen ClickListener erzeugen, der als ViewerListener auf Ereignisse
		// der View reagieren kann
		ClickListener clickListener = new ClickListener(controller);

		// clickListener als ViewerListener bei der viewerPipe anmelden
		viewerPipe.addViewerListener(clickListener);

		// Neuen MouseListener beim viewPanel anmelden. Wenn im viewPanel ein
		// Maus-Button gedrückt oder losgelassen wird, dann wird die Methode
		// viewerPipe.pump() aufgerufen, um alle bei der viewerPipe angemeldeten
		// ViewerListener zu informieren (hier also konkret unseren
		// clickListener).
		
//		viewPanel.setMouseManager(new DefaultMouseManager() {
//			  @Override
//	            protected void elementAttributeChanged(GraphicElement element, String attribute, Object oldValue, Object newValue) {
//	                if (element instanceof GraphicNode && attribute.equals("ui.clicked")) {
//	                    GraphicNode node = (GraphicNode) element;
//	                    System.out.println("Node " + node.getId() + " position: " + node.getX() + ", " + node.getY());
//	                }
//	            }
//		});
		
		EnumSet<InteractiveElement> enumSet = EnumSet.of(InteractiveElement.NODE);
		
		
		viewPanel.addMouseListener(new MouseAdapter(){

			@Override
			public void mousePressed(MouseEvent me) {
				viewerPipe.pump();
			}

			@Override
			public void mouseReleased(MouseEvent me) {
				GraphicElement element = viewPanel.findGraphicElementAt(enumSet, me.getX(),me.getY());
				if (element != null) {
					String id = element.getId();
					double x = element.getX();
					double y = element.getY();
					PetrinetElement p = controller.getPetrinet().getPetrinetElement(id);
					if (p != null)
						if (p.getX() != x || p.getY() != y) {
							p.setX(x);
							p.setY(y);
					}

					
				}
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

		return viewPanel;

	}

}
