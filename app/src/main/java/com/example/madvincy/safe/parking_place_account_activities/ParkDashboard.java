package com.example.madvincy.safe.parking_place_account_activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.madvincy.safe.R;
import com.example.madvincy.safe.booking.ParkBookings;
import com.example.madvincy.safe.parking_place_account_activities.ActiveParking.ActiveCarsParkingRealtime;
import com.example.madvincy.safe.parking_place_account_activities.CarParkHistory.ParkHistory;
import com.example.madvincy.safe.parking_place_account_activities.Myemployees.Employee;
import com.example.madvincy.safe.parking_place_account_activities.ParkSettings.ParkSetting;
import com.example.madvincy.safe.parkinghistory.CustomerParkingHistory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class ParkDashboard extends AppCompatActivity {
    private Switch mWorkingSwitch;
    private CardView bankcardId;
    private CardView EmployeeId;
    private CardView bookingsId;
    private CardView ActiveId,AccInfo;
    private FirebaseAuth auth;
    private  String park;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_dashboard2);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParkDashboard.this, QrScanning.class));
            }
        });
        bankcardId = (CardView) findViewById(R.id.bankcardId);
        bankcardId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ParkDashboard.this, CustomerParkingHistory.class);
                intent.putExtra("customerOrParkingplace", "Parkingplaces");
                startActivity(intent);
            }
        });
        EmployeeId = (CardView) findViewById(R.id.addEmployee);
        EmployeeId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ParkDashboard.this, Employee.class);
                startActivity(intent);
            }
        });
        AccInfo = (CardView) findViewById(R.id.AccInfoId);
        AccInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ParkDashboard.this, ParkSetting.class);
                startActivity(intent);
            }
        });
        bookingsId = (CardView) findViewById(R.id.booking);
        bookingsId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ParkDashboard.this, ParkBookings.class);
                startActivity(intent);
            }
        });
        ActiveId = (CardView) findViewById(R.id.ActiveId);
        ActiveId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ParkDashboard.this, ActiveCarsParkingRealtime.class);
                startActivity(intent);
            }
        });
        mWorkingSwitch = (Switch) findViewById(R.id.workingSwitch);
        mWorkingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    connectParkingPlace();
                }else{
                    disconnectParkingPlace();
                }
            }
        });
    }
    private void connectParkingPlace() {
        auth = FirebaseAuth.getInstance();
          park=auth.getCurrentUser().getUid();
       DatabaseReference  parkingplaceLocationRef = FirebaseDatabase.getInstance().getReference().child("parkingPlacesOpen").child(park);

        Map userInfo = new HashMap();
        userInfo.put("status", 1);
       parkingplaceLocationRef.updateChildren(userInfo);

    }
    private void disconnectParkingPlace() {
        DatabaseReference  parkingplaceLocationRefed = FirebaseDatabase.getInstance().getReference().child("parkingPlacesOpen").child(park);
        parkingplaceLocationRefed .removeValue();

    }
}
