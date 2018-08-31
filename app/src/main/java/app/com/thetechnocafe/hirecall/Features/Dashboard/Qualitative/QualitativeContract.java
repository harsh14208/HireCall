package app.com.thetechnocafe.hirecall.Features.Dashboard.Qualitative;

import java.util.List;

import app.com.thetechnocafe.hirecall.BaseApp;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;

/**
 * Created by gurleensethi on 17/04/17.
 */

public interface QualitativeContract {
    interface View extends BaseApp.View {
        void onCallLogsFetched(List<CallLogModel> callLogs);
    }

    interface Presenter extends BaseApp.Presenter<QualitativeContract.View> {

    }
}
