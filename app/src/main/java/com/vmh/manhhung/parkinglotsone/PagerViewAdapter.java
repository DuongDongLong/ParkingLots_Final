package com.vmh.manhhung.parkinglotsone;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class PagerViewAdapter extends FragmentPagerAdapter {
    public PagerViewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                ProfileFragment profileFragment=new ProfileFragment();
                return profileFragment;
            case 1:
                AllUsersFragment allUsersFragment=new AllUsersFragment();
                return allUsersFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
