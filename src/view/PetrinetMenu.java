package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.MenuBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import control.PetrinetController;

public class PetrinetMenu extends JMenuBar {
	
	JMenu menu;
	
	private JButton button;
	private PetrinetController controller;
	public PetrinetMenu(JFrame parent, PetrinetController controller) {
		this.controller = controller;

		
		
		
		menu = new JMenu("files");
		
		this.add(menu);
		
		JMenuItem filesMenuItem = new JMenuItem("open");
	
		filesMenuItem.addActionListener(null);
		
		
		
		filesMenuItem.addActionListener(e ->{
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new FileFilter() {
				
				@Override
				public String getDescription() {
					return "PNML files (.pnml)";
				}
				
				@Override
				public boolean accept(File f) {
					if (f.isDirectory())
						return true;
					
					return f.getName().endsWith(".pnml");
				}
			});
			
			fileChooser.setCurrentDirectory(new File("/home/bernd/eclipse-workspace/ProPra-WS23-Basis/Beispiele/"));
			int result = fileChooser.showOpenDialog(parent);
			File file = fileChooser.getSelectedFile();
			controller.onFileOpen(file);
			


		}); 
		menu.add(filesMenuItem);

	}
}
