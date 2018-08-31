package app.com.thetechnocafe.hirecall.Features.Dashboard.Calls;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.R;
import butterknife.BindView;
import butterknife.ButterKnife;
public class CallFragment extends Fragment implements CallContract.View {

    @BindView(R.id.total_calls_text_view)
    TextView mTotalCallsTextView;
    @BindView(R.id.unique_calls_text_view)
    TextView mUniqueCallsTextView;
    @BindView(R.id.number_of_duplicate_calls)
    TextView mDuplicateCallsTextView;
    @BindView(R.id.average_duration_text_view)
    TextView mAverageDurationTextView;
    @BindView(R.id.call_line_chart)
    LineChart mCallLineChart;
    private static String filtertext,time_text;

    private CallContract.Presenter mPresenter;
    private static String[] months;
    private Handler mHandler;
    private List<CallLogModel> CALL_LOGS;
    private List<CallLogModel> callLogs = new ArrayList<CallLogModel>(),callLogs1 = new ArrayList<CallLogModel>();;
    int[] date_chart,date_chart1;


    //Instance method
    public static CallFragment getInstance(String filter,String t) {
        filtertext=filter;
        time_text=t;
        return new CallFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call_dashboard, container, false);

        ButterKnife.bind(this, view);

        mHandler = new Handler(Looper.getMainLooper());
        date_chart=new int[7];
        date_chart1=new int[7];
        for(int i=0;i<7;i++) {
            date_chart[i] = 0;
            date_chart1[i] = 0;
        }
        mPresenter = new CallPresenter();
        mPresenter.subscribe(this);
        months= getResources().getStringArray(R.array.months);
        initViews();
        return view;
    }

    private void initViews() {

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

    public void func(CallLogModel callLog)
    {
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
                if (sortintoWeek(formatter.format(callLog.getCallDate())) != -1) {
                    date_chart[sortintoWeek(formatter.format(callLog.getCallDate()))]++;
                }

    }
    public void func1(CallLogModel callLog)
    {
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
        if (sortintoWeek(formatter.format(callLog.getCallDate())) != -1) {
            date_chart1[sortintoWeek(formatter.format(callLog.getCallDate()))]++;
        }

    }

    private void setLineChart(){

    Calendar calendar=Calendar.getInstance();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String date_number=date.split("-")[2];
        ArrayList<String>xVals=new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        String  seven="",six = "", five = "", four = "", three = "", two = "", one = "";
        if(time_text.split(":")[0].equalsIgnoreCase("time")&&time_text.split(":")[1].equalsIgnoreCase("Filter by Week")) {


            if (time_text.split(":")[2].equalsIgnoreCase("Current Week")) {
                cal.add(Calendar.DATE, -1);
                six = sdf.format(cal.getTime()).split("-")[2];
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -2);
                five = sdf.format(cal.getTime()).split("-")[2];
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -3);
                four = sdf.format(cal.getTime()).split("-")[2];
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -4);
                three = sdf.format(cal.getTime()).split("-")[2];
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -5);
                two = sdf.format(cal.getTime()).split("-")[2];
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -6);
                one = sdf.format(cal.getTime()).split("-")[2];
                xVals.add(one);
                xVals.add(two);
                xVals.add(three);
                xVals.add(four);
                xVals.add(five);
                xVals.add(six);
                xVals.add(date_number);
            }
            else if (time_text.split(":")[2].equalsIgnoreCase("Last Week")) {
                cal.add(Calendar.DATE, -7);
                six = sdf.format(cal.getTime()).split("-")[2];
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -8);
                five = sdf.format(cal.getTime()).split("-")[2];
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -9);
                four = sdf.format(cal.getTime()).split("-")[2];
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -10);
                three = sdf.format(cal.getTime()).split("-")[2];
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -11);
                two = sdf.format(cal.getTime()).split("-")[2];
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -12);
                one = sdf.format(cal.getTime()).split("-")[2];
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -6);
                seven = sdf.format(cal.getTime()).split("-")[2];
                xVals.add(one);
                xVals.add(two);
                xVals.add(three);
                xVals.add(four);
                xVals.add(five);
                xVals.add(six);
                xVals.add(seven);
            }
        }
        else if(time_text.split(":")[0].equalsIgnoreCase("time")&&time_text.split(":")[1].equalsIgnoreCase("Filter by Month")){
            xVals.add("0");
            xVals.add("5");
            xVals.add("10");
            xVals.add("15");
            xVals.add("20");
            xVals.add("25");
            xVals.add("30");
        }
        else if(time_text.split(":")[0].equalsIgnoreCase("time")&&time_text.split(":")[1].equalsIgnoreCase("Filter by Quarter")) {
            int current_month = calendar.get(Calendar.MONTH);

            if (time_text.split(":")[2].equalsIgnoreCase("Current Quarter")) {
                xVals.add("");
                xVals.add(String.valueOf(months[current_month-2]));
                xVals.add("");
                xVals.add(String.valueOf(months[current_month - 1]));
                xVals.add("");
                xVals.add(String.valueOf(months[current_month ]));
                xVals.add("");

            } else if (time_text.split(":")[2].equalsIgnoreCase("Previous Quarter")) {
                xVals.add("");
                xVals.add(String.valueOf(months[current_month-5]));
                xVals.add("");
                xVals.add(String.valueOf(months[current_month - 4]));
                xVals.add("");
                xVals.add(String.valueOf(months[current_month - 3]));
                xVals.add("");
            }
        }
        else if(time_text.split(":")[0].equalsIgnoreCase("time")&&time_text.split(":")[1].equalsIgnoreCase("Filter by Biyearly")){
            int current_month = calendar.get(Calendar.MONTH);
            if(current_month>=6) {
                xVals.add("");
                xVals.add(String.valueOf(months[6].substring(0,3)));
                xVals.add(String.valueOf(months[7].substring(0,3)));
                xVals.add(String.valueOf(months[8].substring(0,3)));
                xVals.add(String.valueOf(months[9].substring(0,3)));
                xVals.add(String.valueOf(months[10].substring(0,3)));
                xVals.add(String.valueOf(months[11].substring(0,3)));
            }
            else
            {
                xVals.add("");
                xVals.add(String.valueOf(months[0]).substring(0,3));
                xVals.add(String.valueOf(months[1]).substring(0,3));
                xVals.add(String.valueOf(months[2]).substring(0,3));
                xVals.add(String.valueOf(months[3]).substring(0,3));
                xVals.add(String.valueOf(months[4]).substring(0,3));
                xVals.add(String.valueOf(months[5]).substring(0,3));
            }
        }
        else if(time_text.split(":")[0].equalsIgnoreCase("time")&&time_text.split(":")[1].equalsIgnoreCase("Filter by Year")){
            xVals.add(String.valueOf(months[0].substring(0,3)));
            xVals.add(String.valueOf(months[2].substring(0,3)));
            xVals.add(String.valueOf(months[4].substring(0,3)));
            xVals.add(String.valueOf(months[6].substring(0,3)));
            xVals.add(String.valueOf(months[8].substring(0,3)));
            xVals.add(String.valueOf(months[11].substring(0,3)));
            xVals.add("");
        }
        else {
                cal.add(Calendar.DATE, -1);
                six = sdf.format(cal.getTime()).split("-")[2];
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -2);
                five = sdf.format(cal.getTime()).split("-")[2];
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -3);
                four = sdf.format(cal.getTime()).split("-")[2];
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -4);
                three = sdf.format(cal.getTime()).split("-")[2];
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -5);
                two = sdf.format(cal.getTime()).split("-")[2];
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -6);
                one = sdf.format(cal.getTime()).split("-")[2];
            xVals.add(one);
            xVals.add(two);
            xVals.add(three);
            xVals.add(four);
            xVals.add(five);
            xVals.add(six);
            xVals.add(date_number);
        }

        IAxisValueFormatter valueFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, AxisBase axisBase) {
                return xVals.get((int) v);
            }
        };

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        yVals.add(new Entry(0,date_chart[0]));
        yVals.add(new Entry(1,date_chart[1]));
        yVals.add(new Entry(2,date_chart[2]));
        yVals.add(new Entry(3,date_chart[3]));
        yVals.add(new Entry(4,date_chart[4]));
        yVals.add(new Entry(5,date_chart[5]));
        yVals.add(new Entry(6,date_chart[6]));

        LineDataSet lineDataSet = new LineDataSet(yVals, "Calls");
        lineDataSet.setColors(Color.rgb(0,0,128));
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        LineData lineData = new LineData(dataSets);
        mCallLineChart.setData(lineData);
        // mCallLineChart.setBackgroundColor(0xF0F8FF);
        mCallLineChart.setDrawGridBackground(true);
        // mCallLineChart.getLegend().setForm(Legend.LegendForm.NONE);
        mCallLineChart.getLegend().setForm(Legend.LegendForm.NONE);
        mCallLineChart.invalidate();

        mCallLineChart.getXAxis().setValueFormatter(valueFormatter);
        mCallLineChart.setPinchZoom(true);
}


public int sortintoWeek(String s) {
        int i = -1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        String seven = "", six = "", five = "", four = "", three = "", two = "", one = "";
        Calendar cal = Calendar.getInstance();
        int current_month = cal.get(Calendar.MONTH);
        int current_year = cal.get(Calendar.YEAR);
        try {

            if (time_text.split(":")[0].equalsIgnoreCase("time") && time_text.split(":")[1].equalsIgnoreCase("Filter by Week")) {
                if (time_text.split(":")[2].equalsIgnoreCase("Current Week")) {
                    seven = sdf.format(cal.getTime());
                    cal.add(Calendar.DATE, -1);
                    six = sdf.format(cal.getTime());
                    cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -2);
                    five = sdf.format(cal.getTime());
                    cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -3);
                    four = sdf.format(cal.getTime());
                    cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -4);
                    three = sdf.format(cal.getTime());
                    cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -5);
                    two = sdf.format(cal.getTime());
                    cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -6);
                    one = sdf.format(cal.getTime());
                } else if (time_text.split(":")[2].equalsIgnoreCase("Last Week")) {
                    cal = Calendar.getInstance();
                    seven = sdf.format(cal.getTime());
                    cal.add(Calendar.DATE, -7);
                    six = sdf.format(cal.getTime());
                    cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -8);
                    five = sdf.format(cal.getTime());
                    cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -9);
                    four = sdf.format(cal.getTime());
                    cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -10);
                    three = sdf.format(cal.getTime());
                    cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -11);
                    two = sdf.format(cal.getTime());
                    cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -12);
                    one = sdf.format(cal.getTime());
                }
            } else if (time_text.split(":")[0].equalsIgnoreCase("time") && time_text.split(":")[1].equalsIgnoreCase("Filter by Month")) {
                if (time_text.split(":")[2].equalsIgnoreCase(months[current_month])) {
                    if (Integer.parseInt(s.split("-")[1]) == (current_month + 1)) {
                        if (Integer.parseInt(s.split("-")[2]) > 0 && Integer.parseInt(s.split("-")[2]) <= 5) {
                            return 0;
                        } else if (Integer.parseInt(s.split("-")[2]) > 5 && Integer.parseInt(s.split("-")[2]) <= 10) {
                            return 1;
                        } else if (Integer.parseInt(s.split("-")[2]) > 10 && Integer.parseInt(s.split("-")[2]) <= 15) {
                            return 2;
                        } else if (Integer.parseInt(s.split("-")[2]) > 15 && Integer.parseInt(s.split("-")[2]) <= 20) {
                            return 3;
                        } else if (Integer.parseInt(s.split("-")[2]) > 20 && Integer.parseInt(s.split("-")[2]) <= 25) {
                            return 4;
                        } else if (Integer.parseInt(s.split("-")[2]) > 25 && Integer.parseInt(s.split("-")[2]) < 30) {
                            return 5;
                        } else if (Integer.parseInt(s.split("-")[2]) >= 30 && Integer.parseInt(s.split("-")[2]) <= 31) {
                            return 6;
                        }
                    }
                } else if (time_text.split(":")[2].equalsIgnoreCase(months[current_month - 1])) {

                    if (Integer.parseInt(s.split("-")[1]) == current_month) {
                        if (Integer.parseInt(s.split("-")[2]) > 0 && Integer.parseInt(s.split("-")[2]) <= 5) {
                            return 0;
                        } else if (Integer.parseInt(s.split("-")[2]) > 5 && Integer.parseInt(s.split("-")[2]) <= 10) {
                            return 1;
                        } else if (Integer.parseInt(s.split("-")[2]) > 10 && Integer.parseInt(s.split("-")[2]) <= 15) {
                            return 2;
                        } else if (Integer.parseInt(s.split("-")[2]) > 15 && Integer.parseInt(s.split("-")[2]) <= 20) {
                            return 3;
                        } else if (Integer.parseInt(s.split("-")[2]) > 20 && Integer.parseInt(s.split("-")[2]) <= 25) {
                            return 4;
                        } else if (Integer.parseInt(s.split("-")[2]) > 25 && Integer.parseInt(s.split("-")[2]) < 30) {
                            return 5;
                        } else if (Integer.parseInt(s.split("-")[2]) >= 30 && Integer.parseInt(s.split("-")[2]) <= 31) {
                            return 6;
                        }
                    }

                } else if (time_text.split(":")[2].equalsIgnoreCase(months[current_month - 2])) {

                    if (Integer.parseInt(s.split("-")[1]) == (current_month - 1)) {
                        if (Integer.parseInt(s.split("-")[2]) > 0 && Integer.parseInt(s.split("-")[2]) <= 5) {
                            return 0;
                        } else if (Integer.parseInt(s.split("-")[2]) > 5 && Integer.parseInt(s.split("-")[2]) <= 10) {
                            return 1;
                        } else if (Integer.parseInt(s.split("-")[2]) > 10 && Integer.parseInt(s.split("-")[2]) <= 15) {
                            return 2;
                        } else if (Integer.parseInt(s.split("-")[2]) > 15 && Integer.parseInt(s.split("-")[2]) <= 20) {
                            return 3;
                        } else if (Integer.parseInt(s.split("-")[2]) > 20 && Integer.parseInt(s.split("-")[2]) <= 25) {
                            return 4;
                        } else if (Integer.parseInt(s.split("-")[2]) > 25 && Integer.parseInt(s.split("-")[2]) < 30) {
                            return 5;
                        } else if (Integer.parseInt(s.split("-")[2]) >= 30 && Integer.parseInt(s.split("-")[2]) <= 31) {
                            return 6;
                        }
                    }

                } else if (time_text.split(":")[2].equalsIgnoreCase(months[current_month - 3])) {

                    if (Integer.parseInt(s.split("-")[1]) == (current_month - 2)) {
                        if (Integer.parseInt(s.split("-")[2]) > 0 && Integer.parseInt(s.split("-")[2]) <= 5) {
                            return 0;
                        } else if (Integer.parseInt(s.split("-")[2]) > 5 && Integer.parseInt(s.split("-")[2]) <= 10) {
                            return 1;
                        } else if (Integer.parseInt(s.split("-")[2]) > 10 && Integer.parseInt(s.split("-")[2]) <= 15) {
                            return 2;
                        } else if (Integer.parseInt(s.split("-")[2]) > 15 && Integer.parseInt(s.split("-")[2]) <= 20) {
                            return 3;
                        } else if (Integer.parseInt(s.split("-")[2]) > 20 && Integer.parseInt(s.split("-")[2]) <= 25) {
                            return 4;
                        } else if (Integer.parseInt(s.split("-")[2]) > 25 && Integer.parseInt(s.split("-")[2]) < 30) {
                            return 5;
                        } else if (Integer.parseInt(s.split("-")[2]) >= 30 && Integer.parseInt(s.split("-")[2]) <= 31) {
                            return 6;
                        }
                    }

                }
            } else if (time_text.split(":")[0].equalsIgnoreCase("time") && time_text.split(":")[1].equalsIgnoreCase("Filter by Quarter")) {
                if (time_text.split(":")[2].equalsIgnoreCase("Current Quarter")) {
                    Log.e("" + current_month, s);
                    if (Integer.parseInt(s.split("-")[1]) >= current_month - 1 && Integer.parseInt(s.split("-")[1]) <= current_month) {
                        if (Integer.parseInt(s.split("-")[1]) == current_month - 2)
                            return 1;
                        else if (Integer.parseInt(s.split("-")[1]) == current_month - 1)
                            return 3;
                        else if (Integer.parseInt(s.split("-")[1]) == current_month)
                            return 5;
                    }
                } else if (time_text.split(":")[2].equalsIgnoreCase("Previous Quarter")) {
                    if (Integer.parseInt(s.split("-")[1]) > current_month - 5 && Integer.parseInt(s.split("-")[1]) <= current_month - 2) {
                        if (Integer.parseInt(s.split("-")[1]) == current_month - 5)
                            return 1;
                        else if (Integer.parseInt(s.split("-")[1]) == current_month - 4)
                            return 3;
                        else if (Integer.parseInt(s.split("-")[1]) == current_month - 3)
                            return 5;
                    }
                }
            } else if (time_text.split(":")[0].equalsIgnoreCase("time") && time_text.split(":")[1].equalsIgnoreCase("Filter by Biyearly")) {
                if (current_month >= 6) {
                    if (Integer.parseInt(s.split("-")[1]) == 6)
                        return 1;
                    else if (Integer.parseInt(s.split("-")[1]) == 7)
                        return 2;
                    else if (Integer.parseInt(s.split("-")[1]) == 8)
                        return 3;
                    else if (Integer.parseInt(s.split("-")[1]) == 9)
                        return 4;
                    else if (Integer.parseInt(s.split("-")[1]) == 10)
                        return 5;
                    else if (Integer.parseInt(s.split("-")[1]) == 11)
                        return 6;
                }
                else {
                    if (Integer.parseInt(s.split("-")[1]) == 0)
                        return 1;
                    else if (Integer.parseInt(s.split("-")[1]) == 1)
                        return 2;
                    else if (Integer.parseInt(s.split("-")[1]) == 2)
                        return 3;
                    else if (Integer.parseInt(s.split("-")[1]) == 3)
                        return 4;
                    else if (Integer.parseInt(s.split("-")[1]) == 4)
                        return 5;
                    else if (Integer.parseInt(s.split("-")[1]) == 5)
                        return 6;
                }
            } else if (time_text.split(":")[0].equalsIgnoreCase("time") && time_text.split(":")[1].equalsIgnoreCase("Filter by Year")) {
                if (Integer.parseInt(s.split("-")[0]) == current_year) {
                    if (Integer.parseInt(s.split("-")[1]) == 0 || Integer.parseInt(s.split("-")[1]) == 1)
                        return 0;
                    else if (Integer.parseInt(s.split("-")[1]) == 2 || Integer.parseInt(s.split("-")[1]) == 3)
                        return 1;
                    else if (Integer.parseInt(s.split("-")[1]) == 4 || Integer.parseInt(s.split("-")[1]) == 5)
                        return 2;
                    else if (Integer.parseInt(s.split("-")[1]) == 6 || Integer.parseInt(s.split("-")[1]) == 7)
                        return 3;
                    else if (Integer.parseInt(s.split("-")[1]) == 8 || Integer.parseInt(s.split("-")[1]) == 9)
                        return 4;
                    else if (Integer.parseInt(s.split("-")[1]) == 10 || Integer.parseInt(s.split("-")[1]) == 11)
                        return 5;
                    else
                        return 6;
                }

            } else {
                seven = sdf.format(cal.getTime());
                cal.add(Calendar.DATE, -1);
                six = sdf.format(cal.getTime());
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -2);
                five = sdf.format(cal.getTime());
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -3);
                four = sdf.format(cal.getTime());
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -4);
                three = sdf.format(cal.getTime());
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -5);
                two = sdf.format(cal.getTime());
                cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -6);
                one = sdf.format(cal.getTime());
            }

            if (s.equals(seven))
                i = 6;
            else if (s.equals(six))
                i = 5;
            else if (s.equals(five))
                i = 4;
            else if (s.equals(four))
                i = 3;
            else if (s.equals(three))
                i = 2;
            else if (s.equals(two))
                i = 1;
            else if (s.equals(one))
                i = 0;
            else
                i = -1;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return i;
    }

    @Override
    public void onCallLogsFetched(List<CallLogModel> callLogList) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(new Date().getTime());
            calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) - 6));

            long startTime = calendar.getTimeInMillis();
            calendar.add(Calendar.DATE, 7);
            long endTime = calendar.getTimeInMillis();
            try {
                for (int i = 0; i < callLogList.size(); i++) {
                    CallLogModel callLog = callLogList.get(i);
                    if (!filtertext.equalsIgnoreCase("no")) {
                        if (filtertext.split(":")[0].equalsIgnoreCase("job") && callLog.getPrimarySkill().equalsIgnoreCase(filtertext.split(":")[1])) {
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
                        }else {
                        if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                            callLogs.add(callLog);
                        }
                    }
                }

                for (int i = 0; i < callLogs.size(); i++) {
                    func(callLogs.get(i));
                }

                CALL_LOGS = callLogs;
                mTotalCallsTextView.setText(String.valueOf(callLogs.size()));

                //Calculate the number of unique calls and duplicate code in a differnet thread to reduce load on main thread
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        Set<String> phoneNumbers = new HashSet<>();
                        long totalDuration = 0;
                        int size = 0, size1 = 0;

                        for (CallLogModel callLog : callLogs) {
                            size = phoneNumbers.size();
                            phoneNumbers.add(callLog.getPhoneNumber());
                            size1 = phoneNumbers.size();
                            Log.e("jsdjhdkfjs", String.valueOf(size) + ":" + String.valueOf(size1));
                            if (size == size1)
                                callLogs1.add(callLog);
                            totalDuration += callLog.getDuration();
                        }
                        for (int i = 0; i < callLogs1.size(); i++) {
                            func1(callLogs1.get(i));
                        }
                        if (callLogs.size() != 0) {
                            long averageTimeInDuration = totalDuration / callLogs.size();
                            mHandler.post(() -> {
                                String averageTime = String.valueOf(averageTimeInDuration / 60) + ":" + String.valueOf(averageTimeInDuration % 60) + " mins";

                                mUniqueCallsTextView.setText(String.valueOf(phoneNumbers.size()));
                                mAverageDurationTextView.setText(averageTime);
                                mDuplicateCallsTextView.setText(String.valueOf(callLogs.size() - phoneNumbers.size()));
                            });
                        } else {
                            long averageTimeInDuration = 0;

                            mHandler.post(() -> {
                                String averageTime = String.valueOf(averageTimeInDuration / 60) + ":" + String.valueOf(averageTimeInDuration % 60) + " mins";
                                mTotalCallsTextView.setText("0");
                                mUniqueCallsTextView.setText("0");
                                mAverageDurationTextView.setText("0");
                                mDuplicateCallsTextView.setText("0");
                            });
                        }
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("harsh", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putInt("Totalcalls", callLogs.size());
                        editor.putInt("Duplicates", callLogs.size() - phoneNumbers.size());
                        String s = "";
                        for (int i = 0; i < date_chart1.length; i++)
                            s += String.valueOf(date_chart1[i]) + ":";
                        editor.putString("datachart1", s);

                        editor.commit();


                    }
                }.start();
                setLineChart();

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
    }

}
