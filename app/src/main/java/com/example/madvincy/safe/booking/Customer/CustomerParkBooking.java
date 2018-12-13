package com.example.madvincy.safe.booking.Customer;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.madvincy.safe.R;
import com.example.madvincy.safe.parkinghistory.CustomerParkingHistory;
import com.example.madvincy.safe.parkinghistory.historyRecyclerView.HistoryObject;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CustomerParkBooking extends AppCompatActivity {

    private String customerOrParkingPlace, userId;

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
        mHistoryAdapter = new com.example.madvincy.safe.parkinghistory.historyRecyclerView.HistoryAdapter(getDataSetHistory(), CustomerParkBooking.this);
        mHistoryRecyclerView.setAdapter(mHistoryAdapter);


        customerOrParkingPlace = getIntent().getExtras().getString("customerOrParkingplace");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getUserParkingHistoryIds();

        if(customerOrParkingPlace.equals("Parkingplaces")){
            mBalance.setVisibility(View.VISIBLE);
            mPayout.setVisibility(View.VISIBLE);
            mPayoutEmail.setVisibility(View.VISIBLE);
        }

        mPayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payoutRequest();
            }
        });
    }

    private void getUserParkingHistoryIds() {
        DatabaseReference userHistoryDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(customerOrParkingPlace).child(userId).child("history");
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
        DatabaseReference historyDatabase = FirebaseDatabase.getInstance().getReference().child("history").child(parkingKey);
        historyDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String ParkHiId = dataSnapshot.getKey();
                    Long startingTime = 0L;
                    Long endingTime = 0L;
                    String customer = "";

                    Double parkingPrice = 0.0;

                    if(dataSnapshot.child("Starting Time").getValue() != null){
                        startingTime = Long.valueOf(dataSnapshot.child("Starting Time").getValue().toString());
                    }
                    if(dataSnapshot.child("Ending Time").getValue() != null){
                        endingTime = Long.valueOf(dataSnapshot.child("Ending Time").getValue().toString());
                    }

//                    if(dataSnapshot.child("customerPaid").getValue() != null && dataSnapshot.child("parkingplacePaidOut").getValue() == null){
//                        if(dataSnapshot.child("charges").getValue() != null){
//                            parkingPrice = Double.valueOf(dataSnapshot.child("price").getValue().toString());
//                            Balance += parkingPrice;
//                            mBalance.setText("Balance: " + String.valueOf(Balance) + " $");
//                        }
//                    }


                    com.example.madvincy.safe.booking.Customer.CustomerBookingRecyclerView.BookingObject obj = new  com.example.madvincy.safe.booking.Customer.CustomerBookingRecyclerView.BookingObject(ParkHiId,getDate(startingTime));
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

    private ArrayList resultsHistory = new ArrayList<HistoryObject>();
    private ArrayList<HistoryObject> getDataSetHistory() {
        return resultsHistory;
    }




    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
    ProgressDialog progress;
    private void payoutRequest() {
        progress = new ProgressDialog(this);
        progress.setTitle("Processing your payout");
        progress.setMessage("Please Wait...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        final OkHttpClient client = new OkHttpClient();
        JSONObject postData = new JSONObject();
        try {
            postData.put("uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
            postData.put("email", mPayoutEmail.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE,
                postData.toString());

        final Request request = new Request.Builder()
                .url("https://us-central1-uberapp-408c8.cloudfunctions.net/payout")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Your Token")
                .addHeader("cache-control", "no-cache")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                progress.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {

                int responseCode = response.code();


                if (response.isSuccessful())
                    switch (responseCode) {
                        case 200:
                            Snackbar.make(findViewById(R.id.layout), "Payout Successful!", Snackbar.LENGTH_LONG).show();
                            break;
                        case 501:
                            Snackbar.make(findViewById(R.id.layout), "Error: no payout available", Snackbar.LENGTH_LONG).show();
                            break;
                        default:
                            Snackbar.make(findViewById(R.id.layout), "Error: couldn't complete the transaction", Snackbar.LENGTH_LONG).show();
                            break;
                    }
                else
                    Snackbar.make(findViewById(R.id.layout), "Error: couldn't complete the transaction", Snackbar.LENGTH_LONG).show();

                progress.dismiss();
            }
        });
    }
}

