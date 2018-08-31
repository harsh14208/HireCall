package app.com.thetechnocafe.hirecall.Features.Dashboard;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import app.com.thetechnocafe.hirecall.Features.Dashboard.Analysis.AnalysisFragment;
import app.com.thetechnocafe.hirecall.Features.Dashboard.Calls.CallFragment;
import app.com.thetechnocafe.hirecall.Features.Dashboard.Qualitative.Qualitative_dashboard;

/**
 * Created by gurleensethi on 19/04/17.
 */

public class ViewPagerStateAdapter extends FragmentStatePagerAdapter {

    private String mOptions[];
    private String filter,time;

    public ViewPagerStateAdapter(FragmentManager fm, String options[],String f,String t) {
        super(fm);
        filter=f;
        time=t;
        Log.e("filter",f);
        mOptions = options;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                return CallFragment.getInstance(filter,time);
            }
            case 1: {
                return new Qualitative_dashboard().getInstance(filter,time);
            }
            case 2: {
                return new AnalysisFragment().getInstance(filter,time);
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
