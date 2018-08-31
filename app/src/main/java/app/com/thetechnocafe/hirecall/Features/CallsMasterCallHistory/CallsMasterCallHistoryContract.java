package app.com.thetechnocafe.hirecall.Features.CallsMasterCallHistory;

import java.util.List;

import app.com.thetechnocafe.hirecall.BaseApp;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;

/**
 * Created by gurleensethi on 17/04/17.
 */

public interface CallsMasterCallHistoryContract {
    interface View extends BaseApp.View {
        void onCallLogsFetched(List<CallLogModel> callLogs);
    }

    interface Presenter extends BaseApp.Presenter<CallsMasterCallHistoryContract.View> {
        void fetchCallLogsForJob();
    }
}
