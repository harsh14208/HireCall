package app.com.thetechnocafe.hirecall.Features.JobDetailsCallHistory;

import java.util.List;

import app.com.thetechnocafe.hirecall.BaseApp;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;

/**
 * Created by gurleensethi on 17/04/17.
 */

public interface JobDetailsCallHistoryContract {
    interface View extends BaseApp.View {
        void onCallLogsFetched(List<CallLogModel> callLogs);
        void setCallLogs(List<CallLogModel> CallLog);
    }

    interface Presenter extends BaseApp.Presenter<JobDetailsCallHistoryContract.View> {
        void fetchCallLogsForJob(String jobUID);
    }
}
