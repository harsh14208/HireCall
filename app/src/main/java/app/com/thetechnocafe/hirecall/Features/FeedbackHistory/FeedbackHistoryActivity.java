package app.com.thetechnocafe.hirecall.Features.FeedbackHistory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

import app.com.thetechnocafe.hirecall.Features.JobDetailsCallHistory.CompleteFeedbackRecyclerAdapter;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.R;
import app.com.thetechnocafe.hirecall.Utilities.CallLogListHolder;
import app.com.thetechnocafe.hirecall.Utilities.PhoneCallContactUtility;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedbackHistoryActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.phone_text_view)
    TextView mPhoneTextView;
    @BindView(R.id.client_primary_skill_text_view)
    TextView mClientPrimarySkillTextView;

    private static final String EXTRA_PHONE_TEXT = "phone_text";
    private static final String EXTRA_CLIENT_PRIMARY_SKILL = "primary_skill";
    private List<CallLogModel> callLogs;

    public static Intent getIntent(Context context, String phone, String clientPrimarySkill) {
        Intent intent = new Intent(context, FeedbackHistoryActivity.class);
        intent.putExtra(EXTRA_PHONE_TEXT, phone);
        intent.putExtra(EXTRA_CLIENT_PRIMARY_SKILL, clientPrimarySkill);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_history);

        ButterKnife.bind(this);

        callLogs = CallLogListHolder.getInstance().getCallLogList();

        initViews();
    }

    private void initViews() {
        //Set up toolbar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }

        String phoneText = getIntent().getStringExtra(EXTRA_PHONE_TEXT);
        String clientPrimarySkill = getIntent().getStringExtra(EXTRA_CLIENT_PRIMARY_SKILL);

        phoneText = PhoneCallContactUtility.getInstance().covertNumberToName(this, phoneText);
      //  Log.e("phone number",phoneText);


        mPhoneTextView.setText(phoneText);
        mClientPrimarySkillTextView.setText(clientPrimarySkill);

        if (callLogs != null) {
            CompleteFeedbackRecyclerAdapter completeFeedbackRecyclerAdapter = new CompleteFeedbackRecyclerAdapter(this, callLogs);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            mRecyclerView.setAdapter(completeFeedbackRecyclerAdapter);
        }
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
