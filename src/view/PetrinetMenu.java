package view; 

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import control.MenuInterface;



public class PetrinetMenu extends JMenuBar {

	private static final long serialVersionUID = 1L;


	public PetrinetMenu(MenuInterface controller) {

		JMenu files = new JMenu("Files");
		JMenu help = new JMenu("Help");

		this.add(files);
		this.add(help);

		JMenuItem openMenuItem = new JMenuItem("Open");
		JMenuItem reloadMenuItem = new JMenuItem("Reload");
		JMenuItem analyseManyMenuItem = new JMenuItem("Analyse++");
		JMenuItem closeMenuItem = new JMenuItem("Close");
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		JMenuItem showInfoMenuItem = new JMenuItem("Info");

		files.add(openMenuItem);
		files.add(reloadMenuItem);
		files.add(analyseManyMenuItem);
		files.add(closeMenuItem);
		files.add(exitMenuItem);
		help.add(showInfoMenuItem);

		openMenuItem.addActionListener(e -> controller.onOpen());

		reloadMenuItem.addActionListener(e -> controller.onReload());

		analyseManyMenuItem.addActionListener(e -> controller.onAnalyseMany());

		closeMenuItem.addActionListener(e -> controller.onClose());

		exitMenuItem.addActionListener(e -> controller.onExit());

		showInfoMenuItem.addActionListener(e -> controller.onInfo());
	}
}
