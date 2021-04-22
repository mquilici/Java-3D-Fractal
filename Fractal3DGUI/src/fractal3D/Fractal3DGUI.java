package fractal3D;

/*
3D Mandelbrot fractal drawing program
Last Updated April 22, 2021
Copyright 2021
@version 1.0
@author Michael Quilici
*/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Hashtable;


// Class to create GUI and define input callbacks
@SuppressWarnings("serial")
public class Fractal3DGUI extends JFrame implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

	public static Fractal3DGUI frame;
	private mainDraw draw;
	private Point mousePt;
	private final Action action_decrease_resolution = new SwingAction_Decrease_Resolution();
	private final Action action_increase_resolution = new SwingAction_Increase_Resolution();
	private final Action action_perspective = new SwingAction_Perspective();
  
    // Define keyPressed callback for actions like rotating the objects
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
	    	draw.moveTheta(-10,0);
	    else if (e.getKeyCode() == KeyEvent.VK_LEFT)
	    	draw.moveTheta(10,0);
	    else if (e.getKeyCode() == KeyEvent.VK_DOWN)
	    	draw.moveTheta(0,10);
	    else if (e.getKeyCode() == KeyEvent.VK_UP)
	    	draw.moveTheta(0,-10);
	    else if (e.getKeyCode() == KeyEvent.VK_P)
	    	draw.changePerspective();
	    else if (e.getKeyCode() == KeyEvent.VK_2)
	    	draw.changeResolution(50);
	    else if (e.getKeyCode() == KeyEvent.VK_1)
	    	draw.changeResolution(-50);
	    else if (e.getKeyCode() == KeyEvent.VK_4)
	    	draw.changeOrder(1);
	    else if (e.getKeyCode() == KeyEvent.VK_3)
	    	draw.changeOrder(-1);
	    else if (e.getKeyCode() == KeyEvent.VK_Q)
	    	System.exit(0);
	    else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
	    	System.exit(0);
		draw.setTheta();
	}
	
	// Required callback when keys are released
	public void keyReleased(KeyEvent e) {
	}

	// Required callback when keys are typed
	public void keyTyped(KeyEvent e) {
	}

	// Mouse pressed callback to get the starting point for rotation
	public void mousePressed(MouseEvent e) {
		mousePt = e.getPoint();
	}

	// Mouse released callback sets orientation parameters to their last value
	// otherwise fractal will snap back to the orientation before rotating
	public void mouseReleased(MouseEvent e) {
		if (e.getButton()==MouseEvent.BUTTON3) {
			draw.setXY();
		} else {
			draw.setTheta();
		}
	}

	// Required callback when mouse enters bounds of listened-to component
	public void mouseEntered(MouseEvent e) {
	}
	
	// Required callback when mouse exits bounds of listened-to component
	public void mouseExited(MouseEvent e) {
	}

	// Required callback when mouse is clicked
	public void mouseClicked(MouseEvent e) {
	}
   
	// Mouse dragged callback used to rotate and move fractal
	public void mouseDragged(MouseEvent e) {
		if (e.getButton()==MouseEvent.BUTTON3) {
			double dx = e.getX() - mousePt.x;
			double dy = e.getY() - mousePt.y;
			draw.moveXY(dx/2,dy/2);
		} else {
			double dx = e.getX() - mousePt.x;
			double dy = e.getY() - mousePt.y;
			draw.moveTheta(-dx/5,dy/5);
		}
	}
   
	// Required callback when mouse is moved
	public void mouseMoved(MouseEvent e) {
	}

	// Mouse wheel callback used to scale an object or change perspective
    public void mouseWheelMoved(MouseWheelEvent e) {
    	if (e.isShiftDown()) {
        	if (e.getWheelRotation() < 0) {
        		draw.moveZ(-50);
        	} else {
        		draw.moveZ(50);
            }
    	} else {
	    	if (e.getWheelRotation() < 0) {
	    		draw.zoom(0.1);
	    	} else {
	    		draw.zoom(-0.1);
	        }
    	}
    }

    // Method to implement GUI components
	public Fractal3DGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.draw = new mainDraw();
		
		// Add listeners
	    addKeyListener(this);
	    addMouseListener(this);
	    addMouseMotionListener(this);
	    addMouseWheelListener(this);
	    setFocusable(true);
	    setFocusTraversalKeysEnabled(false);
	    
		// Define top panel for buttons
	    JPanel top_panel = new JPanel();
	    getContentPane().add(top_panel, BorderLayout.NORTH);
	    
			// Define slider to adjust transparency of fractal center
		    JSlider transCenterSlider = new JSlider(JSlider.HORIZONTAL,0,255,1);
		    transCenterSlider.setPaintLabels(true);
		    
			// Define label for slider
		    Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		    labelTable.put(128, new JLabel("Center"));
		    transCenterSlider.setLabelTable(labelTable);
		    transCenterSlider.setToolTipText("Center Transparency");
		    transCenterSlider.setValue(mainDraw.center_color.getAlpha());
		    
			// Add slider to JPanel
		    top_panel.add(transCenterSlider);
		    transCenterSlider.setFocusable(false);
		    transCenterSlider.addChangeListener(new ChangeListener() {
	            public void stateChanged(ChangeEvent e) {
	                draw.setCenterTransparency(((JSlider)e.getSource()).getValue());
	            }
	        });
		    
		    // Add spacer
		    Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		    top_panel.add(horizontalStrut_1);
	    
		    // Define slider to adjust center transparency of fractal body
		    JSlider transSlider = new JSlider(JSlider.HORIZONTAL,0,255,1);
		    transSlider.setPaintLabels(true);

			// Define label for slider
		    Hashtable<Integer, JLabel> labelTable2 = new Hashtable<Integer, JLabel>();
		    labelTable2.put(128, new JLabel("Fractal"));
		    transSlider.setLabelTable(labelTable2);
		    transSlider.setToolTipText("Fractal Transparency");
		    transSlider.setValue(mainDraw.fractal_color.getAlpha());
		    
			// Add slider to JPanel
		    top_panel.add(transSlider);
		    transSlider.setFocusable(false);
		    transSlider.addChangeListener(new ChangeListener() {
	            public void stateChanged(ChangeEvent e) {
	                draw.setTransparency(((JSlider)e.getSource()).getValue());
	            }
	        });
		    
		    // Add spacer
		    Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		    top_panel.add(horizontalStrut_2);
		    
		    // Define slider to adjust pixel size
		    JSlider pixSlider = new JSlider(JSlider.HORIZONTAL,0,25,1);
		    pixSlider.setPaintLabels(true);

			// Define label for slider
		    Hashtable<Integer, JLabel> labelTable3 = new Hashtable<Integer, JLabel>();
		    labelTable3.put(12, new JLabel("Pixel") );
		    pixSlider.setLabelTable(labelTable3);
		    
			// Add slider to JPanel
		    pixSlider.setToolTipText("Pixel Size");
		    pixSlider.setValue(mainDraw.pixel_size);
		    top_panel.add(pixSlider);
		    pixSlider.setFocusable(false);
		    pixSlider.addChangeListener(new ChangeListener() {
	            public void stateChanged(ChangeEvent e) {
	                draw.setPixelSize(((JSlider)e.getSource()).getValue());
	            }
	        });
		    
		    // Add spacer
		    Component horizontalStrut_3 = Box.createHorizontalStrut(20);
		    top_panel.add(horizontalStrut_3);
		    
		    // Define button to decrease the fractal_resolution of the fractal
		    JButton btnDecreaseResolution = new JButton("Decrease Resolution");
		    top_panel.add(btnDecreaseResolution);
		    btnDecreaseResolution.setFocusable(false);
		    btnDecreaseResolution.setAction(action_decrease_resolution);
		    
		    /// Define button to increase the fractal_resolution of the fractal
		    JButton btnIncreaseResolution = new JButton("Increase Resolution");
		    top_panel.add(btnIncreaseResolution);
		    btnIncreaseResolution.setFocusable(false);
		    btnIncreaseResolution.setAction(action_increase_resolution);
		    
		    // Add spacer
		    Component horizontalStrut_4 = Box.createHorizontalStrut(20);
		    top_panel.add(horizontalStrut_4);
		    
		    // Define check box for enabling perspective projection
		    JCheckBox chckbxPerspective = new JCheckBox("Perspective");
		    top_panel.add(chckbxPerspective);
		    chckbxPerspective.setFocusable(false);
		    chckbxPerspective.setSelected(mainDraw.perspective_projection);
		    chckbxPerspective.setAction(action_perspective);

	}

	// Main method for GUI class
	public static void main(String[] args) {
		
		// Define maximum window size as a fraction of screen size
		float windowToScreenScale = 0.85f;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double screenHeight = screenSize.getHeight();
		double screenWidth = screenSize.getWidth();
		int width = (int)(screenSize.getWidth()*windowToScreenScale);
		int height = (int)(screenSize.getHeight()*windowToScreenScale);
		
		// Get center of window
		int x0 = (int) screenWidth/2 - width/2;
		int y0 = (int) screenHeight/2 - height/2;
		
		// Create window frame
		frame = new Fractal3DGUI();
		frame.setResizable(true);
		frame.setBounds(x0, y0, width, height);
		frame.setMinimumSize(new Dimension(640, 480));
		frame.setPreferredSize(new Dimension(width, height));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(frame.draw);
		frame.setTitle("Java 3D Mandelbrot Visualizer");
		frame.pack();
		frame.setVisible(true);
	}
	
	// Define action for decreasing fractal_resolution
	private class SwingAction_Decrease_Resolution extends AbstractAction {
		public SwingAction_Decrease_Resolution() {
			putValue(NAME, "- Res");
		}
		public void actionPerformed(ActionEvent e) {
			draw.changeResolution(-50);
		}
	}
	
	// Define action for increasing fractal_resolution
	private class SwingAction_Increase_Resolution extends AbstractAction {
		public SwingAction_Increase_Resolution() {
			putValue(NAME, "+ Res");
		}
		public void actionPerformed(ActionEvent e) {
			draw.changeResolution(50);
		}
	}
	
	// Define action for enabling perspective projection
	private class SwingAction_Perspective extends AbstractAction {
		public SwingAction_Perspective() {
			putValue(NAME, "Perspective");
		}
		public void actionPerformed(ActionEvent e) {
			draw.changePerspective();
		}
	}
}
