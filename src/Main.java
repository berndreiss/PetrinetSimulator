
import gui.MainFrame;

// TODO: Auto-generated Javadoc
/**
 * The Class Main.
 */
public class Main {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

//		System.setProperty("sun.java2d.uiScale", "1.0");
//System.out.println(System.getProperty("user.dir") + "/petrinet");
//		File[] directory = (new File(System.getProperty("user.dir") + "/src/petrinet")).listFiles();
//		
//		for (File f: directory)
//			System.out.println(f.getName());
//		System.exit(0);
//		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainFrame("Bernd Reiß 3223442");

			}
		});

	}

}
