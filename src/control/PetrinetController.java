package control;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.EnumSet;

import org.graphstream.graph.Graph;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.util.InteractiveElement;

import datamodel.Petrinet;
import datamodel.PetrinetElement;
import datamodel.PetrinetState;
import datamodel.PetrinetStateChangedListener;
import datamodel.Place;
import datamodel.ReachabilityGraphModel;
import datamodel.Transition;
import util.PNMLParser;
import util.PetrinetAnalyser;
import view.PetrinetGraph;
import view.ReachabilityGraph;
import view.ClickListener;

public class PetrinetController {

	private Petrinet petrinet = new Petrinet();
	private PetrinetGraph petrinetGraph;
	private ReachabilityGraph reachabilityGraph;
	private ReachabilityGraphModel reachabilityGraphModel;

	private ViewPanel petrinetViewPanel;
	private ViewPanel reachabilityViewPanel;

	private File file;

	private boolean fileChanged;
	private boolean headless;

	public PetrinetController(File file, boolean headless) {
		this.headless = headless;
		this.file = file;
		init();
	}

	private void init() {
		this.petrinet = new Petrinet();

		if (file != null) {

			if (!headless)
				this.petrinetGraph = new PetrinetGraph(petrinet);

			new PNMLParser(file, this.petrinet);
		}

		if (!headless) {
		}
		this.reachabilityGraphModel = new ReachabilityGraphModel(petrinet);

		if (!headless) {

			this.reachabilityGraph = new ReachabilityGraph(this);

			this.petrinetViewPanel = initGraphStreamView(petrinetGraph);
			this.reachabilityViewPanel = initGraphStreamView(reachabilityGraph);
			reachabilityGraph.setViewPanel(reachabilityViewPanel);
		}

		petrinet.setPetrinetChangeListener(new PetrinetStateChangedListener() {

			@Override
			public void onTransitionFire(Transition t) {
				reachabilityGraphModel.addNewState(petrinet, t);
			}

			@Override
			public void onComponentChanged(Petrinet petrinet) {
				reachabilityGraphModel.reset();
				PetrinetState initialState = reachabilityGraphModel.getInitialState();
				if (initialState != null)
					reachabilityGraphModel.removeState(initialState);
				reachabilityGraphModel.addNewState(petrinet, null);
			}
		});
	}

	public Petrinet getPetrinet() {
		return petrinet;
	}

	public PetrinetGraph getPetrinetGraph() {
		return petrinetGraph;
	}

	public ReachabilityGraph getReachabilityGraph() {
		return reachabilityGraph; // TODOchange when ready!
	}

	public ReachabilityGraphModel getReachabilityGraphModel() {
		return reachabilityGraphModel;
	}

	public void reload() {
		init();
	}

	public void clickNodeInGraph(String id) {

		PetrinetElement pe = petrinet.getPetrinetElement(id);

		if (pe instanceof Transition)
			petrinet.fireTransition(id);
		if (pe instanceof Place)
			petrinetGraph.toggleNodeMark(id);
	}

	public void resetPetrinet() {
		petrinet.setState(reachabilityGraphModel.getInitialState());
		reachabilityGraphModel.setInitial();
	}

	public Object closeCurrent() {
		// TODO Auto-generated method stub
		return null;
	}

	public void incrementPlace(String markedPlace) {

		petrinet.incrementPlace(markedPlace);

		if (!fileChanged) {
			fileChanged = true;
		}
	}

	public void decrementPlace(String markedPlace) {
		petrinet.decrementPlace(markedPlace);

		if (!fileChanged) {
			fileChanged = true;
		}
	}

	public File getCurrentFile() {
		return file;
	}

	public void resetReachabilityGraph() {
		petrinet.setState(reachabilityGraphModel.getInitialState());
		reachabilityGraphModel.reset();
	}

	public void reachabilityNodeClicked(String id) {
		PetrinetState state = reachabilityGraphModel.getState(id);

		petrinet.setState(state);
		reachabilityGraphModel.setCurrentState(state);
	}

	public String[] analyse() {

		PetrinetAnalyser analyser = new PetrinetAnalyser(this);

		return getResults(analyser);

	}

	private String[] getResults(PetrinetAnalyser analyser) {
		String[] strings = { "", "", "" };

		if (file == null)
			return strings;

		StringBuilder sb = new StringBuilder();

		sb.append(file.getName() + " ");
		strings[0] = sb.toString();

		sb = new StringBuilder();
		sb.append(analyser.isFinite() ? " yes" : " no");
		strings[1] = sb.toString();

		sb = new StringBuilder();

		if (!analyser.isFinite()) {
			sb.append(" " + analyser.getTransitionsToMMarked().size());
			sb.append(": (");
			for (String s : analyser.getTransitionsToMMarked())
				sb.append(s + ",");
			sb.deleteCharAt(sb.length() - 1);
			sb.append(");");

			sb.append(" ");
			sb.append(analyser.getM());
			sb.append(" ");
			sb.append(analyser.getMMarked());

		} else {

			sb.append(" " + analyser.getStateCount() + " / " + analyser.getEdgeCount());
		}
		strings[2] = sb.toString();
		return strings;
	}

	public boolean getHeadless() {
		return headless;
	}

	public boolean getFileChanged() {
		return fileChanged;
	}

	public ViewPanel initGraphStreamView(Graph graph) {// TODO change to private

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

		if (graph instanceof PetrinetGraph) {
			// Neuen ClickListener erzeugen, der als ViewerListener auf Ereignisse
			// der View reagieren kann
			ClickListener clickListener = new ClickListener(this);

			// clickListener als ViewerListener bei der viewerPipe anmelden
			viewerPipe.addViewerListener(clickListener);

		}

		if (graph instanceof ReachabilityGraph) {
			viewerPipe.addViewerListener(new ViewerListener() {

				@Override
				public void viewClosed(String viewName) {
				}

				@Override
				public void mouseOver(String id) {
				}

				@Override
				public void mouseLeft(String id) {
				}

				@Override
				public void buttonReleased(String id) {
				}

				@Override
				public void buttonPushed(String id) {
					reachabilityNodeClicked(id);
				}
			});

		}

		EnumSet<InteractiveElement> enumSet = EnumSet.of(InteractiveElement.NODE);

		viewPanel.addMouseListener(new MouseAdapter() {

			private GraphicElement element;

			@Override
			public void mousePressed(MouseEvent me) {
				element = viewPanel.findGraphicElementAt(enumSet, me.getX(), me.getY());
				viewerPipe.pump();
			}

			@Override
			public void mouseReleased(MouseEvent me) {

				if (element != null) {
					String id = element.getId();
					PetrinetElement p = getPetrinet().getPetrinetElement(id);
					double x = element.getX();
					double y = element.getY();
					if (p != null)
						if (p.getX() != x || p.getY() != y) {
							p.setX(x);
							p.setY(y);
						}
					element = null;
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

	public ViewPanel getPetrinetViewPanel() {
		return petrinetViewPanel;
	}

	public ViewPanel getReachabilityViewPanel() {
		return reachabilityViewPanel;
	}

}
