package app.com.thetechnocafe.hirecall.Features.Jobs.ActiveJobs;

import java.util.List;

import app.com.thetechnocafe.hirecall.BaseApp;
import app.com.thetechnocafe.hirecall.Models.JobModel;

/**
 * Created by gurleensethi on 17/04/17.
 */

public interface ActiveJobsContract {
    interface View extends BaseApp.View {
        void onJobsLoaded(List<JobModel> jobsList);

        void onJobArchived();
    }

    interface Presenter extends BaseApp.Presenter<ActiveJobsContract.View> {
        void onArchiveClicked(JobModel jobModel);

        void reloadJobs();

        void loadCreatedJobs();

        void loadInvitedJobs();

        void loadAllJobs();
    }
}
