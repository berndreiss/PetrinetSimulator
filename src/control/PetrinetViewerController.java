package control;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.plaf.basic.BasicBorders.ToggleButtonBorder;

import core.AddedType;
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
import listeners.PetrinetStateChangedListener;
import listeners.ToolbarToggleListener;
import propra.pnml.PNMLWopedWriter;

// TODO: Auto-generated Javadoc
/**
 * The Class PetrinetController.
 */
public class PetrinetViewerController {

	private Petrinet petrinet = new Petrinet();
	private ReachabilityGraph reachabilityGraphModel;

	private File file;

	private boolean fileChanged;


	/**
	 * Instantiates a new petrinet controller.
	 *
	 * @param file     the file
	 * @param toolbarToggleListener 
	 * @throws PetrinetException
	 */
	public PetrinetViewerController(File file, ToolbarToggleListener toolbarToggleListener) throws PetrinetException {
		this.file = file;
		this.petrinet = new Petrinet();

		if (file != null)
			new PNMLParser(file, petrinet);

		this.reachabilityGraphModel = new ReachabilityGraph(petrinet, toolbarToggleListener);

	}

	/**
	 * Gets the petrinet queue.
	 *
	 * @return the petrinet queue
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
	public ReachabilityGraph getReachabilityGraphModel() {
		return reachabilityGraphModel;
	}

	/**
	 * Reset petrinet.
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
	 * Reset reachability graph.
	 */
	public void resetReachabilityGraph() {
		petrinet.setState(reachabilityGraphModel.getInitialState());
		reachabilityGraphModel.reset();

	}

	/**
	 * Analyse.
	 *
	 * @return the string[]
	 */
	public PetrinetAnalyser analyse() {

		PetrinetAnalyser analyser = new PetrinetAnalyser(this);

		return analyser;
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
	 * 
	 * @param id
	 * @param x
	 * @param y
	 */
	public void onPetrinetNodeDragged(String id, double x, double y) {

		PetrinetElement petrinetElement = petrinet.getPetrinetElement(id);

		if (petrinetElement == null)// safety check
			return;

		if (petrinetElement.getX() == x && petrinetElement.getY() == y)
			return;
		petrinet.setCoordinates(id, x, y);
	}

	/**
	 * 
	 * @param id
	 */
	public void onReachabilityGraphNodeClicked(String id) {
		PetrinetState state = reachabilityGraphModel.getState(id);
		petrinet.setState(state);
		reachabilityGraphModel.setCurrentState(state);

	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public PetrinetElement onPetrinetNodeClicked(String id) {
		PetrinetElement pe = petrinet.getPetrinetElement(id);
		if (pe instanceof Transition) {
			petrinet.fireTransition(id);
			return null;
		}
		return pe;
	}

}