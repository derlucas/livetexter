package de.derlucas.livetexter;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import de.derlucas.livetexter.model.TextItem;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class NewControlForm extends JFrame {

    private JPanel panel1;
    private JTextPane textPane1;
    private JButton btnMax;
    private JButton btnPrev;
    private JComboBox comboBox1;
    private JButton btnNext;
    private JSpinner spinner1;
    private JButton btnSave;
    private JButton btnNew;
    private JButton btnLeftify;
    private JButton btnCenter;
    private JButton btnRightify;
    private JButton btnRemove;
    private DisplayForm displayForm;
    private boolean mustSave = false;

    public NewControlForm(final DisplayForm displayForm) throws HeadlessException {
        super("livetexter controller");
        this.displayForm = displayForm;

        setMinimumSize(new Dimension(800, 480));
        setLocation(10, 100);
        setContentPane(panel1);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final File textsFile = new File("texter.data");
        if (textsFile.exists() && textsFile.canRead()) {

            try {
                FileInputStream fis = new FileInputStream(textsFile);
                ObjectInputStream ois = new ObjectInputStream(fis);
                TextItem item = null;

                do {
                    item = (TextItem) ois.readObject();
                    if (item != null) {
                        comboBox1.addItem(item);
                    }
                }
                while (item != null);
            }
            catch (EOFException ignored) {

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                if (!mustSave) { return; }

                try {
                    FileOutputStream fos = new FileOutputStream(textsFile);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);

                    for (int i = 0; i < comboBox1.getItemCount(); i++) {
                        TextItem item = (TextItem) comboBox1.getItemAt(i);
                        oos.writeObject(item);
                    }

                    oos.close();
                    fos.close();
                }
                catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        spinner1.setModel(new SpinnerNumberModel(80, 10, 300, 10));
        spinner1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                MutableAttributeSet attributeSet = textPane1.getInputAttributes();
                StyleConstants.setFontSize(attributeSet, (int) ((JSpinner) e.getSource()).getValue());
                textPane1.setParagraphAttributes(attributeSet, false);
                textPane1.setCharacterAttributes(attributeSet, false);
            }
        });

        btnMax.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayForm.toggleFullscreen();
            }
        });

        MutableAttributeSet attributeSet = textPane1.getInputAttributes();
        StyleConstants.setFontSize(attributeSet, 80);
        StyleConstants.setForeground(attributeSet, Color.WHITE);
        StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
        textPane1.setEditorKit(new StyledEditorKit());
        textPane1.setParagraphAttributes(attributeSet, true);
        textPane1.setCharacterAttributes(attributeSet, true);
        textPane1.setCaretColor(Color.WHITE);
        textPane1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_C:
                            NewControlForm.this.textPane1.setText("");
                            break;
                        case KeyEvent.VK_LEFT:
                            goPrevPreset();
                            break;
                        case KeyEvent.VK_RIGHT:
                            goNextPreset();
                            break;
                    }
                }
            }
        });

        comboBox1.setPreferredSize(new Dimension(300, 32));
        comboBox1.setEditable(false);
        comboBox1.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    TextItem item = (TextItem) e.getItem();
                    EditorKit editorKit = textPane1.getEditorKit();
                    ByteInputStream bis = new ByteInputStream();
                    if (item.getContent() != null) {
                        textPane1.setText("");
                        bis.setBuf(item.getContent().getBytes());
                        try {
                            editorKit.read(bis, textPane1.getDocument(), 0);
                        }
                        catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });

        btnNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goNextPreset();
            }
        });

        btnPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goPrevPreset();
            }
        });

        btnNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textPane1.setText("");
                comboBox1.addItem(new TextItem("new item"));
                comboBox1.setSelectedIndex(comboBox1.getItemCount() - 1);
            }
        });

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mustSave = true;
                boolean wasNew = false;

                TextItem item = (TextItem) comboBox1.getSelectedItem();
                if (item == null) {
                    item = new TextItem();
                    wasNew = true;
                }
                item.setLabel(getLabel(textPane1.getDocument()));

                if (wasNew) {
                    comboBox1.addItem(item);
                }

                EditorKit editorKit = textPane1.getEditorKit();

                try {
                    ByteOutputStream bos = new ByteOutputStream();
                    editorKit.write(bos, textPane1.getDocument(), 0, textPane1.getDocument().getLength());
                    item.setContent(bos.toString());
                }
                catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        btnCenter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MutableAttributeSet attributeSet = textPane1.getInputAttributes();
                StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
                textPane1.setParagraphAttributes(attributeSet, true);
                textPane1.setCharacterAttributes(attributeSet, true);
            }
        });
        btnRightify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MutableAttributeSet attributeSet = textPane1.getInputAttributes();
                StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_RIGHT);
                textPane1.setParagraphAttributes(attributeSet, true);
                textPane1.setCharacterAttributes(attributeSet, true);
            }
        });

        btnLeftify.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MutableAttributeSet attributeSet = textPane1.getInputAttributes();
                StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_LEFT);
                textPane1.setParagraphAttributes(attributeSet, true);
                textPane1.setCharacterAttributes(attributeSet, true);
            }
        });

        btnRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(comboBox1.getSelectedItem() != null) {
                    comboBox1.removeItemAt(comboBox1.getSelectedIndex());
                    textPane1.setText("");
                }
            }
        });

        displayForm.setDocument(textPane1.getDocument());

        pack();
        setVisible(true);
        this.displayForm.display();
    }

    private String getLabel(Document document) {
        if (document.getLength() > 40) {
            try {
                return document.getText(0, 40).trim();
            }
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        try {
            return document.getText(0, document.getLength()).trim();
        }
        catch (BadLocationException e) {
            e.printStackTrace();
        }

        return "error";
    }

    private void goNextPreset() {
        if (comboBox1.getSelectedIndex() < comboBox1.getItemCount() - 1) {
            comboBox1.setSelectedIndex(comboBox1.getSelectedIndex() + 1);
        }
    }

    private void goPrevPreset() {
        if (comboBox1.getSelectedIndex() > 0) {
            comboBox1.setSelectedIndex(comboBox1.getSelectedIndex() - 1);
        }
    }
}
