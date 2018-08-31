package app.com.thetechnocafe.hirecall.Features.Dashboard.Analysis;

/**
 * Created by stark on 15/6/17.
 */

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

public class AnalysisFragment extends Fragment implements AnalysisContract.View {

    @BindView(R.id.pie_chartx)
    PieChart pie_chartx;
    @BindView(R.id.horiz_bar_chart)
    HorizontalBarChart bx;
    @BindView(R.id.analysis_element)
    Spinner analysis_element;
    @BindView(R.id.bar_name)
    TextView bar_name;
    @BindView(R.id.pie_name)
    TextView pie_name;
    private static String filtertext,time_text;
    private  long startTime,endTime;
    private int interested=0,not_interested=0,rescheduled=0,m=0,interested1=0,not_interested1=0,rescheduled1=0,interested2=0,not_interested2=0,rescheduled2=0,conf_reschedule=0;
    private int interested3=0,not_interested3=0,rescheduled3=0,conf_reschedule1=0,conf_reschedule2=0,conf_reschedule0=0,not_interested4=0,rescheduled4=0,conf_reschedule3=0,pos=0;
    private  int no_show=0,int_rejected=0,int_selected=0,interested5=0,rescheduled5=0,not_interested5=0,cand_ditched=0,cand_joined=0,cand_joined_left=0,interested7=0,rescheduled7=0,not_interested7=0;
    private AnalysisContract.Presenter mPresenter;
    private Handler mHandler;
    private ArrayList<String> jobIds,labels;
    PieDataSet pieDataSet;
    PieData pieData;
    private Calendar calendar;
    ArrayList<PieEntry> entries;
    private static String[] call_types,NOT_INTERESTED_OPTIONS;
    private static String[] INTERVIEW_SELECT_OPTIONS,months;
    private static String[] INTERVIEW_REJECT_OPTIONS;
    private static String[] NO_SHOW_OPTIONS;
    private List<CallLogModel> CALL_LOGS;
    private List<CallLogModel> callLogs = new ArrayList<CallLogModel>();
    ArrayList<String> noshow = new ArrayList<String>();
    ArrayList<String> select_reason = new ArrayList<String>();
    ArrayList<String> reject_reason = new ArrayList<String>();
    int count1[]=new int[24],count2[]=new int[24],count3[]=new int[24],count4[]=new int[24],count5[]=new int[24],count6[]=new int[24],count7[]=new int[24];
    int count51[]=new int[24],count52[]=new int[24],count53[]=new int[24];

    //Instance method
    public static app.com.thetechnocafe.hirecall.Features.Dashboard.Analysis.AnalysisFragment getInstance(String s,String t) {
        filtertext=s;
        time_text=t;
        return new app.com.thetechnocafe.hirecall.Features.Dashboard.Analysis.AnalysisFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);

        ButterKnife.bind(this, view);
        call_types = getResources().getStringArray(R.array.call_purpose);
        NOT_INTERESTED_OPTIONS= getResources().getStringArray(R.array.not_interested_options);
        INTERVIEW_SELECT_OPTIONS = getResources().getStringArray(R.array.interview_select_options);
        INTERVIEW_REJECT_OPTIONS = getResources().getStringArray(R.array.interview_reject_options);
        NO_SHOW_OPTIONS = getResources().getStringArray(R.array.no_show_options);
        months= getResources().getStringArray(R.array.months);
        mHandler = new Handler(Looper.getMainLooper());
        mPresenter = new AnalysisPresenter();
        mPresenter.subscribe(this);
        for(int i=0;i<24;i++) {
            count1[i] = 0;
            count2[i] = 0;
            count3[i] = 0;
            count4[i] = 0;
            count5[i] = 0;
            count6[i] = 0;
            count7[i] = 0;
        }
        for(int i=0;i<NO_SHOW_OPTIONS.length;i++)
            count51[i]=0;
        for(int i=0;i<INTERVIEW_SELECT_OPTIONS.length;i++)
            count52[i]=0;
        for(int i=0;i<INTERVIEW_REJECT_OPTIONS.length;i++)
            count53[i]=0;
        initViews();
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) - 6));
        startTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE, 7);
        endTime = calendar.getTimeInMillis();
        return view;
    }

    private void initViews()
    {
        labels=new ArrayList<String>();
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
        CALL_LOGS = callLogs;

        new Thread() {
            @Override
            public void run() {
                super.run();
                Set<String> jobID = new HashSet<String>();
                Set<String> phoneNumbers = new HashSet<>();
                for (CallLogModel callLog : callLogs) {
                    phoneNumbers.add(callLog.getPhoneNumber());
                    jobID.add(callLog.getJobID());
                }
                jobIds=new ArrayList<String>();
                jobIds.addAll(jobID);
                for(int i=0;i<jobIds.size();i++) {
                    Disposable disposable = FirebaseDB.getInstance()
                            .getListOfJobCallLogs(jobIds.get(i))
                            .subscribe(callLogModels -> {
                                onjobCallLogsFetched(callLogModels,jobIds.size());
                            });
                }

            }
        }.start();
    }

    public  void onjobCallLogsFetched(List<CallLogModel> callLogs1,int size) {
        m++;
        ArrayList<String> reasons1 = new ArrayList<String>();
        ArrayList<String> reasons2 = new ArrayList<String>();
        ArrayList<String> reasons3 = new ArrayList<String>();
        ArrayList<String> reasons4 = new ArrayList<String>();
        ArrayList<String> reasons5 = new ArrayList<String>();
        ArrayList<String> reasons6 = new ArrayList<String>();
        ArrayList<String> reasons7 = new ArrayList<String>();

        for (CallLogModel callLog : callLogs1) {

            if (!filtertext.equalsIgnoreCase("no")) {
                if (filtertext.split(":")[0].equalsIgnoreCase("job") && callLog.getPrimarySkill().equalsIgnoreCase(filtertext.split(":")[1])) {
                    if (time_text.split(":")[0].equalsIgnoreCase("time")) {
                        int current_month = calendar.get(Calendar.MONTH);
                        switch (time_text.split(":")[1]) {
                            case "Filter by Week": {
                                if(time_text.split(":")[2].equalsIgnoreCase("Current Week"))
                                {
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                }
                                else if(time_text.split(":")[2].equalsIgnoreCase("Last Week")) {
                                    calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) - 13));
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.DATE, 7);
                                    endTime = calendar.getTimeInMillis();

                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                }
                            }
                            break;
                            case "Filter by Month": {
                                if(time_text.split(":")[2].equalsIgnoreCase(months[current_month]))
                                {
                                    calendar.set(Calendar.DAY_OF_MONTH,1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH,1);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                }
                                else if(time_text.split(":")[2].equalsIgnoreCase(months[current_month-1])) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 1));
                                    calendar.set(Calendar.DAY_OF_MONTH,1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH,1);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                }
                                else if(time_text.split(":")[2].equalsIgnoreCase(months[current_month-2])) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 2));
                                    calendar.set(Calendar.DAY_OF_MONTH,1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH,1);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                }
                                else if(time_text.split(":")[2].equalsIgnoreCase(months[current_month-3])) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 3));
                                    calendar.set(Calendar.DAY_OF_MONTH,1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH,1);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                }
                            }
                            break;
                            case "Filter by Quarter": {
                                if (time_text.split(":")[2].equalsIgnoreCase("Current Quarter")) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 4));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 4);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase("Previous Quarter")) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 8));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 4);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                }
                            }
                            break;
                            case "Filter by Biyearly": {
                                if (time_text.split(":")[2].equalsIgnoreCase("Current")) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 6));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 6);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase("Previous")) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 12));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 6);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                }

                                break;
                            }
                            case "Filter by Year": {
                                calendar.set(Calendar.MONTH,0);
                                calendar.set(Calendar.DAY_OF_MONTH, 1);
                                startTime = calendar.getTimeInMillis();
                                calendar.set(Calendar.MONTH,11);
                                calendar.set(Calendar.DAY_OF_MONTH, 31);
                                endTime = calendar.getTimeInMillis();
                                if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                    callLogs.add(callLog);
                                }
                            }
                            break;
                        }
                    }
                } else if (filtertext.split(":")[0].equalsIgnoreCase("client") && callLog.getClient().equalsIgnoreCase(filtertext.split(":")[1])) {
                    if (time_text.split(":")[0].equalsIgnoreCase("time")) {
                        int current_month = calendar.get(Calendar.MONTH);
                        switch (time_text.split(":")[1]) {
                            case "Filter by Week": {
                                if (time_text.split(":")[2].equalsIgnoreCase("Current Week")) {
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase("Last Week")) {
                                    calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) - 13));
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.DATE, 7);
                                    endTime = calendar.getTimeInMillis();

                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                }
                            }
                            break;
                            case "Filter by Month": {
                                if (time_text.split(":")[2].equalsIgnoreCase(months[current_month])) {
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 1);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase(months[current_month - 1])) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 1));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 1);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase(months[current_month - 2])) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 2));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 1);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase(months[current_month - 3])) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 3));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 1);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                }
                            }
                            break;
                            case "Filter by Quarter": {
                                if (time_text.split(":")[2].equalsIgnoreCase("Current Quarter")) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 4));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 4);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase("Previous Quarter")) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 8));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 4);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                }
                            }
                            break;
                            case "Filter by Biyearly": {
                                if (time_text.split(":")[2].equalsIgnoreCase("Current")) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 6));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 6);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase("Previous")) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 12));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 6);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                }

                                break;
                            }
                            case "Filter by Year": {
                                calendar.set(Calendar.MONTH, 0);
                                calendar.set(Calendar.DAY_OF_MONTH, 1);
                                startTime = calendar.getTimeInMillis();
                                calendar.set(Calendar.MONTH, 11);
                                calendar.set(Calendar.DAY_OF_MONTH, 31);
                                endTime = calendar.getTimeInMillis();
                                if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                    callLogs.add(callLog);
                                }
                            }
                            break;
                        }
                    }
                }
                else {
                    if (time_text.split(":")[0].equalsIgnoreCase("time")) {
                        int current_month = calendar.get(Calendar.MONTH);
                        switch (time_text.split(":")[1]) {
                            case "Filter by Week": {
                                if (time_text.split(":")[2].equalsIgnoreCase("Current Week")) {
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase("Last Week")) {
                                    calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) - 13));
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.DATE, 7);
                                    endTime = calendar.getTimeInMillis();

                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                }
                            }
                            break;
                            case "Filter by Month": {
                                if (time_text.split(":")[2].equalsIgnoreCase(months[current_month])) {
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 1);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase(months[current_month - 1])) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 1));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 1);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase(months[current_month - 2])) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 2));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 1);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase(months[current_month - 3])) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 3));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 1);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                }
                            }
                            break;
                            case "Filter by Quarter": {
                                if (time_text.split(":")[2].equalsIgnoreCase("Current Quarter")) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 4));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 4);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase("Previous Quarter")) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 8));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 4);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                }
                            }
                            break;
                            case "Filter by Biyearly": {
                                if (time_text.split(":")[2].equalsIgnoreCase("Current")) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 6));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 6);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase("Previous")) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 12));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 6);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        callLogs.add(callLog);
                                    }
                                }

                                break;
                            }
                            case "Filter by Year": {
                                calendar.set(Calendar.MONTH, 0);
                                calendar.set(Calendar.DAY_OF_MONTH, 1);
                                startTime = calendar.getTimeInMillis();
                                calendar.set(Calendar.MONTH, 11);
                                calendar.set(Calendar.DAY_OF_MONTH, 31);
                                endTime = calendar.getTimeInMillis();
                                if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                    callLogs.add(callLog);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
        for (CallLogModel callLog : callLogs) {
           if (callLog.getFeedbackReason().equalsIgnoreCase("Introductory Call")) {
                //  Log.e("feedback", callLog.getFeedback().toString());

                if (callLog.getFeedback().size() == 1) {
                    reasons1.add(callLog.getFeedback().get(0));
                }
                if (callLog.getFeedback().size() == 2) {
                    reasons1.add(callLog.getFeedback().get(0));
                }
                if (callLog.getFeedback().size() == 3) {
                    reasons1.add(callLog.getFeedback().get(2));
                }
                if (callLog.getFeedback().size() == 4) {
                    reasons1.add(callLog.getFeedback().get(3));
                } else {
                    reasons1.add(callLog.getFeedback().get(callLog.getFeedback().size() - 1));
                }

            }
            else if (callLog.getFeedbackReason().equalsIgnoreCase("Shortlist Confirmation Call")) {
              //  Log.e("feedback",callLog.getFeedback().toString());
                if (callLog.getFeedback().size() == 1) {
                    reasons2.add(callLog.getFeedback().get(0));
                }
                if (callLog.getFeedback().size() == 2) {
                    reasons2.add(callLog.getFeedback().get(0));
                }
                if (callLog.getFeedback().size() == 3) {
                    reasons2.add(callLog.getFeedback().get(2));
                }
                if (callLog.getFeedback().size() == 4) {
                    reasons2.add(callLog.getFeedback().get(3));
                } else {
                    reasons2.add(callLog.getFeedback().get(callLog.getFeedback().size() - 1));
                }
            }
         else if (callLog.getFeedbackReason().equalsIgnoreCase("Interview Confirmation Call")) {
               // Log.e("feedback",callLog.getFeedback().toString());
                if (callLog.getFeedback().size() == 1) {
                    reasons3.add(callLog.getFeedback().get(0));
                }
                if (callLog.getFeedback().size() == 2) {
                    reasons3.add(callLog.getFeedback().get(0));
                }
                if (callLog.getFeedback().size() == 3) {
                    reasons3.add(callLog.getFeedback().get(2));
                }
                if (callLog.getFeedback().size() == 4) {
                    reasons3.add(callLog.getFeedback().get(3));
                } else if(callLog.getFeedback().get(callLog.getFeedback().size() - 1).split(":")[0].equalsIgnoreCase("Note")) {
                    reasons3.add(callLog.getFeedback().get(callLog.getFeedback().size() - 2));
                }
                else {
                        reasons3.add(callLog.getFeedback().get(callLog.getFeedback().size() - 1));
                }
            }
            else if (callLog.getFeedbackReason().equalsIgnoreCase("Pre Interview Follow Up Call")) {
              // Log.e("feedback",callLog.getFeedback().toString());
                if (callLog.getFeedback().size() == 1) {
                    reasons4.add(callLog.getFeedback().get(0));
                }
                if (callLog.getFeedback().size() == 2) {
                    reasons4.add(callLog.getFeedback().get(1));
                }
                if (callLog.getFeedback().size() == 3) {
                    reasons4.add(callLog.getFeedback().get(2));
                }
                if (callLog.getFeedback().size() == 4) {
                    reasons4.add(callLog.getFeedback().get(3));
                } else if(callLog.getFeedback().get(callLog.getFeedback().size() - 1).split(":")[0].equalsIgnoreCase("Note")) {
                    reasons4.add(callLog.getFeedback().get(callLog.getFeedback().size() - 2));
                }
                else {
                    reasons3.add(callLog.getFeedback().get(callLog.getFeedback().size() - 1));
                }
            }
            else if (callLog.getFeedbackReason().equalsIgnoreCase("Post Interview Call")) {
               //Log.e("feedback",callLog.getFeedback().toString());
                if (callLog.getFeedback().size() == 1) {
                    reasons5.add(callLog.getFeedback().get(0));
                }
                if (callLog.getFeedback().size() == 2) {
                    reasons5.add(callLog.getFeedback().get(1));
                }
                if (callLog.getFeedback().size() == 3) {
                    reasons4.add(callLog.getFeedback().get(1));
                }
                if (callLog.getFeedback().size() == 4) {
                    reasons5.add(callLog.getFeedback().get(2));
                } else if(callLog.getFeedback().get(callLog.getFeedback().size() - 1).split(":")[0].equalsIgnoreCase("Note")) {
                    reasons5.add(callLog.getFeedback().get(callLog.getFeedback().size() - 2));
                }
                else {
                    reasons5.add(callLog.getFeedback().get(callLog.getFeedback().size() - 1));
                }
            }
           else if (callLog.getFeedbackReason().equalsIgnoreCase("Offer Call")) {
               //Log.e("feedback",callLog.getFeedback().toString());
               if (callLog.getFeedback().size() == 1) {
                   reasons6.add(callLog.getFeedback().get(0));
               }
               if (callLog.getFeedback().size() == 2) {
                   reasons6.add(callLog.getFeedback().get(1));
               }
               if (callLog.getFeedback().size() == 3) {
                   reasons6.add(callLog.getFeedback().get(1));
               }
           }
           else if (callLog.getFeedbackReason().equalsIgnoreCase("Post Offer Call")) {
               //Log.e("feedback",callLog.getFeedback().toString());
               if (callLog.getFeedback().size() == 1) {
                   reasons7.add(callLog.getFeedback().get(0));
               }
               if (callLog.getFeedback().size() == 2) {
                   reasons7.add(callLog.getFeedback().get(1));
               }
               if (callLog.getFeedback().size() == 3) {
                   reasons7.add(callLog.getFeedback().get(2));
               }
               if (callLog.getFeedback().size() == 4) {
                   reasons7.add(callLog.getFeedback().get(2));
               } else if(callLog.getFeedback().get(callLog.getFeedback().size() - 1).split(":")[0].equalsIgnoreCase("Note")) {
                   reasons7.add(callLog.getFeedback().get(callLog.getFeedback().size() - 2));
               }
               else {
                   reasons7.add(callLog.getFeedback().get(callLog.getFeedback().size() - 1));
               }
           }

           callLog=null;
        }
        for (String reason : reasons1) {
           // Log.e("Reason",reason.toString());
            if (reason.split(" ")[0].equalsIgnoreCase("candidate")&&reason.split(" ")[1].equalsIgnoreCase("is")) {
                interested++;}
            else if (reason.split(" ")[0].equalsIgnoreCase("candidate")&&reason.split(" ")[1].equalsIgnoreCase("requested"))
                conf_reschedule1++;
             else if (reason.split(" ")[0].equalsIgnoreCase("asked"))
                rescheduled++;
             else
                {
                    //Log.e("feedback", reason.toString());
                    not_interested++;
                    if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Skill"))
                    {
                        count1[6]++;
                    }
                    else  if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Domain"))
                    {
                        count1[2]++;
                    }
                    else  if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Location"))
                    {
                        count1[1]++;
                    }
                    else  if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Company"))
                    {
                        count1[3]++;
                    }
                    else {
                        for (int j = 0; j < count1.length; j++)
                        {
                            if(reason.equalsIgnoreCase(NOT_INTERESTED_OPTIONS[j]))
                            {
                                count1[j]++;
                            }
                        }
                    }

                }
        }
        for (String reason : reasons2) {
            if (reason.split(" ")[0].equalsIgnoreCase("candidate")&&reason.split(" ")[1].equalsIgnoreCase("is")) {
                interested1++;}
               else if (reason.split(" ")[0].equalsIgnoreCase("candidate")&&reason.split(" ")[1].equalsIgnoreCase("requested"))
                    conf_reschedule0++;
            else if (reason.split(" ")[0].equalsIgnoreCase("asked")) {
                rescheduled1++;
            } else {

                not_interested1++;
                //Log.e("feedback", reason.toString());
                if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Skill"))
                {
                    count2[6]++;
                }
                else  if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Domain"))
                {
                    count2[2]++;
                }
                else  if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Location"))
                {
                    count2[1]++;
                }
                else  if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Company"))
                {
                    count2[3]++;
                }
                else {
                    for (int j = 0; j < count2.length; j++)
                    {
                        if(reason.equalsIgnoreCase(NOT_INTERESTED_OPTIONS[j]))
                        {
                            count2[j]++;
                        }
                    }
                }

            }
        }
        for (String reason : reasons3) {
            if (reason.split(" ")[0].equalsIgnoreCase("candidate")&&reason.split(" ")[1].equalsIgnoreCase("requested"))
                conf_reschedule++;
              else if (reason.split(" ")[0].equalsIgnoreCase("candidate")&&reason.split(" ")[1].equalsIgnoreCase("confirmed"))
                    interested2++;
            else if (reason.split(" ")[0].equalsIgnoreCase("asked")) {
                rescheduled2++;
            } else {
                not_interested2++;
              //  Log.e("feedback", reason.toString());
                if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Skill"))
                {
                    count3[6]++;
                }
                else  if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Domain"))
                {
                    count3[2]++;
                }
                else  if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Location"))
                {
                    count3[1]++;
                }
                else  if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Company"))
                {
                    count3[3]++;
                }
                else {
                    for (int j = 0; j < count3.length; j++)
                    {
                        if(reason.equalsIgnoreCase(NOT_INTERESTED_OPTIONS[j]))
                        {
                            count3[j]++;
                        }
                    }
                }


            }
        }

        for (String reason : reasons4) {
            if (reason.split(" ")[0].equalsIgnoreCase("candidate")&&reason.split(" ")[1].equalsIgnoreCase("requested"))
                conf_reschedule2++;
            else if (reason.split(" ")[0].equalsIgnoreCase("candidate")&&reason.split(" ")[1].equalsIgnoreCase("will"))
                interested3++;
            else if (reason.split(" ")[0].equalsIgnoreCase("asked")) {
                rescheduled3++;
            } else {
               // Log.e("feedback", reason.toString());
                not_interested3++;
                if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Skill"))
                {
                    count4[6]++;
                }
                else  if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Domain"))
                {
                    count4[2]++;
                }
                else  if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Location"))
                {
                    count4[1]++;
                }
                else  if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Company"))
                {
                    count4[3]++;
                }
                else {
                    for (int j = 0; j < count4.length; j++)
                    {
                        if(reason.equalsIgnoreCase(NOT_INTERESTED_OPTIONS[j]))
                        {
                            count4[j]++;
                        }
                    }
                }

            }
        }

        for (String reason : reasons5) {
            //Log.e("feedback", reason.toString());
            if (reason.split(" ")[0].equalsIgnoreCase("candidate")&&reason.split(" ")[1].equalsIgnoreCase("requested"))
                conf_reschedule3++;
            else if (reason.split(" ")[0].equalsIgnoreCase("No")) {
                no_show++;
                noshow.add(reason.split(":")[1]);
            }
            else if (reason.split(" ")[reason.split(" ").length-1].equalsIgnoreCase("reject")) {
                int_rejected++;
                reject_reason.add(reason.split(" ")[0]);
            }
            else if (reason.split(" ")[reason.split(" ").length-1].equalsIgnoreCase("select")) {
                int_selected++;
                select_reason.add(reason.split(" ")[0]);
            }
            else if (reason.split(" ")[0].equalsIgnoreCase("asked"))
                rescheduled4++;
            else {
               // Log.e("feedback", reason.toString());
                not_interested4++;
                if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Skill"))
                {
                    count5[6]++;
                }
                else  if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Domain"))
                {
                    count5[2]++;
                }
                else  if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Location"))
                {
                    count5[1]++;
                }
                else  if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Company"))
                {
                    count5[3]++;
                }
                else {
                    for (int j = 0; j < count5.length; j++)
                    {
                        if(reason.equalsIgnoreCase(NOT_INTERESTED_OPTIONS[j]))
                        {
                            count5[j]++;
                        }
                    }
                }

            }
        }

        for (String reason : reasons6){
            if (reason.split(" ")[0].equalsIgnoreCase("candidate")&&reason.split(" ")[1].equalsIgnoreCase("is"))
                interested5++;
            else if (reason.split(" ")[0].equalsIgnoreCase("asked")) {
                rescheduled5++;
            } else {
                //Log.e("feedback", reason.toString());
                not_interested5++;
                if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Skill"))
                {
                    count6[6]++;
                }
                else  if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Domain"))
                {
                    count6[2]++;
                }
                else  if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Location"))
                {
                    count6[1]++;
                }
                else  if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Company"))
                {
                    count6[3]++;
                }
                else {
                    for (int j = 0; j < count6.length; j++)
                    {
                        if(reason.equalsIgnoreCase(NOT_INTERESTED_OPTIONS[j]))
                        {
                            count6[j]++;
                        }
                    }
                }

            }
        }

        for (String reason : reasons7) {
            if (reason.split(" ")[0].equalsIgnoreCase("candidate")&&reason.split(" ")[1].equalsIgnoreCase("ditched"))
                cand_ditched++;
            else if (reason.split(" ")[0].equalsIgnoreCase("candidate")&&reason.split(" ")[1].equalsIgnoreCase("is"))
                interested7++;
            else if (reason.split(" ")[0].equalsIgnoreCase("candidate")&&reason.split(" ")[1].equalsIgnoreCase("joined")&& reason.split(" ").length==2)
                cand_joined++;
            else if (reason.split(" ")[0].equalsIgnoreCase("candidate")&&reason.split(" ")[1].equalsIgnoreCase("joined")&&reason.split(" ").length==4)
                cand_joined_left++;
            else if (reason.split(" ")[0].equalsIgnoreCase("asked"))
                rescheduled7++;
            else
            {  // Log.e("feedback", reason.toString());
                not_interested7++;
                if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Skill"))
                {
                    count7[6]++;
                }
                else  if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Domain"))
                {
                    count7[2]++;
                }
                else  if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Location"))
                {
                    count7[1]++;
                }
                else  if(reason.split(" ")[0].equalsIgnoreCase("Preferred") && reason.split(" ")[1].equalsIgnoreCase("Company"))
                {
                    count7[3]++;
                }
                else {
                    for (int j = 0; j < count7.length; j++)
                    {
                        if(reason.equalsIgnoreCase(NOT_INTERESTED_OPTIONS[j]))
                        {
                            count7[j]++;
                        }
                    }
                }

            }

        }
        String s1 = "" + interested+":" + rescheduled+ ":"+ not_interested+ ":" +conf_reschedule1;
        String s2 = "" + interested1+":" + rescheduled1 + ":" + not_interested1+":"+conf_reschedule0;
        String s3 = "" + interested2+":" + rescheduled2 + ":" + not_interested2+":"+conf_reschedule;
        String s4 = "" + interested3+":"+ rescheduled3 + ":" + not_interested3+":"+conf_reschedule2;
        String s5 = "" + no_show+":"+int_selected+":"+int_rejected+":"+ rescheduled4 + ":" + not_interested4+":"+conf_reschedule3;
        String s6 = "" + interested5+":" + rescheduled5 + ":" + not_interested5;
        String s7 = "" + interested7+":"+rescheduled7+":"+cand_ditched+":"+ cand_joined+ ":" + cand_joined_left+":"+not_interested7;
        if (m == size) {
           // Log.e("values",noshow.toString());
           // Log.e("values",select_reason.toString());
           // Log.e("values",reject_reason.toString());
            for(int i=0;i<noshow.size();i++)
            {
                for(int j=0;j<NO_SHOW_OPTIONS.length;j++)
                {
                    if(noshow.get(i).equalsIgnoreCase(NO_SHOW_OPTIONS[j]))
                    {
                        count51[j]++;
                    }
                }

            }

            for(int i=0;i<reject_reason.size();i++)
            {
                for(int j=0;j<INTERVIEW_SELECT_OPTIONS.length;j++)
                {
                    if(reject_reason.get(i).equalsIgnoreCase(INTERVIEW_SELECT_OPTIONS[j].split(" ")[0]))
                    {
                        count52[j]++;
                    }
                }

            }

            for(int i=0;i<select_reason.size();i++)
            {
                for(int j=0;j<INTERVIEW_REJECT_OPTIONS.length;j++)
                {
                    if(reject_reason.get(i).equalsIgnoreCase(INTERVIEW_REJECT_OPTIONS[j].split(" ")[0]))
                    {
                        count53[j]++;
                    }
                }

            }

            pie_chartx.setVisibility(View.GONE);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,call_types);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            analysis_element.setAdapter(adapter);
            analysis_element.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    ArrayList<String> labels=new ArrayList<String>();
                    ArrayList<Integer>colors=new ArrayList<Integer>();
                    if(position==1)
                    {   labels.clear();
                        labels.add("Candidate is Interested");
                        labels.add("Asked to call Later");
                        labels.add("Candidate not Interested/Suitable");
                        labels.add("Candidate is Interested but Unavailable");
                        colors.add(Color.rgb(0,128,0));
                        colors.add(Color.rgb(255,165,0));
                        colors.add(Color.rgb(255,0,0));
                        colors.add(Color.rgb(144,238,144));



                        setVals(labels,1);
                        setBarChart(s1,colors);
                    }
                    else if(position==2)
                    {
                        labels.clear();
                        labels.add("Candidate is Interested");
                        labels.add("Asked to call Later");
                        labels.add("Candidate not Interested/Suitable");
                        labels.add("Candidate is Interested but Unavailable");
                        colors.add(Color.rgb(0,128,0));
                        colors.add(Color.rgb(255,165,0));
                        colors.add(Color.rgb(255,0,0));
                        colors.add(Color.rgb(144,238,144));
                        setVals(labels,2);
                        setBarChart(s2,colors);
                    }
                    else if(position==3)
                    {
                        labels.clear();
                        labels.add("Candidate confirmed his availability");
                        labels.add("Asked to call Later");
                        labels.add("Candidate not Interested");
                        labels.add("Candidate Requested for Reschedule");
                        colors.add(Color.rgb(0,128,0));
                        colors.add(Color.rgb(255,165,0));
                        colors.add(Color.rgb(255,0,0));
                        colors.add(Color.rgb(144,238,144));
                        setVals(labels,3);
                        setBarChart(s3,colors);
                    }
                    else if(position==4)
                    {
                        labels.clear();
                        labels.add("Candidate will attend interview as scheduled");
                        labels.add("Asked to call Later");
                        labels.add("Candidate not Interested");
                        labels.add("Candidate Requested for Reschedule");
                        colors.add(Color.rgb(0,128,0));
                        colors.add(Color.rgb(255,165,0));
                        colors.add(Color.rgb(255,0,0));
                        colors.add(Color.rgb(144,238,144));
                        setVals(labels,4);
                        setBarChart(s4,colors);
                    }
                    else if(position==5)
                    {
                        labels.clear();
                        labels.add("No Show");
                        labels.add("Interview Select");
                        labels.add("Interview Reject");
                        labels.add("Asked to call Later");
                        labels.add("Candidate not Interested");
                        labels.add("Requested Reschedule");
                        colors.add(Color.rgb(0,0,0));
                        colors.add(Color.rgb(0,128,0));
                        colors.add(Color.rgb(128,128,128));
                        colors.add(Color.rgb(255,165,0));
                        colors.add(Color.rgb(255,0,0));
                        colors.add(Color.rgb(144,238,144));
                        setVals(labels,5);
                        setBarChart(s5,colors);
                    }
                    else if(position==6)
                    {
                        labels.clear();
                        labels.add("Candidate is interested with offer");
                        labels.add("Asked to call Later");
                        labels.add("Candidate not Interested");
                        setVals(labels,6);
                        colors.add(Color.rgb(0,128,0));
                        colors.add(Color.rgb(255,165,0));
                        colors.add(Color.rgb(255,0,0));
                        setBarChart(s6,colors);
                    }
                    else if(position==7)
                    {
                        labels.add("Candidate is interested with offer");
                        labels.add("Asked to call later");
                        labels.add("Candidate ditched after acceptance");
                        labels.add("Candidate Joined");
                        labels.add("Candidate Joined & Left");
                        labels.add("Candidate not Interested");
                        setVals(labels,7);
                        colors.add(Color.rgb(0,128,0));
                        colors.add(Color.rgb(255,165,0));
                        colors.add(Color.rgb(128,128,128));
                        colors.add(Color.rgb(255,215,0));
                        colors.add(Color.rgb(0,0,0));
                        colors.add(Color.rgb(255,0,0));
                        setBarChart(s7,colors);

                    }
                    else
                    {
                        labels.clear();
                        labels.add("Candidate is Interested");
                        labels.add("Asked to call Later");
                        labels.add("Candidate not Interested/Suitable");
                        labels.add("Candidate is Interested but Unavailable");
                        setVals(labels,1);
                        colors.add(Color.rgb(144,238,144));
                        colors.add(Color.rgb(255,0,0));
                        colors.add(Color.rgb(255,165,0));
                        colors.add(Color.rgb(0,128,0));
                        setBarChart(s1,colors);
                    }



                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            bx.setTouchEnabled(true);
            bx.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry entry, Highlight highlight) {
                    Log.e("bar_value",String.valueOf(entry.getX()));
                    bar_name.setText(labels.get((int)entry.getX()));
                    if(pos==1&&labels.get((int)entry.getX()).equalsIgnoreCase("Candidate not Interested/Suitable")){
                        pie_chartx.setVisibility(View.VISIBLE);
                        setPieChart(count1,1);
                    }
                    else if(pos==2&&labels.get((int)entry.getX()).equalsIgnoreCase("Candidate not Interested/Suitable"))
                    {   pie_chartx.setVisibility(View.VISIBLE);
                        setPieChart(count2,1);
                    }
                    else if(pos==3&&labels.get((int)entry.getX()).equalsIgnoreCase("Candidate not Interested"))
                    {   pie_chartx.setVisibility(View.VISIBLE);
                        setPieChart(count3,1);
                    }
                    else if(pos==4&&labels.get((int)entry.getX()).equalsIgnoreCase("Candidate not Interested"))
                    {   pie_chartx.setVisibility(View.VISIBLE);
                        setPieChart(count4,1);
                    }
                    else if(pos==5&&labels.get((int)entry.getX()).equalsIgnoreCase("No Show"))
                    {   pie_chartx.setVisibility(View.VISIBLE);
                        setPieChart(count51,2);
                    }
                    else if(pos==5&&labels.get((int)entry.getX()).equalsIgnoreCase("Interview Select"))
                    {   pie_chartx.setVisibility(View.VISIBLE);
                        setPieChart(count53,3);
                    }
                    else if(pos==5&&labels.get((int)entry.getX()).equalsIgnoreCase("Interview Reject"))
                    {   pie_chartx.setVisibility(View.VISIBLE);
                        setPieChart(count52,4);
                    }
                    else if(pos==5&&labels.get((int)entry.getX()).equalsIgnoreCase("Candidate not Interested"))
                    {   pie_chartx.setVisibility(View.VISIBLE);
                        setPieChart(count5,1);
                    }

                    else if(pos==6&&labels.get((int)entry.getX()).equalsIgnoreCase("Candidate not Interested"))
                    {   pie_chartx.setVisibility(View.VISIBLE);
                        setPieChart(count6,1);
                    }
                    else if(pos==7&&labels.get((int)entry.getX()).equalsIgnoreCase("Candidate not Interested"))
                    {
                        pie_chartx.setVisibility(View.VISIBLE);
                        setPieChart(count7,1);
                    }
                    else{
                        pie_chartx.setVisibility(View.GONE);
                        pie_name.setText("");
                    }
                }
                @Override
                public void onNothingSelected() {

                }
            });
           }
    }

    private  void setVals(ArrayList<String> labelsx,int p)
    {
        pos=p;
        labels.clear();
        for(int i=0;i<labelsx.size();i++)
        labels.add(labelsx.get(i));

        //Log.e("labesl",labels.toString());

    }
    private void setBarChart(String s,ArrayList<Integer>colors) {
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
if(s.split(":").length==3) {
    yVals.add(new BarEntry(0, Integer.parseInt(s.split(":")[0])));
    yVals.add(new BarEntry(1, Integer.parseInt(s.split(":")[1])));
    yVals.add(new BarEntry(2, Integer.parseInt(s.split(":")[2])));
}
else if(s.split(":").length==4){
    yVals.add(new BarEntry(0, Integer.parseInt(s.split(":")[0])));
    yVals.add(new BarEntry(1, Integer.parseInt(s.split(":")[1])));
    yVals.add(new BarEntry(2, Integer.parseInt(s.split(":")[2])));
    yVals.add(new BarEntry(3, Integer.parseInt(s.split(":")[3])));
}
else if(s.split(":").length==5){
    yVals.add(new BarEntry(0, Integer.parseInt(s.split(":")[0])));
    yVals.add(new BarEntry(1, Integer.parseInt(s.split(":")[1])));
    yVals.add(new BarEntry(2, Integer.parseInt(s.split(":")[2])));
    yVals.add(new BarEntry(3, Integer.parseInt(s.split(":")[3])));
    yVals.add(new BarEntry(4, Integer.parseInt(s.split(":")[4])));
}
else if(s.split(":").length==6){
    yVals.add(new BarEntry(0, Integer.parseInt(s.split(":")[0])));
    yVals.add(new BarEntry(1, Integer.parseInt(s.split(":")[1])));
    yVals.add(new BarEntry(2, Integer.parseInt(s.split(":")[2])));
    yVals.add(new BarEntry(3, Integer.parseInt(s.split(":")[3])));
    yVals.add(new BarEntry(4, Integer.parseInt(s.split(":")[4])));
    yVals.add(new BarEntry(5, Integer.parseInt(s.split(":")[5])));

}
        BarDataSet lineDataSet = new BarDataSet(yVals, "Calls");
        lineDataSet.setColors(colors);
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        BarData lineData = new BarData(dataSets);
        lineData.setBarWidth(0.5f);
        bx.setData(lineData);
        bx.getLegend().setForm(Legend.LegendForm.CIRCLE);
        bx.getXAxis().setDrawGridLines(false);
        bx.invalidate();
    }
    public void setPieChart(int val[],int pos)
    {
        int z=0,c=0;
        entries = new ArrayList<>();
        ArrayList<String> value=new ArrayList<>();
        for(int i=0;i<val.length;i++) {
            if(val[i]!=0.0) {
                z++;
                if(z<6) {
                    entries.add(new PieEntry(val[i], i));
                    if (pos == 1)
                        value.add(NOT_INTERESTED_OPTIONS[i]);
                    else if (pos == 2)
                        value.add(NO_SHOW_OPTIONS[i]);
                    else if (pos == 3)
                        value.add(INTERVIEW_SELECT_OPTIONS[i]);
                    else
                        value.add(INTERVIEW_REJECT_OPTIONS[i]);
                }
                else
                    c+=val[i];

            }

        }
        if(c!=0)
        {
            entries.add(new PieEntry(c,6));
            value.add("Others");
        }
        pieDataSet = new PieDataSet(entries," ");

        pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.WHITE);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pie_chartx.setData(pieData);
        pie_chartx.invalidate();
        pie_chartx.setDrawHoleEnabled(false);
        pie_chartx.getLegend().setEnabled(false);
        Description d=new Description();
        d.setText("");
        pie_chartx.setDescription(d);

        pie_chartx.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, Highlight highlight) {
                int x=0;
                for(int i=0;i<entries.size();i++)
                {
                    if(entry.equalTo(entries.get(i)))
                        x=i;
                }
                pie_name.setText(String.valueOf(value.get(x)));

            }

            @Override
            public void onNothingSelected() {

            }
        });
    }


}
