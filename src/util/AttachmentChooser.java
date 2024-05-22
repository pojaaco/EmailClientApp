package util;

import java.io.File;

import javax.swing.JFileChooser;

public class AttachmentChooser {
    public static File[] choose() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        int option = fileChooser.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFiles();
        }
        return new File[] {};
    }
}
