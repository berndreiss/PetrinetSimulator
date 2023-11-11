package core;

/**
 * <p>
 * The type of element that has been added to the petrinet state model.
 * </p>
 */
public enum AddedType {

	/** A state has been added. */
	STATE,
	/** An edge has been added. */
	EDGE,
	/** Nothing has been added (state has been reset only). */
	NOTHING;
}
