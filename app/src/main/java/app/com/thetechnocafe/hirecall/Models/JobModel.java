package app.com.thetechnocafe.hirecall.Models;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;

/**
 * Created by gurleensethi on 18/04/17.
 */

public class JobModel implements Serializable {
    @Exclude
    private String jobID;
    private String creatorID;
    private String creatorName;
    private String mailID;
    private String clientName;
    private String clientID;
    private String primarySkill;
    private String subLocation;
    private String cityLocation;
    private int minExpense;
    private int maxExpense;
    private List<String> invitedUserEmails;
    private long timeCreated;
    private boolean isArchived;
    @Exclude
    private boolean isInvited;

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public String getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(String creatorID) {
        this.creatorID = creatorID;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getMailID() {
        return mailID;
    }

    public void setMailID(String mailID) {
        this.mailID = mailID;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getPrimarySkill() {
        return primarySkill;
    }

    public void setPrimarySkill(String primarySkill) {
        this.primarySkill = primarySkill;
    }

    public String getSubLocation() {
        return subLocation;
    }

    public void setSubLocation(String subLocation) {
        this.subLocation = subLocation;
    }

    public String getCityLocation() {
        return cityLocation;
    }

    public void setCityLocation(String cityLocation) {
        this.cityLocation = cityLocation;
    }

    public int getMinExpense() {
        return minExpense;
    }

    public void setMinExpense(int minExpense) {
        this.minExpense = minExpense;
    }

    public int getMaxExpense() {
        return maxExpense;
    }

    public void setMaxExpense(int maxExpense) {
        this.maxExpense = maxExpense;
    }

    public List<String> getInvitedUserEmails() {
        return invitedUserEmails;
    }

    public void setInvitedUserEmails(List<String> invitedUserEmails) {
        this.invitedUserEmails = invitedUserEmails;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public boolean isInvited() {
        return isInvited;
    }

    public void setInvited(boolean invited) {
        isInvited = invited;
    }
}
