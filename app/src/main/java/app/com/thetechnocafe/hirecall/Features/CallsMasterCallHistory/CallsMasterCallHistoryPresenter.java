package app.com.thetechnocafe.hirecall.Features.CallsMasterCallHistory;

import java.util.Collections;

import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.Utilities.PhoneCallContactUtility;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by gurleensethi on 17/04/17.
 */

public class CallsMasterCallHistoryPresenter implements CallsMasterCallHistoryContract.Presenter {

    private CallsMasterCallHistoryContract.View mView;
    private CompositeDisposable mCompositeDisposable;

    @Override
    public void subscribe(CallsMasterCallHistoryContract.View view) {
        mView = view;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void unSubscribe() {
        mView = null;

        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
    }

    @Override
    public void fetchCallLogsForJob() {
        Disposable disposable = FirebaseDB.getInstance()
                .getListOfAllCallLogs()
                .map(callLogs -> {
                    //Sort the list to bring up the recent call number
                    Collections.sort(callLogs, (o1, o2) -> {
                        if (o1.getCallDate() > o2.getCallDate()) {
                            return -1;
                        } else if (o1.getCallDate() < o2.getCallDate()) {
                            return 1;
                        } else {
                            return 0;
                        }
                    });

                    for (CallLogModel callLog : callLogs) {
                        callLog.setDisplayName(PhoneCallContactUtility.getInstance()
                                .covertNumberToName(mView.getAppContext(), callLog.getPhoneNumber()));
                    }

                    return callLogs;
                })
                .subscribe(callLogs -> {
                    mView.onCallLogsFetched(callLogs);
                });

        mCompositeDisposable.add(disposable);
    }
}
