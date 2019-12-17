package com.ourdreamit.blooddonationproject.ui.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.util.Log;

import com.ourdreamit.blooddonationproject.ui.fragments.UsersFragment;


public class UserListingPagerAdapter extends FragmentPagerAdapter {
    private static final Fragment[] sFragments = new Fragment[]{/*UsersFragment.newInstance(UsersFragment.TYPE_CHATS),*/
            UsersFragment.newInstance(UsersFragment.TYPE_ALL)};
    private static final String[] sTitles = new String[]{/*"Chats",*/
            "All Users"};

    public UserListingPagerAdapter(FragmentManager fm) {
        super(fm);
        Log.d("ChatTrace","UserListingPagerAdapter UserListingPagerAdapter(FragmentManager fm)");
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("ChatTrace","UserListingPagerAdapter getItem");
        return sFragments[position];
    }

    @Override
    public int getCount() {
        Log.d("ChatTrace","UserListingPagerAdapter getCount");
        return sFragments.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Log.d("ChatTrace","UserListingPagerAdapter getPageTitle");
        return sTitles[position];
    }
}
