package gui;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * <p>
 * A custom {@link JMenuBar} consisting of</p>
 * <ul>
 *     <li>File:</li>
 *     <ul>
 *         <li>New</li>
 *         <li>Open</li>
 *         <li>Open in new Tab</li>
 *         <li>Reload</li>
 *         <li>Save</li>
 *         <li>Save as...</li>
 *         <li>Analyse++</li>
 *         <li>Close</li>
 *         <li>Exit</li>
 *     </ul>
 * 
 *     <li>Edit:</li>
 *     <ul>
 *         <li>Open Editor</li>
 *         <li>Close Editor</li>
 *         <li>Change Look and Feel</li>
 *         <li>Enable Automatic Boundedness Check</li>
 *         <li>Disable Automatic Boundedness Check</li>
 *     </ul>
 * 
 *     <li>Help:</li>
 *     <ul>
 *         <li>Info</li>
 *     </ul>
 * </ul>
 * 
 * <p>
 * 
 * For a description of the functionality of these entries see
 * {@link PetrinetMenuController}.
 * 
 * </p>
 */
public class PetrinetMenu extends JMenuBar {

	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new menu.
	 *
	 * @param menuController A controller implementing
	 *                       {@link PetrinetMenuController}.
	 */
	PetrinetMenu(PetrinetMenuController menuController) {

		// create menus and add the to menu bar
		JMenu files = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		JMenu help = new JMenu("Help");

		this.add(files);
		this.add(edit);
		this.add(help);

		// create menu items, add them to according menu and link to controller
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
		JMenuItem changeLookAndFeelMenuItem = new JMenuItem("Change Look and Feel");
		JMenuItem enableAutomaticBoundednessMenuItem = new JMenuItem("Enable Automatic Boundedness Check");
		JMenuItem disableAutomaticBoundednessMenuItem = new JMenuItem("Disable Automatic Boundedness Check");
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
		edit.add(changeLookAndFeelMenuItem);
		edit.add(enableAutomaticBoundednessMenuItem);
		edit.add(disableAutomaticBoundednessMenuItem);
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

		changeLookAndFeelMenuItem.addActionListener(e -> menuController.onChaneLookAndFeel());

		enableAutomaticBoundednessMenuItem.addActionListener(e -> menuController.enableAutomaticBoundednessCheck());
		
		disableAutomaticBoundednessMenuItem.addActionListener(e -> menuController.disableAutomaticBoundednessCheck());
		
		showInfoMenuItem.addActionListener(e -> menuController.onInfo());
	}
}
