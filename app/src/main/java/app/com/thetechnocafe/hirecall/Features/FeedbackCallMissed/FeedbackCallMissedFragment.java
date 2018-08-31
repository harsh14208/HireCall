package app.com.thetechnocafe.hirecall.Features.FeedbackCallMissed;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import app.com.thetechnocafe.hirecall.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
     * Created by gurleen on 22/4/17.
 */

public class FeedbackCallMissedFragment extends Fragment implements FeedbackCallMissedContract.View {

    @BindView(R.id.save_button)
    Button mSaveButton;

    private ProgressDialog mProgressDialog;
    private FeedbackCallMissedContract.Presenter mPresenter;

    //Instance method
    public static FeedbackCallMissedFragment getInstance() {
        return new FeedbackCallMissedFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback_call_missed, container, false);

        ButterKnife.bind(this, view);

        mPresenter = new FeedbackCallMissedPresenter();
        mPresenter.subscribe(this);

        initViews();

        return view;
    }

    private void initViews() {
        mSaveButton.setOnClickListener(view -> {
            ArrayList<String> feedbackList = new ArrayList<>();
            feedbackList.add("Didn't respond/picked the call");
            startProgressDialog();
            mPresenter.submitFeedback(feedbackList);
        });
    }

    private void startProgressDialog() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Saving Feedback...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    public Context getAppContext() {
        return getContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unSubscribe();
    }

    @Override
    public void closeFeedbackActivity() {
        if(mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        getActivity().finish();
    }
}
