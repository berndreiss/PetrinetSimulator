package control;

import java.io.File;

import javax.swing.JOptionPane;

import core.Added;
import core.PNMLParser;
import core.Petrinet;
import core.PetrinetAnalyser;
import core.PetrinetElement;
import core.PetrinetQueue;
import core.PetrinetState;
import core.Place;
import core.ReachabilityGraphModel;
import core.Transition;
import exceptions.PetrinetException;
import listeners.PetrinetStateChangedListener;
import listeners.ToolbarToggleListener;
import propra.pnml.PNMLWopedWriter;

// TODO: Auto-generated Javadoc
/**
 * The Class PetrinetController.
 */
public class PetrinetController {

	private Petrinet petrinet = new Petrinet();
	private ReachabilityGraphModel reachabilityGraphModel;

	private File file;

	private boolean fileChanged;

	private PetrinetQueue petrinetQueue;;

	private ToolbarToggleListener toolbarToggleListener;

	/**
	 * Instantiates a new petrinet controller.
	 *
	 * @param file     the file
	 * @param headless the headless
	 * @throws PetrinetException
	 */
	public PetrinetController(File file, boolean headless) throws PetrinetException {
		this.file = file;
		this.petrinet = new Petrinet();

		if (file != null)
			new PNMLParser(file, petrinet);

		this.reachabilityGraphModel = new ReachabilityGraphModel(petrinet);

		if (!headless)
			initializePetrinetQueue();

		petrinet.setPetrinetChangeListener(new PetrinetStateChangedListener() {

			@Override
			public void onTransitionFire(Transition t) {
				Added added = reachabilityGraphModel.addNewState(petrinet, t);

				if (petrinetQueue != null)
					petrinetQueue.push(reachabilityGraphModel.getCurrentState(),reachabilityGraphModel.getCurrentEdge(), added, t);
			}

			@Override
			public void onComponentChanged(Petrinet petrinet) {
				reachabilityGraphModel.reset();
				PetrinetState initialState = reachabilityGraphModel.getInitialState();
				if (initialState != null)
					reachabilityGraphModel.removeState(initialState);
				reachabilityGraphModel.addNewState(petrinet, null);
				if (petrinetQueue != null) {
					petrinetQueue.resetButtons();
					petrinetQueue = new PetrinetQueue(PetrinetController.this);
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
	 * Gets the reachability graph model.
	 *
	 * @return the reachability graph model
	 */
	public ReachabilityGraphModel getReachabilityGraphModel() {
		return reachabilityGraphModel;
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
		initializePetrinetQueue();
	}

	/**
	 * Analyse.
	 *
	 * @return the string[]
	 */
	public PetrinetAnalyser analyse() {

		PetrinetAnalyser analyser = new PetrinetAnalyser(this);

		if (petrinetQueue != null) {
			petrinetQueue.resetButtons();
			initializePetrinetQueue();
		}

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
	 * Sets the petrinet queue.
	 *
	 * @param petrinetQueue the new petrinet queue
	 */
	public void setPetrinetQueue(PetrinetQueue petrinetQueue) {
		this.petrinetQueue = petrinetQueue;
	}

	/**
	 * 
	 */
	public void initializePetrinetQueue() {
		this.petrinetQueue = new PetrinetQueue(this);
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
		reachabilityGraphModel.setCurrentEdge(null);
		petrinetQueue.push(reachabilityGraphModel.getCurrentState(),reachabilityGraphModel.getCurrentEdge(), Added.NOTHING, null);
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