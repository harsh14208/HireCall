package app.com.thetechnocafe.hirecall.Features.CreateJob;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.com.thetechnocafe.hirecall.Features.Home.HomeActivity;
import app.com.thetechnocafe.hirecall.Models.JobModel;
import app.com.thetechnocafe.hirecall.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateJobActivity extends AppCompatActivity implements CreateJobContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.linear_layout)
    LinearLayout mLinearLayout;
    @BindView(R.id.client_name_auto_complete_text_view)
    AutoCompleteTextView mClientNameAutoCompleteTextView;
    @BindView(R.id.primary_skill_auto_complete_text_view)
    AutoCompleteTextView mPrimarySkillAutoCompleteTextView;
    @BindView(R.id.other_client_name_text_input_layout)
    TextInputLayout mClientNameTextInputLayout;
    @BindView(R.id.other_client_name_text_input_edit_text)
    TextInputEditText mClientNameTextInputEditText;
    @BindView(R.id.primary_skill_text_input_layout)
    TextInputLayout mPrimarySkillTextInputLayout;
    @BindView(R.id.primary_skill_text_input_edit_text)
    TextInputEditText mPrimarySkillTextInputEditText;
    @BindView(R.id.job_location_area_text_input_layout)
    TextInputLayout mJobLocationAreaTextInputLayout;
    @BindView(R.id.job_location_area_text_input_edit_text)
    TextInputEditText mJobLocationAreaTextInputEditText;
    @BindView(R.id.job_location_city_text_input_layout)
    TextInputLayout mJobLocationCityTextInputLayout;
    @BindView(R.id.job_location_city_text_input_edit_text)
    TextInputEditText mJobLocationCityTextInputEditText;
    @BindView(R.id.invite_colleague_text_input_layout)
    TextInputLayout mInviteColleagueTextInputLayout;
    @BindView(R.id.invite_colleague_text_input_edit_text)
    TextInputEditText mInviteColleagueTextInputEditText;
    @BindView(R.id.min_expense_text_input_layout)
    TextInputLayout mMinExpenseTextInputLayout;
    @BindView(R.id.min_expense_text_input_edit_text)
    TextInputEditText mMinExpenseTextInputEditText;
    @BindView(R.id.max_expense_text_input_layout)
    TextInputLayout mMaxExpenseTextInputLayout;
    @BindView(R.id.max_expense_text_input_edit_text)
    TextInputEditText mMaxExpenseTextInputEditText;
    @BindView(R.id.create_job_button)
    Button mCreateJobButton;
    @BindView(R.id.add_email_button)
    Button mAddEmailButton;
    @BindView(R.id.email_container_linear_layout)
    LinearLayout mEmailContainerLinearLayout;

    private static final int PLACES_AUTO_COMPLETE_CITY_REQUEST_CODE = 1;
    private static final int PLACES_AUTO_COMPLETE_SUB_LOCALITY_REQUEST_CODE = 2;
    public static final String EXTRA_CREATE_FIRST_JOB = "create_first_job";
    private CreateJobContract.Presenter mPresenter;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_job);

        ButterKnife.bind(this);

        mPresenter = new CreateJobPresenter();
        mPresenter.subscribe(this);

        initViews();
    }

    private void initViews() {
        //Set up the toolbar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            //Check if activity is called for first job
            //then change the title
            if (getIntent().getBooleanExtra(EXTRA_CREATE_FIRST_JOB, false)) {
                getSupportActionBar().setTitle("Create Your First Job");
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
            }
        }

        mClientNameAutoCompleteTextView.setThreshold(1);
        mPrimarySkillAutoCompleteTextView.setThreshold(1);

        //Add on focus change listeners to location texts,
        //so that user cannot add any junk values
        mJobLocationAreaTextInputEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mJobLocationAreaTextInputEditText.clearFocus();
                openPlacesAutoCompleteForSubLocality();
            }
        });

        mJobLocationCityTextInputEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mJobLocationCityTextInputEditText.clearFocus();
                openPlacesAutoCompleteForCity();
            }
        });

        mCreateJobButton.setOnClickListener(view -> {
            String clientName = mClientNameTextInputEditText.getText().toString();
            String primarySkill = mPrimarySkillTextInputEditText.getText().toString();
            String locationArea = mJobLocationAreaTextInputEditText.getText().toString();
            String locationCity = mJobLocationCityTextInputEditText.getText().toString();
            String minExpense = mMinExpenseTextInputEditText.getText().toString();
            String maxExpense = mMaxExpenseTextInputEditText.getText().toString();
            String autoCompleteClientName = mClientNameAutoCompleteTextView.getText().toString();
            String autoCompletePrimarySkill = mPrimarySkillAutoCompleteTextView.getText().toString();

            if (validateFields(clientName, primarySkill, locationArea, locationCity, minExpense, maxExpense, autoCompleteClientName, autoCompletePrimarySkill)) {
                JobModel job = new JobModel();
                //Check if any value is "Other" in the Auto Complete Text Views
                if (autoCompleteClientName.equals("Other")) {
                    job.setClientName(clientName);
                } else {
                    job.setClientName(autoCompleteClientName);
                }

                if (autoCompletePrimarySkill.equals("Other")) {
                    job.setPrimarySkill(primarySkill);
                } else {
                    job.setPrimarySkill(autoCompletePrimarySkill);
                }

                job.setSubLocation(locationArea);
                job.setCityLocation(locationCity);
                job.setMaxExpense(Integer.parseInt(maxExpense));
                job.setMinExpense(Integer.parseInt(minExpense));
                job.setTimeCreated(new Date().getTime());
                job.setArchived(false);

                List<String> list = new ArrayList<>();
                //Fetch all the entered email id's
                for (int i = 0; i < mEmailContainerLinearLayout.getChildCount(); i++) {
                    TextView textView = (TextView) mEmailContainerLinearLayout.getChildAt(i).findViewById(R.id.email_text_view);
                    list.add(textView.getText().toString());
                }
                job.setInvitedUserEmails(list);

                //Create job
                mPresenter.createNewJob(job, autoCompleteClientName.equals("Other"), autoCompletePrimarySkill.equals("Other"));

                //Show progress dialog
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Creating Job...");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }
        });

        //Set up the add email mechanism
        mAddEmailButton.setOnClickListener(view -> {
            mInviteColleagueTextInputLayout.setErrorEnabled(false);

            String email = mInviteColleagueTextInputEditText.getText().toString();
            if (checkForEmailValidity(email)) {
                //No more than 10 emails are permitted
                if (mEmailContainerLinearLayout.getChildCount() < 10) {
                    View emailView = getLayoutInflater().inflate(R.layout.item_add_email, mEmailContainerLinearLayout, false);

                    TextView emailText = (TextView) emailView.findViewById(R.id.email_text_view);
                    ImageView closeImage = (ImageView) emailView.findViewById(R.id.close_image_view);

                    emailText.setText(email);
                    closeImage.setOnClickListener(v -> {
                        TransitionManager.beginDelayedTransition(mEmailContainerLinearLayout);
                        mEmailContainerLinearLayout.removeView(emailView);
                    });

                    TransitionManager.beginDelayedTransition(mEmailContainerLinearLayout);
                    mEmailContainerLinearLayout.addView(emailView, 0);

                    mInviteColleagueTextInputEditText.setText("");
                    hideInputKeyboard();
                } else {
                    mInviteColleagueTextInputLayout.setError("Maximum email limit reached");
                }
            } else {
                mInviteColleagueTextInputLayout.setError("Email is not valid");
            }
        });

        //Add listeners to auto complete text to check of "Other" is not selected
        //if input is "Other" than show the edit text for new values
        mClientNameAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TransitionManager.beginDelayedTransition(mLinearLayout);
                if (s.toString().equals("Other")) {
                    mClientNameTextInputLayout.setVisibility(View.VISIBLE);
                } else {
                    mClientNameTextInputLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPrimarySkillAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TransitionManager.beginDelayedTransition(mLinearLayout);
                if (s.toString().equals("Other")) {
                    mPrimarySkillTextInputLayout.setVisibility(View.VISIBLE);
                } else {
                    mPrimarySkillTextInputLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Create and show progress dialog until values are not fetched
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Fetching Values. Please wait...");
        mProgressDialog.show();
    }

    private boolean checkForEmailValidity(String email) {
        if (email.length() < 6
                || !email.contains("@")
                || !email.contains(".")
                || email.contains("gmail")
                || email.contains("yahoo")) {
            //return false;
        }
        return true;
    }

    private void hideInputKeyboard() {
        View view = getCurrentFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean validateFields(String clientName, String primarySkill, String locationArea, String locationCity,
                                   String minExpense, String maxExpense, String autoCompleteNameClient, String autoCompletePrimarySkill) {
        //Remove all existing errors
        mClientNameTextInputLayout.setErrorEnabled(false);
        mJobLocationAreaTextInputLayout.setErrorEnabled(false);
        mJobLocationCityTextInputLayout.setErrorEnabled(false);
        mPrimarySkillTextInputLayout.setErrorEnabled(false);
        mMinExpenseTextInputLayout.setErrorEnabled(false);
        mMaxExpenseTextInputLayout.setErrorEnabled(false);

        if (autoCompleteNameClient.length() == 0) {
            mClientNameAutoCompleteTextView.setError("Client name cannot be empty");
            return false;
        }

        if (autoCompletePrimarySkill.length() == 0) {
            mPrimarySkillAutoCompleteTextView.setError("Primary Skill cannot be empty");
            return false;
        }

        //Check if other is present in autocomplete text's
        if (autoCompleteNameClient.equals("Other")) {
            if (clientName.length() < 4) {
                mClientNameTextInputLayout.setError("Name it too short");
                return false;
            }
        }

        if (autoCompletePrimarySkill.equals("Other")) {
            if (primarySkill.length() < 4) {
                mClientNameTextInputLayout.setError("Invalid Primary Skill");
                return false;
            }
        }

        if (locationArea.length() == 0) {
            mJobLocationAreaTextInputLayout.setError("Area is required");
            return false;
        }

        if (locationCity.length() == 0) {
            mJobLocationCityTextInputLayout.setError("City is required");
            return false;
        }

        if (minExpense.length() == 0) {
            mMinExpenseTextInputLayout.setError("Min expense is needed");
            return false;
        }

        if (maxExpense.length() == 0) {
            mMaxExpenseTextInputLayout.setError("Max expense is needed");
            return false;
        }
        return true;
    }

    @Override
    public Context getAppContext() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unSubscribe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        //Check if first job not created then use different menu
        if (getIntent().getBooleanExtra(EXTRA_CREATE_FIRST_JOB, false)) {
            menuInflater.inflate(R.menu.create_first_job_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
            case R.id.logout: {
                mPresenter.logoutUser();
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Open the PlaceAutoComplete
     * Apply the city filter to show only cities in the results
     */

    private void openPlacesAutoCompleteForCity() {
        try {
            //Create the City Filter
            AutocompleteFilter filter = new AutocompleteFilter.Builder()
                    .setCountry("IN")
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                    .build();
            //Create the places auto complete intent
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(filter)
                    .build(this);
            //Start the activity
            startActivityForResult(intent, PLACES_AUTO_COMPLETE_CITY_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the PlaceAutoComplete
     * Apply the region filter to show only regions in the result
     */
    private void openPlacesAutoCompleteForSubLocality() {
        try {
            //Create the City Filter
            AutocompleteFilter filter = new AutocompleteFilter.Builder()
                    .setCountry("IN")
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS)
                    .build();
            //Create the places auto complete intent
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(filter)
                    .build(this);
            //Start the activity
            startActivityForResult(intent, PLACES_AUTO_COMPLETE_SUB_LOCALITY_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PLACES_AUTO_COMPLETE_CITY_REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK) {
                    //Get the selected Place
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    //Get the Text from the place
                    String city = place.getName().toString();
                    //Change the City EditText's Text
                    mJobLocationCityTextInputEditText.setText(city);

                    //Try to convert the places address to geocoder address usning Geocoder
                    //Get the state and pin code from the address and set to corresponding editext's
                    try {
                        Geocoder geocoder = new Geocoder(this);
                        Address address = geocoder.getFromLocationName(place.getAddress().toString(), 1).get(0);
                        //Get the state name and set to the state edit text
                        String state = address.getAdminArea();
                        mJobLocationCityTextInputEditText.setText(city + ", " + state);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mJobLocationCityTextInputEditText.setText(city);
                    }
                }
                break;
            }
            case PLACES_AUTO_COMPLETE_SUB_LOCALITY_REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK) {
                    //Get the selected Place
                    Place place = PlaceAutocomplete.getPlace(this, data);
                    //Get the Text from the place
                    String subRegion = place.getName().toString();
                    //Change the City EditText's Text
                    mJobLocationAreaTextInputEditText.setText(subRegion);
                }
                break;
            }
        }
    }

    @Override
    public void onClientsReceived(List<String> clients) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, clients);
        mClientNameAutoCompleteTextView.setAdapter(adapter);
    }

    @Override
    public void onSkillsReceived(List<String> skills) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, skills);
        mPrimarySkillAutoCompleteTextView.setAdapter(adapter);
    }

    @Override
    public void stopProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void successCreatingJob() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        setResult(RESULT_OK);

        //Check if first job
        if (getIntent().getBooleanExtra(EXTRA_CREATE_FIRST_JOB, false)) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void jobCreationFailed() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        Toast.makeText(getApplicationContext(), "Error creating jobs", Toast.LENGTH_SHORT).show();
    }
}
