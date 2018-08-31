package app.com.thetechnocafe.hirecall.Features.Dashboard.Qualitative;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
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
import app.com.thetechnocafe.hirecall.Features.Dashboard.DashboardActivity;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;

public class Qualitative_dashboard extends Fragment implements QualitativeContract.View,OnChartValueSelectedListener {

    @BindView(R.id.pie_chart1)
    PieChart p1;
    @BindView(R.id.pie_chart2)
    PieChart p2;
    @BindView(R.id.pie_chart3)
    PieChart p3;
    @BindView(R.id.pie_chart4)
    PieChart p4;
    @BindView(R.id.pie_chart5)
    PieChart p5;
    @BindView(R.id.pie_chart6)
    PieChart p6;
    @BindView(R.id.bar_chart1)
    BarChart b1;
    @BindView(R.id.bar_chart2)
    BarChart b2;
    @BindView(R.id.bar_chart3)
    BarChart b3;
    @BindView(R.id.bar_chart4)
    BarChart b4;
    @BindView(R.id.bar_chart5)
    BarChart b5;
    @BindView(R.id.bar_chart6)
    BarChart b6;
    @BindView(R.id.Stats)
    EditText stats;
    private Calendar calendar;
    private long startTime,endTime;
    ArrayList<String>jobIds;
    private String [] months;
    int[] date_chart1,date_chart2,date_chart3,date_chart4,date_chart5,date_chart6;
    ArrayList<PieEntry> entries,entries2,entries3,entries4,entries5,entries6 ;
    ArrayList<String> PieEntryLabels,PieEntryLabels2,PieEntryLabels3,PieEntryLabels4,PieEntryLabels5,PieEntryLabels6;
    PieDataSet pieDataSet,pieDataSet2,pieDataSet3,pieDataSet4,pieDataSet5,pieDataSet6;
    PieData pieData,pieData2,pieData3,pieData4,pieData5,pieData6 ;
    public int z=0,introductory_calls_made=0,introductory_calls_interested=0,shortlist_calls_interested=0,interview_confirmed=0,interview_attended=0,offer_call=0,offer_accepted=0;
    private QualitativeContract.Presenter mPresenter;
    private static String filtertext,time_text;
    private Handler mHandler;
    private List<CallLogModel> CALL_LOGS;
    public static Qualitative_dashboard getInstance(String s,String t) {
        filtertext=s;
        time_text=t;
        return new Qualitative_dashboard();
    }
    private void initViews() {
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) - 6));
        startTime = calendar.getTimeInMillis();
        calendar.add(Calendar.DATE,7);
        endTime = calendar.getTimeInMillis();

        date_chart1=new int[7];
        date_chart2=new int[7];
        date_chart3=new int[7];;
        date_chart4=new int[7];
        date_chart5=new int[7];
        date_chart6=new int[7];
        for(int i=0;i<7;i++)
        {
            date_chart1[i]=0;
            date_chart2[i]=0;
            date_chart3[i]=0;
            date_chart4[i]=0;
            date_chart5[i]=0;
            date_chart6[i]=0;
        }
        Description d=new Description();
        d.setText(" ");
        XAxis xAxis1=b1.getXAxis();
        XAxis xAxis2=b2.getXAxis();
        XAxis xAxis3=b3.getXAxis();
        XAxis xAxis4=b4.getXAxis();
        XAxis xAxis5=b5.getXAxis();
        XAxis xAxis6=b6.getXAxis();
        xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis3.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis4.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis5.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis6.setPosition(XAxis.XAxisPosition.BOTTOM);
        Legend legend=p1.getLegend();
        List<LegendEntry> entrie=new ArrayList<>();
        LegendEntry first=new LegendEntry();
        first.label="Sourced";
        first.formColor=ColorTemplate.rgb("#FF7F50");
        LegendEntry second=new LegendEntry();
        second.label="Duplicates";
        second.formColor=ColorTemplate.rgb("#B22222");
        entrie.add(second);
        entrie.add(first);
        legend.setCustom(entrie);
        /////////////////////////////////////////////////////
        legend=p2.getLegend();
        entrie=new ArrayList<>();
        first=new LegendEntry();
        first.label="Processed";
        first.formColor=ColorTemplate.rgb("#FF7F50");
        second=new LegendEntry();
        second.label="Interacted";
        second.formColor=ColorTemplate.rgb("#B22222");
        entrie.add(first);
        entrie.add(second);
        legend.setCustom(entrie);
        /////////////////////////////////////////////////////
        legend=p3.getLegend();
        entrie=new ArrayList<>();
        first=new LegendEntry();
        first.label="Shortlisted";
        first.formColor=ColorTemplate.rgb("#FF7F50");
        second=new LegendEntry();
        second.label="Processed";
        second.formColor=ColorTemplate.rgb("#B22222");
        entrie.add(first);
        entrie.add(second);
        legend.setCustom(entrie);
        /////////////////////////////////////////////////////
        legend=p4.getLegend();
        entrie=new ArrayList<>();
        first=new LegendEntry();
        first.label="Attended";
        first.formColor=ColorTemplate.rgb("#FF7F50");
        second=new LegendEntry();
        second.label="Shortlisted";
        second.formColor=ColorTemplate.rgb("#B22222");
        entrie.add(first);
        entrie.add(second);
        legend.setCustom(entrie);
        /////////////////////////////////////////////////////
        legend=p5.getLegend();
        entrie=new ArrayList<>();
        first=new LegendEntry();
        first.label="Offered";
        first.formColor=ColorTemplate.rgb("#FF7F50");
        second=new LegendEntry();
        second.label="Attended";
        second.formColor=ColorTemplate.rgb("#B22222");
        entrie.add(first);
        entrie.add(second);
        legend.setCustom(entrie);
        /////////////////////////////////////////////////////
        legend=p6.getLegend();
        entrie=new ArrayList<>();
        first=new LegendEntry();
        first.label="Joined";
        first.formColor=ColorTemplate.rgb("#FF7F50");
        second=new LegendEntry();
        second.label="Offered";
        second.formColor=ColorTemplate.rgb("#B22222");
        entrie.add(first);
        entrie.add(second);
        legend.setCustom(entrie);
        /////////////////////////////////////////////////////

        p1.setDescription(d);
        p2.setDescription(d);
        p3.setDescription(d);
        p4.setDescription(d);
        p5.setDescription(d);
        p6.setDescription(d);
        b1.setDescription(d);
        b2.setDescription(d);
        b3.setDescription(d);
        b4.setDescription(d);
        b5.setDescription(d);
        b6.setDescription(d);
        p1.setUsePercentValues(false);
        p1.setDrawHoleEnabled(false);
        p2.setUsePercentValues(false);
        p2.setDrawHoleEnabled(false);
        p3.setUsePercentValues(false);
        p3.setDrawHoleEnabled(false);
        p4.setUsePercentValues(false);
        p4.setDrawHoleEnabled(false);
        p5.setUsePercentValues(false);
        p5.setDrawHoleEnabled(false);
        p6.setUsePercentValues(false);
        p6.setDrawHoleEnabled(false);
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
                Set<String> phoneNumbers = new HashSet<>();
                Set<String> jobID = new HashSet<String>();
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
    public void setPieChart(String s)
    {
        String val[]=s.split(":");
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("harsh", Context.MODE_PRIVATE);
        int a=sharedPreferences.getInt("Duplicates",0);
        int b=sharedPreferences.getInt("Totalcalls",0);
        entries = new ArrayList<>();
        PieEntryLabels = new ArrayList<String>();

        entries.add(new PieEntry(a, 0));
        entries.add(new PieEntry(b, 1));
        pieDataSet = new PieDataSet(entries," ");

        pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.WHITE);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        p1.setData(pieData);
        p1.invalidate();
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        entries2 = new ArrayList<>();
        PieEntryLabels2 = new ArrayList<String>();
        entries2.add(new PieEntry(Integer.parseInt(val[0]), 0));
        entries2.add(new PieEntry(Integer.parseInt(val[1]), 1));
        pieDataSet2 = new PieDataSet(entries2," ");

        pieData2 = new PieData(pieDataSet2);
        pieData2.setValueTextSize(10f);
        pieData2.setValueTextColor(Color.WHITE);
        pieDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);
        p2.setData(pieData2);
        p2.invalidate();
        ///////////////////////////////////////////////////////////////////////////////////////////////////
                entries3 = new ArrayList<>();
        PieEntryLabels3 = new ArrayList<String>();

        entries3.add(new PieEntry(Integer.parseInt(val[1]), 0));
        entries3.add(new PieEntry(Integer.parseInt(val[2]), 1));
        pieDataSet3 = new PieDataSet(entries3," ");

        pieData3 = new PieData(pieDataSet3);
        pieData3.setValueTextSize(10f);
        pieData3.setValueTextColor(Color.WHITE);
        pieDataSet3.setColors(ColorTemplate.COLORFUL_COLORS);
        p3.setData(pieData3);
        p3.invalidate();

        ///////////////////////////////////////////////////////////////////////////////////////////////////
        entries4 = new ArrayList<>();
        PieEntryLabels4 = new ArrayList<String>();

        entries4.add(new PieEntry(Integer.parseInt(val[2]), 0));
        entries4.add(new PieEntry(Integer.parseInt(val[3]), 1));
        pieDataSet4 = new PieDataSet(entries4," ");

        pieData4= new PieData(pieDataSet4);
        pieData4.setValueTextSize(10f);
        pieData4.setValueTextColor(Color.WHITE);
        pieDataSet4.setColors(ColorTemplate.COLORFUL_COLORS);
        p4.setData(pieData4);
        p4.invalidate();
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        entries5= new ArrayList<>();
        PieEntryLabels5 = new ArrayList<String>();

        entries5.add(new PieEntry(Integer.parseInt(val[3]), 0));
        entries5.add(new PieEntry(Integer.parseInt(val[4]), 1));
        pieDataSet5 = new PieDataSet(entries5," ");

        pieData5 = new PieData(pieDataSet5);
        pieData5.setValueTextSize(10f);
        pieData5.setValueTextColor(Color.WHITE);
        pieDataSet5.setColors(ColorTemplate.COLORFUL_COLORS);
        p5.setData(pieData5);
        p5.invalidate();
        ///////////////////////////////////////////////////////////////////////////////////////////////////
        entries6 = new ArrayList<>();
        PieEntryLabels6 = new ArrayList<String>();

        entries6.add(new PieEntry(Integer.parseInt(val[4]), 0));
        entries6.add(new PieEntry(Integer.parseInt(val[5]), 1));
        pieDataSet6 = new PieDataSet(entries6,"");
        pieData6 = new PieData(pieDataSet6);
        pieData6.setValueTextSize(10f);
        pieData6.setValueTextColor(Color.WHITE);
        pieDataSet6.setColors(ColorTemplate.COLORFUL_COLORS);
        p6.setData(pieData6);
        p6.invalidate();


    }
    public int sortintoWeek(String s) {
        int i = -1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        String seven = "", six = "", five = "", four = "", three = "", two = "", one = "";
        Calendar cal = Calendar.getInstance();
        int current_month = calendar.get(Calendar.MONTH);
        int current_year = calendar.get(Calendar.YEAR);

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
                if (Integer.parseInt(s.split("-")[1])==(current_month+1)) {
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
            } else if (time_text.split(":")[2].equalsIgnoreCase(months[current_month-1])) {

                if (Integer.parseInt(s.split("-")[1])==current_month) {
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

            } else if (time_text.split(":")[2].equalsIgnoreCase(months[current_month-2])) {

                if (Integer.parseInt(s.split("-")[1])==(current_month-1)) {
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

            } else if (time_text.split(":")[2].equalsIgnoreCase(months[current_month-3])) {

                if (Integer.parseInt(s.split("-")[1])==(current_month-2)) {
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
                Log.e(""+current_month,s);
                if (Integer.parseInt(s.split("-")[1]) >= current_month-1 && Integer.parseInt(s.split("-")[1]) <=current_month) {
                    if (Integer.parseInt(s.split("-")[1]) == current_month-2)
                        return 1;
                    else if (Integer.parseInt(s.split("-")[1]) == current_month-1)
                        return 3;
                    else if (Integer.parseInt(s.split("-")[1]) == current_month)
                        return 5;
                }
            }
            else if (time_text.split(":")[2].equalsIgnoreCase("Previous Quarter")) {
                if (Integer.parseInt(s.split("-")[1]) > current_month-5 && Integer.parseInt(s.split("-")[1]) <=current_month-2) {
                    if (Integer.parseInt(s.split("-")[1]) == current_month-5)
                        return 1;
                    else if (Integer.parseInt(s.split("-")[1]) == current_month-4)
                        return 3;
                    else if (Integer.parseInt(s.split("-")[1]) == current_month-3)
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
            } else {
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

        return i;
    }
    public void func(CallLogModel callLog,int size)
    {
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
        if (callLog.getFeedbackReason().equalsIgnoreCase("Introductory Call")) {
            introductory_calls_made++;
        }
        if (callLog.getFeedbackReason().equalsIgnoreCase("Introductory Call")) {
            List<String> reasons = callLog.getFeedback();
            if (reasons.get(reasons.size() - 1).split(" ")[0].equalsIgnoreCase("Candidate")){
                introductory_calls_interested++;
            if (sortintoWeek(formatter.format(callLog.getCallDate())) != -1) {
                date_chart2[sortintoWeek(formatter.format(callLog.getCallDate()))]++;
            }
            }
        }
        if (callLog.getFeedbackReason().equalsIgnoreCase("Shortlist Confirmation Call") || callLog.getFeedbackReason().equalsIgnoreCase("Interview Confirmation Call")) {
            List<String> reasons = callLog.getFeedback();
            if (reasons.get(reasons.size() - 1).split(" ")[0].equalsIgnoreCase("Candidate"))
                shortlist_calls_interested++;

        }
        if (callLog.getFeedbackReason().equalsIgnoreCase("Interview Confirmation Call")) {
            List<String> reasons = callLog.getFeedback();
            if (reasons.get(reasons.size() - 1).split(" ")[0].equalsIgnoreCase("Candidate")) {
                interview_confirmed++;
                if (sortintoWeek(formatter.format(callLog.getCallDate())) != -1)
                    date_chart3[sortintoWeek(formatter.format(callLog.getCallDate()))]++;
            }
        }
        if (callLog.getFeedbackReason().equalsIgnoreCase("Post Interview Call")) {
            interview_attended++;
            if (sortintoWeek(formatter.format(callLog.getCallDate())) != -1)
                date_chart4[sortintoWeek(formatter.format(callLog.getCallDate()))]++;
        }
        if (callLog.getFeedbackReason().equalsIgnoreCase("Offer Call")) {
            offer_call++;
            if (sortintoWeek(formatter.format(callLog.getCallDate())) != -1)
                date_chart5[sortintoWeek(formatter.format(callLog.getCallDate()))]++;
        }
        if (callLog.getFeedbackReason().equalsIgnoreCase("Post Offer Call")) {
            List<String> reasons = callLog.getFeedback();
            if (reasons.get(reasons.size() - 1).equalsIgnoreCase("Candidate Joined")) {
                offer_accepted++;
                if (sortintoWeek(formatter.format(callLog.getCallDate())) != -1)
                    date_chart6[sortintoWeek(formatter.format(callLog.getCallDate()))]++;
            }

        }
    }
    public  void onjobCallLogsFetched(List<CallLogModel> callLogs,int size)
    {
        Calendar calendar=Calendar.getInstance();
               z++;
        for (CallLogModel callLog : callLogs) {
            if (!filtertext.equalsIgnoreCase("no")) {
                if (filtertext.split(":")[0].equalsIgnoreCase("job") && callLog.getPrimarySkill().equalsIgnoreCase(filtertext.split(":")[1])) {
                    if (time_text.split(":")[0].equalsIgnoreCase("time")) {
                        int current_month = calendar.get(Calendar.MONTH);
                        switch (time_text.split(":")[1]) {

                            case "Filter by Week": {
                                if (time_text.split(":")[2].equalsIgnoreCase("Current Week")) {
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        func(callLog, size);
                                        ;
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase("Last Week")) {
                                    calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) - 13));

                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.DATE, 7);
                                    endTime = calendar.getTimeInMillis();

                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        func(callLog, size);
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
                                        func(callLog, size);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase(months[current_month - 1])) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 1));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 1);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        func(callLog, size);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase(months[current_month - 2])) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 2));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 1);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        func(callLog, size);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase(months[current_month - 3])) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 3));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 1);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        func(callLog, size);
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
                                        func(callLog, size);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase("Previous Quarter")) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 8));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 4);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        func(callLog, size);
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
                                        func(callLog, size);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase("Previous")) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 12));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 6);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        func(callLog, size);
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
                                    func(callLog, size);
                                }
                            }
                            break;
                        }
                    } else if (filtertext.split(":")[0].equalsIgnoreCase("client") && callLog.getClient().equalsIgnoreCase(filtertext.split(":")[1]))
                        if (time_text.split(":")[0].equalsIgnoreCase("time")) {
                            int current_month = calendar.get(Calendar.MONTH);
                            switch (time_text.split(":")[1]) {

                                case "Filter by Week": {
                                    if (time_text.split(":")[2].equalsIgnoreCase("Current Week")) {
                                        if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                            func(callLog, size);
                                            ;
                                        }
                                    } else if (time_text.split(":")[2].equalsIgnoreCase("Last Week")) {
                                        calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) - 13));

                                        startTime = calendar.getTimeInMillis();
                                        calendar.add(Calendar.DATE, 7);
                                        endTime = calendar.getTimeInMillis();

                                        if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                            func(callLog, size);
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
                                            func(callLog, size);
                                        }
                                    } else if (time_text.split(":")[2].equalsIgnoreCase(months[current_month - 1])) {
                                        calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 1));
                                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                                        startTime = calendar.getTimeInMillis();
                                        calendar.add(Calendar.MONTH, 1);
                                        endTime = calendar.getTimeInMillis();
                                        if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                            func(callLog, size);
                                        }
                                    } else if (time_text.split(":")[2].equalsIgnoreCase(months[current_month - 2])) {
                                        calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 2));
                                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                                        startTime = calendar.getTimeInMillis();
                                        calendar.add(Calendar.MONTH, 1);
                                        endTime = calendar.getTimeInMillis();
                                        if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                            func(callLog, size);
                                        }
                                    } else if (time_text.split(":")[2].equalsIgnoreCase(months[current_month - 3])) {
                                        calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 3));
                                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                                        startTime = calendar.getTimeInMillis();
                                        calendar.add(Calendar.MONTH, 1);
                                        endTime = calendar.getTimeInMillis();
                                        if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                            func(callLog, size);
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
                                            func(callLog, size);
                                        }
                                    } else if (time_text.split(":")[2].equalsIgnoreCase("Previous Quarter")) {
                                        calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 8));
                                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                                        startTime = calendar.getTimeInMillis();
                                        calendar.add(Calendar.MONTH, 4);
                                        endTime = calendar.getTimeInMillis();
                                        if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                            func(callLog, size);
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
                                            func(callLog, size);
                                        }
                                    } else if (time_text.split(":")[2].equalsIgnoreCase("Previous")) {
                                        calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 12));
                                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                                        startTime = calendar.getTimeInMillis();
                                        calendar.add(Calendar.MONTH, 6);
                                        endTime = calendar.getTimeInMillis();
                                        if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                            func(callLog, size);
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
                                        func(callLog, size);
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
                                        func(callLog, size);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase("Last Week")) {
                                    calendar.set(Calendar.DATE, (calendar.get(Calendar.DATE) - 13));
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.DATE, 7);
                                    endTime = calendar.getTimeInMillis();

                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        func(callLog, size);
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
                                        func(callLog, size);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase(months[current_month - 1])) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 1));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 1);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        func(callLog, size);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase(months[current_month - 2])) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 2));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 1);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        func(callLog, size);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase(months[current_month - 3])) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 3));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 1);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        func(callLog, size);
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
                                        func(callLog, size);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase("Previous Quarter")) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 8));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 4);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        func(callLog, size);
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
                                        func(callLog, size);
                                    }
                                } else if (time_text.split(":")[2].equalsIgnoreCase("Previous")) {
                                    calendar.set(Calendar.MONTH, (calendar.get(Calendar.MONTH) - 12));
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    startTime = calendar.getTimeInMillis();
                                    calendar.add(Calendar.MONTH, 6);
                                    endTime = calendar.getTimeInMillis();
                                    if (callLog.getCallDate() > startTime && callLog.getCallDate() < endTime) {
                                        func(callLog, size);
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
                                    func(callLog, size);
                                }
                            }
                            break;
                        }
                    }
                }
            }
            else{
                func(callLog, size);
            }
        }
        if(z==size) {
            String s=""+introductory_calls_made+":"+introductory_calls_interested+":"+shortlist_calls_interested+":"+interview_attended+":"+offer_call+":"+offer_accepted;
            setPieChart(s);
            setLineChart();
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        }

    private void setLineChart(){
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

        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("harsh", Context.MODE_PRIVATE);
        String s=sharedPreferences.getString("datachart1","");
        for(int i=0;i<7;i++)
            date_chart1[i]=Integer.parseInt(s.split(":")[i]);
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
        yVals.add(new BarEntry(0,date_chart1[0]));
        yVals.add(new BarEntry(1,date_chart1[1]));
        yVals.add(new BarEntry(2,date_chart1[2]));
        yVals.add(new BarEntry(3,date_chart1[3]));
        yVals.add(new BarEntry(4,date_chart1[4]));
        yVals.add(new BarEntry(5,date_chart1[5]));
        yVals.add(new BarEntry(6,date_chart1[6]));

        BarDataSet lineDataSet = new BarDataSet(yVals, "Duplicates");
        lineDataSet.setColors(ColorTemplate.rgb("#0978BE"));
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        BarData lineData = new BarData(dataSets);
        b1.setData(lineData);
        b1.getLegend().setForm(Legend.LegendForm.CIRCLE);
        b1.invalidate();
        b1.getXAxis().setValueFormatter(valueFormatter);
        b1.setTouchEnabled(false);
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();

        yVals2.add(new BarEntry(0,date_chart2[0]));
        yVals2.add(new BarEntry(1,date_chart2[1]));
        yVals2.add(new BarEntry(2,date_chart2[2]));
        yVals2.add(new BarEntry(3,date_chart2[3]));
        yVals2.add(new BarEntry(4,date_chart2[4]));
        yVals2.add(new BarEntry(5,date_chart2[5]));
        yVals2.add(new BarEntry(6,date_chart2[6]));

        lineDataSet = new BarDataSet(yVals2, "Processed");
        lineDataSet.setColors(ColorTemplate.rgb("#0978BE"));
        dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        lineData = new BarData(dataSets);
        b2.setData(lineData);
        b2.getLegend().setForm(Legend.LegendForm.CIRCLE);
        b2.invalidate();
        b2.getXAxis().setValueFormatter(valueFormatter);
        b2.setTouchEnabled(false);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ArrayList<BarEntry> yVals3 = new ArrayList<BarEntry>();

        yVals3.add(new BarEntry(0,date_chart3[0]));
        yVals3.add(new BarEntry(1,date_chart3[1]));
        yVals3.add(new BarEntry(2,date_chart3[2]));
        yVals3.add(new BarEntry(3,date_chart3[3]));
        yVals3.add(new BarEntry(4,date_chart3[4]));
        yVals3.add(new BarEntry(5,date_chart3[5]));
        yVals3.add(new BarEntry(6,date_chart3[6]));
        lineDataSet = new BarDataSet(yVals3, "Shortlisted");
        lineDataSet.setColors(ColorTemplate.rgb("#0978BE"));
        dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        lineData = new BarData(dataSets);
        b3.setData(lineData);
        b3.getLegend().setForm(Legend.LegendForm.CIRCLE);
        b3.invalidate();
        b3.getXAxis().setValueFormatter(valueFormatter);
        b3.setTouchEnabled(false);
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ArrayList<BarEntry> yVals4 = new ArrayList<BarEntry>();

        yVals4.add(new BarEntry(0,date_chart4[0]));
        yVals4.add(new BarEntry(1,date_chart4[1]));
        yVals4.add(new BarEntry(2,date_chart4[2]));
        yVals4.add(new BarEntry(3,date_chart4[3]));
        yVals4.add(new BarEntry(4,date_chart4[4]));
        yVals4.add(new BarEntry(5,date_chart4[5]));
        yVals4.add(new BarEntry(6,date_chart4[6]));

        lineDataSet = new BarDataSet(yVals4, "Attended");
        lineDataSet.setColors(ColorTemplate.rgb("#0978BE"));
        dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        lineData = new BarData(dataSets);
        b4.setData(lineData);
        b4.getLegend().setForm(Legend.LegendForm.CIRCLE);
        b4.invalidate();
        b4.getXAxis().setValueFormatter(valueFormatter);
        b4.setTouchEnabled(false);
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ArrayList<BarEntry> yVals5 = new ArrayList<BarEntry>();

        yVals5.add(new BarEntry(0,date_chart5[0]));
        yVals5.add(new BarEntry(1,date_chart5[1]));
        yVals5.add(new BarEntry(2,date_chart5[2]));
        yVals5.add(new BarEntry(3,date_chart5[3]));
        yVals5.add(new BarEntry(4,date_chart5[4]));
        yVals5.add(new BarEntry(5,date_chart5[5]));
        yVals5.add(new BarEntry(6,date_chart5[6]));

        lineDataSet = new BarDataSet(yVals5, "Offered");
        lineDataSet.setColors(ColorTemplate.rgb("#0978BE"));
        dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        lineData = new BarData(dataSets);
        b5.setData(lineData);
        b5.getLegend().setForm(Legend.LegendForm.CIRCLE);
        b5.invalidate();
        b5.getXAxis().setValueFormatter(valueFormatter);
        b5.setTouchEnabled(false);
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        ArrayList<BarEntry> yVals6 = new ArrayList<BarEntry>();

        yVals6.add(new BarEntry(0, date_chart6[0]));
        yVals6.add(new BarEntry(1, date_chart6[1]));
        yVals6.add(new BarEntry(2, date_chart6[2]));
        yVals6.add(new BarEntry(3, date_chart6[3]));
        yVals6.add(new BarEntry(4, date_chart6[4]));
        yVals6.add(new BarEntry(5, date_chart6[5]));
        yVals6.add(new BarEntry(6, date_chart6[6]));

        lineDataSet = new BarDataSet(yVals6, "Joined");
        lineDataSet.setColors(ColorTemplate.rgb("#0978BE"));
        dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        lineData = new BarData(dataSets);
        b6.setData(lineData);
        b6.getLegend().setForm(Legend.LegendForm.CIRCLE);
        b6.invalidate();
        b6.getXAxis().setValueFormatter(valueFormatter);
        b6.setTouchEnabled(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_qualitative_dashboard, container, false);
        ButterKnife.bind(this, view);

        mHandler = new Handler(Looper.getMainLooper());
        mPresenter = new QualitativePresenter();
        mPresenter.subscribe(this);
        initViews();
        months= getResources().getStringArray(R.array.months);
        return view;
    }


    @Override
    public void onValueSelected(Entry entry, Highlight highlight) {

    }

    @Override
    public void onNothingSelected() {

    }
}
