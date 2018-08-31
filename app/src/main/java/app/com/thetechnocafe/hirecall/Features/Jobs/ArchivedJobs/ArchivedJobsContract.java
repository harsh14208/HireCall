package app.com.thetechnocafe.hirecall.Features.Jobs.ArchivedJobs;

import java.util.List;

import app.com.thetechnocafe.hirecall.BaseApp;
import app.com.thetechnocafe.hirecall.Models.JobModel;

/**
 * Created by gurleensethi on 17/04/17.
 */

public interface ArchivedJobsContract {
    interface View extends BaseApp.View {
        void onJobsLoaded(List<JobModel> jobsList);

        void onJobUnArchived();
    }

    interface Presenter extends BaseApp.Presenter<ArchivedJobsContract.View> {
        void onUnArchiveClicked(JobModel jobModel);

        void reloadJobs();

        void loadCreatedJobs();

        void loadInvitedJobs();

        void loadAllJobs();
    }
}
