package app.com.thetechnocafe.hirecall.Features.Dashboard.Analysis;

/**
 * Created by stark on 15/6/17.
 */
import  java.util.List;

import app.com.thetechnocafe.hirecall.BaseApp;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;

public interface AnalysisContract {
    interface View extends BaseApp.View {
        void onCallLogsFetched(List<CallLogModel> callLogs);
    }

    interface Presenter extends BaseApp.Presenter<app.com.thetechnocafe.hirecall.Features.Dashboard.Analysis.AnalysisContract.View> {

    }
}
