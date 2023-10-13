package view;

import javax.swing.JButton;

import control.PetrinetToolbarInterface;

public class PetrinetToolbar extends AbstractToolbar {

	private static final long serialVersionUID = 1L;


	public PetrinetToolbar(PetrinetToolbarInterface controller) {

		//implement ZOOM
		
		JButton openButton = makeToolbarButton(ToolbarImage.OPEN, e -> controller.onOpen(), "Open file", "open");
		
		JButton saveButton = makeToolbarButton(ToolbarImage.SAVE, e -> controller.onSave(), "Save file", "save");
		
		JButton previousButton = makeToolbarButton(ToolbarImage.LEFT, e -> controller.onPrevious(), "Open the previous file", "previous");

		JButton nextButton = makeToolbarButton(ToolbarImage.RIGHT, e -> controller.onNext(), "Open the next file", "next");

		JButton restartButton = makeToolbarButton(ToolbarImage.RESTART, e -> controller.onRestart(), "Reset the petrinet graph", "Reset p");

		JButton plusButton = makeToolbarButton(ToolbarImage.PLUS, e -> controller.onPlus(), "Adds a token to a selected place", "plus");

		JButton minusButton = makeToolbarButton(ToolbarImage.MINUS, e -> controller.onMinus(), "Removes a token from a selected place", "minus");

		JButton resetButton = makeToolbarButton(ToolbarImage.RESET, e -> controller.onReset(), "Reset the reachability graph", "reset r");

		JButton analyseButton = makeToolbarButton(ToolbarImage.ANALYSE, e -> controller.onAnalyse(), "Analyse petrinet and create reachability graph", "analyse");
		
		JButton clearTextButton = makeToolbarButton(ToolbarImage.CLEAR_TEXT, e -> controller.onClear(), "Clear text area", "clear");

		JButton setDefaultButton = makeToolbarButton(ToolbarImage.DEFAULT, e -> controller.onSetDefault(), "Reset split panes", "reset pane");
		
		JButton undoButton = makeToolbarButton(ToolbarImage.UNDO, e->controller.onUndo(), "Undo last step", "undo");
		
		JButton redoButton = makeToolbarButton(ToolbarImage.REDO, e->controller.onRedo(), "Redo last step", "redo");
		
		JButton openEditButton = makeToolbarButton(ToolbarImage.EDITOR, e->controller.onOpenEditor(), "Switch to editor", "editor");
		
		this.add(openButton);		
		this.add(saveButton);
		this.add(previousButton);
		this.add(nextButton);
		this.add(analyseButton);
		this.add(restartButton);
		this.add(resetButton);
		this.add(plusButton);
		this.add(minusButton);
		this.add(undoButton);
		this.add(redoButton);
		this.add(clearTextButton);
		this.add(setDefaultButton);
		this.add(openEditButton);
		
	}





}
