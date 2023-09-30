package view;

import org.graphstream.ui.view.ViewerListener;

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
	private DemoFrame frame;


	/**
	 * Erzeugt einen neuen ClickListener, der auf verschiedene Mausaktionen reagieren kann.
	 * @param frame Referenz auf die DemoFrame Instanz
	 */
	public ClickListener(DemoFrame frame) {
		this.frame = frame;
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
		frame.clickNodeInGraph(id);
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