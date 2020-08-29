package Model.IO;

import java.io.Serializable;
import java.util.ArrayList;

public class conversation implements Serializable {
    private String sender;
    private String reciver;
    private String time;
    private String text;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciver() {
        return reciver;
    }

    public void setReciver(String reciver) {
        this.reciver = reciver;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    private ArrayList<Message> messages;

    public conversation(String sender, String reciver, ArrayList<Message> messages) {
        this.sender = sender;
        this.reciver = reciver;
        this.messages = messages;
        String message = messages.get(messages.size()-1).getMessageText();
        if (message.length() > 20)
            text = message.substring(0, 20);
        else {
            text = message;
        }
    }
}
