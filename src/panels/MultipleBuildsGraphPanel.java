/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package panels;

import core.COINCOMOComponent;
import core.COINCOMOSubSystem;
import core.COINCOMOUnit;
import database.COINCOMOComponentManager;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import main.GlobalMethods;
import main.Icons;

/**
 *
 * @author Raed Shomali
 */
public class MultipleBuildsGraphPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener, ChangeListener {

    private COINCOMOSubSystem subSystem = null;
    //UpdateShift updateThread = new UpdateShift();
    private Color inceptionColor = Color.RED;
    private Color elaborationColor = Color.BLUE;
    private Color constructionColor = Color.decode("#006600");
    private Color transitionColor = Color.decode("#660066");
    // Starting Coordinates
    private int topDesignYPoint = 90;
    private int leftDesignXPoint = 70;
    private int maximumX = 0;
    private int yCoordinate = 0;
    private ArrayList<COINCOMOUnit> orderedVector = new ArrayList<COINCOMOUnit>();
    // Drag Purposes ...
    private int previousDraggedX = 0;
    private COINCOMOComponent componentUnderMouse = null;
    private JSlider slider = new JSlider(0, 100);
    private JButton zoomInButton = new JButton();
    private JButton zoomOutButton = new JButton();
    // To minimize Graph
    private static double minimizeFactor = 0.01;
    private DecimalFormat format2Decimals = new DecimalFormat("0.00");
    private DecimalFormat format1Decimal = new DecimalFormat("0.0");

    public MultipleBuildsGraphPanel() {

        // Slider
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();

        labelTable.put(new Integer(0), new JLabel("0%"));
        labelTable.put(new Integer(10), new JLabel("10%"));
        labelTable.put(new Integer(20), new JLabel("20%"));
        labelTable.put(new Integer(30), new JLabel("30%"));
        labelTable.put(new Integer(40), new JLabel("40%"));
        labelTable.put(new Integer(50), new JLabel("50%"));
        labelTable.put(new Integer(60), new JLabel("60%"));
        labelTable.put(new Integer(70), new JLabel("70%"));
        labelTable.put(new Integer(80), new JLabel("80%"));
        labelTable.put(new Integer(90), new JLabel("90%"));
        labelTable.put(new Integer(100), new JLabel("100%"));

        slider.setToolTipText("Zoom Level");
        slider.setLabelTable(labelTable);

        slider.setPaintLabels(true);
        slider.setSnapToTicks(true);
        slider.setPaintTicks(true);
        slider.setPaintTrack(true);

        slider.setMinorTickSpacing(1);
        slider.setMajorTickSpacing(10);

        slider.addChangeListener(this);

        // Buttons
        zoomInButton.addActionListener(this);
        zoomOutButton.addActionListener(this);

        zoomInButton.setToolTipText("Zoom In");
        zoomOutButton.setToolTipText("Zoom Out");

        zoomInButton.setFocusable(false);
        zoomOutButton.setFocusable(false);

        zoomInButton.setIcon(Icons.ZOOM_IN_ICON);
        zoomOutButton.setIcon(Icons.ZOOM_OUT_ICON);

        // GUI
        this.setLayout(null);

        zoomOutButton.setBounds(leftDesignXPoint, 20, 25, 25);
        slider.setBounds(leftDesignXPoint + 25, 20, 400, 50);
        zoomInButton.setBounds(leftDesignXPoint + 425, 20, 25, 25);

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    public void setCOINCOMOSubSystem(COINCOMOSubSystem unit) {
        this.subSystem = unit;

        // Set Zoom Level
        slider.setValue(unit.getZoomLevel());

        orderedVector = unit.getListOfSubUnits();

        if (!orderedVector.isEmpty()) {
            this.add(slider);
            this.add(zoomInButton);
            this.add(zoomOutButton);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Clear
        maximumX = 0;
        yCoordinate = 0;

        // Draw Only if Not Empty
        if (!orderedVector.isEmpty()) {
            FontMetrics fm = this.getFontMetrics(this.getFont());
            int blockShift = 0;

            for (int i = 0; i < orderedVector.size(); i++) {
                COINCOMOComponent component = (COINCOMOComponent) orderedVector.get(i);

                // Clear ..
                blockShift = 0;

                // Inception
                int inceptionSize = (int) (component.getInceptionSchedulePercentage() / 100.0d * COINCOMOComponentManager.calculateSLOCWithoutREVL(component) * getMinimizeFactor() * slider.getValue() / 100.0d);
                float num = (float)(slider.getValue() / 100.0d);
                g.setColor(inceptionColor);
                g.drawRect((int)(leftDesignXPoint + blockShift + component.getMultiBuildShift()*num), topDesignYPoint + yCoordinate * 40, inceptionSize, 30);//1.startX 2.startY 3.width 4.height
                g.setColor(new Color(inceptionColor.getRed(), inceptionColor.getGreen(), inceptionColor.getBlue(), 50));
                g.fillRect((int)(leftDesignXPoint + blockShift + component.getMultiBuildShift()*num), topDesignYPoint + yCoordinate * 40, inceptionSize, 30);
              
                blockShift += inceptionSize;


                // Elaboration
                int elaborationSize = (int) (component.getElaborationSchedulePercentage() / 100.0d * component.getSLOC() * getMinimizeFactor() * slider.getValue() / 100.0d);

                g.setColor(elaborationColor);
                g.drawRect((int)(leftDesignXPoint + blockShift + component.getMultiBuildShift()*num), topDesignYPoint + yCoordinate * 40, elaborationSize, 30);

                g.setColor(new Color(elaborationColor.getRed(), elaborationColor.getGreen(), elaborationColor.getBlue(), 50));
                g.fillRect((int)(leftDesignXPoint + blockShift + component.getMultiBuildShift()*num), topDesignYPoint + yCoordinate * 40, elaborationSize, 30);

                blockShift += elaborationSize;


                // Construction
                int constructionSize = (int) (component.getConstructionSchedulePercentage() / 100.0d * component.getSLOC() * getMinimizeFactor() * slider.getValue() / 100.0d);

                g.setColor(constructionColor);
                g.drawRect((int)(leftDesignXPoint + blockShift + component.getMultiBuildShift()*num), topDesignYPoint + yCoordinate * 40, constructionSize, 30);

                g.setColor(new Color(constructionColor.getRed(), constructionColor.getGreen(), constructionColor.getBlue(), 50));
                g.fillRect((int)(leftDesignXPoint + blockShift + component.getMultiBuildShift()*num), topDesignYPoint + yCoordinate * 40, constructionSize, 30);

                blockShift += constructionSize;


                // Transition
                int transitionSize = (int) (component.getTransitionSchedulePercentage() / 100.0d * COINCOMOComponentManager.calculateSLOCWithoutREVL(component) * getMinimizeFactor() * slider.getValue() / 100.0d);

                g.setColor(transitionColor);
                g.drawRect((int)(leftDesignXPoint + blockShift + component.getMultiBuildShift()*num), topDesignYPoint + yCoordinate * 40, transitionSize, 30);

                g.setColor(new Color(transitionColor.getRed(), transitionColor.getGreen(), transitionColor.getBlue(), 50));
                g.fillRect((int)(leftDesignXPoint + blockShift + component.getMultiBuildShift()*num), topDesignYPoint + yCoordinate * 40, transitionSize, 30);

                blockShift += transitionSize;


                // Component Name & Size
                g.setColor(Color.DARK_GRAY);

                String name = component.getName() + " (" + GlobalMethods.FormatLongWithComma(component.getSLOC()) + " SLOC), COST $"
                        + format2Decimals.format(GlobalMethods.roundOff(component.getCost(), 2)) + ", Staff "
                        + format1Decimal.format(GlobalMethods.roundOff(component.getStaff(), 1)) + ", Effort "
                        + format2Decimals.format(GlobalMethods.roundOff(component.getEffort(), 2)) + ", Schedule "
                        + format2Decimals.format(GlobalMethods.roundOff(component.getSchedule(), 2));

                g.drawString(name, leftDesignXPoint + blockShift + component.getMultiBuildShift() + 10, topDesignYPoint + yCoordinate * 40 + 20);


                // Find Maximum X Coordinate ...
                int width = fm.stringWidth(name);
                if (maximumX < leftDesignXPoint + blockShift + component.getMultiBuildShift() + width) {
                    maximumX = leftDesignXPoint + blockShift + component.getMultiBuildShift() + width;
                }

                // Go Down
                yCoordinate++;
            }

            // Go Down ..
            yCoordinate++;

            drawLegend(g);
        } else {
            g.setFont(new Font("arial", 1, 12));

            g.setColor(Color.DARK_GRAY);

            g.drawString("No components are available to draw under this Sub-System.", 30, 40);
        }
    }

    public void drawLegend(Graphics g) {
        // Legend

        g.setColor(Color.DARK_GRAY);
        g.drawRoundRect(leftDesignXPoint, topDesignYPoint + yCoordinate * 25 + 45, 325, 50, 20, 20);

        // Inception
        g.setColor(inceptionColor);
        g.drawString("Inception", leftDesignXPoint + 15, topDesignYPoint + yCoordinate * 25 + 65);

        g.drawRect(leftDesignXPoint + 25, topDesignYPoint + yCoordinate * 25 + 75, 25, 12);
        g.setColor(new Color(inceptionColor.getRed(), inceptionColor.getGreen(), inceptionColor.getBlue(), 50));
        g.fillRect(leftDesignXPoint + 25, topDesignYPoint + yCoordinate * 25 + 75, 25, 12);

        // Elaboration
        g.setColor(elaborationColor);
        g.drawString("Elaboration", leftDesignXPoint + 85, topDesignYPoint + yCoordinate * 25 + 65);

        g.drawRect(leftDesignXPoint + 105, topDesignYPoint + yCoordinate * 25 + 75, 25, 12);
        g.setColor(new Color(elaborationColor.getRed(), elaborationColor.getGreen(), elaborationColor.getBlue(), 50));
        g.fillRect(leftDesignXPoint + 105, topDesignYPoint + yCoordinate * 25 + 75, 25, 12);

        // Construction
        g.setColor(constructionColor);
        g.drawString("Construction", leftDesignXPoint + 165, topDesignYPoint + yCoordinate * 25 + 65);

        g.drawRect(leftDesignXPoint + 185, topDesignYPoint + yCoordinate * 25 + 75, 25, 12);
        g.setColor(new Color(constructionColor.getRed(), constructionColor.getGreen(), constructionColor.getBlue(), 50));
        g.fillRect(leftDesignXPoint + 185, topDesignYPoint + yCoordinate * 25 + 75, 25, 12);

        // Transition
        g.setColor(transitionColor);
        g.drawString("Transition", leftDesignXPoint + 255, topDesignYPoint + yCoordinate * 25 + 65);

        g.drawRect(leftDesignXPoint + 265, topDesignYPoint + yCoordinate * 25 + 75, 25, 12);
        g.setColor(new Color(transitionColor.getRed(), transitionColor.getGreen(), transitionColor.getBlue(), 50));
        g.fillRect(leftDesignXPoint + 265, topDesignYPoint + yCoordinate * 25 + 75, 25, 12);

        // Credits
        g.setColor(Color.LIGHT_GRAY);
        //g.drawString( "Ramin Moazeni" , leftDesignXPoint + 350 , topDesignYPoint + yCoordinate * 25 + 75 );

        this.setPreferredSize(new Dimension(maximumX + leftDesignXPoint, topDesignYPoint + yCoordinate * 25 + 45 + 50 + 20));
        this.revalidate();
    }

    public void mouseDragged(MouseEvent e) {
        if (componentUnderMouse != null) {
            // Drag Shift ...
            int delta = +e.getX() - previousDraggedX;

            // Drag Process ...
            componentUnderMouse.addMultiBuildShift(delta);

            previousDraggedX = e.getX();


            ArrayList<COINCOMOUnit> vector = subSystem.getListOfSubUnits();

            // If Going Left ...
            if (delta < 0) {
                // If not the Top Most Component
                int index = vector.indexOf(componentUnderMouse);

                if (index != 0) {
                    COINCOMOComponent previousComponent = (COINCOMOComponent) vector.get(index - 1);

                    if (componentUnderMouse.getMultiBuildShift() < previousComponent.getMultiBuildShift()) {
                        // Restore
                        componentUnderMouse.setMultiBuildShift(previousComponent.getMultiBuildShift());
                    }
                }
            } else {
                // To determine which component needs to be moved as well
                boolean moveAsWell = false;

                for (int i = 0; i < vector.size(); i++) {
                    COINCOMOComponent tempComponent = (COINCOMOComponent) vector.get(i);

                    // Move this Component as well Only if the component being moved by the mouse
                    // has surpassed the components below it
                    if (moveAsWell && componentUnderMouse.getMultiBuildShift() > tempComponent.getMultiBuildShift()) {
                        tempComponent.setMultiBuildShift(componentUnderMouse.getMultiBuildShift());
                    }

                    // Move all other components below the component that is being moved by the mouse
                    if (tempComponent == componentUnderMouse) {
                        moveAsWell = true;
                    }
                }
            }

            // If Already Exists ..
            /*if ( updateThread != null )
             {
             // Kill it 
             updateThread.interrupt();
             }*/

            //updateThread = new UpdateShift();
            //updateThread.start();

            // Refresh
            repaint();
        }
    }

    public void mouseMoved(final MouseEvent e) {
        // Set Back to Default ...
        componentUnderMouse = null;

        setCursor(Cursor.getDefaultCursor());

        // Create a Thread ...
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Iterator it = orderedVector.iterator();

                int yCoordinate = 0;

                while (it.hasNext()) {
                    COINCOMOComponent component = (COINCOMOComponent) it.next();

                    double totalPercentage = component.getInceptionSchedulePercentage() + component.getElaborationSchedulePercentage()
                            + component.getConstructionSchedulePercentage() + component.getTransitionSchedulePercentage();

                    // Test if Point is Within Bar ...
                    if (testWithin(e.getPoint(), leftDesignXPoint + component.getMultiBuildShift(), topDesignYPoint + yCoordinate * 40, (int) (totalPercentage / 100.0d * component.getSLOC() * getMinimizeFactor() * slider.getValue() / 100.0d), 30)) {
                        // Keep Track of the Component that the mouse is within ...
                        componentUnderMouse = component;

                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                        return;
                    }

                    // Go Down
                    yCoordinate++;
                }
            }
        });
    }

    private boolean testWithin(Point point, int x, int y, int width, int height) {
        if (point.x >= x && point.x <= x + width && point.y >= y && point.y <= y + height) {
            return true;
        }

        return false;
    }

    public void stateChanged(ChangeEvent e) {
        // Update Zoom Level...
        subSystem.setZoomLevel(slider.getValue());

        // Refresh
        repaint();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == zoomInButton) {
            // Calculate new value
            int newValue = (int) Math.ceil(slider.getValue() + 10);

            // Validate new Value before setting it
            slider.setValue(newValue > 100 ? 100 : newValue);
        } else {
            // Calculate new value
            int newValue = (int) Math.floor(slider.getValue() - 10);

            // Validate new value before setting it
            slider.setValue(newValue < 0 ? 0 : newValue);
        }

        // Refresh
        repaint();
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        previousDraggedX = e.getX();
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public static double getMinimizeFactor() {
        return minimizeFactor;
    }

    /*class UpdateShift extends Thread
     {
     public UpdateShift()
     {
     this.setPriority( 1 );
     }

     @Override
     public void run()
     {
     try
     {
     // Wait a bit ...
     Thread.sleep( 1000 );

     // Start Updating ...
     GlobalMethods.updateStatusBar( "Updating Component's Shift ..." );

     COINCOMOComponentManager.updateComponents( GlobalMethods.getOrderedVector( subSystem.getListOfSubUnits() ) );

     GlobalMethods.updateStatusBar( "Component's Shift Saved." );
     }
     catch ( InterruptedException ex )
     {
     }
     }

     }*/
}
