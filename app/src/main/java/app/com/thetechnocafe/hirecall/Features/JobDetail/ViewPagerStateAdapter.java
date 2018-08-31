package app.com.thetechnocafe.hirecall.Features.JobDetail;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import app.com.thetechnocafe.hirecall.Features.JobDetailsCallHistory.JobDetailsCallHistoryFragment;
import app.com.thetechnocafe.hirecall.Features.JobDetailsDialNumber.JobDetailsDialNumberFragment;
import app.com.thetechnocafe.hirecall.Models.JobModel;

/**
 * Created by gurleensethi on 19/04/17.
 */

public class ViewPagerStateAdapter extends FragmentStatePagerAdapter {

    private String mOptions[];
    private JobModel mJob;

    public ViewPagerStateAdapter(FragmentManager fm, String options[], JobModel job) {
        super(fm);
        mOptions = options;
        mJob = job;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                return JobDetailsDialNumberFragment.getInstance(mJob);
            }
            case 1: {
                return JobDetailsCallHistoryFragment.getInstance(mJob);
            }
            default: {
                return null;
            }
        }
    }

    @Override
    public int getCount() {
        return mOptions.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mOptions[position];
    }
}
