package datamodel;

public abstract class PetrinetElement implements Comparable<Transition>{
	protected String id;
	protected String name;
	
	protected float x;
	protected float y;
	
	
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
	public void setName(String name) {
		this.name = name;
		
	}

	public float getX() {
		return x;
	}
	
	public void setX(float x) {
		this.x=x;
	}

	public float getY() {
		return y;
	}
	
	public void setY(float y) {
		this.y=y;
	}

	@Override
	public int compareTo(Transition o) {
		if (this.id.equals(o.id))
			return 0;
		
		return 1;
	}
}
