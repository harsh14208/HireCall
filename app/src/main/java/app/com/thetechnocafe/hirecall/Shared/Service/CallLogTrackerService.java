package app.com.thetechnocafe.hirecall.Shared.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;

import app.com.thetechnocafe.hirecall.Features.Feedback.FeedbackActivity;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.Models.JobModel;
import app.com.thetechnocafe.hirecall.Models.SimplifiedCallLogModel;
import app.com.thetechnocafe.hirecall.Utilities.PhoneCallContactUtility;
import app.com.thetechnocafe.hirecall.Utilities.Constants;
import app.com.thetechnocafe.hirecall.Utilities.SharedPreferencesUtility;

public class CallLogTrackerService extends Service {
    public static final String EXTRA_JOB_MODEL = "job_model";
    public static final String EXTRA_CURRENT_NUMBER = "current_number";
    private static JobModel JOB_MODEL;
    private static String CURRENT_NUMBER;
    private static int PREVIOUS_CALL_STATE;

    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            Log.d("CALL STATE", String.valueOf(state));
            switch (state) {
                case TelephonyManager.CALL_STATE_OFFHOOK: {
                    PREVIOUS_CALL_STATE = state;
                    break;
                }
                case TelephonyManager.CALL_STATE_RINGING: {
                    PREVIOUS_CALL_STATE = state;
                    break;
                }
                case TelephonyManager.CALL_STATE_IDLE: {
                    if (PREVIOUS_CALL_STATE == TelephonyManager.CALL_STATE_OFFHOOK || PREVIOUS_CALL_STATE == TelephonyManager.CALL_STATE_RINGING) {
                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            String name = SharedPreferencesUtility.getInstance().getUserName(getApplicationContext());
                            String domain = SharedPreferencesUtility.getInstance().getDomain(getApplicationContext());
                            SimplifiedCallLogModel callLog = PhoneCallContactUtility.getInstance().getLastCallDetails(getApplicationContext(), CURRENT_NUMBER);

                            CallLogModel callLogModel = new CallLogModel();
                            callLogModel.setDuration(callLog.getDuration());
                            callLogModel.setCallDate(callLog.getCallDate());
                            callLogModel.setPhoneNumber(callLog.getNumber());
                            callLogModel.setClient(JOB_MODEL.getClientName());
                            callLogModel.setPrimarySkill(JOB_MODEL.getPrimarySkill());
                            callLogModel.setName(name);
                            callLogModel.setDomain(domain);
                            callLogModel.setJobID(JOB_MODEL.getJobID());

                            //Save client name and number in SP
                            SharedPreferencesUtility.getInstance().setClient(getApplicationContext(), callLogModel.getClient());
                            SharedPreferencesUtility.getInstance().setCallNumber(getApplicationContext(), callLogModel.getPhoneNumber());

                            Gson gson = new Gson();
                            //Save to internal file system
                            String jobToJson = gson.toJson(callLogModel);

                            //Delete existing file
                            File dir = getFilesDir();
                            File existingFile = new File(dir, Constants.FILE_CALL_LOG_CACHE);
                            existingFile.delete();

                            FileOutputStream fileOutputStream = null;
                            try {
                                fileOutputStream = openFileOutput(Constants.FILE_CALL_LOG_CACHE, Context.MODE_PRIVATE);
                                fileOutputStream.write(jobToJson.getBytes());
                                fileOutputStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //Make shared preferences pending
                            SharedPreferencesUtility.getInstance().setFeedbackPending(getApplicationContext(), true);

                            //Start the Feedback activity
                            Intent intent = new Intent(getApplicationContext(), FeedbackActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);

                            stopSelf();

                            Log.d("TAG", jobToJson);
                        }, 2000);
                    }

                    PREVIOUS_CALL_STATE = state;
                }
            }
        }
    };
    private TelephonyManager mTelephonyManager;

    public CallLogTrackerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTelephonyManager = (TelephonyManager) getApplicationContext().getSystemService(TELEPHONY_SERVICE);
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        this.stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        CURRENT_NUMBER = intent.getStringExtra(EXTRA_CURRENT_NUMBER);
        JOB_MODEL = (JobModel) intent.getSerializableExtra(EXTRA_JOB_MODEL);

        Log.d("CURERNT NUMBER", CURRENT_NUMBER + "<---------");
        Log.d("MODEL", String.valueOf(JOB_MODEL == null));

        return START_STICKY;
    }
}
