package control;

import java.io.File;

import javax.swing.JOptionPane;

import core.Added;
import core.Editor;
import core.PNMLParser;
import core.Petrinet;
import core.PetrinetAnalyser;
import core.PetrinetElement;
import core.PetrinetGraph;
import core.PetrinetQueue;
import core.PetrinetState;
import core.Place;
import core.ReachabilityGraph;
import core.ReachabilityGraphModel;
import core.Transition;
import exceptions.PetrinetException;
import gui.ToolbarMode;
import listeners.PetrinetStateChangedListener;
import listeners.ToolbarToggleListener;
import propra.pnml.PNMLWopedWriter;

// TODO: Auto-generated Javadoc
/**
 * The Class PetrinetController.
 */
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

	/**
	 * Instantiates a new petrinet controller.
	 *
	 * @param file the file
	 * @param headless the headless
	 * @throws PetrinetException the petrinet exception
	 */
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

	/**
	 * Sets the toolbar toggle listener.
	 *
	 * @param toolbarToggleListener the new toolbar toggle listener
	 */
	public void setToolbarToggleListener(ToolbarToggleListener toolbarToggleListener) {
		this.toolbarToggleListener = toolbarToggleListener;
	}

	/**
	 * Gets the toolbar toggle listener.
	 *
	 * @return the toolbar toggle listener
	 */
	public ToolbarToggleListener getToolbarToggleListener() {
		return toolbarToggleListener;
	}

	/**
	 * Gets the editor.
	 *
	 * @return the editor
	 */
	public Editor getEditor() {
		return editor;
	}

	/**
	 * Gets the petrinet queue.
	 *
	 * @return the petrinet queue
	 */
	public PetrinetQueue getPetrinetQueue() {
		return petrinetQueue;
	}

	/**
	 * Gets the petrinet.
	 *
	 * @return the petrinet
	 */
	public Petrinet getPetrinet() {
		return petrinet;
	}

	/**
	 * Gets the petrinet graph.
	 *
	 * @return the petrinet graph
	 */
	public PetrinetGraph getPetrinetGraph() {
		return petrinetGraph;
	}

	/**
	 * Gets the reachability graph.
	 *
	 * @return the reachability graph
	 */
	public ReachabilityGraph getReachabilityGraph() {
		return reachabilityGraph; 
	}

	/**
	 * Gets the reachability graph model.
	 *
	 * @return the reachability graph model
	 */
	public ReachabilityGraphModel getReachabilityGraphModel() {
		return reachabilityGraphModel;
	}

	/**
	 * Click node in graph.
	 *
	 * @param id the id
	 */
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

	/**
	 * Reset petrinet.
	 */
	public void resetPetrinet() {
		petrinet.setState(reachabilityGraphModel.getInitialState());
		reachabilityGraphModel.setInitial();
		petrinetQueue.rewind();

	}

	/**
	 * Increment marked place.
	 *
	 * @return true, if successful
	 */
	boolean incrementMarkedPlace() {

		boolean changed = petrinet.incrementPlace(petrinetGraph.getMarkedNode());

		if (changed && !fileChanged) {
			setFileChanged(true);
		}

		return changed;
	}

	/**
	 * Decrement marked place.
	 *
	 * @return true, if successful
	 */
	boolean decrementMarkedPlace() {
		boolean changed = petrinet.decrementPlace(petrinetGraph.getMarkedNode());

		if (changed && !fileChanged) {
			setFileChanged(true);
		}

		return changed;
	}

	/**
	 * Gets the current file.
	 *
	 * @return the current file
	 */
	public File getCurrentFile() {
		return file;
	}

	/**
	 * Reset reachability graph.
	 */
	public void resetReachabilityGraph() {
		petrinet.setState(reachabilityGraphModel.getInitialState());
		reachabilityGraphModel.reset();
		petrinetQueue.resetButtons();
		petrinetQueue = new PetrinetQueue(reachabilityGraphModel.getCurrentState(), this);
	}

	/**
	 * Reachability node clicked.
	 *
	 * @param id the id
	 */
	public void reachabilityNodeClicked(String id) {
		PetrinetState state = reachabilityGraphModel.getState(id);
		petrinet.setState(state);
		reachabilityGraphModel.setCurrentState(state);
		petrinetQueue.push(reachabilityGraphModel.getCurrentState(), Added.NOTHING, null);
	}

	/**
	 * Analyse.
	 *
	 * @return the string[]
	 */
	String[] analyse() {

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

	/**
	 * Gets the headless.
	 *
	 * @return the headless
	 */
	public boolean getHeadless() {
		return headless;
	}

	/**
	 * Gets the file changed.
	 *
	 * @return the file changed
	 */
	public boolean getFileChanged() {
		return fileChanged;
	}

	/**
	 * Gets the toolbar mode.
	 *
	 * @return the toolbar mode
	 */
	public ToolbarMode getToolbarMode() {

		return toolbarMode;
	}

	/**
	 * Sets the toolbar mode.
	 *
	 * @param toolbarMode the new toolbar mode
	 */
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

	/**
	 * Write to file.
	 */
	void writeToFile() {
		writeToFile(getCurrentFile());
	}

	/**
	 * Write to file.
	 *
	 * @param file the file
	 */
	void writeToFile(File file) {

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

	/**
	 * Sets the file changed.
	 *
	 * @param changed the new file changed
	 */
	public void setFileChanged(boolean changed) {
		fileChanged = changed;
	}

	/**
	 * Sets the label.
	 *
	 * @param label the new label
	 */
	public void setLabel(String label) {
		PetrinetElement markedNode = petrinetGraph.getMarkedNode();
		if (markedNode == null)
			return;

		if (markedNode.getName().equals(label))
			return;

		setFileChanged(true);// has to be set first so that petrinetQueue receives changes

		petrinet.setPetrinetElementName(markedNode.getId(), label);

	}

	/**
	 * Sets the petrinet queue.
	 *
	 * @param petrinetQueue the new petrinet queue
	 */
	public void setPetrinetQueue(PetrinetQueue petrinetQueue) {
		this.petrinetQueue = petrinetQueue;
	}

}
