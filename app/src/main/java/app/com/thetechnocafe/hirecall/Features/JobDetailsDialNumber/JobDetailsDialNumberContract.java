package app.com.thetechnocafe.hirecall.Features.JobDetailsDialNumber;

import java.util.List;

import app.com.thetechnocafe.hirecall.BaseApp;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;

/**
 * Created by gurleensethi on 17/04/17.
 */

public interface JobDetailsDialNumberContract {
    interface View extends BaseApp.View {

        void onCallLogsFetched(List<List<CallLogModel>> callLogs, String domain);

        void changeNumberToName(String displayName);

        void setCallLogs(List<CallLogModel> callLog);

    }

    interface Presenter extends BaseApp.Presenter<JobDetailsDialNumberContract.View> {
        void searchForNumber(String number, String client, String jobID);
    }
}
