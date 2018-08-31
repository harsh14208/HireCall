package app.com.thetechnocafe.hirecall.Features.FeedbackCallMissed;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.Utilities.Constants;
import app.com.thetechnocafe.hirecall.Utilities.SharedPreferencesUtility;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by gurleensethi on 17/04/17.
 */

public class FeedbackCallMissedPresenter implements FeedbackCallMissedContract.Presenter {

    private FeedbackCallMissedContract.View mView;
    private CompositeDisposable mCompositeDisposable;

    @Override
    public void subscribe(FeedbackCallMissedContract.View view) {
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
    public void submitFeedback(ArrayList<String> feedbackModel) {
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
            callLogModel.setFeedbackReason("Didn't respond/picked the call");

            Log.e("createcall log", feedbackModel.toString());

            FirebaseDB.getInstance()
                    .createCallLog(callLogModel)
                    .subscribe(result -> {
                        if (result) {
                            SharedPreferencesUtility.getInstance().setFeedbackPending(mView.getAppContext(), false);
                            mView.closeFeedbackActivity();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
