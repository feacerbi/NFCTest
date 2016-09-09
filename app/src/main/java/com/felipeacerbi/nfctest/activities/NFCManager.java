package com.felipeacerbi.nfctest.activities;

import android.content.Intent;
import android.content.res.TypedArray;
import android.nfc.NfcAdapter;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

import com.felipeacerbi.nfctest.R;
import com.felipeacerbi.nfctest.adapters.SectionsPagerAdapter;
import com.felipeacerbi.nfctest.interfaces.NFCLaunchableActivity;

public class NFCManager extends AppCompatActivity implements NFCLaunchableActivity, ViewPager.OnPageChangeListener {

    private TypedArray fabIcons;
    private Intent nfcIntent;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcmanager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Retrieve FAB multi action button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabIcons = getResources().obtainTypedArray(R.array.fab_icons);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(
                getSupportFragmentManager(),
                getResources().getStringArray(R.array.tab_titles));

        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(this);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Check for available NFC Adapter.
        if (NfcAdapter.getDefaultAdapter(this) == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Retrieve NFC Intent.
        nfcIntent = getIntent();
    }

    @Override
    public Intent getNFCIntent() {
        return nfcIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nfcmanager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement.
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        fab.setImageResource(fabIcons.getResourceId(position, 0));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
