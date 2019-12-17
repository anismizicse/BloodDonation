package com.ourdreamit.blooddonationproject;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.ourdreamit.blooddonationproject.ui.activities.Login;
import com.ourdreamit.blooddonationproject.ui.activities.LoginSignupOptions;
import com.ourdreamit.blooddonationproject.ui.activities.MainActivity;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.NetworkSchedulerService;
import com.ourdreamit.blooddonationproject.utils.SharedPrefUtil;

public class WelcomeActivity extends AppCompatActivity {
    LinearLayout welcome_logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scheduleJob();
        }

        DataLoader.context = this;

        welcome_logo = (LinearLayout) findViewById(R.id.welcome_logo);

        final Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        final Animation animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        welcome_logo.startAnimation(animationFadeIn);

        animationFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                welcome_logo.startAnimation(animationFadeOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animationFadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                String login = new SharedPrefUtil(WelcomeActivity.this).getString(DataLoader.LOGIN, "false");
                String logout = new SharedPrefUtil(WelcomeActivity.this).getString(DataLoader.LOGOUT, "false");
                String phone = new SharedPrefUtil(WelcomeActivity.this).getString(DataLoader.PHONE_NUMBER, null);
                String password = new SharedPrefUtil(WelcomeActivity.this).getString(DataLoader.PASSWORD, null);
                if(logout.equals("false") && login.equals("true") && phone != null && password != null){
                    //startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else if(logout.equals("true") && login.equals("true") && phone != null && password != null){
                    //startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                    Intent intent = new Intent(WelcomeActivity.this, LoginSignupOptions.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else {
                    //startActivity(new Intent(WelcomeActivity.this, Login.class));
                    Intent intent = new Intent(WelcomeActivity.this, LoginSignupOptions.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleJob() {
        JobInfo myJob = new JobInfo.Builder(0, new ComponentName(this, NetworkSchedulerService.class))
                .setRequiresCharging(false)
                .setMinimumLatency(1000)
                .setOverrideDeadline(2000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(myJob);
    }

    @Override
    protected void onStop() {
        // A service can be "started" and/or "bound". In this case, it's "started" by this Activity
        // and "bound" to the JobScheduler (also called "Scheduled" by the JobScheduler). This call
        // to stopService() won't prevent scheduled jobs to be processed. However, failing
        // to call stopService() would keep it alive indefinitely.
        stopService(new Intent(this, NetworkSchedulerService.class));
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start service and provide it a way to communicate with this class.
        Intent startServiceIntent = new Intent(this, NetworkSchedulerService.class);
        startService(startServiceIntent);
    }

}
