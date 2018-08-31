package app.com.thetechnocafe.hirecall.Features.Jobs.ActiveJobs;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.com.thetechnocafe.hirecall.Models.JobModel;
import app.com.thetechnocafe.hirecall.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gurleensethi on 19/04/17.
 */

public class JobsRecyclerAdapter extends RecyclerView.Adapter<JobsRecyclerAdapter.ViewHolder> {

    private Context mContext;
    private List<JobModel> mJobList;
    private OnJobSelectedListener mListener;

    public JobsRecyclerAdapter(Context context, List<JobModel> jobsList) {
        mContext = context;
        mJobList = jobsList;
    }

    //Interface for click callbacks
    public interface OnJobSelectedListener {
        void onClick(JobModel job);

        void onArchiveClicked(JobModel job);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_jobs, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return mJobList.size();
    }

    //ViewHolder
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.client_name_text_view)
        TextView mClientNameTextView;
        @BindView(R.id.primary_skill_text_view)
        TextView mPrimarySkillTextView;
        @BindView(R.id.three_dot_menu_image_view)
        ImageView mThreeDotMenuImageView;
        @BindView(R.id.experience_text_view)
        TextView mExperienceTextView;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        void bindData(int position) {
            JobModel job = mJobList.get(position);
            mClientNameTextView.setText(job.getClientName());
            String primarySkillWithLocation = job.getPrimarySkill() + " - " + job.getCityLocation().split(",")[0];
            mPrimarySkillTextView.setText(primarySkillWithLocation);
            String experience = job.getMinExpense() + " - " + job.getMaxExpense() + " Years";
            mExperienceTextView.setText(experience);

            mThreeDotMenuImageView.setOnClickListener(view -> {
                PopupMenu menu = new PopupMenu(mContext, mThreeDotMenuImageView);
                menu.getMenuInflater().inflate(R.menu.menu_popup_archive, menu.getMenu());
                menu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.archive: {
                            if(mListener != null) {
                                mListener.onArchiveClicked(mJobList.get(getAdapterPosition()));
                            }
                            menu.dismiss();
                            return true;
                        }
                        default:
                            return false;
                    }
                });
                menu.show();
            });

            //Check if invited job
            if(job.isInvited()) {
                mThreeDotMenuImageView.setVisibility(View.GONE);
            } else {
                mThreeDotMenuImageView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onClick(mJobList.get(getAdapterPosition()));
            }
        }
    }

    public void addOnJobSelectedListener(OnJobSelectedListener listener) {
        mListener = listener;
    }
}
