package app.com.thetechnocafe.hirecall.Features.Feedback;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import app.com.thetechnocafe.hirecall.R;
import app.com.thetechnocafe.hirecall.Utilities.SharedPreferencesUtility;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedbackActivity extends AppCompatActivity {

    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.enter_feedback_text_view)
    TextView mEnterFeedbackTextView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        ButterKnife.bind(this);

        initViews();
    }

    private void initViews() {
        //Configure toolbar
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null) {
        }

        //Configure view pager
        ViewPagerStateAdapter adapter = new ViewPagerStateAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.feedback_options));
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);

        String phoneNumber = SharedPreferencesUtility.getInstance().getCallNumber(this);

        String feedbackText = "Enter feedback of " + phoneNumber;
        mEnterFeedbackTextView.setText(feedbackText);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "It is mandatory to fill feedback", Toast.LENGTH_SHORT).show();
    }
}
