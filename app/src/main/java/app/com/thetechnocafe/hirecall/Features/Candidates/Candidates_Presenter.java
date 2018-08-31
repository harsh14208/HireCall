package app.com.thetechnocafe.hirecall.Features.Candidates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import app.com.thetechnocafe.hirecall.Models.JobModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * Created by stark on 15/6/17.
 */

public class Candidates_Presenter implements CandidatesContract.Presenter {

    private CandidatesContract.View mView;
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
    public void subscribe(CandidatesContract.View view) {
        mView = view;
        mCompositeDisposable = new CompositeDisposable();
        loadCallLogs();
    }

    @Override
    public void unSubscribe() {
        mView = null;

        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
    }
    private void loadCallLogs() {
        Disposable disposable = FirebaseDB.getInstance()
                .getListOfAllCallLogs()
                .subscribe(callLogModels -> {
                    mView.onCallLogsFetched(callLogModels);
                });

        mCompositeDisposable.add(disposable);
    }


}
