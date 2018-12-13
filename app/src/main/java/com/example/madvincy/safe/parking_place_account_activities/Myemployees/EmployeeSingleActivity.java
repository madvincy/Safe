package com.example.madvincy.safe.parking_place_account_activities.Myemployees;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.madvincy.safe.R;

import com.example.madvincy.safe.parking_place_account_activities.login;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EmployeeSingleActivity extends AppCompatActivity {
    private String startTime,lote,employeeId, currentUserId, customerId, parkingplaceId, parkingplaceOrCustomer;



    private TextView userPhone,  name,employmentDate,textempid,empid;

    private ImageView userImage, userCarImage;


    private Button mPay;

    private DatabaseReference historyParkingInfoDb;

    private LatLng parkingplaceLatLng;
    private String distance;
    private Double parkingPrice;
    private Boolean CustomerWantsToExit = false;

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_single);


        employeeId = getIntent().getExtras().getString("employeeId");




        employmentDate = (TextView) findViewById(R.id.parkingDate);
        empid= (TextView) findViewById(R.id.empid);
        name = (TextView) findViewById(R.id.userName);
        textempid = (TextView) findViewById(R.id.textempid);
        userPhone = (TextView) findViewById(R.id.userPhone);


        userImage = (ImageView) findViewById(R.id.userImage);
        userCarImage = (ImageView) findViewById(R.id.userCarImage);
        mPay = findViewById(R.id.pay);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        historyParkingInfoDb = FirebaseDatabase.getInstance().getReference().child("").child(employeeId);
        getEmployeeInformation();

    }

    public void getEmployeeInformation(){

    }


    private String getDate(Long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MM-dd-yyyy", cal).toString();
        return date;
    }


    private Long getCurrentTimestamp() {
        Long timestampe = System.currentTimeMillis()/1000;
        return timestampe;
    }
}

