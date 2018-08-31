package app.com.thetechnocafe.hirecall.Features.Candidates;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import app.com.thetechnocafe.hirecall.Enums.NotificationType;
import app.com.thetechnocafe.hirecall.Enums.TodoType;
import app.com.thetechnocafe.hirecall.Models.CandidateModel;
import app.com.thetechnocafe.hirecall.Models.JobModel;
import app.com.thetechnocafe.hirecall.Models.TodoModel;
import app.com.thetechnocafe.hirecall.R;
import app.com.thetechnocafe.hirecall.Shared.Service.CallLogTrackerService;
import app.com.thetechnocafe.hirecall.Utilities.AlarmSchedulerUtility;
import app.com.thetechnocafe.hirecall.Utilities.DateFormatUtility;
import app.com.thetechnocafe.hirecall.Utilities.PhoneCallContactUtility;
import app.com.thetechnocafe.hirecall.Utilities.ShortURL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.ViewHolder> {
    private final List<String> page_sub_filter2 = new ArrayList<>();
    private String sub_filter2 = "";
    private String filtertext = "no";
    private Context mContext;
    String u;
    private static String CURRENT_NUMBER;
    public JobModel SELECTED_JOB = new JobModel();
    public String changedResume;
    private String pStatusText = "";



    JSONObject response = new JSONObject();

    private static final int RC_REQUEST_PERMISSION = 1;
    private ArrayList<CandidateModel> candidateModel = new ArrayList<CandidateModel>();
    private ArrayList<String> candname = new ArrayList<String>();
    List<String> page_sub_filter = new ArrayList<>();
    private Activity activity;
    private int mPosition = 0;
    private String status = "", sub_status = "", filter = "new", filter1 = "no", sub_filter1 = "no", statusText = "no",
            status_check = "no", sub_status_check = "no";
    private static Calendar SELECTED_DATE = Calendar.getInstance();
    public static String[] INTERVIEW_SELECT_OPTIONS;
    public static String[] INTERVIEW_REJECT_OPTIONS;
    public static String[] NO_SHOW_OPTIONS;
    public static String[] NOT_INTERESTED_OPTIONS;
    public static String[] INTERVIEW_CONFIRMED_STAGE;
    public static String[] PREFERRED_COMPANIES;
    public static String[] SKILL_OPTIONS;
    public static String[] DOMAIN_OPTIONS;
    public static String[] ROLE_OPTIONS;


    private JSONObject obj;
    private Calendar calendar = Calendar.getInstance();
    String current_user = "";
    boolean selectall = false;
    private String URL = "http://13.71.116.40:4040/changeCandidateStatus.php";
    ViewHolder temp;
    private String preferred_status = "";
    private String preferred_status_check = "no";

    public CandidateAdapter(String statusT, boolean flag, String s, Context context, JSONObject response1, Activity act) {

        TextView noData = (TextView) act.findViewById(R.id.farzi);
        noData.setVisibility(View.VISIBLE);

        filtertext = s;
        activity = act;
        mContext = context;
        selectall = flag;
        statusText = statusT;
        INTERVIEW_SELECT_OPTIONS = mContext.getResources().getStringArray(R.array.interview_select_options);
        INTERVIEW_REJECT_OPTIONS = mContext.getResources().getStringArray(R.array.interview_reject_options);
        NO_SHOW_OPTIONS = mContext.getResources().getStringArray(R.array.no_show_options);
        NOT_INTERESTED_OPTIONS = mContext.getResources().getStringArray(R.array.not_interested_options);
        INTERVIEW_CONFIRMED_STAGE = mContext.getResources().getStringArray(R.array.confirmation_round);

        PREFERRED_COMPANIES = mContext.getResources().getStringArray(R.array.preferred_companies);
        SKILL_OPTIONS = mContext.getResources().getStringArray(R.array.skill);
        DOMAIN_OPTIONS = mContext.getResources().getStringArray(R.array.domain);
        ROLE_OPTIONS = mContext.getResources().getStringArray(R.array.roles);

        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
        current_user = current.getUid();
        try {

            /***************************************************************************************
             * ********************Spilting the Filter String***************************************
             * ************************************************************************************/

            Log.e("filtertext", filtertext);
            if (filtertext.split(":").length == 3) {
                filter = filtertext.split(":")[0];
                filter1 = filtertext.split(":")[1];
                sub_filter1 = filtertext.split(":")[2];
                for (int i = 0; i < sub_filter1.split("/").length; i++)
                    page_sub_filter.add(sub_filter1.split("/")[i]);


            } else if (filtertext.split(":").length == 4) {
                filter = filtertext.split(":")[0];
                filter1 = filtertext.split(":")[1];
                sub_filter1 = filtertext.split(":")[2];
                for (int i = 0; i < sub_filter1.split("/").length; i++)
                    page_sub_filter.add(sub_filter1.split("/")[i]);
                sub_filter2 = filtertext.split(":")[3];
                for (int i = 0; i < sub_filter2.split("/").length; i++)
                    page_sub_filter2.add(sub_filter2.split("/")[i]);

            } else if (filtertext.split(":").length == 1) {
                filter = filtertext.split(":")[0];
            }

            /***************************************************************************************
             * *************************************************************************************/

            JSONObject value;
            response = response1;

            //   Log.e("response aaya",response.toString());
            //  String jsonArray=response1.getString("added_by");
//            int pos=jsonArray.getJSONObject(0).getInt("position");
            //JSONObject added_by=jsonArray.getJSONObject(0);
            //  int pos=added_by.getInt("position");
            //Log.e("keys",jsonArray);

            Iterator<String> iter = response.keys();
            while (iter.hasNext()) {
                String key = iter.next();

                value = new JSONObject(response.get(key).toString());
                Log.e("name-", value.getString("candidateName"));
                //Log.e("value",value.toString());
                JSONArray jsonArray = value.getJSONArray("added_by");
                JSONObject added_by = jsonArray.getJSONObject(0);
                JSONArray array = added_by.getJSONArray("status");
                obj = (JSONObject) array.get(array.length() - 1);

                if (obj.getString("status").split(":")[0].equalsIgnoreCase("new") && obj.getString("status").split(":")[1].equalsIgnoreCase("Sent - Screening")) {

                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("candidateContact", value.getString("phone_no"));
                        obj.put("Timestamp", new Long(calendar.getTimeInMillis()));
                        obj.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        obj.put("status", "screening:Sent - Screening");
                    } catch (Exception e) {
                        Log.e("screening", "Error");
                        e.printStackTrace();
                    }
                    RequestQueue queue = Volley.newRequestQueue(mContext);
                    JsonObjectRequest req = new JsonObjectRequest(URL, obj,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.e("Response:sent scr", response.toString());

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    queue.add(req);

                } else if (obj.getString("status").split(":")[0].equalsIgnoreCase("new") && obj.getString("status").split(":")[1].equalsIgnoreCase("Interview Confirmed")) {

                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("candidateContact", value.getString("phone_no"));
                        obj1.put("Timestamp", new Long(calendar.getTimeInMillis()));
                        obj1.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        obj1.put("status", "ipending:Pending:" + obj.getString("status").split(":")[2]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    RequestQueue queue = Volley.newRequestQueue(mContext);
                    JsonObjectRequest req = new JsonObjectRequest(URL, obj1,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.e("Response:sent scr", response.toString());

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    queue.add(req);

                } else if (obj.getString("status").split(":")[0].equalsIgnoreCase("screening") && obj.getString("status").split(":")[1].equalsIgnoreCase("Interview Confirmed")) {

                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("candidateContact", value.getString("phone_no"));
                        obj1.put("Timestamp", new Long(calendar.getTimeInMillis()));
                        obj1.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        obj1.put("status", "ipending:Pending:" + obj.getString("status").split(":")[2]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    RequestQueue queue = Volley.newRequestQueue(mContext);
                    JsonObjectRequest req = new JsonObjectRequest(URL, obj1,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.e("Response:i confirmed", response.toString());

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    queue.add(req);
                } else if (obj.getString("status").split(":")[0].equalsIgnoreCase("ipending") && obj.getString("status").split(":")[1].equalsIgnoreCase("Interview Select")) {
                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("candidateContact", value.getString("phone_no"));
                        obj1.put("Timestamp", new Long(calendar.getTimeInMillis()));
                        obj1.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        obj1.put("status", "interviewed:Interview Select:" + obj.getString("status").split(":")[2]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    RequestQueue queue = Volley.newRequestQueue(mContext);
                    JsonObjectRequest req = new JsonObjectRequest(URL, obj1,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.e("Response:i select", response.toString());

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    queue.add(req);
                } else if (obj.getString("status").split(":")[0].equalsIgnoreCase("ipending") && obj.getString("status").split(":")[1].equalsIgnoreCase("Interview Reject")) {
                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("candidateContact", value.getString("phone_no"));
                        obj1.put("Timestamp", new Long(calendar.getTimeInMillis()));
                        obj1.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        obj1.put("status", "interviewed:Interview Reject:" + obj.getString("status").split(":")[2]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    RequestQueue queue = Volley.newRequestQueue(mContext);
                    JsonObjectRequest req = new JsonObjectRequest(URL, obj1,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.e("Response:i reject", response.toString());

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    queue.add(req);
                } else if (obj.getString("status").split(":")[0].equalsIgnoreCase("interviewed") && obj.getString("status").split(":")[1].equalsIgnoreCase("Candidate Accepted – Offer")) {
                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("candidateContact", value.getString("phone_no"));
                        obj1.put("Timestamp", new Long(calendar.getTimeInMillis()));
                        obj1.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        obj1.put("status", "offered:Candidate Accepted – Offer:");// + obj.getString("status").split(":")[2]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    RequestQueue queue = Volley.newRequestQueue(mContext);
                    JsonObjectRequest req = new JsonObjectRequest(URL, obj1,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.e("Response:intr offer", response.toString());

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            error.printStackTrace();
                        }
                    });
                    queue.add(req);
                } else if (obj.getString("status").split(":")[0].equalsIgnoreCase("interviewed") && obj.getString("status").split(":")[1].equalsIgnoreCase("Next Interview")) {
                    JSONObject obj1 = new JSONObject();
                    try {
                        obj1.put("candidateContact", value.getString("phone_no"));
                        obj1.put("Timestamp", new Long(calendar.getTimeInMillis()));
                        obj1.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        obj1.put("status", "ipending:Pending:" + obj.getString("status").split(":")[2]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    RequestQueue queue = Volley.newRequestQueue(mContext);
                    JsonObjectRequest req = new JsonObjectRequest(URL, obj1,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.e("Response:intr offer", response.toString());

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            error.printStackTrace();
                        }
                    });
                    queue.add(req);
                } else if (obj.getString("status").split(":")[0].equalsIgnoreCase("interviewed") && obj.getString("status").split(":")[1].equalsIgnoreCase("Offered")) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("candidateContact", value.getString("phone_no"));
                        obj.put("Timestamp", new Long(calendar.getTimeInMillis()));
                        obj.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        obj.put("status", "offered:Offered");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    RequestQueue queue = Volley.newRequestQueue(mContext);
                    JsonObjectRequest req = new JsonObjectRequest(URL, obj,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.e("Response:intr offer", response.toString());

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            error.printStackTrace();
                        }
                    });
                    queue.add(req);
                } else if (obj.getString("status").split(":")[0].equalsIgnoreCase("offered") && obj.getString("status").split(":")[1].equalsIgnoreCase("Joined")) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("candidateContact", value.getString("phone_no"));
                        obj.put("Timestamp", new Long(calendar.getTimeInMillis()));
                        obj.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        obj.put("status", "joined:Joined");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    RequestQueue queue = Volley.newRequestQueue(mContext);
                    JsonObjectRequest req = new JsonObjectRequest(URL, obj,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.e("Response:joined", response.toString());

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    queue.add(req);
                }
                /***********************************************************************************
                 ********************* Parsing the filter String************************************
                 **********************************************************************************/

                if (filter.equalsIgnoreCase(obj.getString("status").split(":")[0])) {
                    if (filter1.equalsIgnoreCase("no")) {
                        SetValue(value);
                    } else if (filter1.equalsIgnoreCase("status")) {
                        for (int i = 0; i < page_sub_filter.size(); i++) {
                            if (page_sub_filter.get(i).equalsIgnoreCase("Interview select") ||
                                    page_sub_filter.get(i).equalsIgnoreCase("Interview Reject") ||
                                    page_sub_filter.get(i).equalsIgnoreCase("pending")) {
                                for (int j = 0; j < page_sub_filter2.size(); j++)
                                    if (page_sub_filter2.get(j).equalsIgnoreCase(obj.getString("status").split(":")[2]))
                                        SetValue(value);
                            } else if (page_sub_filter.get(i).equalsIgnoreCase(obj.getString("status").split(":")[1])) {
                                SetValue(value);
                            }
                        }
                    } else if (filter1.equalsIgnoreCase("recruiter")) {
                        for (int i = 0; i < page_sub_filter.size(); i++)
                            if (page_sub_filter.get(i).equalsIgnoreCase(added_by.getString("userName")))
                                SetValue(value);

                    } else if (filter1.equalsIgnoreCase("job")) {
                        for (int i = 0; i < page_sub_filter.size(); i++)
                            if (page_sub_filter.get(i).equalsIgnoreCase(added_by.getString("primarySkill"))) {
                                SetValue(value);
                            }
                    }
                }
                /***********************************************************************************
                 * *********************************************************************************
                 * ********************************************************************************/

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycler_resume, parent, false);
        return new ViewHolder(itemView);
    }

    private boolean checkForCallPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG}, RC_REQUEST_PERMISSION);
                return false;
            } else if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG}, RC_REQUEST_PERMISSION);
                return false;
            }
        }

        return true;
    }


    private void startCallLogService(final CandidateFragment.VolleyCallback callback, String jobId) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", jobId);
        RequestQueue queue = Volley.newRequestQueue(mContext);
        JsonObjectRequest req = new JsonObjectRequest("http://13.71.116.40:4040/getJob.php", new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error yahan hai: ", error.getMessage());
            }
        });
        queue.add(req);

    }

    public void stopCallLogService() {

        Intent intent = new Intent(mContext, CallLogTrackerService.class);
        intent.putExtra(CallLogTrackerService.EXTRA_JOB_MODEL, SELECTED_JOB);
        intent.putExtra(CallLogTrackerService.EXTRA_CURRENT_NUMBER, CURRENT_NUMBER);
        mContext.stopService(intent);
    }

    private void SetValue(JSONObject value) {
        try {
            JSONArray jsonArray = value.getJSONArray("added_by");
            JSONObject added_by = jsonArray.getJSONObject(0);
            Log.e("name", value.getString("candidateName"));
            CandidateModel candidateModel1 = new CandidateModel();
            candidateModel1.setUsername(value.getString("candidateName"));
            candidateModel1.setExperience(value.getString("experience"));
            candidateModel1.setCollege(value.getString("college"));
            candidateModel1.setDesignation(value.getString("designation"));
            candidateModel1.setEmployer(added_by.getString("userName"));
            candidateModel1.setCompany(value.getString("employer"));
            candidateModel1.setphoneNumber(value.getString("phone_no"));
            candidateModel1.setStatus(obj.getString("status"));
            candidateModel1.setClientskills(added_by.getString("primarySkill") + " at " + added_by.getString("clientName"));
            candidateModel1.setjobId(added_by.getString("jobID"));
            candidateModel1.setuID(added_by.getString("userID"));
            candidateModel.add(candidateModel1);
            candname.add(value.getString("candidateName"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void makeCall(String CURRENT_NUMBER) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + CURRENT_NUMBER));
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mContext.startActivity(intent);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        temp = holder;

        holder.bindData(position, candidateModel);
        final CandidateModel model = candidateModel.get(position);
        holder.cardview.setBackgroundColor(model.isSelected() ? Color.LTGRAY : Color.WHITE);
        holder.cardview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                model.setSelected(!model.isSelected());
                holder.cardview.setBackgroundColor(model.isSelected() ? Color.LTGRAY : Color.WHITE);

                Button status_button = (Button) activity.findViewById(R.id.status_b);
                if (model.isSelected())
                    status_button.setVisibility(View.VISIBLE);
                else
                    status_button.setVisibility(View.GONE );
                return false;
            }
        });
        if (selectall) {
            for (int i = 0; i < candidateModel.size(); i++) {

                candidateModel.get(i).setSelected(true);
                holder.cardview.setBackgroundColor(model.isSelected() ? Color.LTGRAY : Color.WHITE);
            }
        }

        holder.message_icon.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = "fellowapp.co/invited/index.html?number=" + candidateModel.get(position).getphoneNumber() + "&jobId=" + candidateModel.get(position).getjobId() + "&userId=" + FirebaseAuth.getInstance().getCurrentUser().getUid();

                ShortURL.makeShortUrl(url, new ShortURL.ShortUrlListener() {
                    @Override
                    public void OnFinish(String url) {

                        if (url != null && 0 < url.length()) {
                            Log.e("url", url);
                            u = url.substring(8);
                        } else {
                            Log.e("error", "error");
                        }

                    }
                });


                String phoneNo = candidateModel.get(position).getphoneNumber();
                String primarySkill = candidateModel.get(position).getClientskills();
                String message = "Hey" + candname.get(position) + "," + candidateModel.get(position).getEmployer() + " here. Reaching out to you for " + primarySkill.substring(0, primarySkill.indexOf("at")) + "opening. Click here to chat directly: " + u;
                String sms = message;


                LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.popup_message, null);
                final PopupWindow popupWindow = new PopupWindow(
                        popupView, RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);

                Button btnDismiss = (Button) popupView.findViewById(R.id.dismiss);
                Button btnDismiss2 = (Button) popupView.findViewById(R.id.dismiss2);
                TextView textView = (TextView) popupView.findViewById(R.id.text);
                textView.setText(message);

                LinearLayout mTimePickerLayout = (LinearLayout) popupView.findViewById(R.id.time_picker_layout);


                btnDismiss.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            Log.e("number", phoneNo);
                            Log.e("sms", sms);
                            smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                            Toast.makeText(v.getContext(), "SMS Sent!",
                                    Toast.LENGTH_LONG).show();


                        } catch (Exception e) {
                            Toast.makeText(v.getContext(),
                                    "SMS faild, please try again later!",
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        popupWindow.dismiss();

                    }
                });


                btnDismiss2.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mTimePickerLayout.setVisibility(View.INVISIBLE);
                        popupWindow.dismiss();
                    }
                });


                // popupWindow.showAsDropDown(holder.edit_icon, 50, -30);
                popupWindow.showAtLocation(holder.message_icon, Gravity.CENTER, 0, 0);


            }
        });
        holder.call_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkForCallPermissions()) {

                    startCallLogService(new CandidateFragment.VolleyCallback() {
                        @Override
                        public void onSuccess(JSONObject resp) {

                            CURRENT_NUMBER = model.getphoneNumber();


                            Gson gson = new Gson();
                            SELECTED_JOB = gson.fromJson(resp.toString(), JobModel.class);

                            //
                            // SELECTED_JOB.setJobID(jobId);
                            SELECTED_JOB.setJobID(model.getjobId());
                            Log.e("ye raha", SELECTED_JOB.getJobID());


                            Intent intent = new Intent(mContext, CallLogTrackerService.class);
                            intent.putExtra(CallLogTrackerService.EXTRA_JOB_MODEL, SELECTED_JOB);
                            intent.putExtra(CallLogTrackerService.EXTRA_CURRENT_NUMBER, CURRENT_NUMBER);
                            mContext.startService(intent);

                        }
                    }, model.getjobId());

                    makeCall(model.getphoneNumber());
                }
            }
        });


        holder.add_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.popup, null);
                final PopupWindow popupWindow = new PopupWindow(
                        popupView, RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                popupWindow.setFocusable(true);


                Button btnDismiss = (Button) popupView.findViewById(R.id.dismiss);
                Button btnDismiss2 = (Button) popupView.findViewById(R.id.dismiss2);

                Spinner popupSpinner = (Spinner) popupView.findViewById(R.id.popupspinner);
                Spinner popupSpinner2 = (Spinner) popupView.findViewById(R.id.popupspinner2);
                Spinner popUpSpinner3 = (Spinner) popupView.findViewById(R.id.pop_up_spinner_3);

                TextInputLayout tilPopupPreferredLocation = (TextInputLayout) popupView.findViewById(R.id.til_popup_preferred_location);
                TextInputEditText etPopupPreferredLocation = (TextInputEditText) popupView.findViewById(R.id.et_popup_preferred_location);

                LinearLayout mTimePickerLayout = (LinearLayout) popupView.findViewById(R.id.time_picker_layout);
                Button mTimePickerButton = (Button) popupView.findViewById(R.id.time_picker_button);
                Button mDatePickerButton = (Button) popupView.findViewById(R.id.date_picker_button);
                ArrayList<String> data1 = CandidateFragment.status_data;

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, data1);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                popupSpinner.setAdapter(adapter);
                popupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        status = data1.get(position);
                        status_check = data1.get(0);
                        pStatusText = data1.get(position);

                        if (status.equalsIgnoreCase("Interview Confirmed") || status.equalsIgnoreCase("Candidate Accepted – Offer") || status.equalsIgnoreCase("Reschedule by Client") || status.equalsIgnoreCase("Reschedule by Candidate") || status.equalsIgnoreCase("Next Interview") || status.equalsIgnoreCase("Offered") || status.equalsIgnoreCase("Interested But Unavailable") || status.equalsIgnoreCase("DOJ Revised")) {
                            mTimePickerLayout.setVisibility(View.VISIBLE);

                        }
                        mPosition = position;
                        if (filter.equalsIgnoreCase("ipending")) {
                            if (position == 2) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, INTERVIEW_SELECT_OPTIONS);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                popupSpinner2.setAdapter(adapter);
                                popupSpinner2.setVisibility(View.VISIBLE);
                            } else if (position == 3) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, INTERVIEW_REJECT_OPTIONS);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                popupSpinner2.setAdapter(adapter);
                                popupSpinner2.setVisibility(View.VISIBLE);

                            } else if (position == 4) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, NO_SHOW_OPTIONS);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                popupSpinner2.setAdapter(adapter);
                                popupSpinner2.setVisibility(View.VISIBLE);

                            } else if (position == 7) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, NOT_INTERESTED_OPTIONS);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                popupSpinner2.setAdapter(adapter);
                                popupSpinner2.setVisibility(View.VISIBLE);
                            } else
                                popupSpinner2.setVisibility(View.GONE);
                        } else if (filter.equalsIgnoreCase("interviewed")) {
                            if (position == 3) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, INTERVIEW_CONFIRMED_STAGE);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                popupSpinner2.setAdapter(adapter);
                                popupSpinner2.setVisibility(View.VISIBLE);
                            } else if (position == 5) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, NOT_INTERESTED_OPTIONS);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                popupSpinner2.setAdapter(adapter);
                                popupSpinner2.setVisibility(View.VISIBLE);
                            } else
                                popupSpinner2.setVisibility(View.GONE);
                        } else if (filter.equalsIgnoreCase("new")) {
                            if (position == 2) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, NOT_INTERESTED_OPTIONS);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                popupSpinner2.setAdapter(adapter);
                                popupSpinner2.setVisibility(View.VISIBLE);
                            } else if (position == 6) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, INTERVIEW_CONFIRMED_STAGE);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                popupSpinner2.setAdapter(adapter);
                                popupSpinner2.setVisibility(View.VISIBLE);
                            } else
                                popupSpinner2.setVisibility(View.GONE);
                        } else if (filter.equalsIgnoreCase("screening")) {
                            if (position == 4) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, NOT_INTERESTED_OPTIONS);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                popupSpinner2.setAdapter(adapter);
                                popupSpinner2.setVisibility(View.VISIBLE);
                            } else if (position == 7) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, INTERVIEW_CONFIRMED_STAGE);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                popupSpinner2.setAdapter(adapter);
                                popupSpinner2.setVisibility(View.VISIBLE);
                            } else
                                popupSpinner2.setVisibility(View.GONE);
                        } else if (filter.equalsIgnoreCase("offered")) {
                            if (position == 3) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, NOT_INTERESTED_OPTIONS);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                popupSpinner2.setAdapter(adapter);
                                popupSpinner2.setVisibility(View.VISIBLE);
                            } else
                                popupSpinner2.setVisibility(View.GONE);
                        } else
                            popupSpinner2.setVisibility(View.GONE);
//                        changedResume = status;

                        popupSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (status.equalsIgnoreCase("Interview Select")) {
                                    sub_status = INTERVIEW_SELECT_OPTIONS[position];
                                    sub_status_check = INTERVIEW_SELECT_OPTIONS[0];
                                    pStatusText = status + ":" + sub_status;
                                } else if (status.equalsIgnoreCase("Interview Reject")) {
                                    sub_status = INTERVIEW_REJECT_OPTIONS[position];
                                    sub_status_check = INTERVIEW_REJECT_OPTIONS[0];
                                    pStatusText = status + ":" + sub_status;

                                } else if (status.equalsIgnoreCase("NO show")) {
                                    sub_status = NO_SHOW_OPTIONS[position];
                                    sub_status_check = NO_SHOW_OPTIONS[0];
                                    pStatusText = status + ":" + sub_status;

                                } else if (status.equalsIgnoreCase("Not Interested")) {
                                    sub_status = NOT_INTERESTED_OPTIONS[position];
                                    sub_status_check = NOT_INTERESTED_OPTIONS[0];
                                    pStatusText = status + ":" + sub_status;

                                } else if (status.equalsIgnoreCase("Next Interview")) {
                                    sub_status = INTERVIEW_CONFIRMED_STAGE[position];
                                    sub_status_check = INTERVIEW_CONFIRMED_STAGE[0];
                                    pStatusText = status + ":" + sub_status;

                                } else if (status.equalsIgnoreCase("Interview Confirmed")) {
                                    sub_status = INTERVIEW_CONFIRMED_STAGE[position];
                                    sub_status_check = INTERVIEW_CONFIRMED_STAGE[0];
                                    pStatusText = status + ":" + sub_status;

                                } else
                                    sub_status = "no";

                                if (sub_status.equalsIgnoreCase("Skill Mismatch")) {
                                    popUpSpinner3.setVisibility(View.VISIBLE);
                                    tilPopupPreferredLocation.setVisibility(View.GONE);
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                                            android.R.layout.simple_spinner_dropdown_item, SKILL_OPTIONS);
                                    popUpSpinner3.setAdapter(adapter);


                                } else if (sub_status.equalsIgnoreCase("Location Mismatch")) {
                                    tilPopupPreferredLocation.setVisibility(View.VISIBLE);
                                    popUpSpinner3.setVisibility(View.GONE);

                                } else if (sub_status.equalsIgnoreCase("Company Mismatch")) {
                                    tilPopupPreferredLocation.setVisibility(View.GONE);
                                    popUpSpinner3.setVisibility(View.VISIBLE);
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                                            android.R.layout.simple_list_item_1, PREFERRED_COMPANIES);
                                    popUpSpinner3.setAdapter(adapter);

                                } else if (sub_status.equalsIgnoreCase("Domain Mismatch")) {
                                    tilPopupPreferredLocation.setVisibility(View.GONE);
                                    popUpSpinner3.setVisibility(View.VISIBLE);
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                                            android.R.layout.simple_spinner_dropdown_item, DOMAIN_OPTIONS);
                                    popUpSpinner3.setAdapter(adapter);

                                } else if (sub_status.equalsIgnoreCase("Role Mismatch")) {
                                    popUpSpinner3.setVisibility(View.VISIBLE);
                                    tilPopupPreferredLocation.setVisibility(View.GONE);
                                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                                            android.R.layout.simple_spinner_dropdown_item, ROLE_OPTIONS);

                                    popUpSpinner3.setAdapter(adapter);

                                }

                                popUpSpinner3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        if (sub_status.equalsIgnoreCase("Skill Mismatch")) {
                                            preferred_status = SKILL_OPTIONS[i];
                                            preferred_status_check = SKILL_OPTIONS[0];
                                            pStatusText = status + ":" + sub_status + ":" + preferred_status;


                                        } else if (sub_status.equalsIgnoreCase("Domain Mismatch")) {
                                            preferred_status = DOMAIN_OPTIONS[i];
                                            preferred_status_check = DOMAIN_OPTIONS[0];
                                            pStatusText = status + ":" + sub_status + ":" + preferred_status;

                                        } else if (sub_status.equalsIgnoreCase("Role Mismatch")) {
                                            preferred_status = ROLE_OPTIONS[i];
                                            preferred_status_check = ROLE_OPTIONS[0];
                                            pStatusText = status + ":" + sub_status + ":" + preferred_status;

                                        } else if (sub_status.equalsIgnoreCase("Company Mismatch")) {
                                            preferred_status = PREFERRED_COMPANIES[i];
                                            preferred_status_check = PREFERRED_COMPANIES[0];
                                            pStatusText = status + ":" + sub_status + ":" + preferred_status;

                                        }

                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });


                           /*     if (sub_status.equalsIgnoreCase("no"))
                                    pStatusText = status;
                                else if(preferred_status.equalsIgnoreCase(""))
                                    pStatusText = status + ":" + sub_status;
                                else
                                    pStatusText = status + ":" + sub_status + ":" + preferred_status;*/

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                mDatePickerButton.setOnClickListener(view -> {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, (view1, year, month, dayOfMonth) -> {
                        SELECTED_DATE.set(Calendar.YEAR, year);
                        SELECTED_DATE.set(Calendar.MONTH, month);
                        SELECTED_DATE.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        int m = month + 1;
                        mDatePickerButton.setText(dayOfMonth + "-" + m + "-" + year);
                    }, SELECTED_DATE.get(Calendar.YEAR), SELECTED_DATE.get(Calendar.MONTH), SELECTED_DATE.get(Calendar.DAY_OF_MONTH));
                    datePickerDialog.show();
                });

                mTimePickerButton.setOnClickListener(view -> {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, (view1, hourOfDay, minute) -> {
                        SELECTED_DATE.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        SELECTED_DATE.set(Calendar.MINUTE, minute);

                        int hourToDisplay = SELECTED_DATE.get(Calendar.HOUR_OF_DAY) % 12;
                        int minuteToDisplay = SELECTED_DATE.get(Calendar.MINUTE);
                        String am_pm = SELECTED_DATE.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";

                        mTimePickerButton.setText(hourToDisplay + " : " + minuteToDisplay + " " + am_pm);
                    }, SELECTED_DATE.get(Calendar.HOUR_OF_DAY), SELECTED_DATE.get(Calendar.MINUTE), false);
                    timePickerDialog.show();
                });

                btnDismiss.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (sub_status.equalsIgnoreCase("Location Mismatch")) {
                            preferred_status = etPopupPreferredLocation.getText().toString();
                            pStatusText = status + ":" + sub_status + ":" + preferred_status;
                        }

                        if (status.equalsIgnoreCase(status_check)) {
                            Toast.makeText(mContext, "Please Select Status", Toast.LENGTH_SHORT).show();

                        } else if (sub_status.equalsIgnoreCase(sub_status_check)) {
                            Toast.makeText(mContext, "Please Select Sub Status", Toast.LENGTH_SHORT).show();

                        } else if (preferred_status.equalsIgnoreCase(preferred_status_check)) {
                            Toast.makeText(mContext, "please select the option", Toast.LENGTH_SHORT).show();
                        } else {
//                            setData(filtertext.split(":")[0] + pStatusText);


                            popupWindow.dismiss();
                            mTimePickerLayout.setVisibility(View.INVISIBLE);

                            calendar.setTimeInMillis(new Date().getTime());
                            JSONObject obj = new JSONObject();
                            try {
                                obj.put("candidateContact", model.getphoneNumber());
                                obj.put("Timestamp", new Long(calendar.getTimeInMillis()));
                                obj.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                obj.put("status", filtertext.split(":")[0] + ":" + pStatusText);

                                if (pStatusText.split(":").length == 2 || pStatusText.split(":").length == 3)
                                    holder.resume_status.setText(pStatusText.split(":")[0] + "-" + pStatusText.split(":")[1]);
                                else
                                    holder.resume_status.setText(pStatusText.split(":")[0]);

                                Log.e("object", obj.toString());
                                RequestQueue queue = Volley.newRequestQueue(mContext);
                                JsonObjectRequest req = new JsonObjectRequest(URL, obj,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                Log.e("Response:", response.toString());

                                            }
                                        }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("Error ye bhi hai: ", error.getMessage());
                                    }
                                });
                                queue.add(req);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            TodoModel todo = new TodoModel();


                            if (status.equalsIgnoreCase("Reschedule by Client") || status.equalsIgnoreCase("Reschedule by Candidate") || status.equalsIgnoreCase("Interested But Unavailable")) {
                                String name = PhoneCallContactUtility.getInstance().covertNumberToName(mContext, model.getphoneNumber());
                                String time = DateFormatUtility.getInstance().convertDateAndTimeToString(SELECTED_DATE.getTime().getTime());

                                todo.setNumber(model.getphoneNumber());
                                todo.setTitle("Kindly Call Now");
                                todo.setDescription(name + " - " + model.getClientskills());
                                todo.setTime(SELECTED_DATE.getTime().getTime());
                                todo.setCompleted(false);
                                todo.setTodoType(TodoType.CALL_LATER);

                                FirebaseDB.getInstance()
                                        .createReminder(todo)
                                        .subscribe(todoResult -> {

                                            AlarmSchedulerUtility.getInstance()
                                                    .scheduleReminder(
                                                            mContext,
                                                            todo.getTitle(),
                                                            todo.getDescription(),
                                                            todo.getTime(),
                                                            (int) (Math.random() * 10000),
                                                            todo.getTodoType(),
                                                            NotificationType.DEFAULT
                                                    );
                                        });


                            } else if (status.equalsIgnoreCase("Interview Confirmed") || status.equalsIgnoreCase("Candidate Accepted – Offer") || status.equalsIgnoreCase("Next Interview") || status.equalsIgnoreCase("Offered") || status.equalsIgnoreCase("DOJ Revised")) {

                                String name = PhoneCallContactUtility.getInstance().covertNumberToName(mContext, model.getphoneNumber());
                                todo.setNumber(model.getphoneNumber());
                                todo.setTitle(changedResume);
                                todo.setDescription(name + " - " + model.getClientskills());
                                todo.setTime(SELECTED_DATE.getTime().getTime());
                                todo.setTodoType(TodoType.INTERVIEW);

                                FirebaseDB.getInstance()
                                        .createReminder(todo)
                                        .subscribe(todoResult -> {

                                            AlarmSchedulerUtility.getInstance()
                                                    .scheduleReminderForInterview(
                                                            mContext,
                                                            todo.getTitle(),
                                                            todo.getDescription(),
                                                            todo.getTime()
                                                    );

                                        });

                            }
                        }
                    }


                });


                btnDismiss2.setOnClickListener(new Button.OnClickListener()

                {

                    @Override
                    public void onClick(View v) {
                        mTimePickerLayout.setVisibility(View.INVISIBLE);
                        popupWindow.dismiss();
                    }
                });


                // popupWindow.showAsDropDown(holder.edit_icon, 50, -30);
                popupWindow.showAtLocation(holder.add_icon, Gravity.CENTER, 0, 0);


            }
        });
    }

    @Override
    public int getItemCount() {
        return candname == null ? 0 : candname.size();
    }

    public ArrayList<String> getData() {
        Set<String> recruiters = new HashSet<String>();
        for (int i = 0; i < candidateModel.size(); i++) {
            recruiters.add(candidateModel.get(i).getEmployer());
        }
        ArrayList<String> rec = new ArrayList<String>();
        rec.addAll(recruiters);
        return rec;
    }

    public void setData() {
        for (int i = 0; i < candidateModel.size(); i++) {
            if (candidateModel.get(i).isSelected()) {
                String url = "fellowapp.co/invited/index.html?number=" + candidateModel.get(i).getphoneNumber() + "&jobId=" + candidateModel.get(i).getjobId() + "&userId=" + FirebaseAuth.getInstance().getCurrentUser().getUid();

                ShortURL.makeShortUrl(url, new ShortURL.ShortUrlListener() {
                    @Override
                    public void OnFinish(String url) {

                        if (url != null && 0 < url.length()) {
                            Log.e("url", url);
                            u = url.substring(8);
                        } else {
                            Log.e("error", "error");
                        }

                    }
                });


                String phoneNo = candidateModel.get(i).getphoneNumber();
                String primarySkill = candidateModel.get(i).getClientskills();
                String message = "Hey" + candname.get(i) + "," + candidateModel.get(i).getEmployer() + " here. Reaching out to you for " + primarySkill.substring(0, primarySkill.indexOf("at")) + "opening. Click here to chat directly: " + u;
                String sms = message;


                LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.popup_message, null);
                final PopupWindow popupWindow = new PopupWindow(
                        popupView, RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);

                Button btnDismiss = (Button) popupView.findViewById(R.id.dismiss);
                Button btnDismiss2 = (Button) popupView.findViewById(R.id.dismiss2);
                TextView textView = (TextView) popupView.findViewById(R.id.text);
                textView.setText(message);

                LinearLayout mTimePickerLayout = (LinearLayout) popupView.findViewById(R.id.time_picker_layout);


                btnDismiss.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            Log.e("number", phoneNo);
                            Log.e("sms", sms);
                            smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                            Toast.makeText(v.getContext(), "SMS Sent!",
                                    Toast.LENGTH_LONG).show();


                        } catch (Exception e) {
                            Toast.makeText(v.getContext(),
                                    "SMS faild, please try again later!",
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                        popupWindow.dismiss();

                    }
                });


                btnDismiss2.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mTimePickerLayout.setVisibility(View.INVISIBLE);
                        popupWindow.dismiss();
                    }
                });
                popupWindow.showAtLocation(temp.message_icon, Gravity.CENTER, 0, 0);


            }
        }

    }

    public void setData(String status) {
        for (int i = 0; i < candidateModel.size(); i++) {
            if (candidateModel.get(i).isSelected()) {
                Log.e("username", candidateModel.get(i).getUsername());
                JSONObject obj = new JSONObject();
                try {
                    obj.put("candidateContact", candidateModel.get(i).getphoneNumber());
                    obj.put("Timestamp", new Long(calendar.getTimeInMillis()));
                    obj.put("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    obj.put("status", status);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                RequestQueue queue = Volley.newRequestQueue(mContext);
                JsonObjectRequest req = new JsonObjectRequest(URL, obj,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.e("Response:", response.toString());

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                queue.add(req);
            }
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_view_resume)
        CardView cardview;
        @BindView(R.id.mName_text_view)
        TextView name;
        @BindView(R.id.mExperience_text_view)
        TextView exp;
        @BindView(R.id.mCompany_text_view)
        TextView company;
        @BindView(R.id.mUniversity_text_view)
        TextView college;
        @BindView(R.id.mPosition_text_view)
        TextView job;
        @BindView(R.id.mPhone_text_view)
        TextView phoneNo;
        @BindView(R.id.clientskills)
        TextView clientskill;
        @BindView(R.id.recruiter)
        TextView recruiter;
        @BindView(R.id.resume_type)
        TextView resume_type;
        @BindView(R.id.resume_status)
        TextView resume_status;
        @BindView(R.id.add_image)
        ImageView add_icon;
        @BindView(R.id.message_icon)
        ImageView message_icon;
        @BindView(R.id.call_icon)
        ImageView call_icon;


        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindData(int position, ArrayList<CandidateModel> candidateModel) {

            if (candidateModel.size() != 0) {
                TextView noData = (TextView) activity.findViewById(R.id.farzi);
                noData.setVisibility(View.GONE);
            }

            name.setText(candidateModel.get(position).getUsername());
            exp.setText(" | " + candidateModel.get(position).getExperience() + "Yrs");
            college.setText(candidateModel.get(position).getCollege());
            company.setText(candidateModel.get(position).getCompany());
            job.setText(candidateModel.get(position).getDesignation());
            phoneNo.setText(candidateModel.get(position).getphoneNumber());
            if (candidateModel.get(position).getStatus().split(":").length == 3)
                resume_status.setText(candidateModel.get(position).getStatus().split(":")[1] + ":" + candidateModel.get(position).getStatus().split(":")[2]);
            else
                resume_status.setText(candidateModel.get(position).getStatus().split(":")[1]);
            Log.e(candidateModel.get(position).getuID(), current_user);
            if (current_user.equalsIgnoreCase(candidateModel.get(position).getuID()))
                recruiter.setText("You");
            else
                recruiter.setText(candidateModel.get(position).getEmployer());
            clientskill.setText(candidateModel.get(position).getClientskills());

        }
    }


}
