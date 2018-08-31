package app.com.thetechnocafe.hirecall.Features.CreateJob;

import java.util.List;

import app.com.thetechnocafe.hirecall.BaseApp;
import app.com.thetechnocafe.hirecall.Models.JobModel;

/**
 * Created by gurleensethi on 17/04/17.
 */

public interface CreateJobContract {
    interface View extends BaseApp.View {
        void onClientsReceived(List<String> clients);

        void onSkillsReceived(List<String> skills);

        void stopProgress();

        void successCreatingJob();

        void jobCreationFailed();
    }

    interface Presenter extends BaseApp.Presenter<CreateJobContract.View> {
        void logoutUser();

        void createNewJob(JobModel job, boolean isClientNameCustom, boolean isSkillCustom);
    }
}
