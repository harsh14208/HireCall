package app.com.thetechnocafe.hirecall.Features.Candidates;
import java.util.List;

import app.com.thetechnocafe.hirecall.BaseApp;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.Models.JobModel;

public interface CandidatesContract {
    interface View extends BaseApp.View {
        void onCallLogsFetched(List<CallLogModel> callLogs);


    }

    interface Presenter extends BaseApp.Presenter<CandidatesContract.View> {

    }
}