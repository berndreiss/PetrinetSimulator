package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import control.MainController;
import control.ToolbarMode;
import util.OnEditedListener;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	public static final double GRAPH_PERCENT = 0.5;

	private ResizableSplitPane splitPane;

	private JTabbedPane tabbedPane;
	private JScrollPane scrollPane;
	private JTextArea textArea;

	private JLabel statusLabel;

	private JToolBar viewerToolbar;
	private JToolBar editorToolbar;

	private MainController controller;

	
	public MainFrame(String title) {
		super(title);
		textArea = new JTextArea();
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

		scrollPane = new JScrollPane(textArea);


//		setEmtpy();

		
		tabbedPane = new JTabbedPane();


		splitPane = new ResizableSplitPane(this, JSplitPane.VERTICAL_SPLIT);
		splitPane.setDefaultRatio(0.8);
		splitPane.setLeftComponent(tabbedPane);
		splitPane.setRightComponent(scrollPane);
	
		
		// Erzeuge ein Label, welches als Statuszeile dient, ...
		// ... und zeige dort ein paar hilfreiche Systeminfos an, ...
		statusLabel = new JLabel();
		// ... und füge es zum Haupt-Frame hinzu
		this.add(statusLabel, BorderLayout.SOUTH);

		
		controller = new MainController(this);


		JMenuBar menuBar = new PetrinetMenu(controller);

		this.setJMenuBar(menuBar);

		viewerToolbar = new PetrinetToolbar(controller);

		editorToolbar = new EditorToolbar(controller);

		viewerToolbar.setFloatable(false);
		editorToolbar.setFloatable(false);

		editorToolbar.setVisible(false);

		JPanel toolbarPanel = new JPanel();

		FlowLayout toolbarPanelLayout = new FlowLayout();
		toolbarPanelLayout.setAlignment(FlowLayout.LEFT);
		toolbarPanel.setLayout(toolbarPanelLayout);
		toolbarPanel.add(viewerToolbar);
		toolbarPanel.add(editorToolbar);

		this.add(toolbarPanel, BorderLayout.NORTH);
		add(splitPane, BorderLayout.CENTER);


//		f.setSize(400, 240);
//	      f.setLocationRelativeTo(null);
		// bestimme eine geeignete Fenstergröße in Abhängigkeit von der
		// Bildschirmauflösung
		double heightPerc = 0.7; // relative Höhe des Fensters bzgl. der der Bildschirmhöhe (1.0), hier also 60 %
		double aspectRatio = 16.0 / 10.0; // Seitenverhältnis des Fensters
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int h = (int) (screenSize.height * heightPerc);
		int w = (int) (h * aspectRatio);
		setBounds((screenSize.width - w) / 2, (screenSize.height - h) / 2, w, h);

		// Konfiguriere weitere Parameter des Haupt-Frame
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

	}

	public ResizableSplitPane getSplitPane() {
		return splitPane;
	}

	public void print(String s) {
		textArea.append(s);
	}

	public void clearTextArea() {
		textArea.setText("");
	}

	public void setStatusLabel(String status) {
		statusLabel.setText(status);
	}
	

	public void setToolBarMode(ToolbarMode toolbarMode) {
		if (toolbarMode == null)
			return;

		if (toolbarMode == ToolbarMode.EDITOR) {
			editorToolbar.setVisible(true);
			viewerToolbar.setVisible(false);
		}
		if (toolbarMode == ToolbarMode.VIEWER) {
			viewerToolbar.setVisible(true);
			editorToolbar.setVisible(false);
		}
	}
	
	public JTabbedPane getTabbedPane() {
		return tabbedPane;
	}

	public EditorToolbar getEditorToolbar() {
		return (EditorToolbar) editorToolbar;
	}
	
//	public void setTabbedPane() {
//		splitPane.remove(splitPane.getLeftComponent());
//		splitPane.setLeftComponent(tabbedPane);
//	}

//	public void setEmtpy() {
//		splitPane.remove(splitPane.getLeftComponent());
//		JPanel dummyPanel = new JPanel();
//		dummyPanel.setPreferredSize(
//				new Dimension(getWidth(), (int) (getHeight() * MainFrame.GRAPH_PERCENT)));
//		getSplitPane().setLeftComponent(dummyPanel);
//
//	}
	
}
