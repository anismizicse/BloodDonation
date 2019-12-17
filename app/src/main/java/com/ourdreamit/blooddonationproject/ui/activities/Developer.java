package com.ourdreamit.blooddonationproject.ui.activities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ourdreamit.blooddonationproject.ui.fragments.NavigationDrawerFragment;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.SetToolbar;
import com.ourdreamit.blooddonationproject.utils.DataLoader;

public class Developer extends AppCompatActivity {
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.developer);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SetToolbar.context = this;
        getSupportActionBar().setTitle(SetToolbar.setTitle());
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(SetToolbar.setBgColor()));

        NavigationDrawerFragment drawerFragment =
                (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_drawer);
        drawerFragment.setUp(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

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

    public void anis_web(View view){
        view.startAnimation(DataLoader.buttonClick);
        goToLink("http://ourdreamit.com/");
    }
    public void anis_facebook(View view){
        view.startAnimation(DataLoader.buttonClick);
        goToLink("https://www.facebook.com/mizianis");
    }
    public void anis_tweeter(View view){
        view.startAnimation(DataLoader.buttonClick);
        goToLink("https://twitter.com/anismizicse");
    }
    public void anis_in(View view){
        view.startAnimation(DataLoader.buttonClick);
        goToLink("https://www.linkedin.com/in/anis-mizi-14722164/");
    }
    public void anis_mail(View view){
        view.startAnimation(DataLoader.buttonClick);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","anismizicse@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Donor From LifeCycle");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Please write your email body here.");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }


    public void shaheen_web(View view){
        view.startAnimation(DataLoader.buttonClick);
        goToLink("https://skybare.com/");
    }
    public void shaheen_facebook(View view){
        view.startAnimation(DataLoader.buttonClick);
        goToLink("https://www.facebook.com/liveonshaheen");
    }
    public void shaheen_tweeter(View view){
        view.startAnimation(DataLoader.buttonClick);
        goToLink("https://twitter.com/liveonshaheen");
    }
    public void shaheen_in(View view){
        view.startAnimation(DataLoader.buttonClick);
        goToLink("https://www.linkedin.com/in/liveonshaheen/");
    }
    public void shaheen_mail(View view){
        view.startAnimation(DataLoader.buttonClick);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","liveonshaheen@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Donor From LifeCycle");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Please write your email body here.");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }



    public void rashed_web(View view){
        view.startAnimation(DataLoader.buttonClick);
        goToLink("https://skybare.com/");
    }
    public void rashed_facebook(View view){
        view.startAnimation(DataLoader.buttonClick);
        goToLink("https://www.facebook.com/jmrashedbd");
    }
    public void rashed_tweeter(View view){
        view.startAnimation(DataLoader.buttonClick);
        goToLink("https://twitter.com/_jmrashed");
    }
    public void rashed_in(View view){
        view.startAnimation(DataLoader.buttonClick);
        goToLink("https://www.linkedin.com/in/jmrashed/");
    }
    public void rashed_mail(View view){
        view.startAnimation(DataLoader.buttonClick);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","jmrashed@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Donor From LifeCycle");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Please write your email body here.");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    public void goToLink(String url){
        Intent link = new Intent();
        link.setAction(Intent.ACTION_VIEW);
        link.addCategory(Intent.CATEGORY_BROWSABLE);
        link.setData(Uri.parse(url));
        startActivity(link);
    }
}
