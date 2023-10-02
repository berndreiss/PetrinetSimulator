package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import control.Controller;

public class Menu extends JPanel {
	
	private JButton button;
	private Controller controller;
	public Menu(JFrame parent, Controller controller) {
		this.controller = controller;
		button=new JButton("Get file");
		button.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("TEST");
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
				

				
			}
		}); 
		add(button);
		
	}

}
