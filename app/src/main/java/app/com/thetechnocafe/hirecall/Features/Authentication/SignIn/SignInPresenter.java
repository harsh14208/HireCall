package app.com.thetechnocafe.hirecall.Features.Authentication.SignIn;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.HashMap;

import app.com.thetechnocafe.hirecall.Data.FirebaseAuthDB;
import app.com.thetechnocafe.hirecall.Models.UserModel;
import app.com.thetechnocafe.hirecall.Utilities.AlarmSchedulerUtility;
import app.com.thetechnocafe.hirecall.Utilities.SharedPreferencesUtility;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by gurleensethi on 17/04/17.
 */

public class SignInPresenter implements SignInContract.Presenter {

    private SignInContract.View mView;
    private CompositeDisposable mCompositeDisposable;
    private FirebaseDatabase mFirebaseDatabase;


    @Override
    public void subscribe(SignInContract.View view) {
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
    public void singIn(String email, String password) {
        Disposable disposable = FirebaseAuthDB.getInstance()
                .signInUser(email, password)
                .subscribe(firebaseSignInResult -> {
                    switch (firebaseSignInResult) {
                        case WRONG_CREDENTIALS: {
                            mView.wrongCredentials();
                            break;
                        }
                        case NOT_VERIFIED: {
                            mView.emailNotVerified();
                            break;
                        }
                        case SUCCESSFUL: {
                            Log.e("aaa agaya","ooo mai");

                            String uid = FirebaseAuthDB.getInstance().getSignedInUserUID();
                            RequestQueue queue = Volley.newRequestQueue(mView.getAppContext());

                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("number",uid);
                            JsonObjectRequest jsonReq = new JsonObjectRequest("http://13.71.116.40:4040/getUser.php", new JSONObject(params),
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            Log.d(TAG, "Response: " + response.toString());
                                            // Log.e("Response",response.toString());
                                            UserModel user= new UserModel();
                                            try {
                                                user.setDomain(response.get("domain").toString());
                                                user.setEmail(response.get("email").toString());
                                                user.setName(response.get("name").toString());


                                                SharedPreferencesUtility.getInstance().setUserName(mView.getAppContext(), user.getName());
                                                SharedPreferencesUtility.getInstance().setEmail(mView.getAppContext(), user.getEmail());
                                                SharedPreferencesUtility.getInstance().setFirstJobCreated(mView.getAppContext(), true);
                                                SharedPreferencesUtility.getInstance().setDomain(mView.getAppContext(), user.getDomain());
                                                AlarmSchedulerUtility.getInstance().scheduleRepeatedAlarms(mView.getAppContext());
                                                mView.onLoginSuccessful();


                                            }
                                            catch (Exception e){
                                                Log.e("Exception", e.toString());
                                            }
                                        }
                                    }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(TAG, "Error: " + error.getMessage());
                                }
                            });
                            queue.add(jsonReq);
                            break;

                     /*
                            FirebaseDB.getInstance()
                                    .getUser()
                                    .subscribe(userModel -> {


                                        SharedPreferencesUtility.getInstance().setUserName(mView.getAppContext(), userModel.getName());
                                        SharedPreferencesUtility.getInstance().setEmail(mView.getAppContext(), userModel.getEmail());
                                        SharedPreferencesUtility.getInstance().setFirstJobCreated(mView.getAppContext(), true);
                                        SharedPreferencesUtility.getInstance().setDomain(mView.getAppContext(), userModel.getDomain());
                                        AlarmSchedulerUtility.getInstance().scheduleRepeatedAlarms(mView.getAppContext());
                                        mView.onLoginSuccessful();


                                        SharedPreferences pref = mView.getAppContext().getSharedPreferences(Config.SHARED_PREF, 0);
                                        String regId = pref.getString("regId", null);
                                        String uid = FirebaseAuthDB.getInstance().getSignedInUserUID();

                                        HashMap<String, String> params = new HashMap<String, String>();
                                        params.put("number",uid);
                                        params.put("regId",regId);
                                        JsonObjectRequest jsonReq = new JsonObjectRequest("http://13.71.116.40:4040/addFirebaseRegId.php", new JSONObject(params),
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        Log.d(TAG, "Response: " + response.toString());
                                                        Log.e("Response",response.toString());

                                                    }
                                                }, new Response.ErrorListener() {

                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.d(TAG, "Error: " + error.getMessage());
                                            }
                                        });
                                        AppController.getInstance().addToRequestQueue(jsonReq);

                                    });
                            break;
                   */     }
                        case FIRST_JOB_NOT_CREATED: {

                            String uid = FirebaseAuthDB.getInstance().getSignedInUserUID();
                            RequestQueue queue = Volley.newRequestQueue(mView.getAppContext());

                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("number",uid);
                            JsonObjectRequest jsonReq = new JsonObjectRequest("http://13.71.116.40:4040/getUser.php", new JSONObject(params),
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            Log.d(TAG, "Response: " + response.toString());
                                            // Log.e("Response",response.toString());
                                            UserModel user= new UserModel();
                                            try {
                                                user.setDomain(response.get("domain").toString());
                                                user.setEmail(response.get("email").toString());
                                                user.setName(response.get("name").toString());


                                                SharedPreferencesUtility.getInstance().setUserName(mView.getAppContext(), user.getName());
                                                SharedPreferencesUtility.getInstance().setEmail(mView.getAppContext(), user.getEmail());
                                                SharedPreferencesUtility.getInstance().setDomain(mView.getAppContext(), user.getDomain());
                                                mView.onFirstJobNotCreated();


                                            }
                                            catch (Exception e){
                                                Log.e("Exception", e.toString());
                                            }
                                        }
                                    }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d(TAG, "Error: " + error.getMessage());
                                }
                            });
                            queue.add(jsonReq);


                            break;
                        }
                    }
                });

        mCompositeDisposable.add(disposable);
    }

    @Override
    public void checkLoggedInStatus() {
        if (FirebaseAuthDB.getInstance().isUserLoggedIn()) {
            //If first job is not created then start first job activity
            //else move to home activity
            if (!SharedPreferencesUtility.getInstance().getFirstJobCreated(mView.getAppContext())) {
                mView.onFirstJobNotCreated();
            } else if (SharedPreferencesUtility.getInstance().getFeedbackPending(mView.getAppContext())) {
                mView.startFeedbackActivity();
            } else {
                mView.startHomeActivity();
            }
        }

    }

    @Override
    public void resendVerificationEmail(String email, String password) {
        Disposable disposable = FirebaseAuthDB.getInstance()
                .resendVerificationEmail(email, password)
                .subscribe(result -> {
                    mView.onVerificationMailSent();
                });

        mCompositeDisposable.add(disposable);
    }
}
