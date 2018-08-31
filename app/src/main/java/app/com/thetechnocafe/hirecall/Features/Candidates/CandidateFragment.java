package app.com.thetechnocafe.hirecall.Features.Candidates;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.Models.CandidateSubFilterModel;
import app.com.thetechnocafe.hirecall.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.ceryle.segmentedbutton.SegmentedButtonGroup;

import static app.com.thetechnocafe.hirecall.Features.Candidates.CandidateAdapter.INTERVIEW_CONFIRMED_STAGE;
import static app.com.thetechnocafe.hirecall.Features.Candidates.CandidateAdapter.NOT_INTERESTED_OPTIONS;

public class CandidateFragment extends Fragment implements CandidatesContract.View {

    private CandidatesContract.Presenter mPresenter;
    private Handler mHandler;
    @BindView(R.id.recycler_view_candidates)
    RecyclerView Candidate_recycler_view;
    @BindView(R.id.new_filter_candidate)
    Button new_filter;
    @BindView(R.id.screening_filter_candidate)
    Button screening_filter;
    @BindView(R.id.ipending_filter_candidate)
    Button ipending_filter;
    @BindView(R.id.interviewed_filter_candidate)
    Button interviewed_filter;
    @BindView(R.id.offered_filter_candidate)
    Button offered_filter;
    @BindView(R.id.joined_filter_candidate)
    Button joined_filter;
    @BindView(R.id.cand_filter)
    Spinner cand_filter;
    @BindView(R.id.cand_subfilter)
    Spinner cand_subfilter;
    @BindView(R.id.cand_status)
    Spinner cand_status;
    @BindView(R.id.cand_substatus)
    Spinner cand_substatus;
    @BindView(R.id.filter_b)
    Button filter;
    @BindView(R.id.status_b)
    Button status;
    @BindView(R.id.setfilter)
    Button setfilter;
    @BindView(R.id.setstatus)
    Button setstatus;
    @BindView(R.id.multiple_message_icon)
    ImageView multiple_message_icon;
    @BindView(R.id.farzi)
    TextView farzi;
    @BindView(R.id.selectall_check_box)
    CheckBox select_all;
    @BindView(R.id.more_filters)
    Button more;
    @BindView(R.id.less_filters)
    Button less;
    @BindView(R.id.filters1)
    LinearLayout filter1;
    @BindView(R.id.filters2)
    LinearLayout filter2;

    /******************************************************************************************************************************
     ************************************************Edited By Ravi **************************************************************
     * *****************************************************************************************************************************/

    @BindView(R.id.iv_candidate_filter)
    ImageView ivCandidateFilter;
    SegmentedButtonGroup sbgCandidateFilter;
    ListView lvCandidateSubFilter;
    Button btApplyFilter;

    /*******************************************************************************************************************************
     * *****************************************************************************************************************************
     * *****************************************************************************************************************************/

    private CandidateAdapter candidateAdapter;
    ArrayList<String> data1 = new ArrayList<>();
    ArrayList<CandidateSubFilterModel> data2, recruiters2, jobs2;
    private String filtertext = "new", page_filter = "no", page_sub_filter = "no", mStatus = "no", mSubstatus = "no", statusText = "no";
    private String page_filter_1 = "no";
    private String URL_FEED = "http://13.71.116.40:4040/naukariGetCandidate.php";
    private static String[] INTERVIEW_SELECT_OPTIONS;
    private static String[] INTERVIEW_REJECT_OPTIONS;
    private static String[] NO_SHOW_OPTIONS;
    private ArrayList<String> jobs = new ArrayList<String>();
    private ArrayList<String> clients = new ArrayList<String>();
    int val = 0, pos = 0;
    boolean flag = false;
    JSONObject result = new JSONObject();
    CandidateSubFilterAdapter adapter;
    public static ArrayList<String> status_data;
    private String preferredSubStatus="";

    public interface VolleyCallback {
        void onSuccess(JSONObject resp);
    }

    //Instance method
    public static CandidateFragment getInstance() {
        return new CandidateFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_candidate, container, false);
        ButterKnife.bind(this, view);
        mHandler = new Handler(Looper.getMainLooper());
        mPresenter = new Candidates_Presenter();
        mPresenter.subscribe(this);
        INTERVIEW_SELECT_OPTIONS = getResources().getStringArray(R.array.interview_select_options);
        INTERVIEW_REJECT_OPTIONS = getResources().getStringArray(R.array.interview_reject_options);
        NO_SHOW_OPTIONS = getResources().getStringArray(R.array.no_show_options);
        initViews("new");

        /***************************************************************************************************************************
         * ******************************************Multiple filters ***************************************************************
         * ************************************************************************************************************************/

        ivCandidateFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_candidate_filter);
                dialog.setCancelable(true);

                lvCandidateSubFilter = (ListView) dialog.findViewById(R.id.lv_candidate_sub_filter);
                btApplyFilter = (Button) dialog.findViewById(R.id.bt_candidate_apply_filter);

                sbgCandidateFilter = (SegmentedButtonGroup) dialog.findViewById(R.id.sbg_candidate_filter);
                sbgCandidateFilter.setPosition(0, 0);

                sbgCandidateFilter.setOnClickedButtonPosition(new SegmentedButtonGroup.OnClickedButtonPosition() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClickedButtonPosition(int position) {
                        if (position == 0) {
                            data2 = new ArrayList<CandidateSubFilterModel>();
                            for (int i = 1; i < data1.size(); i++)
                                data2.add(new CandidateSubFilterModel(data1.get(i), false));
                            adapter = new CandidateSubFilterAdapter(getActivity(), R.layout.item_list_view_candidate_sub_filter, data2);
                            lvCandidateSubFilter.setAdapter(adapter);
                            page_filter_1 = "status";

                        } else if (position == 1) {
                            ArrayList<String> recruiters = candidateAdapter.getData();
                            recruiters2 = new ArrayList<CandidateSubFilterModel>();
                            for (int i = 0; i < recruiters.size(); i++)
                                recruiters2.add(new CandidateSubFilterModel(recruiters.get(i), false));
                            adapter = new CandidateSubFilterAdapter(getActivity(), R.layout.item_list_view_candidate_sub_filter, recruiters2);
                            lvCandidateSubFilter.setAdapter(adapter);
                            page_filter_1 = "Recruiter";

                        } else if (position == 2) {
                            jobs2 = new ArrayList<CandidateSubFilterModel>();
                            for (int i = 0; i < jobs.size(); i++)
                                jobs2.add(new CandidateSubFilterModel(jobs.get(i), false));
                            adapter = new CandidateSubFilterAdapter(getActivity(), R.layout.item_list_view_candidate_sub_filter, jobs2);
                            lvCandidateSubFilter.setAdapter(adapter);
                            page_filter_1 = "Job";

                        }
                    }
                });

                btApplyFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        List<String> checkedValue = adapter.getSubFilter();
                        ArrayList<String> popSubFilters = adapter.getPopSubFilters();

                        String page_sub_filter_2 = "no";
                        String page_sub_filter_3 = "no";

                        if (checkedValue.size() != 0)
                            for (int i = 0; i < checkedValue.size(); i++)
                                page_sub_filter_2 = page_sub_filter_2 + "/" + checkedValue.get(i);
                        if (popSubFilters.size() != 0)
                            for (int i = 0; i < popSubFilters.size(); i++)
                                page_sub_filter_3 = page_sub_filter_3 + "/" + popSubFilters.get(i);
                        initViews(filtertext + ":" + page_filter_1 + ":" + page_sub_filter_2 + ":" + page_sub_filter_3);
                        dialog.dismiss();

                    }
                });
                dialog.show();
            }
        });

        /*************************************************************************************************************************
         * ***********************************************************************************************************************
         * ***********************************************************************************************************************/

        new_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_filter.setTextColor(Color.BLUE);
                screening_filter.setTextColor(Color.BLACK);
                ipending_filter.setTextColor(Color.BLACK);
                interviewed_filter.setTextColor(Color.BLACK);
                offered_filter.setTextColor(Color.BLACK);
                joined_filter.setTextColor(Color.BLACK);
                filtertext = "new";
                initViews("new");
            }
        });
        screening_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_filter.setTextColor(Color.BLACK);
                screening_filter.setTextColor(Color.BLUE);
                ipending_filter.setTextColor(Color.BLACK);
                interviewed_filter.setTextColor(Color.BLACK);
                offered_filter.setTextColor(Color.BLACK);
                joined_filter.setTextColor(Color.BLACK);
                filtertext = "screening";
                initViews("screening");
            }
        });
        ipending_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_filter.setTextColor(Color.BLACK);
                screening_filter.setTextColor(Color.BLACK);
                ipending_filter.setTextColor(Color.BLUE);
                interviewed_filter.setTextColor(Color.BLACK);
                offered_filter.setTextColor(Color.BLACK);
                joined_filter.setTextColor(Color.BLACK);
                filtertext = "ipending";
                initViews("ipending");
            }
        });
        interviewed_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_filter.setTextColor(Color.BLACK);
                screening_filter.setTextColor(Color.BLACK);
                ipending_filter.setTextColor(Color.BLACK);
                interviewed_filter.setTextColor(Color.BLUE);
                offered_filter.setTextColor(Color.BLACK);
                joined_filter.setTextColor(Color.BLACK);
                filtertext = "interviewed";
                initViews("interviewed");
            }
        });
        offered_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_filter.setTextColor(Color.BLACK);
                screening_filter.setTextColor(Color.BLACK);
                ipending_filter.setTextColor(Color.BLACK);
                interviewed_filter.setTextColor(Color.BLACK);
                offered_filter.setTextColor(Color.BLUE);
                joined_filter.setTextColor(Color.BLACK);
                filtertext = "offered";
                initViews("offered");
            }
        });
        joined_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_filter.setTextColor(Color.BLACK);
                screening_filter.setTextColor(Color.BLACK);
                ipending_filter.setTextColor(Color.BLACK);
                interviewed_filter.setTextColor(Color.BLACK);
                offered_filter.setTextColor(Color.BLACK);
                joined_filter.setTextColor(Color.BLUE);
                filtertext = "joined";
                initViews("joined");
            }
        });

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* cand_filter.setVisibility(View.VISIBLE);
                filter.setVisibility(View.GONE);
                status.setVisibility(View.GONE);*/

            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter1.setVisibility(View.GONE);
                filter2.setVisibility(View.VISIBLE);
            }
        });
        less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filter2.setVisibility(View.GONE);
                filter1.setVisibility(View.VISIBLE);
            }
        });
        setval(filtertext);
        cand_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setfilter.setVisibility(View.VISIBLE);
                if (position == 1) {
                    page_filter = "Status";
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getAppContext(), android.R.layout.simple_spinner_item, data1);
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    cand_subfilter.setAdapter(adapter1);

                } else if (position == 2) {
                    page_filter = "Recruiter";
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getAppContext(), android.R.layout.simple_spinner_item, candidateAdapter.getData());
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    cand_subfilter.setAdapter(adapter1);
                } else if (position == 3) {
                    page_filter = "Job";
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getAppContext(), android.R.layout.simple_spinner_item, jobs);
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    cand_subfilter.setAdapter(adapter1);
                }
                if (position == 1 || position == 2 || position == 3)
                    cand_subfilter.setVisibility(View.VISIBLE);
                else
                    cand_subfilter.setVisibility(View.GONE);

                cand_subfilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (page_filter.equalsIgnoreCase("Status"))
                            page_sub_filter = data1.get(position);
                        else if (page_filter.equalsIgnoreCase("Recruiter"))
                            page_sub_filter = candidateAdapter.getData().get(position);
                        else if (page_filter.equalsIgnoreCase("Job"))
                            page_sub_filter = jobs.get(position);

                        Log.e("subfilter-", page_sub_filter);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setfilter.setVisibility(View.GONE);
            }
        });

        setfilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cand_filter.setVisibility(View.GONE);
                cand_subfilter.setVisibility(View.GONE);
                setfilter.setVisibility(View.GONE);
                filter.setVisibility(View.VISIBLE);
                status.setVisibility(View.GONE);
                Log.e("setbuttonclicked", filtertext + ":" + page_filter + ":" + page_sub_filter);
                initViews(filtertext + ":" + page_filter + ":" + page_sub_filter);
            }
        });
        status.setOnClickListener(new View.OnClickListener() {
            public String dStatus, dSubStatus, dStatusCheck, dSubStatusCheck;

            @Override
            public void onClick(View v) {
                /* cand_status.setVisibility(View.VISIBLE);
                filter.setVisibility(View.GONE);*/
                status.setVisibility(View.GONE);

                Dialog dialog = new Dialog(getActivity());
                dialog.setTitle("Select Status");
                dialog.setContentView(R.layout.dialog_candidate_status);
                dialog.setCancelable(true);

                Spinner spCandidateStatus = (Spinner) dialog.findViewById(R.id.sp_candidate_status);
                Spinner spCandidateSubStatus = (Spinner) dialog.findViewById(R.id.sp_candidate_sub_status);
                Spinner spCandidatePreferredStatus = (Spinner) dialog.findViewById(R.id.sp_candidate_preferred_sub_status);

                TextInputLayout tilCandidatePreferredLocation = (TextInputLayout) dialog.findViewById(R.id.til_candidate_preferred_location);
                TextInputEditText etCandidatePreferredLocation = (TextInputEditText) dialog.findViewById(R.id.et_candidate_preferredlocation);

                Button btCandidateSetStatus = (Button) dialog.findViewById(R.id.bt_candidate_set_status);
                Button btCandidateCancelStatus = (Button) dialog.findViewById(R.id.bt_candidate_cancel_status);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, status_data);
                spCandidateStatus.setAdapter(adapter);

                spCandidateStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        dStatus = status_data.get(i);
                        dStatusCheck = status_data.get(0);
                        int position = i;

                        if (filtertext.equalsIgnoreCase("ipending")) {
                            if (dStatus.equalsIgnoreCase("interview select")) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, INTERVIEW_SELECT_OPTIONS);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spCandidateSubStatus.setAdapter(adapter);
                                spCandidateSubStatus.setVisibility(View.VISIBLE);
                                spCandidatePreferredStatus.setVisibility(View.GONE);
                                tilCandidatePreferredLocation.setVisibility(View.GONE);
                            } else if (dStatus.equalsIgnoreCase("Interview Reject")) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, INTERVIEW_REJECT_OPTIONS);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spCandidateSubStatus.setAdapter(adapter);
                                spCandidateSubStatus.setVisibility(View.VISIBLE);
                                spCandidatePreferredStatus.setVisibility(View.GONE);
                                tilCandidatePreferredLocation.setVisibility(View.GONE);

                            } else if (dStatus.equalsIgnoreCase("No show")) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, NO_SHOW_OPTIONS);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spCandidateSubStatus.setAdapter(adapter);
                                spCandidateSubStatus.setVisibility(View.VISIBLE);
                                spCandidatePreferredStatus.setVisibility(View.GONE);
                                tilCandidatePreferredLocation.setVisibility(View.GONE);

                            } else if (dStatus.equalsIgnoreCase("Not Interested")) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, NOT_INTERESTED_OPTIONS);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spCandidateSubStatus.setAdapter(adapter);
                                spCandidateSubStatus.setVisibility(View.VISIBLE);
                                spCandidatePreferredStatus.setVisibility(View.GONE);
                                tilCandidatePreferredLocation.setVisibility(View.GONE);
                            } else {
                                spCandidateSubStatus.setVisibility(View.GONE);
                                spCandidatePreferredStatus.setVisibility(View.GONE);
                                tilCandidatePreferredLocation.setVisibility(View.GONE);
                            }


                        } else if (filtertext.equalsIgnoreCase("interviewed")) {
                            if (dStatus.equalsIgnoreCase("Next Interview")) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, INTERVIEW_CONFIRMED_STAGE);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spCandidateSubStatus.setAdapter(adapter);
                                spCandidateSubStatus.setVisibility(View.VISIBLE);
                            } else if (dStatus.equalsIgnoreCase("Not Interested")) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, NOT_INTERESTED_OPTIONS);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spCandidateSubStatus.setAdapter(adapter);
                                spCandidateSubStatus.setVisibility(View.VISIBLE);
                            } else
                                spCandidateSubStatus.setVisibility(View.GONE);

                        } else if (filtertext.equalsIgnoreCase("new")) {
                            if (dStatus.equalsIgnoreCase("Not Interested")) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, NOT_INTERESTED_OPTIONS);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spCandidateSubStatus.setAdapter(adapter);
                                spCandidateSubStatus.setVisibility(View.VISIBLE);
                            } else if (dStatus.equalsIgnoreCase("interview confirmed")) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, INTERVIEW_CONFIRMED_STAGE);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spCandidateSubStatus.setAdapter(adapter);
                                spCandidateSubStatus.setVisibility(View.VISIBLE);
                            } else
                                spCandidateSubStatus.setVisibility(View.GONE);
                        } else if (filtertext.equalsIgnoreCase("screening")) {
                            if (dStatus.equalsIgnoreCase("Not Interested")) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, NOT_INTERESTED_OPTIONS);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spCandidateSubStatus.setAdapter(adapter);
                                spCandidateSubStatus.setVisibility(View.VISIBLE);
                            } else if (dStatus.equalsIgnoreCase("interview confirmed")) {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, INTERVIEW_CONFIRMED_STAGE);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spCandidateSubStatus.setAdapter(adapter);
                                spCandidateSubStatus.setVisibility(View.VISIBLE);
                            } else
                                spCandidateSubStatus.setVisibility(View.GONE);
                        } else if (filtertext.equalsIgnoreCase("offered")) {
                            if (dStatus.equalsIgnoreCase("Not Interested")) {
                                spCandidateSubStatus.setVisibility(View.VISIBLE);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, NOT_INTERESTED_OPTIONS);
                                spCandidateSubStatus.setAdapter(adapter);
                            }
                        } else spCandidateSubStatus.setVisibility(View.GONE);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                spCandidateSubStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (dStatus.equalsIgnoreCase("Interview Select")) {
                            dSubStatus = INTERVIEW_SELECT_OPTIONS[i];
                            dSubStatusCheck = INTERVIEW_SELECT_OPTIONS[0];
                        } else if (dStatus.equalsIgnoreCase("Interview Reject")) {
                            dSubStatus = INTERVIEW_REJECT_OPTIONS[i];
                            dSubStatusCheck = INTERVIEW_REJECT_OPTIONS[0];
                        } else if (dStatus.equalsIgnoreCase("NO show")) {
                            dSubStatus = NO_SHOW_OPTIONS[i];
                            dSubStatusCheck = NO_SHOW_OPTIONS[0];
                        } else if (dStatus.equalsIgnoreCase("Not Interested")) {
                            dSubStatus = NOT_INTERESTED_OPTIONS[i];
                            dSubStatusCheck = NO_SHOW_OPTIONS[0];
                        } else if (dStatus.equalsIgnoreCase("Interview Confirmed")) {
                            dSubStatus = INTERVIEW_CONFIRMED_STAGE[i];
                            dSubStatusCheck = INTERVIEW_CONFIRMED_STAGE[0];
                        } else if (dStatus.equalsIgnoreCase("Next Interview")) {
                            dSubStatus = INTERVIEW_CONFIRMED_STAGE[i];
                            dSubStatusCheck = INTERVIEW_CONFIRMED_STAGE[0];
                        } else
                            dSubStatus = "no";

                        if(dSubStatus.equalsIgnoreCase("Skill Mismatch")){
                            spCandidatePreferredStatus.setVisibility(View.VISIBLE);
                            ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,CandidateAdapter.SKILL_OPTIONS);
                            spCandidatePreferredStatus.setAdapter(adapter);
                            tilCandidatePreferredLocation.setVisibility(View.GONE);
                        }else if(dSubStatus.equalsIgnoreCase("Domain Mismatch")){
                            spCandidatePreferredStatus.setVisibility(View.VISIBLE);
                            ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,CandidateAdapter.DOMAIN_OPTIONS);
                            spCandidatePreferredStatus.setAdapter(adapter);
                            tilCandidatePreferredLocation.setVisibility(View.GONE);
                        }else if(dSubStatus.equalsIgnoreCase("Role Mismatch")){
                            spCandidatePreferredStatus.setVisibility(View.VISIBLE);
                            ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,CandidateAdapter.ROLE_OPTIONS);
                            spCandidatePreferredStatus.setAdapter(adapter);
                            tilCandidatePreferredLocation.setVisibility(View.GONE);
                        }else if(dSubStatus.equalsIgnoreCase("Company Mismatch")){
                            spCandidatePreferredStatus.setVisibility(View.VISIBLE);
                            ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,CandidateAdapter.PREFERRED_COMPANIES);
                            spCandidatePreferredStatus.setAdapter(adapter);
                            tilCandidatePreferredLocation.setVisibility(View.GONE);
                        }else if(dSubStatus.equalsIgnoreCase("Location Mismatch")){
                            spCandidatePreferredStatus.setVisibility(View.GONE);
                            tilCandidatePreferredLocation.setVisibility(View.VISIBLE);
                        }

                    }



                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                spCandidatePreferredStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(dSubStatus.equalsIgnoreCase("Skill Mismatch")){
                            preferredSubStatus=CandidateAdapter.SKILL_OPTIONS[i];
                        }else if(dSubStatus.equalsIgnoreCase("Domain Mismatch")){
                            preferredSubStatus=CandidateAdapter.SKILL_OPTIONS[i];

                        }else if(dSubStatus.equalsIgnoreCase("Role Mismatch")){
                            preferredSubStatus=CandidateAdapter.SKILL_OPTIONS[i];

                        }else if(dSubStatus.equalsIgnoreCase("Company Mismatch")){
                            preferredSubStatus=CandidateAdapter.SKILL_OPTIONS[i];
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                btCandidateSetStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(dSubStatus.equalsIgnoreCase("Location Mismatch"))
                            preferredSubStatus=etCandidatePreferredLocation.getText().toString().trim();
                        if (dStatus.equalsIgnoreCase(dStatusCheck))
                            Toast.makeText(getActivity(), "Please Select Status", Toast.LENGTH_SHORT).show();
                        else if (dSubStatus.equalsIgnoreCase(dSubStatusCheck))
                            Toast.makeText(getActivity(), "Please Select Sub Status", Toast.LENGTH_SHORT).show();
                        else {
                            statusText = filtertext + ":" + dStatus + ":" + dSubStatus+":"+preferredSubStatus;
                            candidateAdapter.setData(statusText);
                            initViews(filtertext);
                            dialog.dismiss();
                        }
                    }
                });
                btCandidateCancelStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });
        setstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cand_status.setVisibility(View.GONE);
                cand_substatus.setVisibility(View.GONE);
                setstatus.setVisibility(View.GONE);
                filter.setVisibility(View.GONE);
                status.setVisibility(View.GONE);
                Log.e(mStatus, mSubstatus);
                statusText = filtertext + ":" + mStatus;
                candidateAdapter.setData(statusText);
                initViews(filtertext);
            }
        });
        multiple_message_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                candidateAdapter.setData();
            }
        });
        cand_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setstatus.setVisibility(View.VISIBLE);
                pos = position;
                mStatus = data1.get(position);
                Log.e("mstatus", mStatus);
                if (filtertext.equalsIgnoreCase("ipending")) {
                    if (position == 2) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getAppContext(), android.R.layout.simple_spinner_item, INTERVIEW_SELECT_OPTIONS);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        cand_substatus.setAdapter(adapter);
                        cand_substatus.setVisibility(View.VISIBLE);
                    } else if (position == 3) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getAppContext(), android.R.layout.simple_spinner_item, INTERVIEW_REJECT_OPTIONS);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        cand_substatus.setAdapter(adapter);
                        cand_substatus.setVisibility(View.VISIBLE);

                    } else if (position == 4) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getAppContext(), android.R.layout.simple_spinner_item, NO_SHOW_OPTIONS);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        cand_substatus.setAdapter(adapter);
                        cand_substatus.setVisibility(View.VISIBLE);

                    } else
                        cand_substatus.setVisibility(View.GONE);
                } /*else if (filtertext.equalsIgnoreCase("interviewed")) {
                    if (position == 2) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getAppContext(), android.R.layout.simple_spinner_item, INTERVIEW_SELECT_OPTIONS);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        cand_substatus.setAdapter(adapter);
                        cand_substatus.setVisibility(View.VISIBLE);
                    } else if (position == 3) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getAppContext(), android.R.layout.simple_spinner_item, INTERVIEW_REJECT_OPTIONS);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        cand_substatus.setAdapter(adapter);
                        cand_substatus.setVisibility(View.VISIBLE);
                    } else
                        cand_substatus.setVisibility(View.GONE);

                } */ else
                    cand_substatus.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cand_substatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (pos == 2)
                    mSubstatus = INTERVIEW_SELECT_OPTIONS[position];
                else if (pos == 3)
                    mSubstatus = INTERVIEW_REJECT_OPTIONS[position];
                else if (pos == 4)
                    mSubstatus = NO_SHOW_OPTIONS[position];
                else
                    mSubstatus = "no";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }

    private void fetchData(final VolleyCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", current.getUid());
        Log.e("id", current.getUid());
        JsonObjectRequest req = new JsonObjectRequest(URL_FEED, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response", response.toString());
                        callback.onSuccess(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                VolleyLog.d("harsh", "Error: " + error.getMessage());
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(req);
    }

    private void setval(String t) {
        String s = t.split(":")[0];

        data1 = new ArrayList<>();
        status_data = new ArrayList<>();

        data1.add("Select Status");
        status_data.add("Select Status");

        if (s.equalsIgnoreCase("new")) {
            data1.add("Duplicate");
            data1.add("Not Interested");
            data1.add("Interested But Unavailable");
            data1.add("Interested");
            data1.add("New Resume");
            data1.add("Duplicacy Detected");
            status_data.add("Duplicate");
            status_data.add("Not Interested");
            status_data.add("Interested But Unavailable");
            status_data.add("Interested");
            status_data.add("Sent - Screening");
            status_data.add("Interview Confirmed");

        } else if (s.equalsIgnoreCase("screening")) {
            data1.add("Duplicate");
            data1.add("Screen Reject");
            data1.add("Screen Select");
            data1.add("Not Interested");
            data1.add("Interested But Unavailable");
            data1.add("Interested");
            status_data.add("Duplicate");
            status_data.add("Screen Reject");
            status_data.add("Screen Select");
            status_data.add("Not Interested");
            status_data.add("Interested But Unavailable");
            status_data.add("Interested");
            status_data.add("Interview Confirmed");

        } else if (s.equalsIgnoreCase("ipending")) {
            data1.add("Duplicate");
            data1.add("No Show");
            data1.add("Reschedule by Client");
            data1.add("Reschedule by Candidate");
            data1.add("Not Interested");
            data1.add("Pending");
            status_data.add("Duplicate");
            status_data.add("Interview Select");
            status_data.add("Interview Reject");
            status_data.add("No Show");
            status_data.add("Reschedule by Client");
            status_data.add("Reschedule by Candidate");
            status_data.add("Not Interested");


        } else if (s.equalsIgnoreCase("interviewed")) {
            data1.add("Duplicate");
            data1.add("Interview Select");
            data1.add("Interview Reject");
            data1.add("Next Interview");
            data1.add("Not Interested");
            status_data.add("Duplicate");
            status_data.add("Candidate Accepted – Offer");
            status_data.add("Next Interview");
            status_data.add("Offered");
            status_data.add("Not Interested");


        } else if (s.equalsIgnoreCase("offered")) {
            data1.add("Duplicate");
            data1.add("Interested");
            data1.add("Not Interested");
            data1.add("Offered");
            data1.add("Candidate Accepted – Offer");
            data1.add("Ditched");
            data1.add("DOJ Revised");
            status_data.add("Duplicate");
            status_data.add("Interested");
            status_data.add("Not Interested");
            status_data.add("Ditched");
            status_data.add("Joined");
            status_data.add("DOJ Revised");


        } else if (s.equalsIgnoreCase("joined")) {
            data1.add("Duplicate");
            data1.add("Joined");
            data1.add("Joined & left");
            status_data.add("Duplicate");
            status_data.add("Joined & left");

        }
    }

    private void initViews(String s) {
        ArrayList<String> data = new ArrayList<>();
        data.add("Select Filter");
        data.add("Status");
        data.add("Recruiter");
        data.add("Job");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getAppContext(), android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cand_filter.setAdapter(adapter);
        setval(s);

        fetchData(new VolleyCallback() {
            @Override
            public void onSuccess(JSONObject resp) {
                result = resp;

                select_all.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (select_all.isChecked()) {
                            flag = true;
                            val++;
                        } else {
                            flag = false;
                        }

                        initViews(s);
                    }
                });
                // Log.e("response",resp.toString());
                ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getAppContext(), android.R.layout.simple_spinner_item, data1);
                adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                cand_status.setAdapter(adapter1);


                candidateAdapter = new CandidateAdapter(statusText, flag, s, getAppContext(),
                        result, getActivity());
                Candidate_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
                Candidate_recycler_view.setAdapter(candidateAdapter);

            }
        });

    }

    @Override
    public Context getAppContext() {
        return getContext();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unSubscribe();
    }


    @Override
    public void onCallLogsFetched(List<CallLogModel> callLogs) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Set<String> client = new HashSet<String>();
                Set<String> jobID = new HashSet<String>();
                for (CallLogModel callLog : callLogs) {
                    client.add(callLog.getClient());
                    jobID.add(callLog.getPrimarySkill());
                }
                clients.addAll(client);
                jobs.addAll(jobID);
            }
        }.start();
    }
}
