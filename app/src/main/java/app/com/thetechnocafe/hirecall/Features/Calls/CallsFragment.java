package app.com.thetechnocafe.hirecall.Features.Calls;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.com.thetechnocafe.hirecall.Features.CallsMasterDialANumber.CallsMasterDialANumberFragment;
import app.com.thetechnocafe.hirecall.Models.JobModel;
import app.com.thetechnocafe.hirecall.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CallsFragment extends Fragment implements CallHistoryContract.View, CallsMasterDialANumberFragment.JobInteractor {

    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    private JobModel mSelectedJobModel;
    private CallHistoryContract.Presenter mPresenter;

    @Override
    public JobModel getJobModel() {
        return mSelectedJobModel;
    }

    @Override
    public void setJobModel(JobModel jobModel) {
        mSelectedJobModel = jobModel;
    }

    //Instance method
    public static CallsFragment getInstance() {
        return new CallsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calls, container, false);

        ButterKnife.bind(this, view);

        mPresenter = new CallHistoryPresenter();
        mPresenter.subscribe(this);

        initViews();

        return view;
    }

    private void initViews() {
        //Set up view pager
        ViewPagerStateAdapter adapter = new ViewPagerStateAdapter(getChildFragmentManager(), getResources().getStringArray(R.array.job_details_options));
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
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
}
