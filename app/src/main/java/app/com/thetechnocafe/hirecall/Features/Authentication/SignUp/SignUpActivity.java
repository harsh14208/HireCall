package app.com.thetechnocafe.hirecall.Features.Authentication.SignUp;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import app.com.thetechnocafe.hirecall.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpActivity extends AppCompatActivity implements SignUpContract.View {

    @BindView(R.id.name_text_input_layout)
    TextInputLayout mNameTextInputLayout;
    @BindView(R.id.name_text_input_edit_text)
    TextInputEditText mNameTextInputEditText;
    @BindView(R.id.email_text_input_layout)
    TextInputLayout mEmailTextInputLayout;
    @BindView(R.id.email_text_input_edit_text)
    TextInputEditText mEmailTextInputEditText;
    @BindView(R.id.password_text_input_layout)
    TextInputLayout mPasswordTextInputLayout;
    @BindView(R.id.password_text_input_edit_text)
    TextInputEditText mPasswordTextInputEditText;
    @BindView(R.id.confirm_password_text_input_layout)
    TextInputLayout mConfirmPasswordTextInputLayout;
    @BindView(R.id.confirm_password_text_input_edit_text)
    TextInputEditText mConfirmPasswordTextInputEditText;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.sign_up_button)
    Button mSignUpButton;
    @BindView(R.id.linear_layout)
    LinearLayout mLinearLayout;

    private static final String TAG_EMAIL_VERIFY_DIALOG = "dialog_tag";
    private SignUpContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mPresenter = new SignUpPresenter();
        mPresenter.subscribe(this);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        mSignUpButton.setOnClickListener(view -> {
            String name = mNameTextInputEditText.getText().toString();
            String email = mEmailTextInputEditText.getText().toString();
            String password = mPasswordTextInputEditText.getText().toString();
            String confirmPassword = mConfirmPasswordTextInputEditText.getText().toString();

            if (validateFields(name, email, password, confirmPassword)) {
                String domain = email.split("@")[1].split("\\.")[0];
                mPresenter.signUp(email, password, name, domain);
                TransitionManager.beginDelayedTransition(mLinearLayout);
                mProgressBar.setVisibility(View.VISIBLE);
                mSignUpButton.setVisibility(View.GONE);
                toggleViews(true);
            }
        });
    }

    private boolean validateFields(String name, String email, String password, String confirmPassword) {
        //Remove all errors
        mNameTextInputLayout.setErrorEnabled(false);
        mEmailTextInputLayout.setErrorEnabled(false);
        mPasswordTextInputLayout.setErrorEnabled(false);
        mConfirmPasswordTextInputLayout.setErrorEnabled(false);

        if (name.length() < 4) {
            mNameTextInputLayout.setError("Name is too short");
            return false;
        }

        if (email.length() < 6) {
            mEmailTextInputLayout.setError("Email is too short");
            return false;
        } else if (!email.contains("@") || !email.contains(".")) {
            mEmailTextInputLayout.setError("Email format wrong");
            return false;
        } else if (email.contains("gmail") || email.contains("yahoo")) {
            mEmailTextInputLayout.setError("Generic email providers not allowed. Please provide other email address");
            return false;
        }

        if (password.length() < 6) {
            mPasswordTextInputLayout.setError("Password is too short");
            return false;
        }

        if (confirmPassword.length() < 6) {
            mConfirmPasswordTextInputLayout.setError("Password is too short");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            mConfirmPasswordTextInputEditText.setError("Passwords don't match");
            return false;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unSubscribe();
    }

    @Override
    public Context getAppContext() {
        return this;
    }

    @Override
    public void onSignUpSuccessful() {
        TransitionManager.beginDelayedTransition(mLinearLayout);
        mProgressBar.setVisibility(View.GONE);
        mSignUpButton.setVisibility(View.VISIBLE);
        toggleViews(false);

        //Show the dialog to inform sign up success
        VerifyEmailDialogFragment fragment = VerifyEmailDialogFragment.getInstance();
        fragment.addOnDialogActionListener(this::finish);
        fragment.show(getSupportFragmentManager(), TAG_EMAIL_VERIFY_DIALOG);
    }

    @Override
    public void emailAlreadyExists() {
        TransitionManager.beginDelayedTransition(mLinearLayout);
        mProgressBar.setVisibility(View.GONE);
        mSignUpButton.setVisibility(View.VISIBLE);
        mEmailTextInputLayout.setError("Email already exists");
        toggleViews(false);
    }

    @Override
    public void onErrorOccurred() {
        Toast.makeText(this, "Error occurred while signing up", Toast.LENGTH_SHORT).show();
        mProgressBar.setVisibility(View.GONE);
        mSignUpButton.setVisibility(View.VISIBLE);
        toggleViews(false);
    }

    //Toggle the states of the views while logging in so that user cannot change the
    //values while sign up process is running
    private void toggleViews(boolean isLoggingIn) {
        if (isLoggingIn) {
            mNameTextInputEditText.setEnabled(false);
            mEmailTextInputEditText.setEnabled(false);
            mPasswordTextInputEditText.setEnabled(false);
            mConfirmPasswordTextInputEditText.setEnabled(false);
        } else {
            mNameTextInputEditText.setEnabled(true);
            mEmailTextInputEditText.setEnabled(true);
            mPasswordTextInputEditText.setEnabled(true);
            mConfirmPasswordTextInputEditText.setEnabled(true);
        }
    }
}
