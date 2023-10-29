package gui;

public interface MenuInterface {
	
	//METHODS FOR FILE
	
	void onNew();
		
	void onOpen();

	void onOpenInNewTab();
	
	void onSave();
	
	void onSaveAs();
	
	void onReload();
	
	void onAnalyseMany();
	
	void onClose();
	
	void onExit();
	
	//METHODS FOR EDIT
	
	void onOpenEditor();
	
	void onCloseEditor();

	//METHODS FOR HELP
	
	void onInfo();
}
