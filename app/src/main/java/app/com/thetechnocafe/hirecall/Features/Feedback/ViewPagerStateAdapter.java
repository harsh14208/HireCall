package app.com.thetechnocafe.hirecall.Features.Feedback;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import app.com.thetechnocafe.hirecall.Features.FeedbackAttended.FeedbackAttendedFragment;
import app.com.thetechnocafe.hirecall.Features.FeedbackCallMissed.FeedbackCallMissedFragment;

/**
 * Created by gurleensethi on 19/04/17.
 */

public class ViewPagerStateAdapter extends FragmentStatePagerAdapter {

    private String mOptions[];

    public ViewPagerStateAdapter(FragmentManager fm, String options[]) {
        super(fm);
        mOptions = options;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: {
                return FeedbackAttendedFragment.getInstance();
            }
            case 1: {
                return FeedbackCallMissedFragment.getInstance();
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
