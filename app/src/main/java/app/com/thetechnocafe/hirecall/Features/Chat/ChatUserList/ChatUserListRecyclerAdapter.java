package app.com.thetechnocafe.hirecall.Features.Chat.ChatUserList;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import app.com.thetechnocafe.hirecall.Models.ChatUserModel;
import app.com.thetechnocafe.hirecall.R;
import app.com.thetechnocafe.hirecall.Utilities.DateFormatUtility;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by gurleen on 8/5/17.
 */

public class ChatUserListRecyclerAdapter extends RecyclerView.Adapter<ChatUserListRecyclerAdapter.ViewHolder> {

    //Interface for callbacks
    public interface OnActionListener {
        void onChatUserClicked(ChatUserModel chatUser);
    }

    private Context mContext;
    private List<ChatUserModel> mChatUserList;
    private OnActionListener mListener;

    public ChatUserListRecyclerAdapter(Context context, List<ChatUserModel> chatUsers) {
        mContext = context;
        mChatUserList = chatUsers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_chat_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return mChatUserList.size();

    }

    //View Holder
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.name_text_view)
        TextView mNameTextView;
        @BindView(R.id.time_text_view)
        TextView mTimeTextView;
        @BindView(R.id.last_message_text_view)
        TextView mLastMessageTextView;
        @BindView(R.id.user_circle_image_view)
        CircleImageView mUserCircleImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        void bindData(int position) {
            ChatUserModel chatUser = mChatUserList.get(position);
            Log.e("chat khul","gaya");


            mNameTextView.setText(chatUser.getUserName());

            String chatTime = "";
            if (chatUser.getLastChatMessage() != null) {
                long lastMessageTime = chatUser.getLastChatMessage().getTimeStamp();
                chatTime = DateFormatUtility.getInstance().convertLongToDateSlashFormat(lastMessageTime);
            }

            mTimeTextView.setText(chatTime);
            mLastMessageTextView.setText(chatUser.getLastChatMessage().getMessage());


            mUserCircleImageView.setImageResource(R.drawable.ic_user);
             }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onChatUserClicked(mChatUserList.get(getAdapterPosition()));
            }
        }
    }

    public void addActionListener(OnActionListener listener) {
        mListener = listener;
    }
}
