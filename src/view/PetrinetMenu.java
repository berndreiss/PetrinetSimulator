package view; 

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import control.MenuInterface;



public class PetrinetMenu extends JMenuBar {

	private static final long serialVersionUID = 1L;


	public PetrinetMenu(MenuInterface controller) {

		JMenu files = new JMenu("Files");
		JMenu edit = new JMenu("Edit");
		JMenu help = new JMenu("Help");

		this.add(files);
		this.add(edit);
		this.add(help);

		JMenuItem newMenuItem = new JMenuItem("New");
		JMenuItem openMenuItem = new JMenuItem("Open");
		JMenuItem reloadMenuItem = new JMenuItem("Reload");
		JMenuItem saveMenuItem = new JMenuItem("Save");
		JMenuItem saveAsMenuItem = new JMenuItem("Save as...");
		JMenuItem analyseManyMenuItem = new JMenuItem("Analyse++");
		JMenuItem closeMenuItem = new JMenuItem("Close");
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		JMenuItem openEditorMenuItem = new JMenuItem("Open Editor");
		JMenuItem closeEditorMenuItem = new JMenuItem("Close Editor");
		JMenuItem showInfoMenuItem = new JMenuItem("Info");

		files.add(newMenuItem);
		files.add(openMenuItem);
		files.add(reloadMenuItem);
		files.add(saveMenuItem);
		files.add(saveAsMenuItem);
		files.add(analyseManyMenuItem);
		files.add(closeMenuItem);
		files.add(exitMenuItem);
		edit.add(openEditorMenuItem);
		edit.add(closeEditorMenuItem);
		help.add(showInfoMenuItem);

		newMenuItem.addActionListener(e -> controller.onNew());
		
		openMenuItem.addActionListener(e -> controller.onOpen());

		reloadMenuItem.addActionListener(e -> controller.onReload());

		saveMenuItem.addActionListener(e -> controller.onSave());

		saveAsMenuItem.addActionListener(e -> controller.onSaveAs());

		analyseManyMenuItem.addActionListener(e -> controller.onAnalyseMany());

		closeMenuItem.addActionListener(e -> controller.onClose());

		exitMenuItem.addActionListener(e -> controller.onExit());

		openEditorMenuItem.addActionListener(e -> controller.onOpenEditor());
		
		closeEditorMenuItem.addActionListener(e -> controller.onCloseEditor());
		
		showInfoMenuItem.addActionListener(e -> controller.onInfo());
	}
}
