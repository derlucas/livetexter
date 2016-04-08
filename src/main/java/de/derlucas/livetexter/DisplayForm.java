package de.derlucas.livetexter;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DisplayForm extends JFrame {

    private JPanel panel1;
    private JTextPane textPane1;
    private JScrollPane scrollPane;
    private final GraphicsDevice graphicsDevice;

    public DisplayForm() throws HeadlessException {
        super("livetexter display");

        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        if(e.getScreenDevices().length > 1) {
            graphicsDevice = e.getScreenDevices()[1];
        } else {
            graphicsDevice = e.getDefaultScreenDevice();
        }

        setLocation(870,100);
        setContentPane(panel1);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        textPane1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    toggleFullscreen();
                }
            }
        });


        scrollPane.setBackground(Color.BLACK);
        scrollPane.setPreferredSize(new Dimension(800, 450));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
            }
        });

    }

    public void display() {
        pack();
        setVisible(true);
    }

    public void setFullScreen() {
        if(graphicsDevice.isFullScreenSupported() &&
           graphicsDevice.getFullScreenWindow() == null) {
            graphicsDevice.setFullScreenWindow(DisplayForm.this);
        }
    }

    public void unsetFullscreen() {
        if(graphicsDevice.getFullScreenWindow() != null) {
            graphicsDevice.setFullScreenWindow(null);
        }
    }

    public void toggleFullscreen() {
        if(graphicsDevice.getFullScreenWindow() == null) {
            if(graphicsDevice.isFullScreenSupported()) {
                graphicsDevice.setFullScreenWindow(this);
            }
        } else {
            graphicsDevice.setFullScreenWindow(null);
        }
    }

    public void setDocument(Document document) {
        textPane1.setDocument(document);
    }
}
