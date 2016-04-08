package de.derlucas.livetexter;

import javax.swing.*;

public class Application {

    private NewControlForm controlForm;
    private DisplayForm displayForm;

    public static void main(String[] args) {
        new Application();
    }

    public Application() {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        displayForm = new DisplayForm();
        controlForm = new NewControlForm(displayForm);


    }

}
