package app.com.thetechnocafe.hirecall.Models;

/**
 * Created by gurleen on 20/4/17.
 */

public class SimplifiedCallLogModel {
    private String number;
    private int duration;
    private long callDate;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getCallDate() {
        return callDate;
    }

    public void setCallDate(long callDate) {
        this.callDate = callDate;
    }
}
