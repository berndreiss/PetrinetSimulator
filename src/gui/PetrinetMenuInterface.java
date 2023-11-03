package gui;

import javax.swing.LookAndFeel;

import control.PetrinetGraphEditor;

/**
 * An interface defining methods for entries in {@link PetrinetMenu}.
 */
public interface PetrinetMenuInterface {

	// METHODS FOR FILE

	/**
	 * Opens a new {@link PetrinetPanel} containing an empty
	 * {@link GraphStreamPetrinetGraph} and sets the panel to ToolbarMode EDITOR
	 * (see {@link ToolbarMode}).
	 */
	void onNew();

	/**
	 * Open a dialog to choose a pnml file from which to load a petrinet.
	 */
	void onOpen();

	/**
	 * Open a dialog to choose a pnml file from which to load a petrinet and open it
	 * in a new tab.
	 */
	void onOpenInNewTab();

	/**
	 * Save changes to the file the petrinet has been opened from.
	 */
	void onSave();

	/**
	 * Open a dialog to choose a directory and file name to save the current
	 * petrinet to.
	 */
	void onSaveAs();

	/**
	 * Reload the contents of the currently opened file.
	 */
	void onReload();

	/**
	 * Open a dialog to choose (multiple) pnml file(s) to analyse. The results are
	 * printed in the text area of {@link MainFrame}.
	 */
	void onAnalyseMany();

	/**
	 * Close the currently opened file.
	 */
	void onClose();

	/**
	 * Exit the program.
	 */
	void onExit();

	// METHODS FOR EDIT

	/**
	 * Open the {@link PetrinetGraphEditor}.
	 */
	void onOpenEditor();

	/**
	 * Close the {@link PetrinetGraphEditor}.
	 */
	void onCloseEditor();

	/**
	 * Change between the {@link LookAndFeel} "Nimbus" (default) and "Metal" (java
	 * default).
	 */

	void onChaneLookAndFeel();

	// METHODS FOR HELP

	/**
	 * Open an info box showing java version and user directory used.
	 */
	void onInfo();

}
