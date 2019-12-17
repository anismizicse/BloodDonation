package com.ourdreamit.blooddonationproject.ui.fragments;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ourdreamit.blooddonationproject.R;
import com.ourdreamit.blooddonationproject.dialogs.AllDialog;
import com.ourdreamit.blooddonationproject.ui.activities.BloodRequestOptions;
import com.ourdreamit.blooddonationproject.utils.DataLoader;
import com.ourdreamit.blooddonationproject.utils.MySingleton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class SendRequest extends Fragment implements View.OnClickListener, View.OnTouchListener {
    EditText bloodGroup;
    private EditText patient_name, patientHos, contactPhone, contactAddress, bloodBags, disease, relation, request_message;

    EditText setDate;
    private DatePickerDialog dateDialog;
    private SimpleDateFormat dateFormatter;
    private String requestedBlood = "Blood Group*", mobileNumber;
    FragmentManager manager;

    ProgressDialog dialog;
    ScrollView root;

    String[] bloodList;
    AppCompatButton sendRequest;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.send_request, container, false);

        root = (ScrollView) view.findViewById(R.id.root);
        root.setOnTouchListener(this);

        bloodGroup = (EditText) view.findViewById(R.id.bloodGroup);
        bloodGroup.setInputType(InputType.TYPE_NULL);
        bloodGroup.setOnClickListener(this);

        setDate = (EditText) view.findViewById(R.id.setDate);
        setDate.setInputType(InputType.TYPE_NULL);
        setDate.setOnClickListener(this);

        bloodList = getResources().getStringArray(R.array.blood_group);


        patient_name = (EditText) view.findViewById(R.id.patient_name);
        patientHos = (EditText) view.findViewById(R.id.patientHos);
        contactPhone = (EditText) view.findViewById(R.id.contactPhone);
        contactAddress = (EditText) view.findViewById(R.id.contactAddress);
        bloodBags = (EditText) view.findViewById(R.id.bloodBags);
        disease = (EditText) view.findViewById(R.id.disease);
        relation = (EditText) view.findViewById(R.id.relation);
        request_message = (EditText) view.findViewById(R.id.request_message);


        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        sendRequest = (AppCompatButton) view.findViewById(R.id.sendRequest);
        sendRequest.setOnClickListener(this);

        try {
            manager = getActivity().getFragmentManager();
        }catch (Exception e){

        }

        dialog = new AllDialog(getActivity()).showProgressDialog("", "Sending Request...", true, true);


        return view;
    }


    public void setDate() {

        try {
            Calendar newCalendar = Calendar.getInstance();
            dateDialog = new DatePickerDialog(getActivity(), R.style.datepicker, new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, monthOfYear, dayOfMonth);
                    setDate.setText(dateFormatter.format(newDate.getTime()));
                }

            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

            dateDialog.show();
        }catch (Exception e){

        }

    }

    public void sendRequest() {
        try {
            mobileNumber = contactPhone.getText().toString();
            if (!DataLoader.checkInternet()) {
                new AllDialog(getActivity()).showDialog("", "Please connect to Internet to setup your account", null, null, "OK");

            } else if (patient_name.getText().toString().isEmpty() || patientHos.getText().toString().isEmpty() || contactPhone.getText().toString().isEmpty() ||
                    requestedBlood.equals("Blood Group*") || contactAddress.getText().toString().isEmpty() ||
                    bloodBags.getText().toString().isEmpty() || disease.getText().toString().isEmpty() ||
                    relation.getText().toString().isEmpty() || setDate.getText().toString().isEmpty()) {
                new AllDialog(getActivity()).showDialog("", "Please fill all the * marked fields.", null, null, "OK");


            } else if (!Pattern.matches("[a-zA-Z]+", mobileNumber)) {

                if (mobileNumber.length() == 11 && mobileNumber != null && mobileNumber.charAt(0) == '0'
                        && mobileNumber.charAt(1) == '1' && getCarrier(2)) {
                    sendBloodRequest();
                    //L.t(this,"Valid");

                } else if (mobileNumber.length() == 14 && mobileNumber != null && mobileNumber.charAt(3) == '0'
                        && mobileNumber.charAt(4) == '1' && getCarrier(5)) {
                    mobileNumber = mobileNumber.substring(3);
                    sendBloodRequest();
                    //L.t(this,"Valid");

                } else {
                    new AllDialog(getActivity()).showDialog("", "Invalid phone number.", null, null, "OK");

                }
            }
        }catch (Exception e){

        }
    }

    private void sendBloodRequest() {
        dialog.show();

        String app_server_url = getString(R.string.server_url) + "insert_web_blood_requests.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.hide();
                        //Toast.makeText(SendRequest.this, response, Toast.LENGTH_LONG).show();
                        //Log.d("phoneNumber",mobileNumber+" "+code+" "+response.toString());
                        if (response.equals("success")) {
                            Toast.makeText(getActivity(), "Your Request has been sent successfully.", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getActivity(), BloodRequestOptions.class));
                            //finish();
                        } else {
                            Toast.makeText(getActivity(), "Sorry the request couldn' be sent.", Toast.LENGTH_LONG).show();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            dialog.hide();
                            Toast.makeText(getActivity(), "Network Error", Toast.LENGTH_LONG).show();
                        }catch (Exception e){

                        }

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("phoneverify","true");
                params.put("patient_name", patient_name.getText().toString());
                params.put("request_blood_group", requestedBlood);
                params.put("patient_hospital", patientHos.getText().toString());
                params.put("contact_phone", contactPhone.getText().toString());
                params.put("contact_address", contactAddress.getText().toString());
                params.put("blood_bag_number", bloodBags.getText().toString());
                params.put("patient_disease", disease.getText().toString());
                params.put("contact_relation", relation.getText().toString());
                params.put("donation_time", setDate.getText().toString());
                params.put("request_msg", request_message.getText().toString());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(MySingleton.volley_timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MySingleton.getmInstance(getActivity()).addToRequest(stringRequest);
    }



    public boolean getCarrier(int pos) {
        if (mobileNumber.charAt(pos) == '5' || mobileNumber.charAt(pos) == '6' || mobileNumber.charAt(pos) == '7' ||
                mobileNumber.charAt(pos) == '8' || mobileNumber.charAt(pos) == '9')
            return true;


        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        try {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(v.getContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }catch (Exception e){

        }
        return false;
    }

    @Override
    public void onClick(View view) {


        int id = view.getId();
        switch (id) {
            case R.id.bloodGroup:
                try {
                    new AllDialog(getActivity()).showListDialog("Blood Group*", bloodList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            bloodGroup.setText(bloodList[which]);
                            requestedBlood = bloodList[which];
                        }
                    });
                }catch (Exception e){

                }
                break;
            case R.id.setDate:
                setDate();
                break;
            case R.id.sendRequest:
                sendRequest();
                break;
        }
    }
}
