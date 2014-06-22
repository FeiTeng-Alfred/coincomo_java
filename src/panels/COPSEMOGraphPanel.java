/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package panels;

import core.COINCOMOComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import main.Icons;

/**
 *
 * @author Raed Shomali
 */
public class COPSEMOGraphPanel extends JPanel implements ChangeListener, ActionListener {

    private COINCOMOComponent component;
    // For Designing
    private double inceptionStart = 0;
    private double elaborationStart = 0;
    private double constructionStart = 0;
    private double transitionStart = 0;
    private double inceptionEnd = 0;
    private double elaborationEnd = 0;
    private double constructionEnd = 0;
    private double transitionEnd = 0;
    BasicStroke bs = new BasicStroke(8);
    private Color inceptionColor = Color.RED;
    private Color elaborationColor = Color.BLUE;
    private Color constructionColor = Color.decode("#006600");
    private Color transitionColor = Color.decode("#660066");
    // Starting Coordinates
    private int topDesignYPoint = 110;
    private int leftDesignXPoint = 70;
    private int maximumX = 0;
    private int maximumY = 0;
    private DecimalFormat numberFormat = new DecimalFormat("#.#");
    private JSlider slider = new JSlider(10, 100, 100);
    private JButton zoomInButton = new JButton();
    private JButton zoomOutButton = new JButton();
    private boolean isLoading = false;

    public void setCOINCOMOComponent(COINCOMOComponent component) {
        this.component = component;
    }

    public COINCOMOComponent getCOINCOMOComponent() {
        return this.component;
    }

    public void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public COPSEMOGraphPanel() {
        slider.addChangeListener(this);

        this.setLayout(null);

        // Slider
        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();

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
        this.add(slider);
        this.add(zoomInButton);
        this.add(zoomOutButton);

        zoomOutButton.setBounds(leftDesignXPoint, 10, 25, 25);
        slider.setBounds(leftDesignXPoint + 25, 10, 400, 50);
        zoomInButton.setBounds(leftDesignXPoint + 425, 10, 25, 25);
    }

    /**
     *
     * @return an integer that represents the furthest X Coordinate for resizing
     */
    public int getFurthestXCoordinate() {
        FontMetrics fm = this.getFontMetrics(this.getFont());
        int width = fm.stringWidth("(M");
        return leftDesignXPoint + maximumX * 50 - 8 + width + leftDesignXPoint - 30;
    }

    /**
     *
     * @return an integer that represents the furthest Y Coordinate for resizing
     */
    public int getFurthestYCoordinate() {
        return topDesignYPoint + maximumY * 25 + 45 + 50 + 10;
    }

    @Override
    public void paintComponent(Graphics g) {
        // Clear
        super.paintComponent(g);

        prepareStartEndValues();

        // Get Highest Values To Prepare the Graph
        maximumY = maximum(component.getInceptionPersonnel() * slider.getValue() / 100, component.getElaborationPersonnel() * slider.getValue() / 100, component.getConstructionPersonnel() * slider.getValue() / 100, component.getTransitionPersonnel() * slider.getValue() / 100) + 1;
        //System.out.println("maxY: " + maximumY);


        maximumX = maximum(inceptionEnd * slider.getValue() / 100, elaborationEnd * slider.getValue() / 100, constructionEnd * slider.getValue() / 100, transitionEnd * slider.getValue() / 100) + 1;
        //System.out.println("maxX: " + maximumX);

        //System.out.println("xcoor: " + getFurtherestXCoordinate());
        //System.out.println("ycoor: " + getFurtherestXCoordinate());        

        this.setPreferredSize(new Dimension(getFurthestXCoordinate(), getFurthestYCoordinate()));
        this.revalidate();

        // If There Is Something To Draw
        if (!isLoading && maximumY != 1 && maximumX != 1) {
            drawGrid(g);

            // Inception
            drawLines(g, inceptionColor, inceptionStart * slider.getValue() / 100, inceptionEnd * slider.getValue() / 100, component.getInceptionPersonnel() * slider.getValue() / 100);

            // Elaboration
            drawLines(g, elaborationColor, elaborationStart * slider.getValue() / 100, elaborationEnd * slider.getValue() / 100, component.getElaborationPersonnel() * slider.getValue() / 100);

            // Construction
            drawLines(g, constructionColor, constructionStart * slider.getValue() / 100, constructionEnd * slider.getValue() / 100, component.getConstructionPersonnel() * slider.getValue() / 100);

            // Transition
            drawLines(g, transitionColor, transitionStart * slider.getValue() / 100, transitionEnd * slider.getValue() / 100, component.getTransitionPersonnel() * slider.getValue() / 100);
        } else if (isLoading) {
            g.drawString("Loading Graph ...", leftDesignXPoint, topDesignYPoint);
        } else {
            g.drawString("Proper Parameters were not yet passed to Generate COPSEMO.", leftDesignXPoint, topDesignYPoint);
        }
    }

    /**
     *
     * @param g is the Graphics object used to draw on the panel
     * @param color is used to determine the color of whats being drawn on the
     * panel
     * @param start is the starting point of the X Coordinate
     * @param end is the ending point of the X Coordinate
     * @param y is the highest value of the Y Coordinate
     */
    public void drawLines(Graphics g, Color color, double start, double end, double y) {
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(color);
        BufferedImage pattern = null;

        try {
            if (color.equals(inceptionColor)) {
                pattern = ImageIO.read(Icons.class.getResource("/com/famfamfam/silk/incept.png"));
            } else if (color.equals(elaborationColor)) {
                pattern = ImageIO.read(Icons.class.getResource("/com/famfamfam/silk/elab.png"));
            } else if (color.equals(constructionColor)) {
                pattern = ImageIO.read(Icons.class.getResource("/com/famfamfam/silk/construct.png"));
            } else if (color.equals(transitionColor)) {
                pattern = ImageIO.read(Icons.class.getResource("/com/famfamfam/silk/transit.png"));
            }
        } catch (IOException ioe) {
        }

        // Start
        g.drawLine(leftDesignXPoint + (int) (start * 50),
                topDesignYPoint + maximumY * 25,
                leftDesignXPoint + (int) (start * 50),
                topDesignYPoint + maximumY * 25 - (int) (y * 25));

        // End
        g.drawLine(leftDesignXPoint + (int) (end * 50),
                topDesignYPoint + maximumY * 25,
                leftDesignXPoint + (int) (end * 50),
                topDesignYPoint + maximumY * 25 - (int) (y * 25));

        // Y
        g.drawLine(leftDesignXPoint + (int) (start * 50),
                topDesignYPoint + maximumY * 25 - (int) (y * 25),
                leftDesignXPoint + (int) (end * 50),
                topDesignYPoint + maximumY * 25 - (int) (y * 25));

        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));



        TexturePaint tp = new TexturePaint(pattern, new Rectangle(15, 15));
        g2.setPaint(tp);
        g2.fillRect(leftDesignXPoint + (int) (start * 50), topDesignYPoint + maximumY * 25 - (int) (y * 25), (int) ((end - start) * 50), (int) (y * 25));

        //g.fillRect( leftDesignXPoint + (int) (start * 50) , topDesignYPoint + maximumY * 25 - (int) (y * 25) , (int)( ( end - start ) * 50 ) , (int)( y * 25 ) );
    }

    /**
     *
     * @param g is the Graphics object used to draw on the panel
     */
    public void drawGrid(Graphics g) {
        g.setColor(Color.BLACK);

        g.drawString("Phased Schedule (M) and Staff (P):", leftDesignXPoint + 10, topDesignYPoint - 10);

        // Left Y Axis
        g.drawLine(leftDesignXPoint, topDesignYPoint, leftDesignXPoint, topDesignYPoint + maximumY * 25);

        g.drawString("(P)", leftDesignXPoint - 30, topDesignYPoint + 5);

        for (int i = maximumY; i > 0; i--) {
            // Numbers
            g.setColor(Color.BLACK);
            g.drawString(Double.parseDouble(numberFormat.format((double) (maximumY - i) * 100 / slider.getValue())) + "", leftDesignXPoint - 30, topDesignYPoint + i * 25 + 5);

            // Black With Transperancy
            g.setColor(new Color(0, 0, 0, 50));
            g.drawLine(leftDesignXPoint, topDesignYPoint + i * 25, leftDesignXPoint + maximumX * 50, topDesignYPoint + i * 25);
        }

        // Back to Default
        g.setColor(Color.BLACK);

        // Right Y Axis
        g.drawLine(leftDesignXPoint + maximumX * 50, topDesignYPoint, leftDesignXPoint + maximumX * 50, topDesignYPoint + maximumY * 25);

        // Top X Axis
        g.drawLine(leftDesignXPoint, topDesignYPoint, leftDesignXPoint + maximumX * 50, topDesignYPoint);

        // Bottom X Axis
        g.drawLine(leftDesignXPoint, topDesignYPoint + maximumY * 25, leftDesignXPoint + maximumX * 50, topDesignYPoint + maximumY * 25);

        g.drawString("(M)", leftDesignXPoint + maximumX * 50 - 8, topDesignYPoint + maximumY * 25 + 25);

        for (int i = 0; i < maximumX; i++) {
            // Numbers
            g.setColor(Color.BLACK);
            g.drawString(Double.parseDouble(numberFormat.format((double) i * 100 / slider.getValue())) + "", leftDesignXPoint + i * 50 - 8, topDesignYPoint + maximumY * 25 + 25);

            // Black With Transperancy
            g.setColor(new Color(0, 0, 0, 50));
            g.drawLine(leftDesignXPoint + i * 50, topDesignYPoint, leftDesignXPoint + i * 50, topDesignYPoint + maximumY * 25);
        }

        // Legend

        g.setColor(Color.DARK_GRAY);
        g.drawRoundRect(leftDesignXPoint, topDesignYPoint + maximumY * 25 + 45, 325, 50, 20, 20);

        Graphics2D g2 = (Graphics2D) g;
        BufferedImage incept = null;
        BufferedImage elab = null;
        BufferedImage construct = null;
        BufferedImage transit = null;
        try {
            incept = ImageIO.read(Icons.class.getResource("/com/famfamfam/silk/incept.png"));
            elab = ImageIO.read(Icons.class.getResource("/com/famfamfam/silk/elab.png"));
            construct = ImageIO.read(Icons.class.getResource("/com/famfamfam/silk/construct.png"));
            transit = ImageIO.read(Icons.class.getResource("/com/famfamfam/silk/transit.png"));
        } catch (IOException ioe) {
        }

        // Inception       
        g.setColor(inceptionColor);
        g.drawString("Inception", leftDesignXPoint + 15, topDesignYPoint + maximumY * 25 + 65);

        g.drawRect(leftDesignXPoint + 25, topDesignYPoint + maximumY * 25 + 75, 25, 12);
        g.setColor(new Color(inceptionColor.getRed(), inceptionColor.getGreen(), inceptionColor.getBlue(), 50));
        TexturePaint inceptTP = new TexturePaint(incept, new Rectangle(10, 10));
        g2.setPaint(inceptTP);
        g.fillRect(leftDesignXPoint + 25, topDesignYPoint + maximumY * 25 + 75, 25, 12);

        // Elaboration
        g.setColor(elaborationColor);
        g.drawString("Elaboration", leftDesignXPoint + 85, topDesignYPoint + maximumY * 25 + 65);

        g.drawRect(leftDesignXPoint + 105, topDesignYPoint + maximumY * 25 + 75, 25, 12);
        g.setColor(new Color(elaborationColor.getRed(), elaborationColor.getGreen(), elaborationColor.getBlue(), 50));
        TexturePaint elabTP = new TexturePaint(elab, new Rectangle(10, 10));
        g2.setPaint(elabTP);
        g.fillRect(leftDesignXPoint + 105, topDesignYPoint + maximumY * 25 + 75, 25, 12);

        // Construction
        g.setColor(constructionColor);
        g.drawString("Construction", leftDesignXPoint + 165, topDesignYPoint + maximumY * 25 + 65);

        g.drawRect(leftDesignXPoint + 185, topDesignYPoint + maximumY * 25 + 75, 25, 12);
        g.setColor(new Color(constructionColor.getRed(), constructionColor.getGreen(), constructionColor.getBlue(), 50));
        TexturePaint constructTP = new TexturePaint(construct, new Rectangle(10, 10));
        g2.setPaint(constructTP);
        g.fillRect(leftDesignXPoint + 185, topDesignYPoint + maximumY * 25 + 75, 25, 12);

        // Transition
        g.setColor(transitionColor);
        g.drawString("Transition", leftDesignXPoint + 255, topDesignYPoint + maximumY * 25 + 65);

        g.drawRect(leftDesignXPoint + 265, topDesignYPoint + maximumY * 25 + 75, 25, 12);
        g.setColor(new Color(transitionColor.getRed(), transitionColor.getGreen(), transitionColor.getBlue(), 50));
        TexturePaint transitTP = new TexturePaint(transit, new Rectangle(10, 10));
        g2.setPaint(transitTP);
        g.fillRect(leftDesignXPoint + 265, topDesignYPoint + maximumY * 25 + 75, 25, 12);

        // Credits
        g.setColor(Color.LIGHT_GRAY);
        //g.drawString( "Ramin Moazeni" , leftDesignXPoint + 350 , topDesignYPoint + maximumY * 25 + 75 ); 
    }

    /**
     *
     * @param value is used to set the Inception Personnel
     */
    /*public void setInceptionPersonnel( double value )
     {
     inceptionPersonnel = value;
     }

     /**
     * 
     * @param value is used to set the Elaboration Personnel
     */
    /*public void setElaborationPersonnel( double value )
     {
     elaborationPersonnel = value;
     }*/
    /**
     *
     * @param value is used to set the Construction Personnel
     */
    /*public void setConstructionPersonnel( double value )
     {
     constructionPersonnel = value;
     }

     /**
     * 
     * @param value is used to set the Transition Personnel
     */
    /*public void setTransitionPersonnel( double value )
     {
     transitionPersonnel = value;
     }*/
    /**
     *
     * @param value is used to set Inception Months
     */
    /*public void setInceptionMonths( double value )
     {
     inceptionMonths = value;
     }*/
    /**
     *
     * @param value is used to set Elaboration Months
     */
    /*public void setElaborationMonths( double value )
     {
     elaborationMonths = value;
     }*/
    /**
     *
     * @param value is used to set Construction Months
     */
    /*public void setConstructionMonths( double value )
     {
     constructionMonths = value;
     }*/
    /**
     *
     * @param value is used to set the Transition Months
     */
    /*public void setTransitionMonths( double value )
     {
     transitionMonths = value;
     }*/
    private void prepareStartEndValues() {
        // Begins at Zero
        inceptionStart = 0;
        inceptionEnd = inceptionStart + component.getInceptionMonth();

        // Starts where Inception Ended
        elaborationStart = inceptionEnd;
        elaborationEnd = elaborationStart + component.getElaborationMonth();

        // Starts where elaboration Ended
        constructionStart = elaborationEnd;
        constructionEnd = constructionStart + component.getConstructionMonth();

        // Starts where construction ended
        transitionStart = constructionEnd;
        transitionEnd = transitionStart + component.getTransitionMonth();
    }

    private int maximum(double x1, double x2, double x3, double x4) {
        return (int) Math.round(Math.max(Math.max(x1, x2), Math.max(x3, x4)));
    }

    public void stateChanged(ChangeEvent e) {
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
}
