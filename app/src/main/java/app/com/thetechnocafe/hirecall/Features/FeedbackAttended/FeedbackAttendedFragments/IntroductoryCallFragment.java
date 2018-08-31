package app.com.thetechnocafe.hirecall.Features.FeedbackAttended.FeedbackAttendedFragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import app.com.thetechnocafe.hirecall.Enums.TodoType;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.Models.TodoModel;
import app.com.thetechnocafe.hirecall.R;
import app.com.thetechnocafe.hirecall.Utilities.Constants;
import app.com.thetechnocafe.hirecall.Utilities.DateFormatUtility;
import app.com.thetechnocafe.hirecall.Utilities.PhoneCallContactUtility;
import app.com.thetechnocafe.hirecall.Utilities.SharedPreferencesUtility;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.widget.AdapterView.*;


/**
 * Created by gurleen on 22/4/17.
 */

public class IntroductoryCallFragment extends Fragment implements FeedbackAttendedOptionsContract {

    @BindView(R.id.imageView)
    ImageView emoji_image_view;
    @BindView(R.id.imageView2)
    ImageView emoji_image_view2;
    @BindView(R.id.linear_layout)
    LinearLayout mLinearLayout;
    @BindView(R.id.radio_group)
    RadioGroup mRadioGroup;
    @BindView(R.id.add_comments_interested_edit_text)
    EditText mAddCommentsEditText;
    @BindView(R.id.not_interested_spinner)
    Spinner mNotInterestedSpinner;
    @BindView(R.id.preference_spinner)
    Spinner mPreferenceSpinner;
    @BindView(R.id.reasons_edit_text)
    EditText mReasonsEditText;
    @BindView(R.id.time_picker_layout)
    LinearLayout mTimePickerLayout;
    @BindView(R.id.time_picker_button)
    Button mTimePickerButton;
    @BindView(R.id.date_picker_button)
    Button mDatePickerButton;
    @BindView(R.id.time_picker_layout2)
    LinearLayout mTimePickerLayout2;
    @BindView(R.id.time_picker_button2)
    Button mTimePickerButton2;
    @BindView(R.id.date_picker_button2)
    Button mDatePickerButton2;
    //@BindView(R.id.city_text_input_candidate_name)
    //TextInputLayout mCandidateNameTextInputLayout;
    //@BindView(R.id.city_text_input_edit_text_candidate_name)
    //TextInputEditText mCandidateNameInputEditText;
    @BindView(R.id.city_text_input_layout)
    TextInputLayout mCityTextInputLayout;
    @BindView(R.id.city_text_input_edit_text)
    TextInputEditText mCityTextInputEditText;
    @BindView(R.id.city_text_input_layout_experience)
    TextInputLayout mCityTextInputLayout_experience;
    @BindView(R.id.city_text_input_edit_text_experience)
    TextInputEditText mCityTextInputEditText_experience;
    @BindView(R.id.attitude_rating_edit_text)
    EditText mAttitudeRatingEditText;
    @BindView(R.id.interest_rating_edit_text)
    EditText mInterestRatingEditText;
    @BindView(R.id.cb_feedback_notice_period)
    CheckBox cbFeedBackNoticePeriod;

    //@BindView(R.id.candidate_attitude_rating)
    public RatingBar mcandidate_attitude_rating;
    // @BindView(R.id.candidate_interest_rating)
    public RatingBar mcandidate_interest_rating;


    private static final int PLACES_AUTO_COMPLETE_CITY_REQUEST_CODE = 1;
    private static Calendar SELECTED_DATE = Calendar.getInstance();
    private static String[] NOT_INTERESTED_OPTIONS;
    private static String[] DOMAIN_OPTIONS;
    private static String[] SKILL_OPTIONS;
    private static String[] ROLE_OPTIONS;
    private static String[] COMPANY_OPTIONS;
    private static CallLogModel CALL_LOG;
    public View v;
    public static String attitude = "", interest = "";
    public static int val, ch = 0;


    //Instance method
    public static IntroductoryCallFragment getInstance() {
        return new IntroductoryCallFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_introductory_call, container, false);
        v = view;
        ButterKnife.bind(this, view);

        NOT_INTERESTED_OPTIONS = getResources().getStringArray(R.array.not_interested_options);
        DOMAIN_OPTIONS = getResources().getStringArray(R.array.domain);
        SKILL_OPTIONS = getResources().getStringArray(R.array.skill);
        ROLE_OPTIONS = getResources().getStringArray(R.array.roles);
        COMPANY_OPTIONS = getResources().getStringArray(R.array.company);
        initViews();
        retrieveCallLog();
        mcandidate_interest_rating = (RatingBar) v.findViewById(R.id.candidate_interest_rating);
        mcandidate_attitude_rating = (RatingBar) v.findViewById(R.id.candidate_attitude_rating);
        mInterestRatingEditText.setText("no");
        mAttitudeRatingEditText.setText("no");
        cbFeedBackNoticePeriod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    mCityTextInputLayout_experience.setVisibility(VISIBLE);
                } else {
                    mCityTextInputLayout_experience.setVisibility(GONE);
                    mCityTextInputEditText_experience.setText("");
                }
            }
        });


        mcandidate_attitude_rating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            attitude = String.valueOf(rating);
            Log.e("attitude_rating", String.valueOf(rating));
            if (rating > 0.0 && rating <= 1.0)
                emoji_image_view.setImageResource(R.drawable.e1);
            else if (rating > 1.0 && rating <= 2.0)
                emoji_image_view.setImageResource(R.drawable.e2);
            else if (rating > 2.0 && rating <= 3.0)
                emoji_image_view.setImageResource(R.drawable.e3);
            else if (rating > 3.0 && rating <= 4.0)
                emoji_image_view.setImageResource(R.drawable.e4);
            else
                emoji_image_view.setImageResource(R.drawable.e5);
            mAttitudeRatingEditText.setText("");
            mAttitudeRatingEditText.setText(String.valueOf(rating));
        });
        mcandidate_interest_rating.setOnRatingBarChangeListener((ratingBar, rating1, fromUser) -> {
            if (rating1 == 0)
                Snackbar.make(mLinearLayout, "Rating is mandatory", Snackbar.LENGTH_SHORT).show();
            interest = String.valueOf(rating1);
            Log.e("interest_rating", String.valueOf(rating1));
            if (rating1 > 0.0 && rating1 <= 1.0)
                emoji_image_view2.setImageResource(R.drawable.e1);
            else if (rating1 > 1.0 && rating1 <= 2.0)
                emoji_image_view2.setImageResource(R.drawable.e2);
            else if (rating1 > 2.0 && rating1 <= 3.0)
                emoji_image_view2.setImageResource(R.drawable.e3);
            else if (rating1 > 3.0 && rating1 <= 4.0)
                emoji_image_view2.setImageResource(R.drawable.e4);
            else
                emoji_image_view2.setImageResource(R.drawable.e5);
            mInterestRatingEditText.setText("");
            mInterestRatingEditText.setText(String.valueOf(rating1));
        });
        return view;
    }


    private void initViews() {
        mCityTextInputEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mCityTextInputEditText.clearFocus();
                openPlacesAutoCompleteForCity(1);
            }
        });

        mDatePickerButton.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
                SELECTED_DATE.set(Calendar.YEAR, year);
                SELECTED_DATE.set(Calendar.MONTH, month);
                SELECTED_DATE.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                int m = month + 1;
                mDatePickerButton.setText(dayOfMonth + "-" + m + "-" + year);
            }, SELECTED_DATE.get(Calendar.YEAR), SELECTED_DATE.get(Calendar.MONTH), SELECTED_DATE.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        mTimePickerButton.setOnClickListener(view -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view1, hourOfDay, minute) -> {
                SELECTED_DATE.set(Calendar.HOUR_OF_DAY, hourOfDay);
                SELECTED_DATE.set(Calendar.MINUTE, minute);

                int hourToDisplay = SELECTED_DATE.get(Calendar.HOUR_OF_DAY) % 12;
                int minuteToDisplay = SELECTED_DATE.get(Calendar.MINUTE);
                String am_pm = SELECTED_DATE.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";

                mTimePickerButton.setText(hourToDisplay + " : " + minuteToDisplay + " " + am_pm);
            }, SELECTED_DATE.get(Calendar.HOUR_OF_DAY), SELECTED_DATE.get(Calendar.MINUTE), false);
            timePickerDialog.show();
        });


        mDatePickerButton2.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
                SELECTED_DATE.set(Calendar.YEAR, year);
                SELECTED_DATE.set(Calendar.MONTH, month);
                SELECTED_DATE.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                int m = month + 1;
                mDatePickerButton2.setText(dayOfMonth + "-" + m + "-" + year);
            }, SELECTED_DATE.get(Calendar.YEAR), SELECTED_DATE.get(Calendar.MONTH), SELECTED_DATE.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        mTimePickerButton2.setOnClickListener(view -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view1, hourOfDay, minute) -> {
                SELECTED_DATE.set(Calendar.HOUR_OF_DAY, hourOfDay);
                SELECTED_DATE.set(Calendar.MINUTE, minute);

                int hourToDisplay = SELECTED_DATE.get(Calendar.HOUR_OF_DAY) % 12;
                int minuteToDisplay = SELECTED_DATE.get(Calendar.MINUTE);
                String am_pm = SELECTED_DATE.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";

                mTimePickerButton2.setText(hourToDisplay + " : " + minuteToDisplay + " " + am_pm);
            }, SELECTED_DATE.get(Calendar.HOUR_OF_DAY), SELECTED_DATE.get(Calendar.MINUTE), false);
            timePickerDialog.show();
        });

        //Set up not interested spinner

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, NOT_INTERESTED_OPTIONS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mNotInterestedSpinner.setAdapter(adapter);
        mNotInterestedSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TransitionManager.beginDelayedTransition(mLinearLayout);
                mReasonsEditText.setVisibility(View.VISIBLE);
                switch (NOT_INTERESTED_OPTIONS[position]) {
                    case "Location Mismatch": {
                        mReasonsEditText.setHint("Enter preferred Location");
                        mPreferenceSpinner.setVisibility(View.GONE);
                        mReasonsEditText.setOnFocusChangeListener((v, hasFocus) -> {
                            if (hasFocus) {
                                mReasonsEditText.clearFocus();
                                openPlacesAutoCompleteForCity(2);
                            }
                        });

                        break;
                    }
                    case "Role Mismatch": {
                        mPreferenceSpinner.setVisibility(VISIBLE);
                        mReasonsEditText.setVisibility(View.GONE);
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, ROLE_OPTIONS);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mPreferenceSpinner.setAdapter(adapter1);
                        ch = 1;
                        break;
                    }
                    case "Domain Mismatch": {
                        mPreferenceSpinner.setVisibility(VISIBLE);
                        mReasonsEditText.setVisibility(View.GONE);
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, DOMAIN_OPTIONS);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mPreferenceSpinner.setAdapter(adapter1);
                        ch = 2;

                        break;
                    }
                    case "Skill Mismatch": {
                        mPreferenceSpinner.setVisibility(VISIBLE);
                        mReasonsEditText.setVisibility(View.GONE);
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, SKILL_OPTIONS);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mPreferenceSpinner.setAdapter(adapter1);
                        ch = 3;
                        break;
                    }
                    case "Company Mismatch": {
                        mReasonsEditText.setVisibility(View.GONE);
                        mPreferenceSpinner.setVisibility(View.VISIBLE);
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, COMPANY_OPTIONS);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mPreferenceSpinner.setAdapter(adapter1);
                        break;
                    }
                    case "Already processed": {
                        mReasonsEditText.setVisibility(View.GONE);
                        mPreferenceSpinner.setVisibility(View.GONE);
                        break;
                    }
                    case "Technically not strong": {
                        mReasonsEditText.setText("");
                        mReasonsEditText.setHint("Why you think he is not strong?");
                        mPreferenceSpinner.setVisibility(View.GONE);
                        break;
                    }
                    case "Other Reasons": {
                        mReasonsEditText.setText("");
                        mReasonsEditText.setHint("Enter other reason");
                        mPreferenceSpinner.setVisibility(View.GONE);
                        break;
                    }
                    default: {
                        mReasonsEditText.setVisibility(View.GONE);
                        mPreferenceSpinner.setVisibility(View.GONE);
                        break;
                    }
                }

                mPreferenceSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (ch == 1)
                            mReasonsEditText.setText("Preferred Role : " + ROLE_OPTIONS[position]);
                        else if (ch == 2)
                            mReasonsEditText.setText("Preferred Domain : " + DOMAIN_OPTIONS[position]);
                        else if (ch == 3)
                            mReasonsEditText.setText("Preferred Skill : " + SKILL_OPTIONS[position]);
                        else if (ch == 4)
                            mReasonsEditText.setText("Preferred Company : " + COMPANY_OPTIONS[position]);
                        else
                            mReasonsEditText.setText("");

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            TransitionManager.beginDelayedTransition(mLinearLayout);
            switch (checkedId) {
                case R.id.asked_to_call_later_radio_button: {
                    mAddCommentsEditText.setVisibility(View.GONE);
                    mNotInterestedSpinner.setVisibility(View.GONE);
                    mTimePickerLayout.setVisibility(View.VISIBLE);
                    mTimePickerLayout2.setVisibility(View.GONE);
                    mPreferenceSpinner.setVisibility(View.GONE);
                    break;
                }
                case R.id.candidate_interested_radio_button: {
                    mAddCommentsEditText.setVisibility(View.VISIBLE);
                    mNotInterestedSpinner.setVisibility(View.GONE);
                    mReasonsEditText.setVisibility(View.GONE);
                    mTimePickerLayout.setVisibility(View.GONE);
                    mTimePickerLayout2.setVisibility(View.GONE);
                    mPreferenceSpinner.setVisibility(View.GONE);
                    break;
                }
                case R.id.candidate_interested_unavailable_radio_button: {
                    mAddCommentsEditText.setVisibility(View.GONE);
                    mNotInterestedSpinner.setVisibility(View.GONE);
                    mReasonsEditText.setVisibility(View.GONE);
                    mTimePickerLayout.setVisibility(View.GONE);
                    mTimePickerLayout2.setVisibility(View.VISIBLE);
                    mPreferenceSpinner.setVisibility(View.GONE);

                    break;
                }
                case R.id.candidate_not_interested_radio_button: {
                    mAddCommentsEditText.setVisibility(View.GONE);
                    mNotInterestedSpinner.setVisibility(View.VISIBLE);
                    mNotInterestedSpinner.setSelection(0);
                    mTimePickerLayout.setVisibility(View.GONE);
                    mTimePickerLayout2.setVisibility(View.GONE);
                    break;
                }
            }
        });

    }

    private void openPlacesAutoCompleteForCity(int i) {
        try {
            //Create the City Filter
            val = i;
            AutocompleteFilter filter = new AutocompleteFilter.Builder()
                    .setCountry("IN")
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS)
                    .build();
            //Create the places auto complete intent
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(filter)
                    .build(getActivity());
            //Start the activity
            startActivityForResult(intent, PLACES_AUTO_COMPLETE_CITY_REQUEST_CODE);
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
                    Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                    //Get the complete address
                    String completeAddress = place.getAddress().toString();
                    //Change the City EditText's Text
                    if (val == 1)
                        mCityTextInputEditText.setText(completeAddress);
                    else
                        mReasonsEditText.setText(completeAddress);

                }
                break;
            }
        }
    }


    @Override
    public ArrayList<String> getListOfFeedbackOptions() {
        ArrayList<String> feedbackList = new ArrayList<>();
        int selectedOptionId = mRadioGroup.getCheckedRadioButtonId();
       /* if(mAttitudeRatingEditText.getText().toString().equals("no")){
            Snackbar.make(mLinearLayout, "Ratings are mandatory", Snackbar.LENGTH_SHORT).show();
            return null;
        }
        else
            feedbackList.add("Attitude Rating:"+mAttitudeRatingEditText.getText().toString());

        Log.e("Ratings",mAttitudeRatingEditText.getText().toString());*/

        if (mInterestRatingEditText.getText().toString().equals("no")) {
            Snackbar.make(mLinearLayout, "Ratings are mandatory", Snackbar.LENGTH_SHORT).show();
            return null;
        } else
            feedbackList.add("Interest Rating:" + mInterestRatingEditText.getText().toString());

        Log.e("Ratings", mInterestRatingEditText.getText().toString());

        //  if (mCandidateNameInputEditText.getText().toString().length() == 0) {
        //     Snackbar.make(mLinearLayout, "Please Enter Candidate's Name", Snackbar.LENGTH_SHORT).show();
        //    return null;
        // }
        //else
        //{
        //   feedbackList.add(mCandidateNameInputEditText.getText().toString());
        //}
        if (mCityTextInputEditText.getText().toString().length() == 0) {
            Snackbar.make(mLinearLayout, "Please Enter an Area", Snackbar.LENGTH_SHORT).show();
            return null;
        } else {
            feedbackList.add(mCityTextInputEditText.getText().toString());
        }
        if (cbFeedBackNoticePeriod.isChecked()) {
            if (mCityTextInputEditText_experience.getText().toString().length() == 0) {
                Snackbar.make(mLinearLayout, "Please Enter Years of Experience", Snackbar.LENGTH_SHORT).show();
                return null;
            } else {
                feedbackList.add(mCityTextInputEditText_experience.getText().toString());
            }
        }

        switch (selectedOptionId) {
            case R.id.asked_to_call_later_radio_button: {
                if (mTimePickerButton.getText().toString().toLowerCase().equals("pick time")
                        || mDatePickerButton.getText().toString().toLowerCase().equals("pick date")
                        || SELECTED_DATE.getTime().getTime() <= new Date().getTime()) {
                    Snackbar.make(mLinearLayout, "Please pick appropriate date", Snackbar.LENGTH_SHORT).show();
                    return null;
                }

                long time = SELECTED_DATE.getTime().getTime();
                DateFormatUtility dateFormatUtility = DateFormatUtility.getInstance();
                String formattedDate = dateFormatUtility.convertLongToDate(time) + " " + dateFormatUtility.convertLongToTime(time);
                String feedback = "Asked to call Later at " + formattedDate;
                feedbackList.add(feedback);
                break;
            }
            case R.id.candidate_interested_radio_button: {
                String feedback = "Candidate is Interested. " + mAddCommentsEditText.getText().toString();
                feedbackList.add(feedback);
                break;
            }
            case R.id.candidate_interested_unavailable_radio_button: {
                if (mTimePickerButton2.getText().toString().toLowerCase().equals("pick time")
                        || mDatePickerButton2.getText().toString().toLowerCase().equals("pick date")
                        || SELECTED_DATE.getTime().getTime() <= new Date().getTime()) {
                    Snackbar.make(mLinearLayout, "Please pick appropriate date", Snackbar.LENGTH_SHORT).show();
                    return null;
                }

                long time = SELECTED_DATE.getTime().getTime();
                DateFormatUtility dateFormatUtility = DateFormatUtility.getInstance();
                String formattedDate = dateFormatUtility.convertLongToDate(time) + " " + dateFormatUtility.convertLongToTime(time);
                String feedback = "Candidate requested reschedule to" + formattedDate;
                feedbackList.add(feedback);
                break;
            }
            case R.id.candidate_not_interested_radio_button: {
                int selectedPosition = mNotInterestedSpinner.getSelectedItemPosition();
                if (selectedPosition == 0) {
                    Snackbar.make(mLinearLayout, R.string.please_select_a_reason, Snackbar.LENGTH_SHORT).show();
                    return null;
                }

                String feedback = NOT_INTERESTED_OPTIONS[selectedPosition];
                feedbackList.add(feedback);

                switch (NOT_INTERESTED_OPTIONS[selectedPosition]) {
                    case "Location Mismatch":

                    case "Other Reasons": {
                        if (mReasonsEditText.getText().toString().length() == 0) {
                            mReasonsEditText.setError("Reason Required");
                            Snackbar.make(mLinearLayout, "Please enter a valid reason", Snackbar.LENGTH_SHORT).show();
                            return null;
                        }

                        String preferred = "Preferred " + feedback.split(" ")[0] + " : " + mReasonsEditText.getText().toString();
                        feedbackList.add(preferred);
                        break;
                    }
                    case "Role Mismatch":
                    case "Domain Mismatch":
                    case "Company Mismatch":
                    case "Skill Mismatch": {

                        if (mReasonsEditText.getText().toString().length() == 0) {
                            mReasonsEditText.setError("One Input Required");
                            Snackbar.make(mLinearLayout, "Please Choose one Option", Snackbar.LENGTH_SHORT).show();
                            return null;
                        }
                        String preferred = mReasonsEditText.getText().toString();
                        feedbackList.add(preferred);
                        break;
                    }
                }
                break;
            }

            default: {
                Snackbar.make(mLinearLayout, "Please select an option", Snackbar.LENGTH_SHORT).show();
                return null;
            }
        }

        return feedbackList;
    }

    @Override
    public TodoModel getTodo() {
        int selectedOptionId = mRadioGroup.getCheckedRadioButtonId();

        switch (selectedOptionId) {
            case R.id.asked_to_call_later_radio_button: {
                TodoModel todo = new TodoModel();

                String name = PhoneCallContactUtility.getInstance().covertNumberToName(getContext(), CALL_LOG.getPhoneNumber());
                String time = DateFormatUtility.getInstance().convertDateAndTimeToString(SELECTED_DATE.getTime().getTime());

                todo.setNumber(CALL_LOG.getPhoneNumber());
                todo.setTitle("Kindly Call Now");
                todo.setDescription(name + " - " + CALL_LOG.getClient() + ", " + CALL_LOG.getPrimarySkill());
                todo.setTime(SELECTED_DATE.getTime().getTime());
                todo.setCompleted(false);
                todo.setTodoType(TodoType.CALL_LATER);
                return todo;
            }
        }

        return null;
    }

    private void retrieveCallLog() {
        //Get the job model from file
        BufferedReader bufferedReader;
        File file;
        try {
            file = new File(getContext().getFilesDir(), Constants.FILE_CALL_LOG_CACHE);
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String jobString = "";
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                jobString += line;
            }
            bufferedReader.close();

            //Convert to json model
            Gson gson = new Gson();
            CALL_LOG = gson.fromJson(jobString, CallLogModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
