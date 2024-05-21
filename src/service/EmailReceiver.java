package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public class EmailReceiver {
    private static String username = "";
    private static String password = "";

    public static void setCredentials(String user, String pass) {
        username = user;
        password = pass;
    }

    public static Message[] receiveEmail() throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", "imap.gmail.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.ssl.enable", "true");
        
        List<Message> messagesList = new ArrayList<>();
    
        Session emailSession = Session.getInstance(properties);
        Store store = emailSession.getStore("imaps");
        store.connect("imap.gmail.com", username, password);
    
        Folder emailFolder = store.getFolder("INBOX");
        emailFolder.open(Folder.READ_ONLY);
    
        Message[] messages = emailFolder.getMessages();
        for (Message message : messages) {
            messagesList.add(message);
        }
    
        emailFolder.close(false);
        store.close();
        
        return messagesList.toArray(new Message[0]);
    }
}
