package com.ourdreamit.blooddonationproject.ui.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.ourdreamit.blooddonationproject.ui.fragments.NavigationDrawerFragment;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.SetToolbar;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.SharedPrefUtil;


public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    FragmentManager manager;
    LinearLayout adminSettings, blood_request, doctorCare, chatActivity;
    String user_type;
    int backPress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backPress = 0;

        if (getIntent().getBooleanExtra("Exit me", false)) {
            finish();
        }

        adminSettings = (LinearLayout) findViewById(R.id.adminSettings);
        doctorCare = (LinearLayout) findViewById(R.id.doctorCare);
        chatActivity = (LinearLayout) findViewById(R.id.chatActivity);
        blood_request = (LinearLayout) findViewById(R.id.blood_request);

        DataLoader.context = this;
        user_type = DataLoader.getUserType();

        if (user_type.equals("donor")) {
            adminSettings.setVisibility(View.GONE);
            //doctorMessages.setVisibility(View.GONE);
        } else if (user_type.equals("doctor")) {
            blood_request.setVisibility(View.GONE);
            adminSettings.setVisibility(View.GONE);
            chatActivity.setVisibility(View.GONE);
        } else if (user_type.equals("admin")) {
            doctorCare.setVisibility(View.GONE);
            blood_request.setVisibility(View.GONE);
            //doctorMessages.setVisibility(View.GONE);
        }

        manager = getFragmentManager();

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

        DataLoader.doctorCarePanel = false;
        DataLoader.donorToDoctorChat = false;
        DataLoader.doctorToDonorChat = false;

        DataLoader.checkLogin(this);

    }

    @Override
    public void onBackPressed() {
        try {
            backPress += 1;
            if (backPress == 1) {
                Toast.makeText(this, "Press once again to exit", Toast.LENGTH_SHORT).show();
            } else if (backPress == 2) {
                finish();
            } else {
                finish();
            }
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

    public void searchDonor(View view) {
        view.startAnimation(DataLoader.buttonClick);
        startActivity(new Intent(this, SearchDonorOptions.class));
    }

    public void bloodRequest(View view) {
        view.startAnimation(DataLoader.buttonClick);
        startActivity(new Intent(this, BloodRequestOptions.class));
    }

    public void profile(View view) {
        view.startAnimation(DataLoader.buttonClick);
        if (!DataLoader.checkInternet()) {
            new AllDialog(this).showDialog("", "Please connect to Internet to see profile details", null, null, "OK");

        } else {
            startActivity(new Intent(this, ProfileDetails.class));
        }
    }

    public void doctorCare(View view) {
        view.startAnimation(DataLoader.buttonClick);
        if (!DataLoader.checkInternet()) {
            new AllDialog(this).showDialog("", "Please connect to Internet", null, null, "OK");

        }else {
            try {
                DataLoader.tokenChanged = false;
                DataLoader.context = this;
                DataLoader.profileInfo = null;
                DataLoader.getUserFromServer("MainActivityFcmCheck");

                DataLoader.doctorCarePanel = true;
                startActivity(new Intent(this, DoctorCare.class));
            }catch (Exception e){

            }
        }

    }

    public void searchDoctor(View view) {
        view.startAnimation(DataLoader.buttonClick);
        startActivity(new Intent(this, SearchDoctor.class));
    }

    public void searchHospital(View view) {
        view.startAnimation(DataLoader.buttonClick);
        startActivity(new Intent(this, Search_Hospital.class));
    }

    public void hotLine(View view) {
        view.startAnimation(DataLoader.buttonClick);
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + DataLoader.getHotline()));
        startActivity(intent);
    }

    public void liveChat(View view) {
        view.startAnimation(DataLoader.buttonClick);
        if (!DataLoader.checkInternet()) {
            new AllDialog(this).showDialog("", "Please connect to Internet", null, null, "OK");

        } else {
            DataLoader.tokenChanged = false;

            DataLoader.context = this;
            DataLoader.profileInfo = null;
            DataLoader.getUserFromServer("MainActivityFcmCheck");

            String userType = new SharedPrefUtil(this).getString(DataLoader.USERTYPE, "na");
            String chatAvailability = new SharedPrefUtil(this).getString(DataLoader.LIVECHATLOGIN, null);

            if (chatAvailability != null && chatAvailability.equals("true")) {
                String adminChatLogin = new SharedPrefUtil(this).getString(DataLoader.ADMINCHATLOGIN, "false");
                //Log.d("ChatCheck", userType + " " + adminChatLogin);
                if (userType.equals("admin") && !userType.equals("na")) {
                    if (adminChatLogin.equals("true")) {
                        startActivity(new Intent(this, AdminChatList.class));
                    } else {
                        //startActivity(new Intent(this, SplashActivity.class));
                        LoginActivity.startIntent(this);
                    }

                } else {
                    startActivity(new Intent(this, AdminList.class));
                }
            } else {
                LoginActivity.startIntent(this);
            }


        }

    }


    public void myStatus(View view) {
        view.startAnimation(DataLoader.buttonClick);
        startActivity(new Intent(this, MyStatus.class));
    }

    public void tobeProud(View view) {
        view.startAnimation(DataLoader.buttonClick);
        startActivity(new Intent(this, ToBeProud.class));
    }

    public void aboutUs(View view) {
        view.startAnimation(DataLoader.buttonClick);
        startActivity(new Intent(this, AboutUs.class));
    }



    public void adminSettings(View view) {
        view.startAnimation(DataLoader.buttonClick);
        startActivity(new Intent(this, AdminSettings.class));
    }

}
