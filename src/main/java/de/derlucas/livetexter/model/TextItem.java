package de.derlucas.livetexter.model;

import java.io.Serializable;

public class TextItem implements Serializable {

    private String label;
    private String content;

    public TextItem() {

    }

    public TextItem(String label) {
        this.label = label;
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
