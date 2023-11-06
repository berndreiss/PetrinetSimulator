import gui.MainFrame;

/**
 * This is the main class being the starting point for the program. No other
 * class contains a main method.
 */
public class Main {

	/**
	 * The main method: starts an instance of {@link MainFrame}.
	 *
	 * @param args Not used.
	 */
	public static void main(String[] args) {

		// avoid GraphStream not recognizing mouse clicks because of scale issues
		System.setProperty("sun.java2d.uiScale", "1.0");

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainFrame("Bernd Reiß 3223442");

			}
		});

	}

}
