/*
 * Copyright (c) 2012 USC Center for Systems and Software Engineering
 */
package dialogs;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import main.COINCOMO;
import main.GlobalMethods;

/**
 *
 * @author Raed Shomali
 */
class BrowserLauncher {

    /**
     *
     * @param url is to be passed to the default browser to view its content
     */
    public static void launchBrowser(String url) {
        try {
            // Identify the operating system.
            String os = System.getProperty("os.name");

            // Launch Browser with URL if it is the Windows OS. 
            if (os.startsWith("Windows")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            }
        } catch (IOException e) {
            System.err.println("Unable to Launch Browser");
        }
    }
}

/**
 *
 * @author Raed Shomali
 */
public class AboutUsDialog extends JDialog implements ActionListener, HyperlinkListener {
    // To AutoScroll the About Section ..

    AutoScroll thread = null;
    Container contentPane = getContentPane();
    // Output String of the About Us Section
    StringBuilder aboutUsContent = new StringBuilder();
    // JLabel
    JLabel versionLabel = new JLabel("Version 2.00 - Released July 8th, 2008");
    // JCheckBox
    JCheckBox autoScrollCheckBox = new JCheckBox("Auto Scroll ?", true);
    // Button
    JButton closeButton = new JButton("Close");
    // Text Pane
    JTextPane textPane = new JTextPane();
    JScrollPane textPaneScroller = new JScrollPane(textPane);

    /**
     *
     * @param parentFrame is the original frame that generated this Dialog
     */
    public AboutUsDialog(COINCOMO parentFrame) {
        super(parentFrame);

        this.setTitle("Loading ...");
        this.setModal(true);

        // Version Label
        versionLabel.setHorizontalAlignment(SwingUtilities.CENTER);
        versionLabel.setFont(new Font("courier", 1, 12));

        // Auto Scroll Check Box
        autoScrollCheckBox.setFont(new Font("courier", 1, 12));

        // Close Button
        closeButton.setFont(new Font("courier", 1, 12));
        closeButton.addActionListener(this);
        closeButton.setFocusable(false);

        // Scroller
        textPaneScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Set Editor As HTML Page
        textPane.addHyperlinkListener(this);
        textPane.setContentType("text/html");
        textPane.setMargin(new Insets(10, 20, 20, 10));

        // Designing of the Text Pane
        TitledBorder aboutTitleBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED), "About");
        aboutTitleBorder.setTitleColor(Color.DARK_GRAY);
        aboutTitleBorder.setTitlePosition(TitledBorder.BELOW_TOP);
        aboutTitleBorder.setTitleJustification(TitledBorder.CENTER);
        textPane.setEditable(false);
        textPaneScroller.setBorder(aboutTitleBorder);

        // GUI Stuff
        contentPane.setLayout(null);

        versionLabel.setBounds(10, 10, 370, 20);
        textPaneScroller.setBounds(10, 30, 370, 300);
        autoScrollCheckBox.setBounds(10, 335, 150, 25);
        closeButton.setBounds(300, 335, 80, 25);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                contentPane.add(versionLabel);
                contentPane.add(textPaneScroller);
                contentPane.add(autoScrollCheckBox);
                contentPane.add(closeButton);

                contentPane.repaint();
            }
        });

        // Scroll Effect ...
        thread = new AutoScroll();
        thread.setPriority(1);
        thread.start();

        GlobalMethods.updateStatusBar("Done.", (COINCOMO) parentFrame);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    // Wait One Second, to Load ...
                    Thread.sleep(1000);

                    // String Builder Was Used For Memory Efficiency
                    // When Appending Strings.
                    aboutUsContent.append("<body style = 'font-size:12pt'>");
                    aboutUsContent.append("<b>Description:</b>");

                    aboutUsContent.append("<br /><br />");

                    aboutUsContent.append("System and Software Development Cost Estimators currently do not have an integrated product that facilitates estimation across a wide variety of cost models. ");
                    aboutUsContent.append("By creating a portable, extensible architecture to facilitate the consolidation of many of the USC CSSE’s cost estimation models into one product, ");
                    aboutUsContent.append("we can ease the burden on the Cost Estimator as well as provide a platform that supports evolution in the cost estimation space. ");

                    aboutUsContent.append("<br /><br />");

                    aboutUsContent.append("<b>Supervisors:</b>");
                    aboutUsContent.append("<ul>");
                    aboutUsContent.append("<li> Professor Barry Boehm </li>");
                    aboutUsContent.append("<li> Winsor Brown </li>");
                    aboutUsContent.append("</ul>");

                    aboutUsContent.append("<br />");

                    aboutUsContent.append("<b>Developers:</b>");
                    aboutUsContent.append("<ul>");
                    aboutUsContent.append("<li> Raed Shomali </li>");
                    aboutUsContent.append("<li> Ramin Moazeni </li>");
                    aboutUsContent.append("</ul>");

                    aboutUsContent.append("<br />");

                    aboutUsContent.append("<hr></hr>");
                    aboutUsContent.append("&copy; 2008 <a href = \'http://www.usc.edu\' >University Of Southern California</a>. All Rights Reserved.");
                    aboutUsContent.append("</body>");

                    // Display HTML Content
                    textPane.setText(aboutUsContent.toString());

                    // Set Carot (Cursor) At the Beginning of the text
                    textPane.setCaretPosition(0);

                    // Set Title of the Dialog
                    setTitle("About USC COINCOMO 2.0");
                } catch (InterruptedException ex) {
                }
            }
        });

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setLocation(this.getOwner().getX() + 100, this.getOwner().getY() + 100);
        this.setResizable(false);
        this.setSize(400, 400);
        this.setVisible(true);
    }

    public void hyperlinkUpdate(HyperlinkEvent e) {
        // User Clicked The Link
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            // Launch Web Browser
            BrowserLauncher.launchBrowser(e.getURL().toString());
        }
    }

    public void actionPerformed(ActionEvent e) {
        // Close Window
        if (e.getSource() == closeButton) {
            if (thread != null && thread.isAlive()) {
                // Kill It
                thread.interrupt();

                // Release Memory
                thread = null;
            }

            // Clean Up
            System.gc();

            this.dispose();
        }
    }

    private class AutoScroll extends Thread {
        // To Wait A Bit For The User To Read At The Beginning ...

        boolean shouldWait = true;

        @Override
        public void run() {
            // Run for Infinity
            while (true) {
                try {
                    if (shouldWait) {
                        // Wait 11 Seconds ..
                        Thread.sleep(11000);

                        // Dont Wait Anymore .. Start Scrolling
                        shouldWait = false;
                    }

                    if (autoScrollCheckBox.isSelected()) {
                        // A Thread To Update GUI
                        SwingUtilities.invokeLater(
                                new Runnable() {
                                    public void run() {
                                        // Scroll Down a bit
                                        textPaneScroller.getVerticalScrollBar().setValue(textPaneScroller.getVerticalScrollBar().getValue() + 1);

                                        // Refresh
                                        contentPane.validate();
                                        contentPane.repaint();
                                    }
                                });
                    }

                    // Rest a Bit ..
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // Exit ...
                }
            }
        }
    }
}