package com.example.madvincy.safe.parking_place_account_activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.madvincy.safe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class CustomerPreview extends AppCompatActivity {
    private int status = 0;
    private DatabaseReference mCustomerDatabase;
    private DatabaseReference mCustomerDatabases;
    private String mRegno,mModel,mColor,mId;
    private String customerId = "";
    private TextView mphon, mRegi,mCala,mModal,mPhon,mIid;
    private EditText mLot;
    private String mProfileImageUrl;
    private ImageView mProfileImage;
    private Button mConfirm;
    private int leftParksize;
    private String cust_Id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_preview);
        mPhon = (TextView) findViewById(R.id.phone_no);
        mRegi= (TextView) findViewById(R.id.regi_no);
        mModal = (TextView) findViewById(R.id.car_model);
        mCala = (TextView) findViewById(R.id.color);
        mLot = (EditText) findViewById(R.id.lot);
        mIid = (TextView) findViewById(R.id.ownerid);
        mProfileImage = (ImageView) findViewById(R.id.profileImage);
         cust_Id= getIntent().getStringExtra("CUSTOMER_ID");
        mConfirm=(Button)findViewById(R.id.confirm);
        getAssignedCustomer();

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userIdi = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference ParkSize = FirebaseDatabase.getInstance().getReference().child("Users").child("Parkingplaces").child(userIdi).child("user info").child("Parking Size");
                final DatabaseReference ParkSizeleft = FirebaseDatabase.getInstance().getReference().child("Users").child("Parkingplaces").child(userIdi).child("parking size left");
                ParkSizeleft.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                            if(map.get("number")!=null){
                                leftParksize = (int)map.get("name");
                              int parking = leftParksize-1;
                                //                saving new parking size
                                Map newParksizeinfo = new HashMap();
                                String prksze =  String.valueOf(parking);
                                newParksizeinfo.put("number", prksze);

                                ParkSizeleft.updateChildren(newParksizeinfo);

                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String parkLot = mLot.getText().toString();



//
// removing it as an active parking request
                DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Parkingplaces").child(userId).child("Active Requests");
                assignedCustomerRef.removeValue();


//data to active parking upon confirming
                DatabaseReference parkiRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Parkingplaces").child(userId).child("Active Parking");
                DatabaseReference custoRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(cust_Id).child("Active Parking");
                DatabaseReference parkingRef = FirebaseDatabase.getInstance().getReference().child("Active Parking");
                String parkingactive_ID = parkingRef.push().getKey();
                parkiRef.child(parkingactive_ID).setValue(true);
                custoRef.child(parkingactive_ID).setValue(true);
                Map userInfo = new HashMap();

                userInfo.put("Customer Id", cust_Id);
                userInfo.put("parkingplace Id", userId);
                userInfo.put("parking Lot", parkLot);
                userInfo.put("starting time", getCurrentTimestamp());
                parkingRef.child(parkingactive_ID).updateChildren(userInfo);
                Toast.makeText(CustomerPreview.this, "Parking started", Toast.LENGTH_LONG).show();
                startActivity(new Intent(CustomerPreview.this, com.example.madvincy.safe.parking_place_account_activities.QrScanning.class));
                finish();

            }
        });


    }
    private Long getCurrentTimestamp() {
        Long timestamp = System.currentTimeMillis()/1000;
        return timestamp;
    }

    private void getAssignedCustomer(){
        String parkId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference assignedCustomerRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Parkingplaces").child(parkId).child("Active Requests");
        assignedCustomerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    status = 1;
                    customerId = cust_Id;
                    getUserInfo();

                }else{
                    cancelrequest();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void cancelrequest(){
        startActivity(new Intent(CustomerPreview.this, QrScanning.class));
                                }



    private void getUserInfo(){
        getCarInfo();
        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId).child("user info");
        mCustomerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("national id_number")!=null){
                        mId = map.get("national id_number").toString();
                        mIid.setText(mId);
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void getCarInfo(){
        mCustomerDatabases = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(customerId).child("car info");
        mCustomerDatabases.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("registration number")!=null){
                        mRegno = map.get("registration number").toString();
                        mRegi.setText(mRegno);
                    }
                    if(map.get("model")!=null){
                        mModel = map.get("model").toString();
                        mModal.setText(mModel);
                    }
                    if(map.get("color")!=null){
                        mColor = map.get("color").toString();
                        mCala.setText(mColor);
                    }
                    if(map.get("profileImageUrl")!=null){
                        mProfileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(mProfileImageUrl).into(mProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
