package app.com.thetechnocafe.hirecall.Utilities;

import java.util.List;

import app.com.thetechnocafe.hirecall.Models.CallLogModel;

/**
 * Created by gurleen on 29/4/17.
 */

public class CallLogListHolder {
    private static final CallLogListHolder ourInstance = new CallLogListHolder();
    private List<CallLogModel> callLogList;

    public static CallLogListHolder getInstance() {
        return ourInstance;
    }

    private CallLogListHolder() {
    }

    public List<CallLogModel> getCallLogList() {
        return callLogList;
    }

    public void setCallLogList(List<CallLogModel> callLogList) {
        this.callLogList = callLogList;
    }
}
