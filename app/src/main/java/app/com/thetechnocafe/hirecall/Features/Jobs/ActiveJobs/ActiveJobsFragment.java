package app.com.thetechnocafe.hirecall.Features.Jobs.ActiveJobs;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import app.com.thetechnocafe.hirecall.Features.CreateJob.CreateJobActivity;
import app.com.thetechnocafe.hirecall.Features.JobDetail.JobDetailActivity;
import app.com.thetechnocafe.hirecall.Models.JobModel;
import app.com.thetechnocafe.hirecall.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gurleensethi on 19/04/17.
 */

public class ActiveJobsFragment extends Fragment implements ActiveJobsContract.View {

    @BindView(R.id.select_type_spinner)
    Spinner mSelectedTypeSpinner;
    @BindView(R.id.no_jobs_text_view)
    TextView mNoJobsTextView;
    @BindView(R.id.jobs_recycler_view)
    RecyclerView mJobsRecyclerView;
    @BindView(R.id.create_job_floating_action_button)
    FloatingActionButton mCreateJobFloatingActionButton;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private static final int RC_CREATE_NEW_JOB = 1;
    private static final int RC_REQUEST_PERMISSION = 1;

    private ProgressDialog mProgressDialog;
    private JobsRecyclerAdapter mJobsRecyclerAdapter;
    private ActiveJobsContract.Presenter mPresenter;
    private static String[] JOB_TYPE_OPTIONS;

    //Instance method
    public static ActiveJobsFragment getInstance() {
        return new ActiveJobsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_jobs, container, false);

        ButterKnife.bind(this, view);

        JOB_TYPE_OPTIONS = getResources().getStringArray(R.array.job_type_options);

        mPresenter = new ActiveJobsPresenter();
        mPresenter.subscribe(this);

        initViews();

        return view;
    }

    private void initViews() {
        //Set up spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, JOB_TYPE_OPTIONS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSelectedTypeSpinner.setAdapter(adapter);
        mSelectedTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mNoJobsTextView.setVisibility(View.GONE);
                mJobsRecyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                loadRequiredJobs();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loadRequiredJobs();
            }
        });

        mJobsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mJobsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    mCreateJobFloatingActionButton.hide();
                } else if (dy < 0) {
                    mCreateJobFloatingActionButton.show();
                }
            }
        });

        mCreateJobFloatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), CreateJobActivity.class);
            startActivityForResult(intent, RC_CREATE_NEW_JOB);
        });

        mSwipeRefreshLayout.setOnRefreshListener(this::loadRequiredJobs);
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
    public void onJobsLoaded(List<JobModel> jobsList) {
        mSwipeRefreshLayout.setRefreshing(false);
        setUpOrRefreshCreatedJobsRecyclerView(jobsList);
    }

    @Override
    public void onJobArchived() {
        loadRequiredJobs();
        stopProgressDialog();
    }

    private void loadRequiredJobs() {
        switch (JOB_TYPE_OPTIONS[mSelectedTypeSpinner.getSelectedItemPosition()]) {
            case "All": {
                mPresenter.loadAllJobs();
                break;
            }
            case "Created Jobs": {
                mPresenter.loadCreatedJobs();
                break;
            }
            case "Invited Jobs": {
                mPresenter.loadInvitedJobs();
                break;
            }
        }
    }

    private void setUpOrRefreshCreatedJobsRecyclerView(List<JobModel> createdJobsList) {
        mProgressBar.setVisibility(View.GONE);
        if (createdJobsList.size() == 0) {
            mNoJobsTextView.setVisibility(View.VISIBLE);
            mJobsRecyclerView.setVisibility(View.GONE);
        } else {
            mNoJobsTextView.setVisibility(View.GONE);
            mJobsRecyclerView.setVisibility(View.VISIBLE);
            mJobsRecyclerAdapter = new JobsRecyclerAdapter(getContext(), createdJobsList);
            mJobsRecyclerView.setAdapter(mJobsRecyclerAdapter);
            mJobsRecyclerAdapter.addOnJobSelectedListener(new JobsRecyclerAdapter.OnJobSelectedListener() {
                @Override
                public void onClick(JobModel job) {
                    if (checkForCallPermissions()) {
                        Intent intent = new Intent(getContext(), JobDetailActivity.class);
                        intent.putExtra(JobDetailActivity.EXTRA_JOB_MODEL, job);
                        startActivity(intent);
                    }
                }

                @Override
                public void onArchiveClicked(JobModel job) {
                    startProgressDialog("Archiving Job. Please wait...");
                    mPresenter.onArchiveClicked(job);
                }
            });
        }
    }

    private boolean checkForCallPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CALL_PHONE, android.Manifest.permission.READ_CALL_LOG}, RC_REQUEST_PERMISSION);
                return false;
            } else if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CALL_PHONE, android.Manifest.permission.READ_CALL_LOG}, RC_REQUEST_PERMISSION);
                return false;
            }
        }

        return true;
    }

    private void startProgressDialog(String message) {
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(message);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void stopProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_CREATE_NEW_JOB: {
                if (resultCode == Activity.RESULT_OK) {
                    mPresenter.reloadJobs();
                }
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case RC_REQUEST_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    checkForCallPermissions();
                }
                break;
            }
        }
    }
}
