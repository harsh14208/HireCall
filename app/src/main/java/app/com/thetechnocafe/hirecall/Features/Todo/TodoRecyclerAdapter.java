package app.com.thetechnocafe.hirecall.Features.Todo;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import app.com.thetechnocafe.hirecall.Models.TodoModel;
import app.com.thetechnocafe.hirecall.R;
import app.com.thetechnocafe.hirecall.Utilities.DateFormatUtility;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gurleen on 2/5/17.
 */

public class TodoRecyclerAdapter extends RecyclerView.Adapter<TodoRecyclerAdapter.ViewHolder> {

    //Interface for callbacks
    public interface OnTodoActionListener {
        void onTodoCheckedChanged(TodoModel todo);
    }

    private Context mContext;
    private List<TodoModel> mTodoList;
    private OnTodoActionListener mListener;
    private boolean isAllList;

    public TodoRecyclerAdapter(Context context, List<TodoModel> todoList, boolean allList) {
        mContext = context;
        mTodoList = todoList;
        isAllList = allList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recycler_todo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return mTodoList.size();
    }

    //View Holder
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title_text_view)
        TextView mTitleTextView;
        @BindView(R.id.description_text_view)
        TextView mDescriptionTextView;
        @BindView(R.id.complete_check_box)
        AppCompatCheckBox mCompletedCheckBox;
        @BindView(R.id.complete_text_view)
        TextView mCompletedTextView;
        @BindView(R.id.reminder_time_text_view)
        TextView mReminderTimeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(int position) {
            mCompletedCheckBox.setOnCheckedChangeListener(null);

            TodoModel todo = mTodoList.get(position);

            mTitleTextView.setText(todo.getTitle());
            mDescriptionTextView.setText(todo.getDescription());
            mCompletedCheckBox.setChecked(todo.isCompleted());

            if (todo.getTime() > new Date().getTime()) {
                mReminderTimeTextView.setVisibility(View.VISIBLE);
                mReminderTimeTextView.setText(DateFormatUtility.getInstance().convertDateAndTimeToStringForTodos(todo.getTime()));
            } else {
                mReminderTimeTextView.setText("Task has been timed out");
            }

            if (todo.isCompleted()) {
                mReminderTimeTextView.setVisibility(View.GONE);
            }

            setCheckedText(todo.isCompleted());

            mCompletedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                todo.setCompleted(isChecked);
                if (mListener != null) {
                    mListener.onTodoCheckedChanged(todo);
                }

                setCheckedText(isChecked);
            });
        }

        private void setCheckedText(boolean isChecked) {
            if (isChecked) {
                mCompletedTextView.setText("Move to\nTodo");
            } else {
                mCompletedTextView.setText("Move to\nDone");
            }
        }
    }

    public void setOnTodoActionListener(OnTodoActionListener listener) {
        mListener = listener;
    }
}
