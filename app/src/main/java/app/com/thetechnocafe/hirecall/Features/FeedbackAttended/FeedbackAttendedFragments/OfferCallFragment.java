package app.com.thetechnocafe.hirecall.Features.FeedbackAttended.FeedbackAttendedFragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.Spinner;

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

import app.com.thetechnocafe.hirecall.Enums.TodoType;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.Models.TodoModel;
import app.com.thetechnocafe.hirecall.R;
import app.com.thetechnocafe.hirecall.Utilities.Constants;
import app.com.thetechnocafe.hirecall.Utilities.DateFormatUtility;
import app.com.thetechnocafe.hirecall.Utilities.PhoneCallContactUtility;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;

/**
 * Created by gurleen on 23/4/17.
 */

public class OfferCallFragment extends Fragment implements FeedbackAttendedOptionsContract {

    @BindView(R.id.linear_layout)
    LinearLayout mLinearLayout;
    @BindView(R.id.imageView)
    ImageView emoji_image_view;
    @BindView(R.id.radio_group)
    RadioGroup mRadioGroup;
    @BindView(R.id.preference_spinner)
    Spinner mPreferenceSpinner;
    @BindView(R.id.not_interested_spinner)
    Spinner mNotInterestedSpinner;
    @BindView(R.id.reasons_edit_text)
    EditText mReasonsEditText;
    @BindView(R.id.time_picker_layout)
    LinearLayout mTimePickerLayout;
    @BindView(R.id.time_picker_button)
    Button mTimePickerButton;
    @BindView(R.id.date_picker_button)
    Button mDatePickerButton;
    @BindView(R.id.add_comments_edit_text)
    EditText mAddCommentsEditText;
    @BindView(R.id.attitude_rating_edit_text)
    EditText mAttitudeRatingEditText;

    //@BindView(R.id.candidate_attitude_rating)
    public RatingBar mcandidate_attitude_rating;

    private static Calendar SELECTED_DATE = Calendar.getInstance();
    public static int ch=0;
    private static String[] NOT_INTERESTED_OPTIONS;
    private static CallLogModel CALL_LOG;
    private static String[] DOMAIN_OPTIONS;
    private static String[] SKILL_OPTIONS;
    private static String[] ROLE_OPTIONS;
    private static String[] COMPANY_OPTIONS;
    private static final int PLACES_AUTO_COMPLETE_CITY_REQUEST_CODE = 1;

    //Instance method
    public static OfferCallFragment getInstance() {
        return new OfferCallFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offer_call, container, false);

        ButterKnife.bind(this, view);
        DOMAIN_OPTIONS=getResources().getStringArray(R.array.domain);
        SKILL_OPTIONS=getResources().getStringArray(R.array.skill);
        ROLE_OPTIONS=getResources().getStringArray(R.array.roles);
        COMPANY_OPTIONS=getResources().getStringArray(R.array.company);
        NOT_INTERESTED_OPTIONS = getResources().getStringArray(R.array.not_interested_options);

        initViews();
        retrieveCallLog();
        mcandidate_attitude_rating=(RatingBar)view.findViewById(R.id.candidate_attitude_rating);
        mAttitudeRatingEditText.setText("no");
        mcandidate_attitude_rating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            mAttitudeRatingEditText.setText("");
            mAttitudeRatingEditText.setText(String.valueOf(rating));
            if(rating>0.0&&rating<=1.0)
                emoji_image_view.setImageResource(R.drawable.e1);
            else if(rating>1.0&&rating<=2.0)
                emoji_image_view.setImageResource(R.drawable.e2);
            else if(rating>2.0&&rating<=3.0)
                emoji_image_view.setImageResource(R.drawable.e3);
            else if(rating>3.0&&rating<=4.0)
                emoji_image_view.setImageResource(R.drawable.e4);
            else
                emoji_image_view.setImageResource(R.drawable.e5);
        });

        return view;
    }
    private void openPlacesAutoCompleteForCity() {
        try {
            //Create the City Filter

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
                    mReasonsEditText.setText(completeAddress);

                }
                break;
            }
        }
    }

    private void initViews() {
        mDatePickerButton.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
                SELECTED_DATE.set(Calendar.YEAR, year);
                SELECTED_DATE.set(Calendar.MONTH, month);
                SELECTED_DATE.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                int m=month+1;
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

        //Set up note interested spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, NOT_INTERESTED_OPTIONS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mNotInterestedSpinner.setAdapter(adapter);
        mNotInterestedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                                openPlacesAutoCompleteForCity();
                            }
                        });
                        break;
                    }
                    case "Role Mismatch": {

                        mPreferenceSpinner.setVisibility(VISIBLE);
                        mReasonsEditText.setVisibility(View.GONE);
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,ROLE_OPTIONS);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mPreferenceSpinner.setAdapter(adapter1);
                        ch=1;
                        break;
                    }
                    case "Domain Mismatch": {

                        mPreferenceSpinner.setVisibility(VISIBLE);
                        mReasonsEditText.setVisibility(View.GONE);
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,DOMAIN_OPTIONS);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mPreferenceSpinner.setAdapter(adapter1);
                        ch=2;

                        break;
                    }
                    case "Skill Mismatch": {
                        mPreferenceSpinner.setVisibility(VISIBLE);
                        mReasonsEditText.setVisibility(View.GONE);
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,SKILL_OPTIONS);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mPreferenceSpinner.setAdapter(adapter1);
                        ch=3;
                        break;
                    }
                    case "Company Mismatch": {
                        ch=4;
                        mReasonsEditText.setVisibility(View.GONE);
                        mPreferenceSpinner.setVisibility(View.VISIBLE);
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, COMPANY_OPTIONS);
                        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mPreferenceSpinner.setAdapter(adapter1);
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
                        break;
                    }
                    default: {
                        mReasonsEditText.setVisibility(View.GONE);
                        mReasonsEditText.setText("");
                        mPreferenceSpinner.setVisibility(View.GONE);
                        break;
                    }
                } mPreferenceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(ch==1)
                            mReasonsEditText.setText("Preferred Role : "+ROLE_OPTIONS[position]);
                        else if(ch==2)
                            mReasonsEditText.setText("Preferred Domain : "+DOMAIN_OPTIONS[position]);
                        else if(ch==3)
                            mReasonsEditText.setText("Preferred Skill : "+SKILL_OPTIONS[position]);
                        else if(ch==4)
                            mReasonsEditText.setText("Preferred Company : "+COMPANY_OPTIONS[position]);
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
                    mTimePickerLayout.setVisibility(View.VISIBLE);
                    mNotInterestedSpinner.setVisibility(View.GONE);
                    mReasonsEditText.setVisibility(View.GONE);
                    break;
                }
                case R.id.candidate_is_interested_with_offer_radio_button: {
                    mTimePickerLayout.setVisibility(View.GONE);
                    mNotInterestedSpinner.setVisibility(View.GONE);
                    mReasonsEditText.setVisibility(View.GONE);
                    break;
                }
                case R.id.candidate_not_interested_radio_button: {
                    mTimePickerLayout.setVisibility(View.GONE);
                    mNotInterestedSpinner.setVisibility(View.VISIBLE);
                    mNotInterestedSpinner.setSelection(0);
                    break;
                }
            }
        });
    }

    @Override
    public ArrayList<String> getListOfFeedbackOptions() {
        ArrayList<String> feedbackList = new ArrayList<>();

        int selectedOptionId = mRadioGroup.getCheckedRadioButtonId();
        if(mAttitudeRatingEditText.getText().toString().equals("no")){
            Snackbar.make(mLinearLayout, "Ratings are mandatory", Snackbar.LENGTH_SHORT).show();
            return null;
        }
        else
            feedbackList.add("Attitude Rating :  "+mAttitudeRatingEditText.getText().toString());


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
            case R.id.candidate_is_interested_with_offer_radio_button: {
                String feedback = "Candidate is interested with offer";
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

                        String preferred = mReasonsEditText.getText().toString();
                        feedbackList.add(preferred);
                        break;
                    }
                    case "Role Mismatch":
                    case "Domain Mismatch":
                    case "Company Mismatch":
                    case "Skill Mismatch":{

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
                if (mReasonsEditText.getText().toString().length() == 0) {
                    Snackbar.make(mLinearLayout, "Please select an option", Snackbar.LENGTH_SHORT).show();
                    return null;
                }
            }
        }

        if(mAddCommentsEditText.getText().length() > 0) {
            feedbackList.add("Note : " + mAddCommentsEditText.getText().toString());
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
