package com.felipeacerbi.nfctest.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.fragments.NFCReadFragment;
import com.felipeacerbi.nfctest.fragments.NFCWriteFragment;
import com.felipeacerbi.nfctest.utils.Constants;

import java.util.Arrays;
import java.util.List;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final String[] tabTitles;

    public SectionsPagerAdapter(FragmentManager fm, String[] tabTitles) {
        super(fm);
        this.tabTitles = tabTitles;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        switch (position) {
            case 0:
                return NFCReadFragment.newInstance(position + 1);
            case 1:
                return NFCWriteFragment.newInstance(position + 1);
            default:
                return NFCReadFragment.newInstance(position + 1);
        }
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return Constants.TABS_NUMBER;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}