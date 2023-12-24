package core;

/**
 * The Class PetrinetElement.
 */
public abstract class PetrinetElement implements Comparable<PetrinetElement> {
	private String id;
	private String name = "";

	private double x;
	private double y;

	/**
	 * Instantiates a new petrinet element.
	 *
	 * @param id the id
	 */
	PetrinetElement(String id) {
		this.id = id;
	}

	/**
	 * Returns the id of the node.
	 * 
	 * @return id of the node
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the name of the node.
	 * 
	 * @return name of the node
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the node.
	 * 
	 * @param name name for node
	 */
	protected void setName(String name) {
		this.name = name;

	}

	/**
	 * Gets the x.
	 *
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * Sets the x.
	 *
	 * @param x the new x
	 */
	protected void setX(double x) {
		this.x = x;
	}

	/**
	 * Gets the y.
	 *
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * Sets the y.
	 *
	 * @param y the new y
	 */
	protected void setY(double y) {
		this.y = y;
	}

	/**
	 * Compare to.
	 *
	 * @param o the other element
	 * @return 0 if ids are the same, 1 otherwise
	 */
	@Override
	public int compareTo(PetrinetElement o) {
		if (this.id.equals(o.getId()))
			return 0;

		return 1;
	}

}
