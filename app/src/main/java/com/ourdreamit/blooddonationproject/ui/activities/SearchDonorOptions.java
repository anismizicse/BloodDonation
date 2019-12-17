package com.ourdreamit.blooddonationproject.ui.activities;


import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;

import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.Constants;
import com.ourdreamit.blooddonationproject.utils.SetToolbar;
import com.ourdreamit.blooddonationproject.ui.fragments.SearchByAddress;
import com.ourdreamit.blooddonationproject.ui.fragments.SearchByLocation;
import com.ourdreamit.blooddonationproject.ui.fragments.NavigationDrawerFragment;
import com.ourdreamit.blooddonationproject.utils.DataLoader;

import java.util.Arrays;
import java.util.List;

/**
 * Created by anismizi on 7/3/17.
 */

public class SearchDonorOptions extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_donor_options);

        try {
            toolbar = (Toolbar) findViewById(R.id.app_bar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

            SetToolbar.context = this;
            getSupportActionBar().setTitle(SetToolbar.setTitle());
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(SetToolbar.setBgColor()));
        }catch (Exception e){

        }

        NavigationDrawerFragment drawerFragment =
                (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        drawerFragment.setUp(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        DataLoader.checkLogin(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                //new SharedPrefUtil(this).saveString(DataLoader.LOGOUT, "true");
                DataLoader.context = this;
                DataLoader.removeLocalVars();
                Intent intent = new Intent(this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;

        }
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {
        try {
            DataLoader.Adapter adapter = new DataLoader.Adapter(getSupportFragmentManager());
            adapter.addFragment(new SearchByLocation(), "Search By Location");
            adapter.addFragment(new SearchByAddress(), "search By Address");
            viewPager.setAdapter(adapter);
        }catch (Exception e){

        }
    }




}
