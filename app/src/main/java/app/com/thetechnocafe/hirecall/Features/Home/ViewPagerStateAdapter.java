package app.com.thetechnocafe.hirecall.Features.Home;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import app.com.thetechnocafe.hirecall.Features.Calls.CallsFragment;
import app.com.thetechnocafe.hirecall.Features.Chat.ChatUserList.ChatUserListFragment;
import app.com.thetechnocafe.hirecall.Features.Candidates.CandidateFragment;
import app.com.thetechnocafe.hirecall.Features.Todo.TodoFragment;

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
                return TodoFragment.getInstance();
            }
            case 1: {
                return ChatUserListFragment.getInstance();
            }
            case 2: {
                return CandidateFragment.getInstance();
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
