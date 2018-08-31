package app.com.thetechnocafe.hirecall.Features.JobDetailsCallHistory;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.R;
import app.com.thetechnocafe.hirecall.Utilities.DateFormatUtility;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gurleen on 21/4/17.
 */

public class CompleteFeedbackRecyclerAdapter extends RecyclerView.Adapter<CompleteFeedbackRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<CallLogModel> mCallLogs;

    public CompleteFeedbackRecyclerAdapter(Context context, List<CallLogModel> feedback) {
        mContext = context;
        mCallLogs = feedback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_feedback_recycle, parent, false);
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
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.call_reason_text_view)
        TextView mCallReasonTextView;
        @BindView(R.id.feedback_text_view)
        TextView mFeedbackTextView;
        @BindView(R.id.time_text_view)
        TextView mTimeTextView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(int position) {
            mCallReasonTextView.setText(mCallLogs.get(position).getFeedbackReason());

            if (!mCallLogs.get(position).getFeedbackReason().equals("Didn't respond/picked the call")) {
                String feedback = "";
                List<String> feedbackList = mCallLogs.get(position).getFeedback();
                for (int i = 0; i < feedbackList.size(); i++) {
                    feedback += feedbackList.get(i);
                    if (i != feedbackList.size() - 1) {
                        feedback += "\n";
                    }
                }
                mFeedbackTextView.setText(feedback);
            } else {
                mFeedbackTextView.setText(mCallLogs.get(position).getFeedbackReason());
            }

            String time = DateFormatUtility.getInstance().convertDateAndTimeToString(mCallLogs.get(position).getCallDate());
            mTimeTextView.setText(time);
        }
    }
}
