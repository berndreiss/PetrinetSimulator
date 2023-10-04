package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.graphstream.ui.swing_viewer.ViewPanel;

import control.PetrinetController;

public class GraphSplitPane extends JSplitPane {

	private double defaultDividerRatio = 0.5;
	private JFrame parent;
	private PetrinetController controller;
	private JSplitPane container;
	

	public GraphSplitPane(JFrame parent, int splitOrientation, PetrinetController controller) {
		super(splitOrientation, new JPanel(), new JPanel());
		

		
		this.parent = parent;
		this.defaultDividerRatio = 0.5;
		this.controller = controller;

		Dimension preferredSize = new Dimension((int) (parent.getWidth()/2-10), (int) (parent.getHeight()*0.5));
		
		this.setLeftComponent(new ResizingViewPanel(GraphStreamView.initGraphStreamView(controller.getPetrinetGraph(), controller), preferredSize));
		
		this.setRightComponent(new ResizingViewPanel(GraphStreamView.initGraphStreamView(controller.getReachabilityGraph(), controller), preferredSize));

		parent.addComponentListener(new FrameResizeAdapter());
	}
	

	
	private class FrameResizeAdapter extends ComponentAdapter{
		
		@Override
		public void componentResized(ComponentEvent e) {
			
			Dimension oldSizeLeft = getLeftComponent().getSize();
			remove(getLeftComponent());
			setLeftComponent(new ResizingViewPanel(GraphStreamView.initGraphStreamView(controller.getPetrinetGraph(), controller), oldSizeLeft));
			Dimension oldSizeRight = getRightComponent().getSize();
			remove(getRightComponent());
			setRightComponent(new ResizingViewPanel(GraphStreamView.initGraphStreamView(controller.getReachabilityGraph(), controller), oldSizeRight));
			setDividerLocation(defaultDividerRatio);
		}
		
		 
	}
	
	
	private class ResizingViewPanel extends JPanel{
		
		public ResizingViewPanel(ViewPanel viewPanel, Dimension size) {
			this.setLayout(new BorderLayout());
			this.add(viewPanel, BorderLayout.CENTER);
			this.addComponentListener(new PanelResizedAdapter());
			Dimension zeroSize = new Dimension(0,0);

			this.setMinimumSize(zeroSize);

			this.setPreferredSize(size);
			
//			this.setSize(size);
		}

		
	}
	
	private class PanelResizedAdapter extends ComponentAdapter{
		
		@Override
		public void componentResized(ComponentEvent e) {
			if (getOrientation() == JSplitPane.HORIZONTAL_SPLIT)
				defaultDividerRatio = (double) getLeftComponent().getWidth() / (getLeftComponent().getWidth()+getRightComponent().getWidth());
			else
				defaultDividerRatio = (double) getLeftComponent().getHeight() / (getLeftComponent().getHeight()+getRightComponent().getHeight());

		}
		
	}
	
}
