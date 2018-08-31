package app.com.thetechnocafe.hirecall.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by gurleensethi on 18/04/17.
 */

public class SharedPreferencesUtility {
    private static SharedPreferencesUtility sInstance;
    private static final String SP_FILE_NAME = "shared_preferencs";
    private static final String SP_IS_FIRST_JOB_CREATED = "first_job_created";
    private static final String SP_USER_NAME = "user_name";
    private static final String SP_EMAIL = "email";
    private static final String SP_DOMAIN = "domain";
    private static final String SP_IS_FEEDBACK_PENDING = "feedback_pending";
    private static final String SP_CLIENT_NAME = "client_name";
    private static final String SP_CALL_NUMBER = "call_number";

    //Singleton
    private SharedPreferencesUtility() {
    }

    public static SharedPreferencesUtility getInstance() {
        if (sInstance == null) {
            sInstance = new SharedPreferencesUtility();
        }
        return sInstance;
    }

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getSharedPreferencesEditor(Context context) {
        return context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE).edit();
    }

    //Update first job created status
    public void setFirstJobCreated(Context context, boolean firstJobCreated) {
        getSharedPreferencesEditor(context).putBoolean(SP_IS_FIRST_JOB_CREATED, firstJobCreated).commit();
    }

    //Get the first job created status
    public boolean getFirstJobCreated(Context context) {
        return getSharedPreferences(context).getBoolean(SP_IS_FIRST_JOB_CREATED, false);
    }

    //Update name
    public void setUserName(Context context, String name) {
        getSharedPreferencesEditor(context).putString(SP_USER_NAME, name).commit();
    }

    //Get the name
    public String getUserName(Context context) {
        return getSharedPreferences(context).getString(SP_USER_NAME, null);
    }

    //Update email
    public void setEmail(Context context, String email) {
        getSharedPreferencesEditor(context).putString(SP_EMAIL, email).commit();
    }

    //Get the email
    public String getEmail(Context context) {
        return getSharedPreferences(context).getString(SP_EMAIL, null);
    }

    //Update the domain
    public void setDomain(Context context, String domain) {
        getSharedPreferencesEditor(context).putString(SP_DOMAIN, domain).commit();
    }

    //Get the domain
    public String getDomain(Context context) {
        return getSharedPreferences(context).getString(SP_DOMAIN, null);
    }

    //Update the domain
    public void setCallNumber(Context context, String callNumber) {
        getSharedPreferencesEditor(context).putString(SP_CALL_NUMBER, callNumber).commit();
    }

    //Get the domain
    public String getCallNumber(Context context) {
        return getSharedPreferences(context).getString(SP_CALL_NUMBER, null);
    }

    //Update the domain
    public void setFeedbackPending(Context context, boolean feedbackPending) {
        getSharedPreferencesEditor(context).putBoolean(SP_IS_FEEDBACK_PENDING, feedbackPending).commit();
    }

    //Get the domain
    public boolean getFeedbackPending(Context context) {
        return getSharedPreferences(context).getBoolean(SP_IS_FEEDBACK_PENDING, false);
    }

    //Update the domain
    public void setClient(Context context, String client) {
        getSharedPreferencesEditor(context).putString(SP_CLIENT_NAME, client).commit();
    }

    //Get the domain
    public String getClient(Context context) {
        return getSharedPreferences(context).getString(SP_CLIENT_NAME, null);
    }
}
