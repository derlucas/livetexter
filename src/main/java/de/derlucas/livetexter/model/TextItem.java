package de.derlucas.livetexter.model;

import javax.swing.text.StyleConstants;
import java.io.Serializable;

public class TextItem implements Serializable {

    private String label;
    private String content;
    private int fontSize = 80;
    private int alignment = StyleConstants.ALIGN_CENTER;

    public TextItem() {

    }

    public TextItem(String label) {
        this.label = label;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getAlignment() {
        return alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label != null ? label : "error";
    }
}
