package control;

import javax.swing.JOptionPane;

import core.Petrinet;
import core.PetrinetElement;
import core.Place;
import core.Transition;
import exceptions.DuplicateIdException;
import exceptions.InvalidEdgeOperationException;
import gui.PetrinetGraph;
import listeners.ToolbarChangeListener;

//TODO when adding edges nodes are not untoggled and toolbar is not toggled
// TODO: Auto-generated Javadoc
/**
 * <p>
 * An editor for an instance of {@link Petrinet} being linked to an implementing
 * instance of {@link PetrinetGraph}.
 * </p>
 */
public class PetrinetEditorController {
	/** An instance of an implementation of petrinet graph. */
	private PetrinetGraph petrinetGraph;
	/** The view controller for the petrinet. */
	private PetrinetViewerController petrinetViewerController;
	/** The petrinet itself. */
	private Petrinet petrinet;
	/** Listener for changes of toolbar buttons. */
	private ToolbarChangeListener toolbarChangeListener;
	/** Keeps track of whether edge is being added. */
	private boolean addsEdge = false;
	/**
	 * Keeps track of source when adding an edge. Null, if no edge is being addded.
	 */
	private PetrinetElement addEdgeSource;
	/** Keeps track of id for edge to add.*/
	private String edgeToAddId;
	/** Keeps track of whether edge is being removed. */
	private boolean removesEdge = false;
	/** */
	private PetrinetElement removeEdgeSource;

	/** */
	private Transition transitionMarked;

	/**
	 * Instantiates a new editor.
	 * 
	 * @param petrinetViewerController The controller for the petrinet view.
	 * @param petrinetGraph            The instance implementing petrinet graph.
	 * @param toolbarToggleListener
	 */
	public PetrinetEditorController(PetrinetViewerController petrinetViewerController, PetrinetGraph petrinetGraph,
			ToolbarChangeListener toolbarToggleListener) {
		this.petrinetViewerController = petrinetViewerController;
		this.petrinetGraph = petrinetGraph;
		this.petrinet = petrinetViewerController.getPetrinet();
		this.toolbarChangeListener = toolbarToggleListener;
	}

	/**
	 * 
	 */

	public boolean setLabel() {
		PetrinetElement markedNode = petrinetGraph.getMarkedNode();

		if (markedNode == null)
			return false;

		String label = JOptionPane.showInputDialog(null, "Enter label for element:");

		if (label == null)
			return false;

		if (markedNode.getName().equals(label))
			return false;

		petrinetViewerController.setFileChanged(true);// has to be set first so that petrinetQueue receives changes

		petrinet.setPetrinetElementLabel(markedNode.getId(), label);

		return true;

	}

	/**
	 * Increment marked place.
	 *
	 * @return true, if successful
	 */
	public boolean incrementMarkedPlace() {

		PetrinetElement petrinetElement = petrinetGraph.getMarkedNode();
		boolean changed = petrinet.incrementPlace(petrinetElement);

		if (changed && !petrinetViewerController.getFileChanged())
			petrinetViewerController.setFileChanged(true);

		return changed;
	}

	/**
	 * Decrement marked place.
	 *
	 * @return true, if successful
	 */
	public boolean decrementMarkedPlace() {
		PetrinetElement petrinetElement = petrinetGraph.getMarkedNode();

		boolean changed = petrinet.decrementPlace(petrinetElement);

		if (changed && !petrinetViewerController.getFileChanged())
			petrinetViewerController.setFileChanged(true);

		return changed;
	}

	// TODO adding place causes compiler error due to
	/**
	 * Adds the place.
	 *
	 * @param id the id
	 * @return true, if successful
	 * @throws DuplicateIdException the duplicate id exception
	 */
	public boolean addPlace(String id) throws DuplicateIdException {

		Place p = petrinet.addPlace(id);
		if (p == null)
			return false;
		petrinetViewerController.setFileChanged(true);
		petrinet.setAddedElementPosition(p);

		return true;
	}

	/**
	 * Adds the transition.
	 *
	 * @param id the id
	 * @return true, if successful
	 * @throws DuplicateIdException the duplicate id exception
	 */
	public boolean addTransition(String id) throws DuplicateIdException {

		Transition t = petrinet.addTransition(id);

		if (t == null)
			return false;

		petrinetViewerController.setFileChanged(true);
		petrinet.setAddedElementPosition(t);

		return true;
	}

	/**
	 * 
	 */
	public void abortAddEdge() {
		addEdgeSource = null;
		addsEdge = false;
	}
	/**
	 * 
	 */
	public void abortRemoveEdge() {
		removeEdgeSource = null;
		removesEdge = false;
	}
	/**
	 * Toggle add edge.
	 *
	 * @param id the id
	 * @throws DuplicateIdException
	 */
	public void addEdge(String id) throws DuplicateIdException {
		if (addsEdge()) {
			addsEdge = false;;
			addEdgeSource = null;
			return;
		}

		addsEdge = true;
		if (petrinet.containsElementWithId(id))
			throw new DuplicateIdException(id);
		edgeToAddId = id;
		PetrinetElement markedNode = petrinetGraph.getMarkedNode();
		if (markedNode != null)
			addEdgeSource = markedNode;

		if (removesEdge())
			removeEdgeSource = null;
	}

	/**
	 * Toggle remove edge.
	 *
	 * @return true, if successful
	 */
	public void toggleRemoveEdge() {
		if (removesEdge()) {
			removesEdge = false;
			removeEdgeSource = null;
		}
		removesEdge = true;

		PetrinetElement markedNode = petrinetGraph.getMarkedNode();
		if (markedNode != null)
			removeEdgeSource = markedNode;

		if (addsEdge())
			addEdgeSource = null;
	}

	/**
	 * Adds edge.
	 *
	 * @return true, if successful
	 */
	public boolean addsEdge() {
		return addsEdge;
	}

	/**
	 * Removes edge.
	 *
	 * @return true, if successful
	 */
	public boolean removesEdge() {
		return removesEdge;
	}

	/**
	 * Removes the component.
	 */
	public void removeComponent() {

		PetrinetElement markedElement = petrinetGraph.getMarkedNode();

		if (markedElement == null)
			return;
		petrinetViewerController.setFileChanged(true);
		petrinet.removePetrinetElement(markedElement.getId());

	}

	/**
	 * Clicked node in graph.
	 * 
	 * @param id
	 */
	public void clickedNodeInGraph(String id) {

		PetrinetElement edgeTarget = petrinet.getPetrinetElement(id);

		if (addsEdge()) {
			if (addEdgeSource == null) {
				addEdgeSource = edgeTarget;
				petrinetGraph.toggleNodeMark(edgeTarget);
				return;
			}

			if (addEdgeSource == edgeTarget) {
				addEdgeSource = null;
				petrinetGraph.toggleNodeMark(edgeTarget);
				return;
			}

			try {
				petrinet.addEdge(addEdgeSource, edgeTarget, edgeToAddId);
			} catch (InvalidEdgeOperationException | DuplicateIdException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			addEdgeSource = null;
			edgeToAddId = null;

			petrinetViewerController.setFileChanged(true);
			petrinetGraph.toggleNodeMark(null);

			if (toolbarChangeListener != null)
				toolbarChangeListener.onEdgeAdded();

			return;
		}

		if (removesEdge()) {
			if (removeEdgeSource == null) {
				removeEdgeSource = edgeTarget;
				petrinetGraph.toggleNodeMark(edgeTarget);
				return;

			}
			if (removeEdgeSource == edgeTarget) {
				removeEdgeSource = null;
				petrinetGraph.toggleNodeMark(edgeTarget);
				return;
			}

			try {
				petrinet.removeEdge(removeEdgeSource, edgeTarget);
			} catch (InvalidEdgeOperationException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
				return;

			}
			removeEdgeSource = null;
			petrinetGraph.toggleNodeMark(null);
			petrinetViewerController.setFileChanged(true);
			if (toolbarChangeListener != null)
				toolbarChangeListener.onEdgeRemoved();
			return;

		}

		petrinetGraph.toggleNodeMark(edgeTarget);

	}

}