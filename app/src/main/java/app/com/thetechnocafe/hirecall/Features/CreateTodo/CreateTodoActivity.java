package app.com.thetechnocafe.hirecall.Features.CreateTodo;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Calendar;
import java.util.Date;

import app.com.thetechnocafe.hirecall.Features.Todo.TodoFragment;
import app.com.thetechnocafe.hirecall.Models.TodoModel;
import app.com.thetechnocafe.hirecall.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateTodoActivity extends AppCompatActivity implements CreateTodoContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.linear_layout)
    LinearLayout mLinearLayout;
    @BindView(R.id.title_text_input_layout)
    TextInputLayout mTitleTextInputLayout;
    @BindView(R.id.title_text_input_edit_text)
    TextInputEditText mTitleTextInputEditText;
    @BindView(R.id.description_text_input_layout)
    TextInputLayout mDescriptionTextInputLayout;
    @BindView(R.id.description_text_input_edit_text)
    TextInputEditText mDescriptionTextInputEditText;
    @BindView(R.id.date_picker_button)
    Button mDatePickerButton;
    @BindView(R.id.time_picker_button)
    Button mTimePickerButton;
    @BindView(R.id.save_reminder_button)
    Button mSaveReminderButton;

    public static final String EXTRA_DESCRIPTION = "description";
    private CreateTodoContract.Presenter mPresenter;
    private static Calendar SELECTED_DATE = Calendar.getInstance();
    private ProgressDialog mProgressDialog;

    //Instance method
    public static TodoFragment getInstance() {
        return new TodoFragment();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_todo);

        ButterKnife.bind(this);

        mPresenter = new CreateTodoPresenter();
        mPresenter.subscribe(this);

        if(getIntent().getStringExtra(EXTRA_DESCRIPTION) != null) {
            mDescriptionTextInputEditText.setText(getIntent().getStringExtra(EXTRA_DESCRIPTION));
        }

        initViews();
    }


    private void initViews() {
        //Set up toolbar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }

        mDatePickerButton.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
                SELECTED_DATE.set(Calendar.YEAR, year);
                SELECTED_DATE.set(Calendar.MONTH, month);
                SELECTED_DATE.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                mDatePickerButton.setText(dayOfMonth + "-" + month + "-" + year);
            }, SELECTED_DATE.get(Calendar.YEAR), SELECTED_DATE.get(Calendar.MONTH), SELECTED_DATE.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        mTimePickerButton.setOnClickListener(view -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                SELECTED_DATE.set(Calendar.HOUR_OF_DAY, hourOfDay);
                SELECTED_DATE.set(Calendar.MINUTE, minute);

                int hourToDisplay = SELECTED_DATE.get(Calendar.HOUR_OF_DAY) % 12;
                int minuteToDisplay = SELECTED_DATE.get(Calendar.MINUTE);
                String am_pm = SELECTED_DATE.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";

                mTimePickerButton.setText(hourToDisplay + " : " + minuteToDisplay + " " + am_pm);
            }, SELECTED_DATE.get(Calendar.HOUR_OF_DAY), SELECTED_DATE.get(Calendar.MINUTE), false);
            timePickerDialog.show();
        });

        mSaveReminderButton.setOnClickListener(view -> {
            if (validateInputs()) {
                String title = mTitleTextInputEditText.getText().toString();
                String description = mDescriptionTextInputEditText.getText().toString();

                TodoModel todoModel = new TodoModel();
                todoModel.setTitle(title);
                todoModel.setDescription(description);
                todoModel.setTime(SELECTED_DATE.getTime().getTime());

                mPresenter.createReminder(todoModel);
                startProgressDialog("Creating Reminder");
            }
        });
    }

    @Override
    public Context getAppContext() {
        return this;
    }

    private boolean validateInputs() {
        String title = mTitleTextInputEditText.getText().toString();
        String description = mDescriptionTextInputEditText.getText().toString();

        if (title.length() < 4) {
            Snackbar.make(mLinearLayout, "Title is too short", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (description.length() < 4) {
            Snackbar.make(mLinearLayout, "Description is too short", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (mTimePickerButton.getText().toString().toLowerCase().equals("pick time")
                || mDatePickerButton.getText().toString().toLowerCase().equals("pick date")
                || SELECTED_DATE.getTime().getTime() <= new Date().getTime()) {
            Snackbar.make(mLinearLayout, "Please pick appropriate date", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void startProgressDialog(String message) {
        mProgressDialog = new ProgressDialog(this);
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
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unSubscribe();
    }

    @Override
    public void reminderSetSuccessful() {
        setResult(RESULT_OK);
        stopProgressDialog();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
