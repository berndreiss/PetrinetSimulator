package view;

import org.graphstream.ui.view.ViewerListener;

import control.PetrinetController;

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


	/**
	 * Erzeugt einen neuen ClickListener, der auf verschiedene Mausaktionen reagieren kann.
	 * @param controller Referenz auf die DemoFrame Instanz
	 */
	public ClickListener(PetrinetController controller) {
		this.controller = controller;
	}

	@Override
	public void viewClosed(String viewName) {
		System.out.println("ClickListener - viewClosed: " + viewName);
		// wird nicht verwendet
	}

	@Override
	public void buttonPushed(String id) {
		System.out.println("ClickListener - buttonPushed: " + id);

		// den frame darüber informieren, dass der Knoten id angeklickt wurde 
		controller.clickNodeInGraph(id);
	}

	@Override
	public void buttonReleased(String id) {
		System.out.println("ClickListener - buttonReleased: " + id);
		// wird nicht verwendet
	}

	@Override
	public void mouseOver(String id) {
		System.out.println("ClickListener - mouseOver: " + id);
		// wird nicht verwendet
		
	}

	@Override
	public void mouseLeft(String id) {
		System.out.println("ClickListener - mouseLeft: " + id);
		// wird nicht verwendet
	}
}