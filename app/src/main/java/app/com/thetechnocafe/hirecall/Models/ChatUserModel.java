package app.com.thetechnocafe.hirecall.Models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * Created by gurleen on 8/5/17.
 */

public class ChatUserModel implements Serializable {
    @Exclude
    private String userID;
    private String chatID;
    private String userName;
    private ChatMessageModel lastChatMessage;
    private String email;

    public ChatMessageModel getLastChatMessage() {
        return lastChatMessage;
    }

    public void setLastChatMessage(ChatMessageModel lastChatMessage) {
        this.lastChatMessage = lastChatMessage;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
