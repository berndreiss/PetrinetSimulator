package gui;

/**
 * The Enum ToolbarImage.
 */
public enum ToolbarImage {

	/** The open. */
	OPEN("folder"),
	/** The save. */
	SAVE("save"),
	/** The analyse. */
	ANALYSE("stats"),
	/** The restart. */
	RESTART("restart"),
	/** The reset. */
	RESET("delete"),
	/** The plus. */
	PLUS("plus"),

	/** The minus. */
	MINUS("minus"),
	/** The left. */
	LEFT("left"),
	/** The right. */
	RIGHT("right"),
	/** The undo. */
	UNDO("undo"),
	/** The redo. */
	REDO("redo"),
	/** The clear text. */
	CLEAR_TEXT("input"),
	/** The editor. */
	EDITOR("edit"),

	/** The default. */
	DEFAULT("layout"),
	/** The zoom in. */
	ZOOM_IN("zoom-in"),
	/** The zoom out. */
	ZOOM_OUT("zoom-out"),

	
	// ADDITIONAL IMAGES FOR EDITOR
	
	/** The add place. */
	ADD_PLACE("add-circle"),
	/** The add transition. */
	ADD_TRANSITION("add-square"),
	/** The add edge. */
	ADD_EDGE("arc"),

	/** The delete component. */
	DELETE_COMPONENT("erase"),
	/** The open viewer. */
	OPEN_VIEWER("eye"),
	/** The remove edge. */
	REMOVE_EDGE("remove-edge"),

	/** The add label. */
	ADD_LABEL("label"),
	/** The auto layout. */
	AUTO_LAYOUT("auto-layout"),

	/** The tree layout. */
	TREE_LAYOUT("tree-layout"),
	/** The circle layout. */
	CIRCLE_LAYOUT("circle-layout"),
	/** The design. */
	DESIGN("design");

	private static final String IMAGE_ROOT_FOLDER = "/resources/images/Toolbar/";
	private String name;

	/**
	 * Instantiates a new toolbar image.
	 *
	 * @param name the name
	 */
	ToolbarImage(String name) {
		this.name = name;

	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * 
	 * @param toolbarImage
	 * @return
	 */
	public String imagePath() {
		return IMAGE_ROOT_FOLDER + this + ".png";

	}
}
