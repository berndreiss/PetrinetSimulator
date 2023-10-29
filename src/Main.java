
import gui.MainFrame;

public class Main {

	public static void main(String[] args) {

		System.setProperty("sun.java2d.uiScale", "1.0");

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainFrame("Bernd Reiß 3223442");

			}
		});

	}

}
