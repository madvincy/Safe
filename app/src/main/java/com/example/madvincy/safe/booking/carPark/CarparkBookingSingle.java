package com.example.madvincy.safe.booking.carPark;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.madvincy.safe.R;

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

public class CarparkBookingSingle extends AppCompatActivity {
    private String timeBooked,bookinghour,timeformat,bookingId, currentUserId, customerId, parkingplaceId, parkingplaceOrCustomer;
    private Long timestamp;
    private TextView parkingLocation;
    private TextView rideDistance;
    private TextView parkingDate, lot;
    private TextView userName;
    private TextView userPhone, from, parkingEmployee;

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
        setContentView(R.layout.activity_carparkbookingsingle);


        bookingId = getIntent().getExtras().getString("bookingId");




        parkingDate = (TextView) findViewById(R.id.parkingDate);
        userName = (TextView) findViewById(R.id.lot);
        lot = (TextView) findViewById(R.id.userName);
        from = (TextView) findViewById(R.id.from);
        userPhone = (TextView) findViewById(R.id.userPhone);
        parkingEmployee = (TextView) findViewById(R.id.parkingEmployee);

        userImage = (ImageView) findViewById(R.id.userImage);
        userCarImage = (ImageView) findViewById(R.id.userCarImage);
        mPay = findViewById(R.id.pay);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        historyParkingInfoDb = FirebaseDatabase.getInstance().getReference().child("Booking").child(bookingId);
        getParkingInformation();

    }

    private void getParkingInformation() {
        historyParkingInfoDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (child.getKey().equals("Customer Id")) {
                            customerId = child.getValue().toString();
                            if (!customerId.equals(currentUserId)) {
                                parkingplaceOrCustomer = "Parkingplaces";
                                getUserInformation("Customers", customerId);
                                getUserCarInformation("Customers", customerId);
                                displayCustomerRelatedObjects();
                            }
                        }
//                        if (child.getKey().equals("parkingplace Id")){
//                            parkingplaceId = child.getValue().toString();
//                            if(!parkingplaceId.equals(currentUserId)){
//                                parkingplaceOrCustomer = "Customers";
//                                getUserInformation("Parkingplaces", parkingplaceId);
//
//                            }
//                        }
                        if (child.getKey().equals("booking date")) {
                            parkingDate.setText(getDate(Long.valueOf(child.getValue().toString())));

                        }

                        if(dataSnapshot.child("booking time format").getValue() != null){
                            timeformat= String.valueOf(dataSnapshot.child("booking time format").getValue().toString());
                        }
                        if (child.getKey().equals("booking hour")) {
                          bookinghour = getDatethen(Long.valueOf(child.getValue().toString()));

                        }
                        if(dataSnapshot.child("booking minute").getValue() != null) {
                             timestamp = Long.valueOf(dataSnapshot.child("booking minute").getValue().toString());
                        }

                        timeBooked = bookinghour + ":" + timestamp  + "-" +  timeformat;

//                        if (child.getKey().equals()) {
//                            .setText(getDate(Long.valueOf(child.getValue().toString())));
//                             String Format = child.getValue().toString();
//
//                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void getUserInformation(String otherUserParkingplaceOrCustomer, String otherUserId) {
        DatabaseReference mOtherUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(otherUserId).child("user info");
        mOtherUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("full names") != null) {
                        userName.setText(map.get("full names").toString());
                    }
                    if (map.get("phone number") != null) {
                        userPhone.setText(map.get("phone number").toString());
                    }
                    if (map.get("profileImageUrl") != null) {
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(userImage);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void getUserCarInformation(String otherUserParkingplaceOrCustomer, String otherUserId) {
        DatabaseReference mOtherUserDB = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(otherUserId).child("car info");
        mOtherUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                    if (map.get("profileImageUrl") != null) {
                        Glide.with(getApplication()).load(map.get("profileImageUrl").toString()).into(userCarImage);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private String getDate(Long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("MM-dd-yyyy", cal).toString();
        return date;
    }

    private String getDatethen(Long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("hh:mm ", cal).toString();
        return date;
    }

    private void displayCustomerRelatedObjects() {
        mPay.setVisibility(View.VISIBLE);

        if (CustomerWantsToExit) {
            mPay.setEnabled(false);
        } else {
            mPay.setEnabled(true);
        }
        mPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                endParking();
            }
        });
    }

    private void endParking(){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Parkingplaces").child(currentUserId).child("Cancelled Bookings");
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId).child("Cancelled Bookings");
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child("Cancelled Bookings");
        String requestId = historyRef.push().getKey();
        driverRef.child(requestId).setValue(true);
        customerRef.child(requestId).setValue(true);

        HashMap map = new HashMap();
        map.put("Cancelled on", getCurrentTimestamp());

        historyRef.child(requestId).updateChildren(map);

        //removing it as active parking and as ending request
        DatabaseReference parkiRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Parkingplaces").child(currentUserId).child("Booking").child(bookingId);
        DatabaseReference custoRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId).child("Booking").child(bookingId);
        DatabaseReference parkingRef = FirebaseDatabase.getInstance().getReference().child("Booking").child(bookingId);

        parkiRef.removeValue();
        custoRef .removeValue();
        parkingRef.removeValue();

        startActivity(new Intent(CarparkBookingSingle.this, carbooking.class));
        finish();



    }
    private Long getCurrentTimestamp() {
        Long timestampe = System.currentTimeMillis()/1000;
        return timestampe;
    }
}

