package net.berndreiss.petrinetsimulator;

import net.berndreiss.petrinetsimulator.gui.MainFrame;

/**
 * The main class that serves as the entry point for the Petrinet Simulator
 * program. This class initializes and displays the main application window.
 */
public class Main {

	public static int WIDTH = 0;
	public static int HEIGHT = 0;
	/**
	 * The main method that starts the application. It initializes an instance of
	 * {@link MainFrame}. This method is set to run on the Swing event dispatch
	 * thread for thread safety. 
	 *
	 * @param args command-line arguments, not used in this application
	 */
	public static void main(String[] args) {

		if (args.length == 2){
			try {
				WIDTH = Integer.parseInt(args[0]);
				HEIGHT = Integer.parseInt(args[1]);
			} catch (NumberFormatException e){
				System.out.println(e.getMessage());
				WIDTH = 0;
				HEIGHT = 0;
			}
		}

		// Set system property to avoid scale issues with GraphStream mouse click
		// recognition, particularly on high-DPI displays where UI scaling is applied.
		System.setProperty("sun.java2d.uiScale", "1.0");

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Initialize and display the main application window with the title.
				new MainFrame("Petrinet Simulator");

			}
		});

	}

}
