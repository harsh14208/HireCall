package app.com.thetechnocafe.hirecall.Features.Authentication.SignUp;

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

import app.com.thetechnocafe.hirecall.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gurleensethi on 17/04/17.
 */

public class VerifyEmailDialogFragment extends DialogFragment {

    @BindView(R.id.ok_button)
    Button mOkButton;

    private OnDialogActionListener mListener;

    //Interface for callbacks
    public interface OnDialogActionListener {
        void onOkButtonClicked();
    }

    //Instance method
    public static VerifyEmailDialogFragment getInstance() {
        return new VerifyEmailDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_verify_email, container, false);

        ButterKnife.bind(this, view);

        initViews();

        return view;
    }

    private void initViews() {
        mOkButton.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onOkButtonClicked();
            }
            getDialog().dismiss();
        });


    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        getDialog().setCancelable(false);
    }

    public void addOnDialogActionListener(OnDialogActionListener listener) {
        mListener = listener;
    }
}
