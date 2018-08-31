package app.com.thetechnocafe.hirecall.Features.CallsMasterCallHistory;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import app.com.thetechnocafe.hirecall.Features.CallsMasterDialANumber.CallsMasterDialANumberFragment;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.Models.JobModel;
import app.com.thetechnocafe.hirecall.R;
import app.com.thetechnocafe.hirecall.Shared.Service.CallLogTrackerService;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gurleensethi on 19/04/17.
 */

public class CallsMasterCallHistoryFragment extends Fragment implements CallsMasterCallHistoryContract.View {

    @BindView(R.id.linear_layout)
    LinearLayout mLinearLayout;
    @BindView(R.id.call_history_recycler_view)
    RecyclerView mCallHistoryRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.no_call_history_text_view)
    TextView mNoCallHistoryTextView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.search_edit_text)
    EditText mSearchEditText;

    private CallsMasterCallHistoryContract.Presenter mPresenter;
    private CallLogsRecyclerAdapter mCallLogsRecyclerAdapter;
    private CallsMasterDialANumberFragment.JobInteractor mJobInteractor;
    private static String CURRENT_NUMBER;
    private static JobModel SELECTED_JOB;

    //Instance method
    public static CallsMasterCallHistoryFragment getInstance() {
        return new CallsMasterCallHistoryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mJobInteractor = (CallsMasterDialANumberFragment.JobInteractor) getParentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_master_call_history, container, false);

        ButterKnife.bind(this, view);

        mPresenter = new CallsMasterCallHistoryPresenter();
        mPresenter.subscribe(this);

        initViews();

        mPresenter.fetchCallLogsForJob();

        return view;
    }

    private void initViews() {
        mCallHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mPresenter.fetchCallLogsForJob();
            mSearchEditText.setText("");
        });

        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mCallLogsRecyclerAdapter != null) {
                    mCallLogsRecyclerAdapter.filterListForKeyword(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public Context getAppContext() {
        return getContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unSubscribe();
        stopCallService();
    }

    @Override
    public void onCallLogsFetched(List<CallLogModel> callLogs) {
        TransitionManager.beginDelayedTransition(mLinearLayout);
        mSwipeRefreshLayout.setRefreshing(false);

        mProgressBar.setVisibility(View.GONE);

        if (callLogs.size() == 0) {
            mNoCallHistoryTextView.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.GONE);
        } else {
            mNoCallHistoryTextView.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
            setUpOrRefreshRecyclerView(callLogs);
        }
    }

    private void setUpOrRefreshRecyclerView(List<CallLogModel> callLogs) {
        mCallLogsRecyclerAdapter = new CallLogsRecyclerAdapter(getContext(), getActivity().getFragmentManager(), callLogs);
        mCallHistoryRecyclerView.setAdapter(mCallLogsRecyclerAdapter);
        mCallLogsRecyclerAdapter.addCallActionListener(phoneNumber -> {
            //Check if a job is selected in the spinner in dial a number fragment
            if (mJobInteractor != null && mJobInteractor.getJobModel() != null) {
                CURRENT_NUMBER = phoneNumber;
                SELECTED_JOB = mJobInteractor.getJobModel();
                startCallLogService();

                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(intent);
            } else {
                Snackbar.make(mLinearLayout, "Please select a job before calling", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void startCallLogService() {
        Intent intent = new Intent(getContext(), CallLogTrackerService.class);
        intent.putExtra(CallLogTrackerService.EXTRA_JOB_MODEL, SELECTED_JOB);
        intent.putExtra(CallLogTrackerService.EXTRA_CURRENT_NUMBER, CURRENT_NUMBER);
        getActivity().startService(intent);
    }

    private void stopCallService() {
        Intent intent = new Intent(getContext(), CallLogTrackerService.class);
        intent.putExtra(CallLogTrackerService.EXTRA_JOB_MODEL, SELECTED_JOB);
        intent.putExtra(CallLogTrackerService.EXTRA_CURRENT_NUMBER, CURRENT_NUMBER);
        getActivity().stopService(intent);
    }
}
