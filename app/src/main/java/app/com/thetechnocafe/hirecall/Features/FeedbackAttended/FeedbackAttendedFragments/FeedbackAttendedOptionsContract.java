package app.com.thetechnocafe.hirecall.Features.FeedbackAttended.FeedbackAttendedFragments;

import java.util.ArrayList;

import app.com.thetechnocafe.hirecall.Models.TodoModel;

/**
 * Created by gurleen on 22/4/17.
 */

public interface FeedbackAttendedOptionsContract {
    ArrayList<String> getListOfFeedbackOptions();

    TodoModel getTodo();
}
