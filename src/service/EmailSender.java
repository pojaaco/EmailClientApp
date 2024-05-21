package service;

import java.io.File;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import model.Email;

public class EmailSender {
    public static void sendEmailWithAttachment(Email email, File[] attachments) {
        try {
            String username = EmailSessionManager.getUsername();
            String password = EmailSessionManager.getPassword();

            Properties prop = new Properties();
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "587");
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.starttls.enable", "true"); // Enable TLS

            Session session = Session.getInstance(prop, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getRecipient()));
                message.setSubject(email.getSubject());

                Multipart multipart = new MimeMultipart();

                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setText(email.getMsgBody());
                multipart.addBodyPart(textPart);

                // Add each attachment
                for (File file : attachments) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.attachFile(file);
                    multipart.addBodyPart(attachmentPart);
                }

                message.setContent(multipart);
                Transport.send(message);
                System.out.println("Email sent successfully with attachments.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
