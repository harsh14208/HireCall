package app.com.thetechnocafe.hirecall.Features.Chat.DetailChat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.com.thetechnocafe.hirecall.Data.FirebaseAuthDB;
import app.com.thetechnocafe.hirecall.Models.ChatMessageModel;
import app.com.thetechnocafe.hirecall.R;
import app.com.thetechnocafe.hirecall.Utilities.DateFormatUtility;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gurleen on 8/5/17.
 */

public class DetailChatRecyclerAdapter extends RecyclerView.Adapter<DetailChatRecyclerAdapter.ViewHolder> {

    //Interface for callbacks
    public interface OnActionListener {
        void onClick(int position, ChatMessageModel chatMessageModel);

        void onLongPressed(int position, ChatMessageModel chatMessageModel);
    }

    private Context mContext;
    private List<ChatMessageModel> mChatMessages;
    private OnActionListener mListener;
    private String mCurrentUserId;
    private List<String> mSelectedList;

    public DetailChatRecyclerAdapter(Context context, List<ChatMessageModel> chatMessages) {
        mContext = context;
        mChatMessages = chatMessages;
        mCurrentUserId = FirebaseAuthDB.getInstance().getSignedInUserUID();
        mSelectedList = new ArrayList<>();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_chat_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return mChatMessages.size();
    }

    //View Holder
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        @BindView(R.id.linear_layout)
        LinearLayout mLinearLayout;
        @BindView(R.id.inner_linear_layout)
        LinearLayout mInnerLinearLayout;
        @BindView(R.id.message_text_view)
        TextView mMessageTextView;
        @BindView(R.id.time_text_view)
        TextView mTimeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        void bindData(int position) {
            ChatMessageModel chatMessage = mChatMessages.get(position);

            mMessageTextView.setText(chatMessage.getMessage());

            //Get the required padding
            int padding4dp = mContext.getResources().getDimensionPixelOffset(R.dimen.dimen_4dp);
            int padding16dp = mContext.getResources().getDimensionPixelOffset(R.dimen.dimen_16dp);
            int padding24dp = mContext.getResources().getDimensionPixelOffset(R.dimen.dimen_24dp);

            //If the message is of the user itself then move it to right
            //else move to left
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams timeLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            timeLayoutParams.topMargin = padding4dp;

            if (chatMessage.getUserID().equals(mCurrentUserId)) {
                Drawable mDrawable= ContextCompat.getDrawable(mContext,R.drawable.ic_chat_bubble_reverse);
                mInnerLinearLayout.setBackground(mDrawable);
                layoutParams.gravity = Gravity.RIGHT;
                layoutParams.leftMargin = 160;
                timeLayoutParams.gravity = Gravity.LEFT;
            } else {
                Drawable mDrawable= ContextCompat.getDrawable(mContext,R.drawable.ic_chat_bubble);
                Drawable wrapDrawable = DrawableCompat.wrap(mDrawable);
                DrawableCompat.setTint(wrapDrawable, Color.GRAY);
                mDrawable.mutate();
                mInnerLinearLayout.setBackground(wrapDrawable);

                layoutParams.gravity = Gravity.LEFT;
                layoutParams.rightMargin = 160;
                timeLayoutParams.gravity = Gravity.RIGHT;
            }
            mInnerLinearLayout.setLayoutParams(layoutParams);
            mInnerLinearLayout.setPadding(padding16dp, padding16dp, padding16dp, padding16dp);

            mTimeTextView.setLayoutParams(timeLayoutParams);

            String formattedTime = DateFormatUtility.getInstance().convertDateAndTimeToString(chatMessage.getTimeStamp());
            mTimeTextView.setText(formattedTime);

            //Toggle the selected foreground visibility according to selected item
            if (mSelectedList.contains(chatMessage.getId())) {
                mLinearLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.md_blue_100));
            } else {
                mLinearLayout.setBackgroundColor(ContextCompat.getColor(mContext, android.R.color.transparent));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mListener != null) {
                mListener.onLongPressed(getAdapterPosition(), mChatMessages.get(getAdapterPosition()));
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onClick(getAdapterPosition(), mChatMessages.get(getAdapterPosition()));
            }
        }
    }

    public void updateChatList(List<ChatMessageModel> chatMessageModels) {
        mChatMessages = chatMessageModels;
        notifyDataSetChanged();
    }

    public void addActionListener(OnActionListener listener) {
        mListener = listener;
    }

    public void toggleSelection(ChatMessageModel chatMessage) {
        Log.d("TAG", "Selection toggled");
        if (mSelectedList.contains(chatMessage.getId())) {
            mSelectedList.remove(chatMessage.getId());
        } else {
            mSelectedList.add(chatMessage.getId());
        }

        notifyItemChanged(mChatMessages.indexOf(chatMessage));
    }

    public void toggleAllUnselected() {
        mSelectedList.clear();
        notifyDataSetChanged();
    }

    public List<String> getSelectedList() {
        return mSelectedList;
    }

    public List<ChatMessageModel> getMessageList() {
        return mChatMessages;
    }

    public String getMessageOfSelectedChatMessage() {
        String message = "";
        for (ChatMessageModel chatMessage : mChatMessages) {
            if (chatMessage.getId().equals(mSelectedList.get(0))) {
                message = chatMessage.getMessage();
            }
        }
        return message;
    }
}
