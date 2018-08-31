package app.com.thetechnocafe.hirecall.Features.CallsMasterDialANumber;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.Models.JobModel;
import app.com.thetechnocafe.hirecall.R;
import app.com.thetechnocafe.hirecall.Shared.Service.CallLogTrackerService;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by gurleensethi on 19/04/17.
 */

public class CallsMasterDialANumberFragment extends Fragment implements CallsMasterDialANumberContract.View {

    @BindView(R.id.phone_number_edit_text)
    EditText mPhoneNumberEditText;
    @BindView(R.id.search_image_view)
    ImageView mSearchImageView;
    @BindView(R.id.previous_records_recycler_view)
    RecyclerView mPreviousJobsDetailRecyclerView;
    @BindView(R.id.number_details_relative_layout)
    RelativeLayout mNumberDetailsRelativeLayout;
    @BindView(R.id.search_progress_bar)
    ProgressBar mSearchProgressBar;
    @BindView(R.id.phone_text_view)
    TextView mPhoneTextView;
    @BindView(R.id.dial_message_text_view)
    TextView mDialMessageTextView;
    @BindView(R.id.call_image_view)
    ImageView mCallImageView;
    @BindView(R.id.linear_layout)
    LinearLayout mLinearLayout;
    @BindView(R.id.save_image_view)
    ImageView mSaveImageView;
    @BindView(R.id.jobs_spinner)
    Spinner mJobsSpinner;

    public interface JobInteractor {
        JobModel getJobModel();

        void setJobModel(JobModel jobModel);
    }

    private static String CURRENT_NUMBER;
    private static final String ARGS_JOB_MODEL = "job_model";
    private static final int RC_REQUEST_PERMISSION = 1;
    private CallsMasterDialANumberContract.Presenter mPresenter;
    private CallLogsRecyclerAdapter mCallLogsRecyclerAdapter;
    private List<JobModel> mJobsList;
    private JobModel SELECTED_JOB;
    private boolean SHOULD_RECORD_CALL = false;
    private JobInteractor mJobInteractor;

    //Instance method
    public static CallsMasterDialANumberFragment getInstance() {
        return new CallsMasterDialANumberFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mJobInteractor = (JobInteractor) getParentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calls_master_dial_a_number, container, false);

        ButterKnife.bind(this, view);

        mPresenter = new CallsMasterDialANumberPresenter();
        mPresenter.subscribe(this);

        initViews();

        return view;
    }

    private void initViews() {
        mPreviousJobsDetailRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mSearchImageView.setOnClickListener(view -> {
            mSaveImageView.setVisibility(View.VISIBLE);

            //Check if an appropriate job is selected
            if (mJobsSpinner.getSelectedItemPosition() == 0) {
                Snackbar.make(mLinearLayout, "Please select a job", Snackbar.LENGTH_SHORT).show();
                return;
            }

            //Hide the relative layout
            mNumberDetailsRelativeLayout.setVisibility(View.GONE);

            String number = mPhoneNumberEditText.getText().toString();
            CURRENT_NUMBER = number;

            hideInputKeyboard();

            if (validateNumber(number)) {
                //Set the number to the phone text
                mPhoneTextView.setText(number);

                //Get the client name
                String clientName = SELECTED_JOB.getClientName();
                String jobID = SELECTED_JOB.getJobID();

                mPresenter.searchForNumber(number, clientName, jobID);
                toggleViews(true);
            }
        });

        mCallImageView.setOnClickListener(view -> {
            if (checkForCallPermissions()) {
                startCallLogService();
                SHOULD_RECORD_CALL = true;
                makeCall();
            }
        });

        mSaveImageView.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, mPhoneTextView.getText().toString());
            startActivity(intent);
        });

        mJobsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mJobsList != null && position != 0) {
                    SELECTED_JOB = mJobsList.get(position - 1);

                    //Send the job to interactor
                    if (mJobInteractor != null) {   
                        mJobInteractor.setJobModel(SELECTED_JOB);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mPhoneNumberEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideInputKeyboard();
            }
        });
    }

    private boolean validateNumber(String number) {
        if (number.length() < 6) {
            Snackbar.make(mLinearLayout, "Please enter a valid number", Snackbar.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean checkForCallPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG}, RC_REQUEST_PERMISSION);
                return false;
            } else if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG}, RC_REQUEST_PERMISSION);
                return false;
            }
        }

        return true;
    }

    private void makeCall() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + CURRENT_NUMBER));
        startActivity(intent);
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

    private void hideInputKeyboard() {
        View view = getActivity().getCurrentFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(INPUT_METHOD_SERVICE);
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case RC_REQUEST_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeCall();
                } else {
                    checkForCallPermissions();
                }
                break;
            }
        }
    }

    @Override
    public void onCallLogsFetched(List<List<CallLogModel>> callLogs, String domain) {
        toggleViews(false);
        if (callLogs.size() == 0) {
            mDialMessageTextView.setText("Does not have any previous records");
        } else {
            mDialMessageTextView.setText("Has Previous Records");
        }

        setUpOrRefreshRecyclerView(callLogs, domain);
    }

    @Override
    public void onJobListFetched(List<JobModel> jobsList) {
        mJobsList = jobsList;

        List<String> jobNames = new ArrayList<>();
        jobNames.add("Select a job");

        //Extract the job name and client from list
        for (JobModel job : jobsList) {
            jobNames.add(job.getPrimarySkill() + " - " + job.getClientName());
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, jobNames);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mJobsSpinner.setAdapter(arrayAdapter);
    }

    @Override
    public void changeNumberToName(String displayName) {
        mSaveImageView.setVisibility(View.GONE);
        mPhoneTextView.setText(displayName);
    }

    private void toggleViews(boolean isSearching) {
        if (isSearching) {
            mNumberDetailsRelativeLayout.setVisibility(View.GONE);
            mSearchProgressBar.setVisibility(View.VISIBLE);
            mSearchImageView.setVisibility(View.GONE);
            mPreviousJobsDetailRecyclerView.setVisibility(View.GONE);
        } else {
            mNumberDetailsRelativeLayout.setVisibility(View.VISIBLE);
            mSearchProgressBar.setVisibility(View.GONE);
            mSearchImageView.setVisibility(View.VISIBLE);
            mPreviousJobsDetailRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void setUpOrRefreshRecyclerView(List<List<CallLogModel>> callLogs, String domain) {
        String client = SELECTED_JOB.getClientName();
        mCallLogsRecyclerAdapter = new CallLogsRecyclerAdapter(getContext(), callLogs, domain, client);
        mPreviousJobsDetailRecyclerView.setAdapter(mCallLogsRecyclerAdapter);
    }
}
