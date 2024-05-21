package service;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public class EmailSessionManager {
    private Session emailSession;
    private Store store;
    private Folder emailFolder;
    private static EmailSessionManager instance;
    
    private static String currentUsername = "";
    private static String currentPassword = "";

    private EmailSessionManager(String username, String password) throws MessagingException {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", "imap.gmail.com");
        properties.put("mail.imaps.port", "993");
        properties.put("mail.imaps.ssl.enable", "true");

        this.emailSession = Session.getInstance(properties, null);
        this.store = emailSession.getStore("imaps");
        this.store.connect(username, password);
        
        // Store the credentials upon successful connection
        currentUsername = username;
        currentPassword = password;
    }

    public static EmailSessionManager getInstance(String username, String password) throws MessagingException {
        if (instance == null) {
            instance = new EmailSessionManager(username, password);
        }
        return instance;
    }
    
    public static EmailSessionManager getInstance() throws IllegalStateException {
        if (instance == null) {
            throw new IllegalStateException("EmailSessionManager is not initialized. Please login first.");
        }
        return instance;
    }

    public static String getUsername() {
        return currentUsername;
    }

    public static String getPassword() {
        return currentPassword;
    }

    public Message[] receiveEmail() throws MessagingException {
        if (emailFolder == null || !emailFolder.isOpen()) {
            emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);
        }
        return emailFolder.getMessages();
    }

    public void close() throws MessagingException {
        if (emailFolder != null) {
            emailFolder.close(false);
            emailFolder = null;
        }
        if (store != null) {
            store.close();
            store = null;
        }
        instance = null;
        // Clear the credentials upon closing the session
        currentUsername = "";
        currentPassword = "";
    }
}
