package app.com.thetechnocafe.hirecall.Features.FeedbackAttended;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;

import app.com.thetechnocafe.hirecall.Features.FeedbackAttended.FeedbackAttendedFragments.FeedbackAttendedOptionsContract;
import app.com.thetechnocafe.hirecall.Features.FeedbackAttended.FeedbackAttendedFragments.InterviewConfirmationCallFragment;
import app.com.thetechnocafe.hirecall.Features.FeedbackAttended.FeedbackAttendedFragments.IntroductoryCallFragment;
import app.com.thetechnocafe.hirecall.Features.FeedbackAttended.FeedbackAttendedFragments.OfferCallFragment;
import app.com.thetechnocafe.hirecall.Features.FeedbackAttended.FeedbackAttendedFragments.PostInterviewCallFragment;
import app.com.thetechnocafe.hirecall.Features.FeedbackAttended.FeedbackAttendedFragments.PostOfferCallFragment;
import app.com.thetechnocafe.hirecall.Features.FeedbackAttended.FeedbackAttendedFragments.PreInterviewFollowUpCallFragment;
import app.com.thetechnocafe.hirecall.Features.FeedbackAttended.FeedbackAttendedFragments.ShortlistConfirmationCallFragment;
import app.com.thetechnocafe.hirecall.Models.TodoModel;
import app.com.thetechnocafe.hirecall.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gurleen on 22/4/17.
 */

public class FeedbackAttendedFragment extends Fragment implements FeedbackAttendedContract.View {

    @BindView(R.id.linear_layout)
    LinearLayout mLinearLayout;
    @BindView(R.id.purpose_spinner)
    Spinner mPurposeSpinner;
    @BindView(R.id.save_button)
    Button mSaveButton;

    private ProgressDialog mProgressDialog;
    private FeedbackAttendedContract.Presenter mPresenter;
    private String[] purposeSpinnerOptions;
    private Fragment mSelectedFragment;

    //Instance method
    public static FeedbackAttendedFragment getInstance() {
        return new FeedbackAttendedFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback_attend, container, false);

        ButterKnife.bind(this, view);

        mPresenter = new FeedbackAttendedPresenter();
        mPresenter.subscribe(this);

        purposeSpinnerOptions = getResources().getStringArray(R.array.call_purpose);

        initViews();

        return view;
    }

    private void initViews() {
        mSaveButton.setOnClickListener(view -> {
            FeedbackAttendedOptionsContract fragment = (FeedbackAttendedOptionsContract) getFragmentFromContainer();
            if (fragment != null) {
                TodoModel todo = fragment.getTodo();
                ArrayList<String> listOfFeedback = fragment.getListOfFeedbackOptions();
                if (listOfFeedback != null && listOfFeedback.size() > 0) {
                    String feedbackReason = purposeSpinnerOptions[mPurposeSpinner.getSelectedItemPosition()];
                    mPresenter.submitFeedback(feedbackReason, listOfFeedback, todo);
                    startProgressDialog();
                }
            }
        });

        //Set up spinner
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, purposeSpinnerOptions);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPurposeSpinner.setAdapter(arrayAdapter);
        mPurposeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TransitionManager.beginDelayedTransition(mLinearLayout);
                mSaveButton.setVisibility(View.VISIBLE);

                switch (purposeSpinnerOptions[position]) {
                    case "Introductory Call": {
                        mSelectedFragment = IntroductoryCallFragment.getInstance();
                        break;
                    }
                    case "Shortlist Confirmation Call": {
                        mSelectedFragment = ShortlistConfirmationCallFragment.getInstance();
                        break;
                    }
                    case "Interview Confirmation Call": {
                        mSelectedFragment = InterviewConfirmationCallFragment.getInstance();
                        break;
                    }
                    case "Pre Interview Follow Up Call": {
                        mSelectedFragment = PreInterviewFollowUpCallFragment.getInstance();
                        break;
                    }
                    case "Post Interview Call": {
                        mSelectedFragment = PostInterviewCallFragment.getInstance();
                        break;
                    }
                    case "Offer Call": {
                        mSelectedFragment = OfferCallFragment.getInstance();
                        break;
                    }
                    case "Post Offer Call": {
                        mSelectedFragment = PostOfferCallFragment.getInstance();
                        break;
                    }
                    default: {
                        mSaveButton.setVisibility(View.GONE);
                        if (mSelectedFragment != null) {
                            getChildFragmentManager().beginTransaction().remove(mSelectedFragment).commit();
                            mSelectedFragment = null;
                        }
                    }
                }

                if (mSelectedFragment != null) {
                    replaceFragment(mSelectedFragment);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, fragment);

        fragmentTransaction.commit();
    }

    private Fragment getFragmentFromContainer() {
        return getChildFragmentManager().findFragmentById(R.id.fragment_container);
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
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        getActivity().finish();
    }
}
