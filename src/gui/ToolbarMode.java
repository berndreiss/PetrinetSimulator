package gui;

import core.Petrinet;

// TODO: Auto-generated Javadoc
/**
 * <p>
 * An enum representing the two modes the toolbar can be in: VIEWER and EDITOR.
 * </p>
 * <p>
 * The two modes are represented by the different buttons the toolbar is
 * showing. The toolbar mode is kept track of in the {@link PetrinetPanel} ->
 * this way there can be different petrinets with different toolbar modes and
 * users can switch between them. Most notably in the VIEWER mode the
 * {@link Petrinet} can not be structurally modified. Only existing places can
 * be in-/decremented. Adding elements and edges between them is only possible
 * on entering EDITOR mode. But more importantly the two modes have implications
 * for what happens when a transition is clicked -> in VIEWER the transition is
 * fired if it is activated. In EDITOR the transition is selected just like
 * places but not fired.
 * </p>
 */
public enum ToolbarMode {

	/** The viewer mode */
	VIEWER,
	/** The editor mode */
	EDITOR;
}
