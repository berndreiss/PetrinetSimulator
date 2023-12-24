package gui;

/**
 * An enum listing all available ToolbarImages and defining their name for the
 * file in the IMAGE_ROOT_FOLDER (originally obtained from
 * <a href="https://iconoir.com/">iconoir.com</a>).
 */
public enum ToolbarImage {

	/** Image for the open button. */
	OPEN("folder"),
	/** Image for the save button. */
	SAVE("save"),
	/** Image for the analyse button. */
	ANALYSE("stats"),
	/** Image for the restart petrinet button. */
	RESTART("restart"),
	/** Image for the delete button. */
	RESET("delete"),
	/** Image for the plus button. */
	PLUS("plus"),
	/** Image for the minus button. */
	MINUS("minus"),
	/** Image for the previous file button. */
	LEFT("left"),
	/** Image for the next file button. */
	RIGHT("right"),
	/** Image for the undo button. */
	UNDO("undo"),
	/** Image for the redo button. */
	REDO("redo"),
	/** Image for the clear text area button. */
	CLEAR_TEXT("input"),
	/** Image for the open editor button. */
	EDITOR("edit"),
	/** Image for the reset split panes button. */
	DEFAULT("layout"),
	/** Image for the zoom in buttons. */
	ZOOM_IN("zoom-in"),
	/** Image for the zoom out buttons. */
	ZOOM_OUT("zoom-out"),
	/** Image for the auto layout button. */
	AUTO_LAYOUT("auto-layout"),
	/** Image for the tree layout button. */
	TREE_LAYOUT("tree-layout"),
	/** Image for the circle layout button. */
	CIRCLE_LAYOUT("circle-layout"),
	/** Image for the change look and feel button. */
	LAF("design"),

	// ADDITIONAL IMAGES FOR EDITOR

	/** Image for the add place button. */
	ADD_PLACE("add-circle"),
	/** Image for the add transition button. */
	ADD_TRANSITION("add-square"),
	/** Image for the add edge button. */
	ADD_EDGE("arc"),
	/** Image for the delete component button. */
	DELETE_COMPONENT("erase"),
	/** Image for the open viewer button. */
	OPEN_VIEWER("eye"),
	/** Image for the remove edge button. */
	REMOVE_EDGE("remove-edge"),
	/** Image for the add label button. */
	ADD_LABEL("label");

	/** Path from user.dir to image root folder. */
	private static final String IMAGE_ROOT_FOLDER = "/resources/images/Toolbar/";
	/** File name of the image resource. */
	private String fileName;

	/**
	 * Instantiates a new toolbar image.
	 *
	 * @param fileName the name of the file
	 */
	ToolbarImage(String fileName) {
		this.fileName = fileName;

	}

	/**
	 * Gets the name of the file associated with the image resource.
	 *
	 * @return the file name of the image
	 */
	@Override
	public String toString() {
		return fileName;
	}

	/**
	 * Gets the path to the image root folder.
	 * 
	 * @return path to image root folder
	 */
	public String imagePath() {
		return IMAGE_ROOT_FOLDER + this + ".png";

	}
}
