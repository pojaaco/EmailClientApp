package service;

import java.io.File;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

public class EmailService {
    private String username;
    private String password;
    private static EmailService instance;

    private Store emailStorage;
    private Session storeSession;

    private EmailService(String username, String password) throws MessagingException {
        this.username = username;
        this.password = password;

        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", "imap.gmail.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.ssl.enable", "true");
        storeSession = Session.getInstance(properties, null);
        this.emailStorage = storeSession.getStore("imaps");
        this.emailStorage.connect(username, password);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static EmailService getInstance(String username, String password) throws MessagingException {
        if (instance == null) {
            instance = new EmailService(username, password);
        }
        return instance;
    }

    public static EmailService getInstance() throws IllegalStateException {
        if (instance == null) {
            throw new IllegalStateException("EmailService is not initialized. Please login first.");
        }
        return instance;
    }

    public void close() throws MessagingException {
        if (emailStorage != null) {
            emailStorage.close();
            emailStorage = null;
        }
        instance = null;
        username = "";
        password = "";
    }

    public Message[] getInbox() throws MessagingException {
        Folder inboxFolder = emailStorage.getFolder("INBOX");
        inboxFolder.open(Folder.READ_ONLY);
        return inboxFolder.getMessages();
    }

    public void sendEmail(String to, String subject, String body, File[] attachments) {
        try {
            Properties properties = new Properties();
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            if (attachments == null) {
                message.setText(body);
            } else {
                Multipart multipart = new MimeMultipart();

                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setText(body);
                multipart.addBodyPart(textPart);

                for (File file : attachments) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.attachFile(file);
                    multipart.addBodyPart(attachmentPart);
                }

                message.setContent(multipart);
            }
            
            Transport.send(message);
            System.out.println("Email sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
