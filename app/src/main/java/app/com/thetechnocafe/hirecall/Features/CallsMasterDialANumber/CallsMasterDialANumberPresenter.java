package app.com.thetechnocafe.hirecall.Features.CallsMasterDialANumber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.Models.JobModel;
import app.com.thetechnocafe.hirecall.Utilities.PhoneCallContactUtility;
import app.com.thetechnocafe.hirecall.Utilities.SharedPreferencesUtility;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * Created by gurleensethi on 17/04/17.
 */

public class CallsMasterDialANumberPresenter implements CallsMasterDialANumberContract.Presenter {

    private CallsMasterDialANumberContract.View mView;
    private CompositeDisposable mCompositeDisposable;
    private Function<List<JobModel>, List<JobModel>> removeArchivedFunction = jobModels -> {
        List<JobModel> finalJobList = new ArrayList<>();

        for (JobModel job : jobModels) {
            if (!job.isArchived()) {
                finalJobList.add(job);
            }
        }

        return finalJobList;
    };

    @Override
    public void subscribe(CallsMasterDialANumberContract.View view) {
        mView = view;
        mCompositeDisposable = new CompositeDisposable();

        loadAllJobs();
    }

    @Override
    public void unSubscribe() {
        mView = null;

        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
    }

    @Override
    public void searchForNumber(String number, String client, String jobID) {
        String domain = SharedPreferencesUtility.getInstance().getDomain(mView.getAppContext());

        String convertedNumber = PhoneCallContactUtility.getInstance().covertNumberToName(mView.getAppContext(), number);
        if (!convertedNumber.equals(number)) {
            mView.changeNumberToName(convertedNumber);
        }

        Disposable disposable = FirebaseDB.getInstance()
                .getListOfCallLogs(number)
                .map(lists -> {
                    List<List<CallLogModel>> completeCallLogs = new ArrayList<>();

                    for (List<CallLogModel> callLogList : lists) {
                        List<CallLogModel> callLogModelList = new ArrayList<>();

                        //This loop adds only the jobs with the requested jobID
                        for (CallLogModel callLog : callLogList) {
                            if (callLog.getDomain().equals(domain) || callLog.getJobID().equals(jobID)) {
                                callLog.setDisplayName(PhoneCallContactUtility.getInstance()
                                        .covertNumberToName(mView.getAppContext(), callLog.getPhoneNumber()));
                                callLogModelList.add(callLog);
                            }
                        }

                        if (callLogModelList.size() > 0) {
                            completeCallLogs.add(callLogList);
                        }
                    }
                    return completeCallLogs;
                })
                .subscribe(callLogs -> {
                    mView.onCallLogsFetched(callLogs, domain);
                });

        mCompositeDisposable.add(disposable);
    }

    private void loadAllJobs() {
        List<JobModel> allJobsList = new ArrayList<>();
        Disposable disposable = FirebaseDB.getInstance()
                .getCreatedJobList()
                .map(removeArchivedFunction)
                .subscribe(jobsList -> {
                    allJobsList.addAll(jobsList);
                    FirebaseDB.getInstance()
                            .getInvitedJobsList()
                            .map(removeArchivedFunction)
                            .subscribe(invitedJobsList -> {
                                allJobsList.addAll(invitedJobsList);
                                Collections.sort(allJobsList, (o1, o2) -> {
                                    if (o1.getTimeCreated() > o2.getTimeCreated()) {
                                        return 1;
                                    } else if (o1.getTimeCreated() < o2.getTimeCreated()) {
                                        return -1;
                                    } else {
                                        return 0;
                                    }
                                });
                                mView.onJobListFetched(allJobsList);
                            });
                });

        mCompositeDisposable.add(disposable);
    }
}
