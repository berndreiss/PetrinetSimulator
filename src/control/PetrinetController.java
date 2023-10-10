package control;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.print.DocFlavor.READER;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import org.graphstream.ui.view.util.InteractiveElement;

import datamodel.Petrinet;
import datamodel.PetrinetElement;
import datamodel.PetrinetState;
import datamodel.PetrinetChangeListener;
import datamodel.Place;
import datamodel.ReachabilityGraphModel;
import datamodel.PetrinetState;
import datamodel.StateChangeListener;
import datamodel.Transition;
import util.PNMLParser;
import util.PetrinetAnalyser;
import view.PetrinetPanel;
import view.PetrinetGraph;
import view.ReachabilityGraph;
import view.ResizableSplitPane;
import view.ClickListener;
import view.GetComponentInterface;
import view.GraphStreamView;

public class PetrinetController {

	private Petrinet petrinet = new Petrinet();
	private PetrinetGraph petrinetGraph;
	private ReachabilityGraph reachabilityGraph;
	private ReachabilityGraphModel reachabilityGraphModel;

	private ViewPanel petrinetViewPanel;
	private ViewPanel reachabilityViewPanel;

	private PetrinetPanel petrinetPanel;

	private File currentFile;

	private boolean fileChanged;
	private boolean headless;
	private boolean analysed;

	private List<String> transitionsToMMarked;
	private String m;
	private String mMarked;


	public PetrinetController(PetrinetPanel petrinetPanel, File file, boolean headless) {
		this.petrinetPanel = petrinetPanel;
		this.headless = headless;
		this.petrinetGraph = new PetrinetGraph(this);
		if (file == null)
			init();
		else
			onFileOpen(file);

		if (!headless) {
			this.petrinetViewPanel = initGraphStreamView(petrinetGraph);
			petrinetGraph.setViewPanel(petrinetViewPanel);

		}
			
	}

	private void init() {
		this.reachabilityGraphModel = new ReachabilityGraphModel(this);

		if (!headless) {

			this.reachabilityGraph = new ReachabilityGraph(this);
			this.reachabilityViewPanel = initGraphStreamView(reachabilityGraph);
			reachabilityGraph.setViewPanel(reachabilityViewPanel);
		}
		petrinet.setPetrinetChangeListener(new PetrinetChangeListener() {
			
			@Override
			public void onTransitionFire(Transition t) {
				PetrinetState state = reachabilityGraphModel.addNewState(petrinet, t);
				reachabilityGraphModel.checkIfCurrentStateIsBackwardsValid();
			}
			
			@Override
			public void onChanged(Petrinet petrinet) {
				reachabilityGraphModel.resetTo(petrinet);
				
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
		if (currentFile != null)
			onFileOpen(currentFile);
	}

	public void clickNodeInGraph(String id) {

		PetrinetElement pe = petrinet.getPetrinetElement(id);

		if (pe instanceof Transition)
			petrinet.fireTransition(id);
		if (pe instanceof Place)
			petrinetGraph.toggleNodeMark(id);
		
	}

	public void onFileOpen(File file) {

		if (file == null)
			return;

		this.currentFile = file;

		this.petrinet = new Petrinet();

		new PNMLParser(file, this.petrinet);

		init();


		
		if (!headless) {
			this.petrinetGraph = new PetrinetGraph(this);

			for (Place p : petrinet.getPlaces())
				petrinetGraph.addPlace(p);

			for (Transition t : petrinet.getTransitions())
				petrinetGraph.addTransition(t);

		}
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

		reachabilityGraphModel.reset();
		reachabilityGraphModel = new ReachabilityGraphModel(this);
		reachabilityGraph.reinitialize();
		
		if (!fileChanged) {
			fileChanged = true;
		}
	}

	public void repaint() {
		ResizableSplitPane splitPane = (ResizableSplitPane) petrinetPanel.getGraphPane();
		splitPane.getLeftComponent().repaint();
		splitPane.getRightComponent().repaint();
	}

	public PetrinetPanel getFrame() {
		return petrinetPanel;
	}

	public File getCurrentFile() {
		return currentFile;
	}

	public void decrementPlace(String markedPlace) {
		petrinet.decrementPlace(markedPlace);
		if (!headless)
			resetReachabilityGraph();

		if (!fileChanged) {
			fileChanged = true;
		}
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

	public void analyse() {
		
		resetReachabilityGraph();
		
		PetrinetAnalyser analyser = new PetrinetAnalyser(this);
		analyser.analyse();
		this.analysed = true;
	}

	public String[] getResults() {
		String[] strings = {"","",""};
		if (!analysed || currentFile == null)
			return strings;
		boolean isValid = reachabilityGraphModel.getInvalidState() == null;
		StringBuilder sb = new StringBuilder();

		sb.append(currentFile.getName());
		
		strings[0] = sb.toString();
		
		sb = new StringBuilder();
		sb.append(isValid?"ja":"nein");
		strings[1] = sb.toString();
		
		sb = new StringBuilder();
		
		if (!isValid) {
			sb.append(transitionsToMMarked.size());
			sb.append(": (");
			for (String s : transitionsToMMarked)
				sb.append(s + ",");
			sb.deleteCharAt(sb.length() - 1);
			sb.append(");");

			sb.append(" ");
			sb.append(m);
			sb.append(" ");
			sb.append(mMarked);

		} else {

			int stateCount = 0;
			int edgeCount = 0;

			Set<String> edges = new HashSet<String>();

			for (PetrinetState rs : reachabilityGraphModel.getStates()) {

				for (PetrinetState ss : rs.getSuccessors()) {
					for (Transition t : rs.getSuccessorTransitions(ss)) {
						String edgesString = rs.getState() + ss.getState() + t.getId();
						if (!edges.contains(edgesString)) {
							edges.add(edgesString);
							edgeCount++;
						}
					}
				}

				stateCount++;
			}
			sb.append(stateCount + " / " + edgeCount);
		}
		strings[2] = sb.toString();
		return strings;
	}

	public boolean stateIsValid() {
		return reachabilityGraphModel.getCurrentState().getM() == null;
	}

	public void setState(PetrinetState petrinetState) {

		petrinet.setState(petrinetState);
		reachabilityGraphModel.setCurrentState(petrinetState);

	}

	public void setHeadless(boolean headless) {
		this.headless = headless;

	}

	public boolean getHeadless() {
		return headless;
	}

	public void updateReachabilityGraph() {

		PetrinetState invalidState = reachabilityGraphModel.getInvalidState();
		if (invalidState != null) {

			m = invalidState.getM().getState();
			mMarked = invalidState.getState();
			transitionsToMMarked = new ArrayList<String>();

			List<PetrinetState> pathToM = invalidState.getListToOtherState(invalidState.getM());

			List<PetrinetState> pathToInitial = invalidState.getM()
					.getListToOtherState(reachabilityGraphModel.getInitialState());
			PetrinetState currentState = reachabilityGraphModel.getCurrentState();

			
	
			ArrayList<Transition> transitionList = new ArrayList<Transition>();
			
			if (pathToInitial != null) {

				for (int i = pathToInitial.size() - 1; i >= 0; i--) {

					PetrinetState nextState = pathToInitial.get(i);

					Transition transition = currentState.getFirstSuccessorTransition(nextState);
					
					transitionList.add(transition);
					
					currentState = pathToInitial.get(i);
				}

			}

			for (int i = pathToM.size() - 1; i >= 0; i--) {

				PetrinetState nextState = pathToM.get(i);

				Transition transition = currentState.getFirstSuccessorTransition(nextState);
				transitionsToMMarked.add(transition.getId());

				transitionList.add(transition);
				
				currentState = pathToM.get(i);
			}

			Transition transition = currentState.getFirstSuccessorTransition(invalidState);

			if (transition != null)
				transitionsToMMarked.add(transition.getId());

			transitionList.add(transition);
			
			reachabilityGraphModel.reset();
			petrinet.setState(reachabilityGraphModel.getInitialState());

			
			//TODO: why have been null transitions been added? -> look into reachabilitygraphmodel
			for (Transition t: transitionList) {
				if (t != null)
					petrinet.fireTransition(t.getId());
			}
			return;
		}
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
