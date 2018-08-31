package app.com.thetechnocafe.hirecall.Features.FeedbackAttended;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import app.com.thetechnocafe.hirecall.Enums.NotificationType;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.Models.TodoModel;
import app.com.thetechnocafe.hirecall.Utilities.AlarmSchedulerUtility;
import app.com.thetechnocafe.hirecall.Utilities.Constants;
import app.com.thetechnocafe.hirecall.Utilities.SharedPreferencesUtility;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by gurleensethi on 17/04/17.
 */

public class FeedbackAttendedPresenter implements FeedbackAttendedContract.Presenter {

    private FeedbackAttendedContract.View mView;
    private CompositeDisposable mCompositeDisposable;

    @Override
    public void subscribe(FeedbackAttendedContract.View view) {
        mView = view;
        mCompositeDisposable = new CompositeDisposable();
    }

    @Override
    public void unSubscribe() {
        mView = null;

        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
    }

    @Override
    public void submitFeedback(String feedbackReason, ArrayList<String> feedbackModel, TodoModel todo) {
        //Get the job model from file
        BufferedReader bufferedReader;
        File file;
        try {
            file = new File(mView.getAppContext().getFilesDir(), Constants.FILE_CALL_LOG_CACHE);
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String jobString = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                jobString += line;
            }
            bufferedReader.close();

            //Convert to json model
            Gson gson = new Gson();
            CallLogModel callLogModel = gson.fromJson(jobString, CallLogModel.class);
            callLogModel.setFeedback(feedbackModel);
            callLogModel.setFeedbackReason(feedbackReason);

            FirebaseDB.getInstance()
                    .createCallLog(callLogModel)
                    .subscribe(result -> {
                        if (result) {
                            SharedPreferencesUtility.getInstance().setFeedbackPending(mView.getAppContext(), false);
                            if (todo != null) {
                                Log.d("TAG", "Creating Todo");
                                FirebaseDB.getInstance()
                                        .createReminder(todo)
                                        .subscribe(todoResult -> {
                                            switch (todo.getTodoType()) {
                                                case INTERVIEW: {
                                                    AlarmSchedulerUtility.getInstance()
                                                            .scheduleReminderForInterview(
                                                                    mView.getAppContext(),
                                                                    todo.getTitle(),
                                                                    todo.getDescription(),
                                                                    todo.getTime()
                                                            );
                                                    break;
                                                }
                                                case CALL_LATER: {
                                                    AlarmSchedulerUtility.getInstance()
                                                            .scheduleReminder(
                                                                    mView.getAppContext(),
                                                                    todo.getTitle(),
                                                                    todo.getDescription(),
                                                                    todo.getTime(),
                                                                    (int) (Math.random() * 10000),
                                                                    todo.getTodoType(),
                                                                    NotificationType.DEFAULT
                                                            );
                                                    break;
                                                }
                                            }

                                            mView.closeFeedbackActivity();
                                        });
                            } else {
                                mView.closeFeedbackActivity();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
