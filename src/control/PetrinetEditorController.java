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
	/** Keeps track of id for edge to add. */
	private String edgeToAddId;
	/** Keeps track of whether edge is being removed. */
	private boolean removesEdge = false;
	/** Keeps track of the source of the edge to be removed. */
	private PetrinetElement removeEdgeSource;

	/**
	 * Instantiates a new editor.
	 * 
	 * @param petrinetViewerController The controller for the petrinet view.
	 * @param petrinetGraph            The instance implementing petrinet graph.
	 * @param toolbarChangeListener    Listener for change of the toolbar buttons.
	 */
	public PetrinetEditorController(PetrinetViewerController petrinetViewerController, PetrinetGraph petrinetGraph,
			ToolbarChangeListener toolbarChangeListener) {
		this.petrinetViewerController = petrinetViewerController;
		this.petrinetGraph = petrinetGraph;
		this.petrinet = petrinetViewerController.getPetrinet();
		this.toolbarChangeListener = toolbarChangeListener;
	}

	/**
	 * Set new label for the marked node in the petrinet graph.
	 * 
	 * @return true, if label has been set
	 */

	public boolean setLabel() {

		// get marked node in the petrinet graph
		PetrinetElement markedNode = petrinetGraph.getMarkedNode();

		// if no node is marked return false
		if (markedNode == null)
			return false;

		// get label from user
		String label = JOptionPane.showInputDialog(null, "Enter label for element:");

		// if no valid label has been provided return false
		if (label == null)
			return false;

		// if label is already the same, return false
		if (markedNode.getName().equals(label))
			return false;
		// inform petrinet viewer controller that the file has been changed -> has to be
		// set first so that petrinetQueue receives changes
		petrinetViewerController.setFileChanged(true);

		// set the label
		petrinet.setPetrinetElementLabel(markedNode.getId(), label);

		return true;

	}

	/**
	 * Increment marked place.
	 *
	 * @return true, if successful
	 */
	public boolean incrementMarkedPlace() {

		// get the marked node from the petrinet graph
		PetrinetElement petrinetElement = petrinetGraph.getMarkedNode();
		// keep track of whether element has been changed
		boolean changed = petrinet.incrementPlace(petrinetElement);
		// inform the petrinet viewer controller about changes
		if (changed)
			petrinetViewerController.setFileChanged(true);

		return changed;
	}

	/**
	 * Decrement marked place.
	 *
	 * @return true, if successful
	 */
	public boolean decrementMarkedPlace() {

		// get the marked node from the petrinet graph
		PetrinetElement petrinetElement = petrinetGraph.getMarkedNode();

		// keep track of whether element has been changed
		boolean changed = petrinet.decrementPlace(petrinetElement);

		// inform the petrinet viewer controller about changes
		if (changed)
			petrinetViewerController.setFileChanged(true);

		return changed;
	}

	/**
	 * Adds a place.
	 *
	 * @param id the id of the place to be added
	 * @return true, if successful
	 * @throws DuplicateIdException thrown if id already exists
	 */
	public boolean addPlace(String id) throws DuplicateIdException {

		// add place and keep track of it
		Place p = petrinet.addPlace(id);

		// if no place has been added return false
		if (p == null)
			return false;

		// inform petrinet viewer controller about changes
		petrinetViewerController.setFileChanged(true);

		// set newly added element to be above the top left most element in the petrinet
		petrinet.setAddedElementPosition(p);

		return true;
	}

	/**
	 * Adds a transition.
	 *
	 * @param id the id of the transition to be added
	 * @return true, if successful
	 * @throws DuplicateIdException thrown if id already exists
	 */
	public boolean addTransition(String id) throws DuplicateIdException {

		// add transition and keep track of it
		Transition t = petrinet.addTransition(id);
		// if no transition has been added return false
		if (t == null)
			return false;
		// inform petrinet viewer controller about changes
		petrinetViewerController.setFileChanged(true);
		// set newly added element to be above the top left most element in the petrinet
		petrinet.setAddedElementPosition(t);

		return true;
	}

	/**
	 * Abort adding edge.
	 */
	public void abortAddEdge() {
		addEdgeSource = null;
		addsEdge = false;
		edgeToAddId = null;
	}

	/**
	 * Abort removing edge.
	 */
	public void abortRemoveEdge() {
		removeEdgeSource = null;
		removesEdge = false;
	}

	/**
	 * Add an edge. If already adding an edge, adding edge gets aborted.
	 *
	 * @param id the id of the edge to be added
	 * @throws DuplicateIdException thrown if id already exists
	 */
	public void addEdge(String id) throws DuplicateIdException {
		// if already adding edge, abort adding edge
		if (addsEdge()) {
			abortAddEdge();
			return;
		}

		addsEdge = true;

		// check for duplicate id
		if (petrinet.containsElementWithId(id))
			throw new DuplicateIdException(id);

		edgeToAddId = id;

		// get the source of the edge if there is a marked node
		PetrinetElement markedNode = petrinetGraph.getMarkedNode();
		if (markedNode != null)
			addEdgeSource = markedNode;

		// if removing edge, abort
		if (removesEdge())
			abortRemoveEdge();
	}

	/**
	 * Remove an edge. If already removing an edge, removing edge gets aborted.
	 *
	 */
	public void removeEdge() {
		if (removesEdge()) {
			abortRemoveEdge();
			return;
		}

		removesEdge = true;

		// get the source of the edge if there is a marked node
		PetrinetElement markedNode = petrinetGraph.getMarkedNode();
		if (markedNode != null)
			removeEdgeSource = markedNode;

		// if adding edge, abort
		if (addsEdge())
			addEdgeSource = null;
	}

	/**
	 * Check whether editor is in the process of adding an edge.
	 *
	 * @return true, if in process of adding an edge
	 */
	public boolean addsEdge() {
		return addsEdge;
	}

	/**
	 * Check whether editor is in the process of removing an edge.
	 *
	 * @return true, if in process of removing an edge
	 */
	public boolean removesEdge() {
		return removesEdge;
	}

	/**
	 * Removes the component being marked in the petrinet graph.
	 */
	public void removeComponent() {

		// get the marked element
		PetrinetElement markedElement = petrinetGraph.getMarkedNode();

		// if no element is marked, return
		if (markedElement == null)
			return;

		// inform petrinet viewer controller about changes
		petrinetViewerController.setFileChanged(true);
		// remove the element
		petrinet.removePetrinetElement(markedElement.getId());

	}

	/**
	 * Handles clicks on nodes in the petrinet graph.
	 * 
	 * @param id id of the element being clicked
	 */
	public void clickedNodeInGraph(String id) {

		// get currently marked element
		PetrinetElement petrinetElement = petrinet.getPetrinetElement(id);

		// if in the process of adding an edge set edge source or, if source already
		// exists, add the edge
		if (addsEdge()) {

			// if source does not exist, set source and mark element in graph
			if (addEdgeSource == null) {
				addEdgeSource = petrinetElement;
				petrinetGraph.toggleNodeMark(petrinetElement);
				return;
			}

			// if source if marked element, set source to null and unmark element in graph
			if (addEdgeSource == petrinetElement) {
				addEdgeSource = null;
				petrinetGraph.toggleNodeMark(petrinetElement);
				return;
			}

			// else add edge and catch duplicate id exception
			try {
				petrinet.addEdge(addEdgeSource, petrinetElement, edgeToAddId);
			} catch (InvalidEdgeOperationException | DuplicateIdException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			abortAddEdge();

			// inform petrinet viewer controller about changes
			petrinetViewerController.setFileChanged(true);

			// TODO does not work!
			// unmark element in petrinet graph
			petrinetGraph.toggleNodeMark(null);

			// TODO does not unmark button
			// inform the listener
			if (toolbarChangeListener != null)
				toolbarChangeListener.onEdgeAdded();

			return;
		}

		// if in the process of removing an edge set edge source or, if source already
		// exists, remove the edge
		if (removesEdge()) {
			// if source does not exist, set source and mark element in graph
			if (removeEdgeSource == null) {
				removeEdgeSource = petrinetElement;
				petrinetGraph.toggleNodeMark(petrinetElement);
				return;

			}
			// if source if marked element, set source to null and unmark element in graph
			if (removeEdgeSource == petrinetElement) {
				removeEdgeSource = null;
				petrinetGraph.toggleNodeMark(petrinetElement);
				return;
			}

			// else remove edge and catch invalid edge operation exception
			try {
				petrinet.removeEdge(removeEdgeSource, petrinetElement);
			} catch (InvalidEdgeOperationException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
				return;

			}
			abortRemoveEdge();
			// TODO does not work!
			// unmark element in petrinet graph
			petrinetGraph.toggleNodeMark(null);
			// inform petrinet viewer controller about changes
			petrinetViewerController.setFileChanged(true);
			// TODO does not unmark button
			// inform the listener
			if (toolbarChangeListener != null)
				toolbarChangeListener.onEdgeRemoved();
			return;

		}

		// if none of the above, simply mark/unmark element
		petrinetGraph.toggleNodeMark(petrinetElement);

	}

}