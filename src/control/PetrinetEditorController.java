package control;

import javax.swing.JOptionPane;

import core.Petrinet;
import core.PetrinetElement;
import core.Place;
import core.Transition;
import exceptions.DuplicateIdException;
import exceptions.InvalidEdgeOperationException;
import gui.PetrinetGraph;
import listeners.ToolbarToggleListener;

// TODO: Auto-generated Javadoc
/**
 * <p> 
 * An editor for an instance of {@link Petrinet} being linked to an implementing instance of {@link PetrinetGraph}.
 * </p> 
 */
public class PetrinetEditorController {
	/** */
	private PetrinetGraph petrinetGraph;
	/** */
	private PetrinetViewerController petrinetController;
	/** */
	private Petrinet petrinet; 
	/** */
	private ToolbarToggleListener toolbarToggleListener;
	/** */
	private PetrinetElement[] addEdge;
	/** */
	private String edgeToAddId;
	/** */
	private PetrinetElement[] removeEdge;

	/** */
	private Transition transitionMarked;
	
	/**
	 * Instantiates a new editor.
	 * @param petrinetController 
	 * @param petrinetGraph 
	 */
	public PetrinetEditorController(PetrinetViewerController petrinetController, PetrinetGraph petrinetGraph, ToolbarToggleListener toolbarToggleListener) {
		this.petrinetController = petrinetController;
		this.petrinetGraph = petrinetGraph;
		this.petrinet = petrinetController.getPetrinet();
		this.toolbarToggleListener = toolbarToggleListener;
	}

	

	/**
	 * 
	 * @param label
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

		petrinetController.setFileChanged(true);// has to be set first so that petrinetQueue receives changes

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

		if (changed && !petrinetController.getFileChanged())
			petrinetController.setFileChanged(true);

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

		if (changed && !petrinetController.getFileChanged())
			petrinetController.setFileChanged(true);
		
		return changed;
	}
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
		petrinetController.setFileChanged(true);
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

		petrinetController.setFileChanged(true);
		petrinet.setAddedElementPosition(t);

		return true;
	}
/**
 * 
 */
	public void abortAddEdge() {
		addEdge = null;
	}
	
	/**
	 * Toggle add edge.
	 *
	 * @param id the id
	 * @return true, if successful
	 * @throws DuplicateIdException 
	 */
	public boolean addEdge(String id) throws DuplicateIdException {
		if (addsEdge()) {
			addEdge = null;
		}

		if (petrinet.containsElementWithId(id))
			throw new DuplicateIdException(id);
		edgeToAddId = id;
		addEdge = new PetrinetElement[2];
		PetrinetElement markedNode = petrinetGraph.getMarkedNode();
		if (markedNode != null)
			addEdge[0] = markedNode;

		if (removesEdge())
			removeEdge = null;
		return true;
	}

	/**
	 * Toggle remove edge.
	 *
	 * @return true, if successful
	 */
	public boolean toggleRemoveEdge() {
		if (removesEdge()) {
			removeEdge = null;
			return true;
		}

		removeEdge = new PetrinetElement[2];
		PetrinetElement markedNode = petrinetGraph.getMarkedNode();
		if (markedNode != null)
			removeEdge[0] = markedNode;

		if (addsEdge())
			addEdge = null;
		return true;
	}

	/**
	 * Adds edge.
	 *
	 * @return true, if successful
	 */
	public boolean addsEdge() {
		return addEdge != null;
	}

	/**
	 * Removes edge.
	 *
	 * @return true, if successful
	 */
	public boolean removesEdge() {
		return removeEdge != null;
	}

	/**
	 * Removes the component.
	 */
	public void removeComponent() {

		PetrinetElement markedElement = petrinetGraph.getMarkedNode();

		if (markedElement == null)
			return;
		petrinetController.setFileChanged(true);
		petrinet.removePetrinetElement(markedElement.getId());

	}

	/**
	 * Clicked node in graph.
	 * @param id 
	 */
	public void clickedNodeInGraph(String id){

		PetrinetElement petrinetElement = petrinet.getPetrinetElement(id);
		
		if (addsEdge()) {
			if (addEdge[0] == null) {
				addEdge[0] = petrinetElement;
				petrinetGraph.toggleNodeMark(petrinetElement);
				return;
			}

			if (addEdge[0] == petrinetElement) {
				addEdge[0] = null;
				petrinetGraph.toggleNodeMark(petrinetElement);
				return;
			}

			addEdge[1] = petrinetElement;

			try {
				petrinet.addEdge(addEdge[0], addEdge[1], edgeToAddId);
			} catch (InvalidEdgeOperationException | DuplicateIdException e ) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			addEdge = null;
			edgeToAddId = null;

			petrinetController.setFileChanged(true);
			petrinetGraph.toggleNodeMark(null);

			if (toolbarToggleListener != null)
				toolbarToggleListener.onEdgeAdded();

			return;
		}

		if (removesEdge()) {
			if (removeEdge[0] == null) {
				removeEdge[0] = petrinetElement;
				petrinetGraph.toggleNodeMark(petrinetElement);
				return;

			}
			if (removeEdge[0] == petrinetElement) {
				removeEdge[0] = null;
				petrinetGraph.toggleNodeMark(petrinetElement);
				return;
			}

			removeEdge[1] = petrinetElement;
			try {
				petrinet.removeEdge(removeEdge[0], removeEdge[1]);
			} catch (InvalidEdgeOperationException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
				return;

			}
			removeEdge = null;
			petrinetGraph.toggleNodeMark(null);
			petrinetController.setFileChanged(true);
			if (toolbarToggleListener != null)
				toolbarToggleListener.onEdgeRemoved();
			return;

		}

		petrinetGraph.toggleNodeMark(petrinetElement);

	}

}