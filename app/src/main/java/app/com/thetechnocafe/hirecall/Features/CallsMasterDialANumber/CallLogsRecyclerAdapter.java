package app.com.thetechnocafe.hirecall.Features.CallsMasterDialANumber;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.R;
import app.com.thetechnocafe.hirecall.Utilities.DateFormatUtility;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gurleen on 21/4/17.
 */

public class CallLogsRecyclerAdapter extends RecyclerView.Adapter<CallLogsRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private String mDomain;
    private String mClient;
    private List<CallLogModel> mFullCallLogs;

    public CallLogsRecyclerAdapter(Context context, List<List<CallLogModel>> callLogs, String domain, String client) {
        mContext = context;
        mDomain = domain;
        mClient = client;
        mFullCallLogs = new ArrayList<>();
        for (List<CallLogModel> callLog : callLogs) {
            mFullCallLogs.addAll(callLog);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_call_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return mFullCallLogs.size();
    }

    //View Holder
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.client_name_text_view)
        TextView mClientNameTextView;
        @BindView(R.id.date_text_view)
        TextView mDateTextView;
        @BindView(R.id.time_text_view)
        TextView mTimeTextView;
        @BindView(R.id.user_name_text_view)
        TextView mUserNameTextView;
        @BindView(R.id.primary_skill_text_view)
        TextView mPrimarySkillTextView;
        @BindView(R.id.feedback_text_view)
        TextView mFeedbackTextView;
        @BindView(R.id.feedback_purpose_text_view)
        TextView mFeedbackPurposeTextView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(int position) {
            CallLogModel callLog = mFullCallLogs.get(position);

            String date = DateFormatUtility.getInstance().convertLongToDate(callLog.getCallDate());
            String time = DateFormatUtility.getInstance().convertLongToTime(callLog.getCallDate());

            mClientNameTextView.setText(callLog.getClient());
            mPrimarySkillTextView.setText(callLog.getPrimarySkill());
            if (mDomain.equals(callLog.getDomain())) {
                mFeedbackPurposeTextView.setVisibility(View.VISIBLE);
                mFeedbackTextView.setVisibility(View.VISIBLE);
                mFeedbackPurposeTextView.setText(callLog.getFeedbackReason());

                String feedbackString = "";

                for(int i = 0; i < callLog.getFeedback().size(); i++) {
                    feedbackString += callLog.getFeedback().get(i);
                    if(i != callLog.getFeedback().size() - 1) {
                        feedbackString += "\n";
                    }
                }

                mFeedbackTextView.setText(feedbackString);

                if(callLog.getFeedbackReason().equals("Didn't respond/picked the call")) {
                    mFeedbackTextView.setVisibility(View.GONE);
                }

                mUserNameTextView.setText(callLog.getName());
            } else {
                mFeedbackTextView.setVisibility(View.GONE);
                mFeedbackPurposeTextView.setVisibility(View.GONE);
                mUserNameTextView.setText(R.string.unknown);
            }
            mDateTextView.setText(date);
            mTimeTextView.setText(time);
        }
    }
}
