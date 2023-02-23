package com.hakaninc.messengerapp.model;

public class Chat {

    private String sender;
    private String message;
    private String receiver;
    private Boolean isseen = false;
    private String url;
    private String messageID;

    public Chat() {
    }

    public Chat(String sender, String message, String receiver, Boolean isseen, String url, String messageID) {
        this.sender = sender;
        this.message = message;
        this.receiver = receiver;
        this.isseen = isseen;
        this.url = url;
        this.messageID = messageID;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Boolean getIsseen() {
        return isseen;
    }

    public void setIsseen(Boolean isseen) {
        this.isseen = isseen;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }
}
