package view;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

import service.EmailService;
import utils.AttachmentChooser;

public class ComposeView extends JDialog {
    private JTextField toField;
    private JTextField subjectField;
    private JTextArea bodyArea;
    private List<File> attachedFiles;
    
    public ComposeView () {
        setTitle("Compose Email");
        setModal(true);
        setSize(600, 400);
        setLocationRelativeTo(null);

        initUI();

        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout(5, 5));

        Box fieldsPanel = Box.createVerticalBox();

        fieldsPanel.add(new JLabel("To:"));
        toField = new JTextField();
        fieldsPanel.add(toField);

        fieldsPanel.add(new JLabel("Subject:"));
        subjectField = new JTextField();
        fieldsPanel.add(subjectField);

        add(fieldsPanel, BorderLayout.NORTH);

        bodyArea = new JTextArea();
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);

        add(new JScrollPane(bodyArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();

        JButton attachButton = new JButton("Attach Files");
        attachButton.addActionListener(e -> getAttachments());

        bottomPanel.add(attachButton);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendEmail());

        bottomPanel.add(sendButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void getAttachments() {
        attachedFiles = new ArrayList<>();
        File[] files = AttachmentChooser.choose();
        attachedFiles.addAll(Arrays.asList(files));
    }

    private void sendEmail() {
        String to = toField.getText();
        String subject = subjectField.getText();
        String body = bodyArea.getText();
        File[] attachments = null;
        if (attachedFiles != null) {
            attachments = attachedFiles.toArray(new File[0]);
        }
        EmailService.getInstance().sendEmail(to, subject, body, attachments);
        dispose();
    }
}
