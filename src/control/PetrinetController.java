package control;

import java.io.File;

import javax.swing.JOptionPane;

import gui.ToolbarMode;
import gui.ToolbarToggleListener;
import petrinet.Added;
import petrinet.Editor;
import petrinet.PNMLParser;
import petrinet.Petrinet;
import petrinet.PetrinetAnalyser;
import petrinet.PetrinetElement;
import petrinet.PetrinetException;
import petrinet.PetrinetGraph;
import petrinet.PetrinetQueue;
import petrinet.PetrinetState;
import petrinet.PetrinetStateChangedListener;
import petrinet.Place;
import petrinet.ReachabilityGraph;
import petrinet.ReachabilityGraphModel;
import petrinet.Transition;
import propra.pnml.PNMLWopedWriter;

public class PetrinetController {

	private Petrinet petrinet = new Petrinet();
	private PetrinetGraph petrinetGraph;
	private ReachabilityGraph reachabilityGraph;
	private ReachabilityGraphModel reachabilityGraphModel;

	private Editor editor;

	private File file;

	private boolean fileChanged;
	private boolean headless;

	private ToolbarMode toolbarMode = ToolbarMode.VIEWER;

	private PetrinetQueue petrinetQueue;;

	private ToolbarToggleListener toolbarToggleListener;

	public PetrinetController(File file, boolean headless) throws PetrinetException {
		this.headless = headless;
		this.file = file;
		this.editor = new Editor(this);
		this.petrinet = new Petrinet();

		if (!headless)
			this.petrinetGraph = new PetrinetGraph(this);

		if (file != null)
			new PNMLParser(file, petrinet);

		this.reachabilityGraphModel = new ReachabilityGraphModel(petrinet);

		if (!headless) {
			this.reachabilityGraph = new ReachabilityGraph(this);
			this.petrinetQueue = new PetrinetQueue(reachabilityGraphModel.getCurrentPetrinetState(), this);
		}

		petrinet.setPetrinetChangeListener(new PetrinetStateChangedListener() {

			@Override
			public void onTransitionFire(Transition t) {
				Added added = reachabilityGraphModel.addNewState(petrinet, t);

				if (!headless)
					petrinetQueue.push(reachabilityGraphModel.getCurrentState(), added, t);
			}

			@Override
			public void onComponentChanged(Petrinet petrinet) {
				reachabilityGraphModel.reset();
				PetrinetState initialState = reachabilityGraphModel.getInitialState();
				if (initialState != null)
					reachabilityGraphModel.removeState(initialState);
				reachabilityGraphModel.addNewState(petrinet, null);
				if (!headless) {
					petrinetQueue.resetButtons();
					petrinetQueue = new PetrinetQueue(reachabilityGraphModel.getCurrentState(),
							PetrinetController.this);
				}
			}

		});

	}

	public void setToolbarToggleListener(ToolbarToggleListener toolbarToggleListener) {
		this.toolbarToggleListener = toolbarToggleListener;
	}

	public ToolbarToggleListener getToolbarToggleListener() {
		return toolbarToggleListener;
	}

	public Editor getEditor() {
		return editor;
	}

	public PetrinetQueue getPetrinetQueue() {
		return petrinetQueue;
	}

	public Petrinet getPetrinet() {
		return petrinet;
	}

	public PetrinetGraph getPetrinetGraph() {
		return petrinetGraph;
	}

	public ReachabilityGraph getReachabilityGraph() {
		return reachabilityGraph; 
	}

	public ReachabilityGraphModel getReachabilityGraphModel() {
		return reachabilityGraphModel;
	}

	public void clickNodeInGraph(String id) {

		PetrinetElement pe = petrinet.getPetrinetElement(id);

		if (toolbarMode == ToolbarMode.VIEWER) {
			if (pe instanceof Transition)
				petrinet.fireTransition(id);
			if (pe instanceof Place)
				petrinetGraph.toggleNodeMark(pe);
		}
		if (toolbarMode == ToolbarMode.EDITOR)
			editor.clickedNodeInGraph(pe);
	}

	public void resetPetrinet() {
		petrinet.setState(reachabilityGraphModel.getInitialState());
		reachabilityGraphModel.setInitial();
		petrinetQueue.rewind();

	}

	public boolean incrementMarkedPlace() {

		boolean changed = petrinet.incrementPlace(petrinetGraph.getMarkedNode());

		if (changed && !fileChanged) {
			setFileChanged(true);
		}

		return changed;
	}

	public boolean decrementMarkedPlace() {
		boolean changed = petrinet.decrementPlace(petrinetGraph.getMarkedNode());

		if (changed && !fileChanged) {
			setFileChanged(true);
		}

		return changed;
	}

	public File getCurrentFile() {
		return file;
	}

	public void resetReachabilityGraph() {
		petrinet.setState(reachabilityGraphModel.getInitialState());
		reachabilityGraphModel.reset();
		petrinetQueue.resetButtons();
		petrinetQueue = new PetrinetQueue(reachabilityGraphModel.getCurrentState(), this);
	}

	public void reachabilityNodeClicked(String id) {
		PetrinetState state = reachabilityGraphModel.getState(id);
		petrinet.setState(state);
		reachabilityGraphModel.setCurrentState(state);
		petrinetQueue.push(reachabilityGraphModel.getCurrentState(), Added.NOTHING, null);
	}

	public String[] analyse() {

		PetrinetAnalyser analyser = new PetrinetAnalyser(this);

		if (!headless) {
			petrinetQueue.resetButtons();
			petrinetQueue = new PetrinetQueue(reachabilityGraphModel.getCurrentState(), this);
			JOptionPane.showMessageDialog(null,
					"The petrinet is " + (analyser.isBounded() ? "bounded" : "unbounded") + ".", "",
					JOptionPane.INFORMATION_MESSAGE);

		}
		return analyser.getResults();
	}

	public boolean getHeadless() {
		return headless;
	}

	public boolean getFileChanged() {
		return fileChanged;
	}

	public ToolbarMode getToolbarMode() {

		return toolbarMode;
	}

	public void setToolbarMode(ToolbarMode toolbarMode) {
		if (toolbarMode == ToolbarMode.EDITOR)
			resetReachabilityGraph();
		if (toolbarMode == ToolbarMode.VIEWER) {
			PetrinetElement toggledElement = petrinetGraph.getMarkedNode();
			if (toggledElement != null && toggledElement instanceof Transition)
				petrinetGraph.toggleNodeMark(toggledElement);
		}
		this.toolbarMode = toolbarMode;
	}

	public void writeToFile() {
		writeToFile(getCurrentFile());
	}

	public void writeToFile(File file) {

		PNMLWopedWriter writer = new PNMLWopedWriter(file);
		writer.startXMLDocument();

		for (Place p : petrinet.getPlaces())
			writer.addPlace(p.getId(), p.getName(), String.valueOf(p.getX()), String.valueOf(-p.getY()),
					String.valueOf(p.getNumberOfTokens()));
		for (Transition t : petrinet.getTransitions()) {
			writer.addTransition(t.getId(), t.getName(), String.valueOf(t.getX()), String.valueOf(-t.getY()));
			for (Place p : t.getInputs())
				writer.addArc(petrinet.getOriginalArcId(p.getId() + t.getId()), p.getId(), t.getId());
			for (Place p : t.getOutputs())
				writer.addArc(petrinet.getOriginalArcId(t.getId() + p.getId()), t.getId(), p.getId());
		}

		writer.finishXMLDocument();

		this.file = file;

		fileChanged = false;

	}

	public void setFileChanged(boolean changed) {
		fileChanged = changed;
	}

	public void setLabel(String label) {
		PetrinetElement markedNode = petrinetGraph.getMarkedNode();
		if (markedNode == null)
			return;

		if (markedNode.getName().equals(label))
			return;

		setFileChanged(true);// has to be set first so that petrinetQueue receives changes

		petrinet.setPetrinetElementName(markedNode.getId(), label);

	}

	public void setPetrinetQueue(PetrinetQueue petrinetQueue) {
		this.petrinetQueue = petrinetQueue;
	}

}
