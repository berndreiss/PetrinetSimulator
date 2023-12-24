package gui;

import core.PetrinetElement;

/**
 * <p>
 * Interface for a graph representing a petrinet.
 * </p>
 */
public interface PetrinetGraph {

	/**
	 * Gets the petrinet element represented by the marked node.
	 *
	 * @return petrinet element of the marked node
	 */
	PetrinetElement getMarkedNode();

	/**
	 * Marks the node representing the element if it is has not been selected and
	 * unmark the former marked node (if there was one). If the provided node is
	 * already marked unmark it. If null is passed unmark the currently marked node.
	 *
	 * @param petrinetElement rlement for which node should be marked / unmarked
	 *                        (null -> currently marked node is unmarked)
	 */
	void toggleNodeMark(PetrinetElement petrinetElement);
}
