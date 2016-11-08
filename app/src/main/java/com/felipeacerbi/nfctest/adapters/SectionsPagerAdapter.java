package com.felipeacerbi.nfctest.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.fragments.FeedFragment;
import com.felipeacerbi.nfctest.fragments.NFCReadFragment;
import com.felipeacerbi.nfctest.fragments.NFCWriteFragment;
import com.felipeacerbi.nfctest.utils.Constants;

import java.util.Arrays;
import java.util.List;

public class SectionsPagerAdapter extends FragmentPagerAdapter implements TabLayout.OnTabSelectedListener {

    private final FloatingActionButton fab;
    private final FragmentManager fm;
    private TypedArray fabIcons;
    private String[] tabTitles;

    public SectionsPagerAdapter(FragmentManager fm, Context context, FloatingActionButton fab) {
        super(fm);
        this.fm = fm;
        this.fab = fab;

        fabIcons = context.getResources().obtainTypedArray(R.array.fab_icons);
        tabTitles = context.getResources().getStringArray(R.array.tab_titles);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        switch (position) {
            case 0:
                return FeedFragment.newInstance(position + 1);
            case 1:
                return NFCReadFragment.newInstance(position + 1);
            case 2:
                return NFCWriteFragment.newInstance(position + 1);
            default:
                return FeedFragment.newInstance(position + 1);
        }
    }

    @Override
    public int getCount() {
        // Show total pages.
        return Constants.TABS_NUMBER;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        Fragment fragment = fm.getFragments().get(position + 1);
        fab.setImageResource(fabIcons.getResourceId(position, 0));
        fab.setOnClickListener((View.OnClickListener) fragment);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
