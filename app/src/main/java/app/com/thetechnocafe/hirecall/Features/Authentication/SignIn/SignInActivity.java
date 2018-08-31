package app.com.thetechnocafe.hirecall.Features.Authentication.SignIn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import app.com.thetechnocafe.hirecall.Features.Authentication.SignUp.SignUpActivity;
import app.com.thetechnocafe.hirecall.Features.CreateJob.CreateJobActivity;
import app.com.thetechnocafe.hirecall.Features.Feedback.FeedbackActivity;
import app.com.thetechnocafe.hirecall.Features.Home.HomeActivity;
import app.com.thetechnocafe.hirecall.R;
import app.com.thetechnocafe.hirecall.Utilities.NotificationUtils;
import app.com.thetechnocafe.hirecall.app.Config;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SignInActivity extends AppCompatActivity implements SignInContract.View {

    @BindView(R.id.email_text_input_layout)
    TextInputLayout mEmailTextInputLayout;
    @BindView(R.id.email_text_input_edit_text)
    TextInputEditText mEmailTextInputEditText;
    @BindView(R.id.password_text_input_layout)
    TextInputLayout mPasswordTextInputLayout;
    @BindView(R.id.password_text_input_edit_text)
    TextInputEditText mPasswordTextInputEditText;
    @BindView(R.id.sign_up_button)
    Button mSignUpButton;
    @BindView(R.id.sign_in_button)
    Button mSignInButton;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    private static final String TAG_EMAIL_NOT_VERIFIED_DIALOG = "dialog_tag";
    private EmailNotVerifiedDialogFragment mEmailNotVerifiedDialogFragment;
    private SignInContract.Presenter mPresenter;
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("broadcast",intent.getAction());

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);


                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                }
            }
        };


        mPresenter = new SignInPresenter();
        mPresenter.subscribe(this);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        mSignInButton.setOnClickListener(view -> {
            String email = mEmailTextInputEditText.getText().toString();
            String password = mPasswordTextInputEditText.getText().toString();

            if (validateFields(email, password)) {
                mPresenter.singIn(email, password);
                toggleViews(true);
            }
        });

        mSignUpButton.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public Context getAppContext() {
        return this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
        mPresenter.checkLoggedInStatus();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unSubscribe();
    }

    @Override
    public void startHomeActivity() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoginSuccessful() {
        toggleViews(false);

        startHomeActivity();
    }

    @Override
    public void wrongCredentials() {
        toggleViews(false);
        Toast.makeText(getApplicationContext(), "Email or Password is wrong", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void emailNotVerified() {
        //Show the dialog fragment
        mEmailNotVerifiedDialogFragment = EmailNotVerifiedDialogFragment.getInstance();
        mEmailNotVerifiedDialogFragment.addOnDialogActionListener(() -> {
            String email = mEmailTextInputEditText.getText().toString();
            String password = mPasswordTextInputEditText.getText().toString();

            mPresenter.resendVerificationEmail(email, password);
        });
        mEmailNotVerifiedDialogFragment.show(getSupportFragmentManager(), TAG_EMAIL_NOT_VERIFIED_DIALOG);

        toggleViews(false);
    }

    @Override
    public void onVerificationMailSent() {
        //Hide the dialog
        if (mEmailNotVerifiedDialogFragment != null) {
            mEmailNotVerifiedDialogFragment.dismiss();
        }

        Toast.makeText(getApplicationContext(), "Verification email sent successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFirstJobNotCreated() {
        toggleViews(false);
        Intent intent = new Intent(getApplicationContext(), CreateJobActivity.class);
        intent.putExtra(CreateJobActivity.EXTRA_CREATE_FIRST_JOB, true);
        startActivity(intent);
        finish();
    }

    @Override
    public void startFeedbackActivity() {
        Intent intent = new Intent(getApplicationContext(), FeedbackActivity.class);
        startActivity(intent);
    }

    private boolean validateFields(String email, String password) {
        //Remove all errors
        mEmailTextInputLayout.setErrorEnabled(false);
        mPasswordTextInputLayout.setErrorEnabled(false);

        if (email.length() < 6) {
            mEmailTextInputLayout.setError("Email is too short");
            return false;
        }

        if (!email.contains("@") && !email.contains(".")) {
            mEmailTextInputLayout.setError("Email format is wrong");
            return false;
        }

        if (email.contains("gmail") || email.contains("yahoo")) {
            mEmailTextInputLayout.setError("Generic email providers not allowed. Please provide other email address");
            return false;
        }

        if (password.length() < 6) {
            mPasswordTextInputLayout.setError("Password is too short");
        }
        return true;
    }

    //Toggle the visibility of view during login
    //To prevent user from changing values while logging in
    private void toggleViews(boolean isLoggingIn) {
        if (isLoggingIn) {
            mProgressBar.setVisibility(View.VISIBLE);
            mSignInButton.setVisibility(View.GONE);
            mEmailTextInputEditText.setEnabled(false);
            mPasswordTextInputEditText.setEnabled(false);
            mSignUpButton.setEnabled(false);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mSignInButton.setVisibility(View.VISIBLE);
            mEmailTextInputEditText.setEnabled(true);
            mPasswordTextInputEditText.setEnabled(true);
            mSignUpButton.setEnabled(true);
        }
    }
}
