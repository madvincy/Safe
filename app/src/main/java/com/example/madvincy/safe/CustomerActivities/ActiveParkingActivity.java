package com.example.madvincy.safe.CustomerActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.madvincy.safe.R;
import com.example.madvincy.safe.parking_place_account_activities.CustomerPreview;
import com.example.madvincy.safe.parking_place_account_activities.login;
import com.example.madvincy.safe.qrscanner.GenerateQr;
import com.example.madvincy.safe.qrscanner.GenerateQr2;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ActiveParkingActivity extends AppCompatActivity {
    private TextView mTime,mParkname,mParkingLot,mParkPhone;
    private ImageView mProfileImage;
    private String customerOrDriver,parkId, userId, ParkplaceId = "";
    private RatingBar mRatingBar;
    private Button mConfirm;
    private  Long timestamp = 0L;
    private String nulle="Not Available";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_parking);
        mTime = (TextView) findViewById(R.id.time);
        mParkname= (TextView) findViewById(R.id.parkname);
        mParkingLot=(TextView) findViewById(R.id.slot);
        mParkPhone=(TextView) findViewById(R.id.phone);
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        mProfileImage = (ImageView) findViewById(R.id.profileImage);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference parkingRef = FirebaseDatabase.getInstance().getReference().child("Active Parking");
        getUserActiveParkingIds();
        getParkplaceinfo();


    }

    public void getUserActiveParkingIds(){
        DatabaseReference userHistoryDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userId).child("Active Parking");
        userHistoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ActiveParking : dataSnapshot.getChildren()){
                        FetchParkingInformation(ActiveParking.getKey());
                         parkId=ActiveParking.getKey();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    public void FetchParkingInformation(String activeparkId){
        DatabaseReference historyDatabase = FirebaseDatabase.getInstance().getReference().child("Active Parking").child(activeparkId);
        historyDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String activeparkId = dataSnapshot.getKey();




                    if(dataSnapshot.child("starting time").getValue() != null){
                        mTime.setText(dataSnapshot.child("starting time").getValue().toString());
                        timestamp = Long.valueOf(dataSnapshot.child("starting time").getValue().toString());
                    }
                    if(dataSnapshot.child("parkingplace Id").getValue() != null){
                        ParkplaceId = String.valueOf(dataSnapshot.child("parkingplace Id").getValue().toString());
                    }
                    if(dataSnapshot.child("parking Lot").getValue() != null){
                        mParkingLot.setText(dataSnapshot.child("parking Lot").getValue().toString());
                    }




                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mConfirm=(Button)findViewById(R.id.confirm);
        mConfirm.setOnClickListener(new View.OnClickListener() {
//            recoding to wait approval ending session
            @Override
            public void onClick(View v) {




//data to active parking upon confirming
                DatabaseReference parkiRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Parkingplaces").child(ParkplaceId).child("End Parking Requests");
                DatabaseReference custoRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userId).child("End Parking Requests");
                DatabaseReference parkingRef = FirebaseDatabase.getInstance().getReference().child("End Parking Requests");
                String parkingactive_ID = parkingRef.push().getKey();
                parkiRef.child(parkId).setValue(true);
                custoRef.child(parkId).setValue(true);
                Map userInfo = new HashMap();

                userInfo.put("Customer Id",userId );
                userInfo.put("parkingplace Id", ParkplaceId);
                userInfo.put("Starting Time", timestamp);


                userInfo.put("Ending time", getCurrentTimestamp());
                parkingRef.child(parkId).updateChildren(userInfo);
                Toast.makeText(ActiveParkingActivity.this, "Await Approval", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ActiveParkingActivity.this, GenerateQr2.class);
                startActivity(intent);
//                                    Toast.makeText(login.this, "step 2 entering parking place Details", Toast.LENGTH_LONG).show();
                finish();

            }
        });


    }
    private Long getCurrentTimestamp() {
        Long timestampe = System.currentTimeMillis()/1000;
        return timestampe;
    }

    public void getParkplaceinfo(){

        DatabaseReference mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Parkingplaces").child(ParkplaceId).child("user info");
        mCustomerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    if(dataSnapshot.child("car name")!=null){
                        mParkname.setText(dataSnapshot.child("name").getValue().toString());
                    }
                    else{
                        mParkname.setText(nulle);
                    }
                    if(dataSnapshot.child("phone")!=null){
                        mParkPhone.setText(dataSnapshot.child("phone").getValue().toString());
                    }
                    else{
                        mParkPhone.setText(nulle);
                    }
                    if(dataSnapshot.child("profileImageUrl").getValue()!=null){
                        Glide.with(getApplication()).load(dataSnapshot.child("profileImageUrl").getValue().toString()).into(mProfileImage);
                    }

                    int ratingSum = 0;
                    float ratingsTotal = 0;
                    float ratingsAvg = 0;
                    for (DataSnapshot child : dataSnapshot.child("rating").getChildren()){
                        ratingSum = ratingSum + Integer.valueOf(child.getValue().toString());
                        ratingsTotal++;
                    }
                    if(ratingsTotal!= 0){
                        ratingsAvg = ratingSum/ratingsTotal;
                        mRatingBar.setRating(ratingsAvg);
                    }
                    else{
                        mRatingBar.setRating(0);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

}
