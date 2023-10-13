package view;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;


public class AbstractToolbar extends JToolBar {

	private static final long serialVersionUID = 1L;
	private static final String IMAGE_ROOT_FOLDER = "/resources/images/Toolbar/";


	public JButton makeToolbarButton(ToolbarImage toolbarImage, ActionListener actionListener, String toolTipText,
			String altText) {
		String imgLocation = IMAGE_ROOT_FOLDER + toolbarImage + ".png";
		String imagePath = System.getProperty("user.dir");

		JButton button = new JButton() {

			private static final long serialVersionUID = 1L;

			// TODO is this issue resolved without overriding the method?
			// Changing the getToolTipLocation() method to show it above the button because
			// it keeps interfering with the rendering of GraphStreams
//			@Override
//			public Point getToolTipLocation(MouseEvent event) {
//				// Display the tooltip 5 pixels above the button's top edge
//				return new Point(event.getX(), -getToolTipText().length());
//			}
		};
		button.addActionListener(actionListener);
		button.setToolTipText(toolTipText);

		int sizeInt = 30;

		Dimension size = new Dimension(sizeInt, sizeInt);

		ImageIcon icon = new ImageIcon(imagePath + imgLocation, altText);

		if (imagePath != null) {
			button.setIcon(icon);
		} else {
			button.setText(altText);
			System.err.println("Resource not found: " + imgLocation);
		}

		button.setMaximumSize(size);
		button.setSize(size);
		button.setPreferredSize(size);

		// repaint graph components when mouse leaves because tooltips mess with graph
		// rendering
		button.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent me) {

				ToolTipManager.sharedInstance().setEnabled(true);

			}

			@Override
			public void mouseExited(MouseEvent me) {
				ToolTipManager.sharedInstance().setEnabled(false);
			}
		});
		return button;

	}

	public enum ToolbarImage {
		OPEN("folder"), SAVE("save"), ANALYSE("stats"), RESTART("restart"), RESET("delete"), PLUS("plus"), MINUS("minus"), LEFT("left"),
		RIGHT("right"), UNDO("undo"), REDO("redo"), CLEAR_TEXT("input"), EDITOR("edit"),  DEFAULT("layout"),
		
		//additional images for Editor
		ADD_PLACE("add-circle"), ADD_TRANSITION("add-square"), ADD_EDGE("arc"), DELETE_COMPONENT("erase"), OPEN_VIEWER("eye"), REMOVE_EDGE("remove-edge")
		;

		private String name;

		ToolbarImage(String name) {
			this.name = name;

		}

		@Override
		public String toString() {
			return name;
		}

	}
}
