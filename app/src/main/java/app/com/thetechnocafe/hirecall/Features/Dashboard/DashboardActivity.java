package app.com.thetechnocafe.hirecall.Features.Dashboard;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import app.com.thetechnocafe.hirecall.Data.FirebaseDB;
import app.com.thetechnocafe.hirecall.Models.CallLogModel;
import app.com.thetechnocafe.hirecall.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.Disposable;


public class DashboardActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    View mView;
    @BindView(R.id.dashboard_spinner)
    Spinner filter;
    @BindView(R.id.dashboard_spinner2)
    Spinner filter2;
    @BindView(R.id.filter_layout)
    LinearLayout filter_layout;
    @BindView(R.id.filter_button)
    Button filter_button;
    @BindView(R.id.set_time_button)
    Button filter_button2;
    @BindView(R.id.filter2)
    Button filter_2;
    @BindView(R.id.time_filter_button)
    Button time_filter;
    @BindView(R.id.set_time_filter_button)
    Button set_time_filter;

    private String filtertext ="no",time_text="Time:Filter by Week:Current Week";
    private static String[] DATE_FILTER_OPTIONS,months,DATE_FILTER_OPTIONS1;
    private ArrayList<String> jobs = new ArrayList<String>();
    private ArrayList<String> clients = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);

        DATE_FILTER_OPTIONS1 = getResources().getStringArray(R.array.date_filter1);
        months = getResources().getStringArray(R.array.months);
        getData();
        mView = this.getWindow().getDecorView().getRootView();
        initViews();
        Calendar calendar=Calendar.getInstance();
        int current_month = calendar.get(Calendar.MONTH);
        time_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_time_filter.setVisibility(View.VISIBLE);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, DATE_FILTER_OPTIONS1);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                filter.setAdapter(adapter);

                filter_layout.setVisibility(View.VISIBLE);
                filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ((TextView) view).setTextColor(Color.BLACK);
                        if (position==0)
                        {
                            filter_button.setVisibility(View.GONE);
                        }
                        if(position==1){
                            filter2.setVisibility(View.VISIBLE);
                            ArrayList<String> f1=new ArrayList<String>();
                            f1.add("Current Week");
                            f1.add("Last Week");
                            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, f1);
                            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            filter2.setAdapter(adapter1);
                            filter_layout.setVisibility(View.VISIBLE);
                            filter2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    ((TextView) view).setTextColor(Color.BLUE);
                                    setval1("Time:" +"Filter by Week:"+f1.get(position));

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                        else if(position==2)
                        {
                            filter2.setVisibility(View.VISIBLE);
                            ArrayList<String> f1=new ArrayList<String>();
                            f1.add(months[current_month]);
                            f1.add(months[current_month-1]);
                            f1.add(months[current_month-2]);
                            f1.add(months[current_month-3]);
                            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item,f1);
                            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            filter2.setAdapter(adapter1);
                            filter_layout.setVisibility(View.VISIBLE);
                            filter2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    ((TextView) view).setTextColor(Color.BLACK);
                                    setval1("Time:" +"Filter by Month:"+f1.get(position));
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                        else if(position==3)
                        {
                            filter2.setVisibility(View.VISIBLE);
                            ArrayList<String> f1=new ArrayList<String>();
                            f1.add("Current Quarter");
                            f1.add("Previous Quarter");
                            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, f1);
                            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            filter2.setAdapter(adapter1);
                            filter_layout.setVisibility(View.VISIBLE);
                            filter2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    ((TextView) view).setTextColor(Color.BLACK);
                                    setval1("Time:" +"Filter by Quarter:"+f1.get(position));
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                        else if(position==4)
                        {
                            filter2.setVisibility(View.VISIBLE);
                            ArrayList<String> f1=new ArrayList<String>();
                            f1.add("Current");
                            f1.add("Previous");
                            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item,f1 );
                            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            filter2.setAdapter(adapter1);
                            filter_layout.setVisibility(View.VISIBLE);
                            filter2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    ((TextView) view).setTextColor(Color.BLACK);
                                    setval1("Time:" +"Filter by Biyearly:"+f1.get(position));
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                        else if(position==5)
                        {
                            setval1("Time:" +"Filter by Year:Current Year");
                        }

                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
        set_time_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initViews();
                filter_layout.setVisibility(View.GONE);
                filter_button2.setText(time_text.split(":")[2]);
                filter_button2.setVisibility(View.VISIBLE);
                filter2.setVisibility(View.GONE);
                set_time_filter.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }

        if (id == R.id.job_filter) {
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, jobs);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            filter.setAdapter(adapter1);
            filter_layout.setVisibility(View.VISIBLE);
            filter2.setVisibility(View.GONE);
            filter_button.setVisibility(View.VISIBLE);
            filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((TextView) view).setTextColor(Color.BLACK);
                    setval("Job:" + jobs.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else if (id == R.id.company_filter) {
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, clients);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            filter.setAdapter(adapter1);
            filter2.setVisibility(View.GONE);
            filter_layout.setVisibility(View.VISIBLE);
            filter_button.setVisibility(View.VISIBLE);
            filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((TextView) view).setTextColor(Color.BLACK);
                    setval("Client:" + clients.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }

            });
        } else {
            filter_layout.setVisibility(View.GONE);
        }
        filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initViews();
                filter_layout.setVisibility(View.GONE);
                filter_2.setText(filtertext.split(":")[1]);
                filter_2.setVisibility(View.VISIBLE);
                filter2.setVisibility(View.GONE);
                filter_button.setVisibility(View.GONE);
                time_filter.setVisibility(View.VISIBLE);

            }
        });


            return super.onOptionsItemSelected(item);
    }

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

    public void getData() {
        Disposable disposable = FirebaseDB.getInstance()
                .getListOfAllCallLogs()
                .subscribe(callLogModels -> {
                    onCallLogsFetched(callLogModels);
                });
    }

    public void setval(String s) {
        filtertext = s;
    }
    public void setval1(String s) { time_text = s; }

    private void initViews() {
        //Setup toolbar

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Log.e("client filter",filtertext);
        ViewPagerStateAdapter adapter = new ViewPagerStateAdapter(getSupportFragmentManager(), getResources().getStringArray(R.array.dashboard_options),filtertext,time_text);
        mViewPager.setAdapter(adapter);

        mTabLayout.setupWithViewPager(mViewPager);
    }

}
