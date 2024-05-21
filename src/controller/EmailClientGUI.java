package controller;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.event.WindowAdapter;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;

import model.Email;
import service.EmailSender;
import service.EmailSessionManager;

public class EmailClientGUI extends JFrame {
    private JTextField usernameField = new JTextField(20);
    private JPasswordField passwordField = new JPasswordField(20);
    
    private DefaultListModel<String> emailListModel = new DefaultListModel<>();
    private JList<String> emailList = new JList<>(emailListModel);

    private JTextArea emailContent = new JTextArea();
    private Message[] messages;

    public EmailClientGUI() {
        setTitle("Java Email Client");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
        setVisible(true);

        // Add window listener to handle application close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (EmailSessionManager.getInstance() != null) {
                        EmailSessionManager.getInstance().close();
                    }
                } catch (MessagingException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void initUI() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setOneTouchExpandable(true);

        emailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        emailList.addListSelectionListener(this::emailListSelectionChanged);
        JScrollPane listScrollPane = new JScrollPane(emailList);

        // JTextArea emailContent = new JTextArea();
        emailContent.setEditable(false);
        // add(new JScrollPane(emailContent), BorderLayout.CENTER);
        JScrollPane contentScrollPane = new JScrollPane(emailContent);

        splitPane.setLeftComponent(listScrollPane);
        splitPane.setRightComponent(contentScrollPane);

        getContentPane().add(splitPane, BorderLayout.CENTER);
        
        JButton composeButton = new JButton("Compose");
        composeButton.addActionListener(e -> showComposeDialog());

        // add(composeButton, BorderLayout.SOUTH);
        JButton refreshInboxButton = new JButton("Refresh Inbox");
        refreshInboxButton.addActionListener(e -> refreshInbox());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(composeButton);
        bottomPanel.add(refreshInboxButton);
        add(bottomPanel, BorderLayout.SOUTH);

        SwingUtilities.invokeLater(this::showLoginDialog);
    }

    private void refreshInbox() {
        try {
            messages = EmailSessionManager.getInstance().receiveEmail();
            emailListModel.clear();
            for (Message message : messages) {
                emailListModel.addElement(message.getSubject() + " - From: " + InternetAddress.toString(message.getFrom()));
            }
        } catch (MessagingException e) {
            JOptionPane.showMessageDialog(this, "Failed to fetch emails: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void emailListSelectionChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && emailList.getSelectedIndex() != -1) {
            try {
                Message selectedMessage = messages[emailList.getSelectedIndex()];
                emailContent.setText(""); // Clear previous content
                emailContent.append("Subject: " + selectedMessage.getSubject() + "\n\n");
                emailContent.append("From: " + InternetAddress.toString(selectedMessage.getFrom()) + "\n\n");
                // Use the new method to get and display the email body
                emailContent.append(getTextFromMessage(selectedMessage));
            } catch (MessagingException ex) {
                emailContent.setText("Error reading email content: " + ex.getMessage());
            } catch (IOException ex) {
                emailContent.setText("Error reading email content: " + ex.getMessage());
            }
        } 
    }

    private String getTextFromMessage(Message message) throws MessagingException, IOException {
        if (message.isMimeType("text/plain")) {
            return (String) message.getContent();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            for (int i = 0; i < mimeMultipart.getCount(); i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    return (String) bodyPart.getContent();
                }
            }
        }
        return "No readable content found."; // Fallback text
    }

    private void showComposeDialog() {
        JDialog composeDialog = new JDialog(this, "Compose Email", true);
        composeDialog.setLayout(new BorderLayout(5, 5));

        Box fieldsPanel = Box.createVerticalBox();
        JTextField toField = new JTextField();
        JTextField subjectField = new JTextField();
        JTextArea bodyArea = new JTextArea(10, 20);
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);

        fieldsPanel.add(new JLabel("To:"));
        fieldsPanel.add(toField);
        fieldsPanel.add(new JLabel("Subject:"));
        fieldsPanel.add(subjectField);

        JPanel bottomPanel = new JPanel();
        JButton attachButton = new JButton("Attach Files");
        JButton sendButton = new JButton("Send");
        JLabel attachedFilesLabel = new JLabel("No files attached");

        List<File> attachedFiles = new ArrayList<>();
        attachButton.addActionListener(e -> {
            File[] files = AttachmentChooser.chooseAttachments();
            attachedFiles.addAll(Arrays.asList(files));
            attachedFilesLabel.setText(attachedFiles.size() + " files attached");
        });

        sendButton.addActionListener(e -> {
            String to = toField.getText();
            String subject = subjectField.getText();
            String body = bodyArea.getText();
            Email email = new Email("", to, subject, body);
            File[] attachments = attachedFiles.toArray(new File[0]);
            EmailSender.sendEmailWithAttachment(email, attachments);
            composeDialog.dispose();
        });

        bottomPanel.add(attachButton);
        bottomPanel.add(sendButton);

        composeDialog.add(fieldsPanel, BorderLayout.NORTH);
        composeDialog.add(new JScrollPane(bodyArea), BorderLayout.CENTER);
        composeDialog.add(bottomPanel, BorderLayout.SOUTH);

        composeDialog.pack(); // Adjust dialog size to fit content
        composeDialog.setLocationRelativeTo(this); // Center dialog relative to the main window
        composeDialog.setVisible(true);
    }


    private void showLoginDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Email:"));
        panel.add(usernameField);
        panel.add(new JLabel("App Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            try {
                // Initialize EmailSessionManager here
                EmailSessionManager.getInstance(username, password);
                refreshInbox(); // Refresh inbox to load emails
            } catch (MessagingException e) {
                JOptionPane.showMessageDialog(this, "Failed to initialize email session: " + e.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("Login cancelled.");
        }
    }
}
