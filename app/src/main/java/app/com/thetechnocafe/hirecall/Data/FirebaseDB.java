package app.com.thetechnocafe.hirecall.Data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.Models.JobModel;
import app.com.thetechnocafe.hirecall.Models.TodoModel;
import app.com.thetechnocafe.hirecall.Models.UserModel;
import app.com.thetechnocafe.hirecall.Utilities.Constants;
import app.com.thetechnocafe.hirecall.app.Config;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by gurleensethi on 17/04/17.
 */

public class FirebaseDB implements DatabaseContract {
    private static FirebaseDB sInstance;
    private FirebaseDatabase mFirebaseDatabase;
    private BroadcastReceiver mRegistrationBroadcastReceiver;


    //Instance method
    public static FirebaseDB getInstance() {
        if (sInstance == null) {
            sInstance = new FirebaseDB();
        }
        return sInstance;
    }

    public FirebaseDatabase getFirebaseInstance() {
        return mFirebaseDatabase;
    }

    //Singleton class
    private FirebaseDB() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseDatabase.setPersistenceEnabled(true);
    }

    /**
     * Add the user to the Firebase Database
     *
     * @param uid       UID of the user when signed up
     * @param userModel Other user details contained in UserModel object
     */
    @Override
    public Observable<Boolean> addUserToDatabase(String uid, UserModel userModel) {
        Observable<Boolean> observable = Observable.create(emitter -> {
            mFirebaseDatabase.getReference(Constants.FB_USERS)
                    .child(uid)
                    .setValue(userModel)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            emitter.onNext(true);
                        } else {
                            emitter.onNext(false);
                        }
                    });
        });

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Check if the user has created first job or not
     */
    @Override
    public Observable<Boolean> checkForFirstJob() {
        Observable<Boolean> observable = Observable.create(emitter -> {
            //Get the User's UID
            String uid = FirebaseAuthDB.getInstance().getSignedInUserUID();

            mFirebaseDatabase.getReference(Constants.FB_USERS)
                    .child(uid)
                    .child(Constants.FB_CREATED_JOBS)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean firstJobCreated = dataSnapshot.hasChildren();

                            emitter.onNext(firstJobCreated);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        });

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Get the Map of all skills with their id's
     */
    @Override
    public Observable<Map<String, String>> getSkillsMap() {
        Observable<Map<String, String>> observable = Observable.create(emitter -> {
            DatabaseReference databaseReference = mFirebaseDatabase.getReference(Constants.FB_SKILLS);
            databaseReference.keepSynced(true);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, String> map = new HashMap<>();

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        map.put(String.valueOf(child.getKey()), child.getValue(String.class));
                    }

                    emitter.onNext(map);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        });

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Get the Map of all clients with their id's
     */
    @Override
    public Observable<Map<String, String>> getClientsMap() {
        Observable<Map<String, String>> observable = Observable.create(emitter -> {
            DatabaseReference databaseReference = mFirebaseDatabase.getReference(Constants.FB_CLIENTS);
            databaseReference.keepSynced(true);
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, String> map = new HashMap<>();

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        map.put(String.valueOf(child.getKey()), child.getValue(String.class));
                    }

                    emitter.onNext(map);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        });

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Fetch the user from firebase
     */
    @Override
    public Observable<UserModel> getUser() {
        Observable<UserModel> observable = Observable.create(emitter -> {

            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    // checking for type intent filter
                    if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                        // gcm successfully registered
                        // now subscribe to `global` topic to receive app wide notifications
                        FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                        // new push notification is received

                        String message = intent.getStringExtra("message");

                    }
                }
            };




            /*mFirebaseDatabase.getReference(Constants.FB_USERS)
                    .child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserModel user = dataSnapshot.getValue(UserModel.class);
                            user.setUid(uid);
                            emitter.onNext(user);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
*/
        });

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Get the list of created jobs of the signed in user
     **/
    @Override
    public Observable<List<JobModel>> getCreatedJobList() {
        Observable<List<JobModel>> observable = Observable.create(emitter -> {
            String uid = FirebaseAuthDB.getInstance().getSignedInUserUID();

            DatabaseReference databaseReference = mFirebaseDatabase.getReference(Constants.FB_USERS)
                    .child(uid)
                    .child(Constants.FB_CREATED_JOBS);

            databaseReference.keepSynced(true);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<JobModel> jobsList = new ArrayList<>();

                    long childCount = dataSnapshot.getChildrenCount();
                    long counter = 0;
                    final WrapperString lastKey = new WrapperString();

                    //If the list is empty notify
                    if (childCount == 0) {
                        emitter.onNext(jobsList);
                        return;
                    }

                    //Loop over the job id's and retrieve all the jobs
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String jobId = child.getValue(String.class);
                        counter++;
                        if (childCount == counter) {
                            lastKey.value = jobId;
                        }

                        DatabaseReference jobDatabaseRefrence = mFirebaseDatabase.getReference(Constants.FB_JOBS)
                                .child(jobId);

                        jobDatabaseRefrence.keepSynced(true);

                        jobDatabaseRefrence.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                JobModel job = dataSnapshot.getValue(JobModel.class);
                                job.setJobID(dataSnapshot.getKey());
                                jobsList.add(job);

                                if (dataSnapshot.getKey().equals(lastKey.value)) {
                                    emitter.onNext(jobsList);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                emitter.onError(databaseError.toException());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    emitter.onError(databaseError.toException());
                }
            });
        });

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Get the list of invited jobs of the signed in user
     **/
    @Override
    public Observable<List<JobModel>> getInvitedJobsList() {
        Observable<List<JobModel>> observable = Observable.create(emitter -> {
            String uid = FirebaseAuthDB.getInstance().getSignedInUserUID();

            DatabaseReference databaseReference = mFirebaseDatabase.getReference(Constants.FB_USERS)
                    .child(uid)
                    .child(Constants.FB_INVITED_JOBS);

            databaseReference.keepSynced(true);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<JobModel> jobsList = new ArrayList<>();
                    //Log.e("jobsssss", dataSnapshot.toString());

                    long childCount = dataSnapshot.getChildrenCount();
                    long counter = 0;
                    final WrapperString lastKey = new WrapperString();

                    //If the list is empty notify
                    if (childCount == 0) {
                        emitter.onNext(jobsList);
                        return;
                    }

                    //Loop over the job id's and retrieve all the jobs
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String jobId = child.getValue(String.class);
                        counter++;
                        if (childCount == counter) {
                            lastKey.value = jobId;
                        }
                        Log.e("JobID", jobId);

                        DatabaseReference jobDatabaseReference = mFirebaseDatabase.getReference(Constants.FB_JOBS)
                                .child(jobId);

                        jobDatabaseReference.keepSynced(true);

                        jobDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                JobModel job = dataSnapshot.getValue(JobModel.class);
                                job.setJobID(dataSnapshot.getKey());
                                job.setInvited(true);
                                jobsList.add(job);

                                if (dataSnapshot.getKey().equals(lastKey.value)) {
                                    emitter.onNext(jobsList);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                emitter.onError(databaseError.toException());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    emitter.onError(databaseError.toException());
                }
            });
        });

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> setJobArchived(JobModel jobModel, boolean isArchived) {
        Observable<Boolean> observable = Observable.create(emitter -> {
            mFirebaseDatabase.getReference(Constants.FB_JOBS)
                    .child(jobModel.getJobID())
                    .child(Constants.FB_ARCHIVED)
                    .setValue(isArchived)
                    .addOnCompleteListener(task -> {
                        emitter.onNext(task.isSuccessful());
                    });
        });

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Return the list of all call logs of a user
     * that are related to the user's jobs(created and invited)
     *
     * @return Observable<List<CallLogModel>>
     */
    @Override
    public Observable<List<CallLogModel>> getListOfAllCallLogs() {
        Observable<List<CallLogModel>> observable = Observable.create(emitter -> {
            List<JobModel> jobList = new ArrayList<>();
            List<CallLogModel> callLogs = new ArrayList<>();

            getCreatedJobList()
                    .subscribe(jobModels -> {
                        jobList.addAll(jobModels);
                        getInvitedJobsList()
                                .subscribe(invitedJobs -> {
                                    jobList.addAll(invitedJobs);

                                    for (int i = 0; i < jobList.size(); i++) {
                                        final int index = i;
                                        JobModel job = jobList.get(i);

                                        getListOfJobCallLogs(job.getJobID())
                                                .subscribe(callLogModels -> {
                                                    callLogs.addAll(callLogModels);

                                                    if (index == (jobList.size() - 1)) {
                                                        emitter.onNext(callLogs);
                                                    }
                                                });
                                    }
                                });
                    });
        });

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> createReminder(TodoModel todo) {
        Observable<Boolean> observable = Observable.create(emitter -> {
            String uid = FirebaseAuthDB.getInstance().getSignedInUserUID();

            mFirebaseDatabase.getReference(Constants.FB_USERS)
                    .child(uid)
                    .child(Constants.FB_TODOS)
                    .push()
                    .setValue(todo)
                    .addOnCompleteListener(task -> {
                        emitter.onNext(task.isSuccessful());
                    });
        });

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<TodoModel>> getTodoList() {
        Observable<List<TodoModel>> observable = Observable.create(emitter -> {
            String uid = FirebaseAuthDB.getInstance().getSignedInUserUID();

            DatabaseReference databaseReference = mFirebaseDatabase.getReference(Constants.FB_USERS)
                    .child(uid)
                    .child(Constants.FB_TODOS);

            databaseReference.keepSynced(true);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<TodoModel> todoList = new ArrayList<>();


                    for (DataSnapshot child : dataSnapshot.getChildren()) {

                        TodoModel todo = child.getValue(TodoModel.class);
                        todo.setId(child.getKey());
                        Log.e(AppController.TAG, "Response: " +todo.toString());

                        todoList.add(todo);
                    }

                    //Sort the list
                    Collections.sort(todoList, (o1, o2) -> {
                        if (o1.getTime() < o2.getTime()) {
                            return -1;
                        } else if (o1.getTime() > o2.getTime()) {
                            return 1;
                        } else {
                            return 0;
                        }
                    });

                    emitter.onNext(todoList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        });

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> changeTodoCompleted(TodoModel todo) {
        Observable<Boolean> observable = Observable.create(emitter -> {
            String uid = FirebaseAuthDB.getInstance().getSignedInUserUID();

            mFirebaseDatabase.getReference(Constants.FB_USERS)
                    .child(uid)
                    .child(Constants.FB_TODOS)
                    .child(todo.getId())
                    .setValue(todo)
                    .addOnCompleteListener(task -> {
                        emitter.onNext(task.isSuccessful());
                    });
        });

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> createCallLog(CallLogModel callLog) {
        Observable<Boolean> observable = Observable.create(emitter -> {
            String uid = FirebaseAuthDB.getInstance().getSignedInUserUID();
            callLog.setUserUID(uid);


            mFirebaseDatabase.getReference(Constants.FB_CALL_LOGS)
                    .child(callLog.getPhoneNumber())
                    .child(uid)
                    .push()
                    .setValue(callLog)
                    .addOnCompleteListener(task -> {
                        emitter.onNext(task.isSuccessful());
                    });

            mFirebaseDatabase.getReference(Constants.FB_USERS)
                    .child(uid)
                    .child(Constants.FB_JOB_CALL_HISTORY)
                    .child(callLog.getJobID())
                    .push()
                    .setValue(callLog);
        });

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Get the list of call logs related to a number
     *
     * @param number Number to get logs for
     */
    @Override
    public Observable<List<List<CallLogModel>>> getListOfCallLogs(String number) {
        Observable<List<List<CallLogModel>>> observable = Observable.create(emitter -> {
            String uid = FirebaseAuthDB.getInstance().getSignedInUserUID();

            DatabaseReference reference = mFirebaseDatabase.getReference(Constants.FB_CALL_LOGS)
                    .child(number);

            reference.keepSynced(true);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<List<CallLogModel>> callLogsCompleteList = new ArrayList<>();

                    for (DataSnapshot userUID : dataSnapshot.getChildren()) {
                        List<CallLogModel> callLogs = new ArrayList<>();

                        for (DataSnapshot log : userUID.getChildren()) {
                            CallLogModel callLogModel = log.getValue(CallLogModel.class);
                            callLogModel.setUid(userUID.getKey());
                            callLogs.add(callLogModel);
                        }

                        //Sort the list to bring up the recent call number
                        Collections.sort(callLogs, (o1, o2) -> {
                            if (o1.getCallDate() > o2.getCallDate()) {
                                return -1;
                            } else if (o1.getCallDate() < o2.getCallDate()) {
                                return 1;
                            } else {
                                return 0;
                            }
                        });

                        callLogsCompleteList.add(callLogs);
                    }

                    Collections.sort(callLogsCompleteList, (o1, o2) -> {
                        if (o1.get(0).getCallDate() > o2.get(0).getCallDate()) {
                            return -1;
                        } else if (o1.get(0).getCallDate() < o2.get(0).getCallDate()) {
                            return 1;
                        } else {
                            return 0;
                        }
                    });

                    emitter.onNext(callLogsCompleteList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    emitter.onError(databaseError.toException());
                }
            });
        });

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Get the list of all call logs for a particular job and for a particular user
     *
     * @param jobUID ID of the job to fetch the call logs for
     */
    @Override
    public Observable<List<CallLogModel>> getListOfJobCallLogs(String jobUID) {
        Observable<List<CallLogModel>> observable = Observable.create(emitter -> {
            String uid = FirebaseAuthDB.getInstance().getSignedInUserUID();

            DatabaseReference databaseReference = mFirebaseDatabase.getReference(Constants.FB_USERS)
                    .child(uid)
                    .child(Constants.FB_JOB_CALL_HISTORY)
                    .child(jobUID);

            databaseReference.keepSynced(true);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<CallLogModel> callLogs = new ArrayList<>();

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        CallLogModel callLog = child.getValue(CallLogModel.class);
                        callLogs.add(callLog);
                    }

                    //Sort list on basis of time
                    Collections.sort(callLogs, (o1, o2) -> {
                        if (o1.getCallDate() > o2.getCallDate()) {
                            return -1;
                        } else if (o1.getCallDate() < o2.getCallDate()) {
                            return 1;
                        } else {
                            return 0;
                        }
                    });

                    emitter.onNext(callLogs);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        });

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Create new Job from job model and also create new client and skill if required
     *
     * @param job            New Job details to be created
     * @param isClientCustom is custom client provided
     * @param isSkillCustom  is custom skill provided
     */
    @Override
    public Observable<Boolean> createJob(JobModel job, boolean isClientCustom,
                                         boolean isSkillCustom) {
        Observable<Boolean> observable = Observable.create(emitter -> {
            String uid = FirebaseAuthDB.getInstance().getSignedInUserUID();
            job.setCreatorID(uid);

            DatabaseReference databaseReference = mFirebaseDatabase.getReference(Constants.FB_JOBS).push();
            String jobId = databaseReference.getKey();
            databaseReference.setValue(job)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //Add the values in skills and clients is unique
                            if (isClientCustom) {
                                String clientKey = mFirebaseDatabase.getReference(Constants.FB_CLIENTS).push().getKey();
                                mFirebaseDatabase.getReference(Constants.FB_CLIENTS)
                                        .child(clientKey)
                                        .setValue(job.getClientName());
                            }
                            if (isSkillCustom) {
                                String skillKey = mFirebaseDatabase.getReference(Constants.FB_SKILLS).push().getKey();
                                mFirebaseDatabase.getReference(Constants.FB_SKILLS)
                                        .child(skillKey)
                                        .setValue(job.getPrimarySkill());
                            }

                            mFirebaseDatabase.getReference(Constants.FB_USERS)
                                    .child(uid)
                                    .child(Constants.FB_CREATED_JOBS)
                                    .push()
                                    .setValue(jobId);

                            //Add the job to invited emails
                            List<String> invitedEmails = job.getInvitedUserEmails();
                            for (int i = 0; i < invitedEmails.size(); i++) {
                                String email = invitedEmails.get(i);

                                Query query = mFirebaseDatabase.getReference(Constants.FB_USERS)
                                        .orderByChild("email")
                                        .equalTo(email);

                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            String userKey = child.getKey();

                                            //Add the job in invited jobs
                                            mFirebaseDatabase.getReference(Constants.FB_USERS)
                                                    .child(userKey)
                                                    .child(Constants.FB_INVITED_JOBS)
                                                    .push()
                                                    .setValue(jobId);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                            emitter.onNext(true);
                        } else {
                            emitter.onNext(false);
                        }
                    });
        });

        return observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Boolean> setUserImageURL(String imageURL) {
        Observable<Boolean> observable = Observable.create(emitter -> {
            String userID = FirebaseAuthDB.getInstance().getSignedInUserUID();

            mFirebaseDatabase.getReference(Constants.FB_USERS)
                    .child(userID)
                    .child(Constants.FB_IMAGE_URL)
                    .setValue(imageURL)
                    .addOnCompleteListener(task -> {
                        emitter.onNext(task.isSuccessful());
                    });
        });

        return observable.subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<String> getUserImageURL(String userID) {
        Observable<String> observable = Observable.create(emitter -> {
            mFirebaseDatabase.getReference(Constants.FB_USERS)
                    .child(userID)
                    .child(Constants.FB_IMAGE_URL)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String imageUrl = dataSnapshot.getValue(String.class);
                            Log.e("TAG", "Fetched Image URL for" + userID + " : "  + imageUrl);
                            if (imageUrl == null) {
                                emitter.onNext("");
                            } else {
                                emitter.onNext(imageUrl);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            emitter.onError(databaseError.toException());
                        }
                    });
        });

        return observable.subscribeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread());
    }

    class WrapperString {
        String value;
    }

}
