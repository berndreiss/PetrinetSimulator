package view;

import javax.swing.JButton;

import control.MainController;

public class EditorToolbar extends AbstractToolbar{

	private static final long serialVersionUID = 1L;

	public EditorToolbar(MainController controller) {
		
		//implement ZOOM

		
		JButton openButton = makeToolbarButton(ToolbarImage.OPEN, e -> controller.onOpen(), "Open file", "open");
		
		JButton saveButton = makeToolbarButton(ToolbarImage.SAVE, e -> controller.onSave(), "Save file", "save");
		
		JButton previousButton = makeToolbarButton(ToolbarImage.LEFT, e -> controller.onPrevious(), "Open the previous file", "previous");

		JButton nextButton = makeToolbarButton(ToolbarImage.RIGHT, e -> controller.onNext(), "Open the next file", "next");

		JButton addPlaceButton = makeToolbarButton(ToolbarImage.ADD_PLACE, e -> controller.onAddPlace(), "Add place", "add place");
		
		JButton addTransitionButton = makeToolbarButton(ToolbarImage.ADD_TRANSITION, e -> controller.onAddTransition(), "Add transition", "add trans");

		JButton deleteComponentButton = makeToolbarButton(ToolbarImage.DELETE_COMPONENT, e -> controller.onRemoveComponent(), "Delete component", "delete");

		JButton addEdgeButton = makeToolbarButton(ToolbarImage.ADD_EDGE, e -> controller.onAddEdge(), "Add edge", "add edge");

		JButton removeEdgeButton = makeToolbarButton(ToolbarImage.REMOVE_EDGE, e -> controller.onRemoveEdge(), "Remove an edge: choose source first and then target", "remove edge");
		
		JButton plusButton = makeToolbarButton(ToolbarImage.PLUS, e -> controller.onPlus(), "Adds a token to a selected place", "plus");

		JButton minusButton = makeToolbarButton(ToolbarImage.MINUS, e -> controller.onMinus(), "Removes a token from a selected place", "minus");

		JButton setDefaultButton = makeToolbarButton(ToolbarImage.DEFAULT, e -> controller.onSetDefault(), "Reset split panes", "reset pane");
				
		JButton closeEditorButton = makeToolbarButton(ToolbarImage.OPEN_VIEWER, e -> controller.onCloseEditor(), "Switch to viewer", "close editor");
		
		this.add(openButton);		
		this.add(saveButton);
		this.add(previousButton);
		this.add(nextButton);
		this.add(addPlaceButton);
		this.add(addTransitionButton);
		this.add(deleteComponentButton);
		this.add(addEdgeButton);
		this.add(removeEdgeButton);
		this.add(plusButton);
		this.add(minusButton);
		this.add(setDefaultButton);
		this.add(closeEditorButton);
	}
}
