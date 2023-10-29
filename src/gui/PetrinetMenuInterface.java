package gui;

// TODO: Auto-generated Javadoc
/**
 * The Interface MenuInterface.
 */
public interface PetrinetMenuInterface {
	
	//METHODS FOR FILE
	
	/**
	 * On new.
	 */
	void onNew();
		
	/**
	 * On open.
	 */
	void onOpen();

	/**
	 * On open in new tab.
	 */
	void onOpenInNewTab();
	
	/**
	 * On save.
	 */
	void onSave();
	
	/**
	 * On save as.
	 */
	void onSaveAs();
	
	/**
	 * On reload.
	 */
	void onReload();
	
	/**
	 * On analyse many.
	 */
	void onAnalyseMany();
	
	/**
	 * On close.
	 */
	void onClose();
	
	/**
	 * On exit.
	 */
	void onExit();
	
	//METHODS FOR EDIT
	
	/**
	 * On open editor.
	 */
	void onOpenEditor();
	
	/**
	 * On close editor.
	 */
	void onCloseEditor();

	//METHODS FOR HELP
	
	/**
	 * On info.
	 */
	void onInfo();
}
