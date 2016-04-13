package de.derlucas.livetexter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    private JButton btnSaveAllTexts;
    private DisplayForm displayForm;
    private boolean mustSave = false;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final File textsFile = new File("texter.json");

    public NewControlForm(final DisplayForm displayForm) throws HeadlessException {
        super("livetexter controller");
        this.displayForm = displayForm;

        setMinimumSize(new Dimension(800, 480));
        setLocation(10, 100);
        setContentPane(panel1);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        if (textsFile.exists() && textsFile.canRead()) {
            loadTexts();
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (mustSave) {

                    final JOptionPane optionPane = new JOptionPane("You have unsaved changes, do you want to save them?", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
                    final JDialog jDialog = optionPane.createDialog("livetexter");
                    jDialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                    jDialog.pack();
                    jDialog.setVisible(true);

                    int val = ((Integer)optionPane.getValue()).intValue();
                    if(val == JOptionPane.YES_OPTION) {
                        saveTexts();
                    }

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

                        MutableAttributeSet attributeSet = textPane1.getInputAttributes();
                        StyleConstants.setForeground(attributeSet, Color.WHITE);
                        StyleConstants.setAlignment(attributeSet, item.getAlignment());
                        StyleConstants.setFontSize(attributeSet, item.getFontSize());
                        textPane1.setParagraphAttributes(attributeSet, true);
                        textPane1.setCharacterAttributes(attributeSet, true);

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

                MutableAttributeSet attributeSet = textPane1.getInputAttributes();
                item.setFontSize(StyleConstants.getFontSize(attributeSet));
                item.setAlignment(StyleConstants.getAlignment(attributeSet));

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

        btnRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(comboBox1.getSelectedItem() != null) {
                    comboBox1.removeItemAt(comboBox1.getSelectedIndex());
                    textPane1.setText("");
                    saveTexts();
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

        btnSaveAllTexts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTexts();
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

    private void saveTexts() {
        try {

            final FileWriter fileWriter = new FileWriter(textsFile, false);
            final List<TextItem> itemList = new ArrayList<>();

            for (int i = 0; i < comboBox1.getItemCount(); i++) {
                TextItem item = (TextItem) comboBox1.getItemAt(i);
                itemList.add(item);
            }

            Collections.sort(itemList, new Comparator<TextItem>() {
                @Override
                public int compare(TextItem o1, TextItem o2) {
                    return o1.getLabel().compareTo(o2.getLabel());
                }
            });

            fileWriter.write(objectMapper.writeValueAsString(itemList));
            fileWriter.close();
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void loadTexts() {

        try {
            List<TextItem> itemList = objectMapper.readValue(textsFile,  new TypeReference<List<TextItem>>() {});

            while(comboBox1.getItemCount() > 0) {
                comboBox1.removeItemAt(0);
            }

            for(TextItem item: itemList) {
                comboBox1.addItem(item);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
