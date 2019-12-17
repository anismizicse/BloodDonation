package com.ourdreamit.blooddonationproject.ui.activities;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ourdreamit.blooddonationproject.ui.fragments.NavigationDrawerFragment;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.utils.SetToolbar;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.ImageConverter;
import com.ourdreamit.blooddonationproject.utils.MySingleton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MyStatus extends AppCompatActivity implements View.OnClickListener,
        DataLoader.TalkToProfileDetails, View.OnTouchListener {
    private Toolbar toolbar;
    ImageView pro_pic, setAvailableDate;
    TextView name, blood_group, pro_visible, auto_available;
    AppCompatCheckBox already_donated, available, not_available;
    EditText comment;
    Button updateMyStatus;
    ProgressDialog dialog;
    FragmentManager manager;
    int profile_visible, donated, visibility;
    int donated_already, donations_number;
    String autopro_visible;
    private DatePickerDialog dateDialog;
    private SimpleDateFormat dateFormatter;
    ScrollView root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_status);

        root = (ScrollView) findViewById(R.id.root);
        root.setOnTouchListener(this);

        manager = getFragmentManager();
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        pro_pic = (ImageView) findViewById(R.id.pro_pic);
        setAvailableDate = (ImageView) findViewById(R.id.setAvailableDate);

        setAvailableDate.setOnClickListener(this);
        setAvailableDate.setOnTouchListener(this);

        name = (TextView) findViewById(R.id.name);
        blood_group = (TextView) findViewById(R.id.blood_group);
        pro_visible = (TextView) findViewById(R.id.pro_visible);
        auto_available = (TextView) findViewById(R.id.auto_available);

        comment = (EditText) findViewById(R.id.comment);

        already_donated = (AppCompatCheckBox) findViewById(R.id.already_donated);
        available = (AppCompatCheckBox) findViewById(R.id.available);
        not_available = (AppCompatCheckBox) findViewById(R.id.not_available);

        already_donated.setOnClickListener(this);
        already_donated.setOnTouchListener(this);

        available.setOnClickListener(this);
        available.setOnTouchListener(this);

        not_available.setOnClickListener(this);
        not_available.setOnTouchListener(this);

        updateMyStatus = (Button) findViewById(R.id.updateMyStatus);
        updateMyStatus.setOnClickListener(this);
        updateMyStatus.setOnTouchListener(this);

        dialog = new AllDialog(this).showProgressDialog("",
                "Saving...",true,false);


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

        DataLoader.context = this;
        DataLoader.profileInfo = null;
        DataLoader.getUserFromServer("MyStatus");

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
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.already_donated:
                if (donated == 1 && visibility == 0) {
                    already_donated.setChecked(true);
                    available.setChecked(false);
                    not_available.setChecked(false);
                } else {
                    if (already_donated.isChecked()) {
                        donated_already = 1;
                        already_donated.setChecked(true);
                        available.setChecked(false);
                        not_available.setChecked(false);
                        //Gets todays date(day,month,year)
                        final Calendar cal = Calendar.getInstance();
                        int yy = cal.get(Calendar.YEAR);
                        int mm = 1 + cal.get(Calendar.MONTH);
                        int dd = cal.get(Calendar.DAY_OF_MONTH);

                        String dateInString = dd + "-" + mm + "-" + yy;
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        Calendar c = Calendar.getInstance();
                        try {
                            c.setTime(sdf.parse(dateInString));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        c.add(Calendar.DATE, 90);
                        sdf = new SimpleDateFormat("dd-MM-yyyy");
                        Date resultdate = new Date(c.getTimeInMillis());
                        dateInString = sdf.format(resultdate);

                        autopro_visible = dateInString;
                        profile_visible = 0;

                        auto_available.setText(dateInString);

                        new AllDialog(this).showDialog("","Are you sure? Your profile will be hidden from donors list for next 3 months. " +
                                "You won\'t be able to set your profile abailable within this 3 months.",null,null,"OK");




                    } else {
                        already_donated.setChecked(false);
                    }
                }
                break;
            case R.id.available:
                if (donated == 1 && visibility == 0) {
                    already_donated.setChecked(true);
                    available.setChecked(false);
                    not_available.setChecked(false);
                } else {
                    if (available.isChecked()) {
                        profile_visible = 1;
                        donated_already = 0;
                        already_donated.setChecked(false);
                        available.setChecked(true);
                        not_available.setChecked(false);
                    } else {
                        available.setChecked(false);
                    }
                }
                break;
            case R.id.not_available:
                if (donated == 1 && visibility == 0) {
                    already_donated.setChecked(true);
                    available.setChecked(false);
                    not_available.setChecked(false);
                } else {
                    if (not_available.isChecked()) {
                        profile_visible = 0;
                        donated_already = 0;
                        already_donated.setChecked(false);
                        available.setChecked(false);
                        not_available.setChecked(true);
                    } else {
                        not_available.setChecked(false);
                    }
                }
                break;
            case R.id.setAvailableDate:
                v.startAnimation(DataLoader.buttonClick);
                if (donated == 1 && visibility == 0) {

                } else {
                    setAutopro_visible();
                }
                break;
            case R.id.updateMyStatus:
                v.startAnimation(DataLoader.buttonClick);
                if (!auto_available.getText().toString().equals("DD-MM-YY")) {
                    autopro_visible = auto_available.getText().toString();
                }
                donations_number = 1;
                if (donated_already == 1) {
                    donations_number += Integer.parseInt(DataLoader.profileInfo.donations_number);
                }

                if ((already_donated.isChecked() || not_available.isChecked()) && comment.getText().toString().isEmpty()) {

                    new AllDialog(this).showDialog("Write comment","Please write your comment.",null,null,"OK");


                }else {

                    dialog.show();

                    String app_server_url = getString(R.string.server_url) + "update_mystatus.php";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //Log.d("myStatus", DataLoader.getUserPhone() + " " + profile_visible + " " + autopro_visible + " " + response.toString());
                                    dialog.hide();
                                    if (response.equals("updated")) {
                                        Toast.makeText(MyStatus.this, "Status updated Successfully", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(MyStatus.this, MainActivity.class);
                                        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    } else if (response.equals("error")) {
                                        new AllDialog(MyStatus.this).showDialog("","Sorry ! Your status was updated. Please try again later.",
                                                null,null,"OK");

                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //Toast.makeText(getActivity(),"Network Error",Toast.LENGTH_LONG).show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            //params.put("phoneverify","true");
                            params.put("phone", DataLoader.getUserPhone());
                            params.put("pro_visible", "" + profile_visible);
                            params.put("autopro_visible", autopro_visible);
                            params.put("already_donated", "" + donated_already);
                            params.put("donations_number", "" + donations_number);
                            params.put("comment", comment.getText().toString());
                            return params;
                        }
                    };
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    MySingleton.getmInstance(this).addToRequest(stringRequest);
                }

                break;
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

    @Override
    public void setUserDetails() {
        setProfilePic();

        name.setText(DataLoader.profileInfo.fname + " " + DataLoader.profileInfo.lname);
        blood_group.setText(DataLoader.profileInfo.blood_group);
        if (DataLoader.profileInfo.pro_visible.equals("1")) {
            available.setChecked(true);
            pro_visible.setText("Available");
        } else {
            pro_visible.setText("Not Available");
        }

        donated = Integer.parseInt(DataLoader.profileInfo.already_donated);
        visibility = Integer.parseInt(DataLoader.profileInfo.pro_visible);
        if (donated == 1 && visibility == 0) {
            already_donated.setChecked(true);
            profile_visible = 0;
            donated_already = 1;
        } else if (visibility == 1) {
            available.setChecked(true);
            profile_visible = 1;
            donated_already = 0;
        } else if (donated == 0 && visibility == 0) {
            not_available.setChecked(true);
            profile_visible = 0;
            donated_already = 0;
        }

        if (!DataLoader.profileInfo.autopro_visible.equals("na")) {
            auto_available.setText(DataLoader.profileInfo.autopro_visible);
        }
    }

    public void setProfilePic() {
        String url = DataLoader.profileInfo.pic_path;
        Glide.with(this).load(url)
                .apply(RequestOptions.placeholderOf(R.drawable.userpic_default))
                .override(70,70)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(pro_pic);

        /*ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(response, 100);
                pro_pic.setImageBitmap(circularBitmap);
            }
        }, 0, 0, ImageView.ScaleType.CENTER_CROP, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(MyStatus.this, error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        imageRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(this).addToRequest(imageRequest);*/
    }

    public void setAutopro_visible() {
        Calendar newCalendar = Calendar.getInstance();
        dateDialog = new DatePickerDialog(this, R.style.datepicker, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                auto_available.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        dateDialog.show();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(v.getContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }catch (Exception e){

        }
        return false;
    }
}
