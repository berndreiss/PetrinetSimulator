package core;

import javax.swing.JOptionPane;

import control.PetrinetController;
import exceptions.DuplicateIdException;
import exceptions.InvalidEdgeOperationException;

// TODO: Auto-generated Javadoc
/**
 * The Class Editor.
 */
public class Editor {

	private PetrinetController controller;

	private PetrinetElement[] addEdge;
	private String edgeToAddId;
	private PetrinetElement[] removeEdge;

	/**
	 * Instantiates a new editor.
	 *
	 * @param controller the controller
	 */
	public Editor(PetrinetController controller) {
		this.controller = controller;
	}

	/**
	 * Adds the place.
	 *
	 * @param id the id
	 * @return true, if successful
	 * @throws DuplicateIdException the duplicate id exception
	 */
	public boolean addPlace(String id) throws DuplicateIdException {

		Place p = controller.getPetrinet().addPlace(id);
		if (p == null)
			return false;
		controller.setFileChanged(true);
		controller.getPetrinet().setAddedElementPosition(p);

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

		Transition t = controller.getPetrinet().addTransition(id);

		if (t == null)
			return false;

		controller.setFileChanged(true);
		controller.getPetrinet().setAddedElementPosition(t);

		return true;
	}

	/**
	 * Toggle add edge.
	 *
	 * @param id the id
	 * @return true, if successful
	 */
	public boolean toggleAddEdge(String id) {
		if (addsEdge()) {
			addEdge = null;
			return true;
		}

		if (controller.getPetrinet().hasEdgeWithId(id))
			return false;
		edgeToAddId = id;
		addEdge = new PetrinetElement[2];
		PetrinetElement markedNode = controller.getPetrinetGraph().getMarkedNode();
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
		PetrinetElement markedNode = controller.getPetrinetGraph().getMarkedNode();
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

		PetrinetElement markedElement = controller.getPetrinetGraph().getMarkedNode();

		if (markedElement == null)
			return;
		controller.setFileChanged(true);
		controller.getPetrinet().removePetrinetElement(markedElement.getId());

	}

	/**
	 * Clicked node in graph.
	 *
	 * @param pe the pe
	 */
	public void clickedNodeInGraph(PetrinetElement pe) {

		if (addsEdge()) {
			if (addEdge[0] == null) {
				addEdge[0] = pe;
				controller.getPetrinetGraph().toggleNodeMark(pe);
				return;
			}

			if (addEdge[0] == pe) {
				addEdge[0] = null;
				controller.getPetrinetGraph().toggleNodeMark(pe);
				return;
			}

			addEdge[1] = pe;

			try {
				controller.getPetrinet().addEdge(addEdge[0], addEdge[1], edgeToAddId);
			} catch (InvalidEdgeOperationException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			addEdge = null;
			edgeToAddId = null;

			controller.setFileChanged(true);
			controller.getPetrinetGraph().toggleNodeMark(null);

			if (controller.getToolbarToggleListener() != null)
				controller.getToolbarToggleListener().onEdgeAdded();

			return;
		}

		if (removesEdge()) {
			if (removeEdge[0] == null) {
				removeEdge[0] = pe;
				controller.getPetrinetGraph().toggleNodeMark(pe);
				return;

			}
			if (removeEdge[0] == pe) {
				removeEdge[0] = null;
				controller.getPetrinetGraph().toggleNodeMark(pe);
				return;
			}

			removeEdge[1] = pe;
			try {
				controller.getPetrinet().removeEdge(removeEdge[0], removeEdge[1]);
			} catch (InvalidEdgeOperationException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "", JOptionPane.INFORMATION_MESSAGE);
				return;

			}
			removeEdge = null;
			controller.getPetrinetGraph().toggleNodeMark(null);
			controller.setFileChanged(true);
			if (controller.getToolbarToggleListener() != null)
				controller.getToolbarToggleListener().onEdgeRemoved();
			return;

		}

		controller.getPetrinetGraph().toggleNodeMark(pe);

	}

}
