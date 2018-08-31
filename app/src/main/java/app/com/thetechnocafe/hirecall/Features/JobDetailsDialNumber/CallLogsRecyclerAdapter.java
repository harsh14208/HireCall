package app.com.thetechnocafe.hirecall.Features.JobDetailsDialNumber;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.com.thetechnocafe.hirecall.Features.ClientHistory.ClientHistoryActivity;
import app.com.thetechnocafe.hirecall.Features.FeedbackHistory.FeedbackHistoryActivity;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.R;
import app.com.thetechnocafe.hirecall.Utilities.CallLogListHolder;
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
    private int position;
    private List<CallLogModel> mFullCallLogs,temp;
    private CallActionListener mListener;



    public interface CallActionListener {
        void onCallButtonClicked(String value);
    }

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
        this.position=position;
        holder.bindData(position);
    }


    @Override
    public int getItemCount() {
        return mFullCallLogs.size();
    }

    //View Holder
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
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

            itemView.setOnClickListener(this);
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

                for (int i = 0; i < callLog.getFeedback().size(); i++) {
                    feedbackString += callLog.getFeedback().get(i);
                    if (i != callLog.getFeedback().size() - 1) {
                        feedbackString += "\n";
                    }
                }

                mFeedbackTextView.setText(feedbackString);

                if (callLog.getFeedbackReason().equals("Didn't respond/picked the call")) {
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
        private void showClientDialog(String client, String clientPrimarySkill, String time) {

            Intent intent = ClientHistoryActivity.getIntent(mContext, client, clientPrimarySkill);
            mContext.startActivity(intent);
        }

        @Override
        public void onClick(View v){
            CallLogModel call = mFullCallLogs.get(getAdapterPosition());

            String domain=call.getDomain();
            String client= call.getClient();

            List<CallLogModel> callLogList = new ArrayList<>();
            for(CallLogModel callLog : temp) {

                if(call.getClient().equals(callLog.getClient()) && call.getDomain().equals(callLog.getDomain()) && call.getDomain().equals(mDomain) && call.getPhoneNumber().equals(callLog.getPhoneNumber()) && call.getJobID().equals(callLog.getJobID())) {
                    callLogList.add(callLog);
                }



            }
            CallLogListHolder.getInstance().setCallLogList(callLogList);

            String time = DateFormatUtility.getInstance().convertLongToDateSlashFormat(call.getCallDate());

            showClientDialog(call.getClient(), call.getPrimarySkill(), time);

        }


    }
    public void setCompleteCallLogList(List<CallLogModel> callLogs){
        temp=callLogs;
    }

}
