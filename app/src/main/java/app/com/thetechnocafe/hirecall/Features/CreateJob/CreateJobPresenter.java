package app.com.thetechnocafe.hirecall.Features.CreateJob;

import java.util.ArrayList;
import java.util.List;

import app.com.thetechnocafe.hirecall.Data.FirebaseAuthDB;
import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import app.com.thetechnocafe.hirecall.Models.JobModel;
import app.com.thetechnocafe.hirecall.Utilities.SharedPreferencesUtility;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by gurleensethi on 17/04/17.
 */

public class CreateJobPresenter implements CreateJobContract.Presenter {

    private CreateJobContract.View mView;
    private CompositeDisposable mCompositeDisposable;

    @Override
    public void subscribe(CreateJobContract.View view) {
        mView = view;
        mCompositeDisposable = new CompositeDisposable();

        loadSkillsAndClients();
    }

    @Override
    public void unSubscribe() {
        mView = null;

        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
    }

    @Override
    public void logoutUser() {
        FirebaseAuthDB.getInstance().signOut();
    }

    @Override
    public void createNewJob(JobModel job, boolean isClientNameCustom, boolean isSkillCustom) {
        job.setCreatorName(SharedPreferencesUtility.getInstance().getUserName(mView.getAppContext()));
        job.setMailID(SharedPreferencesUtility.getInstance().getEmail(mView.getAppContext()));

        Disposable disposable = FirebaseDB.getInstance()
                .createJob(job, isClientNameCustom, isSkillCustom)
                .subscribe(result -> {
                    if (result) {
                        SharedPreferencesUtility.getInstance().setFirstJobCreated(mView.getAppContext(), true);
                        mView.successCreatingJob();
                    } else {
                        mView.jobCreationFailed();
                    }
                });

        mCompositeDisposable.add(disposable);
    }

    private void loadSkillsAndClients() {
        Disposable disposable = FirebaseDB.getInstance()
                .getClientsMap()
                .subscribe(clientsMap -> {
                    List<String> clients = new ArrayList<>(clientsMap.values());
                    mView.onClientsReceived(clients);
                    FirebaseDB.getInstance()
                            .getSkillsMap()
                            .subscribe(skillsMap -> {
                                List<String> skills = new ArrayList<>(skillsMap.values());
                                mView.onSkillsReceived(skills);
                                mView.stopProgress();
                            });
                });

        mCompositeDisposable.add(disposable);
    }
}
