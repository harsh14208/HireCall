package app.com.thetechnocafe.hirecall.Features.Chat.ChatUserList;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import app.com.thetechnocafe.hirecall.Data.FirebaseAuthDB;
import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import app.com.thetechnocafe.hirecall.Features.Chat.DetailChat.DetailChatActivity;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.Models.ChatMessageModel;
import app.com.thetechnocafe.hirecall.Models.ChatUserModel;
import app.com.thetechnocafe.hirecall.Models.UserModel;
import app.com.thetechnocafe.hirecall.R;
import app.com.thetechnocafe.hirecall.Utilities.AlarmSchedulerUtility;
import app.com.thetechnocafe.hirecall.Utilities.Constants;
import app.com.thetechnocafe.hirecall.Utilities.SharedPreferencesUtility;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by gurleen on 8/5/17.
 */

public class ChatUserListFragment extends Fragment {

    @BindView(R.id.chat_user_list_recycler_view)
    RecyclerView mChatUserRecyclerView;

    public View mView;
    private ChatUserListRecyclerAdapter mChatUserRecyclerAdapter;
    private DatabaseReference mUserDatabaseReference;
    private ValueEventListener mUserListValueEventListener;
    private String mUserID;
    private List<ChatUserModel> mChatUserList;
    boolean allowRefresh=true;

    //Instance method
    public static ChatUserListFragment getInstance() {
        return new ChatUserListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserID = FirebaseAuthDB.getInstance().getSignedInUserUID();

        mUserDatabaseReference = FirebaseDB.getInstance()
                .getFirebaseInstance()
                .getReference(Constants.FB_USERS)
                .child(mUserID);

        mUserDatabaseReference.keepSynced(true);

        addListenerForListOfChats();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_user_list, container, false);
        mView=view;
        ButterKnife.bind(this, view);

        initViews();

        return view;
    }

    private void initViews() {
        mChatUserRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeListenerForListOfChats();
    }

    private void addListenerForListOfChats() {


        String uid = FirebaseAuthDB.getInstance().getSignedInUserUID();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        List<ChatUserModel> userList = new ArrayList<>();


        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId",uid);
        JsonObjectRequest jsonReq = new JsonObjectRequest("http://13.71.116.40:4040/getUserChatId.php", new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.e(TAG, "Response: " + response.toString());

                        try{
                            JSONArray jsonArray= response.getJSONArray("chats");
                            for(int i=0;i<jsonArray.length();i++){
                                Gson gson= new Gson();
                                ChatUserModel chatUserModel= gson.fromJson(jsonArray.getString(i), ChatUserModel.class);
                                userList.add(chatUserModel);
                            }

                            //Sort the list according to the chat with latest message
                            Collections.sort(userList, (o1, o2) -> {
                                Log.d("TAG", "Before Sorting");
                                if (o1.getLastChatMessage() == null) {
                                    return -1;
                                }

                                Log.d("TAG", "Hello Sorting");

                                if (o2.getLastChatMessage() == null) {
                                    return -1;
                                }

                                Log.d("TAG", "Sorting");

                                if (o1.getLastChatMessage().getTimeStamp() > o2.getLastChatMessage().getTimeStamp()) {
                                    return -1;
                                } else if (o1.getLastChatMessage().getTimeStamp() < o2.getLastChatMessage().getTimeStamp()) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            });

                            mChatUserList = userList;
                            setUpOrRefreshRecyclerView(userList);

                        }
                        catch (Exception e){

                        }
                     //    Log.e("Response",response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
            }
        });
        queue.add(jsonReq);









 /*
                         Gson gson= new Gson();
            String params= gson.toJson(job);
                         */
 /*

        mUserListValueEventListener = mUserDatabaseReference.child(Constants.FB_CHATS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<ChatUserModel> userList = new ArrayList<>();

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            ChatUserModel chatUser = child.getValue(ChatUserModel.class);
                            chatUser.setUserID(child.getKey());
                            userList.add(chatUser);
                        }


                        //Sort the list according to the chat with latest message
                        Collections.sort(userList, (o1, o2) -> {
                            Log.d("TAG", "Before Sorting");
                            if (o1.getLastChatMessage() == null) {
                                return -1;
                            }

                            Log.d("TAG", "Hello Sorting");

                            if (o2.getLastChatMessage() == null) {
                                return -1;
                            }

                            Log.d("TAG", "Sorting");

                            if (o1.getLastChatMessage().getTimeStamp() > o2.getLastChatMessage().getTimeStamp()) {
                                return -1;
                            } else if (o1.getLastChatMessage().getTimeStamp() < o2.getLastChatMessage().getTimeStamp()) {
                                return 1;
                            } else {
                                return 0;
                            }
                        });

                        mChatUserList = userList;
                        setUpOrRefreshRecyclerView(userList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                */

    }

    private void removeListenerForListOfChats() {
        if (mUserListValueEventListener != null) {
            mUserDatabaseReference.child(Constants.FB_CHATS)
                    .removeEventListener(mUserListValueEventListener);
        }
    }

    private void setUpOrRefreshRecyclerView(List<ChatUserModel> chatUserList) {
        mChatUserRecyclerAdapter = new ChatUserListRecyclerAdapter(getContext(), chatUserList);
        mChatUserRecyclerView.setAdapter(mChatUserRecyclerAdapter);
        mChatUserRecyclerAdapter.addActionListener(new ChatUserListRecyclerAdapter.OnActionListener() {
            @Override
            public void onChatUserClicked(ChatUserModel chatUser) {
                Intent intent = new Intent(getContext(), DetailChatActivity.class);
                intent.putExtra(DetailChatActivity.EXTRA_CHAT_USER_MODEL, chatUser);
                startActivity(intent);
            }
        });
    }

    private void addUserToChat(String email) {
        //Check if email already exists
        for (ChatUserModel chatUser : mChatUserList) {
            if (chatUser.getEmail().equals(email)) {
                Toast.makeText(getContext(), "User already in chat", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        new Thread() {
            @Override
            public void run() {
                super.run();
                FirebaseDatabase firebaseDatabase = FirebaseDB.getInstance().getFirebaseInstance();

                //Create a new chat in the chat tree
                String chatID = firebaseDatabase
                        .getReference(Constants.FB_CHATS)
                        .push()
                        .getKey();

                Query query = firebaseDatabase.getReference(Constants.FB_USERS)
                        .orderByChild("email")
                        .equalTo(email);

                //Get the uid of the user with email
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            //Create a demo chat message model
                            ChatMessageModel chatMessageModel = new ChatMessageModel();
                            chatMessageModel.setTimeStamp(new Date().getTime());

                            //Add the details in the opposite user's chat
                            UserModel oppositeUser = child.getValue(UserModel.class);
                            oppositeUser.setUid(child.getKey());

                            ChatUserModel oppositeChatModel = new ChatUserModel();
                            oppositeChatModel.setChatID(chatID);
                            oppositeChatModel.setUserName(SharedPreferencesUtility.getInstance().getUserName(getContext()));
                            oppositeChatModel.setEmail(SharedPreferencesUtility.getInstance().getEmail(getContext()));
                            oppositeChatModel.setLastChatMessage(chatMessageModel);

                            firebaseDatabase.getReference(Constants.FB_USERS)
                                    .child(oppositeUser.getUid())
                                    .child(Constants.FB_CHATS)
                                    .child(mUserID)
                                    .setValue(oppositeChatModel);

                            ChatUserModel chatUserModel = new ChatUserModel();
                            chatUserModel.setEmail(oppositeUser.getEmail());
                            chatUserModel.setUserName(oppositeUser.getName());
                            chatUserModel.setChatID(chatID);
                            chatUserModel.setLastChatMessage(chatMessageModel);

                            mUserDatabaseReference.child(Constants.FB_CHATS)
                                    .child(oppositeUser.getUid())
                                    .setValue(chatUserModel);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }.start();
    }

    public void reloadFragment(){
        mChatUserRecyclerAdapter.notifyDataSetChanged();
    }
}
