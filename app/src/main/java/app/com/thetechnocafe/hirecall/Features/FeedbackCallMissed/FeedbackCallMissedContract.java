package app.com.thetechnocafe.hirecall.Features.FeedbackCallMissed;

import java.util.ArrayList;

import app.com.thetechnocafe.hirecall.BaseApp;

/**
 * Created by gurleensethi on 17/04/17.
 */

public interface FeedbackCallMissedContract {
    interface View extends BaseApp.View {
        void closeFeedbackActivity();
    }

    interface Presenter extends BaseApp.Presenter<FeedbackCallMissedContract.View> {
        void submitFeedback(ArrayList<String> feedbackModel);
    }
}
