package app.com.thetechnocafe.hirecall.Features.CallsMasterCallHistory;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

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
    private List<CallLogModel> mCallLogs;
    private List<CallLogModel> mOriginalList;
    private android.app.FragmentManager mFragmentManager;
    private CallActionListener mListener;

    public interface CallActionListener {
        void onCallButtonClicked(String phoneNumber);
    }

    public CallLogsRecyclerAdapter(Context context, android.app.FragmentManager fragmentManager, List<CallLogModel> callLogs) {
        mContext = context;
        mCallLogs = callLogs;
        mOriginalList = callLogs;
        mFragmentManager = fragmentManager;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_call_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return mCallLogs.size();
    }

    //View Holder
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.date_text_view)
        TextView mDateTextView;
        @BindView(R.id.client_primary_skill_text_view)
        TextView mClientPrimarySkillTextView;
        @BindView(R.id.phone_number_text_view)
        TextView mPhoneNumberTextView;
        @BindView(R.id.call_image_view)
        ImageView mCallImageView;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        void bindData(int position) {
            CallLogModel callLog = mCallLogs.get(position);

            String date = DateFormatUtility.getInstance().convertDateAndTimeToString(callLog.getCallDate());

            String clientPrimarySkill = callLog.getClient() + " - " + callLog.getPrimarySkill();

            mClientPrimarySkillTextView.setText(clientPrimarySkill);
            mDateTextView.setText(date);
            mPhoneNumberTextView.setText(callLog.getDisplayName());

            mCallImageView.setOnClickListener(view -> {
                if(mListener != null) {
                    mListener.onCallButtonClicked(callLog.getPhoneNumber());
                }
            });
        }

        private void showFeedbackDialog(String phone, String clientPrimarySkill, String time) {
            Intent intent = FeedbackHistoryActivity.getIntent(mContext, phone, clientPrimarySkill);
            mContext.startActivity(intent);
        }

        @Override
        public void onClick(View v) {
            CallLogModel call = mCallLogs.get(getAdapterPosition());

            //Get all the call logs with selected number
            String number = mCallLogs.get(getAdapterPosition()).getPhoneNumber();

            String time = DateFormatUtility.getInstance().convertLongToDateSlashFormat(call.getCallDate());

            List<CallLogModel> callLogList = new ArrayList<>();
            for(CallLogModel callLog : mCallLogs) {
                if(call.getJobID().equals(callLog.getJobID()) && call.getPhoneNumber().equals(callLog.getPhoneNumber())) {
                    callLogList.add(callLog);
                }
            }
            CallLogListHolder.getInstance().setCallLogList(callLogList);

            showFeedbackDialog(call.getPhoneNumber(), call.getClient() + " - " + call.getPrimarySkill(), time);
        }
    }

    public void filterListForKeyword(String keyword) {
        mCallLogs = new ArrayList<>();
        for (CallLogModel callLog : mOriginalList) {
            if (callLog.getPhoneNumber().toLowerCase().contains(keyword.toLowerCase())
                    || callLog.getDisplayName().toLowerCase().contains(keyword.toLowerCase())) {
                mCallLogs.add(callLog);
            }
        }

        notifyDataSetChanged();
    }

    public void addCallActionListener(CallActionListener listener) {
        mListener = listener;
    }
}
