package view;

import org.graphstream.algorithm.Toolkit;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import control.PetrinetController;
import datamodel.PetrinetElement;

/**
 * Dieser Listener reagiert auf Klicks in der Anzeige des Graphen.
 * 
 * <p>
 * Um einen Klick weiter verarbeiten zu können, benötigt der ClickListener eine
 * Referenz auf die Instanz der Klasse {@link DemoFrame}.
 * </p>
 * 
 * @author ProPra-Team FernUni Hagen
 */
public class ClickListener implements ViewerListener {

	/**
	 * Referenz auf die DemoFrame Instanz 
	 */
	private PetrinetController controller;
	
	private double x=Double.MAX_VALUE;
	private double y=Double.MAX_VALUE;

	/**
	 * Erzeugt einen neuen ClickListener, der auf verschiedene Mausaktionen reagieren kann.
	 * @param controller Referenz auf die DemoFrame Instanz
	 */
	public ClickListener(PetrinetController controller) {
		this.controller = controller;
	}

	@Override
	public void viewClosed(String viewName) {
		// wird nicht verwendet
	}

	@Override
	public void buttonPushed(String id) {

		PetrinetElement e = controller.getPetrinet().getPetrinetElement(id);
		this.x = e.getX();
		this.y = e.getY();
		
		// den frame darüber informieren, dass der Knoten id angeklickt wurde 
	}

	@Override
	public void buttonReleased(String id) {
		PetrinetElement e = controller.getPetrinet().getPetrinetElement(id);
		
		if (this.x == e.getX() && this.y == e.getY())	
			controller.clickNodeInGraph(id);

		resetCoordinates();
		// wird nicht verwendet
	}

	@Override
	public void mouseOver(String id) {
		// wird nicht verwendet
		
	}

	@Override
	public void mouseLeft(String id) {
		// wird nicht verwendet
	}

	
	private void resetCoordinates() {
		this.x = Integer.MAX_VALUE;
		this.y = Integer.MAX_VALUE;
	}
	
}