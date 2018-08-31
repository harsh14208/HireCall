package app.com.thetechnocafe.hirecall.Features.Chat.ChatUserList;

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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import app.com.thetechnocafe.hirecall.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gurleen on 8/5/17.
 */

public class AddNewChatDialogFragment extends DialogFragment {

    @BindView(R.id.add_button)
    Button mAddButton;
    @BindView(R.id.cancel_button)
    Button mCancelButton;
    @BindView(R.id.email_edit_text)
    EditText mEmailEditText;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    private OnDialogActionListener mListener;

    //Interface for callbacks
    public interface OnDialogActionListener {
        void onAddButtonClicked(String email);
    }

    //Instance method
    public static AddNewChatDialogFragment getInstance() {
        return new AddNewChatDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_new_chat, container, false);

        ButterKnife.bind(this, view);

        initViews();

        return view;
    }

    private void initViews() {
        mAddButton.setOnClickListener(view -> {
            if (mEmailEditText.getText().length() == 0) {
                Toast.makeText(getContext(), "Please enter an email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (mListener != null) {
                mListener.onAddButtonClicked(mEmailEditText.getText().toString());
                mProgressBar.setVisibility(View.VISIBLE);
                mAddButton.setEnabled(false);
                mCancelButton.setEnabled(false);
            }
        });

        mCancelButton.setOnClickListener(view -> {
            dismiss();
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

    public void isEmailIdValid(boolean isValid) {
        mProgressBar.setVisibility(View.GONE);
        mAddButton.setEnabled(true);
        mCancelButton.setEnabled(true);

        if (isValid) {
            dismiss();
        } else {
            Toast.makeText(getContext(), "User with email doesn't exists", Toast.LENGTH_SHORT).show();
        }
    }
}
