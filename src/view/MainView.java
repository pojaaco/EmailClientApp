package view;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.awt.event.WindowAdapter;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;

import service.EmailService;
import utils.TextFromMessage;

public class MainView extends JFrame{
    private DefaultListModel<String> emailListModel;
    private JList<String> emailList;
    private JTextArea emailContent;
    private Message[] messages;

    public MainView() {
        this.emailListModel = new DefaultListModel<>();
        this.emailList = new JList<>(emailListModel);

        setTitle("Java Email Client");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (EmailService.getInstance() != null) {
                        EmailService.getInstance().close();
                    }
                } catch (MessagingException ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        initUI();
    }

    private void initUI() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setOneTouchExpandable(true);

        emailList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        emailList.addListSelectionListener(this::emailListSelectionChanged);
        JScrollPane listScrollPane = new JScrollPane(emailList);
        splitPane.setLeftComponent(listScrollPane);

        emailContent = new JTextArea();
        emailContent.setEditable(false);
        JScrollPane contentScrollPane = new JScrollPane(emailContent);
        splitPane.setRightComponent(contentScrollPane);

        add(splitPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();

        JButton composeButton = new JButton("Compose");
        composeButton.addActionListener(e -> new ComposeView());
        bottomPanel.add(composeButton);

        JButton refreshInboxButton = new JButton("Refresh Inbox");
        refreshInboxButton.addActionListener(e -> refreshInbox());
        bottomPanel.add(refreshInboxButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void emailListSelectionChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && emailList.getSelectedIndex() != -1) {
            try {
                Message selectedMessage = messages[emailList.getSelectedIndex()];
                emailContent.setText(""); // Clear previous content
                emailContent.append("Subject: " + selectedMessage.getSubject() + "\n\n");
                emailContent.append("From: " + InternetAddress.toString(selectedMessage.getFrom()) + "\n\n");
                emailContent.append(TextFromMessage.extract(selectedMessage));
            } catch (MessagingException ex) {
                emailContent.setText("Error reading email content: " + ex.getMessage());
            } catch (IOException ex) {
                emailContent.setText("Error reading email content: " + ex.getMessage());
            }
        } 
    }

    private void refreshInbox() {
        try {
            messages = EmailService.getInstance().getInbox();
            emailListModel.clear();
            for (Message message : messages) {
                emailListModel.addElement(message.getSubject() + " - From: " + InternetAddress.toString(message.getFrom()));
            }
        } catch (MessagingException e) {
            JOptionPane.showMessageDialog(this, "Failed to fetch emails: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
