package com.ourdreamit.blooddonationproject.ui.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;

import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import com.ourdreamit.blooddonationproject.ui.fragments.DoctorChatList;
import com.ourdreamit.blooddonationproject.ui.fragments.NavigationDrawerFragment;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.SetToolbar;
import com.ourdreamit.blooddonationproject.ui.fragments.DoctorCarePhones;
import com.ourdreamit.blooddonationproject.ui.fragments.ActiveDoctorList;
import com.ourdreamit.blooddonationproject.utils.DataLoader;

public class DoctorCare extends AppCompatActivity {
    private Toolbar toolbar;
    String user_type;
    Button message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_care);
        DataLoader.context = this;

        user_type = DataLoader.getUserType();
        message = (Button) findViewById(R.id.message);

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
    public void onBackPressed() {
        try {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch (Exception e){

        }
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
        DataLoader.Adapter adapter = new DataLoader.Adapter(getSupportFragmentManager());

        if (user_type.equals("doctor")) {

            DoctorCarePhones.doctor = true;
            adapter.addFragment(new DoctorChatList(), "Write to Donor");
            adapter.addFragment(new DoctorCarePhones(), "Settings");

        }else if(user_type.equals("donor")){

            DoctorCarePhones.doctor = false;
            adapter.addFragment(new ActiveDoctorList(), "Write to Doctor");
            adapter.addFragment(new DoctorCarePhones(), "Call Doctor");
        }



        viewPager.setAdapter(adapter);
    }



}
