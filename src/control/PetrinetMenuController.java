package control;

import javax.swing.LookAndFeel;

import gui.MainFrame;
import gui.PetrinetGraph;
import gui.GraphStreamPetrinetPanel;
import gui.ToolbarMode;

/**
 * An interface defining methods for entries in the menus.
 */
public interface PetrinetMenuController {

	// METHODS FOR FILE

	/**
	 * Opens a new {@link GraphStreamPetrinetPanel} containing an empty
	 * {@link PetrinetGraph} and sets the panel to ToolbarMode EDITOR
	 * (see {@link ToolbarMode}).
	 */
	void onNew();

	/**
	 * Opens a dialog to choose a pnml file from which to load a petrinet.
	 */
	void onOpen();

	/**
	 * Opens a dialog to choose a pnml file from which to load a petrinet and open
	 * it in a new tab.
	 */
	void onOpenInNewTab();

	/**
	 * Saves changes to the file the petrinet has been opened from.
	 */
	void onSave();

	/**
	 * Opens a dialog to choose a directory and file name to save the current
	 * petrinet to.
	 */
	void onSaveAs();

	/**
	 * Reloads the contents of the currently opened file.
	 */
	void onReload();

	/**
	 * Opens a dialog to choose (multiple) pnml file(s) to analyse. The results are
	 * printed in the text area of {@link MainFrame}.
	 */
	void onAnalyseMany();

	/**
	 * Closes the currently opened file.
	 */
	void onClose();

	/**
	 * Exits the program.
	 */
	void onExit();

	// METHODS FOR EDIT

	/**
	 * Switch to {@link ToolbarMode} EDITOR.
	 */
	void onOpenEditor();

	/**
	 * Switch to {@link ToolbarMode} VIEW.
	 */
	void onCloseEditor();

	/**
	 * Changes between the {@link LookAndFeel} "Nimbus" (default) and "Metal" (java
	 * default).
	 */

	void onChangeLookAndFeel();

	// METHODS FOR HELP

	/**
	 * Opens an info box showing java version and user directory.
	 */
	void onInfo();

	/**
	 * Enables automatic checks for boundedness.
	 */
	void enableAutomaticBoundednessCheck();

	/**
	 * Disables automatic checks for boundedness.
	 */
	void disableAutomaticBoundednessCheck();
}
