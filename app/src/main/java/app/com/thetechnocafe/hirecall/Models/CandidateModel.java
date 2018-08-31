package app.com.thetechnocafe.hirecall.Models;

import java.util.List;

/**
 * Created by stark on 12/7/17.
 */

public class CandidateModel {
    String username;
    String experience;
    String designation;
    String employer;
    String college;
    String status;
    String phoneNumber;
    String clientskills;
    String company;
    String jobId;
    String uId;
    Boolean isSelected=false;
    String firebaseRegId;

    public void setFirebaseRegId(String regId){
        this.firebaseRegId=regId;
    }

    public String getFirebaseRegId(){
        return firebaseRegId;
    }

    public String getphoneNumber() {
        return phoneNumber;
    }

    public void setphoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) { this.username=username;}

    public String getExperience() { return experience; }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer=employer;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getClientskills() {
        return clientskills;
    }

    public void setClientskills(String clientskills) {
        this.clientskills = clientskills;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
    public String getjobId() {
        return jobId;
    }

    public void setjobId(String jobId) {
        this.jobId = jobId;
    }

    public String getuID() {
        return uId;
    }

    public void setuID(String uID) { this.uId = uID; }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public boolean isSelected() {
        return isSelected;
    }

}
