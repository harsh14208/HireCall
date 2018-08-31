package app.com.thetechnocafe.hirecall.Features.JobDetailsCallHistory;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.Models.JobModel;
import app.com.thetechnocafe.hirecall.R;
import app.com.thetechnocafe.hirecall.Shared.Service.CallLogTrackerService;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gurleensethi on 19/04/17.
 */

public class JobDetailsCallHistoryFragment extends Fragment implements JobDetailsCallHistoryContract.View {

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

    private static final String ARGS_JOB_MODEL = "job_model";
    private static JobModel JOB_MODEL;
    private static String CURRENT_NUMBER;
    public List<CallLogModel> mcallLog= new ArrayList<CallLogModel>();

    private JobDetailsCallHistoryContract.Presenter mPresenter;

    //Instance method
    public static JobDetailsCallHistoryFragment getInstance(JobModel jobModel) {
        //Create args
        Bundle args = new Bundle();
        args.putSerializable(ARGS_JOB_MODEL, jobModel);

        JobDetailsCallHistoryFragment fragment = new JobDetailsCallHistoryFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_job_details_call_history, container, false);

        ButterKnife.bind(this, view);

        mPresenter = new JobDetailsCallHistoryPresenter();
        mPresenter.subscribe(this);

        initViews();

        JOB_MODEL = (JobModel) getArguments().getSerializable(ARGS_JOB_MODEL);
        mPresenter.fetchCallLogsForJob(JOB_MODEL.getJobID());

        return view;
    }

    private void initViews() {
        mCallHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mPresenter.fetchCallLogsForJob(JOB_MODEL.getJobID());
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
        CallLogsRecyclerAdapter adapter = new CallLogsRecyclerAdapter(getContext(), getActivity().getFragmentManager(), callLogs);
            adapter.setCompleteCallLogList(mcallLog);
        mCallHistoryRecyclerView.setAdapter(adapter);
        adapter.addCallActionListener(phoneNumber -> {
            CURRENT_NUMBER = phoneNumber;
            startCallLogService();

            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        });
    }
    public void setCallLogs(List<CallLogModel> callLog){
        mcallLog=callLog;
    }

    private void startCallLogService() {
        Intent intent = new Intent(getContext(), CallLogTrackerService.class);
        intent.putExtra(CallLogTrackerService.EXTRA_JOB_MODEL, getArguments().getSerializable(ARGS_JOB_MODEL));
        intent.putExtra(CallLogTrackerService.EXTRA_CURRENT_NUMBER, CURRENT_NUMBER);
        getActivity().startService(intent);
    }

    private void stopCallService() {
        Intent intent = new Intent(getContext(), CallLogTrackerService.class);
        intent.putExtra(CallLogTrackerService.EXTRA_JOB_MODEL, getArguments().getSerializable(ARGS_JOB_MODEL));
        intent.putExtra(CallLogTrackerService.EXTRA_CURRENT_NUMBER, CURRENT_NUMBER);
        getActivity().stopService(intent);
    }
}
