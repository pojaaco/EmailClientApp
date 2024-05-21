package model;

public class Email {
    private String sender;
    private String recipient;
    private String subject;
    private String msgBody;
    
    public Email(String sender, String recipient, String subject, String msgBody) {
        this.sender = sender;
        this.recipient = recipient;
        this.subject = subject;
        this.msgBody = msgBody;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

}
