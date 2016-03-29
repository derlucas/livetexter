package de.derlucas.livetexter;

import javax.swing.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ControlForm extends JFrame implements KeyListener {

    private JPanel panel1;
    private JTextPane textPane1;
    private GraphicsDevice graphicsDevice;
    private Color foregroundColor = Color.white;
    private boolean caretOn = true;

    private Color colors[] = {
        Color.WHITE,
        Color.DARK_GRAY,
        Color.RED,
        Color.PINK,
        Color.ORANGE,
        Color.YELLOW,
        Color.GREEN,
        Color.MAGENTA,
        Color.CYAN,
        Color.BLUE
    };
    private int colorPtr = 0;

    public ControlForm() throws HeadlessException {
        super("Live texter");

        GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphicsDevice = e.getDefaultScreenDevice();

        setUndecorated(true);
        setAlwaysOnTop(true);


        setContentPane(panel1);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setExtendedState(MAXIMIZED_BOTH);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, (int)screenSize.getWidth(), (int)screenSize.getHeight());



        pack();
        setVisible(true);

        MutableAttributeSet attributeSet = textPane1.getInputAttributes();
        StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
        StyleConstants.setFontSize(attributeSet, 80);
        setForeground(attributeSet, Color.WHITE);


        textPane1.setBackground(Color.BLACK);
        textPane1.setParagraphAttributes(attributeSet, true);
        textPane1.setSelectionColor(Color.GRAY);
        textPane1.addKeyListener(this);

        graphicsDevice.setFullScreenWindow(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        MutableAttributeSet attributeSet = textPane1.getInputAttributes();
        int fSize = (int) attributeSet.getAttribute(StyleConstants.FontSize);

        if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_C:
                    this.textPane1.setText("");
                    break;
                case KeyEvent.VK_PLUS:
                case KeyEvent.VK_9:
                    if (fSize < 200) {
                        StyleConstants.setFontSize(attributeSet, fSize + 2);
                    }
                    break;
                case KeyEvent.VK_MINUS:
                case KeyEvent.VK_0:
                    if (fSize > 10) {
                        StyleConstants.setFontSize(attributeSet, fSize - 2);
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_LEFT);
                    break;
                case KeyEvent.VK_RIGHT:
                    StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_RIGHT);
                    break;
                case KeyEvent.VK_UP:
                    StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
                    break;
                case KeyEvent.VK_1:
                    setForeground(attributeSet, Color.WHITE);
                    break;
                case KeyEvent.VK_2:
                    setForeground(attributeSet, previousColor());
                    break;
                case KeyEvent.VK_3:
                    setForeground(attributeSet, nextColor());
                    break;
                case KeyEvent.VK_4:
                    if(textPane1.getBackground() != Color.BLACK) {
                        textPane1.setBackground(Color.BLACK);
                    } else {
                        textPane1.setBackground(Color.green);
                    }
                    break;
                case KeyEvent.VK_8:
                    if(caretOn) {
                        caretOn = false;
                        textPane1.setCaretColor(textPane1.getBackground());
                    } else {
                        caretOn = true;
                        textPane1.setCaretColor(foregroundColor);
                    }
                    break;
            }
        } else {
            if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                this.dispose();
            }
        }


        textPane1.setParagraphAttributes(attributeSet, true);
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    private Color nextColor() {
        colorPtr++;
        if(colorPtr == colors.length) {
            colorPtr = 0;
        }
        return colors[colorPtr];
    }

    private Color previousColor() {
        if(colorPtr > 0) {
            colorPtr--;
        } else  {
            colorPtr = colors.length-1;
        }
        return colors[colorPtr];
    }

    private void setForeground(MutableAttributeSet attributeSet, Color color) {
        StyleConstants.setForeground(attributeSet, color);
        if(caretOn) {
            textPane1.setCaretColor(color);
        }
        foregroundColor = color;
    }
}
