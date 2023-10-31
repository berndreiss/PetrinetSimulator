package gui;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * A custom {@link JMenuBar} consisting of 
 * File: 
 * -New 
 * -Open 
 * -Open in new Tab
 * -Reload 
 * -Save 
 * -Save as... 
 * -Analyse++ 
 * -Close 
 * -Exit
 * 
 * Edit: 
 * -Open Editor 
 * -Close Editor
 * -Change Design
 * 
 * Help: 
 * -Info
 * 
 * For a description of the functionality of these entries see
 * {@link PetrinetMenuInterface}.
 * 
 */
class PetrinetMenu extends JMenuBar {

	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new menu.
	 *
	 * @param menuController A controller implementing {@link PetrinetMenuInterface}.
	 */
	PetrinetMenu(PetrinetMenuInterface menuController) {

		// create menus and add the to menu bar
		JMenu files = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		JMenu help = new JMenu("Help");

		this.add(files);
		this.add(edit);
		this.add(help);

		//create menu items, add them to according menu and link to controller
		JMenuItem newMenuItem = new JMenuItem("New");
		JMenuItem openMenuItem = new JMenuItem("Open");
		JMenuItem openInNewTabMenuItem = new JMenuItem("Open in new Tab");
		JMenuItem reloadMenuItem = new JMenuItem("Reload");
		JMenuItem saveMenuItem = new JMenuItem("Save");
		JMenuItem saveAsMenuItem = new JMenuItem("Save as...");
		JMenuItem analyseManyMenuItem = new JMenuItem("Analyse++");
		JMenuItem closeMenuItem = new JMenuItem("Close");
		JMenuItem exitMenuItem = new JMenuItem("Exit");
		JMenuItem openEditorMenuItem = new JMenuItem("Open Editor");
		JMenuItem closeEditorMenuItem = new JMenuItem("Close Editor");
		JMenuItem changeDesignMenuItem = new JMenuItem("Change Design");
		JMenuItem showInfoMenuItem = new JMenuItem("Info");

		files.add(newMenuItem);
		files.add(openMenuItem);
		files.add(openInNewTabMenuItem);
		files.add(reloadMenuItem);
		files.add(saveMenuItem);
		files.add(saveAsMenuItem);
		files.add(analyseManyMenuItem);
		files.add(closeMenuItem);
		files.add(exitMenuItem);
		edit.add(openEditorMenuItem);
		edit.add(closeEditorMenuItem);
		edit.add(changeDesignMenuItem);
		help.add(showInfoMenuItem);

		newMenuItem.addActionListener(e -> menuController.onNew());

		openMenuItem.addActionListener(e -> menuController.onOpen());

		openInNewTabMenuItem.addActionListener(e -> menuController.onOpenInNewTab());

		reloadMenuItem.addActionListener(e -> menuController.onReload());

		saveMenuItem.addActionListener(e -> menuController.onSave());

		saveAsMenuItem.addActionListener(e -> menuController.onSaveAs());

		analyseManyMenuItem.addActionListener(e -> menuController.onAnalyseMany());

		closeMenuItem.addActionListener(e -> menuController.onClose());

		exitMenuItem.addActionListener(e -> menuController.onExit());

		openEditorMenuItem.addActionListener(e -> menuController.onOpenEditor());

		closeEditorMenuItem.addActionListener(e -> menuController.onCloseEditor());
		
		changeDesignMenuItem.addActionListener(e -> menuController.onChangeDesign());
		
		showInfoMenuItem.addActionListener(e -> menuController.onInfo());
	}
}
