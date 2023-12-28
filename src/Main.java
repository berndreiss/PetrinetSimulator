import gui.MainFrame;

/**
 * The main class that serves as the entry point for the Petrinet Simulator
 * program. This class initializes and displays the main application window.
 */
public class Main {

	/**
	 * The main method that starts the application. It initializes an instance of
	 * {@link MainFrame}. This method is set to run on the Swing event dispatch
	 * thread for thread safety.
	 *
	 * @param args command-line arguments, not used in this application
	 */
	public static void main(String[] args) {

		// Set system property to avoid scale issues with GraphStream mouse click
		// recognition, particularly on high-DPI displays where UI scaling is applied.
		System.setProperty("sun.java2d.uiScale", "1.0");

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Initialize and display the main application window with the title.
				new MainFrame("Bernd Reiß 3223442");

			}
		});

	}

}
