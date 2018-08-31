package app.com.thetechnocafe.hirecall.Features.FeedbackAttended;

import java.util.ArrayList;

import app.com.thetechnocafe.hirecall.BaseApp;
import app.com.thetechnocafe.hirecall.Models.TodoModel;

/**
 * Created by gurleensethi on 17/04/17.
 */

public interface FeedbackAttendedContract {
    interface View extends BaseApp.View {
        void closeFeedbackActivity();
    }

    interface Presenter extends BaseApp.Presenter<FeedbackAttendedContract.View> {
        void submitFeedback(String feedbackReason, ArrayList<String> feedbackModel, TodoModel todo);
    }
}
