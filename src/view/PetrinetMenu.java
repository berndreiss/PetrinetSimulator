package view;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import control.PetrinetController;

public class PetrinetMenu extends JMenuBar {

	private static final long serialVersionUID = 1L;

	private File lastFileForFileChooser;

	private PetrinetController controller;

	public PetrinetMenu(JFrame parent, PetrinetController controller) {
		this.controller = controller;

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

		openMenuItem.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new FileFilter() {

				@Override
				public String getDescription() {
					return "PNML files (.pnml)";
				}

				@Override
				public boolean accept(File f) {
					if (f.isDirectory())
						return true;

					return f.getName().endsWith(".pnml");
				}
			});

			if (lastFileForFileChooser == null)
				lastFileForFileChooser = new File(System.getProperty("user.dir") + "/../ProPra-WS23-Basis/Beispiele/");

			fileChooser.setCurrentDirectory(lastFileForFileChooser);
			int result = fileChooser.showOpenDialog(parent);

			if (result == 0) {
				File file = fileChooser.getSelectedFile();
				controller.onFileOpen(file);
				lastFileForFileChooser = file.getParentFile();
			}
			
			
		});

		reloadMenuItem.addActionListener(e -> controller.reload());

		analyseManyMenuItem.addActionListener(e -> {

		});

		closeMenuItem.addActionListener(e -> controller.closeCurrent());

		exitMenuItem.addActionListener(e -> System.exit(0));

		showInfoMenuItem.addActionListener(e -> {
			JOptionPane.showMessageDialog(parent, "java.version = " + System.getProperty("java.version")
					+ "\n\nuser.dir = " + System.getProperty("user.dir") + "\n", "Information",
					JOptionPane.PLAIN_MESSAGE);
		});
	}
}
