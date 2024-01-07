package control;

import java.io.File;

import core.PNMLParser;
import core.Petrinet;
import core.PetrinetAnalyser;
import core.PetrinetElement;
import core.ReachabilityGraphUndoQueue;
import core.PetrinetState;
import core.Place;
import core.ReachabilityGraphModel;
import core.Transition;
import exceptions.PetrinetException;
import gui.ToolbarMode;
import listeners.ToolbarChangedListener;
import util.PNMLWopedWriter;

/**
 * <p>
 * Controller handling a petrinet model.
 * </p>
 * 
 * <p>
 * It also creates and handles a reachability graph model linked to petrinet
 * (see {@link ReachabilityGraphModel}).
 * </p>
 */
public class PetrinetViewerController {

	/** The petrinet being handled. */
	private Petrinet petrinet = new Petrinet();
	/** The reachability graph model for the petrinet. */
	private ReachabilityGraphModel reachabilityGraphModel;

	/** The file being processed. */
	private File file;

	/** True if the file has been changed. */
	private boolean fileChanged;

	/**
	 * Instantiates a new petrinet controller.
	 *
	 * @param file            the file that is loaded
	 * @param toolbarListener listener for highlighting toolbar buttons
	 * @param toolbarMode     toolbar mode to be used
	 * @throws PetrinetException thrown by PNMLParser
	 */
	public PetrinetViewerController(File file, ToolbarChangedListener toolbarListener, ToolbarMode toolbarMode)
			throws PetrinetException {
		this.file = file;
		this.petrinet = new Petrinet();

		// load file if it was provided
		if (file != null)
			new PNMLParser(file, petrinet);

		if (toolbarMode == ToolbarMode.VIEWER && !petrinet.isConnected())
			throw new PetrinetException(
					"Petrinet is not connected. You can not view it but still open it in the editor.");

		// create reachability graph model
		this.reachabilityGraphModel = new ReachabilityGraphModel(petrinet, toolbarListener);

	}

	/**
	 * Gets the reachability undo queue.
	 *
	 * @return the reachability undo queue
	 */
	public ReachabilityGraphUndoQueue getPetrinetQueue() {
		return reachabilityGraphModel.getUndoQueue();
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
	 * Gets the reachability graph model.
	 *
	 * @return the reachability graph model
	 */
	public ReachabilityGraphModel getReachabilityGraphModel() {
		return reachabilityGraphModel;
	}

	/**
	 * Resets the petrinet -> sets it to the initial state. The initial state is
	 * reset when petrinet is modified.
	 */
	public void resetPetrinet() {
		petrinet.setState(reachabilityGraphModel.getInitialState());
		reachabilityGraphModel.setInitial();

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
	 * Resets the reachability graph. Also resets the petrinet to the initial state.
	 */
	public void resetReachabilityGraph() {
		petrinet.setState(reachabilityGraphModel.getInitialState());
		reachabilityGraphModel.reset();

	}

	/**
	 * Analyses the petrinet.
	 *
	 * @return the petrinet analyser
	 */
	public PetrinetAnalyser analyse() {

		PetrinetAnalyser analyser = new PetrinetAnalyser(this);

		return analyser;
	}

	/**
	 * Returns whether file has been changed.
	 *
	 * @return true, if file has been changed
	 */
	public boolean getFileChanged() {
		return fileChanged;
	}

	/**
	 * Writes changes to the file.
	 */
	void writeToFile() {
		if (file == null)// safety check
			return;
		writeToFile(getCurrentFile());
	}

	/**
	 * Writes changes to the file provided.
	 *
	 * @param file the file changes are written to
	 */
	void writeToFile(File file) {

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
	 * Sets file changed.
	 *
	 * @param changed true, if file has been modified
	 */
	public void setFileChanged(boolean changed) {
		fileChanged = changed;
	}

	/**
	 * Updates the coordinates of an element.
	 * 
	 * @param id id of the element
	 * @param x  the new x
	 * @param y  the new y
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
	 * Handles clicks in the reachability graph -> sets petrinet to the state that
	 * has been clicked and updates reachability graph model.
	 * 
	 * @param id element that has been clicked
	 */
	public void onReachabilityGraphNodeClicked(String id) {
		PetrinetState state = reachabilityGraphModel.getState(id);
		petrinet.setState(state);
		reachabilityGraphModel.setCurrentState(state);

	}

	/**
	 * Handles clicks in the petrinet graph -> if the element was a transition, fire
	 * it; otherwise return element for graph to handle it.
	 * 
	 * @param id dlement that has been clicked
	 * @return the element, if it was of type Place, null otherwise (or also if it
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