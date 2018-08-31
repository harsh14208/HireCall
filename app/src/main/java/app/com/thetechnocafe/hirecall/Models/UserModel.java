package app.com.thetechnocafe.hirecall.Models;

import com.google.firebase.database.Exclude;

/**
 * Created by gurleensethi on 17/04/17.
 */

public class UserModel {
    @Exclude
    private String uid;
    private String name;
    private String email;
    private String domain;
    private String imageUrl;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
