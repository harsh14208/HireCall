package app.com.thetechnocafe.hirecall.Features.Chat.DetailChat;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.com.thetechnocafe.hirecall.Data.FirebaseAuthDB;
import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import app.com.thetechnocafe.hirecall.Features.CreateTodo.CreateTodoActivity;
import app.com.thetechnocafe.hirecall.Models.ChatMessageModel;
import app.com.thetechnocafe.hirecall.Models.ChatUserModel;
import app.com.thetechnocafe.hirecall.R;
import app.com.thetechnocafe.hirecall.Utilities.Constants;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailChatActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.chat_recycler_view)
    RecyclerView mChatRecyclerView;
    @BindView(R.id.message_edit_text)
    EditText mMessageEditText;
    @BindView(R.id.send_button)
    ImageView mSendButton;

    public static final String EXTRA_CHAT_USER_MODEL = "chat_user";
    private DetailChatRecyclerAdapter mChatRecyclerAdapter;
    private LinearLayoutManager mRecyclerViewLayoutManager;
    private ActionMode mActionMode;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.menu_action_chat, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            if (mChatRecyclerAdapter.getSelectedList().size() > 1) {
                menu.findItem(R.id.create_task).setVisible(false);
            } else {
                menu.findItem(R.id.create_task).setVisible(true);
            }
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete: {
                    for (String messageId : mChatRecyclerAdapter.getSelectedList()) {
                        deleteMessage(messageId);
                    }
                    break;
                }
                case R.id.create_task: {
                    Intent intent = new Intent(getApplicationContext(), CreateTodoActivity.class);
                    intent.putExtra(CreateTodoActivity.EXTRA_DESCRIPTION, mChatRecyclerAdapter.getMessageOfSelectedChatMessage());
                    startActivity(intent);
                    break;
                }
            }

            mActionMode.finish();
            mActionMode = null;
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mChatRecyclerAdapter.toggleAllUnselected();
        }
    };

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mChatDatabaseReference;
    private DatabaseReference mOppositeUserChatDatabaseReference;
    private DatabaseReference mCurrentUserChatDatabaseReference;
    private ValueEventListener mChatValueEventListener;
    private ChatUserModel mChatUserModel;
    private String mUserId,name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_chat);

        ButterKnife.bind(this);

        mChatUserModel = (ChatUserModel) getIntent().getSerializableExtra(EXTRA_CHAT_USER_MODEL);
        mUserId = FirebaseAuthDB.getInstance().getSignedInUserUID();
        name= FirebaseAuthDB.getInstance().getSignedInUserName();

        mFirebaseDatabase = FirebaseDB.getInstance().getFirebaseInstance();
        mChatDatabaseReference = mFirebaseDatabase.getReference(Constants.FB_CHATS)
                .child(mChatUserModel.getChatID());
        mOppositeUserChatDatabaseReference = mFirebaseDatabase.getReference(Constants.FB_USERS)
                .child(mChatUserModel.getUserID())
                .child(Constants.FB_CHATS)
                .child(mUserId);
        mCurrentUserChatDatabaseReference = mFirebaseDatabase.getReference(Constants.FB_USERS)
                .child(mUserId)
                .child(Constants.FB_CHATS)
                .child(mChatUserModel.getUserID());

        addValueEventListenerForChat();

        initViews();
    }

    private void initViews() {
        //Initialize toolbar
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
            getSupportActionBar().setTitle(mChatUserModel.getUserName());

        }

        //Set up the recycler view
        mRecyclerViewLayoutManager = new LinearLayoutManager(this);
        mRecyclerViewLayoutManager.setStackFromEnd(true);
        mChatRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);

        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mSendButton.setEnabled(false);
                } else {
                    mSendButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mSendButton.setOnClickListener(view -> {
            String message = mMessageEditText.getText().toString();
            sendMessage(message);
            mMessageEditText.setText("");
        });
        mSendButton.setEnabled(false);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeValueEventListenerForChat();
    }

    private void addValueEventListenerForChat() {
        mChatValueEventListener = mChatDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ChatMessageModel> chatMessagesList = new ArrayList<>();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    ChatMessageModel chatMessage = child.getValue(ChatMessageModel.class);
                    chatMessage.setId(child.getKey());
                    chatMessagesList.add(chatMessage);
                }

                setUpOrRefreshRecyclerView(chatMessagesList);
                mRecyclerViewLayoutManager.scrollToPosition(chatMessagesList.size() - 1);

                //Change the last message
           /*     if (chatMessagesList.size() > 0) {
                    setLastMessage(chatMessagesList.get(chatMessagesList.size() - 1));
                } else {
                    ChatMessageModel chatMessageModel = new ChatMessageModel();
                    chatMessageModel.setTimeStamp(new Date().getTime());
                    setLastMessage(chatMessageModel);
                }
            */}

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void removeValueEventListenerForChat() {
        if (mChatValueEventListener != null) {
            mChatDatabaseReference.removeEventListener(mChatValueEventListener);
        }
    }

    private void setUpOrRefreshRecyclerView(List<ChatMessageModel> chatMessages) {

        if (mChatRecyclerAdapter == null) {
            mChatRecyclerAdapter = new DetailChatRecyclerAdapter(this, chatMessages);
            mChatRecyclerView.setAdapter(mChatRecyclerAdapter);
            mChatRecyclerAdapter.addActionListener(new DetailChatRecyclerAdapter.OnActionListener() {
                @Override
                public void onClick(int position, ChatMessageModel chatMessageModel) {
                    //Check if the action mode is active
                    if (mActionMode != null) {
                        onListItemSelected(position, chatMessageModel);
                    }
                }

                @Override
                public void onLongPressed(int position, ChatMessageModel chatMessageModel) {
                    onListItemSelected(position, chatMessageModel);
                }
            });
        } else {
            mChatRecyclerAdapter.updateChatList(chatMessages);
        }
    }

    private void sendMessage(String message) {
        ChatMessageModel chatMessage = new ChatMessageModel();
        chatMessage.setMessage(message);
        chatMessage.setTimeStamp(new Date().getTime());
        chatMessage.setUserID(mUserId);
        chatMessage.setName(name);

        mChatDatabaseReference.push().setValue(chatMessage);
        //setLastMessage(chatMessage);
    }

    private void setLastMessage(ChatMessageModel chatMessage) {
        mCurrentUserChatDatabaseReference.child(Constants.FB_LAST_CHAT_MESSAGE).setValue(chatMessage);
        mOppositeUserChatDatabaseReference.child(Constants.FB_LAST_CHAT_MESSAGE).setValue(chatMessage);
    }

    private void onListItemSelected(int position, ChatMessageModel chatMessage) {
        mChatRecyclerAdapter.toggleSelection(chatMessage);

        boolean hasCheckedItems = mChatRecyclerAdapter.getSelectedList().size() > 0;

        if (hasCheckedItems && mActionMode == null) {
            mActionMode = startSupportActionMode(mActionModeCallback);
        } else if (!hasCheckedItems && mActionMode != null) {
            mActionMode.finish();
            mActionMode = null;
        }

        if (mActionMode != null) {
            mActionMode.invalidate();
            mActionMode.setTitle(mChatRecyclerAdapter.getSelectedList().size() + " Selected");
        }
    }

    private void deleteMessage(String messageID) {
        mChatDatabaseReference.child(messageID).removeValue();
    }
}
