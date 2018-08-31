package app.com.thetechnocafe.hirecall.Data;

import java.util.List;
import java.util.Map;

import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.Models.JobModel;
import app.com.thetechnocafe.hirecall.Models.TodoModel;
import app.com.thetechnocafe.hirecall.Models.UserModel;
import io.reactivex.Observable;

/**
 * Created by gurleensethi on 17/04/17.
 */

public interface DatabaseContract {
    Observable<Boolean> addUserToDatabase(String uid, UserModel userModel);

    Observable<Boolean> checkForFirstJob();

    Observable<Map<String, String>> getClientsMap();

    Observable<Map<String, String>> getSkillsMap();

    Observable<Boolean> createJob(JobModel job, boolean isClientCustom, boolean isSkillCustom);

    Observable<UserModel> getUser();

    Observable<List<JobModel>> getCreatedJobList();

    Observable<Boolean> createCallLog(CallLogModel callLog);

    Observable<List<List<CallLogModel>>> getListOfCallLogs(String number);

    Observable<List<CallLogModel>> getListOfJobCallLogs(String jobUID);

    Observable<List<JobModel>> getInvitedJobsList();

    Observable<Boolean> setJobArchived(JobModel jobModel, boolean isArchived);

    Observable<List<CallLogModel>> getListOfAllCallLogs();

    Observable<Boolean> createReminder(TodoModel todo);

    Observable<List<TodoModel>> getTodoList();

    Observable<Boolean> changeTodoCompleted(TodoModel todo);

    Observable<Boolean> setUserImageURL(String imageURL);

    Observable<String> getUserImageURL(String userID);
}
