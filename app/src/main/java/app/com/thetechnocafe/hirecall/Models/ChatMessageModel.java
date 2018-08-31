package app.com.thetechnocafe.hirecall.Models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

/**
 * Created by gurleen on 8/5/17.
 */

public class ChatMessageModel implements Serializable {
    @Exclude
    private String id;
    private long timeStamp;
    private String userID;
    private String message;
    private String name;

    public String getId() {
        return id;
    }

    public String getName(){
        return name;
    }

    public void setName(String n){
        this.name=n;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
