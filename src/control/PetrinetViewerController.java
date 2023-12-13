package control;

import java.io.File;

import core.PNMLParser;
import core.Petrinet;
import core.PetrinetAnalyser;
import core.PetrinetElement;
import core.ReachabilityGraphUndoQueue;
import core.PetrinetState;
import core.Place;
import core.ReachabilityGraph;
import core.Transition;
import exceptions.PetrinetException;
import gui.ToolbarMode;
import listeners.ToolbarChangedListener;
import propra.pnml.PNMLWopedWriter;

/**
 * <p>
 * Controller handling a petrinet model.
 * </p>
 * 
 * <p>
 * It also creates and handles a reachability graph model linked to petrinet
 * (see {@link ReachabilityGraph}).
 * </p>
 */
public class PetrinetViewerController {

	/** The petrinet being handled. */
	private Petrinet petrinet = new Petrinet();
	/** The reachability graph model for the petrinet. */
	private ReachabilityGraph reachabilityGraphModel;

	/** The file being processed. */
	private File file;

	/** True if the file has been changed. */
	private boolean fileChanged;

	/**
	 * Instantiates a new petrinet controller.
	 *
	 * @param file            The file that is loaded.
	 * @param toolbarListener Listener for highlighting toolbar buttons.
	 * @param toolbarMode toolbar mode to be used
	 * @throws PetrinetException Thrown by PNMLParser.
	 */
	public PetrinetViewerController(File file, ToolbarChangedListener toolbarListener, ToolbarMode toolbarMode) throws PetrinetException {
		this.file = file;
		this.petrinet = new Petrinet();

		// load file if it was provided
		if (file != null)
			new PNMLParser(file, petrinet);

		if (toolbarMode == ToolbarMode.VIEWER && !petrinet.isConnected())
			throw new PetrinetException(
					"Petrinet is not connected. You can not view it but still open it in the editor.");

		// create reachability graph model
		this.reachabilityGraphModel = new ReachabilityGraph(petrinet, toolbarListener);

	}

	/**
	 * Get the reachability undo queue.
	 *
	 * @return the reachability undo queue
	 */
	public ReachabilityGraphUndoQueue getPetrinetQueue() {
		return reachabilityGraphModel.getUndoQueue();
	}

	/**
	 * Get the petrinet.
	 *
	 * @return the petrinet
	 */
	public Petrinet getPetrinet() {
		return petrinet;
	}

	/**
	 * Get the reachability graph model.
	 *
	 * @return the reachability graph model
	 */
	public ReachabilityGraph getReachabilityGraphModel() {
		return reachabilityGraphModel;
	}

	/**
	 * Reset the petrinet -> sets it to the initial state. The initial state is
	 * reset when petrinet is modified.
	 */
	public void resetPetrinet() {
		petrinet.setState(reachabilityGraphModel.getInitialState());
		reachabilityGraphModel.setInitial();

	}

	/**
	 * Get the current file.
	 *
	 * @return the current file
	 */
	public File getCurrentFile() {
		return file;
	}

	/**
	 * Reset the reachability graph. Also resets the petrinet to the initial state.
	 */
	public void resetReachabilityGraph() {
		petrinet.setState(reachabilityGraphModel.getInitialState());
		reachabilityGraphModel.reset();

	}

	/**
	 * Analyse the petrinet.
	 *
	 * @return the petrinet analyser
	 */
	public PetrinetAnalyser analyse() {

		PetrinetAnalyser analyser = new PetrinetAnalyser(this);

		return analyser;
	}

	/**
	 * Return whether file has been changed.
	 *
	 * @return true, if file has been changed
	 */
	public boolean getFileChanged() {
		return fileChanged;
	}

	/**
	 * Write changes to the file.
	 */
	public void writeToFile() {
		if (file == null)// safety check
			return;
		writeToFile(getCurrentFile());
	}

	/**
	 * Write changes to the file provided.
	 *
	 * @param file The file changes are written to.
	 */
	public void writeToFile(File file) {

		// instantiate writer
		PNMLWopedWriter writer = new PNMLWopedWriter(file);
		writer.startXMLDocument();

		// add components to writer
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

		// set new file
		if (this.file != file)
			this.file = file;

		// update fileChanged
		fileChanged = false;

	}

	/**
	 * Set file changed.
	 *
	 * @param changed True, if file has been modified.
	 */
	public void setFileChanged(boolean changed) {
		fileChanged = changed;
	}

	/**
	 * Update the coordinates of an element.
	 * 
	 * @param id Id of the element.
	 * @param x  The new x.
	 * @param y  The new y.
	 */
	public void onPetrinetNodeDragged(String id, double x, double y) {

		// get the element
		PetrinetElement petrinetElement = petrinet.getPetrinetElement(id);

		if (petrinetElement == null)// safety check
			return;

		// if nothing has changed return
		if (petrinetElement.getX() == x && petrinetElement.getY() == y)
			return;

		// set new coordinates
		petrinet.setCoordinates(id, x, y);
	}

	/**
	 * Handle clicks in the reachability graph -> sets petrinet to the state that
	 * has been clicked and updates reachability graph model.
	 * 
	 * @param id Element that has been clicked.
	 */
	public void onReachabilityGraphNodeClicked(String id) {
		PetrinetState state = reachabilityGraphModel.getState(id);
		petrinet.setState(state);
		reachabilityGraphModel.setCurrentState(state);

	}

	/**
	 * Handle clicks in the petrinet graph -> if the element was a transition, fire
	 * it; otherwise return element for graph to handle it.
	 * 
	 * @param id Element that has been clicked.
	 * @return The element, if it was of type Place, null otherwise (or also if it
	 *         hasn't been found).
	 */
	public PetrinetElement onPetrinetNodeClicked(String id) {
		// get the element
		PetrinetElement pe = petrinet.getPetrinetElement(id);

		// if it is a transition, fire it
		if (pe instanceof Transition) {
			petrinet.fireTransition(id);
			return null;
		}

		// return element
		return pe;
	}

}