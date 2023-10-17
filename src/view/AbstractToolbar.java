package view;

import java.awt.Color;
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

		JButton button = new JButton();
		
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
		return button;

	}

	public enum ToolbarImage {
		OPEN("folder"), SAVE("save"), ANALYSE("stats"), RESTART("restart"), RESET("delete"), PLUS("plus"), MINUS("minus"), LEFT("left"),
		RIGHT("right"), UNDO("undo"), REDO("redo"), CLEAR_TEXT("input"), EDITOR("edit"),  DEFAULT("layout"),
		
		//additional images for Editor
		ADD_PLACE("add-circle"), ADD_TRANSITION("add-square"), ADD_EDGE("arc"), DELETE_COMPONENT("erase"), OPEN_VIEWER("eye"), REMOVE_EDGE("remove-edge"), ADD_LABEL("label")
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
