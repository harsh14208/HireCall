package app.com.thetechnocafe.hirecall.Features.Authentication.SignIn;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import app.com.thetechnocafe.hirecall.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gurleensethi on 17/04/17.
 */

public class EmailNotVerifiedDialogFragment extends DialogFragment {
    @BindView(R.id.ok_button)
    Button mOkButton;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.resend_email_button)
    Button mResendEmailButton;

    private OnDialogActionListener mListener;

    //Interface for callbacks
    public interface OnDialogActionListener {
        void resendVerificationEmail();
    }

    //Instance method
    public static EmailNotVerifiedDialogFragment getInstance() {
        return new EmailNotVerifiedDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_email_not_verfied, container, false);

        ButterKnife.bind(this, view);

        initViews();

        return view;
    }

    private void initViews() {
        mResendEmailButton.setOnClickListener(view -> {
            toggleViews(true);
            mListener.resendVerificationEmail();
        });

        mOkButton.setOnClickListener(view -> getDialog().dismiss());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().setCancelable(false);
    }

    public void addOnDialogActionListener(OnDialogActionListener listener) {
        mListener = listener;
    }

    //Toggle the states of the views depending
    //if the resend email is currently loading
    private void toggleViews(boolean isLoading) {
        if (isLoading) {
            mProgressBar.setVisibility(View.VISIBLE);
            mOkButton.setEnabled(false);
            mResendEmailButton.setEnabled(false);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mOkButton.setEnabled(true);
            mResendEmailButton.setEnabled(true);
        }
    }
}
