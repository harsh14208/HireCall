package app.com.thetechnocafe.hirecall.Features.Jobs.ArchivedJobs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import app.com.thetechnocafe.hirecall.Models.JobModel;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * Created by gurleensethi on 17/04/17.
 */

public class ArchivedJobsPresenter implements ArchivedJobsContract.Presenter {

    private ArchivedJobsContract.View mView;
    private CompositeDisposable mCompositeDisposable;
    private Function<List<JobModel>, List<JobModel>> filterForArchived = jobModels -> {
        List<JobModel> finalJobList = new ArrayList<>();

        for (JobModel job : jobModels) {
            if (job.isArchived()) {
                finalJobList.add(job);
            }
        }

        return finalJobList;
    };

    @Override
    public void subscribe(ArchivedJobsContract.View view) {
        mView = view;
        mCompositeDisposable = new CompositeDisposable();

        loadCreatedJobs();
    }

    @Override
    public void unSubscribe() {
        mView = null;

        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
    }

    @Override
    public void onUnArchiveClicked(JobModel jobModel) {
        Disposable disposable = FirebaseDB.getInstance()
                .setJobArchived(jobModel, false)
                .subscribe(result -> {
                    mView.onJobUnArchived();
                });

        mCompositeDisposable.add(disposable);
    }

    @Override
    public void reloadJobs() {

    }

    @Override
    public void loadCreatedJobs() {
        Disposable disposable = FirebaseDB.getInstance()
                .getCreatedJobList()
                .map(filterForArchived)
                .subscribe(jobsList -> {
                    mView.onJobsLoaded(jobsList);
                });

        mCompositeDisposable.add(disposable);
    }

    @Override
    public void loadInvitedJobs() {
        Disposable disposable = FirebaseDB.getInstance()
                .getInvitedJobsList()
                .map(filterForArchived)
                .subscribe(jobsList -> {
                    mView.onJobsLoaded(jobsList);
                });

        mCompositeDisposable.add(disposable);
    }

    @Override
    public void loadAllJobs() {
        List<JobModel> allJobsList = new ArrayList<>();
        Disposable disposable = FirebaseDB.getInstance()
                .getCreatedJobList()
                .map(filterForArchived)
                .subscribe(jobsList -> {
                    allJobsList.addAll(jobsList);
                    FirebaseDB.getInstance()
                            .getInvitedJobsList()
                            .map(filterForArchived)
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
                                mView.onJobsLoaded(allJobsList);
                            });
                });
        mCompositeDisposable.add(disposable);
    }
}