package datamodel;


public abstract class PetrinetElement implements Comparable<Transition>{
	private String id;
	private String name = "";
	
	private double x;
	private double y;
	

	public PetrinetElement(String id) {
		this.id = id;
	}
	
	/**
	 * Returns the id of the node.
	 * @return Id of the node.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns the name of the node.
	 * @return Name of the node.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Set the name of the node.
	 * @param name Name for node.
	 */
	protected void setName(String name) {
		this.name = name;
		
	}

	public double getX() {
		return x;
	}
	
	protected void setX(double x) {
		this.x=x;
	}

	public double getY() {
		return y;
	}
	
	protected void setY(double y) {
		this.y=y;
	}

	@Override
	public int compareTo(Transition o) {
		if (this.id.equals(o.getId()))
			return 0;
		
		return 1;
	}

	

}
