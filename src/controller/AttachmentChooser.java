package controller;

import java.io.File;

import javax.swing.JFileChooser;

public class AttachmentChooser {
    public static File[] chooseAttachments() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        int option = fileChooser.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFiles();
        }
        // Return an empty array if no selection
        return new File[] {};
    }
}
