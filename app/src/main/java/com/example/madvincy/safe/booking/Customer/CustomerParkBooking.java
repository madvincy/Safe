package com.example.madvincy.safe.booking.Customer;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.madvincy.safe.R;
import com.example.madvincy.safe.booking.Customer.CustomerBookingRecyclerView.BookingAdapter;
import com.example.madvincy.safe.booking.Customer.CustomerBookingRecyclerView.BookingObject;
import com.example.madvincy.safe.parkinghistory.CustomerParkingHistory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CustomerParkBooking extends AppCompatActivity {
    private String bookingId,customerId="", userId;

    private RecyclerView mHistoryRecyclerView;
    private RecyclerView.Adapter mHistoryAdapter;
    private RecyclerView.LayoutManager mHistoryLayoutManager;


    private TextView mBalance;

    private Double Balance = 0.0;

    private Button mPayout;

    private EditText mPayoutEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_booking);
        mBalance = findViewById(R.id.balance);
        mPayout = findViewById(R.id.payout);
        mPayoutEmail = findViewById(R.id.payoutEmail);

        mHistoryRecyclerView = (RecyclerView) findViewById(R.id.historyRecyclerView);
        mHistoryRecyclerView.setNestedScrollingEnabled(false);
        mHistoryRecyclerView.setHasFixedSize(true);
        mHistoryLayoutManager = new LinearLayoutManager(CustomerParkBooking.this);
        mHistoryRecyclerView.setLayoutManager(mHistoryLayoutManager);
        mHistoryAdapter = new com.example.madvincy.safe.booking.Customer.CustomerBookingRecyclerView.BookingAdapter(getDataSetHistory(), CustomerParkBooking.this);
        mHistoryRecyclerView.setAdapter(mHistoryAdapter);



        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getUserParkingHistoryIds();


            mBalance.setVisibility(View.VISIBLE);
            mPayout.setVisibility(View.VISIBLE);
            mPayoutEmail.setVisibility(View.VISIBLE);



    }

    private void getUserParkingHistoryIds() {
        DatabaseReference userHistoryDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userId).child("Booking");
        userHistoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot history : dataSnapshot.getChildren()){
                        FetchParkingInformation(history.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void FetchParkingInformation(String parkingKey) {
        DatabaseReference historyDatabase = FirebaseDatabase.getInstance().getReference().child("Booking").child(parkingKey);
        historyDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String bookingIid = dataSnapshot.getKey();
                    Long timestamp = 0L;
//                    String distance = "";

//                    Double parkingPrice = 0.0;

                    if(dataSnapshot.child("booking date").getValue() != null){
                        timestamp = Long.valueOf(dataSnapshot.child("booking date").getValue().toString());
                    }
                    if(dataSnapshot.child("Customer Id").getValue() != null){
                        customerId = String.valueOf(dataSnapshot.child("Customer Id").getValue().toString());
                    }


//                    if(dataSnapshot.child("customerPaid").getValue() != null && dataSnapshot.child("parkingplacePaidOut").getValue() == null){
//                        if(dataSnapshot.child("distance").getValue() != null){
//                            parkingPrice = Double.valueOf(dataSnapshot.child("price").getValue().toString());
//                            Balance += parkingPrice;
//                            mBalance.setText("Balance: " + String.valueOf(Balance) + " $");
//                        }
//                    }


                    com.example.madvincy.safe.booking.Customer.CustomerBookingRecyclerView.BookingAdapter obj = new   com.example.madvincy.safe.booking.Customer.CustomerBookingRecyclerView.BookingAdapter(bookingIid,getDate(timestamp));
                    resultsHistory.add(obj);
                    mHistoryAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private String getDate(Long time) {
        Calendar cal = Calendar.getInstance(Locale.getDefault());
        cal.setTimeInMillis(time*1000);
        String date = DateFormat.format("MM-dd-yyyy hh:mm", cal).toString();
        return date;
    }
    private ArrayList resultsHistory = new ArrayList<Object>();
    private ArrayList<BookingObject> getDataSetHistory() {
        return resultsHistory;
    }




}

