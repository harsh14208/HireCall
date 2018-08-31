package app.com.thetechnocafe.hirecall.Features.JobDetailsCallHistory;

import android.telecom.Call;
import android.util.ArraySet;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.Utilities.PhoneCallContactUtility;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by gurleensethi on 17/04/17.
 */

public class JobDetailsCallHistoryPresenter implements JobDetailsCallHistoryContract.Presenter {

    private JobDetailsCallHistoryContract.View mView;
    private CompositeDisposable mCompositeDisposable;

    @Override
    public void subscribe(JobDetailsCallHistoryContract.View view) {
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
    public void fetchCallLogsForJob(String jobUID) {

        Disposable disposable = FirebaseDB.getInstance()
                .getListOfJobCallLogs(jobUID)
                .subscribe(callLogs -> {
                        List<String> number = new ArrayList<String>();
                        for (CallLogModel temp : callLogs) {
                            if(!number.contains(temp.getPhoneNumber())) {
                                number.add(temp.getPhoneNumber());
                            }
                        }

                        Log.e("numbers:", number.toString());

                        List<CallLogModel> uniquelist = new ArrayList<CallLogModel>();

                        for (CallLogModel callLog : callLogs) {
                            for (String num : number) {
                                if (callLog.getPhoneNumber().equals(num)) {
                                    uniquelist.add(callLog);
                                    callLog.setDisplayName(PhoneCallContactUtility.getInstance()
                                            .covertNumberToName(mView.getAppContext(), callLog.getPhoneNumber()));
                                    int index= number.indexOf(num);
                                    number.set(index,"0");
                                }
                            }
                        }


                        mView.setCallLogs(callLogs);
                        mView.onCallLogsFetched(uniquelist);

                });

        mCompositeDisposable.add(disposable);
    }
}
