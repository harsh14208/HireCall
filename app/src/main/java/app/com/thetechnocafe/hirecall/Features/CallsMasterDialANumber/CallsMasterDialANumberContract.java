package app.com.thetechnocafe.hirecall.Features.CallsMasterDialANumber;

import java.util.List;

import app.com.thetechnocafe.hirecall.BaseApp;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.Models.JobModel;

/**
 * Created by gurleensethi on 17/04/17.
 */

public interface CallsMasterDialANumberContract {
    interface View extends BaseApp.View {
        void onCallLogsFetched(List<List<CallLogModel>> callLogs, String domain);

        void onJobListFetched(List<JobModel> jobsList);

        void changeNumberToName(String displayName);
    }

    interface Presenter extends BaseApp.Presenter<CallsMasterDialANumberContract.View> {
        void searchForNumber(String number, String client, String jobID);
    }
}
